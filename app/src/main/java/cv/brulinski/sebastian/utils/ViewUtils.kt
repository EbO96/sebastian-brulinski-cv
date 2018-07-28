import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.ctx

fun AppCompatActivity.setBaseToolbar(enableHomeButton: Boolean = false, title: String = "") {
    setSupportActionBar(findViewById(R.id.myToolbar))
    supportActionBar?.apply {
        setDisplayHomeAsUpEnabled(enableHomeButton)
        this.title = title
    }
}

infix fun ViewGroup.inflateViewHolderView(resLayout: Int) =
        LayoutInflater.from(this.context).inflate(resLayout, this, false)

fun Int.inflate(context: Context? = null) =
        LayoutInflater.from(context ?: ctx).inflate(this, null, false)

fun RecyclerView.setup(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, divider: Boolean = false) {
    ViewCompat.setNestedScrollingEnabled(this, false)
    val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    layoutManager = lm
    itemAnimator = DefaultItemAnimator()
    this.adapter = adapter
    if (divider) addItemDecoration(DividerItemDecoration(context, lm.orientation))
}

fun TextView.setText(text: String, hideTextViewWhenTextEmpty: Boolean) {
    if (hideTextViewWhenTextEmpty)
        visibility = if (text.isNotBlank()) {
            View.VISIBLE
        } else View.GONE
    this.text = text

}