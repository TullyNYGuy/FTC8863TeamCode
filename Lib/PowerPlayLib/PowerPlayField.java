package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
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
    public PowerPlayField(AllianceColor allianceColor, TeamLocation teamLocation) {
        // default field units will be inches
        units = DistanceUnit.INCH;
        startPose = determinestartPose2d(allianceColor, teamLocation);
        parkingPoseLocation1 = determineParkLocation1(allianceColor, teamLocation);
        parkingPoseLocation2 = determineParkLocation2(allianceColor, teamLocation);
        parkingPoseLocation3 = determineParkLocation3(allianceColor, teamLocation);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    // red left = -x -y
    // red right = +x -y
    // blue left = +x +y
    // blue right = -x +y

    public Pose2d determinestartPose2d(AllianceColor allianceColor, TeamLocation teamLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,Math.toRadians(0));
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(-36, -65, Math.toRadians(0));
        }
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(36, -65, Math.toRadians(0));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(36, 65, Math.toRadians(0));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(-36, 65, Math.toRadians(0));
        }
        return pose2dLocation;
    }

    public Pose2d determineParkLocation1(AllianceColor allianceColor, TeamLocation teamLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,0);
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        return pose2dLocation;
    }

    public Pose2d determineParkLocation2(AllianceColor allianceColor, TeamLocation teamLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,0);
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        return pose2dLocation;
    }

    public Pose2d determineParkLocation3(AllianceColor allianceColor, TeamLocation teamLocation) {
        Pose2d pose2dLocation = new Pose2d(0,0,0);
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.LEFT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.RIGHT) {
            pose2dLocation = new Pose2d(24, 24, Math.toRadians(270));
        }
        return pose2dLocation;
    }


    /**
     * Returns a vector from a pose. The units are inches because a Pose2D units are inches.
     * @param pose2d
     * @return
     */
    private Vector2d getVector(Pose2d pose2d) {
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
        Vector2d robotVector = getVector(robotPose);
        Vector2d goalVector = getVector(goalPose);
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
        Vector2d robotVector = getVector(robotPose);
        Vector2d goalVector = getVector(goalPose);
        Vector2d vectorDif = goalVector.minus(robotVector);
        return desiredUnits.fromRadians(Math.atan(vectorDif.getY() / vectorDif.getX()));
    }
}
