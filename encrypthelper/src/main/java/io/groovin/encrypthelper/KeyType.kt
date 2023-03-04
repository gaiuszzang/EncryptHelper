package io.groovin.encrypthelper

import android.security.keystore.KeyProperties

enum class KeyType(
    val keyLengthBit: Int,
    val cipherAlgorithm: String
) {
    RSA_ECB_PKCS1_2048(
        keyLengthBit = 2048,
        cipherAlgorithm = RSA_ECB_PKCS1
    ),
    RSA_ECB_PKCS1_4096(
        keyLengthBit = 4096,
        cipherAlgorithm = RSA_ECB_PKCS1
    )
}

private const val RSA_ECB_PKCS1 =
    "${KeyProperties.KEY_ALGORITHM_RSA}/${KeyProperties.BLOCK_MODE_ECB}/${KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1}"
