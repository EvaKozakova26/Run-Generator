package cz.uhk.fim.runhk.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianCounterUtils {

    private MedianCounterUtils() {
    }

    public static double getMedian(List<Double> valuesList) {
        List<Double> sortedList = getSortedList(valuesList);
        if (isEven(sortedList.size())) {
            int middleElement = sortedList.size() / 2;
            double firstValue = sortedList.get(middleElement);
            double secondValue = sortedList.get(middleElement + 1);
            return (firstValue + secondValue) / 2;
        } else {
            return sortedList.get(sortedList.size() / 2);
        }

    }

    private static List<Double> getSortedList(List<Double> valuesList) {
        List<Double> doublesList = new ArrayList<>(valuesList);
        Collections.sort(doublesList);
        return doublesList;
    }

    private static boolean isEven(int listSize) {
        return listSize % 2 == 0;
    }
}
