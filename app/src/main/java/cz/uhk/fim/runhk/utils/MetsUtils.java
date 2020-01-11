package cz.uhk.fim.runhk.utils;

public class MetsUtils {

    private MetsUtils() {
    }

    public static double getMETS(double pace) {
        double METS = 0;
        if (pace < 3.4) METS = 18;
        if (pace < 3.75 && pace > 3.4) METS = 16;
        if (pace < 4 && pace > 3.75) METS = 15;
        if (pace < 4.4 && pace > 4) METS = 14;
        if (pace < 4.7 && pace > 4.4) METS = 13.5;
        if (pace < 5 && pace > 4.7) METS = 12.5;
        if (pace < 5.3 && pace > 5) METS = 11.5;
        if (pace < 5.6 && pace > 5.3) METS = 11;
        if (pace < 6.25 && pace > 5.6) METS = 10;
        if (pace < 7.2 && pace > 6.25) METS = 9;
        if (pace > 7.2) METS = 8;

        return METS;

    }
}
