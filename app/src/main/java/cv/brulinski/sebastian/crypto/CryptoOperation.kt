package cv.brulinski.sebastian.crypto

import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.google.gson.Gson
import cv.brulinski.sebastian.annotations.Crypto
import cv.brulinski.sebastian.interfaces.CryptoClass
import cv.brulinski.sebastian.utils.ctx
import java.io.IOException
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

/**
 * Class used to perform cryptography operations.
 * Working schema:
 * 1. Get instance of AndroidKeyStore
 * 2. Check for already existing RSA key
 * 2.1 - if key exist then get encrypted AES key from 'SharedPreferences'
 *       and decrypt using RSA private key.
 * 2.2 - if key not exist then create new one, encrypt by RSA public key and insert into SharedPreferences
 * 4. Encrypt and Decrypt all data using AES key
 */
class CryptoOperations {

    private val cipherRSATransformation = "RSA/ECB/PKCS1Padding"
    private val cipherAESTransformation = "AES/CBC/PKCS5PADDING"
    private val cipherProvider = "AndroidOpenSSL"
    private val KEYSTORE_NAME = "AndroidKeyStore"
    private val KEYSTORE_ALIAS = "sebastian_brulinski_cv_app_keystore_alias"
    private val KEYSTORE_KEY_ALGORITHM = "RSA"
    private val PREFERENCES_KEY_ALGORITHM = "AES"
    private val AES_KEY_SIZE = 256
    private val RSA_KEY_SIZE = 4096
    private var keyStore: KeyStore
    private var myPrivateKeyPreferences: MyPrivateKeyPreferences
    private var myKey: MyKey

    init {
        keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)

        myPrivateKeyPreferences = MyPrivateKeyPreferences()

        if (!isKeyExistsInKeyStore()) {
            saveKey()
        }

