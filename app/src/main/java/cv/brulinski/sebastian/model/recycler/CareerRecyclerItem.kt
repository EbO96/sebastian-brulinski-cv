package cv.brulinski.sebastian.model.recycler

abstract class CareerRecyclerItem {

    companion object {
        val EDUCATION_ITEM_HEADER = 0
        val EDUCATION_ITEM = 1
        val JON_EXPERIENCE_HEADER = 2
        val JON_EXPERIENCE = 3
    }

    abstract fun type(): Int
}