package cv.brulinski.sebastian.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.fragment.StartFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.utils.get
import kotlinx.android.synthetic.main.activity_main.*
import setBaseToolbar

class MainActivity : AppCompatActivity(),
        StartFragment.StartFragmentCallback {

    //ViewPager adapter
    private lateinit var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setBaseToolbar(title = R.string.start.get())

        setupViewPager()
    }

    private fun setupViewPager() {
        val fragments = ArrayList<Fragment>().apply {
            add(StartFragment())
            add(WelcomeFragment())
        }
        val pagesTitles = ArrayList<String>().apply {
            add(R.string.start.get().apply { asToolbarTitle() })
            add(R.string.welcome.get())
        }
        viewPager.apply {
            mainActivityViewPagerAdapter = MainActivityViewPagerAdapter(fragments, pagesTitles, supportFragmentManager).apply { adapter = this }

            addOnPageChangeListener(viewPagerPageListener())
        }
    }

    private fun viewPagerPageListener() = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            mainActivityViewPagerAdapter.getPageTitle(position).asToolbarTitle()
        }
    }

    private fun String.asToolbarTitle() {
        supportActionBar?.title = this
    }

    /*
    VIEWPAGER FRAGMENTS CALLBACKS BELOW
     */

    /*
    Start Fragment callbacks
     */

    override fun pdfVersionClick() {

    }

    override fun electronicVersionClick() {

    }
}
