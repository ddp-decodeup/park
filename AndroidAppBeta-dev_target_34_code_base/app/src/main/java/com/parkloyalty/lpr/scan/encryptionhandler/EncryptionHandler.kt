//package com.parkloyalty.lpr.scan.encryptionhandler
//
//import com.parkloyalty.lpr.scan.util.LogUtil
//import org.spongycastle.jce.provider.BouncyCastleProvider
//import org.spongycastle.util.encoders.Base64
//import java.security.*
//import java.security.spec.InvalidKeySpecException
//import java.security.spec.PKCS8EncodedKeySpec
//import java.security.spec.X509EncodedKeySpec
//import javax.crypto.*
//import javax.crypto.spec.IvParameterSpec
//import javax.crypto.spec.SecretKeySpec
//
///*
// this class is used for generating public private key and encrypt decrypt data for API calling
// * */
//object EncryptionHandler {
//    private val TAG = EncryptionHandler::class.java.simpleName
//
//    /**
//     * Store private and public key securely to avoid loading new ones every-time
//     */
//    private var privateKey: String? = null
//    private var publicKey: String? = null
//    private fun generateKeys() {
//        try {
//            val kpg = KeyPairGenerator.getInstance(SecurityConstants.TYPE_RSA)
//            kpg.initialize(SecurityConstants.KEY_SIZE)
//            val kp = kpg.generateKeyPair()
//            privateKey = String(Base64.encode(kp.private.encoded))
//            publicKey = String(Base64.encode(kp.public.encoded))
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//    }
//
//    fun loadPublicKeyFromString(): PublicKey? {
//        try {
//            if (publicKey == null) {
//                generateKeys()
//            }
//            var key = publicKey!!.replace("-----BEGIN PUBLIC KEY-----\n", "")
//            key = key.replace("-----END PUBLIC KEY-----", "")
//            val encodedPublicKey = Base64.decode(publicKey)
//
//            // decode the encoded RSA public key
//            val pubKeySpec = X509EncodedKeySpec(encodedPublicKey)
//            val keyFactory = KeyFactory.getInstance(SecurityConstants.TYPE_RSA)
//            return keyFactory.generatePublic(pubKeySpec)
//        } catch (e: InvalidKeySpecException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    fun loadServerPublicKey(publicKey: String): PublicKey? {
//        try {
//            var key = publicKey.replace("-----BEGIN PUBLIC KEY-----\n", "")
//            key = key.replace("-----END PUBLIC KEY-----", "")
//            val encodedPublicKey = Base64.decode(key)
//            LogUtil.printLog("KEY string ------>", String(encodedPublicKey))
//            // decode the encoded RSA public key
//            val pubKeySpec = X509EncodedKeySpec(encodedPublicKey)
//            val keyFactory = KeyFactory.getInstance(SecurityConstants.TYPE_RSA)
//            return keyFactory.generatePublic(pubKeySpec)
//        } catch (e: InvalidKeySpecException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    fun loadServerPublicKeyUpdated(publicKey: String?): PublicKey? {
//        try {
//            val encodedPublicKey = Base64.decode(publicKey)
//            var temp = String(encodedPublicKey)
//            temp = temp.replace("-----BEGIN PUBLIC KEY-----", "")
//            temp = temp.replace("-----END PUBLIC KEY-----", "")
//            temp = temp.replace("\n", "")
//            temp = temp.replace("\r", "")
//            temp = temp.replace("\t", "")
//            temp = temp.replace(" ", "")
//
//
//            // decode the encoded RSA public key
//            val pubKeySpec = X509EncodedKeySpec(Base64.decode(temp))
//            val keyFactory = KeyFactory.getInstance(SecurityConstants.TYPE_RSA)
//            return keyFactory.generatePublic(pubKeySpec)
//        } catch (e: InvalidKeySpecException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    fun loadPrivateKeyFromString(): PrivateKey? {
//        try {
//            if (privateKey == null) {
//                generateKeys()
//            }
//            var privKeyPEM = privateKey!!.replace("-----BEGIN RSA PRIVATE KEY-----\n", "")
//            privKeyPEM = privKeyPEM.replace("-----END RSA PRIVATE KEY-----", "")
//            val decodedKey = Base64.decode(privKeyPEM)
//
//            // PKCS8 decode the encoded RSA private key
//            val privKeySpec = PKCS8EncodedKeySpec(decodedKey)
//            val keyFactory = KeyFactory.getInstance(SecurityConstants.TYPE_RSA)
//            return keyFactory.generatePrivate(privKeySpec)
//        } catch (e: InvalidKeySpecException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    fun loadPrivateKeyFromStringUpdated(base64PrimaryKey: String?): PrivateKey? {
//        try {
//            val encodedPublicKey = Base64.decode(base64PrimaryKey)
//            var temp = String(encodedPublicKey)
//            temp = temp.replace("-----BEGIN RSA PRIVATE KEY-----", "")
//            temp = temp.replace("-----END RSA PRIVATE KEY-----", "")
//            temp = temp.replace("\n", "")
//            temp = temp.replace("\r", "")
//            temp = temp.replace("\t", "")
//            temp = temp.replace(" ", "")
//            val decodedKey = Base64.decode(temp)
//
//            // PKCS8 decode the encoded RSA private key
//            val privKeySpec = PKCS8EncodedKeySpec(decodedKey)
//            val keyFactory = KeyFactory.getInstance(SecurityConstants.TYPE_RSA)
//            return keyFactory.generatePrivate(privKeySpec)
//        } catch (e: InvalidKeySpecException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    fun rsaDecrypt(base64EncryptedText: String?): ByteArray? {
//        try {
//            val key = loadPrivateKeyFromString()
//
//            // decrypts the message
////            Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA-256andMGF1Padding");
//            val cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1andMGF1Padding")
//            cipher.init(Cipher.DECRYPT_MODE, key)
//            return cipher.doFinal(Base64.decode(base64EncryptedText))
//        } catch (e: IllegalBlockSizeException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: BadPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: InvalidKeyException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    fun rsaDecryptUpdated(base64PrivateKey: String?, base64EncryptedText: String?): ByteArray? {
//        try {
//            val key = loadPrivateKeyFromStringUpdated(base64PrivateKey)
//
//            // decrypts the message
//            //Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA-256andMGF1Padding");
//            val cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1andMGF1Padding")
//            cipher.init(Cipher.DECRYPT_MODE, key)
//            return cipher.doFinal(Base64.decode(base64EncryptedText))
//        } catch (e: IllegalBlockSizeException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: BadPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: InvalidKeyException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    private fun rsaEncrypt(plaintext: ByteArray?, publicKey: PublicKey?): ByteArray? {
//        try {
//            // encrypts the message
//            //Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA-256andMGF1Padding");
//            val cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1andMGF1Padding")
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
//            return cipher.doFinal(plaintext)
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: InvalidKeyException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: BadPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: IllegalBlockSizeException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    private fun aesDecrypt(symKey: ByteArray?, encrypted: String?): String? {
//        try {
//            //Add an initialization vector
//            val iv = ByteArray(16) // initialization vector with all 0
//            val ivSpec = IvParameterSpec(iv)
//            val key: Key = SecretKeySpec(symKey, SecurityConstants.TYPE_AES)
//            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
//            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
//            val original = cipher.doFinal(Base64.decode(encrypted))
//            return String(original)
//        } catch (e: IllegalBlockSizeException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: BadPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: InvalidKeyException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: InvalidAlgorithmParameterException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    /**
//     * AES encrypt message using server key
//     *
//     * @param serverKey
//     * @param message
//     * @return
//     */
//    fun aesEncrypt(serverKey: PublicKey?, message: String): String? {
//        try {
//            val kgenerator = KeyGenerator.getInstance(SecurityConstants.TYPE_AES)
//            val random = SecureRandom()
//            kgenerator.init(128, random)
//            val aesKey: Key = kgenerator.generateKey()
//            val aesKeyLen = aesKey.encoded.size
//            //assertEquals(16, aesKeyLen); Hex
//            val aesKeyEncrypted = rsaEncrypt(aesKey.encoded, serverKey) ?: return null
//
//            //Add an initialization vector
//            val iv = ByteArray(16) // initialization vector with all 0
//            val ivSpec = IvParameterSpec(iv)
//            val aesKeyEncryptedBase64 = Base64.toBase64String(aesKeyEncrypted)
//            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
//            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec)
//            val encrypted = cipher.doFinal(message.toByteArray())
//            val messageEncrypted = Base64.toBase64String(encrypted)
//            val aesKeyLenHex = String.format("%03X", aesKeyEncryptedBase64.length)
//            return aesKeyLenHex + aesKeyEncryptedBase64 + messageEncrypted
//        } catch (e: NoSuchAlgorithmException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: InvalidKeyException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: NoSuchPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: BadPaddingException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: IllegalBlockSizeException) {
//            LogUtil.printLog(TAG, e.toString())
//        } catch (e: InvalidAlgorithmParameterException) {
//            LogUtil.printLog(TAG, e.toString())
//        }
//        return null
//    }
//
//    fun decryptMessage(
//        privateKey: String?,
//        encryptedMessage: String
//    ): String? {
//        val symLengthHex = encryptedMessage.substring(0, 3)
//        val symBase64Length = symLengthHex.toInt(16)
//        val symBase64 = encryptedMessage.substring(3, symBase64Length + 3)
//        val messageEncrypted =
//            encryptedMessage.substring(symBase64Length + 3, encryptedMessage.length)
//
//        // decrypts the symmetricKey
//        val symKey = rsaDecryptUpdated(privateKey, symBase64) ?: return ""
//        LogUtil.printLog("TAG", "SymBase64 : $symBase64")
//        LogUtil.printLog(
//            "TAG",
//            "EncryptedMessage64 : $messageEncrypted"
//        )
//        LogUtil.printLog("TAG", "SymKey length : " + symKey.size)
//        // LogUtil.printLog("TAG", "DecryptedMessage : " + decrypted);
//        return aesDecrypt(symKey, messageEncrypted)
//        //        messageView.setText(String.format("DECRYPTED MESSAGE: \n%s", decrypted));
//    } /*
//    public Key getEncryptionKey(String keyValue) throws NoSuchAlgorithmException {
//        return new SecretKeySpec(keyValue.getBytes(), "AES/CBC/PKCS5PADDING");
//    }
//
//    public static String decrypt(String encryptedData) throws NoSuchPaddingException,
//            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
//            IllegalBlockSizeException, IOException {
//
//        EncryptionHandler encrypter = new EncryptionHandler();
//        Key secretKey = encrypter.getEncryptionKey(Constants.SECRET_KEY);
//
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decodedValue = cipher.doFinal(Base64.decode(encryptedData));
////        byte[] decodedValue = new BASE64Decoder().decodeBuffer(encryptedData);
//        return new String(cipher.doFinal(decodedValue));
//    }*/
//
//    interface SecurityConstants {
//        companion object {
//            const val KEY_SIZE = 2048
//            const val TYPE_AES = "AES"
//            const val TYPE_RSA = "RSA"
//        }
//    }
//
//    init {
//        Security.insertProviderAt(BouncyCastleProvider(), 1)
//    }
//}