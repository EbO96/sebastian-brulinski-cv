package cv.brulinski.sebastian.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
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
import cv.brulinski.sebastian.fragment.*
import cv.brulinski.sebastian.interfaces.DataProviderInterface
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.utils.*
import cv.brulinski.sebastian.utils.view_pager.toLeft
import cv.brulinski.sebastian.utils.view_pager.toPage
import cv.brulinski.sebastian.view.SlideDrawer
import cv.brulinski.sebastian.view_model.MainViewModel
import inflate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*

class MainActivity : AppCompatActivity(),
        Toolbar.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnFetchingStatuses,
        DataProviderInterface {

    //ViewPager adapter
    private var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter? = null
    //ViewModel
    private var mainViewModel: MainViewModel<*>? = null
    //Dagger ViewPager pages component
    private lateinit var pagesComponent: PagesComponent
    //Data for fragments
    private val welcome: MutableLiveData<Welcome> = MutableLiveData()
    private val personalInfo: MutableLiveData<PersonalInfo> = MutableLiveData()
    private val career: MutableLiveData<List<Career>> = MutableLiveData()
    private val languages: MutableLiveData<List<Language>> = MutableLiveData()
    private val skills: MutableLiveData<List<Skill>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeCv()
        viewPager.set()

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
    }

    /*
    Public methods
     */

    fun toPage(page: Int) = viewPager.toPage(page)

    /*
    Private methods
     */

    private fun ViewPager.set() {
        val fragments = arrayListOf(
                WelcomeFragment(),
                PersonalInfoFragment(),
                CareerFragment(),
                LanguagesFragment(),
                SkillsFragment()
        )
        val titles = arrayListOf(R.string.introduction.string(),
                R.string.personal_details.string(),
                R.string.career.string(),
                R.string.languages.string(),
                R.string.skills.string())

        mainActivityViewPagerAdapter = MainActivityViewPagerAdapter(fragments, titles, supportFragmentManager).apply {
            pagesComponent = DaggerPagesComponent.builder().pagesModule(PagesModule(this, viewPager)).build()
            pagesComponent.inject(this@MainActivity)
        }
        offscreenPageLimit = fragments.size
        this@set.adapter = mainActivityViewPagerAdapter
        addOnPageChangeListener(viewPagerPageListener())
    }

    private fun observeCv() {
        mainViewModel = MainViewModel(application, this)
        mainViewModel?.myCv?.observe(this, Observer {
            //Inform fetching observers that fetch is final
            //Get CV parts and inform each fragment associated with this about update
            it?.apply {
                welcome?.let { welcome ->
                    this@MainActivity.welcome.value = welcome
                }
                personalInfo?.let { personalInfo ->
                    this@MainActivity.personalInfo.value = personalInfo
                }
                career?.let { career ->
                    this@MainActivity.career.value = career
                }
                languages?.let { languages ->
                    this@MainActivity.languages.value = languages
                }
                skills?.let { skills ->
                    this@MainActivity.skills.value = skills
                }
            }
        })
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

    /*
    Fetching data statuses
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

    /*
    DataProviderInterface callback's
     */
    override fun getWelcome(block: (Welcome) -> Unit) {
        welcome.observe(this, Observer {
            it?.let { block(it) }
        })
    }

    override fun getPersonalInfo(block: (PersonalInfo) -> Unit) {
        personalInfo.observe(this, Observer {
            it?.let { block(it) }
        })
    }

    override fun getCareer(block: (List<Career>) -> Unit) {
        career.observe(this, Observer {
            it?.let { block(it) }
        })
    }

    override fun getLanguages(block: (List<Language>) -> Unit) {
        languages.observe(this, Observer {
            it?.let { block(it) }
        })
    }

    override fun getSkills(block: (List<Skill>) -> Unit) {
        skills.observe(this, Observer {
            it?.let { block(it) }
        })
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
