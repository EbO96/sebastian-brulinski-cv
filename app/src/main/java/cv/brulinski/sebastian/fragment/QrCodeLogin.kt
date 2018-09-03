package cv.brulinski.sebastian.fragment

/**
 * Interface between QrCodeLoginFragment and host activity
 * @see QrCodeLoginFragment
 * @see cv.brulinski.sebastian.activity.SplashActivity
 */
interface QrCodeLogin {

    /**
     * Sign in by QR code successful
     */
    fun qrCodeSignedIn()

    fun requestForCameraPermissions()
}