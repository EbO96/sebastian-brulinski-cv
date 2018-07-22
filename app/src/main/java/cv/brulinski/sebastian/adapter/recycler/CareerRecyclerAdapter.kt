package cv.brulinski.sebastian.adapter.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.career_view_holder.CareerHeaderViewHolder
import cv.brulinski.sebastian.adapter.recycler.career_view_holder.CareerItemViewHolder
import cv.brulinski.sebastian.adapter.recycler.empty.EmptyRecyclerViewHolder
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.RecyclerItem
import cv.brulinski.sebastian.utils.TYPE_HEADER
import cv.brulinski.sebastian.utils.TYPE_ITEM
import inflateViewHolderView

class CareerRecyclerAdapter(private val onItemClickListener: OnItemClickListener? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = arrayListOf<RecyclerItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = items[position].itemType
            ?: -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> CareerHeaderViewHolder(parent inflateViewHolderView R.layout.career_item_header)
            TYPE_ITEM -> CareerItemViewHolder(parent inflateViewHolderView R.layout.career_item)
            else -> EmptyRecyclerViewHolder(parent inflateViewHolderView R.layout.empty_list_item)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OnBindViewInViewHolder)?.onBind(items[position], position, onItemClickListener)
    }
}