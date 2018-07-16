package cv.brulinski.sebastian.adapter.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnRecyclerItemClick
import cv.brulinski.sebastian.model.Education
import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import inflateRecyclerView

class CareerRecyclerAdapter(private val onRecyclerItemClick: OnRecyclerItemClick? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items = arrayListOf<CareerRecyclerItem>()

    override fun getItemViewType(position: Int) = items[position].type()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CareerRecyclerItem.EDUCATION_ITEM -> EducationViewHolder(parent inflateRecyclerView R.layout.education_recycler_item)
            else -> EmptyRecyclerViewHolder(parent inflateRecyclerView R.layout.education_recycler_item)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OnBindViewInViewHolder)?.onBind(position)
    }

    inner class EducationViewHolder(private var v: View) : RecyclerView.ViewHolder(v), OnBindViewInViewHolder {

        override fun onBind(position: Int) {
            (items[position] as? Education)?.let { education ->
                v.setOnClickListener {
                    onRecyclerItemClick?.onClick(education, position)
                }
            }
        }
    }

    inner class EmptyRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}