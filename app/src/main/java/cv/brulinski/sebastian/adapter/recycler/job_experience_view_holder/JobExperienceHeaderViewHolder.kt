package cv.brulinski.sebastian.adapter.recycler.job_experience_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Job
import kotlinx.android.synthetic.main.job_header_item.view.*


class JobExperienceHeaderViewHolder(private var v: View) : RecyclerView.ViewHolder(v), OnBindViewInViewHolder {
    override fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener?) {
        (item as? Job)?.apply {
            v.companyNameTextView.text = companyName
            v.jobPositionTextView.text = jobPosition
            v.jobDescriptionTextView.text = jobDescription
        }
    }
}