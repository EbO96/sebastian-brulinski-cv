package cv.brulinski.sebastian.dependency_injection.component

import cv.brulinski.sebastian.activity.MainActivity
import cv.brulinski.sebastian.dependency_injection.module.PagesModule
import cv.brulinski.sebastian.fragment.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PagesModule::class])
interface PagesComponent {

    fun getWelcomeScreen(): WelcomeFragment

    fun getPersonalInfoScreen(): PersonalInfoFragment

    fun getCareerScreen(): CareerFragment

    fun getLanguagesScreen(): LanguagesFragment

    fun getSkillsScreen(): SkillsFragment

    fun inject(mainActivity: MainActivity)
}