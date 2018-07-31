package cv.brulinski.sebastian.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.skills.SkillsRecyclerAdapter
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.Skill
import cv.brulinski.sebastian.utils.getBitmapsForObjects
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

    @SuppressLint("CheckResult")
    fun update(skills: List<Skill>) {
        getBitmapsForObjects(skills) {
            skillsRecyclerAdapter?.items = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPagerUtilsFragmentCreatedListener?.onFragmentDestroyed()
    }
}
