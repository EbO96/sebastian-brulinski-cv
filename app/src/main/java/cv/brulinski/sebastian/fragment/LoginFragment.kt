package cv.brulinski.sebastian.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.model.Auth
import cv.brulinski.sebastian.utils.hideKeyboard
import cv.brulinski.sebastian.utils.snack
import cv.brulinski.sebastian.utils.string
import hideLoading
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import showLoading

/**
 * Class to handle login user to firebase
 */
class LoginFragment : Fragment() {

    //Parent callback interface
    private var emailPasswordLogin: EmailPasswordLogin? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.loginButton.setOnClickListener {
            tryLogin()
        }

        view.qrLoginButton.setOnClickListener {
            emailPasswordLogin?.tryQrLogin()
        }

        view.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    tryLogin()
                    true
                }
                else -> false
            }
        }
    }

    /*
    Public methods
     */

    fun snackbarAnchorView() = view?.snackAnchorView

    fun getRootView() = view?.loginScreenContainer

    fun login(auth: Auth) {
        activity?.showLoading()
        activity?.hideKeyboard()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(auth.email, auth.password)
                .addOnCompleteListener {
                    activity?.hideLoading()
                    if (it.isSuccessful) {
                        activity?.also { activity ->
                            R.string.login_successful.string().snack(activity) {
                                activity.supportFragmentManager.popBackStack()
                                emailPasswordLogin?.emailPasswordSuccessful()
                            }
                        }
                    } else {
                        getString(R.string.access_denied).snack(activity!!)
                    }
                }
    }

    /*
    Private methods
     */

    /**
     * Get email and password from editTexts and try to login
     */
    private fun tryLogin() {
        val auth = Auth("${emailEditText.text}", "${passwordEditText.text}")
        if (auth.valid())
            login(auth)
    }

    /*
    Override methods
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        emailPasswordLogin = context as? EmailPasswordLogin
    }

}
