package cv.brulinski.sebastian.adapter.recycler.skills

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.empty.EmptyRecyclerViewHolder
import cv.brulinski.sebastian.adapter.recycler.view_holder.skills_view_holder.SkillHeaderViewHolder
import cv.brulinski.sebastian.adapter.recycler.view_holder.skills_view_holder.SkillViewHolder
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.model.Skill
import cv.brulinski.sebastian.utils.TYPE_HEADER
import cv.brulinski.sebastian.utils.TYPE_ITEM
import cv.brulinski.sebastian.utils.list.MyRecyclerAdapter
import inflateViewHolderView

class SkillsRecyclerAdapter(private val onListItemClickListener: OnItemClickListener? = null) : MyRecyclerAdapter<Skill>() {

    override var items = arrayListOf<MyRecyclerItem<Skill>>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = items[position].itemType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                SkillViewHolder(parent inflateViewHolderView R.layout.skill_item)
            }
            TYPE_HEADER -> {
                SkillHeaderViewHolder(parent inflateViewHolderView R.layout.skill_item_header)
            }
            else -> EmptyRecyclerViewHolder(parent inflateViewHolderView R.layout.empty_item)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OnBindViewInViewHolder)?.onBind(items[position], position, onListItemClickListener)
    }
}