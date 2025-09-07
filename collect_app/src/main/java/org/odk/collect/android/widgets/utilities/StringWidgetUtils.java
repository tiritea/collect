package org.odk.collect.android.widgets.utilities;

import org.javarosa.core.model.data.DecimalData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.listeners.ThousandsSeparatorTextWatcher;
import org.odk.collect.android.utilities.Appearances;
import org.odk.collect.android.utilities.FormEntryPromptUtils;

import timber.log.Timber;

public final class StringWidgetUtils {

    private StringWidgetUtils() {
    }

    public static Integer getIntegerAnswerValueFromIAnswerData(IAnswerData dataHolder) {
        if (dataHolder != null) {
            Object dataValue = dataHolder.getValue();
            if (dataValue instanceof Double) {
                return ((Double) dataValue).intValue();
            } else if (dataValue instanceof Integer) {
                return (Integer) dataValue;
            } else if (dataValue instanceof String) {
                try {
                    return Integer.parseInt((String) dataValue);
                } catch (NumberFormatException ignored) {
                    // ignored
                }
            }
        }
        return null;
    }

    public static Double getDoubleAnswerValueFromIAnswerData(IAnswerData dataHolder) {
        if (dataHolder != null) {
            Object dataValue = dataHolder.getValue();
            if (dataValue instanceof Double) {
                return (Double) dataValue;
            } else if (dataValue instanceof Integer) {
                return Double.valueOf((Integer) dataValue);
            } else if (dataValue instanceof String) {
                try {
                    return Double.parseDouble((String) dataValue);
                } catch (NumberFormatException ignored) {
                    // ignored
                }
            }
        }
        return null;
    }

    public static IntegerData getIntegerData(String answer, FormEntryPrompt prompt) {
        if (Appearances.useThousandSeparator(prompt)) {
            answer = ThousandsSeparatorTextWatcher.getOriginalString(answer);
        }

        if (answer.isEmpty()) {
            return null;
        } else {
            try {
                return new IntegerData(Integer.parseInt(answer));
            } catch (Exception numberFormatException) {
                return null;
            }
        }
    }

    public static DecimalData getDecimalData(String answer, FormEntryPrompt prompt) {
        if (Appearances.useThousandSeparator(prompt)) {
            answer = ThousandsSeparatorTextWatcher.getOriginalString(answer);
        }

        if (answer.isEmpty()) {
            return null;

        } else {
            try {
                return new DecimalData(Double.parseDouble(answer));
            } catch (Exception numberFormatException) {
                return null;
            }
        }
    }

    public static StringData getStringNumberData(String answer, FormEntryPrompt prompt) {
        if (Appearances.useThousandSeparator(prompt)) {
            answer = ThousandsSeparatorTextWatcher.getOriginalString(answer);
        }

        if (answer.isEmpty()) {
            return null;
        } else {
            try {
                return new StringData(answer);
            } catch (Exception numberFormatException) {
                return null;
            }
        }
    }

    public static Integer getNumberOfRows(FormEntryPrompt prompt) {
        String rows = FormEntryPromptUtils.getAdditionalAttribute(prompt, "rows");
        if (rows != null && !rows.isEmpty()) {
            try {
                return Integer.parseInt(rows);
            } catch (Exception e) {
                Timber.e(new Error("Unable to process the rows setting for the answerText field: " + e));
            }
        }
        return null;
    }
}
