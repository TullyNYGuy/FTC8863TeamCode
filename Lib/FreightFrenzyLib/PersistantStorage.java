package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    private static Integer motorTicks;
    private static FreightFrenzyColor color;

    public static FreightFrenzyColor getColor(){
        return color;
    }

    public static void setColor(FreightFrenzyColor colorInput) {
        color = colorInput;
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
