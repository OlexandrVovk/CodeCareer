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

globalThis.handleGoogleLogin = handleGoogleLogin;
globalThis.signOut = signOut;