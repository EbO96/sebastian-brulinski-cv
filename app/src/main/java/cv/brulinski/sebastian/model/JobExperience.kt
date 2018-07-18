package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.JOB_EXPERIENCE

class JobExperience(var id: Long) : CareerRecyclerItem {

    var job: List<Job>? = null

    override fun type() = JOB_EXPERIENCE
}