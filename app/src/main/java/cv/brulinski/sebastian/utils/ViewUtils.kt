import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.model.DialogConfig
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

/**
 * Inflating layouts
 */
fun Int.inflate(context: Context? = null) =
        LayoutInflater.from(context ?: ctx).inflate(this, null, false)

fun AppCompatActivity.androidContainer() = findViewById<ViewGroup>(android.R.id.content)

fun AppCompatActivity.addToAndroidContainer(view: View?) {
    view?.also {
        androidContainer().addView(view)
    }
}

fun AppCompatActivity.removeFromAndroidContainer(view: View?) {
    view?.also {
        androidContainer().removeView(view)
    }
}

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

fun ViewGroup.removeViewSafe(view: View) {
    indexOfChild(view).let { index ->
        if (index != -1) removeViewAt(index)
        else for (i in 0 until childCount) {
            if (getChildAt(i).id == view.id)
                removeViewAt(i)
        }
    }
}

/**
 * Show view
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Hide view
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Hide view
 */
fun View.gone(gone: Boolean = true) {
    if (gone)
        visibility = View.GONE
    else visible()
}

/**
 * Detecting bottom and top positions for lists
 */
fun NestedScrollView.bottomAndTopDetector(top: () -> Unit, bottom: () -> Unit) {
    this.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
        if (!this.canScrollVertically(1)) {
            bottom()
        }
        if (!this.canScrollVertically(-1)) {
            top()
        }
    }
}

fun AppCompatActivity.dialog(dialogConfig: DialogConfig, action: (Boolean) -> Unit) =
        with(dialogConfig) {
            AlertDialog.Builder(this@dialog)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positive) { _, _ ->
                        action(true)
                    }
                    .setNegativeButton(negative) { _, _ ->
                        action(false)
                    }
                    .create()
        }


enum class SlideDirection {
    UP,
    DOWN
}

