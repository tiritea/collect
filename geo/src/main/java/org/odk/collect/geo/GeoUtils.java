package org.odk.collect.geo;

import android.content.Context;
import android.location.Location;

import org.odk.collect.maps.MapPoint;
import org.odk.collect.shared.strings.StringUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public final class GeoUtils {

    private GeoUtils() {

    }

    /**
     * Serializes a list of vertices into a string, in the format
     * appropriate for storing as the result of the form question.
     */
    public static String formatPointsResultString(List<MapPoint> points, boolean isShape) {
        if (isShape) {
            // Polygons are stored with a last point that duplicates the
            // first point.  Add this extra point if it's not already present.
            int count = points.size();
            if (count > 1 && !points.get(0).equals(points.get(count - 1))) {
                points.add(points.get(0));
            }
        }
        StringBuilder result = new StringBuilder();
        for (MapPoint point : points) {
            // TODO(ping): Remove excess precision when we're ready for the output to change.
            result.append(String.format(Locale.US, "%s %s %s %s;",
                    Double.toString(point.latitude), Double.toString(point.longitude),
                    Double.toString(point.altitude), Float.toString((float) point.accuracy)));
        }

        return StringUtils.removeEnd(result.toString().trim(), ";");
    }

    public static String formatLocationResultString(Location location) {
        return formatLocationResultString(new org.odk.collect.location.Location(
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                location.getAccuracy()
        ));
    }

    public static String formatLocationResultString(org.odk.collect.location.Location location) {
        return String.format("%s %s %s %s", location.getLatitude(), location.getLongitude(),
                location.getAltitude(), location.getAccuracy());
    }

    public static String formatAccuracy(Context context, float accuracy) {
        String formattedValue = new DecimalFormat("#.##").format(accuracy);
        return context.getString(org.odk.collect.strings.R.string.accuracy_m, formattedValue);
    }
}
