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

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
exports.getWelcome = functions.https.onRequest((request, response) => {
    return db
        .collection('welcome')
        .get()
        .then((snapshot) => {
            var d;
            snapshot.forEach((doc) => {
                d = doc.data()
            });
            response.status(200).json(d)
        })
        .catch((err) => {
            response.status(401).json(err)
        })
});

exports.getPersonalInfo = functions.https.onRequest((request, response) => {
    return db
        .collection('personal_data')
        .get()
        .then((snapshot) => {
            snapshot.forEach((doc) => {
                response.status(200).json(doc.data())
            })
        }).catch((err) => {
            response.status(401).json(err)
        })
})

function getSchoolsPromise() {
    return db
        .collection('career')
        .doc('school')
        .collection('schools')
        .get()
}

function getJobsPromise() {
    return db
        .collection('career')
        .doc('job')
        .collection('jobs')
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

exports.getSchools = functions.https.onRequest((request, response) => {
    var resultJson = []
    return getSchoolsPromise()
        .then((snapshot) => {
            snapshot.forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                resultJson.push(d)
            })
            response.status(200).json(resultJson)
        }).catch((err) => {
            response.status(200).json(resultJson)
        })
})

exports.getJobs = functions.https.onRequest((request, response) => {
    var resultJson = []
    return getJobsPromise()
        .then((snapshot) => {
            snapshot.forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                resultJson.push(d)
            })
            response.status(200).json(resultJson)
        }).catch((err) => {
            response.status(401).json(resultJson)
        })
})

exports.getCareer = functions.https.onRequest((request, response) => {
    var requests = []
    requests.push(getSchoolsPromise())
    requests.push(getJobsPromise())

    return Promise.all(requests)
        .then((results) => {
            var result = { status: 1, schools: [], jobs: [] }
            schoolsSnapshot = results[0]
            jobsSnapshot = results[1]

            schoolsSnapshot.forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                result.schools.push(d)
            })

            jobsSnapshot.forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                result.jobs.push(d)
            })

            response.status(200).json(result)
        }).catch(err => {
            response.status(401).json({ status: -1 })
        })
})

exports.getAll = functions.https.onRequest((request, response) => {
    var requests = []
    var jsonResult = { status: -1, welcome: {}, personal_info: {}, career: {}, languages: [] }
    requests.push(getWelcomePromise())
    requests.push(getPersonalInfoPromise())
    requests.push(getSchoolsPromise())
    requests.push(getJobsPromise())
    requests.push(getLanguagesPromise())

    return Promise.all(requests)
        .then((results) => {

            var welcome
            results[0].forEach((doc) => {
                welcome = doc.data()
            });
            jsonResult.welcome = welcome

            var personalInfo
            results[1].forEach((doc) => {
                personalInfo = doc.data()
            })
            jsonResult.personal_info = personalInfo

            var career = { schools: [], jobs: [] }

            results[2].forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                career.schools.push(d)
            })

            results[3].forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                career.jobs.push(d)
            })
            jsonResult.career = career

            results[4].forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                jsonResult.languages.push(d)
            })

            jsonResult.status = 1
            response.status(200).json(jsonResult)

        }).catch((error) => {
            response.status(401).json(jsonResult)
        })
})