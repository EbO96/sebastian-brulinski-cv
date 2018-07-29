package cv.brulinski.sebastian.utils.list

import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.MyRecyclerItem

abstract class MyRecyclerAdapter<T>(var onItemClickListener: OnItemClickListener? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract var items: ArrayList<MyRecyclerItem<T>>
}