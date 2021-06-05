package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    private static Integer motorTicks;

    public static int getMotorTicks (){
        if (motorTicks == null){
            motorTicks = new Integer(0);
        }
        return motorTicks;
    }
    public static void setMotorTicks(int motorTicks){
        PersistantStorage.motorTicks = motorTicks;
    }

    private static Double shooterAngle;

    public static double getShooterAngle(AngleUnit units) {
        if (shooterAngle==null){
            shooterAngle=new Double(0);
        }
        return units.fromRadians(shooterAngle);
    }

    public static void setShooterAngle(double shooterAngle, AngleUnit units) {
        PersistantStorage.shooterAngle = AngleUnit.RADIANS.fromUnit(units, shooterAngle);
    }

    public static Pose2d robotPose;

    private final static double HIGH_GOAL_SHOOTER_ANGLE = 31.0;
    public static double getHighGoalShooterAngle() {
        return HIGH_GOAL_SHOOTER_ANGLE;
    }

    private final static double POWER_SHOT_SHOOTER_ANGLE = 25.0;
    public static double getPowerShotShooterAngle() {
        return POWER_SHOT_SHOOTER_ANGLE;
    }
}