        myKey = getKey()
    }

    /*
    Public methods
     */

    /**
     * Encrypt data using AES key
     * @property strToEncrypt is string data to encrypt
     * @return encrypted data
     */
    fun encrypt(strToEncrypt: String?): String {
        strToEncrypt?.let {
            myKey.key?.apply {
                try {
                    val secretKey = SecretKeySpec(this, "AES")
                    val cipher = Cipher.getInstance(myKey.cipherTransformation)
                    val ivParameterSpec = IvParameterSpec(myKey.vector)

                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
                    return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))), Base64.NO_WRAP)

                } catch (e: Exception) {
                    println("Error while encrypting: " + e.toString())
                }
            }
        }

        return ""
    }

    /**
     * Decrypt data using AES key
     * @property strToDecrypt is string data to decrypt
     * @return decrypted data
     */
    fun decrypt(strToDecrypt: String?): String {
        myKey.key?.apply {
            try {
                val secretKey = SecretKeySpec(this, "AES")
                val cipher = Cipher.getInstance(myKey.cipherTransformation)
                val ivParameterSpec = IvParameterSpec(myKey.vector)

                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
                return String(cipher.doFinal(android.util.Base64.decode(strToDecrypt, android.util.Base64.NO_WRAP)))

            } catch (e: Exception) {
                println("Error while decrypting: " + e.toString())
            }
        }
        return ""
    }

    /*
    Private methods and classes
     */

    private fun isKeyExistsInKeyStore(): Boolean {
        var exists = false
        try {
            exists = keyStore.getEntry(KEYSTORE_ALIAS, null) != null
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnrecoverableEntryException) {
            e.printStackTrace()
        }

        return exists
    }

    /*
    * Generate new RSA key and put this key into AndroidKeyStore
    */
    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun generateKeyStoreKey() {

        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 1)

        val kpGenerator = KeyPairGenerator
                .getInstance(KEYSTORE_KEY_ALGORITHM,
                        KEYSTORE_NAME)

        val spec: AlgorithmParameterSpec

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            spec = KeyPairGeneratorSpec.Builder(ctx)
                    .setAlias(KEYSTORE_ALIAS)
                    .setSubject(X500Principal("CN=$KEYSTORE_ALIAS"))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .setKeySize(RSA_KEY_SIZE)
                    .build()

        } else {

            spec = KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setCertificateSubject(X500Principal("CN=$KEYSTORE_ALIAS"))
                    .setCertificateSerialNumber(BigInteger.TEN)
                    .setCertificateNotBefore(start.time)
                    .setCertificateNotAfter(end.time)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setKeySize(RSA_KEY_SIZE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
        }

        kpGenerator.initialize(spec)
        kpGenerator.generateKeyPair()
    }

    /**
     * Generate new AES key to encrypt and decrypt data
     */
    @Throws(NoSuchAlgorithmException::class)
    private fun generateAESKey(): Key {
        val keyGenerator = KeyGenerator.getInstance(PREFERENCES_KEY_ALGORITHM)
        val secureRandom = SecureRandom()
        keyGenerator.init(AES_KEY_SIZE, secureRandom)
        return keyGenerator.generateKey()
    }

    /**
     * Get IV based on previously generated AES key
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class)
    private fun getIV(aesKey: Key): ByteArray {
        val cipher = Cipher.getInstance(cipherAESTransformation)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        return cipher.iv
    }

    /**
     * Generate AES key, encrypt and save into preferences
     */
    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class, IllegalBlockSizeException::class, InvalidKeyException::class, BadPaddingException::class, NoSuchPaddingException::class, UnrecoverableEntryException::class, KeyStoreException::class, CertificateException::class, IOException::class)
    private fun saveKey() {
        generateKeyStoreKey()
        val aesKey = generateAESKey()
        val plainAesKey = aesKey.encoded
        val plainIv = getIV(aesKey)
        val encryptedAes = encryptAesKey(plainAesKey, getRSAPublicKey())
        val encryptedIv = encryptAesKey(plainIv, getRSAPublicKey())
        myPrivateKeyPreferences.saveKey(encryptedAes, encryptedIv)
    }

    /**
     * Get RSA public key from KeyStore
     */
    @Throws(UnrecoverableEntryException::class, NoSuchAlgorithmException::class, KeyStoreException::class)
    private fun getRSAPublicKey(): PublicKey {
        return getKeyEntry().certificate.publicKey
    }

    /**
     * Get RSA private key from KeyStore
     */
    @Throws(UnrecoverableEntryException::class, NoSuchAlgorithmException::class, KeyStoreException::class)
    private fun getRSAPrivateKey(): PrivateKey {
        return getKeyEntry().privateKey
    }

    /**
     * Get key entry from KeyStore
     */
    @Throws(UnrecoverableEntryException::class, NoSuchAlgorithmException::class, KeyStoreException::class)
    private fun getKeyEntry(): KeyStore.PrivateKeyEntry {
        return keyStore.getEntry(KEYSTORE_ALIAS, null) as KeyStore.PrivateKeyEntry
    }

    /**
     *  Encrypt AES key by RSA key from KeyStore
     *
     * @param plainAesKey  plain AES key
     * @param rsaPublicKey RSA public key from AndroidKeyStore
     * @return encrypted AES key
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class, NoSuchProviderException::class)
    private fun encryptAesKey(plainAesKey: ByteArray, rsaPublicKey: PublicKey): String {
        val cipher = getCipherInstance(true)
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
        val encryptedAes = cipher.doFinal(plainAesKey)
        return Base64.encodeToString(encryptedAes, Base64.NO_WRAP)
    }

    /**
     * Decrypt AES key by RSA key from AndroidKeyStore
     *
     * @param encryptedAesKey encrypted AES key
     * @param rsaPrivateKey   RSA provate key from AndroidKeyStore
     * @return decrypted AES key
     */
    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class, NoSuchProviderException::class)
    private fun decryptAesKey(encryptedAesKey: ByteArray, rsaPrivateKey: PrivateKey): String {
        val cipher = getCipherInstance(false)
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey)
        val decryptedAes = cipher.doFinal(encryptedAesKey)
        return Base64.encodeToString(decryptedAes, Base64.NO_WRAP)
    }

    /**
     * Get cipher instance
     */
    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class)
    private fun getCipherInstance(encrypting: Boolean): Cipher {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || encrypting)
            Cipher.getInstance(cipherRSATransformation, cipherProvider)
        else
            Cipher.getInstance(cipherRSATransformation)
    }

    /**
     * Get encrypted AES key from preferences and decrypt this key using RSA private key from AndroidKeyStore
     */
    @Throws(IllegalBlockSizeException::class, InvalidKeyException::class, BadPaddingException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class, UnrecoverableEntryException::class, KeyStoreException::class, NoSuchProviderException::class, IOException::class, CertificateException::class)
    private fun getKey(): MyKey {

        val myKey = myPrivateKeyPreferences.getKey()
        val rsaPrivateKey = getRSAPrivateKey()
        val decryptedAesKey = Base64.decode(decryptAesKey(myKey.key
                ?: byteArrayOf(), rsaPrivateKey), Base64.NO_WRAP)
        val decryptedIv = Base64.decode(decryptAesKey(myKey.vector
                ?: byteArrayOf(), rsaPrivateKey), Base64.NO_WRAP)

        myKey.key = decryptedAesKey
        myKey.vector = decryptedIv

        return myKey
    }

    /**
     * Class used to save and retrieve AES key from preferences
     */
    private inner class MyPrivateKeyPreferences {
        private val keyAES = "d_v"
        private val keyIV = "d_vv"
        private val sharedPreferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(ctx)

        fun getKey(): MyKey {

            val iv = Base64.decode(sharedPreferences.getString(keyIV, Arrays.toString(byteArrayOf())), Base64.NO_WRAP)
            val aesKey = Base64.decode(sharedPreferences.getString(keyAES, Arrays.toString(byteArrayOf())), Base64.NO_WRAP)

            val myKey = MyKey()
            myKey.vector = iv
            myKey.key = aesKey
            myKey.cipherTransformation = cipherAESTransformation

            return myKey
        }

        fun saveKey(aesKey: String, iv: String) {
            sharedPreferences.edit().putString(keyIV, iv).apply()
            sharedPreferences.edit().putString(keyAES, aesKey).apply()
        }
    }

    /**
     * Ready to use AES key
     */
    private inner class MyKey {
        var key: ByteArray? = null
        var vector: ByteArray? = null
        var cipherTransformation: String? = null
    }

    inner class CryptoOperation<T : CryptoClass>(private val sourceObject: T?, objectClass: Class<T>) {

        private val gson = Gson()
        //Clone current instance of CV to avoid displaying encrypted data
        private val copyOfSourceObject =
                gson.fromJson<T>(gson.toJson(sourceObject), objectClass)

        fun start(encrypt: Boolean, disable: Boolean = false) =
                doCrypto(copyOfSourceObject, encrypt, disable) as? T


        /**
         * Do encrypt and decrypt operations
         * @property toEncrypt source object to encrypt. Field to encrypt must have @Crypto annotation
         * @property encrypt used to identify mode
         * @property disable pass true to disable encryption operations and return oryginal object
         * @see Crypto
         * @return encrypted/decrypted deep copy of source object. But if you pass true as 'disable' parameter
         * then function return original object
         */
        private fun doCrypto(toEncrypt: CryptoClass?, encrypt: Boolean, disable: Boolean = false): CryptoClass? {

            if (!disable) {
                if (toEncrypt is CryptoClass) {
                    toEncrypt.javaClass.declaredFields.filter { it.isAnnotationPresent(Crypto::class.java) }.forEach { field ->
                        field.isAccessible = true
                        val subjectField = field.get(toEncrypt)
                        if (subjectField is Collection<*>) {
                            subjectField.apply {
                                forEach {
                                    it?.let { listElement ->
                                        doCrypto(listElement as? CryptoClass, encrypt, disable)
                                    }
                                }
                            }
                        } else {
                            subjectField.javaClass.declaredFields.filter { it.isAnnotationPresent(Crypto::class.java) }.forEach { fieldToEncryptDecrypt ->
                                fieldToEncryptDecrypt?.apply {
                                    fieldToEncryptDecrypt.isAccessible = true
                                    val changedField = (this.get(subjectField) as? String)?.let { if (encrypt) encrypt(it) else decrypt(it) }
                                    this.set(subjectField, changedField)
                                }
                            }
                        }
                    }
                }
                return toEncrypt
            } else
                return sourceObject
        }
    }
}