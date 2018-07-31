package cv.brulinski.sebastian.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import cv.brulinski.sebastian.repository.MainRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayInputStream
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
            if (url.isNotEmpty()) {
                if (url == MainRepository.errorImageUrl) {
                    MainRepository.errorBitmap?.let {
                        emitter.onNext(it)
                        emitter.onComplete()
                        return@create
                    }
                }
                val path = URL(url)
                val connection = (path.openConnection() as HttpURLConnection).apply {
                    doInput = true
                    connect()
                }
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let {
                    if (url == MainRepository.errorImageUrl && MainRepository.errorBitmap == null)
                        MainRepository.errorBitmap = it
                    emitter.onNext(it)
                }
            } else {
                emitter.onError(Throwable("Wrong image url"))
            }
            emitter.onComplete()
        } catch (e: IOException) {
            emitter.onError(e)
        }
    }
}

fun String.base64ToBitmap() = Observable.create<Bitmap> { emitter ->
    try {
        val byteArray = Base64.decode(this, Base64.DEFAULT)
        val stream = ByteArrayInputStream(byteArray)
        BitmapFactory.decodeStream(stream)?.let { emitter.onNext(it) }
        emitter.onComplete()
    } catch (e: Exception) {
        emitter.onError(e)
    }
}

fun loadBitmapsIntoImageViews(vararg pairs: Pair<ImageView, String?>) = Observable.create<Boolean> { emitter ->
    val observables = arrayListOf<Observable<Bitmap>?>()
    pairs.forEach {
        observables.add(it.second?.base64ToBitmap())
    }
    var count = 0

    Observable.merge(observables)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                emitter.onNext(true)
                emitter.onComplete()
            }
            .subscribe({
                pairs[count].first.setImageBitmap(it)
                count++
            }, {
                emitter.onError(it)
            })
}


fun Bitmap.toBase64String() = let {
    val byteArrayOutStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutStream)
    val bitmapBytes = byteArrayOutStream.toByteArray()
    Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
}
