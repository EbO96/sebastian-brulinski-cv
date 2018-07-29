package cv.brulinski.sebastian.adapter.recycler.view_holder.career_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Career
import cv.brulinski.sebastian.model.MyRecyclerItem
import kotlinx.android.synthetic.main.career_item_header.view.*
import setText

class CareerHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnBindViewInViewHolder {

    override fun onBind(item: MyRecyclerItem<*>, position: Int, onItemClickListener: OnItemClickListener?) {
        (item.item as? Career)?.apply {
            itemView.apply {
                placeNameTextView.setText(placeName, true)
                functionTextView.setText(function, true)
                descriptionTextView.setText(description, true)
            }
        }
    }
}