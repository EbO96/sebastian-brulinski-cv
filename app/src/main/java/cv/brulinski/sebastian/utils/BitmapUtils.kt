package cv.brulinski.sebastian.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import cv.brulinski.sebastian.interfaces.BitmapLoadable
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.repository.MainRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

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

fun loadBitmapsIntoImageViews(vararg pairs: Pair<ImageView, String?>): Observable<Boolean>? = Observable.create<Boolean> { emitter ->
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

fun <T> loadBitmapsInto(map: HashMap<T, String>): Observable<HashMap<T, Bitmap>>? = Observable.create<HashMap<T, Bitmap>> { emitter ->
    val observables = arrayListOf<Observable<Bitmap>?>()

    map.forEach { (_, base64Bitmap) ->
        observables.add(base64Bitmap.base64ToBitmap())
    }

    val result = HashMap<T, Bitmap>()
    var count = 0
    val keys = map.keys.toList()
    Observable.merge(observables)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                emitter.onNext(result)
                emitter.onComplete()
            }
            .subscribe({
                val id = keys[count]
                result[id] = it
                count++
            }, {
                emitter.onError(it)
            })
}

@SuppressLint("CheckResult")
fun <T : BitmapLoadable> getBitmapsForObjects(elements: List<T>, listElements: (ArrayList<MyRecyclerItem<T>>) -> Unit) {
    val map = HashMap<T, String>()
    elements.forEach {
        it.getTypeBitmapBase64()?.let { bitmapBase64 ->
            if (bitmapBase64.isNotEmpty()) map[it] = bitmapBase64
        }
    }
    val itemsWithBitmaps = ArrayList<T>()

    fun makeList() {
        var items = arrayListOf<MyRecyclerItem<T>>()
        if (itemsWithBitmaps.isNotEmpty() && itemsWithBitmaps[0].getTypeSkillCategory() != null) {
            val sortedListMap = HashMap<T, List<T>>()
            itemsWithBitmaps.groupBy { it.getTypeSkillCategory() }.forEach {
                val headerItem = it.value[0]
                sortedListMap[headerItem] = it.value
            }
            sortedListMap.toSortedMap(compareBy { it.getTypeSkillCategory() }).forEach {
                val header = MyRecyclerItem(it.key, TYPE_HEADER)
                items.add(header)
                it.value.map { MyRecyclerItem(it, TYPE_ITEM) }.let {
                    items.addAll(it.sortedBy { it.item.getSortKey() })
                }
            }
        } else {
            if (itemsWithBitmaps.isNotEmpty()) {
                itemsWithBitmaps.sortedBy { it.getSortKey() }.forEach {
                    items.add(MyRecyclerItem(it, TYPE_ITEM))
                }
            } else {
                items.addAll(elements.map { MyRecyclerItem(it, TYPE_ITEM) }.sortedBy { it.item.getSortKey() })
            }
        }
        listElements(items)
    }

    if (map.isNotEmpty()) {
        loadBitmapsInto(map)?.apply {
            subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        makeList()
                    }
                    .subscribe({
                        it.forEach { (t, bitmap) ->
                            t.setTypeBitmap(bitmap)
                            itemsWithBitmaps.add(t)
                        }
                    }, {
                        it.printStackTrace()
                    })
        } ?: run {
            makeList()
        }
    } else makeList()
}

fun Bitmap.toBase64String() = let {
    val byteArrayOutStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutStream)
    val bitmapBytes = byteArrayOutStream.toByteArray()
    Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
}
