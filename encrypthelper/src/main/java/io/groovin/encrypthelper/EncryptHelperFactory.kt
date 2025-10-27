package io.groovin.encrypthelper

/**
 * Factory class for creating appropriate EncryptHelper instances based on KeyType.
 * Automatically selects RSAEncryptHelper or AESEncryptHelper based on the key type.
 */
object EncryptHelperFactory {

    /**
     * Creates an EncryptHelper instance based on the provided KeyType.
     *
     * @param keyAlias The alias for the key in Android KeyStore
     * @param keyType The type of encryption to use
     * @return An EncryptHelper instance (RSAEncryptHelper or AESEncryptHelper)
     * @throws IllegalArgumentException if keyType is not supported
     */
    fun create(
        keyAlias: String,
        keyType: KeyType = KeyType.AES_GCM_256
    ): EncryptHelper {
        return when {
            keyType.name.startsWith("RSA") -> {
                RSAEncryptHelper(keyAlias, keyType)
            }
            keyType.name.startsWith("AES") -> {
                AESEncryptHelper(keyAlias, keyType)
            }
            else -> {
                throw IllegalArgumentException("Unsupported key type: $keyType")
            }
        }
    }
}
