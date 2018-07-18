package cv.brulinski.sebastian.adapter.recycler.education_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.SchoolHeader
import kotlinx.android.synthetic.main.education_header_item.view.*

open class EducationHeaderViewHolder(private var v: View) : RecyclerView.ViewHolder(v), OnBindViewInViewHolder {
    override fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener?) {
        val headerTextView = v.headerTextView
        headerTextView.text = (item as? SchoolHeader)?.place
    }
}