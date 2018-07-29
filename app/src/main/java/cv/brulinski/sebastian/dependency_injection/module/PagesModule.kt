package cv.brulinski.sebastian.dependency_injection.module

import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.fragment.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PagesModule(var fragmentStatePagerAdapter: FragmentStatePagerAdapter, var viewPager: ViewPager) {

    @Provides
    @Singleton
    fun provideWelcomeScreen(): WelcomeFragment = fragmentStatePagerAdapter.instantiateItem(viewPager,
            MainActivityViewPagerAdapter.pageMap[MainActivityViewPagerAdapter.Companion.Page.WELCOME_SCREEN]
                    ?: 0) as WelcomeFragment

    @Provides
    @Singleton
    fun providePersonalInfoScreen(): PersonalInfoFragment = fragmentStatePagerAdapter.instantiateItem(viewPager,
            MainActivityViewPagerAdapter.pageMap[MainActivityViewPagerAdapter.Companion.Page.PERSONAL_INFO_SCREEN]
                    ?: 1) as PersonalInfoFragment

    @Provides
    @Singleton
    fun provideCareerScreen(): CareerFragment = fragmentStatePagerAdapter.instantiateItem(viewPager,
            MainActivityViewPagerAdapter.pageMap[MainActivityViewPagerAdapter.Companion.Page.CAREER]
                    ?: 2) as CareerFragment

    @Provides
    @Singleton
    fun provideLanguagesScreen(): LanguagesFragment = fragmentStatePagerAdapter.instantiateItem(viewPager,
            MainActivityViewPagerAdapter.pageMap[MainActivityViewPagerAdapter.Companion.Page.LANGUAGES]
                    ?: 3) as LanguagesFragment

    @Provides
    @Singleton
    fun provideSkillsScreen(): SkillsFragment = fragmentStatePagerAdapter.instantiateItem(viewPager,
            MainActivityViewPagerAdapter.pageMap[MainActivityViewPagerAdapter.Companion.Page.SKILLS]
                    ?: 4) as SkillsFragment
}