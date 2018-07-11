import androidx.appcompat.app.AppCompatActivity
import cv.brulinski.sebastian.R

fun AppCompatActivity.setBaseToolbar(enableHomeButton: Boolean = false, title: String = "") {
    setSupportActionBar(findViewById(R.id.myToolbar))
    supportActionBar?.apply {
        setDisplayHomeAsUpEnabled(enableHomeButton)
        this.title = title
    }
}