package cv.brulinski.sebastian.view.dialog

import android.app.AlertDialog
import android.content.DialogInterface

/**
 * Class used to create custom implementation of android alert dialog
 */
class MyDialog {

    companion object {

        fun create(myDialogConfig: MyDialogConfig): AlertDialog? = let {

            val positive = DialogInterface.OnClickListener { p0, p1 ->
                myDialogConfig.myDialogAction?.positive()
            }

            val negative = DialogInterface.OnClickListener { p0, p1 ->
                myDialogConfig.myDialogAction?.negative()
            }

            val neutral = DialogInterface.OnClickListener { dialogInterface, i ->
                myDialogConfig.myDialogAction?.neutral()
            }

            AlertDialog.Builder(myDialogConfig.activity)
                    .setPositiveButton(myDialogConfig.positive, positive)
                    .setNegativeButton(myDialogConfig.negative, negative)
                    .setNeutralButton(myDialogConfig.neutral, neutral)
                    .setCancelable(myDialogConfig.cancelable)
                    .create()

        }

    }
}