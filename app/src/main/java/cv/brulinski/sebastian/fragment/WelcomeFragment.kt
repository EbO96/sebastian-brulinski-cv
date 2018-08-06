package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.activity.MainActivity
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.Welcome
import cv.brulinski.sebastian.utils.delay
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.lang.ClassCastException

class WelcomeFragment : Fragment() {

    interface WelcomeFragmentCallback {
    }

    companion object {
        private var viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null
        fun newInstance(viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null): WelcomeFragment {
            this.viewPagerUtilsFragmentCreatedListener = viewPagerUtilsFragmentCreatedListener
            return WelcomeFragment()
        }
    }

    //Callback to parent activity
    private lateinit var welcomeFragmentCallback: WelcomeFragmentCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomeNextButton.setOnClickListener {
            (activity as? MainActivity)?.toPage(1)
        }
        viewPagerUtilsFragmentCreatedListener?.onFragmentCreated()
        shimmerFrame.showShimmer()
    }


    /*
    Public methods
     */
    fun update(welcome: Welcome) {
        150L.delay { shimmerFrame.hideShimmer() }
        welcomeContentTextView?.text = welcome.description
    }


    /*
    Private methods
     */
    private fun ShimmerFrameLayout.showShimmer() {
        startShimmerAnimation()
        this.visibility = View.VISIBLE
    }

    private fun ShimmerFrameLayout.hideShimmer() {
        stopShimmerAnimation()
        this.visibility = View.GONE
    }

    /*
    Override methods
     */

    override fun onDestroy() {
        super.onDestroy()
        viewPagerUtilsFragmentCreatedListener?.onFragmentDestroyed()
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
