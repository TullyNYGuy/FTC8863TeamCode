package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

public class PoseStorageFF {
    public static Pose2d START_POSE;

    // public static DATA FIELDS that persist between opmodes

    //***********************************************************************
    // BLUE WALL SIDE
    //***********************************************************************
    public static Pose2d BLUE_WALL_START_POSE = new Pose2d(-37.25, 62.6, Math.toRadians(0));

    public static Pose2d DUCK_SPINNER_BLUE = new Pose2d(-58.5, 57.5, Math.toRadians(90));

    public static Pose2d WAYPOINT_BLUE_HUB = new Pose2d(-56.32, 11.8, Math.toRadians(104));

    public static Pose2d DELIVER_TO_LOW_BLUE_WALL = new Pose2d(-39.46, 8.03, Math.toRadians(104));
    public static Pose2d DELIVER_TO_MID_BLUE_WALL = new Pose2d(-43.35, 8.06, Math.toRadians(104));
    public static Pose2d DELIVER_TO_TOP_BLUE_WALL = new Pose2d(-52.3, 4.8, Math.toRadians(104));

    public static Pose2d STORAGE_BLUE = new Pose2d(-60.75, 37.25, Math.toRadians(0));

    //***********************************************************************
    // BLUE WAREHOUSE SIDE
    //***********************************************************************
    public static Pose2d BLUE_WAREHOUSE_START_POSE = new Pose2d(9.20, 62.6, Math.toRadians(0));

    public static Pose2d DELIVER_TO_LOW_BLUE_WAREHOUSE = new Pose2d(-.20, 45.76, Math.toRadians(328));
    public static Pose2d DELIVER_TO_MID_BLUE_WAREHOUSE = new Pose2d(1.92, 49.15, Math.toRadians(328));
    public static Pose2d DELIVER_TO_TOP_BLUE_WAREHOUSE = new Pose2d(7.88, 58.69, Math.toRadians(328));

    public static Pose2d BLUE_SIDE_PASSAGE_APPROACH = new Pose2d(-20, 60, Math.toRadians(0));
    public static Pose2d BLUE_ENTRY_TO_WAREHOUSE_WAYPOINT = new Pose2d(11.20, 63.6, Math.toRadians(0));
    public static Pose2d BLUE_WAREHOUSE_PARK_WAYPOINT = new Pose2d(38, 63.6, Math.toRadians(0));
    public static Pose2d BLUE_WAREHOUSE_PARK = new Pose2d(44, 42, Math.toRadians(45));
    public static Pose2d SIDE_PASSAGE_BLUE = new Pose2d(8.75, 67, Math.toRadians(0));

    public static Pose2d FREIGHT_BLUE = new Pose2d(60, 67, Math.toRadians(0));

    //***********************************************************************
    // RED WALL SIDE
    //***********************************************************************
    public static Pose2d RED_WALL_START_POSE = new Pose2d(-33.5, -62.6, Math.toRadians(0));

    public static Pose2d DUCK_SPINNER_RED = new Pose2d(-57.75, -56.5, Math.toRadians(180));

    public static Pose2d WAYPOINT_RED_HUB = new Pose2d(-56.32, -11.8, Math.toRadians(256));

    public static Pose2d DELIVER_TO_LOW_RED_WALL = new Pose2d(-32.61, -16.24, Math.toRadians(256));
    public static Pose2d DELIVER_TO_MID_RED_WALL = new Pose2d(-37.46, -15.03, Math.toRadians(256));
    public static Pose2d DELIVER_TO_TOP_RED_WALL = new Pose2d(-48.62, -12.25, Math.toRadians(256));

    public static Pose2d WAYPOINT_RED_PARK = new Pose2d(-56.32, -16.8, Math.toRadians(256));

    public static Pose2d STORAGE_RED = new Pose2d(-58.5, -35.25); // <- need test

    //***********************************************************************
    // RED WAREHOUSE SIDE
    //***********************************************************************
    public static Pose2d RED_WAREHOUSE_START_POSE = new Pose2d(12, -62.6, Math.toRadians(0));

    public static Pose2d DELIVER_TO_LOW_RED_WAREHOUSE = new Pose2d(-.2, -45.76, Math.toRadians(32));
    public static Pose2d DELIVER_TO_MID_RED_WAREHOUSE = new Pose2d(1.92, -49.15, Math.toRadians(32));
    public static Pose2d DELIVER_TO_TOP_RED_WAREHOUSE = new Pose2d(7.88, -58.69, Math.toRadians(32));

    public static Pose2d RED_SIDE_PASSAGE_APPROACH = new Pose2d(-20, -60, Math.toRadians(0));
    public static Pose2d SIDE_PASSAGE_RED = new Pose2d(8.75, -68, Math.toRadians(0));
    public static Pose2d RED_ENTRY_TO_WAREHOUSE_WAYPOINT = new Pose2d(11.20, -63.6, Math.toRadians(0));
    public static Pose2d RED_WAREHOUSE_PARK_WAYPOINT = new Pose2d(38, -63.6, Math.toRadians(0));
    public static Pose2d RED_WAREHOUSE_PARK = new Pose2d(44, -42, Math.toRadians(315));

    public static Pose2d FREIGHT_RED = new Pose2d(60, -65, Math.toRadians(0));

    //***********************************************************************
    // SHARED HUB
    //***********************************************************************

    public static Pose2d SHARED_HUB_BLUE = new Pose2d(63, 8.5, Math.toRadians(270.0));
    public static Pose2d SHARED_HUB_RED = new Pose2d(63, -8.5, Math.toRadians(-270.0)); // <- need test

    //***********************************************************************
    // UNKNOWN
    //***********************************************************************

    public static Pose2d TOP_PASSAGE_RED = new Pose2d(65, 0); // <- need test
    public static Pose2d TOP_PASSAGE_BLUE = new Pose2d(65, 0); // <- need test

    public static Pose2d HUB_BLUE_INTAKE_DUMP = new Pose2d(-12, 49, Math.toRadians(270));
    public static Pose2d HUB_RED_INTAKE_DUMP = new Pose2d(-12, -49, Math.toRadians(90));

    public static void retreiveStartPose(){
        START_POSE = PersistantStorage.getStartPosition();

    }
}
