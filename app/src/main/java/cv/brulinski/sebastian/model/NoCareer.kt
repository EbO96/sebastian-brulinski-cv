package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.EMPTY_CAREER

class NoCareer : CareerRecyclerItem {
    override fun type() = EMPTY_CAREER
}