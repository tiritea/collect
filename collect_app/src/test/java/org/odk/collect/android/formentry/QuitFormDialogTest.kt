package org.odk.collect.android.formentry

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.odk.collect.android.R
import org.odk.collect.android.formentry.saving.FormSaveViewModel
import org.odk.collect.settings.InMemSettingsProvider
import org.odk.collect.settings.keys.ProtectedProjectKeys.KEY_SAVE_MID
import org.odk.collect.shadows.ShadowAndroidXAlertDialog
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow.extract
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
@Config(shadows = [ShadowAndroidXAlertDialog::class])
class QuitFormDialogTest {

    private val formSaveViewModel = mock<FormSaveViewModel>()
    private val formEntryViewModel = mock<FormEntryViewModel>()
    private val settingsProvider = InMemSettingsProvider()

    @Test
    fun isCancellable() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        assertThat(shadowOf(dialog).isCancelable, equalTo(true))
    }

    @Test
    fun clickingDiscardChanges_callsExitOnFormEntryViewModel() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)
        val view = shadowDialog.getView()
        view.findViewById<View>(R.id.discard_changes).performClick()

        verify(formEntryViewModel).exit()
    }

    @Test
    fun clickingOutlinedKeepEditing_dismissesDialog() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)
        val view = shadowDialog.getView()
        view.findViewById<View>(R.id.keep_editing_outlined).performClick()

        assertThat(dialog.isShowing, equalTo(false))
    }

    @Test
    fun clickingFilledKeepEditing_dismissesDialog() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)
        val view = shadowDialog.getView()
        view.findViewById<View>(R.id.keep_editing_filled).performClick()

        assertThat(dialog.isShowing, equalTo(false))
    }

    @Test
    fun whenSaveAsDraftIsEnabled_andFormCanBeFullyDiscarded_showsSaveExplanation() {
        settingsProvider.getProtectedSettings().save(KEY_SAVE_MID, true)
        whenever(formSaveViewModel.canBeFullyDiscarded()).doReturn(true)
        whenever(formSaveViewModel.lastSavedTime).doReturn(null)

        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)
        assertThat(
            shadowDialog.getView().findViewById<TextView>(R.id.save_explanation).text,
            equalTo(activity.getString(org.odk.collect.strings.R.string.save_explanation))
        )
    }

    @Test
    fun whenSaveAsDraftIsEnabled_andFormCanNotBeFullyDiscarded_showsLastSavedTime() {
        settingsProvider.getProtectedSettings().save(KEY_SAVE_MID, true)
        whenever(formSaveViewModel.canBeFullyDiscarded()).doReturn(false)
        whenever(formSaveViewModel.lastSavedTime).doReturn(456L)

        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)

        assertThat(
            shadowDialog.getView().findViewById<TextView>(R.id.save_explanation).text,
            equalTo(
                SimpleDateFormat(
                    activity.getString(org.odk.collect.strings.R.string.save_explanation_with_last_saved),
                    Locale.getDefault()
                ).format(456L)
            )
        )
    }

    @Test
    fun whenSaveAsDraftIsEnabled_showsOutlinedKeepEditing_andHidesFilledKeepEditing() {
        settingsProvider.getProtectedSettings().save(KEY_SAVE_MID, true)
        whenever(formSaveViewModel.canBeFullyDiscarded()).doReturn(true)
        whenever(formSaveViewModel.lastSavedTime).doReturn(null)

        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)

        assertThat(
            shadowDialog.getView().findViewById<View>(R.id.keep_editing_outlined).visibility,
            equalTo(View.VISIBLE)
        )
        assertThat(
            shadowDialog.getView().findViewById<View>(R.id.keep_editing_filled).visibility,
            equalTo(View.GONE)
        )
    }

    @Test
    fun whenSaveAsDraftIsDisabled_andFormCanBeFullyDiscarded_showsWarningTitleAndMessage_andHidesButton() {
        settingsProvider.getProtectedSettings().save(KEY_SAVE_MID, false)
        whenever(formSaveViewModel.canBeFullyDiscarded()).doReturn(true)
        whenever(formSaveViewModel.lastSavedTime).doReturn(null)

        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)

        assertThat(
            shadowDialog.title,
            equalTo(activity.getString(org.odk.collect.strings.R.string.quit_form_continue_title))
        )
        assertThat(
            shadowDialog.getView().findViewById<TextView>(R.id.save_explanation).text,
            equalTo(activity.getString(org.odk.collect.strings.R.string.discard_form_warning))
        )
        assertThat(
            shadowDialog.getView().findViewById<View>(R.id.save_changes).visibility,
            equalTo(View.GONE)
        )
    }

    @Test
    fun whenSaveAsDraftIsDisabled_andFormCanNotBeFullyDiscarded_showsWarningTitleAndMessage_andHidesButton() {
        settingsProvider.getProtectedSettings().save(KEY_SAVE_MID, false)
        whenever(formSaveViewModel.canBeFullyDiscarded()).doReturn(false)
        whenever(formSaveViewModel.lastSavedTime).doReturn(456L)

        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)

        assertThat(
            shadowDialog.title,
            equalTo(activity.getString(org.odk.collect.strings.R.string.quit_form_continue_title))
        )
        assertThat(
            shadowDialog.getView().findViewById<TextView>(R.id.save_explanation).text,
            equalTo(
                SimpleDateFormat(
                    activity.getString(org.odk.collect.strings.R.string.discard_changes_warning),
                    Locale.getDefault()
                ).format(456L)
            )
        )
        assertThat(
            shadowDialog.getView().findViewById<View>(R.id.save_changes).visibility,
            equalTo(View.GONE)
        )
    }

    @Test
    fun whenSaveAsDraftIsDisabled_hidesOutlinedKeepEditing_andShowsFilledKeepEditing() {
        settingsProvider.getProtectedSettings().save(KEY_SAVE_MID, false)
        whenever(formSaveViewModel.canBeFullyDiscarded()).doReturn(true)
        whenever(formSaveViewModel.lastSavedTime).doReturn(null)

        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val dialog = showDialog(activity)

        val shadowDialog = extract<ShadowAndroidXAlertDialog>(dialog)

        assertThat(
            shadowDialog.getView().findViewById<View>(R.id.keep_editing_outlined).visibility,
            equalTo(View.GONE)
        )
        assertThat(
            shadowDialog.getView().findViewById<View>(R.id.keep_editing_filled).visibility,
            equalTo(View.VISIBLE)
        )
    }

    private fun showDialog(activity: Activity) = QuitFormDialog.show(
        activity,
        formSaveViewModel,
        formEntryViewModel,
        settingsProvider,
        null
    )
}
