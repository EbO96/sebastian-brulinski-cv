package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem

class Education : CareerRecyclerItem() {

    var school: List<School> = listOf()

    var timestamp = 0L

    override fun type() = EDUCATION_ITEM
}