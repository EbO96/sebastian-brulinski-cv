package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.CareerRecyclerAdapter
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.Career
import cv.brulinski.sebastian.model.RecyclerItem
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
    private lateinit var careerRecyclerAdapter: CareerRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_career, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCareerRecycler()
    }

    fun update(career: List<Career>) {
        val items = arrayListOf<RecyclerItem>()
        career.sortedBy { it.startTime.date() }.reversed().forEach {
            val header = it
            header.itemType = TYPE_HEADER
            val item = it.clone()
            item.itemType = TYPE_ITEM
            items.add(header)
            items.add(item)
        }
        careerRecyclerAdapter.items = items
    }

    private fun setupCareerRecycler() {
        careerRecyclerAdapter = CareerRecyclerAdapter(object : OnItemClickListener {
            override fun onClick(item: Any, position: Int) {

            }
        })
        recyclerView.setup(careerRecyclerAdapter, true)
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
