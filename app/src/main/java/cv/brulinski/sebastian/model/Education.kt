package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem

class Education : CareerRecyclerItem() {

    var school: List<School>? = null

    override fun type() = EDUCATION_ITEM
}