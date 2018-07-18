package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem

class JobExperience(var id: Long) : CareerRecyclerItem() {

    var job: List<Job>? = null

    override fun type() = CareerRecyclerItem.JOB_EXPERIENCE
}