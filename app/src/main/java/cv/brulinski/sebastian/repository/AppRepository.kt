package cv.brulinski.sebastian.repository

import cv.brulinski.sebastian.crypto.CryptoOperations
import cv.brulinski.sebastian.model.MyCv

interface AppRepository {
    fun getRepository(): MainRepository<*>
    fun getCrypto(): CryptoOperations
    fun getMyCv(): MyCv?
}