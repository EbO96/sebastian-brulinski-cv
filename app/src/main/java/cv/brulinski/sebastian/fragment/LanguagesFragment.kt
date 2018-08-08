package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.languages.LanguagesRecyclerAdapter
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.utils.getBitmapsForObjects
import kotlinx.android.synthetic.main.fragment_languages.*
import setup

/**
 * Fragment which is used for displaying list of languages
 * which I know
 */
open class LanguagesFragment : Fragment() {

    private var parentActivityCallback: ParentActivityCallback? = null

    private lateinit var languagesRecyclerAdapter: LanguagesRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_languages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setup(LanguagesRecyclerAdapter().apply { languagesRecyclerAdapter = this })

        parentActivityCallback?.getLanguages {
                getBitmapsForObjects(it) {
                    languagesRecyclerAdapter.items = it
                }
        }
    }

    /*
    Override methods
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivityCallback = context as? ParentActivityCallback
    }
}
