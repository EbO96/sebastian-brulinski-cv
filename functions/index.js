const functions = require('firebase-functions');
const admin = require('firebase-admin');
const requestify = require('requestify');
var serviceAccount = require("./sebastian-brulinski-cv-app-firebase-adminsdk-b1ddn-608e079791");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://sebastian-brulinski-cv-app.firebaseio.com"
});

const baseUrl = 'https://us-central1-sebastian-brulinski-cv-app.cloudfunctions.net/'

var db = admin.firestore();

function getCareerProomise() {
    return db
        .collection('career')
        .get()
}
function getWelcomePromise() {
    return db
        .collection('welcome')
        .get()
}

function getPersonalInfoPromise() {
    return db
        .collection('personal_data')
        .get()
}

function getLanguagesPromise() {
    return db
        .collection('languages')
        .get()
}

exports.getAll = functions.https.onRequest((request, response) => {
    var requests = []
    var jsonResult = { status: -1, welcome: { timestamp: -1 }, personal_info: { timestamp: -1 }, career: [], languages: [] }
    requests.push(getWelcomePromise())
    requests.push(getPersonalInfoPromise())
    requests.push(getCareerProomise())
    requests.push(getLanguagesPromise())

    return Promise.all(requests)
        .then((results) => {

            const time = Date.now()

            var welcome
            results[0].forEach((doc) => {
                welcome = doc.data()
            });
            jsonResult.welcome = welcome
            jsonResult.welcome.timestamp = time

            var personalInfo
            results[1].forEach((doc) => {
                personalInfo = doc.data()
            })
            jsonResult.personal_info = personalInfo
            jsonResult.personal_info.timestamp = time

            results[2].forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                d["timestamp"] = time
                jsonResult.career.push(d)
            })

            results[3].forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                d["timestamp"] = time
                jsonResult.languages.push(d)
            })

            jsonResult.status = 1
            response.status(200).json(jsonResult)

        }).catch((error) => {
            response.status(401).json(jsonResult)
        })
})

exports.addCareer = functions.https.onRequest((request, response) => {
    var career = {
        endTime: "",
        function: "",
        description: "",
        startTime: "",
        placeName: "",
        latitude: 0,
        longitude: 0,
        endTimeDescription: "",
        type: 1,
        startTimeDescription: ""
    }
    return db.collection('career').add(career)
        .then(result => {
            response.status(200).send("Success")
        }).catch(error => {
            response.status(401).send("Error")
        })
})

exports.addLanguage = functions.https.onRequest((request, response) => {
    var language = {
        name: "",
        description: "",
        level: 0,
        levelScale: 5,
        imageUrl: ""
    }
    return db.collection('languages').add(language)
        .then(result => {
            response.status(200).send("Success")
        }).catch(error => {
            response.status(401).send("Error")
        })
})