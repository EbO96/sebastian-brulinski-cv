package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.recycler.credits.CreditsRecyclerAdapter
import cv.brulinski.sebastian.interfaces.OnItemClickListener
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.model.Credit
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.utils.TYPE_ITEM
import cv.brulinski.sebastian.utils.string
import kotlinx.android.synthetic.main.fragment_credits.view.*
import kotlinx.android.synthetic.main.my_toolbar.view.*
import setup

/**
 * Fragment to display credits
 */
class CreditsFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    //Communication with parent activity
    private var parentActivityCallback: ParentActivityCallback? = null
    //Credits recycler adapter
    private var creditsRecyclerAdapter: CreditsRecyclerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setup Toolbar
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(view.myToolbar)
            supportActionBar?.apply {
                title = R.string.credits.string()
                setDisplayHomeAsUpEnabled(true)
            }
        }

        //Setup recycler
        creditsRecyclerAdapter = CreditsRecyclerAdapter(this).apply {
            view.creditsRecyclerView.setup(this, true)
        }

        parentActivityCallback?.getCredits { listOfCredit ->
            updateUi(listOfCredit)
        }

        view.swipeRefreshLayout.setOnRefreshListener(this)

    }

    /*
    Private methods
     */
    private fun updateUi(list: List<Credit>?) {
        view?.swipeRefreshLayout?.isRefreshing = false
        list?.also { _ ->
            creditsRecyclerAdapter?.items = list.map { MyRecyclerItem(it, TYPE_ITEM) }
        }
    }

    /*
    Override methods
    */

    override fun onClick(item: Any, position: Int, v: View) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }
            else -> false
        }
    }


    override fun onRefresh() {
        parentActivityCallback?.refreshCredits { credits ->
            updateUi(credits)
        }
    }

    override fun onDetach() {
        super.onDetach()
        parentActivityCallback?.onFragmentDestroyed(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivityCallback = context as? ParentActivityCallback
    }

}
