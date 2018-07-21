package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.JOB_EXPERIENCE_HEADER
import java.util.*

class JobHeader(var timestamp: Date?) : Job(), CareerRecyclerItem {
    override fun type() = JOB_EXPERIENCE_HEADER
}