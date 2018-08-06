package cv.brulinski.sebastian.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page.WELCOME_SCREEN
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.dependency_injection.component.DaggerPagesComponent
import cv.brulinski.sebastian.dependency_injection.component.PagesComponent
import cv.brulinski.sebastian.dependency_injection.module.PagesModule
import cv.brulinski.sebastian.fragment.CareerFragment
import cv.brulinski.sebastian.fragment.PersonalInfoFragment
import cv.brulinski.sebastian.fragment.SettingsFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.utils.*
import cv.brulinski.sebastian.utils.view_pager.MyMainViewPager
import cv.brulinski.sebastian.utils.view_pager.ViewPagerStates
import cv.brulinski.sebastian.utils.view_pager.toLeft
import cv.brulinski.sebastian.utils.view_pager.toPage
import cv.brulinski.sebastian.view.SlideDrawer
import cv.brulinski.sebastian.view_model.MainViewModel
import inflate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*

class MainActivity : AppCompatActivity(),
        WelcomeFragment.WelcomeFragmentCallback,
        PersonalInfoFragment.PersonalInfoCallback,
        CareerFragment.CareerFragmentCallback,
        Toolbar.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnFetchingStatuses {

    //Loading screen - displayed during first fetching
    private val loadingScreen by lazy { R.layout.data_loading_screen.inflate(this) }
    //ViewPager adapter
    private var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter? = null
    //ViewModel
    private var mainViewModel: MainViewModel<*>? = null
    //MyCv
    private var myCv: LiveData<MyCv>? = null
    //Dagger ViewPager pages component
    private lateinit var pagesComponent: PagesComponent
    //Menu items
    private var refreshMenuItem: MenuItem? = null
    //Main ViewPager
    private var myMainViewPager: MyMainViewPager? = null
    //Number of pages
    private var numberOfPages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewPager()

        //Slide drawer setting up
        slideDrawer({
            drawerTitle = R.string.table_of_contents.string()
        }, {
            //Menu items
            val items = arrayListOf(SlideDrawer.DrawerMenuItem(R.string.introduction.string()),
                    SlideDrawer.DrawerMenuItem(R.string.personal_details.string()),
                    SlideDrawer.DrawerMenuItem(R.string.career.string()),
                    SlideDrawer.DrawerMenuItem(R.string.languages.string()),
                    SlideDrawer.DrawerMenuItem(R.string.skills.string()))
            setMenu(items)
            //Menu items listener
            setMenuItemClickListener(object : SlideDrawer.MenuItemsClickListener {
                override fun onClick(position: Int, drawerMenuItem: SlideDrawer.DrawerMenuItem) {
                    viewPager.toPage(position)
                }
            })
        })

        //BottomAppBar FloatingActiobButton
        fab.setOnClickListener {
            if (currentFragment !is SettingsFragment)
                slideDrawer.apply {
                    if (!isOpen()) open() else close() //Open/close drawer
                }
            else {
                backFromSettings() //If current screen is SettingsFragment
            }
        }

        /*
        BottomAppBar Fab animations
         */
        val h = AnimatorSet()

        val stateListAnimator = StateListAnimator()
        val showAnimX = ObjectAnimator.ofFloat(fab, "scaleX", 0f, 1f)
        val showAnimY = ObjectAnimator.ofFloat(fab, "scaleY", 0f, 1f)
        val showAnimSet = AnimatorSet()
        showAnimSet.playTogether(showAnimX, showAnimY)

        val hideAnimX = ObjectAnimator.ofFloat(fab, "scaleX", 1f, 0f)
        val hideAnimY = ObjectAnimator.ofFloat(fab, "scaleY", 1f, 0f)
        val hideAnimSet = AnimatorSet()
        hideAnimSet.playTogether(hideAnimX, hideAnimY)

        h.playSequentially(hideAnimSet, showAnimSet)

        stateListAnimator.addState(intArrayOf(-android.R.attr.state_selected), h)
        stateListAnimator.addState(intArrayOf(android.R.attr.state_selected), h)

        fab.stateListAnimator = stateListAnimator

        /*
        BottomAppBar
         */
        //Setting up
        bar.replaceMenu(R.menu.bottom_app_bar_menu)
        bar.setOnMenuItemClickListener(this)

        swipeRefreshLayout.setOnRefreshListener(this)

        // clearFindViewByIdCache()
    }

    /*
    Public methods
     */

    fun toPage(page: Int) = viewPager.toPage(page)

    /*
    Private methods
     */

    private fun setupViewPager() {
        myMainViewPager = MyMainViewPager(supportFragmentManager, viewPager, viewPagerPageListener()).setup()
        mainActivityViewPagerAdapter = myMainViewPager?.mainActivityViewPagerAdapter

        /*
        MyMainViewPager is class that handle view pager and fragments settings up. The observer is called only when
        all fragments inside viewPager are created and ready to use
         */
        myMainViewPager?.observeForever { it ->
            it?.let { states ->
                when (states) {
                    ViewPagerStates.VIEW_PAGER_PAGES_CREATED -> {

                        numberOfPages = myMainViewPager?.getNumberOfPages() ?: 0

                        mainActivityViewPagerAdapter?.let { adapter ->
                            //Inject PagesComponent. This component is used for getting viewPager fragments
                            pagesComponent = DaggerPagesComponent.builder().pagesModule(PagesModule(adapter, viewPager)).build()
                            pagesComponent.inject(this)
                            //ViewModel
                            mainViewModel = MainViewModel(application, this)
                            myCv = mainViewModel?.myCv
                            //Observe CV
                            myCv?.observe(this, Observer {
                                //Inform fetching observers that fetch is final
                                //Get CV parts and inform each fragment associated with this about update
                                it?.apply {
                                    welcome?.let { welcome ->
                                        pagesComponent.getWelcomeScreen().update(welcome)
                                    }
                                    personalInfo?.let { personalInfo ->
                                        pagesComponent.getPersonalInfoScreen().update(personalInfo)
                                    }
                                    career?.let { career ->
                                        pagesComponent.getCareerScreen().update(career)
                                    }
                                    languages?.let { languages ->
                                        pagesComponent.getLanguagesScreen().update(languages)
                                    }
                                    skills?.let { skills ->
                                        pagesComponent.getSkillsScreen().update(skills)
                                    }
                                }
                            })
                        }
                    }
                    ViewPagerStates.VIEW_PAGER_PAGES_DESTROYED -> {
                    }
                }
            }
        }
    }

    private fun viewPagerPageListener() = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            bar.menu.apply {
                //Hide phone and mail icon (BottomAppBar) when current screen is PersonalInfoFragment
                setGroupVisible(R.id.callMailGroup, position != 1)
            }
        }
    }

    /*
    Return form SettingsFragment
     */
    private fun backFromSettings(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(this@MainActivity.mainContainer.id)
        return if (fragment is SettingsFragment) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            this@MainActivity.fab.setImageState(intArrayOf(-android.R.attr.state_selected), false)
            currentFragment = null
            this@MainActivity.bar.menu.apply {
                setGroupVisible(R.id.callMailGroup, viewPager.currentItem != 1)
                setGroupVisible(R.id.settingsGroup, true)
            }
            true
        } else false

    }

    /*
    Open settings screen
     */
    private fun goToSettings() {
        SettingsFragment().apply {
            val fade = Fade()
            enterTransition = fade
            exitTransition = fade
            this@apply.set(supportFragmentManager, this@MainActivity.mainContainer.id)
            this@MainActivity.fab.setImageState(intArrayOf(android.R.attr.state_selected), false)
            this@MainActivity.bar.menu.apply {
                setGroupVisible(R.id.callMailGroup, false)
                setGroupVisible(R.id.settingsGroup, false)
            }
        }
    }

    /*
    Override methods
     */

    override fun onFetchStart() {
        MAIN_ACTIVITY.log("start")
    }

    override fun onFetchEnd() {
        swipeRefreshLayout.isRefreshing = false
        MAIN_ACTIVITY.log("end")
    }

    override fun onFetchError(error: Throwable) {
        swipeRefreshLayout.isRefreshing = false
        MAIN_ACTIVITY.log("error")
        R.string.data_fetching_error.string().toast()
    }

    override fun onRefresh() {
        mainViewModel?.refreshAll()
    }

    override fun onMenuItemClick(item: MenuItem?) = when (item?.itemId) {
        R.id.settings -> {
            goToSettings()
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        viewPager.apply {
            when {
                backFromSettings() -> {
                }
                this@MainActivity.slideDrawer?.isOpen() != false -> slideDrawer.close()
                currentItem == pageMap[WELCOME_SCREEN] ?: 0 -> super.onBackPressed()
                else -> toLeft()
            }
        }
    }
}
