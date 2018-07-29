package cv.brulinski.sebastian.model

data class MyRecyclerItem<T>(var item: T, override var itemType: Int) : RecyclerItem()