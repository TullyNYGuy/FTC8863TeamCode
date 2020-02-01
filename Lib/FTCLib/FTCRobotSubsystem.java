package org.firstinspires.ftc.teamcode.Lib.FTCLib;

public interface FTCRobotSubsystem {
    String getName();

    boolean isInitComplete();

    boolean init(Configuration config);

    void update();

    void shutdown();
}
