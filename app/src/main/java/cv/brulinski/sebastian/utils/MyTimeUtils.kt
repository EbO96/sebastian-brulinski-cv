package cv.brulinski.sebastian.utils

import android.annotation.SuppressLint
import cv.brulinski.sebastian.R
import org.joda.time.LocalDate
import org.joda.time.Years
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

fun String.date(): Date? = try {
    dateFormat.parse(this)
} catch (e: ParseException) {
    null
}

fun Date.age(): Int {
    val born = Calendar.getInstance()
    born.time = this
    val current = Calendar.getInstance()
    val birth = LocalDate(born[Calendar.YEAR], born[Calendar.MONTH], born[Calendar.DAY_OF_MONTH])
    val now = LocalDate(current[Calendar.YEAR], current[Calendar.MONTH], current[Calendar.DAY_OF_MONTH])
    return Years.yearsBetween(birth, now).years
}

fun Int.ageSufix() = when (this % 10) {
    2, 3, 4 -> R.string.years.string()
    else -> R.string.year.string()
}