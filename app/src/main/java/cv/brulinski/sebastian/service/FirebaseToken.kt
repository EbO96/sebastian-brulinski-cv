package cv.brulinski.sebastian.service

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId


class FirebaseToken(result: (String?) -> Unit) {

    init {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        result(null)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result.token
                    result(token)
                })
    }
}