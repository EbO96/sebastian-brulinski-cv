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
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.utils.delay
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.lang.ClassCastException

/**
 *Fragment used for displaying introduction into app.
 * A Few words about what this application is
 */
open class WelcomeFragment : Fragment() {

    private var parentActivityCallback: ParentActivityCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeNextButton.setOnClickListener {
            //Navigate to PersonalInfoFragment
            (activity as? MainActivity)?.toPage(1)
        }
        //Show Shimmer text loading animation
        shimmerFrame.showShimmer()
        parentActivityCallback?.getWelcome {
            150L.delay {
                shimmerFrame.hideShimmer()
                welcomeContentTextView?.text = it.description
            }
        }
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            parentActivityCallback = context as? ParentActivityCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement WelcomeFragmentCallback")
        }
    }
}
