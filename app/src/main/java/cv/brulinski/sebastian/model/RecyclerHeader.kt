package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.utils.TYPE_HEADER

class RecyclerHeader<T>(var header: T) : RecyclerItem() {
    override var itemType: Int = TYPE_HEADER
}