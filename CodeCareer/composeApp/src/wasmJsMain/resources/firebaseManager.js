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
            // // Dispatch login success event
            // window.dispatchEvent(new CustomEvent('googleLoginResult', {
            //     detail: JSON.stringify({
            //         success: true,
            //         uid: user.uid,
            //         displayName: user.displayName,
            //         email: user.email,
            //         photoURL: user.photoURL
            //     })
            // }));
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

globalThis.handleGoogleLogin = handleGoogleLogin;
globalThis.signOut = signOut;
globalThis.createUserWithEmail = createUserWithEmail;
globalThis.signInWithEmail = signInWithEmail;