package org.odk.collect.android.feature.settings;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.odk.collect.android.R;
import org.odk.collect.android.configure.qr.AppConfigurationGenerator;
import org.odk.collect.android.configure.qr.QRCodeGenerator;
import org.odk.collect.android.support.TestDependencies;
import org.odk.collect.android.support.pages.MainMenuPage;
import org.odk.collect.android.support.pages.ProjectSettingsPage;
import org.odk.collect.android.support.pages.QRCodePage;
import org.odk.collect.android.support.rules.CollectTestRule;
import org.odk.collect.android.support.rules.RunnableRule;
import org.odk.collect.android.support.rules.TestRuleChain;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

@RunWith(AndroidJUnit4.class)
public class ConfigureWithQRCodeTest {

    private final TestDependencies testDependencies = new TestDependencies() {
        @Override
        public QRCodeGenerator providesQRCodeGenerator() {
            return stubQRCodeGenerator;
        }
    };
    private final CollectTestRule rule = new CollectTestRule();
    private final StubQRCodeGenerator stubQRCodeGenerator = new StubQRCodeGenerator();
    @Rule
    public RuleChain copyFormChain = TestRuleChain.chain(testDependencies)
            .around(new RunnableRule(stubQRCodeGenerator::setup))
            .around(rule);

    @After
    public void teardown() {
        // Clean up files created by stub generator
        stubQRCodeGenerator.teardown();
    }

    @Test
    public void clickConfigureQRCode_opensScanner_andThenScanning_importsSettings() {
        QRCodePage qrCodePage = rule.startAtMainMenu()
                .openProjectSettingsDialog()
                .clickSettings()
                .clickProjectManagement()
                .clickConfigureQR();

        testDependencies.getFakeBarcodeScannerViewFactory().scan("{\"general\":{ \"server_url\": \"http://gallops.example\" },\"admin\":{}}");
        qrCodePage
                .checkIsToastWithMessageDisplayed(org.odk.collect.strings.R.string.successfully_imported_settings)
                .assertFileWithProjectNameUpdated("Demo project", "gallops.example");

        new MainMenuPage()
                .assertOnPage()
                .openProjectSettingsDialog()
                .clickSettings()
                .clickServerSettings()
                .assertText("http://gallops.example");
    }

    @Test
    public void clickConfigureQRCode_andClickingOnView_showsQRCode() {
        rule.startAtMainMenu()
                .openProjectSettingsDialog()
                .clickSettings()
                .clickProjectManagement()
                .clickConfigureQR()
                .clickView()
                .assertImageViewShowsImage(R.id.ivQRcode, BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        stubQRCodeGenerator.getDrawableID()
                ));
    }

    @Test
    public void whenThereIsAnAdminPassword_canRemoveFromQRCode() {
        rule.startAtMainMenu()
                .openProjectSettingsDialog()
                .clickSettings()
                .setAdminPassword("blah")
                .clickProjectManagement()
                .clickConfigureQR()
                .clickView()
                .clickOnString(org.odk.collect.strings.R.string.qrcode_with_admin_password)
                .clickOnString(org.odk.collect.strings.R.string.admin_password)
                .clickOnString(org.odk.collect.strings.R.string.generate)
                .assertText(org.odk.collect.strings.R.string.qrcode_without_passwords);
    }

    @Test
    public void whenThereIsAServerPassword_canRemoveFromQRCode() {
        rule.startAtMainMenu()
                .openProjectSettingsDialog()
                .clickSettings()
                .clickServerSettings()
                .clickServerPassword()
                .inputText("blah")
                .clickOKOnDialog()
                .pressBack(new ProjectSettingsPage())
                .pressBack(new MainMenuPage())

                .openProjectSettingsDialog()
                .clickSettings()
                .clickProjectManagement()
                .clickConfigureQR()
                .clickView()
                .clickOnString(org.odk.collect.strings.R.string.qrcode_with_server_password)
                .clickOnString(org.odk.collect.strings.R.string.server_password)
                .clickOnString(org.odk.collect.strings.R.string.generate)
                .assertText(org.odk.collect.strings.R.string.qrcode_without_passwords);
    }

    private static class StubQRCodeGenerator implements QRCodeGenerator {

        private static final int CHECKER_BACKGROUND_DRAWABLE_ID = com.rarepebble.colorpicker.R.drawable.checker_background;

        @Override
        public String generateQRCode(Collection<String> selectedPasswordKeys, AppConfigurationGenerator appConfigurationGenerator) {
            return getQRCodeFilePath();
        }

        public void setup() {
            Bitmap bitmap = BitmapFactory.decodeResource(
                    getApplicationContext().getResources(),
                    getDrawableID());
            saveBitmap(bitmap);
        }

        public void teardown() {
            File file = new File(getQRCodeFilePath());
            if (file.exists()) {
                file.delete();
            }
        }

        String getQRCodeFilePath() {
            return getApplicationContext().getExternalFilesDir(null) + File.separator + "test-collect-settings.png";
        }

        int getDrawableID() {
            return CHECKER_BACKGROUND_DRAWABLE_ID;
        }

        private void saveBitmap(Bitmap bitmap) {
            try (FileOutputStream out = new FileOutputStream(getQRCodeFilePath())) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
