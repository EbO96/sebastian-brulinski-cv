package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.EDUCATION_ITEM_HEADER

class SchoolHeader : School(), CareerRecyclerItem {

    override fun type() = EDUCATION_ITEM_HEADER
}