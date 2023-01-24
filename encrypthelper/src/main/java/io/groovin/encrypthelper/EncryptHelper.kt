package io.groovin.encrypthelper

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.GeneralSecurityException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import java.util.*
import javax.crypto.Cipher


class EncryptHelper(
    private val keyAlias: String
) {
    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    init {
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
            encryptCipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
                init(Cipher.ENCRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey)
            }
            decryptCipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
                init(Cipher.DECRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).privateKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Beware that input must be shorter than 256 bytes. The length limit of plainText could be dramatically
     * shorter than 256 letters in certain character encoding, such as UTF-8.
     */
    fun toEncrypt(plainText: String): String {
        try {
            encryptCipher?.let { cipher ->
                val bytes = plainText.toByteArray(Charsets.UTF_8)
                val encryptedBytes = cipher.doFinal(bytes)
                val encoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encode(encryptedBytes)
                } else {
                    android.util.Base64.encode(encryptedBytes, android.util.Base64.DEFAULT)
                }
                return String(encoded)
            } ?: run {
                return plainText
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setup() //re-setup
            return plainText
        }
    }

    fun toDecrypt(encryptedText: String): String {
        try {
            decryptCipher?.let { cipher ->
                val base64EncryptedBytes = encryptedText.toByteArray(Charsets.UTF_8)
                val encryptedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getDecoder().decode(base64EncryptedBytes)
                } else {
                    android.util.Base64.decode(base64EncryptedBytes, android.util.Base64.DEFAULT)
                }
                return String(cipher.doFinal(encryptedBytes))
            } ?: run {
                return encryptedText
            }
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
                    .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(KEY_LENGTH_BIT, F4))
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

    companion object {
        // 모든 입력값은 2048비트(256바이트) 보다 클 수 없다.
        private const val KEY_LENGTH_BIT = 2048

        private const val KEY_PROVIDER_NAME = "AndroidKeyStore"
        private const val KEYSTORE_INSTANCE_TYPE = "AndroidKeyStore"

        private const val CIPHER_ALGORITHM =
            "${KeyProperties.KEY_ALGORITHM_RSA}/" + "${KeyProperties.BLOCK_MODE_ECB}/" + KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1

    }
}