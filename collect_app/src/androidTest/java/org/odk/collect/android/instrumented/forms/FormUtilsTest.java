package org.odk.collect.android.instrumented.forms;

import static org.mockito.Mockito.mock;
import static org.odk.collect.android.support.StorageUtils.copyFormToStorage;
import static java.util.Collections.emptyList;

import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.reference.RootTranslator;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.odk.collect.android.external.FormsContract;
import org.odk.collect.android.storage.StoragePathProvider;
import org.odk.collect.android.storage.StorageSubdirectory;
import org.odk.collect.android.support.CollectHelpers;
import org.odk.collect.android.support.rules.ResetStateRule;
import org.odk.collect.android.tasks.FormLoaderTask;
import org.odk.collect.android.utilities.FileUtils;
import org.odk.collect.android.utilities.FormUtils;
import org.odk.collect.android.utilities.FormsRepositoryProvider;
import org.odk.collect.forms.Form;
import org.odk.collect.forms.instances.Instance;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FormUtilsTest {
    private static final String BASIC_FORM = "basic.xml";

    private final FormLoaderTask.FormEntryControllerFactory formEntryControllerFactory = new FormLoaderTask.FormEntryControllerFactory() {
        @Override
        public FormEntryController create(FormDef formDef, File formMediaDir, Instance instance) {
            return new FormEntryController(new FormEntryModel(formDef));
        }
    };

    @Rule
    public ResetStateRule resetStateRule = new ResetStateRule();

    @Before
    public void setUp() throws IOException {
        CollectHelpers.addDemoProject();
        copyFormToStorage(BASIC_FORM, emptyList(), true);
    }

    /* Verify that each host string matches only a single root translator, allowing for them to
     be defined in any order. See: https://github.com/getodk/collect/issues/3334
    */
    @Test
    public void sessionRootTranslatorOrderDoesNotMatter() throws Exception {
        final String formPath = new StoragePathProvider().getOdkDirPath(StorageSubdirectory.FORMS) + File.separator + BASIC_FORM;
        final Form form = new FormsRepositoryProvider(ApplicationProvider.getApplicationContext()).create().getOneByPath(formPath);
        final Uri formUri = FormsContract.getUri("DEMO", form.getDbId());

        // Load the form in order to populate the ReferenceManager
        FormLoaderTask formLoaderTask = new FormLoaderTask(formUri, FormsContract.CONTENT_ITEM_TYPE, null, null, formEntryControllerFactory, mock(), mock());
        formLoaderTask.executeSynchronously();

        final File formXml = new File(formPath);
        final File formMediaDir = FileUtils.getFormMediaDir(formXml);
        List<RootTranslator> rootTranslators = FormUtils.buildSessionRootTranslators(formMediaDir.getName(), FormUtils.enumerateHostStrings());

        // Check each type of host string to determine that only one match is resolved.
        for (String hostString : FormUtils.enumerateHostStrings()) {
            String uri = String.format("jr://%s/test", hostString);
            int matchCount = 0;
            for (RootTranslator rootTranslator : rootTranslators) {
                if (rootTranslator.derives(uri)) {
                    matchCount++;
                }
            }
            Assert.assertEquals("Expected only a single match for URI: " + uri, 1, matchCount);
        }
    }

    /* Verify that the host strings appear in an order that does not allow for greedy matches, e.g.
     matching 'file' instead of 'file-csv'. According to the behavior in the test above,
     sessionRootTranslatorOrderDoesNotMatter, it is not actually a requirement to have the test
     below pass. This simply follows the cautionary remarks in the following issue:
     https://github.com/getodk/collect/issues/3334
     */
    @Test
    public void hostStringsOrderedCorrectly() throws Exception {
        String[] hostStrings = FormUtils.enumerateHostStrings();
        // No host string should be a substring of the subsequent ones.
        for (int i = 0; i < hostStrings.length; ++i) {
            String currentHostString = hostStrings[i];
            for (int j = i + 1; j < hostStrings.length; ++j) {
                String subsequentHostString = hostStrings[j];
                Assert.assertFalse(subsequentHostString.contains(currentHostString));
            }
        }
    }
}
