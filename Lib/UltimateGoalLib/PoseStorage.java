package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class PoseStorage {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object

    //public static AngleChanger angleChanger;

    public static Pose2d START_POSE = new Pose2d(-61.25, 6.5, Math.toRadians(180));
    //public static Pose2d START_POSE = new Pose2d(-61.25, -17, Math.toRadians(180));
    public static Pose2d SHOOTING_AT_LEFT_POWER_SHOT_POSE = new Pose2d(0, 15.5, Math.toRadians(180));
    public static Pose2d SHOOTING_AT_MIDDLE_POWER_SHOT_POSE = new Pose2d(0, 7, Math.toRadians(180));
    public static Pose2d SHOOTING_AT_RIGHT_POWER_SHOT_POSE = new Pose2d(0, -.25, Math.toRadians(180));
    public static Pose2d SHOOTING_AT_HIGH_GOAL = new Pose2d(0, -17, Math.toRadians(180));
    public static Pose2d PARK_POSE = new Pose2d(15, -18.9, Math.toRadians(180));
    public static Pose2d ZERO_POSE = new Pose2d(0, 0, Math.toRadians(180));
    public static Pose2d HIGH_GOAL_WAY_POINT = new Pose2d(-23, 5, Math.toRadians(180.0));
    public static Pose2d PICKUP_POSE = new Pose2d(-10.5, -11, Math.toRadians(180.0));
    public static Pose2d PICKUP_POSE_FIRST_RING = new Pose2d(-14, -11, Math.toRadians(180.0));
    public static Pose2d PICKUP_POSE_SECOND_RING = new Pose2d(-15.5, -11, Math.toRadians(180.0));
    public static Pose2d PICKUP_POSE_THIRD_RING = new Pose2d(-17, -11, Math.toRadians(180.0));
}
