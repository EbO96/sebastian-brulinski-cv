package cv.brulinski.sebastian.adapter.recycler.view_holder.skills_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.model.Skill
import kotlinx.android.synthetic.main.skill_item_header.view.*

class SkillHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnBindViewInViewHolder {

    override fun onBind(item: MyRecyclerItem<*>, position: Int, onItemClickListener: OnItemClickListener?) {
        (item.item as? Skill)?.apply {
            itemView.categoryTextView.text = skillCategory
        }
    }
}