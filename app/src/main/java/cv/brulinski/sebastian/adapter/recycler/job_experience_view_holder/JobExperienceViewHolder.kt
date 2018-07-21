package cv.brulinski.sebastian.adapter.recycler.job_experience_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Job
import kotlinx.android.synthetic.main.education_item.view.*

class JobExperienceViewHolder(private var v: View) : RecyclerView.ViewHolder(v), OnBindViewInViewHolder {
    override fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener?) {
        (item as? Job)?.apply {
            v.dateTextView.text = if (startTime.isBlank()) endTime else startTime
            v.descriptionTextView.text = if (startTimeDescription.isBlank()) endTimeDescription else startTimeDescription
        }
    }
}