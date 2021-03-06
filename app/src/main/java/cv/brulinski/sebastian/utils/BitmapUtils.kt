package cv.brulinski.sebastian.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import cv.brulinski.sebastian.interfaces.BitmapLoadable
import cv.brulinski.sebastian.model.MyRecyclerItem
import cv.brulinski.sebastian.repository.RemoteRepository
import cv.brulinski.sebastian.repository.RemoteRepository.Companion.errorBitmap
import cv.brulinski.sebastian.repository.RemoteRepository.Companion.errorImageUrl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Download bitmap from url
 * @param url bitmap url to download
 * @return Bitmap RxJava observable
 */
fun downloadBitmap(url: String): Observable<Bitmap> {
    return Observable.create { emitter ->
        try {
            if (url.isNotEmpty()) {
                if (url == errorImageUrl) {
                    RemoteRepository.errorBitmap?.let {
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
                    if (url == errorImageUrl && errorBitmap == null)
                        errorBitmap = it
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

fun Bitmap.toBase64String() = let {
    val byteArrayOutStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutStream)
    val bitmapBytes = byteArrayOutStream.toByteArray()
    Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
}

@SuppressLint("CheckResult")
fun String.getBitmapFromBase64(bitmap: (Bitmap) -> Unit) {
    base64ToBitmap()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.let {
                    bitmap(it)
                }
            }, {
                it.printStackTrace()
            })
}

/**
 * Loads bitmap into ImageView
 * @param pairs pair of ImageView into which Bitmap will be loaded
 * @return true if success
 */
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

/**
 * Method operates on map from
 * @see getBitmapsForObjects method.
 * Decodes Base64 bitmap into Bitmap
 * @param map map of object and his Base64 bitmap representation
 * @return RxJava HashMap<T, Bitmap> observable
 */
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

/**
 * Method used for loading bitmaps into fields in passed 'T' object which must implements
 * BitmapLoadable interface. Also it methods prepare list of items which are next used in
 * RecyclerView adapters for languages list and skills list.
 * @property elements list of 'T' objects into which bitmaps will loaded
 * @property listElements lambda expression which returns prepared object to use in RecyclerAdapter
 * @see MyRecyclerItem
 * @see cv.brulinski.sebastian.adapter.recycler.languages.LanguagesRecyclerAdapter
 * @see cv.brulinski.sebastian.adapter.recycler.skills.SkillsRecyclerAdapter
 */
@SuppressLint("CheckResult")
fun <T : BitmapLoadable> getBitmapsForObjects(elements: List<T>, listElements: (ArrayList<MyRecyclerItem<T>>) -> Unit) {
    val map = HashMap<T, String>() //Map of object and it string representation of bitmap (Base64 encoded)
    //Create map
    elements.forEach {
        it.getTypeBitmapBase64()?.let { bitmapBase64 ->
            if (bitmapBase64.isNotEmpty()) map[it] = bitmapBase64
        }
    }
    val itemsWithBitmaps = ArrayList<T>()

    fun makeList() {
        val items = arrayListOf<MyRecyclerItem<T>>()
        if (itemsWithBitmaps.isNotEmpty() && itemsWithBitmaps[0].getTypeSkillCategory() != null) {
            //Prepare list of object for skills list used SkillsFragment
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
            //Prepare list of object for languages list used in LanguagesFragment
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
        //Load bitmaps into prepared list
        loadBitmapsInto(map)?.apply {
            subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        makeList()
                    }
                    .subscribe({
                        it.forEach { (t, bitmap) ->
                            t.setTypeBitmap(bitmap)
                            doAsync {
                                t.setAvgColor(bitmap.getAverageColor())
                            }
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
