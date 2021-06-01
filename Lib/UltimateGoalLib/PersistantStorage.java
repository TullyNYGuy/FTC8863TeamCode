package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import java.security.interfaces.DSAPublicKey;

public class PersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    // angle changer object

    public static double getShooterAngle() {
        if (shooterAngle==null){
            shooterAngle=new Double(0);

        }
        return shooterAngle;
    }

    public static void setShooterAngle(double shooterAngle) {
        PersistantStorage.shooterAngle = shooterAngle;
    }

    private static Double shooterAngle;
    public static Pose2d robotPose;
}
