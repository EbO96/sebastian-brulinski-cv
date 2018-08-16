package cv.brulinski.sebastian.adapter.recycler.view_holder.languages_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Language
import cv.brulinski.sebastian.model.MyRecyclerItem
import kotlinx.android.synthetic.main.language_item.view.*

open class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnBindViewInViewHolder {
    override fun onBind(item: MyRecyclerItem<*>, position: Int, onItemClickListener: OnItemClickListener?) {
        (item.item as? Language)?.apply {
            itemView.apply {
                getLanguageIcon {
                    iconImageView.setImageBitmap(it)
                }
                nameTextView.text = name
                descriptionTextView.text = description
                dotLevelView.create(level, levelScale)
            }
        }
    }
}