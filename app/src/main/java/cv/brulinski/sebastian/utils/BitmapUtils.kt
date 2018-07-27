package cv.brulinski.sebastian.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

fun fetchBitmap(url: String): Observable<Bitmap> {
    return Observable.create { emitter ->
        Picasso.with(ctx)
                .load(url)
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }

                    override fun onBitmapFailed(errorDrawable: Drawable?) {
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bitmap?.apply { emitter.onNext(this) }
                        emitter.onComplete()
                    }
                })
    }
}

fun downloadBitmap(url: String): Observable<Bitmap> {
    return Observable.create { emitter ->
        try {
            val path = URL(url)
            val connection = (path.openConnection() as HttpURLConnection).apply {
                doInput = true
                connect()
            }
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap?.let {
                emitter.onNext(it)
            }
            emitter.onComplete()
        } catch (e: IOException) {
            emitter.onError(e)
        }
    }
}

fun Bitmap.toBase64String() = let {
    val byteArrayOutStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutStream)
    val bitmapBytes = byteArrayOutStream.toByteArray()
    Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
}
