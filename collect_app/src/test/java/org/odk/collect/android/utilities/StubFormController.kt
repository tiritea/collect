package org.odk.collect.android.utilities

import org.javarosa.core.model.FormDef
import org.javarosa.core.model.FormIndex
import org.javarosa.core.model.data.IAnswerData
import org.javarosa.core.model.instance.TreeReference
import org.javarosa.core.services.transport.payload.ByteArrayPayload
import org.javarosa.form.api.FormEntryCaption
import org.javarosa.form.api.FormEntryPrompt
import org.odk.collect.android.exception.JavaRosaException
import org.odk.collect.android.formentry.audit.AuditEventLogger
import org.odk.collect.android.javarosawrapper.FormController
import org.odk.collect.android.javarosawrapper.InstanceMetadata
import org.odk.collect.android.javarosawrapper.SuccessValidationResult
import org.odk.collect.android.javarosawrapper.ValidationResult
import org.odk.collect.entities.javarosa.finalization.EntitiesExtra
import java.io.File

open class StubFormController : FormController {
    override fun getFormDef(): FormDef? = null

    override fun getMediaFolder(): File? = null

    override fun getInstanceFile(): File? = null

    override fun setInstanceFile(instanceFile: File?) {}

    override fun getAbsoluteInstancePath(): String? = null

    override fun getLastSavedPath(): String? = null

    override fun getIndexWaitingForData(): FormIndex? = null

    override fun setIndexWaitingForData(index: FormIndex?) {}

    override fun getAuditEventLogger(): AuditEventLogger? = null

    override fun isEditing(): Boolean {
        return false
    }

    override fun getXPath(index: FormIndex?): String? = null

    override fun getIndexFromXPath(xpath: String): FormIndex? = null

    override fun getEvent(): Int = -1

    override fun getEvent(index: FormIndex?): Int = -1

    override fun getFormIndex(): FormIndex = FormIndex.createBeginningOfFormIndex()

    override fun getLanguage(): String? = null

    override fun getLanguages(): Array<String>? = null

    override fun setLanguage(language: String?) {}

    override fun getFormTitle(): String? = null

    override fun getCaptionPrompt(): FormEntryCaption? = null

    override fun getCaptionPrompt(index: FormIndex?): FormEntryCaption? = null

    override fun finalizeForm() {}

    override fun usesDatabaseExternalDataFeature(index: FormIndex): Boolean = false

    override fun indexIsInFieldList(): Boolean = false

    override fun indexIsInFieldList(index: FormIndex?): Boolean = false

    override fun currentPromptIsQuestion(): Boolean = false

    override fun isCurrentQuestionFirstInForm(): Boolean = false

    override fun answerQuestion(index: FormIndex?, data: IAnswerData?): Int = -1

    @Throws(JavaRosaException::class)
    override fun validateAnswers(moveToInvalidIndex: Boolean): ValidationResult = SuccessValidationResult

    override fun saveAnswer(index: FormIndex?, data: IAnswerData?): Boolean = false

    override fun stepToNextEvent(stepIntoGroup: Boolean): Int = -1

    override fun stepToPreviousEvent(): Int = -1

    override fun jumpToIndex(index: FormIndex?): Int = -1

    override fun stepOverGroup(): Int = -1

    @Throws(JavaRosaException::class)
    override fun stepToPreviousScreenEvent(): Int = -1

    @Throws(JavaRosaException::class)
    override fun stepToNextScreenEvent(): Int = -1

    override fun stepToOuterScreenEvent(): Int = -1

    override fun jumpToNewRepeatPrompt() {}

    override fun isDisplayableGroup(index: FormIndex?): Boolean = false

    override fun saveOneScreenAnswer(
        index: FormIndex?,
        data: IAnswerData?,
        evaluateConstraints: Boolean
    ): ValidationResult = SuccessValidationResult

    @Throws(JavaRosaException::class)
    override fun saveAllScreenAnswers(
        answers: HashMap<FormIndex, IAnswerData>?,
        evaluateConstraints: Boolean
    ): ValidationResult = SuccessValidationResult

    override fun newRepeat() {}

    override fun deleteRepeat() {}

    override fun getQuestionPrompt(): FormEntryPrompt? = null

    override fun getQuestionPrompt(index: FormIndex?): FormEntryPrompt? = null

    override fun getQuestionPrompts(index: FormIndex): Array<FormEntryPrompt> = emptyArray()

    override fun getQuestionPromptConstraintText(index: FormIndex?): String? = null

    override fun getQuestionPromptRequiredText(index: FormIndex?): String? = null

    override fun currentCaptionPromptIsQuestion(): Boolean = false

    override fun isGroupRelevant(): Boolean = false

    override fun getGroupsForIndex(formIndex: FormIndex?): Array<FormEntryCaption> = emptyArray()

    override fun indexContainsRepeatableGroup(): Boolean = false

    override fun indexContainsRepeatableGroup(formIndex: FormIndex?): Boolean = false

    override fun getLastRepeatedGroupRepeatCount(): Int = -1

    override fun getLastRepeatedGroupName(): String? = null

    override fun getLastGroupText(): String? = null

    override fun isSubmissionEntireForm(): Boolean = false

    override fun getFilledInFormXml(): ByteArrayPayload = ByteArrayPayload()

    override fun getSubmissionXml(): ByteArrayPayload? = null

    override fun getSubmissionMetadata(): InstanceMetadata? = null

    override fun currentFormAuditsLocation(): Boolean = false

    override fun currentFormCollectsBackgroundLocation(): Boolean = false

    override fun getAnswer(treeReference: TreeReference?): IAnswerData? = null

    override fun getEntities(): EntitiesExtra? = null
}
