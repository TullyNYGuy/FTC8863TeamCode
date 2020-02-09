package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public interface FTCRobot {


    boolean createRobot();

    void init();

    boolean isInitComplete();

    void update();

    void shutdown();

    void timedUpdate(double timerValueMsec);

    double getCurrentRotation(AngleUnit unit);
}
