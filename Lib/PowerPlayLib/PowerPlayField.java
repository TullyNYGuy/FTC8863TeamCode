package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalGoal;

public class PowerPlayField {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Pose2d parkingPoseLocation1;

    public Pose2d getParkingPoseLocation1() {
        return parkingPoseLocation1;
    }

    private Pose2d parkingPoseLocation2;

    public Pose2d getParkingPoseLocation2() {
        return parkingPoseLocation2;
    }

    private Pose2d parkingPoseLocation3;

    public Pose2d getParkingPoseLocation3() {
        return parkingPoseLocation3;
    }

    private Pose2d startPose;

    public Pose2d getStartPose() {
        return startPose;
    }

    private DistanceUnit units;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayField(AllianceColorTeamLocation.ColorLocation colorLocation) {
        // default field units will be inches
        this.units = DistanceUnit.INCH;
        this.startPose = determinestartPose2d(colorLocation);
        this.parkingPoseLocation1 = determineParkLocation1(colorLocation);
        this.parkingPoseLocation2 = determineParkLocation2(colorLocation);
        this.parkingPoseLocation3 = determineParkLocation3(colorLocation);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    // red left = -x -y, 90
    // red right = +x -y 90
    // blue left = +x +y 270
    // blue right = -x +y 270

    private Pose2d determinestartPose2d(AllianceColorTeamLocation.ColorLocation colorLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,Math.toRadians(0));
        switch (colorLocation) {
            case RED_LEFT:
                pose2dLocation = PowerPlayPoseStorage.RED_LEFT_START_POSE;
                break;
            case RED_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.RED_RIGHT_START_POSE;
                break;
            case BLUE_LEFT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_LEFT_START_POSE;
                break;
            case BLUE_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_RIGHT_START_POSE;
                break;
        }
        return pose2dLocation;
    }

    private Pose2d determineParkLocation1(AllianceColorTeamLocation.ColorLocation colorLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,0);
        switch (colorLocation) {
            case RED_LEFT:
                pose2dLocation = PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_1;
                break;
            case RED_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_1;
                break;
            case BLUE_LEFT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_LEFT_PARK_LOCATION_1;
                break;
            case BLUE_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_RIGHT_PARK_LOCATION_1;
                break;
        }
        return pose2dLocation;
    }

    private Pose2d determineParkLocation2(AllianceColorTeamLocation.ColorLocation colorLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,0);
        switch (colorLocation) {
            case RED_LEFT:
                pose2dLocation = PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_2;
                break;
            case RED_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_2;
                break;
            case BLUE_LEFT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_LEFT_PARK_LOCATION_2;
                break;
            case BLUE_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_RIGHT_PARK_LOCATION_2;
                break;
        }
        return pose2dLocation;
    }

    private Pose2d determineParkLocation3(AllianceColorTeamLocation.ColorLocation colorLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,0);
        switch (colorLocation) {
            case RED_LEFT:
                pose2dLocation = PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_3;
                break;
            case RED_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_3;
                break;
            case BLUE_LEFT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_LEFT_PARK_LOCATION_3;
                break;
            case BLUE_RIGHT:
                pose2dLocation = PowerPlayPoseStorage.BLUE_RIGHT_PARK_LOCATION_3;
                break;
        }
        return pose2dLocation;
    }


    /**
     * Returns a vector from a pose. The units are inches because a Pose2D units are inches.
     * @param pose2d
     * @return
     */
    public static Vector2d getVector2d(Pose2d pose2d) {
        return new Vector2d(pose2d.getX(), pose2d.getY());
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Calculate the distance between the robot an another object.
     * @param desiredUnits
     * @param robotPose
     * @param goalPose
     * @return distance in the units you specify
     */
    public double distanceTo(DistanceUnit desiredUnits, Pose2d robotPose, Pose2d goalPose) {
        Vector2d robotVector = getVector2d(robotPose);
        Vector2d goalVector = getVector2d(goalPose);
        return desiredUnits.fromInches(goalVector.distTo(robotVector)) ;
    }

    /**
     * Calculate the angle from the robot to another object.
     * @param desiredUnits
     * @param robotPose
     * @param goalPose
     * @return angle in the units you specify
     */
    public double angleTo(AngleUnit desiredUnits, Pose2d robotPose, Pose2d goalPose) {
        Vector2d robotVector = getVector2d(robotPose);
        Vector2d goalVector = getVector2d(goalPose);
        Vector2d vectorDif = goalVector.minus(robotVector);
        return desiredUnits.fromRadians(Math.atan(vectorDif.getY() / vectorDif.getX()));
    }
}
