package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AutonomousFunctions;

public class TestAutonomousInterface implements AutonomousFunctions {
SkystoneRobot robot;
int level;
TestAutonomousInterface(SkystoneRobot robot, int level){
    this.robot = robot;
    this.level = level;

}

    @Override
    public AutonomousStatus getStatus() {
return AutonomousStatus.COMPLETE;
    }

    @Override
    public void update() {

    }

    @Override
    public void start() {

    }

}
