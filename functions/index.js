const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.createNewUser = functions.https.onCall(async (data, context) => {
  // Check if the request is made by an authenticated user
  if (!context.auth) {
    throw new functions.https.HttpsError(
        "unauthenticated",
        "The function must be called while authenticated.",
    );
  }

  // Check if the calling user is an admin
  const callerUid = context.auth.uid;
  const callerUserRecord = await admin.firestore().collection("users").doc(callerUid).get();
  if (callerUserRecord.data()?.role !== "admin") {
    throw new functions.https.HttpsError(
        "permission-denied",
        "Only administrators can create new users.",
    );
  }

  const {email, password, name, role} = data;

  if (!email || !password || !name || !role) {
    throw new functions.https.HttpsError(
        "invalid-argument",
        "The function must be called with all required arguments.",
    );
  }

  try {
    // Create user in Firebase Authentication
    const userRecord = await admin.auth().createUser({
      email: email,
      password: password,
      displayName: name,
    });

    // Create user document in Firestore
    await admin.firestore().collection("users").doc(userRecord.uid).set({
      uid: userRecord.uid,
      name: name,
      email: email,
      role: role, // 'admin' or 'employee'
      active: true,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return {
      result: `Successfully created user ${name} (${email}) with role ${role}.`,
      uid: userRecord.uid,
    };
  } catch (error) {
    // Log the error and rethrow it as an HttpsError
    console.error("Error creating new user:", error);
    throw new functions.https.HttpsError(
        "internal",
        "An error occurred while creating the user.",
        error.message,
    );
  }
});
