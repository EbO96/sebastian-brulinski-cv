package cv.brulinski.sebastian.dependency_injection.module

import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.fragment.CareerFragment
import cv.brulinski.sebastian.fragment.PersonalInfoFragment
import cv.brulinski.sebastian.fragment.StartFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PagesModule(var fragmentStatePagerAdapter: FragmentStatePagerAdapter, var viewPager: ViewPager) {

    @Provides
    @Singleton
    fun provideStartScreen(): StartFragment = fragmentStatePagerAdapter.instantiateItem(viewPager, 0) as StartFragment

    @Provides
    @Singleton
    fun provideWelcomeScreen(): WelcomeFragment = fragmentStatePagerAdapter.instantiateItem(viewPager, 1) as WelcomeFragment

    @Provides
    @Singleton
    fun providePersonalInfoScreen(): PersonalInfoFragment = fragmentStatePagerAdapter.instantiateItem(viewPager, 2) as PersonalInfoFragment

    @Provides
    @Singleton
    fun provideCareerScreen(): CareerFragment = fragmentStatePagerAdapter.instantiateItem(viewPager, 3) as CareerFragment
}