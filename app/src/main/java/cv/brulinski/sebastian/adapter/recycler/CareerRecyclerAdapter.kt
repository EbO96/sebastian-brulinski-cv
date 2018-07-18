package cv.brulinski.sebastian.adapter.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.education_view_holder.EducationHeaderViewHolder
import cv.brulinski.sebastian.adapter.recycler.education_view_holder.EducationViewHolder
import cv.brulinski.sebastian.adapter.recycler.empty.EmptyRecyclerViewHolder
import cv.brulinski.sebastian.adapter.recycler.jon_experience_view_holder.JobExperienceHeaderViewHolder
import cv.brulinski.sebastian.adapter.recycler.jon_experience_view_holder.JobExperienceViewHolder
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import inflateViewHolderView

class CareerRecyclerAdapter(private val onItemClickListener: OnItemClickListener? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = arrayListOf<CareerRecyclerItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = items[position].type()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CareerRecyclerItem.EDUCATION_ITEM_HEADER -> EducationHeaderViewHolder(parent inflateViewHolderView R.layout.education_header_item)
            CareerRecyclerItem.EDUCATION_ITEM -> EducationViewHolder(parent inflateViewHolderView R.layout.education_item)
            CareerRecyclerItem.JOB_EXPERIENCE_HEADER -> JobExperienceHeaderViewHolder(parent inflateViewHolderView R.layout.jon_experience_header_item)
            CareerRecyclerItem.JOB_EXPERIENCE -> JobExperienceViewHolder(parent inflateViewHolderView R.layout.jon_experience_item)
            else -> EmptyRecyclerViewHolder(parent inflateViewHolderView R.layout.empty_list_item)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OnBindViewInViewHolder)?.onBind(position, onItemClickListener)
    }
}