// Firebase app instance
let firebaseApp;
let auth;
let initialized = false;

// Initialize Firebase if needed
function ensureInitialized() {
    if (initialized) return Promise.resolve();

    // Initialize Firebase with your config
    return Promise.all([
        import('https://www.gstatic.com/firebasejs/11.6.0/firebase-app.js'),
        import('https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js')
    ]).then(([firebaseAppModule, firebaseAuthModule]) => {
        const firebaseConfig = {
            apiKey: "AIzaSyC7QpaA-wvFg2hyGGu9bTLXm8ibUuttrec",
            authDomain: "codecareer-3bb27.firebaseapp.com",
            projectId: "codecareer-3bb27",
            storageBucket: "codecareer-3bb27.firebasestorage.app",
            messagingSenderId: "543985129145",
            appId: "1:543985129145:web:23348d8d01b9abdec92d68",
            measurementId: "G-6Q5SSGNKBR"
        };

        // Initialize Firebase modules
        firebaseApp = firebaseAppModule.initializeApp(firebaseConfig);
        auth = firebaseAuthModule.getAuth(firebaseApp);
        initialized = true;

        // Set up auth state listener
        firebaseAuthModule.onAuthStateChanged(auth, (user) => {
            if (user) {
                // Dispatch event when user signs in
                const userData = {
                    uid: user.uid,
                    displayName: user.displayName,
                    email: user.email,
                    photoURL: user.photoURL
                };

                // Dispatch auth state event
                window.dispatchEvent(new CustomEvent('authStateChanged', {
                    detail: JSON.stringify(userData)
                }));
            } else {
                // Dispatch event when user signs out
                window.dispatchEvent(new CustomEvent('authStateChanged', {
                    detail: JSON.stringify({ signedOut: true })
                }));
            }
        });
    });
}
/**
 * Send a password reset email via Firebase Auth
 */
function sendPasswordResetEmail(email, kotlinCallback) {
    ensureInitialized()
        .then(() => import('https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js'))
        .then(firebaseAuthModule => {
            return firebaseAuthModule.sendPasswordResetEmail(auth, email);
        })
        .then(() => {
            kotlinCallback(JSON.stringify({ success: true }));
        })
        .catch(error => {
            console.error("Password reset error:", error);
            kotlinCallback(JSON.stringify({
                success: false,
                code: error.code,
                message: getErrorMessage(error.code)
            }));
        });
}

function handleGoogleLogin(kotlinCallback) {
    ensureInitialized()
        .then(() => import('https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js'))
        .then((firebaseAuthModule) => {
            const provider = new firebaseAuthModule.GoogleAuthProvider();

            // Set custom parameters
            provider.setCustomParameters({
                prompt: 'select_account'
            });

            // Start login process
            return firebaseAuthModule.signInWithPopup(auth, provider);
        })
        .then((result) => {
            // Get user data
            const user = result.user;
            const userJson = JSON.stringify({
                success: true,
                uid: user.uid,
                displayName: user.displayName,
                email: user.email,
                photoURL: user.photoURL
            })
            kotlinCallback(userJson);
        })
        .catch((error) => {
            console.error("Google auth error:", error);

            // Dispatch login error event
            window.dispatchEvent(new CustomEvent('googleLoginResult', {
                detail: JSON.stringify({
                    success: false,
                    error: true,
                    code: error.code,
                    message: error.message
                })
            }));
        });
}

function createUserWithEmail(email, password, displayName, callback) {
    ensureInitialized()
        .then(() => import('https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js'))
        .then((firebaseAuthModule) => {
            // Create the user with email and password
            return firebaseAuthModule.createUserWithEmailAndPassword(auth, email, password)
                .then((userCredential) => {
                    // Update user profile with display name
                    const user = userCredential.user;
                    return firebaseAuthModule.updateProfile(user, {
                        displayName: displayName
                    }).then(() => user);
                });
        })
        .then((user) => {
            // User created and profile updated successfully
            const userJson = JSON.stringify({
                success: true,
                uid: user.uid,
                displayName: user.displayName || displayName, // Fallback to provided displayName
                email: user.email,
                photoURL: user.photoURL
            });
            callback(userJson);
        })
        .catch((error) => {
            console.error("Email registration error:", error);
            // Handle registration errors
            const errorJson = JSON.stringify({
                success: false,
                error: true,
                code: error.code,
                message: getErrorMessage(error.code)
            });
            callback(errorJson);
        });
}

