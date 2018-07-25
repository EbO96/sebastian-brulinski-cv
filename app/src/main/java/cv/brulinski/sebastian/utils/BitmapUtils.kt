package cv.brulinski.sebastian.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Observable

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
                    }
                })
    }
}