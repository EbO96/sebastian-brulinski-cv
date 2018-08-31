package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.utils.MAIN_ACTIVITY
import cv.brulinski.sebastian.utils.log
import cv.brulinski.sebastian.utils.settings
import cv.brulinski.sebastian.utils.string
import kotlinx.android.synthetic.main.fragment_settings.view.*
import setBaseToolbar

class SettingsFragment : androidx.fragment.app.Fragment() {

    //Communication with parent activity
    private var parentActivityCallback: ParentActivityCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.setBaseToolbar(true, R.string.settings.string())

        view.fetchGraphicsSwitch.apply {
            isChecked = settings.fetchGraphics //set the switch state
            setOnCheckedChangeListener { _, checked ->
                settings.fetchGraphics = checked //set the preferences
            }
        }

        view.newCvNotificationSwitch.apply {
            isChecked = settings.newCvNotification
            setOnCheckedChangeListener { _, checked ->
                settings.newCvNotification = checked
                parentActivityCallback?.registerForCvNotifications(checked) {
                    MAIN_ACTIVITY.log("status = $it")
                }
            }
        }

        view.creditsSection.setOnClickListener {
            parentActivityCallback?.goToCredits()
        }
    }

    /*
    Override methods
     */
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            activity?.supportFragmentManager?.popBackStack()
            true
        }
        R.id.logout -> {
            parentActivityCallback?.logout()
            true
        }
        else -> false
    }

    override fun onDetach() {
        parentActivityCallback?.onFragmentDestroyed(this)
        super.onDetach()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivityCallback = context as? ParentActivityCallback
    }

}
