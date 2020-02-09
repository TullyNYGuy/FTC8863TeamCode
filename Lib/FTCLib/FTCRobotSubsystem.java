package org.firstinspires.ftc.teamcode.Lib.FTCLib;

public interface FTCRobotSubsystem {
    String getName();

    boolean isInitComplete();

    boolean init(Configuration config);

    void update();

    void shutdown();

    /*
     * This function can be called asynchronously with the rest of the program access to shared variables needs to be controller by either synchronized or explicit locking.
     * @param timerValueMsec current time in milliseconds
     */
    void timedUpdate(double timerValueMsec);

}