function signInWithEmail(email, password, callback) {
    ensureInitialized()
        .then(() => import('https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js'))
        .then((firebaseAuthModule) => {
            // Sign in with email and password
            return firebaseAuthModule.signInWithEmailAndPassword(auth, email, password);
        })
        .then((userCredential) => {
            // User signed in successfully
            const user = userCredential.user;
            const userJson = JSON.stringify({
                success: true,
                uid: user.uid,
                displayName: user.displayName,
                email: user.email,
                photoURL: user.photoURL
            });
            callback(userJson);
        })
        .catch((error) => {
            console.error("Email sign-in error:", error);
            // Handle sign-in errors
            const errorJson = JSON.stringify({
                success: false,
                error: true,
                code: error.code,
                message: getErrorMessage(error.code)
            });
            callback(errorJson);
        });
}

function signOut() {
    ensureInitialized().then(() => {
        import('https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js')
            .then((firebaseAuthModule) => {
                return firebaseAuthModule.signOut(auth);
            })
            .then(() => {
                console.log("User signed out successfully");
            })
            .catch((error) => {
                console.error("Sign out error:", error);
            });
    })
}

function getErrorMessage(errorCode) {
    switch (errorCode) {
        case 'auth/email-already-in-use':
            return 'This email is already registered. Please sign in or use a different email.';
        case 'auth/invalid-email':
            return 'The email address is not valid.';
        case 'auth/operation-not-allowed':
            return 'Email/password accounts are not enabled for this app.';
        case 'auth/weak-password':
            return 'The password is too weak. Please choose a stronger password.';
        case 'auth/user-disabled':
            return 'This account has been disabled. Please contact support.';
        case 'auth/user-not-found':
            return 'No account found with this email. Please check your email or register.';
        case 'auth/wrong-password':
            return 'Incorrect password. Please try again or reset your password.';
        case 'auth/too-many-requests':
            return 'Too many unsuccessful login attempts. Please try again later.';
        case 'auth/network-request-failed':
            return 'Network error. Please check your internet connection and try again.';
        default:
            return `Authentication error: ${errorCode}`;
    }
}

function signInFromSession(uid, displayName, email, callback) {
    ensureInitialized()
        .then(() => import('https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js'))
        .then((firebaseAuthModule) => {
            // Check if a user is already signed in
            const currentUser = auth.currentUser;

            if (currentUser) {
                console.log("User already signed in:", currentUser.email);
                // User is already signed in - just return their info
                const userJson = JSON.stringify({
                    success: true,
                    uid: currentUser.uid,
                    displayName: currentUser.displayName,
                    email: currentUser.email,
                    photoURL: currentUser.photoURL
                });
                callback(userJson);
                return;
            }

            console.log("No user currently signed in, attempting to restore session");

            // Since we can't sign in anonymously and we don't have the password,
            // we'll use the persistent session that Firebase maintains

            // Set persistence to LOCAL (browser persistence)
            firebaseAuthModule.setPersistence(auth, firebaseAuthModule.browserLocalPersistence)
                .then(() => {
                    console.log("Persistence set to LOCAL");

                    // Instead of trying to sign in again, we'll check if Firebase
                    // can restore the auth state on its own

                    // Add a one-time auth state change listener
                    const unsubscribe = firebaseAuthModule.onAuthStateChanged(auth, (user) => {
                        unsubscribe(); // Unsubscribe immediately after first callback

                        if (user) {
                            console.log("Firebase restored auth state for:", user.email);
                            // Firebase restored the auth state
                            const userJson = JSON.stringify({
                                success: true,
                                uid: user.uid,
                                displayName: user.displayName,
                                email: user.email,
                                photoURL: user.photoURL
                            });
                            callback(userJson);
                        } else {
                            console.log("Firebase could not restore auth state");
                            // Firebase couldn't restore auth state
                            // Return session data with a warning
                            const userJson = JSON.stringify({
                                success: true,
                                uid: uid,
                                displayName: displayName,
                                email: email,
                                photoURL: null,
                                warning: "Authentication state could not be fully restored. Some features might be limited until you log in again."
                            });
                            callback(userJson);
                        }
                    });

                    // Set a timeout in case the auth state change doesn't fire
                    setTimeout(() => {
                        console.log("Auth state change timeout - returning session data");
                        const userJson = JSON.stringify({
                            success: true,
                            uid: uid,
                            displayName: displayName,
                            email: email,
                            photoURL: null,
                            warning: "Session restoration timed out. Please log in again for full functionality."
                        });
                        callback(userJson);
                    }, 3000); // 3 second timeout
                })
                .catch((error) => {
                    console.error("Error setting persistence:", error);
                    // Return session data with a warning
                    const userJson = JSON.stringify({
                        success: true,
                        uid: uid,
                        displayName: displayName,
                        email: email,
                        photoURL: null,
                        warning: "There was an error restoring your session. Please log in again."
                    });
                    callback(userJson);
                });
        })
        .catch((error) => {
            console.error("Error initializing Firebase for session sign-in:", error);
            const errorJson = JSON.stringify({
                success: false,
                error: true,
                message: "Failed to initialize Firebase for session sign-in"
            });
            callback(errorJson);
        });
}

