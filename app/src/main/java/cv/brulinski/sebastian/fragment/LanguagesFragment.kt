package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.languages.LanguagesRecyclerAdapter
import cv.brulinski.sebastian.interfaces.DataProviderInterface
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.Language
import cv.brulinski.sebastian.utils.getBitmapsForObjects
import kotlinx.android.synthetic.main.fragment_languages.*
import setup

open class LanguagesFragment : Fragment() {

    private var dataProviderInterface: DataProviderInterface? = null

    private lateinit var languagesRecyclerAdapter: LanguagesRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_languages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setup(LanguagesRecyclerAdapter().apply { languagesRecyclerAdapter = this })

        dataProviderInterface?.getLanguages{
            getBitmapsForObjects(it) {
                languagesRecyclerAdapter.items = it
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        dataProviderInterface = context as? DataProviderInterface
    }
}
