package cv.brulinski.sebastian.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.languages.LanguagesRecyclerAdapter
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.Language
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.utils.TYPE_ITEM
import cv.brulinski.sebastian.utils.getBitmapsForObjects
import kotlinx.android.synthetic.main.fragment_languages.*
import setup

class LanguagesFragment : Fragment() {

    companion object {
        var viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null
        fun newInstance(viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null): LanguagesFragment {
            this.viewPagerUtilsFragmentCreatedListener = viewPagerUtilsFragmentCreatedListener
            return LanguagesFragment()
        }
    }

    private lateinit var languagesRecyclerAdapter: LanguagesRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_languages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setup(LanguagesRecyclerAdapter().apply { languagesRecyclerAdapter = this })
        viewPagerUtilsFragmentCreatedListener?.onFragmentCreated()
    }

    fun update(languages: List<Language>) {
//        languagesRecyclerAdapter.items = languages.sortedBy { it.level }.reversed()
//                .map { MyRecyclerItem(it, TYPE_ITEM) } as ArrayList<MyRecyclerItem<Language>>
        getBitmapsForObjects(languages) {
            languagesRecyclerAdapter.items = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPagerUtilsFragmentCreatedListener?.onFragmentDestroyed()
    }
}
