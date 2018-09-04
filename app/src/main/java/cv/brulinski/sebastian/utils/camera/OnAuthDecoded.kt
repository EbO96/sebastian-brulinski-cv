package cv.brulinski.sebastian.utils.camera

import cv.brulinski.sebastian.model.Auth

interface OnAuthDecoded {
    fun authDecoded(auth: Auth)
}