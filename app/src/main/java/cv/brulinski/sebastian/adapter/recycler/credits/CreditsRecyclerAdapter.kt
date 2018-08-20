package cv.brulinski.sebastian.adapter.recycler.credits

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.empty.EmptyRecyclerViewHolder
import cv.brulinski.sebastian.adapter.recycler.view_holder.credit_view_holder.CreditItemViewHolder
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Credit
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.utils.TYPE_ITEM
import inflateViewHolderView

open class CreditsRecyclerAdapter(private val onItemClickListener: OnItemClickListener? = null)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<MyRecyclerItem<Credit>>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = items[position].itemType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> CreditItemViewHolder(parent inflateViewHolderView R.layout.credit_item)
            else -> EmptyRecyclerViewHolder(parent inflateViewHolderView R.layout.empty_item)
        }
    }

    override fun getItemCount(): Int {
        val size = items.size
        return size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OnBindViewInViewHolder)?.onBind(items[position], position, onItemClickListener)
    }
}