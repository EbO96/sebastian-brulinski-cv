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
import cv.brulinski.sebastian.interfaces.DataProviderInterface
import cv.brulinski.sebastian.utils.delay
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.lang.ClassCastException

open class WelcomeFragment : Fragment() {

    private var dataProviderInterface: DataProviderInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeNextButton.setOnClickListener {
            (activity as? MainActivity)?.toPage(1)
        }
        shimmerFrame.showShimmer()
        dataProviderInterface?.getWelcome {
            150L.delay { shimmerFrame.hideShimmer() }
            welcomeContentTextView?.text = it.description
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
            dataProviderInterface = context as? DataProviderInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement WelcomeFragmentCallback")
        }
    }
}
