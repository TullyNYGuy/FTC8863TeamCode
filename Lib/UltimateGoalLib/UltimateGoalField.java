package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class UltimateGoalField {

    //         top = new UltimateGoalGoal(35.5 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));
//         powerShotLeft = new UltimateGoalGoal(30 * 0.0254, new Pose2d(71 * 0.0254, 20 * 0.0254, 0));
//         powerShotMid = new UltimateGoalGoal(30 * 0.0254, new Pose2d(71 * 0.0254, 12 * 0.0254, 0));
//         powerShotRight = new UltimateGoalGoal(30 * 0.0254, new Pose2d(71 * 0.0254, 4.25 * 0.0254, 0));
//         middle = new UltimateGoalGoal(26 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));
//         bottom = new UltimateGoalGoal(16.5 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));

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
    public UltimateGoalGoal topGoal;
    public UltimateGoalGoal powerShotLeft;
    public UltimateGoalGoal powerShotMid;
    public UltimateGoalGoal powerShotRight;
    public UltimateGoalGoal middleGoal;
    public UltimateGoalGoal bottomGoal;
    public DistanceUnit units;

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
    public UltimateGoalField() {
        // default field units will be inches
        units = DistanceUnit.INCH;

        // create the objects in the field
        topGoal = new UltimateGoalGoal(DistanceUnit.INCH, 35.5, new Pose2d(71, -12, 0));
        powerShotLeft = new UltimateGoalGoal(DistanceUnit.INCH, 30, new Pose2d(71, 20, 0));
        powerShotMid = new UltimateGoalGoal(DistanceUnit.INCH, 30, new Pose2d(71, 12, 0));
        powerShotRight = new UltimateGoalGoal(DistanceUnit.INCH, 30, new Pose2d(71, 4.25, 0));
        middleGoal = new UltimateGoalGoal(DistanceUnit.INCH, 26, new Pose2d(71, -12, 0));
        bottomGoal = new UltimateGoalGoal(DistanceUnit.INCH, 16.5, new Pose2d(71, -12, 0));
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

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
