package org.odk.collect.android.support.pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.odk.collect.testshared.RecyclerViewMatcher.withRecyclerView;

import androidx.annotation.NonNull;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.odk.collect.android.R;
import org.odk.collect.testshared.WaitFor;

import java.util.concurrent.Callable;

public class FormHierarchyPage extends Page<FormHierarchyPage> {

    private final String formName;

    public FormHierarchyPage(String formName) {
        this.formName = formName;
    }

    @NonNull
    @Override
    public FormHierarchyPage assertOnPage() {
        // Make sure we've left the fill blank form screen
        WaitFor.waitFor((Callable<Void>) () -> {
            onView(withId(R.id.menu_goto)).check(doesNotExist());
            return null;
        });

        assertToolbarTitle(formName);
        assertText(org.odk.collect.strings.R.string.jump_to_beginning);
        assertText(org.odk.collect.strings.R.string.jump_to_end);
        return this;
    }

    public FormHierarchyPage assertNotRemovableGroup() {
        onView(withId(R.id.menu_delete_child)).check(doesNotExist());
        return this;
    }

    public FormHierarchyPage clickGoUpIcon() {
        onView(withId(R.id.menu_go_up)).perform(click());
        return this;
    }

    public FormEntryPage clickGoToStart() {
        onView(withId(R.id.jumpBeginningButton)).perform(click());
        return new FormEntryPage(formName).assertOnPage();
    }

    public FormEndPage clickGoToEnd() {
        return clickOnString(org.odk.collect.strings.R.string.jump_to_end)
                .assertOnPage(new FormEndPage(formName));
    }

    public FormEndPage clickGoToEnd(String instanceName) {
        return clickOnString(org.odk.collect.strings.R.string.jump_to_end)
                .assertOnPage(new FormEndPage(instanceName));
    }

    public FormEntryPage addGroup() {
        onView(withId(R.id.menu_add_repeat)).perform(click());
        return new FormEntryPage(formName).assertOnPage();
    }

    public FormHierarchyPage deleteGroup() {
        onView(withId(R.id.menu_delete_child)).perform(click());
        return clickOnTextInDialog(org.odk.collect.strings.R.string.delete_repeat, this);
    }

    public FormEndPage clickJumpEndButton() {
        onView(withId(R.id.jumpEndButton)).perform(click());
        return new FormEndPage(formName).assertOnPage();
    }

    public FormHierarchyPage assertHierarchyItem(int position, String primaryText, String secondaryText) {
        onView(withRecyclerView(R.id.list)
                .atPositionOnView(position, R.id.primary_text))
                .check(matches(withText(primaryText)));

        if (secondaryText != null) {
            onView(withRecyclerView(R.id.list)
                    .atPositionOnView(position, R.id.secondary_text))
                    .check(matches(withText(secondaryText)));
        }
        return this;
    }

    public FormHierarchyPage assertPath(String text) {
        onView(withId(R.id.pathtext)).check(matches(withText(text)));
        return this;
    }

    public FormEntryPage clickOnQuestion(String questionLabel) {
        return clickOnQuestion(questionLabel, false);
    }

    public FormEntryPage clickOnQuestion(String questionLabel, boolean isRequired) {
        if (isRequired) {
            questionLabel = "* " + questionLabel;
        }

        onView(withId(R.id.list)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText(questionLabel))));
        clickOnText(questionLabel);
        return new FormEntryPage(formName);
    }

    public FormHierarchyPage clickOnGroup(String groupLabel) {
        onView(withId(R.id.list)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText(groupLabel))));
        clickOnText(groupLabel);
        return this;
    }
}
