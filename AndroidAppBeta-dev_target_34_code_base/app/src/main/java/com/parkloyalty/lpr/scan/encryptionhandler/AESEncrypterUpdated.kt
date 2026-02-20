package com.parkloyalty.lpr.scan.encryptionhandler

import Decoder.BASE64Decoder
import Decoder.BASE64Encoder
import android.os.Build
import androidx.annotation.RequiresApi
import com.parkloyalty.lpr.scan.interfaces.Constants
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESEncrypterUpdated {
    /**
     * This generates the secret key to use for the
     * encryption process
     *
     * @param keyValue the key value
     * @return a secret key
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    @Throws(NoSuchAlgorithmException::class)
    fun getEncryptionKey(keyValue: String): Key {
        return SecretKeySpec(keyValue.toByteArray(), "AES")
    }

    /**
     * This function encrypts the data and encodes it in
     * base64
     *
     * @param data      the data we need to encrypt
     * @param secretKey the secretKey to use in the encryption
     * @return base64Encoded encrypted data
     * @throws NoSuchPaddingException             NoSuchPaddingException
     * @throws NoSuchAlgorithmException           NoSuchAlgorithmException
     * @throws InvalidKeyException                InvalidKeyException
     * @throws BadPaddingException                BadPaddingException
     * @throws IllegalBlockSizeException          IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun encrypt(data: String, secretKey: Key?): String {
        val iv = IvParameterSpec(Constants.SECRET_KEY.toByteArray(StandardCharsets.UTF_8))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        return BASE64Encoder().encode(cipher.doFinal(data.toByteArray()))
        //        return new String(
//                Base64.getEncoder().encode(cipher.doFinal(data.getBytes())),
//                StandardCharsets.UTF_8
//        );
    }

    /**
     * This function decrypts the data using the secret Key.We start by
     * decoding the base64 encoded string then decrypt
     *
     * @param encryptedData the encrypted data
     * @param secretKey     the shared secret key used to encrypt the data
     * @return the string that was encrypted
     * @throws NoSuchPaddingException             NoSuchPaddingException
     * @throws NoSuchAlgorithmException           NoSuchAlgorithmException
     * @throws InvalidKeyException                InvalidKeyException
     * @throws BadPaddingException                BadPaddingException
     * @throws IllegalBlockSizeException          IllegalBlockSizeException
     * @throws IOException                        the io exception
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        IOException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decrypt(encryptedData: String?, secretKey: Key?): String {
        val key = Constants.SECRET_KEY
        val iv = IvParameterSpec(ByteArray(16))
        val spec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        //        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        val decodedValue = BASE64Decoder().decodeBuffer(encryptedData)
        println(TAG + " Decoded Value Bytes " + Arrays.toString(decodedValue))
        return String(cipher.doFinal(decodedValue))
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        IOException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decryptVisa(encryptedData: String?, secretKey: Key?): String {
        val iv = IvParameterSpec(Constants.SECRET_KEY.toByteArray(charset("UTF-8")))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val decodedValue = BASE64Decoder().decodeBuffer(encryptedData)
        return String(cipher.doFinal(decodedValue))
    }

    /**
     * This function encrypts the data and encodes it in
     * base64
     *
     * @param data      the data we need to encrypt
     * @param secretKey the secretKey to use in the encryption
     * @return base64Encoded encrypted data
     * @throws NoSuchPaddingException             NoSuchPaddingException
     * @throws NoSuchAlgorithmException           NoSuchAlgorithmException
     * @throws InvalidKeyException                InvalidKeyException
     * @throws BadPaddingException                BadPaddingException
     * @throws IllegalBlockSizeException          IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun encryptDBKey(data: String, secretKey: Key?): String {
        val iv = IvParameterSpec(Constants.SECRET_KEY.toByteArray(StandardCharsets.UTF_8))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        return BASE64Encoder().encode(cipher.doFinal(data.toByteArray()))
    }

    /**
     * this method decrypt the database key
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        IOException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decryptDBKey(encryptedData: String?, secretKey: Key?): String {
        val iv = IvParameterSpec(Constants.SECRET_KEY.toByteArray(charset("UTF-8")))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val decodedValue = BASE64Decoder().decodeBuffer(encryptedData)
        return String(cipher.doFinal(decodedValue))
    }

    companion object {
        private const val TAG = "AESEncrypterUpdated_TAG"

        /**
         * Handles Decryption of Strings
         *
         * @param value String
         * @return String decrypted value
         */
        fun getDecryptedValue(value: String?): String {

//        try {
//            AESEncrypterUpdated encrypter = new AESEncrypterUpdated();
//            Key key = encrypter.getEncryptionKey(Constants.SECRET_KEY);
//            return encrypter.decrypt(value, key);
//        } catch (Exception ex) {
//            System.out.println(TAG + ex.toString());
//            return "";
//        }
            return try {
                val encrypter = AESEncrypterUpdated()
                val key = encrypter.getEncryptionKey(Constants.SECRET_KEY)
                encrypter.decryptVisa(value, key)
            } catch (ex: Exception) {
                println(TAG + ex.toString())
                ""
            }
        }

        /**
         * Handles Encryption of Strings
         *
         * @param value String
         * @return String encrypted value
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun getEncryptedValue(value: String): String {
            return try {
                val encrypter = AESEncrypterUpdated()
                val key = encrypter.getEncryptionKey(Constants.SECRET_KEY)
                val s = value.replace(" ", "")
                encrypter.encrypt(s, key)
            } catch (ex: Exception) {
                println(TAG + ex.toString())
                ""
            }
        }

        /**
         * Test Class For Encryption and Decryption
         *
         * @param args args
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @JvmStatic
        fun main(args: Array<String>) {
            val encrypter = AESEncrypterUpdated()
            try {
                val key = encrypter.getEncryptionKey(Constants.SECRET_KEY)
                val temp = encrypter.encrypt("3.0", key)
                println(TAG + "Original Text : " + "3.0")
                println(TAG + "Encrypted text : " + temp)
                println(TAG + "Decrypted text : " + encrypter.decrypt(temp, key))
            } catch (ex: Exception) {
                println(TAG + "Exception" + ex.toString())
            }
        }

        /**
         * Handles Decryption of Strings
         *
         * @param value String
         * @return String decrypted value
         */
        fun getDecryptedValueVisa(value: String?): String {
            return try {
                val encrypter = AESEncrypterUpdated()
                val key = encrypter.getEncryptionKey(Constants.SECRET_KEY)
                encrypter.decryptVisa(value, key)
            } catch (ex: Exception) {
                println(TAG + ex.toString())
                ""
            }
        }
    }
}