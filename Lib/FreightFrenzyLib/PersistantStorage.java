package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    private static Pose2d startPostion;
    private static Integer motorTicks;
    private static FreightFrenzyStartSpot startSpot;
    private static ShippingElementPipeline.ShippingPosition positionOfElement;

    public static FreightFrenzyStartSpot getStartSpot(){
        return startSpot;
    }

    public static Pose2d getStartPosition() {
        return startPostion;
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
            case RED_WALL:
                startPostion = new Pose2d(-17.5, -63.75, Math.toRadians(0));
                break;
            case BLUE_WALL:startPostion = new Pose2d(-17.5, 63.75, Math.toRadians(0));
                break;
            case RED_WAREHOUSE:startPostion = new Pose2d(12.5, -63.75, Math.toRadians(0));
                break;
            case BLUE_WAREHOUSE:startPostion = new Pose2d(12.5, 63.75, Math.toRadians(0));
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
