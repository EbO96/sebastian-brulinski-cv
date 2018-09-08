const functions = require('firebase-functions');
const admin = require('firebase-admin');
const requestify = require('requestify');
var serviceAccount = require("./sebastian-brulinski-cv-app-firebase-adminsdk-b1ddn-608e079791");
const express = require('express');
const cookieParser = require('cookie-parser')();
const cors = require('cors')({ origin: true });
const app = express();
const app1 = express();
const app2 = express();
const app3 = express();

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://sebastian-brulinski-cv-app.firebaseio.com"
});

const baseUrl = 'https://us-central1-sebastian-brulinski-cv-app.cloudfunctions.net/'
//Clound Messaging topics
const FCM_NEW_CV_TOPIC = "new_cv_topic"

const firestore = admin.firestore();

// const settings = { timestampsInSnapshots: true };
// firestore.settings(settings);

function getCareerProomise() {
    return firestore
        .collection('career')
        .get()
}
function getWelcomePromise() {
    return firestore
        .collection('welcome')
        .get()
}

function getPersonalInfoPromise() {
    return firestore
        .collection('personal_data')
        .get()
}

function getLanguagesPromise() {
    return firestore
        .collection('languages')
        .get()
}

function getSkillsPromise() {
    return firestore.
        collection('skills')
        .get()
}

const getAll = functions.https.onRequest((request, response) => {
    var requests = []
    var jsonResult = { status: -1, welcome: { timestamp: -1 }, personal_info: { timestamp: -1 }, career: [], languages: [], skills: [] }
    requests.push(getWelcomePromise())
    requests.push(getPersonalInfoPromise())
    requests.push(getCareerProomise())
    requests.push(getLanguagesPromise())
    requests.push(getSkillsPromise())

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

            results[4].forEach((doc) => {
                var d = doc.data()
                d["id"] = doc.id
                d["timestamp"] = time
                jsonResult.skills.push(d)
            })

            jsonResult['status'] = 1
            response.status(200).json(jsonResult)

        }).catch((error) => {
            response.status(401).json(jsonResult)
        })
})

const addCareer = functions.https.onRequest((request, response) => {
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
    return firestore.collection('career').add(career)
        .then(result => {
            response.status(200).send("Success")
        }).catch(error => {
            response.status(401).send("Error")
        })
})

const addLanguage = functions.https.onRequest((request, response) => {
    var language = {
        name: "",
        description: "",
        level: 0,
        levelScale: 5,
        imageUrl: ""
    }
    return firestore.collection('languages').add(language)
        .then(result => {
            response.status(200).send("Success")
        }).catch(error => {
            response.status(401).send("Error")
        })
})

const addSkill = functions.https.onRequest((request, response) => {
    var skill = {
        skillName: "",
        skillDescription: "",
        skillCategory: "",
        iconUrl: ""
    }

    return firestore.collection('skills').add(skill)
        .then(result => {
            response.status(200).send("Success")
        }).catch(error => {
            response.status(401).send("Error")
        })
})

const notifyAboutNewCv = functions.https.onRequest((request, response) => {

    return firestore.collection('new_cv_notification').get().then((snapshot) => {

        let message = {
            data: {
                newCv: 'true',
                title: '',
                message: ''
            },
            topic: FCM_NEW_CV_TOPIC
        };

        snapshot.forEach((doc) => {
            message.data.title = doc.data().contentTitle
            message.data.message = doc.data().contentText
        })

        // Send a message to devices subscribed to the provided topic.
        return admin.messaging().send(message)

    }).then((res) => {
        response.status(200).send('Successfully sent message')
    }).catch((err) => {
        response.status(400).send('Error sending message')
    })
})

const addCredits = functions.https.onRequest((request, response) => {

    let authorParam = request.query.author
    let authorUrlParam = request.query.authorUrl

    let data = {
    }

    data['author'] = authorParam
    data['authorUrl'] = authorUrlParam

    return firestore.collection('credits').add(data)
        .then((res) => {
            response.status(200).send("Success")
        }).catch((err) => {
            response.status(400).send("Error")
        })
})

const getCredits = functions.https.onRequest((request, response) => {

    let credits = []

    return firestore.collection('credits').get()
        .then((snapshot) => {
            snapshot.forEach((doc) => {
                let credit = doc.data()
                credit['id'] = doc.id
                credits.push(credit)
            })
            response.status(200).send(credits)
        }).catch((error) => {
            response.status(400).send(credits)
        })
})

const getPersonalDataProcessing = functions.https.onRequest((request, response) => {

    return firestore.collection('personal_data_processing').get()
        .then((snapshot) => {
            snapshot.forEach( doc => {
                response.status(200).send(doc.data())
            })
        }).catch((error) => {
            response.status(400).send(null)
        })
})

function validateToken(token) {

    return new Promise((resolve, reject) => {
        admin.auth().verifyIdToken(token)
            .then(decodedIdToken => {
                console.log(decodedIdToken);
                resolve(true)
            })
            .catch(error => {
                console.log(error);
                reject(false)
            });
    });
}


// Express middleware that validates Firebase ID Tokens passed in the Authorization HTTP header.
// The Firebase ID token needs to be passed as a Bearer token in the Authorization HTTP header like this:
// `Authorization: Bearer <Firebase ID Token>`.
// when decoded successfully, the ID Token content will be added as `req.user`.
const validateFirebaseIdToken = (req, res, next) => {
    console.log('Check if request is authorized with Firebase ID token');

    if ((!req.headers.authorization || !req.headers.authorization.startsWith('Bearer ')) &&
        !(req.cookies && req.cookies.__session)) {
        console.error('No Firebase ID token was passed as a Bearer token in the Authorization header.',
            'Make sure you authorize your request by providing the following HTTP header:',
            'Authorization: Bearer <Firebase ID Token>',
            'or by passing a "__session" cookie.');
        res.status(403).send('Unauthorized');
        return;
    }

    let idToken;
    if (req.headers.authorization && req.headers.authorization.startsWith('Bearer ')) {
        console.log('Found "Authorization" header');
        // Read the ID Token from the Authorization header.
        idToken = req.headers.authorization.split('Bearer ')[1];
    } else if (req.cookies) {
        console.log('Found "__session" cookie');
        // Read the ID Token from cookie.
        idToken = req.cookies.__session;
    } else {
        // No cookie
        res.status(403).send('Unauthorized');
        return;
    }
    admin.auth().verifyIdToken(idToken).then((decodedIdToken) => {
        console.log('ID Token correctly decoded', decodedIdToken);
        req.user = decodedIdToken;
        return next();
    }).catch((error) => {
        console.error('Error while verifying Firebase ID token:', error);
        res.status(403).send('Unauthorized');
    });
};

app.use(cors);
app.use(cookieParser);
app.use(validateFirebaseIdToken);
app.use(getAll);

app1.use(cors);
app1.use(cookieParser);
app1.use(validateFirebaseIdToken);
app1.use(getCredits);

app2.use(cors);
app2.use(cookieParser);
app2.use(validateFirebaseIdToken);
app2.use(notifyAboutNewCv);

app3.use(cors);
app3.use(cookieParser);
app3.use(validateFirebaseIdToken);
app3.use(getPersonalDataProcessing);

// This HTTPS endpoint can only be accessed by your Firebase Users.
// Requests need to be authorized by providing an `Authorization` HTTP header
// with value `Bearer <Firebase ID Token>`.
exports.app = functions.https.onRequest(app);
exports.app1 = functions.https.onRequest(app1);
exports.app2 = functions.https.onRequest(app2);
exports.app3 = functions.https.onRequest(app3)