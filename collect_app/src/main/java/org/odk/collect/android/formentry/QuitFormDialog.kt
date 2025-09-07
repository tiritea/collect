package org.odk.collect.android.formentry

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.odk.collect.android.R
import org.odk.collect.android.databinding.QuitFormDialogLayoutBinding
import org.odk.collect.android.formentry.saving.FormSaveViewModel
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.settings.keys.ProtectedProjectKeys
import java.text.SimpleDateFormat
import java.util.Locale

object QuitFormDialog {

    @JvmStatic
    fun show(
        activity: Activity,
        formSaveViewModel: FormSaveViewModel,
        formEntryViewModel: FormEntryViewModel,
        settingsProvider: SettingsProvider,
        onSaveChangesClicked: Runnable?
    ): AlertDialog {
        return create(
            activity,
            formSaveViewModel,
            formEntryViewModel,
            settingsProvider,
            onSaveChangesClicked
        ).also {
            it.show()
        }
    }

    private fun create(
        activity: Activity,
        formSaveViewModel: FormSaveViewModel,
        formEntryViewModel: FormEntryViewModel,
        settingsProvider: SettingsProvider,
        onSaveChangesClicked: Runnable?
    ): AlertDialog {
        val saveAsDraft = settingsProvider.getProtectedSettings()
            .getBoolean(ProtectedProjectKeys.KEY_SAVE_MID)
        val canBeFullyDiscarded = formSaveViewModel.canBeFullyDiscarded()

        val binding = QuitFormDialogLayoutBinding.inflate(activity.layoutInflater)
        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(
                if (saveAsDraft) {
                    org.odk.collect.strings.R.string.quit_form_title
                } else {
                    org.odk.collect.strings.R.string.quit_form_continue_title
                }
            )
            .setView(binding.root)
            .create()

        binding.saveExplanation.text = if (!saveAsDraft) {
            if (canBeFullyDiscarded) {
                activity.getString(org.odk.collect.strings.R.string.discard_form_warning)
            } else {
                val string = activity.getString(org.odk.collect.strings.R.string.discard_changes_warning)
                SimpleDateFormat(string, Locale.getDefault()).format(formSaveViewModel.lastSavedTime)
            }
        } else if (canBeFullyDiscarded) {
            activity.getString(org.odk.collect.strings.R.string.save_explanation)
        } else {
            val string = activity.getString(org.odk.collect.strings.R.string.save_explanation_with_last_saved)
            SimpleDateFormat(string, Locale.getDefault()).format(formSaveViewModel.lastSavedTime)
        }

        binding.discardChanges.setText(
            if (canBeFullyDiscarded) {
                org.odk.collect.strings.R.string.do_not_save
            } else {
                org.odk.collect.strings.R.string.discard_changes
            }
        )

        binding.discardChanges.setOnClickListener {
            formSaveViewModel.ignoreChanges()
            formEntryViewModel.exit()
            activity.finish()
            dialog.dismiss()
        }

        binding.keepEditingOutlined.isVisible = saveAsDraft
        binding.keepEditingFilled.isVisible = !saveAsDraft

        binding.keepEditingOutlined.setOnClickListener {
            dialog.dismiss()
        }

        binding.keepEditingFilled.setOnClickListener {
            dialog.dismiss()
        }

        binding.saveChanges.isVisible = saveAsDraft
        binding.saveChanges.setOnClickListener {
            onSaveChangesClicked?.run()
        }

        return dialog
    }
}
