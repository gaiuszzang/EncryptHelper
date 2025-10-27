package io.groovin.encrypthelper

/**
 * Interface for encryption and decryption operations.
 * Implementations should handle key management through Android KeyStore.
 */
interface EncryptHelper {
    /**
     * Encrypts the given plain text and returns the encrypted string.
     * @param plainText The text to encrypt
     * @return Base64 encoded encrypted text
     * @throws Exception if encryption fails
     */
    fun toEncrypt(plainText: String): String

    /**
     * Decrypts the given encrypted text and returns the original plain text.
     * @param encryptedText The Base64 encoded encrypted text to decrypt
     * @return Decrypted plain text
     * @throws Exception if decryption fails
     */
    fun toDecrypt(encryptedText: String): String
}

internal const val KEY_PROVIDER_NAME = "AndroidKeyStore"
internal const val KEYSTORE_INSTANCE_TYPE = "AndroidKeyStore"
