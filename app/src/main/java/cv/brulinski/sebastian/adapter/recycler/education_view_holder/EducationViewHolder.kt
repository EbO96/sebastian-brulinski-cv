package cv.brulinski.sebastian.adapter.recycler.education_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.SchoolItem
import kotlinx.android.synthetic.main.education_item.view.*

open class EducationViewHolder(private var v: View) : RecyclerView.ViewHolder(v), OnBindViewInViewHolder {
    override fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener?) {
        (item as? SchoolItem)?.apply {
            val dateTextView = v.dateTextView
            dateTextView.text = if (startTime.isBlank()) endTime else startTime
            val descriptionTextView = v.descriptionTextView
            descriptionTextView.text = if (startTimeDescription.isBlank()) endTimeDescription else startTimeDescription
        }
    }
}