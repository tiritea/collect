package org.odk.collect.android.instancemanagement

import android.content.Context
import android.content.res.Resources
import org.odk.collect.android.application.Collect
import org.odk.collect.forms.instances.Instance
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.settings.keys.ProtectedProjectKeys
import org.odk.collect.strings.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Instance.canBeEdited(settingsProvider: SettingsProvider): Boolean {
    return isDraft() &&
        settingsProvider.getProtectedSettings().getBoolean(ProtectedProjectKeys.KEY_EDIT_SAVED)
}

fun Instance.getStatusDescription(resources: Resources): String {
    return getStatusDescription(resources, status, Date(lastStatusChangeDate))
}

fun getStatusDescription(context: Context, state: String?, date: Date): String {
    return getStatusDescription(context.resources, state, date)
}

fun Instance.isDraft(): Boolean {
    return draftStatuses.contains(status)
}

fun Instance.isEdit(): Boolean {
    return editOf != null
}

fun Instance.isDeletable(): Boolean {
    return canDeleteBeforeSend() || status != Instance.STATUS_COMPLETE && status != Instance.STATUS_SUBMISSION_FAILED
}

fun Instance.showAsEditable(settingsProvider: SettingsProvider): Boolean {
    return isDraft() && settingsProvider.getProtectedSettings()
        .getBoolean(ProtectedProjectKeys.KEY_EDIT_SAVED)
}

fun Instance.userVisibleInstanceName(
    resources: Resources = Collect.getInstance().resources
): String {
    return if (isEdit()) {
        resources.getString(R.string.user_visible_instance_name, displayName, editNumber)
    } else {
        displayName
    }
}

private fun getStatusDescription(resources: Resources, state: String?, date: Date): String {
    return try {
        if (Instance.STATUS_INCOMPLETE.equals(state, ignoreCase = true) ||
            Instance.STATUS_INVALID.equals(state, ignoreCase = true) ||
            Instance.STATUS_VALID.equals(state, ignoreCase = true) ||
            Instance.STATUS_NEW_EDIT.equals(state, ignoreCase = true)
        ) {
            SimpleDateFormat(
                resources.getString(R.string.saved_on_date_at_time),
                Locale.getDefault()
            ).format(date)
        } else if (Instance.STATUS_COMPLETE.equals(state, ignoreCase = true)) {
            SimpleDateFormat(
                resources.getString(R.string.finalized_on_date_at_time),
                Locale.getDefault()
            ).format(date)
        } else if (Instance.STATUS_SUBMITTED.equals(state, ignoreCase = true)) {
            SimpleDateFormat(
                resources.getString(R.string.sent_on_date_at_time),
                Locale.getDefault()
            ).format(date)
        } else if (Instance.STATUS_SUBMISSION_FAILED.equals(state, ignoreCase = true)) {
            SimpleDateFormat(
                resources.getString(R.string.sending_failed_on_date_at_time),
                Locale.getDefault()
            ).format(date)
        } else {
            SimpleDateFormat(
                resources.getString(R.string.added_on_date_at_time),
                Locale.getDefault()
            ).format(date)
        }
    } catch (e: IllegalArgumentException) {
        Timber.e(e, "Current locale: %s", Locale.getDefault())
        ""
    }
}

fun Instance.getIcon(): Int {
    return getInstanceIcon(this.status)
}

fun getInstanceIcon(status: String): Int {
    return when (status) {
        Instance.STATUS_INCOMPLETE, Instance.STATUS_INVALID, Instance.STATUS_VALID, Instance.STATUS_NEW_EDIT -> org.odk.collect.android.R.drawable.ic_form_state_saved
        Instance.STATUS_COMPLETE -> org.odk.collect.android.R.drawable.ic_form_state_finalized
        Instance.STATUS_SUBMITTED -> org.odk.collect.android.R.drawable.ic_form_state_submitted
        Instance.STATUS_SUBMISSION_FAILED -> org.odk.collect.android.R.drawable.ic_form_state_submission_failed
        else -> throw IllegalArgumentException()
    }
}

private val draftStatuses = arrayOf(
    Instance.STATUS_INCOMPLETE,
    Instance.STATUS_INVALID,
    Instance.STATUS_VALID,
    Instance.STATUS_NEW_EDIT
)
