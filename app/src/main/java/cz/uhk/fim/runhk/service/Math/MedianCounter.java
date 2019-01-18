package cz.uhk.fim.runhk.service.Math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianCounter {

    public double getMedian(List<Double> valuesList) {
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

    private List<Double> getSortedList(List<Double> valuesList) {
        List<Double> doublesList = new ArrayList<>(valuesList);
        Collections.sort(doublesList);
        return doublesList;
    }

    private boolean isEven(int listSize) {
        return listSize % 2 == 0;
    }
}
