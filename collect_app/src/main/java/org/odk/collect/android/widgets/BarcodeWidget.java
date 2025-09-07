/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import static org.odk.collect.android.utilities.Appearances.FRONT;
import static org.odk.collect.android.utilities.Appearances.hasAppearance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.activities.ScannerWithFlashlightActivity;
import org.odk.collect.android.databinding.BarcodeWidgetAnswerBinding;
import org.odk.collect.android.formentry.questions.QuestionDetails;
import org.odk.collect.android.utilities.Appearances;
import org.odk.collect.androidshared.system.CameraUtils;
import org.odk.collect.androidshared.ui.ToastUtils;
import org.odk.collect.android.widgets.interfaces.WidgetDataReceiver;
import org.odk.collect.android.widgets.utilities.WaitingForDataRegistry;

/**
 * Widget that allows user to scan barcodes and add them to the form.
 */

@SuppressLint("ViewConstructor")
public class BarcodeWidget extends QuestionWidget implements WidgetDataReceiver {
    BarcodeWidgetAnswerBinding binding;

    private final WaitingForDataRegistry waitingForDataRegistry;
    private final CameraUtils cameraUtils;

    public BarcodeWidget(Context context, QuestionDetails questionDetails, WaitingForDataRegistry waitingForDataRegistry,
                         CameraUtils cameraUtils, Dependencies dependencies) {
        super(context, dependencies, questionDetails);
        render();

        this.waitingForDataRegistry = waitingForDataRegistry;
        this.cameraUtils = cameraUtils;
    }

    @Override
    protected View onCreateAnswerView(Context context, FormEntryPrompt prompt, int answerFontSize) {
        binding = BarcodeWidgetAnswerBinding.inflate(((Activity) context).getLayoutInflater());

        if (prompt.isReadOnly()) {
            binding.barcodeButton.setVisibility(GONE);
        } else {
            binding.barcodeButton.setOnClickListener(v -> onButtonClick());
        }
        binding.barcodeAnswerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, answerFontSize);

        String answer = prompt.getAnswerText();
        if (answer != null && !answer.isEmpty()) {
            binding.barcodeButton.setText(getContext().getString(org.odk.collect.strings.R.string.replace_barcode));
            binding.barcodeAnswerText.setText(answer);
        }

        updateVisibility();
        return binding.getRoot();
    }

    @Override
    public void clearAnswer() {
        binding.barcodeAnswerText.setText(null);
        binding.barcodeButton.setText(getContext().getString(org.odk.collect.strings.R.string.get_barcode));
        widgetValueChanged();

        updateVisibility();
    }

    @Override
    public IAnswerData getAnswer() {
        String answer = binding.barcodeAnswerText.getText().toString();
        return answer.isEmpty() ? null : new StringData(answer);
    }

    @Override
    public void setData(Object answer) {
        String response = (String) answer;

        binding.barcodeAnswerText.setText(stripInvalidCharacters(response));
        binding.barcodeButton.setText(getContext().getString(org.odk.collect.strings.R.string.replace_barcode));
        updateVisibility();

        widgetValueChanged();
    }

    private void updateVisibility() {
        if (hasAppearance(getFormEntryPrompt(), Appearances.HIDDEN_ANSWER)) {
            binding.barcodeAnswerText.setVisibility(GONE);
        } else {
            binding.barcodeAnswerText.setVisibility(binding.barcodeAnswerText.getText().toString().isBlank() ? GONE : VISIBLE);
        }
    }

    // Remove control characters, invisible characters and unused code points.
    private String stripInvalidCharacters(String data) {
        return data == null ? null : data.replaceAll("\\p{C}", "");
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        binding.barcodeAnswerText.setOnLongClickListener(l);
        binding.barcodeButton.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        binding.barcodeButton.cancelLongPress();
        binding.barcodeAnswerText.cancelLongPress();
    }

    private void onButtonClick() {
        getPermissionsProvider().requestCameraPermission((Activity) getContext(), () -> {
            waitingForDataRegistry.waitForData(getFormEntryPrompt().getIndex());

            IntentIntegrator intent = new IntentIntegrator((Activity) getContext())
                    .setCaptureActivity(ScannerWithFlashlightActivity.class);

            setCameraIdIfNeeded(getFormEntryPrompt(), intent);
            intent.initiateScan();
        });
    }

    private void setCameraIdIfNeeded(FormEntryPrompt prompt, IntentIntegrator intent) {
        if (Appearances.isFrontCameraAppearance(prompt)) {
            if (cameraUtils.isFrontCameraAvailable(getContext())) {
                intent.addExtra(FRONT, true);
            } else {
                ToastUtils.showLongToast(org.odk.collect.strings.R.string.error_front_camera_unavailable);
            }
        }
    }
}
