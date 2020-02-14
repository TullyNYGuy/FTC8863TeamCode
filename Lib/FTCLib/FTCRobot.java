package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public interface FTCRobot {

    boolean createRobot();

    void init();

    boolean isInitComplete();

    void update();

    void shutdown();

    /*
     * This function can be called asynchronously with the rest of the program access to shared variables needs to be controller by either synchronized or explicit locking.
     * @param timerValueMsec current time in milliseconds
     */
    void timedUpdate(double timerValueMsec);

    double getCurrentRotation(AngleUnit unit);
}
