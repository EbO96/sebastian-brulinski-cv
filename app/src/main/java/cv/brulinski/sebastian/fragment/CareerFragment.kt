package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.career.CareerRecyclerAdapter
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.model.Career
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.utils.TYPE_HEADER
import cv.brulinski.sebastian.utils.TYPE_ITEM
import cv.brulinski.sebastian.utils.date
import gone
import kotlinx.android.synthetic.main.fragment_career.view.*
import kotlinx.android.synthetic.main.fragment_credits.view.*
import setup
import visible
import java.lang.ClassCastException

/**
 * Fragment which is used for displaying list of witch my career,
 * including school, studies and work
 */
open class CareerFragment : Fragment() {

    //Career recycler adapter
    private var careerRecyclerAdapter: CareerRecyclerAdapter? = null

    private var parentActivityCallback: ParentActivityCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_career, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCareerRecycler()

        //Update UI
        parentActivityCallback?.getCareer { listOfCareer ->

            val items = arrayListOf<MyRecyclerItem<Career>>()
            //Make list of career
            listOfCareer.sortedBy { it.startTime.date() }.forEach { career ->
                val header = MyRecyclerItem(career, TYPE_HEADER)
                val item = MyRecyclerItem(career, TYPE_ITEM)
                items.add(header)
                items.add(item)
            }

            if (items.isNotEmpty()) {
                view.noCareerLayout.gone()
                careerRecyclerAdapter?.items = items
            } else view.noCareerLayout.visible()
        }
    }

    /*
    Private methods
     */
    private fun setupCareerRecycler() {
        careerRecyclerAdapter = CareerRecyclerAdapter(object : OnItemClickListener {
            override fun onClick(item: Any, position: Int, v: View) {

            }
        }).apply {
            view?.recyclerView?.setup(this, false)
        }
    }

    /*
    Override methods
     */

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            parentActivityCallback = context as? ParentActivityCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement CareerFragmentCallback")
        }
    }
}