function addNewVacancyTrack(companyName, companyImageUrl, jobName, jobDescription, jobUrl, callback) {
    // Ensure Firebase is initialized
    ensureInitialized()
        .then(() => {
            // Import Firestore
            return import('https://www.gstatic.com/firebasejs/11.6.0/firebase-firestore.js');
        })
        .then((firestoreModule) => {
            // Get the current user
            const user = auth.currentUser;

            if (!user) {
                callback(JSON.stringify({
                    success: false,
                    error: true,
                    message: "User not authenticated"
                }));
                return;
            }

            console.log("Current user:", user.email);

            // Initialize Firestore
            const firestore = firestoreModule.getFirestore(firebaseApp);

            // Encode the job URL to make it safe for use as a document ID
            // This replaces characters that aren't allowed in Firestore document IDs
            const encodedJobUrl = encodeURIComponent(jobUrl).replace(/\./g, '%2E');

            // Create a reference to the vacancy document
            // Use email as the user identifier in the path
            const vacancyRef = firestoreModule.doc(
                firestore,
                `users/${user.email}/tracked_vacancies/${encodedJobUrl}`
            );

            console.log(`Attempting to write to: users/${user.email}/tracked_vacancies/${encodedJobUrl}`);

            // Create the vacancy data
            const vacancyData = {
                companyName: companyName,
                companyImageUrl: companyImageUrl,
                jobName: jobName,
                jobDescription: jobDescription,
                jobUrl: jobUrl,
                trackingStatus: "Interested", // Default status
                notes: "",                    // Empty notes by default
                dateAdded: firestoreModule.serverTimestamp(),
                lastUpdated: firestoreModule.serverTimestamp()
            };

            // Add the vacancy to Firestore
            firestoreModule.setDoc(vacancyRef, vacancyData)
                .then(() => {
                    // Success
                    console.log("Vacancy tracked successfully");
                    callback(JSON.stringify({
                        success: true,
                        message: "Vacancy tracked successfully"
                    }));
                })
                .catch((error) => {
                    // Error
                    console.error("Error tracking vacancy:", error);
                    callback(JSON.stringify({
                        success: false,
                        error: true,
                        code: error.code,
                        message: error.message || "Failed to track vacancy"
                    }));
                });
        })
        .catch((error) => {
            // Error importing Firestore
            console.error("Error importing Firestore:", error);
            callback(JSON.stringify({
                success: false,
                error: true,
                message: "Failed to initialize Firestore"
            }));
        });
}

