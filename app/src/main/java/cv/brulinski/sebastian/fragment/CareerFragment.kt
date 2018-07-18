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
import cv.brulinski.sebastian.model.Education
import kotlinx.android.synthetic.main.fragment_career.*
import setup
import java.lang.ClassCastException

class CareerFragment : Fragment() {

    interface CareerFragmentCallback: OnContentRefreshed {
        fun getEducation(): LiveData<Education>?
        fun refreshEducation()
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
        careerFragmentCallback.getEducation()?.observe(this, Observer {
            careerFragmentCallback.onRefreshed()
        })
    }

    private fun setupCareerRecycler() {
        careerRecyclerAdapter = CareerRecyclerAdapter(object : OnItemClickListener {
            override fun onClick(item: Any, position: Int) {

            }
        })
        recyclerView.setup()
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
