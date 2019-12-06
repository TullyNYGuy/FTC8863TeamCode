package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MasterOdometry {
    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
 /*
    public enum Units{
        CM,
        IN
    }

*/

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private OdometryModule left;
    private OdometryModule right;
    private OdometryModule back;
    private double currentPositionX;
    private double currentPositionY;
    private double currentPositionRotation;
    private double backDistance;
    private double otherYDistance;
    private double horizontalDistance;
    private double timeTracker;
    private Units units;


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************


    public MasterOdometry(OdometryModule left, OdometryModule right, OdometryModule back) {
        this.left = left;
        this.right = right;
        this.back = back;
        timeTracker = 0;
        currentPositionX = 0;
        currentPositionY = 0;
        currentPositionRotation = 0;
    }

    public void resetter() {
        left.resetEncoderValue();
        right.resetEncoderValue();
        back.resetEncoderValue();
    }

    public double getPostionX() {

        return currentPositionX;
    }

    public double getPostionY() {

        return currentPositionY;
    }

    public double getPostionRotation() {

        return currentPositionRotation;
    }

    public double getTime() {

        return timeTracker;
    }

    public void calculatePosition() {
        //MATH
        double centreDiametre;
        double leftDiametre;
        double rightDiametre;
        double changeOrintation;
        double L = horizontalDistance * 2;

        double PAngle;
        // nums are temp. change to real
        double rotChange = 0;
        double wholeAngle = 0;
        leftDiametre = left.getDistanceSinceLastChange(Units.CM);
        rightDiametre = right.getDistanceSinceLastChange(Units.CM);
        centreDiametre = (leftDiametre + rightDiametre) / 2;
        changeOrintation = (leftDiametre - rightDiametre) / L;
        currentPositionX += centreDiametre * Math.sin(wholeAngle);
        currentPositionY += centreDiametre * Math.sin(wholeAngle);
        currentPositionRotation += wholeAngle * rotChange;

    }

}

