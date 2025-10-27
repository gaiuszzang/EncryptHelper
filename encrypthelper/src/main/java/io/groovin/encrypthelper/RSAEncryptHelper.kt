package io.groovin.encrypthelper

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.GeneralSecurityException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import java.util.*
import javax.crypto.Cipher

/**
 * RSA-based encryption helper using Android KeyStore.
 * Supports RSA 2048-bit and 4096-bit keys with PKCS1 padding.
 *
 * Note: Maximum plaintext size is limited by key size:
 * - RSA 2048-bit: ~245 bytes
 * - RSA 4096-bit: ~501 bytes
 */
internal class RSAEncryptHelper(
    private val keyAlias: String,
    private val keyType: KeyType = KeyType.RSA_ECB_PKCS1_2048
) : EncryptHelper {
    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    init {
        require(keyType.name.startsWith("RSA")) {
            "RSAEncryptHelper only supports RSA key types"
        }
        setup()
    }

    private fun setup() {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply {
                load(null)
            }
            if (!keyStore.containsAlias(keyAlias) || keyStore.getEntry(keyAlias, null) !is KeyStore.PrivateKeyEntry) {
                createPrivateKeyStore(keyAlias)
            }
            val keyEntry = keyStore.getEntry(keyAlias, null)
            encryptCipher = Cipher.getInstance(keyType.cipherAlgorithm).apply {
                init(Cipher.ENCRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey)
            }
            decryptCipher = Cipher.getInstance(keyType.cipherAlgorithm).apply {
                init(Cipher.DECRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).privateKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Encrypts the given plain text using RSA encryption.
     * Beware that input must be shorter than (keySize/8 - 11) bytes.
     * The length limit of plainText could be dramatically shorter in certain character encoding, such as UTF-8.
     *
     * @param plainText The text to encrypt
     * @return Base64 encoded encrypted text
     * @throws Exception if encryption fails or input is too long
     */
    override fun toEncrypt(plainText: String): String {
        val bytes = plainText.toByteArray(Charsets.UTF_8)
        val cipher = encryptCipher ?: return plainText
        try {
            val encryptedBytes = cipher.doFinal(bytes)
            val encoded = Base64.getEncoder().encode(encryptedBytes)
            return String(encoded)
        } catch (e: Exception) {
            e.printStackTrace()
            setup() //re-setup
            throw e
        }
    }

    override fun toDecrypt(encryptedText: String): String {
        val cipher = decryptCipher ?: return encryptedText
        try {
            val base64EncryptedBytes = encryptedText.toByteArray(Charsets.UTF_8)
            val encryptedBytes = Base64.getDecoder().decode(base64EncryptedBytes)
            return String(cipher.doFinal(encryptedBytes))
        } catch (e: Exception) {
            e.printStackTrace()
            setup()  //re-setup
            return encryptedText
        }
    }

    private fun createPrivateKeyStore(alias: String) {
        try {
            with(KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER_NAME)) {
                val spec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(keyType.keyLengthBit, F4))
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setUserAuthenticationRequired(false)
                    .build()

                initialize(spec)
                generateKeyPair()
            }
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
    }
}
