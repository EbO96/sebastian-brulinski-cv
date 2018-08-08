package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.skills.SkillsRecyclerAdapter
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.utils.getBitmapsForObjects
import kotlinx.android.synthetic.main.fragment_skills.*
import setup

/**
 * Fragment which is used for displaying list of my skills like
 * known programing languages, IDE, version control system
 */
class SkillsFragment : Fragment() {

    //Recycler adapter
    private var skillsRecyclerAdapter: SkillsRecyclerAdapter? = null

    private var parentActivityCallback: ParentActivityCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSkillsRecycler()

        parentActivityCallback?.getSkills {
            getBitmapsForObjects(it) {
                skillsRecyclerAdapter?.items = it
            }
        }
    }

    /*
    Private methods
     */
    private fun setupSkillsRecycler() {
        skillsRecyclerAdapter = SkillsRecyclerAdapter().apply {
            recyclerView.setup(this, false)
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
