package cv.brulinski.sebastian.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.activity.MainActivity
import cv.brulinski.sebastian.adapter.recycler.skills.SkillsRecyclerAdapter
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.model.Skill
import cv.brulinski.sebastian.utils.getBitmapsForObjects
import cv.brulinski.sebastian.utils.openUrl
import gone
import kotlinx.android.synthetic.main.fragment_skills.view.*
import setup
import showLoading
import visible

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
                if (it.isNotEmpty()) {
                    view.noSkillsLayout.gone()
                    skillsRecyclerAdapter?.items = it
                } else view.noSkillsLayout.visible()
            }
        }
    }

    /*
    Private methods
     */
    private fun setupSkillsRecycler() {
        skillsRecyclerAdapter = SkillsRecyclerAdapter(object : OnItemClickListener {
            override fun onClick(item: Any, position: Int, v: View) {
                if (v.id == R.id.moreFooter) {
                    (item as? Skill)?.apply {
                        activity?.showLoading()
                        moreUrl.openUrl(activity, MainActivity.OPEN_URL_REQUEST_CODE)
                    }
                }
            }
        }).apply {
            view?.recyclerView?.setup(this, false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*
    Override methods
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivityCallback = context as? ParentActivityCallback
    }
}
