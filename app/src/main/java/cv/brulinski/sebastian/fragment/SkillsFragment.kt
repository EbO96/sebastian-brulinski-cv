package cv.brulinski.sebastian.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.skills.SkillsRecyclerAdapter
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.model.Skill
import cv.brulinski.sebastian.utils.TYPE_HEADER
import cv.brulinski.sebastian.utils.TYPE_ITEM
import kotlinx.android.synthetic.main.fragment_skills.*
import setup

class SkillsFragment : Fragment() {

    //Recycler adapter
    private var skillsRecyclerAdapter: SkillsRecyclerAdapter? = null

    companion object {
        var viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null
        fun newInstance(viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null): SkillsFragment {
            this.viewPagerUtilsFragmentCreatedListener = viewPagerUtilsFragmentCreatedListener
            return SkillsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSkillsRecycler()
        viewPagerUtilsFragmentCreatedListener?.onFragmentCreated()
    }

    private fun setupSkillsRecycler() {
        skillsRecyclerAdapter = SkillsRecyclerAdapter().apply {
            recyclerView.setup(this, false)
        }
    }

    fun update(skills: List<Skill>) {
        val items = arrayListOf<MyRecyclerItem<Skill>>()
        skills.groupBy { it.skillCategory }.forEach {
            val header = MyRecyclerItem(it.value[0], TYPE_HEADER)
            items.add(header)
            it.value.map { MyRecyclerItem(it, TYPE_ITEM) }.let {
                items.addAll(it)
            }
        }
        skillsRecyclerAdapter?.items = items
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPagerUtilsFragmentCreatedListener?.onFragmentDestroyed()
    }
}
