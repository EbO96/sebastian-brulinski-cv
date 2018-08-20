package cv.brulinski.sebastian.adapter.recycler.view_holder.credit_view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.interfaces.OnBindViewInViewHolder
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Credit
import cv.brulinski.sebastian.model.MyRecyclerItem
import kotlinx.android.synthetic.main.credit_item.view.*

class CreditItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnBindViewInViewHolder {

    override fun onBind(item: MyRecyclerItem<*>, position: Int, onItemClickListener: OnItemClickListener?) {
        itemView.apply {
            (item.item as? Credit)?.also { credit ->
                authorFirstLetterTextView.text = credit.author
                authorTextView.text = credit.author
                authorUrlTextView.text = credit.authorUrl
            }
        }
    }
}