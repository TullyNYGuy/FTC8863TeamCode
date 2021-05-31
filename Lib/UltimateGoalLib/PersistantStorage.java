package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object

    //public static AngleChanger angleChanger;
    public static AngleUnit angleUnit = AngleUnit.RADIANS;
    public static Double shooterAngle = null;
    public static Integer angleChangerMotorEncoderCount = null;

    public static Pose2d robotPose = null;
}
