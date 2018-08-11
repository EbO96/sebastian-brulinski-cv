package cv.brulinski.sebastian.activity

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.transition.Fade
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomappbar.BottomAppBar
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page.*
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.dependency_injection.component.DaggerPagesComponent
import cv.brulinski.sebastian.dependency_injection.component.PagesComponent
import cv.brulinski.sebastian.dependency_injection.module.PagesModule
import cv.brulinski.sebastian.fragment.*
import cv.brulinski.sebastian.interfaces.OnFetchingStatuses
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.model.*
import cv.brulinski.sebastian.utils.*
import cv.brulinski.sebastian.utils.view_pager.toLeft
import cv.brulinski.sebastian.utils.view_pager.toPage
import cv.brulinski.sebastian.view.LargeSnackbar
import cv.brulinski.sebastian.view.SlideDrawer
import cv.brulinski.sebastian.view_model.MainViewModel
import gone
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import visible

class MainActivity : AppCompatActivity(),
        Toolbar.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnFetchingStatuses,
        ParentActivityCallback {

    companion object {
        /*
        Request codes
         */
        //Open skills url request code
        const val OPEN_URL_REQUEST_CODE = 0
        //Permissions request codes
        const val REQUEST_CODE_MAKE_CALL = 1
        //Activity request codes
        const val APP_SETTINGS_REQUEST_CODE = 2
    }

    //Colors for snackbar
    private val colorError by lazy { R.color.colorError.color() }
    private val colorWarning by lazy { R.color.colorWarning.color() }
    private val cardLight by lazy { R.color.cardview_light_background.color() }
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
            drawerTitle = ""
            itemsDividerEnabled = true
            menuShowHideAnimation = false
        }, {
            //Menu items
            val items = arrayListOf(SlideDrawer.DrawerMenuItem(R.string.introduction.string()),
                    SlideDrawer.DrawerMenuItem(R.string.personal_details.string()),
                    SlideDrawer.DrawerMenuItem(R.string.career.string()),
                    SlideDrawer.DrawerMenuItem(R.string.languages.string()),
                    SlideDrawer.DrawerMenuItem(R.string.skills.string()))
            setMenu(items, GlobalMenuTheme(titleColorSelected = R.color.whiteBackgroundTextColor,
                    subtitleColorSelected = R.color.whiteBackgroundTextColorLight,
                    titleColor = android.R.color.darker_gray,
                    subtitleColor = R.color.colorPrimaryDark))
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
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (viewPager.currentItem in 1..(mainActivityViewPagerAdapter?.count ?: 1)) {
                    val currentState = bar.fabAlignmentMode
                    if (currentState != BottomAppBar.FAB_ALIGNMENT_MODE_END)
                        changeFabPosition(BottomAppBar.FAB_ALIGNMENT_MODE_END)
                } else changeFabPosition(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER)
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            when (position) {
                pageMap[WELCOME_SCREEN] -> {

                }
                pageMap[PERSONAL_INFO_SCREEN] -> {
                }
                pageMap[CAREER] -> {

                }
                pageMap[LANGUAGES] -> {

                }
                pageMap[SKILLS] -> {

                }
            }
            bar.menu.apply {
                //Hide phone and mail icon (BottomAppBar) when current screen is PersonalInfoFragment
                setGroupVisible(R.id.callMailGroup, position != 1)
            }
        }
    }

    //Return form SettingsFragment
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

    //Open settings screen
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

    private fun PersonalInfo.makeACall() {
        Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
            startActivity(this)
        }
    }

    private fun shouldRequestRationale(permission: String) =
            ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    permission)

    private fun makeRequestForCallPermission() {
        ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CODE_MAKE_CALL)
    }

    private fun makeSnackBarCallExplanation() {
        LargeSnackbar.getInstance().apply {
            show(mainContent, fab,
                    R.string.warning.string(),
                    R.string.explanation_line_content.string(),
                    LargeSnackbar.Duration.LONG,
                    R.string.settings.string()) {
                goToAppSettings()
            }
        }
    }

    private fun makeSnackbar(@ColorInt colorFirst: Int, @ColorInt colorSecond: Int,
                             title: String, subtitle: String, action: String) {
        LargeSnackbar.getInstance().apply {
            setBackgroundColor(colorFirst)
            setTextTitleColor(colorSecond)
            setTextMessageColor(colorSecond)
            setButtonColor(colorSecond)
            show(mainContent, fab, title, subtitle,
                    LargeSnackbar.Duration.SHORT, action) {

            }
        }
    }

    private fun makeSnackbarFetchingError() {
        makeSnackbar(colorError,
                cardLight,
                R.string.ups.string(),
                R.string.data_fetching_error.string(),
                android.R.string.ok.string())
    }

    private fun makeSnackbarNoConnectionToNetwork() {
        makeSnackbar(colorWarning,
                cardLight,
                R.string.offline.string(),
                R.string.no_connection_message.string(),
                android.R.string.ok.string())
    }


    private fun goToAppSettings() {
        val permissionSettingsIntent = Intent()
        permissionSettingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        permissionSettingsIntent.data = uri
        startActivity(permissionSettingsIntent)
    }

    /*
    Override methods
     */

    override fun showLoading() = loadingLayout.visible()

    override fun hideLoading() = loadingLayout.gone()

    override fun changeFabPosition(position: Int) {
        bar.fabAlignmentMode = position
    }

    /*
    Fetching data statuses
     */
    override fun onFetchStart() {
    }

    override fun onFetchEnd() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onFetchError(error: Throwable) {
        swipeRefreshLayout.isRefreshing = false
        if (isNetworkAvailable())
            makeSnackbarFetchingError()
        else makeSnackbarNoConnectionToNetwork()
    }

    /*
    ParentActivityCallback callback's
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

    /**
     * Try to make phone call
     */
    override fun tryMakeACall() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission is not granted
            makeRequestForCallPermission()
        } else {
            //Permission granted
            personalInfo.value?.makeACall()
        }
    }

    /**
     * Compose email and open app which can handle sending emails
     */
    override fun composeEmail() {
        personalInfo.value?.apply {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_EMAIL, email)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                resolveActivity(packageManager)?.let {
                    loadingLayout.visible()
                    startActivityForResult(this, APP_SETTINGS_REQUEST_CODE)
                }
            }
        }
    }

    override fun onRefresh() {
        mainViewModel?.refreshAll()
    }

    /**
     * BottomAppBar menu items click listener
     */
    override fun onMenuItemClick(item: MenuItem?) = when (item?.itemId) {
        R.id.settings -> {
            goToSettings()
            true
        }
        R.id.call -> {
            tryMakeACall()
            true
        }
        R.id.mail -> {
            composeEmail()
            true
        }
        else -> false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_MAKE_CALL -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    personalInfo.value?.makeACall()
                } else {
                    if (!shouldRequestRationale(Manifest.permission.CALL_PHONE))
                        makeSnackBarCallExplanation()
                    MAIN_ACTIVITY.log("on request permissions result denied")
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            APP_SETTINGS_REQUEST_CODE, OPEN_URL_REQUEST_CODE -> {
                loadingLayout.gone()
            }
        }
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
