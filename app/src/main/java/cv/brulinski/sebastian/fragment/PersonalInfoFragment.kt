package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.model.PersonalInfo
import kotlinx.android.synthetic.main.fragment_personal_info.*
import java.lang.ClassCastException

class PersonalInfoFragment : Fragment(), LifecycleOwner {

    interface PersonalInfoCallback {
        fun getPersonalInfo(): LiveData<PersonalInfo>?
    }

    //Callback to parent activity
    private lateinit var personalInfoCallback: PersonalInfoCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalInfoCallback.getPersonalInfo()?.observe(this, Observer {
            it?.apply {
                profileImageView.setImageBitmap(profilePicture)
                bcgImageView.setImageBitmap(profileBcg)
                nameAndSurnameTextView.text = "$name\n$surname"
                birthDateTextView.text = "$birthDay.$birthMonth.$birthYear"
                phoneTextView.text = phoneNumber
                emailTextView.text = email
                locationTextView.text = "$cityName\n$provinceName"
            }
        })
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
