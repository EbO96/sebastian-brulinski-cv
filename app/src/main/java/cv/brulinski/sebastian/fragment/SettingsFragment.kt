package cv.brulinski.sebastian.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.settings
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : androidx.fragment.app.Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchGraphicsSwitch.apply {
            isChecked = settings.fetchGraphics //set the switch state
            setOnCheckedChangeListener { compoundButton, b ->
                settings.fetchGraphics = b //set the preferences
            }
        }
    }

}
