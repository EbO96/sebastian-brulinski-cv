package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.utils.age
import cv.brulinski.sebastian.utils.ageSufix
import cv.brulinski.sebastian.utils.date
import cv.brulinski.sebastian.utils.loadBitmapsIntoImageViews
import kotlinx.android.synthetic.main.fragment_personal_info.*
import java.lang.ClassCastException

class PersonalInfoFragment : Fragment(), LifecycleOwner {

    interface PersonalInfoCallback {
    }

    //Callback to parent activity
    private lateinit var personalInfoCallback: PersonalInfoCallback

    companion object {
        var viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null
        fun newInstance(viewPagerUtilsFragmentCreatedListener: ViewPagerUtilsFragmentCreatedListener? = null): PersonalInfoFragment {
            this.viewPagerUtilsFragmentCreatedListener = viewPagerUtilsFragmentCreatedListener
            return PersonalInfoFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerUtilsFragmentCreatedListener?.onFragmentCreated()
    }

    fun update(personalInfo: PersonalInfo) {
        personalInfo.apply {
            loadBitmapsIntoImageViews(Pair(profileImageView, profilePictureBase64))?.subscribe()
            val nameAndSurname = "$name $surname"
            nameAndSurnameTextView?.text = nameAndSurname
            specialityTextView.text = speciality
            numberTextView?.text = phoneNumber
            numberTypeTextView.text = numberType
            emailTextView?.text = email
            emailTypeTextView.text = emailType
            val a1 = "$cityName, $postalCode"
            val a2 = "$country, $provinceName"
            address1TextView?.text = a1
            address2TextView?.text = a2
            val bornDate = "$birthDay.$birthMonth.$birthYear".trim()
            birthDateTextView?.text = bornDate
            ageTextView?.apply {
                val age = bornDate.date()?.age() ?: -1
                if (age != -1)
                    "$age ${age.ageSufix()}".apply {
                        text = this
                    }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPagerUtilsFragmentCreatedListener?.onFragmentDestroyed()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            personalInfoCallback = context as PersonalInfoCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement PersonalInfoCallback")
        }
    }
}
