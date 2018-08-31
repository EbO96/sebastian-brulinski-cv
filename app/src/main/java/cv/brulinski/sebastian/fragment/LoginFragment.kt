package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.interfaces.SplashScreenCallback
import cv.brulinski.sebastian.utils.snack
import cv.brulinski.sebastian.utils.string
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Class to handle login user to firebase
 */
class LoginFragment : Fragment() {

    //Parent callback interface
    private var splashScreenCallback: SplashScreenCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            if (!emailEditText.text.isNullOrEmpty() && !passwordEditText.text.isNullOrEmpty())
                login()
        }
    }

    private fun login() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("${emailEditText.text}", "${passwordEditText.text}")
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        activity?.also { activity ->
                            R.string.login_successful.string().snack(activity) {
                                activity.supportFragmentManager.popBackStack()
                                splashScreenCallback?.loginSuccessful()
                            }
                        }
                    } else {
                        getString(R.string.access_denied).snack(activity!!)
                    }
                }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        splashScreenCallback = context as? SplashScreenCallback
    }

}