function getTrackedVacancies(callback) {
    // Ensure Firebase is initialized
    ensureInitialized()
        .then(() => {
            // Import Firestore
            return import('https://www.gstatic.com/firebasejs/11.6.0/firebase-firestore.js');
        })
        .then((firestoreModule) => {
            // Get the current user
            const user = auth.currentUser;

            if (!user) {
                callback(JSON.stringify({
                    success: false,
                    error: true,
                    message: "User not authenticated"
                }));
                return;
            }

            console.log("Getting tracked vacancies for user:", user.email);

            // Initialize Firestore
            const firestore = firestoreModule.getFirestore(firebaseApp);

            // Create a reference to the user's tracked vacancies collection
            const vacanciesRef = firestoreModule.collection(
                firestore,
                `users/${user.email}/tracked_vacancies`
            );

            // Get all documents in the collection
            firestoreModule.getDocs(vacanciesRef)
                .then((querySnapshot) => {
                    const vacancies = [];

                    querySnapshot.forEach((doc) => {
                        const data = doc.data();

                        // Map Firestore data to our vacancy object structure
                        vacancies.push({
                            jobInfo: {
                                companyName: data.companyName,
                                companyImageUrl: data.companyImageUrl,
                                jobName: data.jobName,
                                jobDescription: data.jobDescription,
                                jobUrl: data.jobUrl
                            },
                            status: data.trackingStatus || "INTERESTED",
                            notes: data.notes || "",
                            // Support multiple interview schedules
                            interviewSchedules: data.interviewSchedules || (data.interviewSchedule ? [data.interviewSchedule] : [])
                        });
                    });

                    console.log(`Retrieved ${vacancies.length} tracked vacancies`);

                    // Return the vacancies as JSON string
                    callback(JSON.stringify({
                        success: true,
                        vacancies: vacancies
                    }));
                })
                .catch((error) => {
                    console.error("Error getting tracked vacancies:", error);
                    callback(JSON.stringify({
                        success: false,
                        error: true,
                        code: error.code,
                        message: error.message || "Failed to retrieve tracked vacancies"
                    }));
                });
        })
        .catch((error) => {
            console.error("Error importing Firestore:", error);
            callback(JSON.stringify({
                success: false,
                error: true,
                message: "Failed to initialize Firestore"
            }));
        });
}

function updateTrackedVacancy(jobUrl, status, notes, callback) {
    ensureInitialized()
        .then(() => {
            return import('https://www.gstatic.com/firebasejs/11.6.0/firebase-firestore.js');
        })
        .then((firestoreModule) => {
            const user = auth.currentUser;

            if (!user) {
                console.error("User not authenticated");
                callback(false);
                return;
            }

            console.log(`Updating vacancy for user: ${user.email}, job URL: ${jobUrl}`);

            // Initialize Firestore
            const firestore = firestoreModule.getFirestore(firebaseApp);

            // Encode the job URL to make it safe for use as a document ID
            // This replaces characters that aren't allowed in Firestore document IDs
            const encodedJobUrl = encodeURIComponent(jobUrl).replace(/\./g, '%2E');

            // Create a reference to the vacancy document
            const vacancyRef = firestoreModule.doc(
                firestore,
                `users/${user.email}/tracked_vacancies/${encodedJobUrl}`
            );

            // Update data
            const updateData = {
                trackingStatus: status,
                notes: notes,
                lastUpdated: firestoreModule.serverTimestamp()
            };

            // Update the document in Firestore
            firestoreModule.updateDoc(vacancyRef, updateData)
                .then(() => {
                    console.log("Vacancy updated successfully");
                    callback(true);
                })
                .catch((error) => {
                    console.error("Error updating vacancy:", error);
                    callback(false);
                });
        })
        .catch((error) => {
            console.error("Error importing Firestore:", error);
            callback(false);
        });
}

function deleteTrackedVacancy(jobUrl, callback) {
    // Ensure Firebase is initialized
    ensureInitialized()
        .then(() => {
            // Import Firestore
            return import('https://www.gstatic.com/firebasejs/11.6.0/firebase-firestore.js');
        })
        .then((firestoreModule) => {
            // Get the current user
            const user = auth.currentUser;

            if (!user) {
                console.error("User not authenticated");
                callback(false);
                return;
            }

            console.log(`Deleting vacancy for user: ${user.email}, job URL: ${jobUrl}`);

            // Initialize Firestore
            const firestore = firestoreModule.getFirestore(firebaseApp);

            // Encode the job URL to make it safe for use as a document ID
            // This replaces characters that aren't allowed in Firestore document IDs
            const encodedJobUrl = encodeURIComponent(jobUrl).replace(/\./g, '%2E');

            // Create a reference to the vacancy document
            const vacancyRef = firestoreModule.doc(
                firestore,
                `users/${user.email}/tracked_vacancies/${encodedJobUrl}`
            );

            // Delete the document from Firestore
            firestoreModule.deleteDoc(vacancyRef)
                .then(() => {
                    console.log("Vacancy deleted successfully");
                    callback(true);
                })
                .catch((error) => {
                    console.error("Error deleting vacancy:", error);
                    callback(false);
                });
        })
        .catch((error) => {
            console.error("Error importing Firestore:", error);
            callback(false);
        });
}

