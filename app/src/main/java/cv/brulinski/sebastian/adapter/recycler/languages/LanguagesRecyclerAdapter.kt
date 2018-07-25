package cv.brulinski.sebastian.adapter.recycler.languages

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.languages_view_holder.LanguageViewHolder
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.model.Language
import inflateViewHolderView

class LanguagesRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<Language>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            LanguageViewHolder(parent inflateViewHolderView R.layout.language_list_item)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OnBindViewInViewHolder)?.onBind(items[position], position)
    }
}