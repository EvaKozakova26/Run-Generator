package cz.uhk.fim.runhk.service.helper.utils;

public class StringLabelUtils {

    private StringLabelUtils() {
    }

    public static String createDiffString(int originalValue, int newValue) {
        int distanceDifference = ((originalValue - newValue));
        if (distanceDifference >= 0) {
            return " +" + distanceDifference;
        } else {
            return String.valueOf(distanceDifference);
        }
    }
}
