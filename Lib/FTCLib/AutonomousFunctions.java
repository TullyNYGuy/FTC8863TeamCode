package org.firstinspires.ftc.teamcode.Lib.FTCLib;

public interface AutonomousFunctions {
    public enum AutonomousStatus {
        INPROGRESS, COMPLETE, FAILED
    }

    AutonomousStatus getStatus();
    void update();
    void start();
}
