package cv.brulinski.sebastian.model

/**
 * Class to hold personal info
 */
class PersonalInfo {

    var name = ""
    var surname = ""
    var birthDay = 0
    var birthMonth = 0
    var birthYear = 0
    var profilePhotoUrl = ""

    class Contact {

        var phoneNumber = 0L
        var email = ""

    }

    class Address {

        var cityName = ""
        var provinceName = ""
        var latitude = 0.0
        var longitude = 0.0

    }
}