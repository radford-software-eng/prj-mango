package com.mango.prjmango;

import com.mango.prjmango.utilities.DatabaseCommands;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Keeps track of the currently logged-in user throughout the application.
 */
public class LoggedInUser {

    @Getter private static int teacherId;
    @Getter @Setter private static String firstName;
    @Getter @Setter private static String lastName;
    @Getter @Setter private static String email;
    @Getter @Setter private static char[] password;
    @Getter @Setter private static int securityQuestion1Index;
    @Getter @Setter private static int securityQuestion2Index;

    /**
     * Constructor. Calls {@link LoggedInUser#setFields(List)} to set values to variables that will be accessed
     * throughout the application.
     * 
     * @param teacherId the specific {@code teacher_id} within the iepCipher database.
     */
    public LoggedInUser(int teacherId) {
        this.teacherId = teacherId;
        setFields(DatabaseCommands.getUserDetails(teacherId));
    }

    private static void setFields(List<String> rawData) {
        firstName = rawData.get(0);
        lastName = rawData.get(1);
        email = rawData.get(2);
    }

    @Override
    public String toString() {
        return firstName + ", " + lastName + ": " + email;
    }
}
