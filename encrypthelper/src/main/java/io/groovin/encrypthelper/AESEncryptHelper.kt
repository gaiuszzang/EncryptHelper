package io.groovin.encrypthelper

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

/**
 * AES-based encryption helper using Android KeyStore.
 * Uses AES/GCM/NoPadding for authenticated encryption.
 *
 * Advantages over RSA:
 * - No size limit for plaintext (can encrypt large data)
 * - Much faster encryption/decryption
 * - Authenticated encryption (prevents tampering)
 *
 * Note: The encrypted output includes IV (12 bytes) + ciphertext + authentication tag
 */
internal class AESEncryptHelper(
    private val keyAlias: String,
    private val keyType: KeyType = KeyType.AES_GCM_256
) : EncryptHelper {

    init {
        require(keyType.name.startsWith("AES")) {
            "AESEncryptHelper only supports AES key types"
        }
        setupKey()
    }

    private fun setupKey() {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply {
                load(null)
            }
            if (!keyStore.containsAlias(keyAlias) || keyStore.getEntry(keyAlias, null) !is KeyStore.SecretKeyEntry) {
                createSecretKey(keyAlias)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Encrypts the given plain text using AES-GCM encryption.
     * No size limit - can encrypt large data.
     *
     * The output format is: IV (12 bytes) + Ciphertext + Auth Tag (16 bytes)
     * All encoded in Base64.
     *
     * @param plainText The text to encrypt
     * @return Base64 encoded encrypted text with IV
     * @throws Exception if encryption fails
     */
    override fun toEncrypt(plainText: String): String {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply {
                load(null)
            }
            val secretKey = (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey

            val cipher = Cipher.getInstance(keyType.cipherAlgorithm).apply {
                init(Cipher.ENCRYPT_MODE, secretKey)
            }

            val iv = cipher.iv
            val bytes = plainText.toByteArray(Charsets.UTF_8)
            val encryptedBytes = cipher.doFinal(bytes)

            // Combine IV + encrypted data
            val combined = iv + encryptedBytes
            val encoded = Base64.getEncoder().encode(combined)
            return String(encoded)
        } catch (e: Exception) {
            e.printStackTrace()
            setupKey() //re-setup
            throw e
        }
    }

    /**
     * Decrypts the given encrypted text using AES-GCM decryption.
     * Automatically extracts IV from the encrypted data.
     *
     * @param encryptedText The Base64 encoded encrypted text with IV
     * @return Decrypted plain text
     * @throws Exception if decryption fails or authentication fails
     */
    override fun toDecrypt(encryptedText: String): String {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply {
                load(null)
            }
            val secretKey = (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey

            val base64EncryptedBytes = encryptedText.toByteArray(Charsets.UTF_8)
            val combined = Base64.getDecoder().decode(base64EncryptedBytes)

            // Extract IV (first 12 bytes) and encrypted data
            val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
            val encryptedData = combined.copyOfRange(GCM_IV_LENGTH, combined.size)

            val cipher = Cipher.getInstance(keyType.cipherAlgorithm).apply {
                init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH * 8, iv))
            }

            return String(cipher.doFinal(encryptedData))
        } catch (e: Exception) {
            e.printStackTrace()
            setupKey()  //re-setup
            return encryptedText
        }
    }

    private fun createSecretKey(alias: String) {
        try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEY_PROVIDER_NAME
            )

            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(keyType.keyLengthBit)
                .setUserAuthenticationRequired(false)
                .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val GCM_IV_LENGTH = 12 // 96 bits
        private const val GCM_TAG_LENGTH = 16 // 128 bits
    }
}
