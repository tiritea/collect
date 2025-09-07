package org.odk.collect.android.fragments.support;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.testing.FragmentScenario;

import org.joda.time.LocalDateTime;
import org.odk.collect.android.R;
import org.odk.collect.android.widgets.datetime.pickers.CustomDatePickerDialog;
import org.odk.collect.android.widgets.datetime.DatePickerDetails;
import org.odk.collect.android.widgets.utilities.DateTimeWidgetUtils;
import org.odk.collect.testshared.RobolectricHelpers;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;

public class DialogFragmentHelpers {

    private DialogFragmentHelpers() {
    }

    public static DatePickerDetails setUpDatePickerDetails(DatePickerDetails.DatePickerType datePickerType) {
        DatePickerDetails datePickerDetails = mock(DatePickerDetails.class);
        when(datePickerDetails.getDatePickerType()).thenReturn(datePickerType);
        when(datePickerDetails.isSpinnerMode()).thenReturn(true);
        when(datePickerDetails.isMonthYearMode()).thenReturn(false);
        when(datePickerDetails.isYearMode()).thenReturn(false);

        return datePickerDetails;
    }

    public static Bundle getDialogFragmentArguments(DatePickerDetails datePickerDetails) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DateTimeWidgetUtils.DATE, new LocalDateTime().withDate(2020, 5, 12));
        bundle.putSerializable(DateTimeWidgetUtils.DATE_PICKER_DETAILS, datePickerDetails);
        return bundle;
    }

    public static void assertDialogIsCancellable(boolean cancellable) {
        assertThat(shadowOf(ShadowDialog.getLatestDialog()).isCancelable(), equalTo(cancellable));
    }

    public static void assertDialogShowsCorrectDate(int year, int month, int day, String date) {
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();

        assertDatePickerValue(dialog, year, month, day);
        assertThat(((TextView) dialog.findViewById(R.id.date_gregorian)).getText().toString(), equalTo(date));
    }

    public static void assertDialogShowsCorrectDateForYearMode(int year, String date) {
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();

        assertDatePickerValue(dialog, year, 0, 1);
        assertThat(((TextView) dialog.findViewById(R.id.date_gregorian)).getText().toString(), equalTo(date));
    }

    public static void assertDialogShowsCorrectDateForMonthMode(int year, int month, String date) {
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();

        assertDatePickerValue(dialog, year, month, 1);
        assertThat(((TextView) dialog.findViewById(R.id.date_gregorian)).getText().toString(), equalTo(date));
    }

    public static void assertDialogTextViewUpdatesDate(String date, int year, int month, int day) {
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        setDatePickerValue(dialog, year, month, day);

        assertThat(((TextView) dialog.findViewById(R.id.date_gregorian)).getText().toString(), equalTo(date));
    }

    public static void assertDateUpdateInActivity(DatePickerTestActivity activity, int year, int month, int day) {
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        setDatePickerValue(dialog, year, month, day);

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        RobolectricHelpers.runLooper();

        assertThat(activity.selectedDate.getYear(), equalTo(2020));
        assertThat(activity.selectedDate.getMonthOfYear(), equalTo(5));
        assertThat(activity.selectedDate.getDayOfMonth(), equalTo(12));
    }

    public static void assertDialogIsDismissedOnButtonClick(int dialogButton) {
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        dialog.getButton(dialogButton).performClick();
        RobolectricHelpers.runLooper();
        assertTrue(shadowOf(dialog).hasBeenDismissed());
    }

    /**
     * @deprecated should use {@link FragmentScenario#recreate()} instead of Robolectric for this
     */
    @Deprecated
    public static <T extends DialogFragment> void assertDialogRetainsDateOnScreenRotation(T dialogFragment, String date, int year, int month, int day) {
        ActivityController<FragmentActivity> activityController = Robolectric.buildActivity(FragmentActivity.class);
        activityController.setup();

        dialogFragment.show(activityController.get().getSupportFragmentManager(), "TAG");
        RobolectricHelpers.runLooper();
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        setDatePickerValue(dialog, year, month, day);

        activityController.recreate();

        T restoredFragment = (T) activityController.get().getSupportFragmentManager().findFragmentByTag("TAG");
        AlertDialog restoredDialog = (AlertDialog) restoredFragment.getDialog();

        assertDatePickerValue(restoredDialog, year, month, day);
        assertThat(((TextView) restoredDialog.findViewById(R.id.date_gregorian)).getText().toString(), equalTo(date));
    }

    private static void setDatePickerValue(AlertDialog dialog, int year, int month, int day) {
        ((NumberPicker) dialog.findViewById(R.id.year_picker)).setValue(year);
        ((NumberPicker) dialog.findViewById(R.id.month_picker)).setValue(month);
        ((NumberPicker) dialog.findViewById(R.id.day_picker)).setValue(day);
    }

    private static void assertDatePickerValue(AlertDialog dialog, int year, int month, int day) {
        assertThat(((NumberPicker) dialog.findViewById(R.id.year_picker)).getValue(), equalTo(year));
        assertThat(((NumberPicker) dialog.findViewById(R.id.month_picker)).getValue(), equalTo(month));
        assertThat(((NumberPicker) dialog.findViewById(R.id.day_picker)).getValue(), equalTo(day));
    }

    public static class DatePickerTestActivity extends FragmentActivity implements CustomDatePickerDialog.DateChangeListener {
        public LocalDateTime selectedDate;

        @Override
        public void onDateChanged(LocalDateTime selectedDate) {
            this.selectedDate = selectedDate;
        }
    }
}
