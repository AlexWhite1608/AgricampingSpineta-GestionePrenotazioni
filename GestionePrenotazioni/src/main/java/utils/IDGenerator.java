package utils;

import java.util.Calendar;
import java.util.UUID;

public class IDGenerator {

    public static String generateRandomId() {
        String uniqueID = UUID.randomUUID().toString();

        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String currentDay = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        return currentYear + "-" + currentDay + uniqueID;
    }
}
