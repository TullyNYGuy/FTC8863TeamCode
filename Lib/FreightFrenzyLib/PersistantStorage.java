package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    private static Integer motorTicks;
    private static FreightFrenzyStartSpot startSpot;

    public static FreightFrenzyStartSpot getStartSpot(){
        return startSpot;
    }

    public static void setStartSpot(FreightFrenzyStartSpot inputStart) {
        startSpot = inputStart;
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
