package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;

public class PowerPlayPoseStorage {
    public static Pose2d START_POSE;

    // public static DATA FIELDS that persist between opmodes

    //***********************************************************************
    // RED LEFT POSES
    //***********************************************************************
    public static Pose2d RED_LEFT_START_POSE = new Pose2d(-36, -65, Math.toRadians(90));
    // public static Pose2d RED_LEFT_PARK_LOCATION_1 =
    // public static Pose2d RED_LEFT_PARK_LOCATION_2 =
    // public static Pose2d RED_LEFT_PARK_LOCATION_3 =

    //***********************************************************************
    // RED RIGHT POSES
    //***********************************************************************
    public static Pose2d RED_RIGHT_START_POSE = new Pose2d(36, -65, Math.toRadians(90));

    //***********************************************************************
    // BLUE LEFT POSES
    //***********************************************************************
    public static Pose2d BLUE_LEFT_START_POSE = new Pose2d(36, 65, Math.toRadians(270));

    //***********************************************************************
    // BLUE RIGHT POSES
    //***********************************************************************
    public static Pose2d BLUE_RIGHT_START_POSE = new Pose2d(-36, 65, Math.toRadians(270));
}
