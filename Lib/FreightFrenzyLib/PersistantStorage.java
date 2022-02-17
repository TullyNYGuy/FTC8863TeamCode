package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes
    public static boolean isDeliveryFull;
    private static Pose2d startPostion;
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
                startPostion = new Pose2d(-35.25, -62.6, Math.toRadians(0));
                setAllianceColor(AllianceColor.RED);
                break;
            case BLUE_WALL:startPostion = new Pose2d(-29.75, 62.6, Math.toRadians(0));
                setAllianceColor(AllianceColor.BLUE);
                break;
            case RED_WAREHOUSE:startPostion = new Pose2d(10.75, -62.6, Math.toRadians(0));
                setAllianceColor(AllianceColor.RED);
                break;
            case BLUE_WAREHOUSE:startPostion = new Pose2d(17, 62.6, Math.toRadians(0));
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
