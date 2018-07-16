package cv.brulinski.sebastian.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.CareerRecyclerAdapter
import cv.brulinski.sebastian.interfaces.OnRecyclerItemClick

class CareerFragment : Fragment() {

    interface CareerFragmentCallback {

    }

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

    private fun setupCareerRecycler() {
        careerRecyclerAdapter = CareerRecyclerAdapter(object : OnRecyclerItemClick {
            override fun onClick(item: Any, position: Int) {

            }
        })
    }
}
