package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.career.CareerRecyclerAdapter
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.Career
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.utils.TYPE_HEADER
import cv.brulinski.sebastian.utils.TYPE_ITEM
import cv.brulinski.sebastian.utils.date
import kotlinx.android.synthetic.main.fragment_career.*
import setup
import java.lang.ClassCastException

class CareerFragment : Fragment() {

    interface CareerFragmentCallback {
    }

    //Callback to parent activity
    private lateinit var careerFragmentCallback: CareerFragmentCallback

    //Career recycler adapter
    private var careerRecyclerAdapter: CareerRecyclerAdapter? = null

    companion object {
        var viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null
        fun newInstance(viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null): CareerFragment {
            this.viewPagerUtilsFragmentCreatedListener = viewPagerUtilsFragmentCreatedListener
            return CareerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_career, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCareerRecycler()
        viewPagerUtilsFragmentCreatedListener?.onFragmentCreated()
    }

    fun update(career: List<Career>) {
        val items = arrayListOf<MyRecyclerItem<Career>>()
        career.sortedBy { it.startTime.date() }.forEach {
            val header = MyRecyclerItem(it, TYPE_HEADER)
            val item = MyRecyclerItem(it, TYPE_ITEM)
            items.add(header)
            items.add(item)
        }
        if (careerRecyclerAdapter == null)
            setupCareerRecycler()
        careerRecyclerAdapter?.items = items
    }

    private fun setupCareerRecycler() {
        careerRecyclerAdapter = CareerRecyclerAdapter(object : OnItemClickListener {
            override fun onClick(item: Any, position: Int) {

            }
        }).apply {
            recyclerView.setup(this, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPagerUtilsFragmentCreatedListener?.onFragmentDestroyed()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            careerFragmentCallback = context as CareerFragmentCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement CareerFragmentCallback")
        }
    }
}
