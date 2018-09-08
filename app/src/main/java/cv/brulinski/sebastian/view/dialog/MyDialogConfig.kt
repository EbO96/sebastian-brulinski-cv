package cv.brulinski.sebastian.view.dialog

import android.app.Activity

data class MyDialogConfig(val activity: Activity,
                          val title: String,
                          val message: String,
                          val positive: String = "YES",
                          val negative: String = "NO",
                          val neutral: String = "",
                          val cancelable: Boolean = false,
                          val myDialogAction: MyDialogAction? = null)