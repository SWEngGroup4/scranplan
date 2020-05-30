package com.group4sweng.scranplan;

public interface Credentials {
    //  Default test values.
    String TEST_EMAIL = "ncab500+test@gmail.com";
    String TEST_PASSWORD = "password1";

    String TEST_EMAIL_ACC2 = "ncab500+test2@gmail.com";
    String TEST_PASSWORD_ACC2 = "password1";


    //  How long we should sleep when waiting for Firebase information to update. Increase this value if you have a slower machine or emulator.
     int THREAD_SLEEP_TIME = 5000;
}
