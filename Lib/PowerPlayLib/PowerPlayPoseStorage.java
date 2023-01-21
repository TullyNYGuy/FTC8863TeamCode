package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;

public class PowerPlayPoseStorage {
    public static Pose2d START_POSE;

    // public static DATA FIELDS that persist between opmodes
    // These constant definitions can be copied into the MeepMeepTesting class to easily try out
    // various paths in meep meep

    //***********************************************************************
    // RED LEFT POSES -x -y, 90
    //***********************************************************************
    public static Pose2d RED_LEFT_START_POSE = new Pose2d(-40.5, -63.5, Math.toRadians(90));
    public static Pose2d RED_LEFT_PARK_LOCATION_1 = new Pose2d(-58.75, -11.75, Math.toRadians(90));
    public static Pose2d RED_LEFT_PARK_LOCATION_2 = new Pose2d(-35.25, -11.75, Math.toRadians(90));
    public static Pose2d RED_LEFT_PARK_LOCATION_3 = new Pose2d(-11.75,-23.5 , Math.toRadians(90));
    public static Pose2d RED_LEFT_JUNCTION_POLE_LOCATION = new Pose2d(-11.75, -23.5, Math.toRadians(90));
    public static Pose2d RED_LEFT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
    //***********************************************************************
    // RED RIGHT POSES +x -y 90
    //***********************************************************************
    public static Pose2d RED_RIGHT_START_POSE = new Pose2d(30, -63.5, Math.toRadians(90));
    public static Pose2d RED_RIGHT_PARK_LOCATION_1 = new Pose2d(11.75, -23.5, Math.toRadians(90));
    public static Pose2d RED_RIGHT_PARK_LOCATION_2 = new Pose2d(35.25, -23.5, Math.toRadians(90));
    public static Pose2d RED_RIGHT_PARK_LOCATION_3 = new Pose2d(58.75, -11.75, Math.toRadians(270));
    public static Pose2d RED_RIGHT_JUNCTION_POLE_LOCATION = new Pose2d(11.75, -23.5, Math.toRadians(90));
    public static Pose2d RED_RIGHT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
    //***********************************************************************
    // BLUE LEFT POSES +x +y 270
    //***********************************************************************
    public static Pose2d BLUE_LEFT_START_POSE = new Pose2d(30, 63.5, Math.toRadians(270));
    public static Pose2d BLUE_LEFT_PARK_LOCATION_1 = new Pose2d(0, 0, Math.toRadians(0));
    public static Pose2d BLUE_LEFT_PARK_LOCATION_2 = new Pose2d(+35.25, +35.25, Math.toRadians(90));
    public static Pose2d BLUE_LEFT_PARK_LOCATION_3 = new Pose2d(0, 0, Math.toRadians(0));
    public static Pose2d BLUE_LEFT_JUNCTION_POLE_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
    public static Pose2d BLUE_LEFT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));

    //***********************************************************************
    // BLUE RIGHT POSES -x +y 270
    //***********************************************************************
    public static Pose2d BLUE_RIGHT_START_POSE = new Pose2d(-30, 63.5, Math.toRadians(270));
    public static Pose2d BLUE_RIGHT_PARK_LOCATION_1 = new Pose2d(0, 0, Math.toRadians(0));
    public static Pose2d BLUE_RIGHT_PARK_LOCATION_2 = new Pose2d(-35.25, +35.25, Math.toRadians(90));
    public static Pose2d BLUE_RIGHT_PARK_LOCATION_3 = new Pose2d(0, 0, Math.toRadians(0));
    public static Pose2d BLUE_RIGHT_JUNCTION_POLE_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
    public static Pose2d BLUE_RIGHT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
}
