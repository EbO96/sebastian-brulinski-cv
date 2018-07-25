package cv.brulinski.sebastian.interfaces

interface OnBindViewInViewHolder {
    fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener? = null)
}