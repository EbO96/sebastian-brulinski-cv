package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.EDUCATION_ITEM
import cv.brulinski.sebastian.utils.JOB_EXPERIENCE

class JobItem : Job(), CareerRecyclerItem {
    override fun type() = JOB_EXPERIENCE
}