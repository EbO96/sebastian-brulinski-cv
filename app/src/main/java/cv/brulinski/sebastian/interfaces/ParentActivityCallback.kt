package cv.brulinski.sebastian.interfaces

import androidx.fragment.app.Fragment
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page
import cv.brulinski.sebastian.model.*

/**
 * Used for getting CV data from MainActivity by each fragment
 * @see cv.brulinski.sebastian.activity.MainActivity
 * @see Welcome
 * @see PersonalInfo
 * @see Career
 * @see Language
 * @see Skill
 */
interface ParentActivityCallback {
    fun getWelcome(block: (Welcome) -> Unit)
    fun getPersonalInfo(block: (PersonalInfo) -> Unit)
    fun getCareer(block: (List<Career>) -> Unit)
    fun getLanguages(block: (List<Language>) -> Unit)
    fun getSkills(block: (List<Skill>) -> Unit)
    fun getCredits(block: (List<Credit>) -> Unit)
    fun refreshCredits(block: (List<Credit>?) -> Unit)
    fun tryMakeACall()
    fun composeEmail()
    fun changeFabPosition(position: Int)
    fun goToPage(page: Page)
    fun registerForCvNotifications(register: Boolean, status: (Int) -> Unit)
    fun goToCredits()
    fun onFragmentDestroyed(fragment: Fragment)
    fun openMapsActivity(lat: Double?, lng: Double?)
    fun logout()
}