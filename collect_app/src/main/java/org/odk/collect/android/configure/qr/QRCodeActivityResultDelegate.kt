package org.odk.collect.android.configure.qr

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import org.odk.collect.analytics.Analytics.Companion.log
import org.odk.collect.android.R
import org.odk.collect.android.activities.ActivityUtils
import org.odk.collect.android.activities.MainMenuActivity
import org.odk.collect.android.analytics.AnalyticsEvents
import org.odk.collect.projects.Project.Saved
import org.odk.collect.qrcode.QRCodeDecoder
import org.odk.collect.settings.ODKAppSettingsImporter
import java.io.FileNotFoundException
import java.io.InputStream

class QRCodeActivityResultDelegate(
    private val activity: Activity,
    private val settingsImporter: ODKAppSettingsImporter,
    private val qrCodeDecoder: QRCodeDecoder,
    private val project: Saved
) {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == QRCodeMenuDelegate.SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                val imageStream: InputStream? = try {
                    activity.contentResolver.openInputStream(imageUri)
                } catch (e: FileNotFoundException) {
                    // Not sure how this could happen? If you work it out: write a test!
                    return
                }
                try {
                    val response = qrCodeDecoder.decode(imageStream)
                    if (settingsImporter.fromJSON(response, project)) {
                        log(AnalyticsEvents.RECONFIGURE_PROJECT)
                        showToast(R.string.successfully_imported_settings)
                        ActivityUtils.startActivityAndCloseAllOthers(
                            activity,
                            MainMenuActivity::class.java
                        )
                    } else {
                        showToast(R.string.invalid_qrcode)
                    }
                } catch (e: QRCodeDecoder.QRCodeInvalidException) {
                    showToast(R.string.invalid_qrcode)
                } catch (e: QRCodeDecoder.QRCodeNotFoundException) {
                    showToast(R.string.qr_code_not_found)
                }
            }
        }
    }

    private fun showToast(string: Int) {
        Toast.makeText(activity, activity.getString(string), Toast.LENGTH_LONG).show()
    }
}
