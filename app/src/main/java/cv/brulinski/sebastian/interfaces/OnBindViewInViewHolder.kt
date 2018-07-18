package cv.brulinski.sebastian.interfaces

import cv.brulinski.sebastian.model.recycler.RecyclerItem

interface OnBindViewInViewHolder {
    fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener?)
}