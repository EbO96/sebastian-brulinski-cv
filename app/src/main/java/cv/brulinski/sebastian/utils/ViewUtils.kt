import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R

fun AppCompatActivity.setBaseToolbar(enableHomeButton: Boolean = false, title: String = "") {
    setSupportActionBar(findViewById(R.id.myToolbar))
    supportActionBar?.apply {
        setDisplayHomeAsUpEnabled(enableHomeButton)
        this.title = title
    }
}

infix fun ViewGroup.inflateViewHolderView(resLayout: Int) =
        LayoutInflater.from(this.context).inflate(resLayout, this, false)

fun RecyclerView.setup(divider: Boolean = false) {
    val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    layoutManager = lm
    itemAnimator = DefaultItemAnimator()
    if (divider) addItemDecoration(DividerItemDecoration(context, lm.orientation))
}