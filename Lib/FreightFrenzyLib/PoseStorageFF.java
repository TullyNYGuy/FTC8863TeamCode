package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

public class PoseStorageFF {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object

    //public static AngleChanger angleChanger;

    public static Pose2d START_POSE = new Pose2d(-17.5, 63.75, Math.toRadians(270));
    //public static Pose2d START_POSE = new Pose2d(-61.25, -17, Math.toRadians(180));
    public static Pose2d TOP_PASSAGE_RED = new Pose2d(47, 0);
    public static Pose2d FREIGHT_RED =  new Pose2d(61, -61);
    public static Pose2d HUB_RED = new Pose2d(-11.75, -17.625);
    public static Pose2d DUCK_SPINNER_RED = new Pose2d(-58.75, -58.75);
    public static Pose2d STORAGE_RED = new Pose2d(-58.75, -35.25);
    public static Pose2d SIDE_PASSAGE_RED = new Pose2d(23.5, -64.5);
    public static Pose2d SHARED_HUB_RED = new Pose2d(46, 0);
    public static Pose2d HUB_BLUE = new Pose2d(-11.75, 17.625);
    public static Pose2d DUCK_SPINNER_BLUE = new Pose2d(-58.75, 58.75);
    public static Pose2d STORAGE_BLUE = new Pose2d(-58.75, 35.25);
    public static Pose2d TOP_PASSAGE_BLUE = new Pose2d(47, 0);
    public static Pose2d FREIGHT_BLUE = new Pose2d(61, 61);
    public static Pose2d SIDE_PASSAGE_BLUE = new Pose2d(23.5, 64.5);
    public static Pose2d SHARED_HUB_BLUE = new Pose2d(46, 0);
}
