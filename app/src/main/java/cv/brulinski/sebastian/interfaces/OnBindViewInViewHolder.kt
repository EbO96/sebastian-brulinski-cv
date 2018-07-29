package cv.brulinski.sebastian.interfaces

import cv.brulinski.sebastian.model.MyRecyclerItem

interface OnBindViewInViewHolder {
    fun onBind(item: MyRecyclerItem<*>, position: Int, onItemClickListener: OnItemClickListener? = null)
}