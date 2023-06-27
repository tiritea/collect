package org.odk.collect.android.feature.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.odk.collect.android.R
import org.odk.collect.android.support.rules.CollectTestRule
import org.odk.collect.android.support.rules.TestRuleChain

@RunWith(AndroidJUnit4::class)
class MovingBackwardsTest {
    private val rule = CollectTestRule()

    @get:Rule
    var ruleChain: RuleChain = TestRuleChain.chain().around(rule)

    @Test
    fun whenMovingBackwardDisabledWithPreventingUsersFormBypassingIt_relatedOptionsShouldBeUpdated() {
        rule.startAtMainMenu()
            .openProjectSettingsDialog()
            .clickSettings()
            .clickAccessControl()
            .clickFormEntrySettings()
            .clickMovingBackwards()
            .clickOnString(R.string.yes)
            .assertGoToPromptDisabled()
            .assertSaveAsDraftInFormEntryDisabled()
            .assertSaveAsDraftInFormEndDisabled()
            .assertFinalizeDisabled()
            .assertGoToPromptUnchecked()
            .assertSaveAsDraftInFormEntryUnchecked()
            .assertSaveAsDraftInFormEndUnchecked()
            .assertFinalizeChecked()
    }

    @Test
    fun whenMovingBackwardDisabledWithoutPreventingUsersFormBypassingIt_relatedOptionsShouldNotBeUpdated() {
        rule.startAtMainMenu()
            .openProjectSettingsDialog()
            .clickSettings()
            .clickAccessControl()
            .clickFormEntrySettings()
            .clickMovingBackwards()
            .clickOnString(R.string.no)
            .assertGoToPromptEnabled()
            .assertSaveAsDraftInFormEntryEnabled()
            .assertSaveAsDraftInFormEndEnabled()
            .assertFinalizeEnabled()
            .assertGoToPromptChecked()
            .assertSaveAsDraftInFormEntryChecked()
            .assertSaveAsDraftInFormEndChecked()
            .assertFinalizeChecked()
    }

    @Test
    fun whenMovingBackwardDisabledWithPreventingUsersFormBypassingIt_finalizedShouldBecomeEnabled() {
        rule.startAtMainMenu()
            .openProjectSettingsDialog()
            .clickSettings()
            .clickAccessControl()
            .clickFormEntrySettings()
            .clickOnString(R.string.finalize)
            .clickMovingBackwards()
            .clickOnString(R.string.yes)
            .assertFinalizeChecked()
    }
}
