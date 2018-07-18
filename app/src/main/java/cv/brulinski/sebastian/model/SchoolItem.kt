package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.EDUCATION_ITEM

class SchoolItem : School(), CareerRecyclerItem {

    override fun type() = EDUCATION_ITEM
}