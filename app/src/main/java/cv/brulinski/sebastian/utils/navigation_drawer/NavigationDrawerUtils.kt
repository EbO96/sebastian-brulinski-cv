package cv.brulinski.sebastian.utils.navigation_drawer

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
fun DrawerLayout.close(gravity: Int = Gravity.START, closed: (() -> Unit?)? = null) {
    Observable.fromCallable {
        while (true) {
            if (!isDrawerOpen(gravity)) {
                break
            }
        }
    }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                closed?.let { it() }
            }

    closeDrawer(gravity)
}

