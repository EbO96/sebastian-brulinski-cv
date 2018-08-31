package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.facebook.shimmer.ShimmerFrameLayout
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.interfaces.ParentActivityCallback
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.utils.age
import cv.brulinski.sebastian.utils.ageSufix
import cv.brulinski.sebastian.utils.date
import cv.brulinski.sebastian.utils.loadBitmapsIntoImageViews
import kotlinx.android.synthetic.main.fragment_personal_info.view.*
import java.lang.ClassCastException

/*
Fragment which is used for displaying my personal profile
with information like phone number, email address, live address, born date
 */
open class PersonalInfoFragment : Fragment(), LifecycleOwner {

    private var parentActivityCallback: ParentActivityCallback? = null
    private var personalInfo: PersonalInfo? = null

    //Views
    private val shimmers by lazy {
        listOf(view?.nameSurnameShimmer,
                view?.phoneShimmer,
                view?.emailShimmer,
                view?.addressShimmer,
                view?.birthShimmer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shimmers.start()
        parentActivityCallback?.getPersonalInfo {
            personalInfo = it
            shimmers.stop()
            it.apply {
                //Load profile picture
                loadBitmapsIntoImageViews(Pair(view.profileImageView, profilePictureBase64))?.subscribe()

                view.apply {
                    //Set information about me
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
        }

        view.phoneFrame.setOnClickListener {
            parentActivityCallback?.tryMakeACall()
        }

        view.emailFrame.setOnClickListener {
            parentActivityCallback?.composeEmail()
        }

        view.addressFrame.setOnClickListener {
            parentActivityCallback?.openMapsActivity(personalInfo?.latitude, personalInfo?.longitude)
        }
    }

    /*
    Public methods
     */
    fun getPersonalInfo() = personalInfo

    /*
    Private methods
     */

    private fun List<ShimmerFrameLayout?>.start() {
        this.forEach {
            it?.startShimmerAnimation()
            it?.visibility = View.VISIBLE
        }
    }

    private fun List<ShimmerFrameLayout?>.stop() {
        this.forEach {
            it?.stopShimmerAnimation()
            it?.visibility = View.GONE
        }
    }

    /*
    Override methods
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            parentActivityCallback = context as? ParentActivityCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement PersonalInfoCallback")
        }
    }
}
