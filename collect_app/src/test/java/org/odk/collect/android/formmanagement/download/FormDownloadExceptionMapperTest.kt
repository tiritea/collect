package org.odk.collect.android.formmanagement.download

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormDownloadExceptionMapperTest {
    private lateinit var context: Context
    private lateinit var mapper: FormDownloadExceptionMapper

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        mapper = FormDownloadExceptionMapper(context)
    }

    @Test
    fun formWithNoHashError_returnsFormWithNoHashErrorMessage() {
        val expectedString = context.getString(
            org.odk.collect.strings.R.string.form_with_no_hash_error
        ) + " " + context.getString(org.odk.collect.strings.R.string.report_to_project_lead)
        assertThat(
            mapper.getMessage(FormDownloadException.FormWithNoHash()),
            `is`(expectedString)
        )
    }

    @Test
    fun formParsingError_returnsFormParsingErrorMessage() {
        val original = Exception("original message")
        original.stackTrace = arrayOf(
            StackTraceElement("Class", "method", "File", 1),
            StackTraceElement("Class", "method", "File", 2)
        )

        assertThat(
            mapper.getMessage(FormDownloadException.FormParsingError(original)),
            equalTo(
                context.getString(org.odk.collect.strings.R.string.form_parsing_error) + "\n\n" +
                    original.message + "\n" +
                    "Class.method(File:1)" + "\n\n" +
                    context.getString(org.odk.collect.strings.R.string.report_to_project_lead)
            )
        )
    }

    @Test
    fun formSaveError_returnsFormSaveErrorMessage() {
        val expectedString = context.getString(
            org.odk.collect.strings.R.string.form_save_disk_error
        ) + " " + context.getString(org.odk.collect.strings.R.string.report_to_project_lead)
        assertThat(
            mapper.getMessage(FormDownloadException.DiskError()),
            `is`(expectedString)
        )
    }

    @Test
    fun formWithInvalidSubmissionError_returnsFormInvalidSubmissionErrorMessage() {
        val expectedString = context.getString(
            org.odk.collect.strings.R.string.form_with_invalid_submission_error
        ) + " " + context.getString(org.odk.collect.strings.R.string.report_to_project_lead)
        assertThat(
            mapper.getMessage(FormDownloadException.InvalidSubmission()),
            `is`(expectedString)
        )
    }
}
