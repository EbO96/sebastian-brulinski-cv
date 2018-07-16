package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem

class Education : CareerRecyclerItem() {
    override fun type() = CareerRecyclerItem.EDUCATION_ITEM
}