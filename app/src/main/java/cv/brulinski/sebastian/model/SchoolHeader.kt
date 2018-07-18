package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.EDUCATION_ITEM_HEADER
import java.util.*

class SchoolHeader(var timestamp: Date?) : School(), CareerRecyclerItem {

    override fun type() = EDUCATION_ITEM_HEADER
}