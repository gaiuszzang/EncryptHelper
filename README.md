## EncryptHelper
[![Release](https://jitpack.io/v/gaiuszzang/EncryptHelper.svg)](https://jitpack.io/#gaiuszzang/EncryptHelper)  
This library offers a Encrypt & Decrypt Utils.
It uses KeyStore internally, so keys are protected by Android System.

![example](https://user-images.githubusercontent.com/15318053/214221741-6f32e3e6-3270-4802-8dc6-0f4fe7d17bc1.gif)

<br />

### Including in your project
Add below codes to `settings.gradle`.
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
for old gradle version, Add below codes to **your project**'s `build.gradle`.
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation 'com.github.gaiuszzang:EncryptHelper:x.x.x'
}
```
<br />

### How to use
Create `EncryptHelper` instance with key alias.
```kotlin
val encryptHelper = EncryptHelper("sample_app_key_alias")
```

You can set up other Key Type.
```kotlin
val encryptHelper = EncryptHelper(
    keyAlias = "sample_app_key_alias",
    keyType = KeyType.RSA_ECB_PKCS1_4096
)
```
See the following KeyTypes.
 - `KeyType.RSA_ECB_PKCS1_2048`(default) : RSA Algorithm, ECB Block Mode, PKCS1 Padding, Key Length 2048bit
 - `KeyType.RSA_ECB_PKCS1_4096` : RSA Algorithm, ECB Block Mode, PKCS1 Padding, Key Length 4096bit
 
<br />

Use the `toEncrypt()`, `toDecrypt()` method for encryption or decryption.
```kotlin
val originalText = "Hello, World!"
val encryptText = encryptHelper.toEncrypt(originalText)
val decryptText = encryptHelper.toDecrypt(encryptText)
```

<br />

### License
```xml
Copyright 2022 gaiuszzang (Mincheol Shin)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
