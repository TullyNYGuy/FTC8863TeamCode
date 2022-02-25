package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

public class PoseStorageFF {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object

    //public static AngleChanger angleChanger;

    public static Pose2d START_POSE;
    //public static Pose2d START_POSE = new Pose2d(-61.25, -17, Math.toRadians(180));
    public static Pose2d DELIVER_TO_LOW_RED_WALL = new Pose2d();
    public static Pose2d DELIVER_TO_LOW_RED_WAREHOUSE= new Pose2d();
    public static Pose2d DELIVER_TO_LOW_BLUE_WALL= new Pose2d(-39.75,21.75, Math.toRadians(90));
    public static Pose2d DELIVER_TO_LOW_BLUE_WAREHOUSE= new Pose2d(3.75,46,Math.toRadians(315));
    public static Pose2d DELIVER_TO_MID_RED_WALL= new Pose2d(-41.5,-20, Math.toRadians(374));
    public static Pose2d DELIVER_TO_MID_RED_WAREHOUSE = new Pose2d(9,-49, Math.toRadians(51));
    public static Pose2d DELIVER_TO_MID_BLUE_WALL = new Pose2d(-41.5,16.75, Math.toRadians(104));
    public static Pose2d DELIVER_TO_MID_BLUE_WAREHOUSE= new Pose2d(5.75,49, Math.toRadians(321.4));
    public static Pose2d DELIVER_TO_HIGH_HUB_RED_WALL= new Pose2d();
    public static Pose2d DELIVER_TO_HIGH_HUB_RED_WAREHOUSE= new Pose2d(-13.75, -63.75, Math.toRadians(0));
    public static Pose2d DELIVER_TO_HIGH_HUB_BLUE_WALL = new Pose2d();
    public static Pose2d DELIVER_TO_HIGH_HUB_BLUE_WAREHOUSE= new Pose2d(-13.75,63.75, Math.toRadians(0));
    public static Pose2d DELIVER_TO_HIGH_HUB_BLUE_STORAGE= new Pose2d();
    public static Pose2d DELIVER_TO_HIGH_HUB_RED_STORAGE= new Pose2d();
    public static Pose2d TOP_PASSAGE_RED = new Pose2d(65, 0); // <- need test
    public static Pose2d FREIGHT_BLUE =  new Pose2d(60, 67, Math.toRadians(0));
    public static Pose2d HUB_BLUE_INTAKE_DUMP = new Pose2d(-12, 49,Math.toRadians(270));
    public static Pose2d DUCK_SPINNER_BLUE = new Pose2d(-58.5,57.5,Math.toRadians(90));
    public static Pose2d STORAGE_RED = new Pose2d(-58.75, -35.25); // <- need test
    public static Pose2d BLUE_SIDE_PASSAGE_APPROACH = new Pose2d(-20,60,Math.toRadians(0));
    public static Pose2d RED_SIDE_PASSAGE_APPROACH = new Pose2d(-20,-60,Math.toRadians(0));
    public static Pose2d SIDE_PASSAGE_BLUE = new Pose2d(8.75,67,Math.toRadians(0));
    public static Pose2d SHARED_HUB_RED = new Pose2d(63, -8.5, Math.toRadians(-270.0)); // <- need test
    public static Pose2d HUB_RED_INTAKE_DUMP = new Pose2d(-12, -49,Math.toRadians(90));
    public static Pose2d DUCK_SPINNER_RED = new Pose2d(-59,56,Math.toRadians(90));
    public static Pose2d STORAGE_BLUE = new Pose2d(-58.75, 35.25); // <- need test
    public static Pose2d TOP_PASSAGE_BLUE = new Pose2d(65, 0); // <- need test
    public static Pose2d FREIGHT_RED = new Pose2d(60, -65, Math.toRadians(0));
    public static Pose2d SIDE_PASSAGE_RED = new Pose2d(8.75,-68,Math.toRadians(0));
    public static Pose2d SHARED_HUB_BLUE = new Pose2d(63, 8.5,Math.toRadians(270.0));
    public static Pose2d WAYPOINT_BLUE_HUB = new Pose2d(-50,20,0);
    public static Pose2d WAYPOINT_RED_HUB = new Pose2d(-50,-20,0);

    public static void retreiveStartPose(){
        START_POSE = PersistantStorage.getStartPosition();

    }
}
