package cv.brulinski.sebastian.dependency_injection.component

import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.activity.MainActivity
import cv.brulinski.sebastian.dependency_injection.module.PagesModule
import cv.brulinski.sebastian.fragment.CareerFragment
import cv.brulinski.sebastian.fragment.PersonalInfoFragment
import cv.brulinski.sebastian.fragment.StartFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PagesModule::class])
interface PagesComponent {

    fun getStartScreen(): StartFragment

    fun getWelcomeScreen(): WelcomeFragment

    fun getPersonalInfoScreen(): PersonalInfoFragment

    fun getCareerFragment(): CareerFragment

    fun inject(mainActivity: MainActivity)
}