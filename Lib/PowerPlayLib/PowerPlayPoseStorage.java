package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.checkerframework.checker.index.qual.PolyUpperBound;
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
    public static Pose2d RED_LEFT_PARK_LOCATION_1 = new Pose2d(-11.75, -23.5, Math.toRadians(270));
    public static Pose2d RED_LEFT_PARK_LOCATION_2 = new Pose2d(-35.25, -11.75, Math.toRadians(270));
    public static Pose2d RED_LEFT_PARK_LOCATION_3 = new Pose2d(-58.75,-14.75 , Math.toRadians(270));
    //public static Pose2d RED_LEFT_JUNCTION_POLE_LOCATION = new Pose2d(-11.75, -23.5, Math.toRadians(270));
    public static Pose2d RED_LEFT_JUNCTION_POLE_LOCATION = new Pose2d(-14.75, -22, Math.toRadians(270));
    public static Pose2d RED_LEFT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
    //***********************************************************************
    // RED RIGHT POSES +x -y 90
    //***********************************************************************
    public static Pose2d RED_RIGHT_START_POSE = new Pose2d(30, -63.5, Math.toRadians(90));
    public static Pose2d RED_RIGHT_PARK_LOCATION_1 = new Pose2d(11.75, -23.5, Math.toRadians(90));
    public static Pose2d RED_RIGHT_PARK_LOCATION_2 = new Pose2d(35.25, -23.5, Math.toRadians(90));
    //public static Pose2d RED_RIGHT_PARK_LOCATION_3 = new Pose2d(58.75, -11.75, Math.toRadians(90));
    public static Pose2d RED_RIGHT_PARK_LOCATION_3 = new Pose2d(55.75, -9.75, Math.toRadians(85));
    // in theory
    //public static Pose2d RED_RIGHT_JUNCTION_POLE_LOCATION = new Pose2d(11.75, -23.5, Math.toRadians(90));
    public static double RED_RIGHT_START_DUAL_SENSORS_BEFORE_POLE_DISTANCE = -5;
    public static double RED_RIGHT_STOP_TRAJECTORY_AFTER_POLE_DISTANCE = 5;
    public static Pose2d RED_RIGHT_START_DUAL_SENSORS_LOCATION = new Pose2d(10.75, -25.5 + RED_RIGHT_START_DUAL_SENSORS_BEFORE_POLE_DISTANCE, Math.toRadians(90));
    public static Pose2d RED_RIGHT_STOP_JUNCTION_POLE_TRAJECTORY_LOCATION = new Pose2d(10.75, -25.5 + RED_RIGHT_STOP_TRAJECTORY_AFTER_POLE_DISTANCE, Math.toRadians(90));

    public static Pose2d RED_RIGHT_JUNCTION_POLE_LOCATION = new Pose2d(10.75, -25.5, Math.toRadians(90));
    public static Pose2d RED_RIGHT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
    //***********************************************************************
    // BLUE LEFT POSES +x +y 270
    //***********************************************************************
    public static Pose2d BLUE_LEFT_START_POSE = new Pose2d(-40.5, -63.5, Math.toRadians(90));
    public static Pose2d BLUE_LEFT_PARK_LOCATION_1 = new Pose2d(-11.75, -23.5, Math.toRadians(270));
    public static Pose2d BLUE_LEFT_PARK_LOCATION_2 = new Pose2d(-35.25, -11.75, Math.toRadians(270));
    public static Pose2d BLUE_LEFT_PARK_LOCATION_3 = new Pose2d(-58.75,-14.75 , Math.toRadians(270));
    //public static Pose2d BLUE_LEFT_JUNCTION_POLE_LOCATION = new Pose2d(-11.75, -23.5, Math.toRadians(270));
    public static Pose2d BLUE_LEFT_JUNCTION_POLE_LOCATION = new Pose2d(-14.75, -22, Math.toRadians(270));
    public static Pose2d BLUE_LEFT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));

    //***********************************************************************
    // BLUE RIGHT POSES -x +y 270
    //***********************************************************************
    public static Pose2d BLUE_RIGHT_START_POSE = new Pose2d(30, -63.5, Math.toRadians(90));
    public static Pose2d BLUE_RIGHT_PARK_LOCATION_1 = new Pose2d(11.75, -23.5, Math.toRadians(90));
    public static Pose2d BLUE_RIGHT_PARK_LOCATION_2 = new Pose2d(35.25, -23.5, Math.toRadians(90));
    //public static Pose2d BLUE_RIGHT_PARK_LOCATION_3 = new Pose2d(58.75, -11.75, Math.toRadians(90));
    public static Pose2d BLUE_RIGHT_PARK_LOCATION_3 = new Pose2d(55.75, -9.75, Math.toRadians(85));
    // in theory
    //public static Pose2d BLUE_RIGHT_JUNCTION_POLE_LOCATION = new Pose2d(11.75, -23.5, Math.toRadians(90));
    public static Pose2d BLUE_RIGHT_JUNCTION_POLE_LOCATION = new Pose2d(10.75, -25.5, Math.toRadians(90));
    public static Pose2d BLUE_RIGHT_SIGNAL_CONE_PICKUP_LOCATION = new Pose2d(0, 0, Math.toRadians(0));
}
