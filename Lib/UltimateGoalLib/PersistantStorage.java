package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.security.interfaces.DSAPublicKey;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object

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
