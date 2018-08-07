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
import cv.brulinski.sebastian.interfaces.DataProviderInterface
import cv.brulinski.sebastian.utils.age
import cv.brulinski.sebastian.utils.ageSufix
import cv.brulinski.sebastian.utils.date
import cv.brulinski.sebastian.utils.loadBitmapsIntoImageViews
import kotlinx.android.synthetic.main.fragment_personal_info_1.*
import java.lang.ClassCastException

/*
Fragment which is used for displaying my personal profile
with information like phone number, email address, live address, born date
 */
open class PersonalInfoFragment : Fragment(), LifecycleOwner {

    private var dataProviderInterface: DataProviderInterface? = null

    //Views
    private val shimmers by lazy {
        listOf(nameSurnameShimmer,
                phoneShimmer,
                emailShimmer,
                addressShimmer,
                birthShimmer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shimmers.start()
        dataProviderInterface?.getPersonalInfo {
            it.apply {
                shimmers.stop()
                //Load profile picture
                loadBitmapsIntoImageViews(Pair(profileImageView, profilePictureBase64))?.subscribe()

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

    /*
    Private methods
     */

    private fun List<ShimmerFrameLayout>.start() {
        this.forEach {
            it.startShimmerAnimation()
            it.visibility = View.VISIBLE
        }
    }

    private fun List<ShimmerFrameLayout>.stop() {
        this.forEach {
            it.stopShimmerAnimation()
            it.visibility = View.GONE
        }
    }

    /*
    Override methods
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            dataProviderInterface = context as? DataProviderInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement PersonalInfoCallback")
        }
    }
}
