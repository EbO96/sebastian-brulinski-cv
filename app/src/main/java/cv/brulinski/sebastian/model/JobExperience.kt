package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.JOB_EXPERIENCE

class JobExperience() : CareerRecyclerItem {

    var jobs: List<Job>? = listOf()

    var timestamp = 0L

    override fun type() = JOB_EXPERIENCE
}