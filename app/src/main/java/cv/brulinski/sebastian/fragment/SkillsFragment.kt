package cv.brulinski.sebastian.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.skills.SkillsRecyclerAdapter
import cv.brulinski.sebastian.interfaces.DataProviderInterface
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.Skill
import cv.brulinski.sebastian.utils.getBitmapsForObjects
import kotlinx.android.synthetic.main.fragment_skills.*
import setup

class SkillsFragment : Fragment() {

    //Recycler adapter
    private var skillsRecyclerAdapter: SkillsRecyclerAdapter? = null

    private var dataProviderInterface: DataProviderInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSkillsRecycler()

        dataProviderInterface?.getSkills {
            getBitmapsForObjects(it) {
                skillsRecyclerAdapter?.items = it
            }
        }
    }

    private fun setupSkillsRecycler() {
        skillsRecyclerAdapter = SkillsRecyclerAdapter().apply {
            recyclerView.setup(this, false)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        dataProviderInterface = context as? DataProviderInterface
    }
}
