package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.model.Welcome
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.lang.ClassCastException

class WelcomeFragment : Fragment() {

    interface WelcomeFragmentCallback {
    }

    //Callback to parent activity
    private lateinit var welcomeFragmentCallback: WelcomeFragmentCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun update(welcome: Welcome) {
        welcomeTitleTextView?.text = welcome.title
        welcomeDescriptionTextView?.text = welcome.description
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            welcomeFragmentCallback = context as WelcomeFragmentCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement WelcomeFragmentCallback")
        }
    }
}
