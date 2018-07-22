package cv.brulinski.sebastian.adapter.recycler.career_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Career
import kotlinx.android.synthetic.main.career_item_header.view.*

class CareerHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnBindViewInViewHolder {

    override fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener?) {
        (item as? Career)?.apply {
            itemView.apply {
                placeNameTextView.text = placeName
                functionTextView.text = function
                descriptionTextView.text = description
            }
        }
    }
}