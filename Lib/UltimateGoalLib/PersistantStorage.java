package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object
    public static int getMotorTicks (){
        if (motorTicks == null){
            motorTicks = new Integer(0);
        }
        return motorTicks;
    }
    public static void setMotorTicks(int motorTicks){
        PersistantStorage.motorTicks = motorTicks;
    }
    public static double getShooterAngle(AngleUnit units) {
        if (shooterAngle==null){
            shooterAngle=new Double(0);
        }
        return units.fromRadians(shooterAngle);
    }

    public static void setShooterAngle(double shooterAngle, AngleUnit units) {
        PersistantStorage.shooterAngle = AngleUnit.RADIANS.fromUnit(units, shooterAngle);
    }
    private static Integer motorTicks;
    private static Double shooterAngle;

    public static Pose2d robotPose;
}
