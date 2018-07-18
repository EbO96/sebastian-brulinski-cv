const functions = require('firebase-functions');
const admin = require('firebase-admin');
var serviceAccount = require("./sebastian-brulinski-cv-app-firebase-adminsdk-b1ddn-608e079791");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://sebastian-brulinski-cv-app.firebaseio.com"
});

var db = admin.firestore();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
// exports.getWelcome = functions.https.onRequest((request, response) => {
//     return db
//         .collection('welcome')
//         .get()
//         .then((snapshot) => {
//             var d;
//             snapshot.forEach((doc) => {
//                 d = doc.data()
//             });
//             response.status(200).json(d)
//         })
//         .catch((err) => {
//             response.status(401).json(err)
//         })
// });

// exports.getPersonalInfo = functions.https.onRequest((request, response) => {
//     return db
//         .collection('personal_data')
//         .get()
//         .then((snapshot) => {
//             snapshot.forEach((doc) => {
//                 response.status(200).json(doc.data())
//             })
//         }).catch((err) => {
//             response.status(401).json(err)
//         })
// })

// exports.getSchool = functions.https.onRequest((request, response) => {
//     var resultJson = []
//     return db
//         .collection('career')
//         .doc('school')
//         .collection('schools')
//         .get()
//         .then((snapshot) => {
//             snapshot.forEach((doc) => {
//                 resultJson.push(doc.data())
//             })
//             response.status(200).json(resultJson)
//         }).catch((err) => {
//             response.status(200).json(resultJson)
//         })
// })