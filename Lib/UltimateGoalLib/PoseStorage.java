package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class PoseStorage {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object

    //public static AngleChanger angleChanger;

    public static Pose2d START_POSE = new Pose2d(-61.25, -17, Math.toRadians(180));
    public static Pose2d SHOOTING_AT_LEFT_POWER_SHOT_POSE = new Pose2d(0, 15.5, Math.toRadians(180));
    public static Pose2d SHOOTING_AT_MIDDLE_POWER_SHOT_POSE = new Pose2d(0, 7, Math.toRadians(180));
    public static Pose2d SHOOTING_AT_RIGHT_POWER_SHOT_POSE = new Pose2d(0, -.25, Math.toRadians(180));
    public static Pose2d PARK_POSE = new Pose2d(15, -18.9, Math.toRadians(180));
}
