package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;

// This annotation tells the FTC Dashboard that any public static variable should be put into the
// Dashboard and made available to the user for tweaking.
// The @Config annotation tells the FTC Dashboard to look in this class for public static constants
// and make them available on the Dashboard. I'm not sure how they will be represented since they
// are objects in and of themselves but let's see. 
@Config
public class PoseStorageAutonomousPositionsDemo {
    public static Pose2d START_POSE = new Pose2d(-61.25, -17, Math.toRadians(180));
    public static Pose2d DELIVER_TO_MID_AND_LOW_HUB_BLUE = new Pose2d(-12, 29, Math.toRadians(0));
    public static Pose2d DUCK_SPINNER_BLUE = new Pose2d(-59,57.5,Math.toRadians(90));
}
