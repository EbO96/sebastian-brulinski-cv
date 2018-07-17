package cv.brulinski.sebastian.fragment

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.interfaces.OnContentRefreshed
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.utils.age
import cv.brulinski.sebastian.utils.ageSufix
import cv.brulinski.sebastian.utils.date
import kotlinx.android.synthetic.main.fragment_personal_info.*
import java.lang.ClassCastException

class PersonalInfoFragment : Fragment(), LifecycleOwner {

    interface PersonalInfoCallback : OnContentRefreshed {
        fun getPersonalInfo(): LiveData<PersonalInfo>?
        fun goToCareerScreen()
    }

    //Callback to parent activity
    private lateinit var personalInfoCallback: PersonalInfoCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneTextView.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        personalInfoCallback.apply {
            getPersonalInfo()?.observe(this@PersonalInfoFragment, Observer {
                it?.apply {
                    profileImageView.setImageBitmap(profilePicture)
                    bcgImageView.setImageBitmap(profileBcg)
                    nameAndSurnameTextView.text = "$name\n$surname"
                    val bornDate = "$birthDay.$birthMonth.$birthYear".trim()
                    birthDateTextView.text = bornDate
                    ageTextView.apply {
                        val age = bornDate.date()?.age() ?: -1
                        if (age != -1)
                            "$age ${age.ageSufix()}".apply {
                                text = this
                            }
                    }
                    phoneTextView.text = phoneNumber
                    emailTextView.text = email
                    cityNameTextView.text = cityName
                    provinceNameTextView.text = provinceName
                }
                onRefreshed()
            })
        }
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
