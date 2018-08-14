package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bottomAndTopDetector
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomappbar.BottomAppBar
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.activity.MainActivity
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page.PERSONAL_INFO_SCREEN
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.utils.delay
import kotlinx.android.synthetic.main.fragment_welcome.view.*
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
        view.welcomeNextButton.setOnClickListener {
            //Navigate to PersonalInfoFragment
            (activity as? MainActivity)?.goToPage(PERSONAL_INFO_SCREEN)
        }
        //Show Shimmer text loading animation
        view.shimmerFrame.showShimmer()
        parentActivityCallback?.getWelcome {
            150L.delay {
                view.shimmerFrame.hideShimmer()
                view.welcomeContentTextView?.text = it.description
            }
        }

        parentActivityCallback?.changeFabPosition(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER)

        view.textScrollView.bottomAndTopDetector({
            //Top
        }, {
            //Bottom
            parentActivityCallback?.changeFabPosition(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER)
        })
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
