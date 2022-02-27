package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes
    public static boolean isDeliveryFull;
    private static Pose2d startPostion;
    private static Pose2d highBlue;
    private static Pose2d highRed;
    private static Pose2d midBlue;
    private static Pose2d midRed;
    private static Pose2d lowBlue;
    private static Pose2d lowRed;
    private static Integer motorTicks;
    private static AllianceColor allianceColor;
    private static FreightFrenzyStartSpot startSpot;
    private static ShippingElementPipeline.ShippingPosition positionOfElement;

    public static FreightFrenzyStartSpot getStartSpot(){
        return startSpot;
    }

    public static Pose2d getStartPosition() {
        return startPostion;
    }
    public static void setAllianceColor(AllianceColor inputColor){
        allianceColor = inputColor;
    }
    public static AllianceColor getAllianceColor(){
        return allianceColor;
    }
    public static void setShippingElementPosition(ShippingElementPipeline.ShippingPosition elementPalce){
        positionOfElement =  elementPalce;
    }

    public static ShippingElementPipeline.ShippingPosition getShippingElementPosition(){
        return positionOfElement;
    }

    public static void setStartSpot(FreightFrenzyStartSpot inputStart) {
        startSpot = inputStart;
        switch(startSpot){
            //TODO: adjust all the start positions to match reality
            case RED_WALL:
                startPostion = PoseStorageFF.RED_WALL_START_POSE;
                setAllianceColor(AllianceColor.RED);
                break;
            case BLUE_WALL:
                startPostion = PoseStorageFF.BLUE_WALL_START_POSE;
                setAllianceColor(AllianceColor.BLUE);
                break;
            case RED_WAREHOUSE:
                startPostion = PoseStorageFF.RED_WAREHOUSE_START_POSE;
                setAllianceColor(AllianceColor.RED);
                break;
            case BLUE_WAREHOUSE:
                startPostion = PoseStorageFF.BLUE_WAREHOUSE_START_POSE;
                setAllianceColor(AllianceColor.BLUE);
                break;
        }
    }

    public static int getMotorTicks (){
        if (motorTicks == null){
            motorTicks = new Integer(0);
        }
        return motorTicks;
    }

    public static void setMotorTicks(int motorTicks){
        PersistantStorage.motorTicks = motorTicks;
    }





    public static Pose2d robotPose;


}
