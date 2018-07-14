package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import kotlinx.android.synthetic.main.fragment_start.*
import java.lang.ClassCastException

class StartFragment : Fragment() {

    interface StartFragmentCallback {
        fun pdfVersionClick()
        fun electronicVersionClick()
        fun printCvClick()
        fun onStartFragmentResume()
    }
    //Callback to parent activity
    private lateinit var startFragmentCallback: StartFragmentCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfVersionView.setOnClickListener {
            startFragmentCallback.pdfVersionClick()
        }

        electronicVersionView.setOnClickListener {
            startFragmentCallback.electronicVersionClick()
        }

        printCvButton.setOnClickListener {
            startFragmentCallback.printCvClick()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            startFragmentCallback = context as StartFragmentCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement StartFragmentCallback")
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
