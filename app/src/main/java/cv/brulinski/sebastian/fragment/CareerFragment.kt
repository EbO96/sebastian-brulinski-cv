package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.CareerRecyclerAdapter
import cv.brulinski.sebastian.interfaces.OnContentRefreshed
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.model.recycler.CareerRecyclerItem
import cv.brulinski.sebastian.utils.date
import kotlinx.android.synthetic.main.fragment_career.*
import setup
import java.lang.ClassCastException

class CareerFragment : Fragment() {

    interface CareerFragmentCallback : OnContentRefreshed {
        fun getCareer(): LiveData<Career>?
        fun refreshCareer()
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
        careerFragmentCallback.apply {
            getCareer()?.observe(this@CareerFragment, Observer { career ->
                val items = arrayListOf<CareerRecyclerItem>()
                career?.schools?.let { schools ->
                    schools.sortedBy { it.startTime }.forEach {
                        val header = SchoolHeader(it.startTime.date())
                        header.place = it.place
                        val startItem = SchoolItem()
                        startItem.startTime = it.startTime
                        startItem.startTimeDescription = it.startTimeDescription
                        val endItem = SchoolItem()
                        endItem.endTime = it.endTime
                        endItem.endTimeDescription = it.endTimeDescription
                        items.add(header)
                        items.add(startItem)
                        items.add(endItem)
                    }
                }
                career?.jobs?.let { jobs ->
                    jobs.sortedBy { it.startTime }.forEach {
                        val header = JobHeader(it.startTime.date())
                        header.companyName = it.companyName
                        header.jobPosition = it.jobPosition
                        header.jobDescription = it.jobDescription
                        val startItem = JobItem()
                        startItem.startTime = it.startTime
                        startItem.startTimeDescription = it.startTimeDescription
                        val endItem = JobItem()
                        endItem.endTime = it.endTime
                        endItem.endTimeDescription = it.endTimeDescription
                        items.add(header)
                        items.add(startItem)
                        items.add(endItem)
                    }
                }
                if (items.isNotEmpty()) {
                    careerRecyclerAdapter.items = items
                } else careerRecyclerAdapter.items.add(NoCareer())
                careerFragmentCallback.onRefreshed()
            })
        }
    }

    private fun setupCareerRecycler() {
        careerRecyclerAdapter = CareerRecyclerAdapter(object : OnItemClickListener {
            override fun onClick(item: Any, position: Int) {

            }
        })
        recyclerView.setup(careerRecyclerAdapter)
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
