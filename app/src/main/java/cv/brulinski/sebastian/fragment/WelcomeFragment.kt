package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cv.brulinski.sebastian.R
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.lang.ClassCastException

class WelcomeFragment : Fragment() {

    interface WelcomeFragmentCallback {
        fun nextButtonClicked()
        fun onWelcomeFragmentResume()
    }
    //Callback to parent activity
    private lateinit var welcomeFragmentCallback: WelcomeFragmentCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextButton.setOnClickListener {
            welcomeFragmentCallback.nextButtonClicked()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            welcomeFragmentCallback = context as WelcomeFragmentCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement StartFragmentCallback")
        }
    }
}
