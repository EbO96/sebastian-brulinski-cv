package cv.brulinski.sebastian.adapter.recycler.languages_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Language
import cv.brulinski.sebastian.utils.fetchBitmap
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.language_list_item.view.*

open class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnBindViewInViewHolder {
    override fun onBind(item: Any, position: Int, onItemClickListener: OnItemClickListener?) {
        (item as? Language)?.apply {
            itemView.apply {
                flag?.let { flagImageView.setImageBitmap(it) }
                nameTextView.text = name
                descriptionTextView.text = description
                dotLevelView.create(level, levelScale)
            }
        }
    }
}