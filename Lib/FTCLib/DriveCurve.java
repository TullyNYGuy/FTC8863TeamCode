package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class DriveCurve {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum Direction {
        CCW,
        CW
    }

    public enum CurveState {
        NOT_STARTED,
        TURNING,
        COMPLETE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private CurveState curveState = CurveState.NOT_STARTED;

    public CurveState getCurveState() {
        return curveState;
    }

    private Direction curveDirection;

    private double curveAngle;

    public double getCurveAngle() {
        return curveAngle;
    }

    public void setCurveAngle(double curveAngle) {
        if (curveAngle >= 0) {
            curveDirection = Direction.CCW;
            // limit the curve to less than 360
            curveAngle = Range.clip(curveAngle, 0, 360);
        } else {
            curveDirection = Direction.CW;
            // limit the curve to 360 degrees or less
            curveAngle = Range.clip(curveAngle, -360, 0);
        }
        this.curveAngle = curveAngle;
    }

    private double speed;

    private double radius;

    private double wheelBase;

    private double leftWheelSpeed;

    public double getLeftWheelSpeed() {
        return leftWheelSpeed;
    }

    private double rightWheelSpeed;

    public double getRightWheelSpeed() {
        return rightWheelSpeed;
    }

    private double lastDistance = 0;
    private double heading = 0;
    private double lastHeading = 0;

    /**
     * The curve will be called complete when it is between the desired angle (curveAngle) -
     * curveThreshold and desired angle + threshold. Default is within a 2 degree range, or +/- 1
     * degree
     */
    private double curveThreshold = 1;

    public double getCurveThreshold() {
        return curveThreshold;
    }

    public void setCurveThreshold(double curveThreshold) {
        this.curveThreshold = curveThreshold;
    }

    private double rateOfTurn = 0;

    private AdafruitIMU8863 imu;
    private DriveTrain driveTrain;
    private ElapsedTime timer;
    private DataLogging logFile = null;

    private boolean enableLogging = false;

    public boolean isLoggingEnabled() {
        return enableLogging;
    }

    public void enableLogging() {
        if (logFile != null) {
            this.enableLogging = true;
        } else {
            this.enableLogging = false;
        }
    }

    public void disableLogging() {
        this.enableLogging = false;
    }

    private PIDControl pidControl;
    private boolean usePID = false;
    private double correctionSign = -1;

    public void enablePID() {
        usePID = true;
    }

    public void disablePID() {
        usePID = false;
    }

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

    public DriveCurve(double curveAngle, double speed, double radius, double wheelBase, AdafruitIMU8863 imu, DataLogging logFile, DriveTrain driveTrain) {
        this.logFile = logFile;
        enableLogging();
        this.driveTrain = driveTrain;
        init(curveAngle, speed, radius, wheelBase, imu);
    }

    public DriveCurve(double curveAngle, double speed, double radius, double wheelBase, AdafruitIMU8863 imu) {
        this.logFile = null;
        enableLogging = false;
        init(curveAngle, speed, radius, wheelBase, imu);
    }

    private void init(double curveAngle, double speed, double radius, double wheelBase, AdafruitIMU8863 imu) {
        this.imu = imu;
        this.wheelBase = wheelBase;
        setCurveAngle(curveAngle);
        this.speed = speed;
        this.radius = radius;

        timer = new ElapsedTime();
        timer.reset();

        curveState = CurveState.NOT_STARTED;

        rateOfTurn = calculateRateOfTurn(radius);

        pidControl = new PIDControl();
        pidControl.setSetpoint(rateOfTurn);
        pidControl.setMaxCorrection(radius * .2);
        // threshold is not meaningful in this movement. It is normally used to say when the
        // movement is complete but since this movement goes forever, threshold does nothing.
        // But it has to be set to something!
        pidControl.setThreshold(10);
        //pidControl.setKp(0.011);
        pidControl.setKp(40.0);
        //pidControl.setKi(0.05/1000000);
        pidControl.reset();

        calculateWheelSpeeds(radius);
        driveTrain.setDistanceDrivenReference();
        lastDistance = imu.getHeading();

        if (logFile != null && enableLogging) {
            logFile.logData("Curve radius = " + radius + " speed = " + speed + " angle = " + curveAngle + " left wheel speed = " + leftWheelSpeed + " right wheel speed = " + rightWheelSpeed);
            logFile.logData("rate of turn should be = " + Double.toString(calculateRateOfTurn(radius)));
        }
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void calculateWheelSpeeds(double radius) {
        if (radius == 0) {
            leftWheelSpeed = speed;
            rightWheelSpeed = speed;
        } else {
            // these are for forward movement
            switch (curveDirection) {
                case CW:
                    // outside wheel is the left
                    leftWheelSpeed = speed * (1 + wheelBase / (2 * radius));
                    // inside wheel is the right
                    rightWheelSpeed = speed * (1 - wheelBase / (2 * radius));
                    correctionSign = +1.0;
                    break;
                case CCW:
                    // inside wheel is the left
                    leftWheelSpeed = speed * (1 - wheelBase / (2 * radius));
                    // outside wheel is the right
                    rightWheelSpeed = speed * (1 + wheelBase / (2 * radius));
                    correctionSign = +1.0;
                    break;
            }
        }

    }

    private double calculateRateOfTurn(double radius) {
        double rateOfTurn = 0;
        switch (curveDirection) {
            // CCW is a postive change in heading
            case CCW:
                rateOfTurn = 360 / (2 * Math.PI * radius);
                break;
            // CW is a negitive change in heading
            case CW:
                rateOfTurn = -360 / (2 * Math.PI * radius);
                break;
        }
        return rateOfTurn;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public boolean update() {
        boolean returnValue = false;
        double currentHeading;
        double newRadius = this.radius;
        double newLeftWheelSpeed = leftWheelSpeed;
        double newRightWheelSpeed = rightWheelSpeed;
        double currentRateOfTurn = 0;
        double correction = 0;

        // The first time this update() is run, the curve will be started and the robot will be
        // turning
        switch (curveState) {
            case NOT_STARTED:
                curveState = CurveState.TURNING;
                returnValue = false;
                break;
            case TURNING:
                currentHeading = imu.getHeading();
                currentRateOfTurn = getRateOfTurn(currentHeading);
                if (usePID) {
                    if (currentRateOfTurn == 0) {
                        correction = 0;
                    } else {
                        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
                    }
                    newRadius = radius + correction;
                    logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
                    calculateWheelSpeeds(newRadius);
                    driveTrain.setLeftDriveMotorSpeed(leftWheelSpeed);
                    driveTrain.setRightDriveMotorSpeed(rightWheelSpeed);
                    logFile.logData(leftWheelSpeed, rightWheelSpeed);
                    driveTrain.applyPowersToMotors();
                }

                if (logFile != null && enableLogging) {
                    driveTrain.updateDriveDistance();
                    logFile.logData(Double.toString(currentHeading), Double.toString(driveTrain.getDistanceDriven()), Double.toString(currentRateOfTurn));
                }
                // if the current heading is close enough to the desired heading indicate the turn is done
                if (Math.abs(currentHeading) > Math.abs(curveAngle) - curveThreshold && Math.abs(currentHeading) < Math.abs(curveAngle) + curveThreshold) {
                    // curve is complete
                    if (logFile != null && enableLogging) {
                        driveTrain.updateDriveDistance();
                        logFile.logData("final heading = " + Double.toString(currentHeading) + " distance driven = ", Double.toString(driveTrain.getDistanceDriven()));
                        logFile.logData("average rate of turn = " + Double.toString(currentHeading / driveTrain.getDistanceDriven()));
                    }
                    curveState = CurveState.COMPLETE;
                }
                returnValue = false;
                break;
            case COMPLETE:
                returnValue = true;
                break;
        }
        return returnValue;
    }

    private double getRateOfTurn(double currentHeading) {
        double distanceDrivenSinceLast = driveTrain.getDistanceDrivenSinceLast();
        double headingChange = currentHeading - lastHeading;
        lastHeading = currentHeading;
        if (distanceDrivenSinceLast != 0){
            return headingChange / distanceDrivenSinceLast;
        } else {
            return 0;
        }

    }
}
