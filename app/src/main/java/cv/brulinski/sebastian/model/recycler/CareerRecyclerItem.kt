package cv.brulinski.sebastian.model.recycler

abstract class CareerRecyclerItem {

    companion object {
        val EDUCATION_ITEM_HEADER = 0
        val EDUCATION_ITEM = 1
        val JOB_EXPERIENCE_HEADER = 2
        val JOB_EXPERIENCE = 3
    }

    abstract fun type(): Int
}