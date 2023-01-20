package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.Pipelines.SignalConePipeline;

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
    /**
     * Parking location 1 given the alliance color and team location
     */
    private Pose2d parkingPoseLocation1;

    public Pose2d getParkingPoseLocation1() {
        return parkingPoseLocation1;
    }

    /**
     * Parking location 2 given the alliance color and team location
     */
    private Pose2d parkingPoseLocation2;

    public Pose2d getParkingPoseLocation2() {
        return parkingPoseLocation2;
    }

    /**
     * Parking location 3 given the alliance color and team location
     */
    private Pose2d parkingPoseLocation3;

    public Pose2d getParkingPoseLocation3() {
        return parkingPoseLocation3;
    }

    private Pose2d startPose;

    public Pose2d getStartPose() {
        return startPose;
    }

    private Pose2d junctionPolePose;

    public Pose2d getJunctionPolePose() {
        return junctionPolePose;
    }

    /**
     * Parking location given the signal cone indication. The 3 possible parking locations based on
     * the alliance color and team location have already upon the field creation because we already
     * know the alliance color and team location at that time. However, we do not know the signal
     * cone indication so that has to take place later.
     */
    private Pose2d parkingLocationPose;

    public Pose2d getParkingLocationPose() {
        return parkingLocationPose;
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
        determinePosesGivenColorLocation(colorLocation);
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

    private void determinePosesGivenColorLocation(AllianceColorTeamLocation.ColorLocation colorLocation) {
        switch (colorLocation) {
            case RED_LEFT:
                startPose = PowerPlayPoseStorage.RED_LEFT_START_POSE;
                junctionPolePose = PowerPlayPoseStorage.RED_LEFT_JUNCTION_POLE_LOCATION;
                parkingPoseLocation1 = PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_1;
                parkingPoseLocation2 = PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_2;
                parkingPoseLocation3 = PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_3;
                break;
            case RED_RIGHT:
                startPose = PowerPlayPoseStorage.RED_RIGHT_START_POSE;
                junctionPolePose = PowerPlayPoseStorage.RED_RIGHT_JUNCTION_POLE_LOCATION;
                parkingPoseLocation1 = PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_1;
                parkingPoseLocation2 = PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_2;
                parkingPoseLocation3 = PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_3;
                break;
            case BLUE_LEFT:
                startPose = PowerPlayPoseStorage.BLUE_LEFT_START_POSE;
                junctionPolePose = PowerPlayPoseStorage.BLUE_LEFT_JUNCTION_POLE_LOCATION;
                parkingPoseLocation1 = PowerPlayPoseStorage.BLUE_LEFT_PARK_LOCATION_1;
                parkingPoseLocation2 = PowerPlayPoseStorage.BLUE_LEFT_PARK_LOCATION_2;
                parkingPoseLocation3 = PowerPlayPoseStorage.BLUE_LEFT_PARK_LOCATION_3;
                break;
            case BLUE_RIGHT:
                startPose = PowerPlayPoseStorage.BLUE_RIGHT_START_POSE;
                junctionPolePose = PowerPlayPoseStorage.BLUE_RIGHT_JUNCTION_POLE_LOCATION;
                parkingPoseLocation1 = PowerPlayPoseStorage.BLUE_RIGHT_PARK_LOCATION_1;
                parkingPoseLocation2 = PowerPlayPoseStorage.BLUE_RIGHT_PARK_LOCATION_2;
                parkingPoseLocation3 = PowerPlayPoseStorage.BLUE_RIGHT_PARK_LOCATION_3;
                break;
        }
    }

    /**
     * Once the signal cone color is determined, we can figure out what the corresponding parking
     * location is. The 3 possible parking locations were already determined based on the alliance
     * color and the team location. We just need to figure out which of the 3 is the one to park in.
     * if cone color is green, park location is location 1
     * if cone color is blue, park location is location 2
     * if cone color is red, park location is location 3
     * @param coneColor
     * @return
     */
    public Pose2d getParkLocation(SignalConePipeline.ConeColor coneColor) {
        // default park location is location 1
        Pose2d parkingPose = parkingPoseLocation1;
        return parkingPose;
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
