package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/*
 * This Odometry system designed to be used with mecanum drive.
 * The idea is to split movement of the robot into rotational and translational.
 * The system is designed to work with three odometer modules with two of them
 * (left and right) set parallel to each other. The third module (back) is
 * perpendicular to the first two.
 * First the system calculates angle of rotation by cancelling translation motion from
 * the readings of the two parallel odometers. After the angle of rotation is found
 * the readings are adjusted by the number derived from the angle of rotation.
 * After that is adjusted parallel readings are averaged and that gives translation
 * motion along the length of the parallel odometers. The adjusted reading of
 * the back module gives translation across the length of the parallel odometers.
 */
public class OdometrySystem {
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

    private OdometryModule left;
    private OdometryModule right;
    private OdometryModule back;

    DcMotor.Direction leftDirection;
    DcMotor.Direction rightDirection;
    DcMotor.Direction backDirection;
    private double leftDirectionMultiplier = 1;
    private double rightDirectionMultiplier = 1;
    private double backDirectionMultiplier = 1;

    /*
     * Units of measurement for the rest of linear variables
     */
    private Units unit;

    /*
     * Distance from the center of the robot to the left odometer module
     * measured along the depth of the robot
     */

    private double leftOffsetDepth = 1.0;
    /*
     * Distance from the center of the robot to the left odometer module
     * measured along the width of the robot
     */
    private double leftOffsetWidth = 1.0;

    /*
     * Distance from the center of the robot to the right odometer module
     * measured along the depth of the robot
     */
    private double rightOffsetDepth = 1.0;

    /*
     * Distance from the center of the robot to the right odometer module
     * measured along the width of the robot
     */
    private double rightOffsetWidth = 1.0;

    /*
     * Distance from the center of the robot to the back odometer module
     * measured along the depth of the robot
     */

    private double backOffsetDepth = 1.0;
    /*
     * Distance from the center of the robot to the back odometer module
     * measured along the width of the robot
     */
    private double backOffsetWidth = 1.0;

    /*
     * Left odometer module distance from the center of the robot.
     * Equals to sqrt(leftOffsetDepth^2 + leftOffsetWidth^2)
     */
    private double leftModuleDistance;

    /*
     * Right odometer module distance from the center of the robot.
     * Equals to sqrt(rightOffsetDepth^2 + rightOffsetWidth^2)
     */
    private double rightModuleDistance;

    /*
     * Back odometer module distance from the center of the robot.
     * Equals to sqrt(backOffsetDepth^2 + backOffsetWidth^2)
     */
    private double backModuleDistance;

    /*
     * Left odometer module multiplier. Equals to
     * leftModuleDistance^2/leftOffsetWidth
     */
    private double leftMultiplier;

    /*
     * Right odometer module multiplier. Equals to
     * rightModuleDistance^2/rightOffsetWidth
     */
    private double rightMultiplier;

    /*
     * Back odometer module multiplier. Equals to
     * backModuleDistance^2/backOffsetDepth
     */
    private double backMultiplier;

    /*
     * Rotational multiplier. Equals to
     * 1/(leftModuleDistance^2/leftOffsetWidth + rightModuleDistance^2/rightOffsetWidth)
     */
    private double rotationalMultiplier;

    private double angleOfRotation = 0;

    private double translationDepth = 0;

    private double translationWidth = 0;

    private double angleOfTranslation = 0;

    private double lengthOfTranslation = 0;

    private double currentX = 0;
    private double currentY = 0;
    private double currentRotation = 0;

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
    public OdometrySystem(Units unit, OdometryModule left, OdometryModule right, OdometryModule back) {
        this.left = left;
        this.right = right;
        this.back = back;
        this.unit = unit;

    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void initializeRobotGeometry(
            double leftOffsetDepth, double leftOffsetWidth, DcMotor.Direction leftDirection,
            double rightOffsetDepth, double rightOffsetWidth, DcMotor.Direction rightDirection,
            double backOffsetDepth, double backOffsetWidth, DcMotor.Direction backDirection) {
        this.leftOffsetDepth = leftOffsetDepth;
        this.leftOffsetWidth = leftOffsetWidth;
        this.leftDirection = leftDirection;
        this.rightDirection = rightDirection;
        this.backDirection = backDirection;
        if(backDirection == DcMotor.Direction.FORWARD)
            backDirectionMultiplier = 1.0;
        else
            backDirectionMultiplier = -1.0;
        if(rightDirection == DcMotor.Direction.FORWARD)
            rightDirectionMultiplier = 1.0;
        else
            rightDirectionMultiplier = -1.0;
        if(leftDirection == DcMotor.Direction.FORWARD)
            leftDirectionMultiplier = 1.0;
        else
            leftDirectionMultiplier = -1.0;
        this.rightOffsetDepth = rightOffsetDepth;
        this.rightOffsetWidth = rightOffsetWidth;
        this.backOffsetDepth = backOffsetDepth;
        this.backOffsetWidth = backOffsetWidth;
        double leftModuleDistanceSq = leftOffsetDepth*leftOffsetDepth + leftOffsetWidth*leftOffsetWidth;
        double rightModuleDistanceSq = rightOffsetDepth*rightOffsetDepth + rightOffsetWidth*rightOffsetWidth;
        double backModuleDistanceSq = backOffsetDepth*backOffsetDepth + backOffsetWidth*backOffsetWidth;
        leftMultiplier = leftModuleDistanceSq/leftOffsetWidth;
        rightMultiplier = rightModuleDistanceSq/rightOffsetWidth;
        backMultiplier = backModuleDistanceSq/backOffsetDepth;
        rotationalMultiplier =  1.0 / (leftMultiplier + rightMultiplier);
    }

    public void calculateMoveDistance() {
        double leftEncoderValue = left.getDistanceSinceReset(unit)*leftDirectionMultiplier;
        double rightEncoderValue = right.getDistanceSinceReset(unit);
        double backEncoderValue = back.getDistanceSinceReset(unit);

        // calculate angle of rotation
        angleOfRotation = (leftEncoderValue - rightEncoderValue) * rotationalMultiplier;

        // adjust values by canceling rotation
        double leftVal = leftEncoderValue - angleOfRotation * leftMultiplier;
        double rightVal = rightEncoderValue + angleOfRotation * leftMultiplier;
        double backVal = backEncoderValue - angleOfRotation * leftMultiplier;

        translationDepth = (leftVal + rightVal) / 2.0;
        translationWidth = backVal;

        lengthOfTranslation = Math.sqrt(translationDepth * translationDepth + translationWidth * translationWidth);
        angleOfTranslation = Math.atan2(translationWidth, translationDepth);
    }

    public void getMovement(MecanumCommands data) {
        data.setAngleOfTranslation(angleOfTranslation);
        data.setSpeed(lengthOfTranslation);
        data.setSpeedOfRotation(angleOfRotation);
    }

    public void resetCoordinates() {
        currentX = 0.0;
        currentY = 0.0;
        currentRotation = 0.0;

    }

    public void setCoordinates(double rotation, double x, double y) {
        currentRotation = rotation;
        currentX = x;
        currentY = y;
    }

    public void updateCoordinates() {
        currentRotation = currentRotation + angleOfRotation;
        currentY = currentY + translationWidth;
        currentX = currentX + translationDepth;
    }

    public double getCurrentY() {
        return currentY;
    }

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentRotation() {
        return currentRotation;
    }
}