function scheduleInterview(jobUrl, dateAndTime, type, notes, callback) {
    // Ensure Firebase is initialized
    ensureInitialized()
        .then(() => {
            // Import Firestore
            return import('https://www.gstatic.com/firebasejs/11.6.0/firebase-firestore.js');
        })
        .then((firestoreModule) => {
            // Get the current user
            const user = auth.currentUser;

            if (!user) {
                console.error("User not authenticated");
                callback(false);
                return;
            }

            // Initialize Firestore
            const firestore = firestoreModule.getFirestore(firebaseApp);
            const encodedJobUrl = encodeURIComponent(jobUrl).replace(/\./g, '%2E');
            const vacancyRef = firestoreModule.doc(
                firestore,
                `users/${user.email}/tracked_vacancies/${encodedJobUrl}`
            );

            // Parse date and time
            const [date, time] = dateAndTime.split('_');

            // Create interview schedule object
            const interviewSchedule = {
                date: date,
                time: time,
                type: type,
                notes: notes
            };

            // Update data by adding interview schedule to the schedules array
            const updateData = {
                interviewSchedules: firestoreModule.arrayUnion(interviewSchedule),
                lastUpdated: firestoreModule.serverTimestamp()
            };

            // Update the document in Firestore
            firestoreModule.updateDoc(vacancyRef, updateData)
                .then(() => {
                    console.log("Interview scheduled successfully");
                    callback(true);
                })
                .catch((error) => {
                    console.error("Error scheduling interview:", error);
                    callback(false);
                });
        })
        .catch((error) => {
            console.error("Error importing Firestore:", error);
            callback(false);
        });
}

function deleteMeeting(jobUrl, dateAndTime, callback) {
    // Ensure Firebase is initialized
    ensureInitialized()
        .then(() => {
            // Import Firestore
            return import('https://www.gstatic.com/firebasejs/11.6.0/firebase-firestore.js');
        })
        .then((firestoreModule) => {
            // Get the current user
            const user = auth.currentUser;

            if (!user) {
                console.error("User not authenticated");
                callback(false);
                return;
            }

            // Initialize Firestore
            const firestore = firestoreModule.getFirestore(firebaseApp);
            const encodedJobUrl = encodeURIComponent(jobUrl).replace(/\./g, '%2E');
            const vacancyRef = firestoreModule.doc(
                firestore,
                `users/${user.email}/tracked_vacancies/${encodedJobUrl}`
            );
            // Delete a specific meeting from the schedules array
            const [date, time] = dateAndTime.split('_');
            // Read the current document
            firestoreModule.getDoc(vacancyRef)
                .then((docSnap) => {
                    if (!docSnap.exists()) {
                        callback(false);
                        return;
                    }
                    const data = docSnap.data();
                    // Retrieve existing schedules array or fallback for backward compatibility
                    const schedules = data.interviewSchedules || (data.interviewSchedule ? [data.interviewSchedule] : []);
                    // Determine filtered schedules: remove all matching date (and time if provided)
                    let filteredSchedules;
                    if (time === undefined) {
                        // Remove all schedules on this date
                        filteredSchedules = schedules.filter(s => s.date !== date);
                    } else {
                        // Remove only the specific date/time
                        filteredSchedules = schedules.filter(s => s.date !== date || s.time !== time);
                    }
                    // Update the document with the filtered schedules list
                    return firestoreModule.updateDoc(vacancyRef, {
                        interviewSchedules: filteredSchedules,
                        lastUpdated: firestoreModule.serverTimestamp()
                    });
                })
                .then(() => {
                    console.log("Meeting deleted successfully");
                    callback(true);
                })
                .catch((error) => {
                    console.error("Error deleting meeting:", error);
                    callback(false);
                });

        })
        .catch((error) => {
            console.error("Error importing Firestore:", error);
            callback(false);
        });
}


globalThis.handleGoogleLogin = handleGoogleLogin;
globalThis.signOut = signOut;
globalThis.createUserWithEmail = createUserWithEmail;
globalThis.signInWithEmail = signInWithEmail;
globalThis.addNewVacancyTrack = addNewVacancyTrack;
globalThis.signInFromSession = signInFromSession;
globalThis.getTrackedVacancies = getTrackedVacancies;
globalThis.updateTrackedVacancy = updateTrackedVacancy;
globalThis.deleteTrackedVacancy = deleteTrackedVacancy;
globalThis.scheduleInterview = scheduleInterview;
globalThis.deleteMeeting = deleteMeeting;
globalThis.sendPasswordResetEmail = sendPasswordResetEmail;
