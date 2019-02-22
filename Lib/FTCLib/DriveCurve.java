package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class DriveCurve {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum CurveDirection {
        CCW,
        CW
    }

    public enum CurveState {
        NOT_STARTED,
        TURNING,
        COMPLETE
    }

    public enum  DriveDirection{
        FORWARD,
        BACKWARD
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

    private CurveDirection curveDirection;

    public CurveDirection getCurveDirection() {
        return curveDirection;
    }

    public void setCurveDirection(CurveDirection curveDirection) {
        this.curveDirection = curveDirection;
    }

    private DriveDirection driveDirection;

    public DriveDirection getDriveDirection() {
        return driveDirection;
    }

    public void setDriveDirection(DriveDirection driveDirection) {
        this.driveDirection = driveDirection;
    }

    private double curveAngle;

    public double getCurveAngle() {
        return curveAngle;
    }

    public void setCurveAngle(double curveAngle) {
        if (curveAngle >= 0) {
            curveDirection = CurveDirection.CCW;
            // limit the curve to less than 360
            curveAngle = Range.clip(curveAngle, 0, 360);
        } else {
            curveDirection = CurveDirection.CW;
            // limit the curve to 360 degrees or less
            curveAngle = Range.clip(curveAngle, -360, 0);
        }
        this.curveAngle = curveAngle;
    }

    private double speed;

    /**
     * Forwards speed is a positive motor value. Backwards speed is a negative motor value. This
     * method makes sure that the speed has the correct sign based on the direction the robot is
     * supposed to move.
     * @param speed
     */
    private void setSpeed(Double speed) {
        speed = Math.abs(speed);
        switch (driveDirection) {
            case FORWARD:
                this.speed = speed;
                break;
            case BACKWARD:
                this.speed = -speed;
                break;
        }
    }

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
    private double initialHeading = 0;
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

    public DriveCurve(double curveAngle, double speed, double radius, CurveDirection curveDirection, DriveDirection driveDirection, Double wheelBase, AdafruitIMU8863 imu, DataLogging logFile, DriveTrain driveTrain) {
        this.logFile = logFile;
        enableLogging();
        this.driveTrain = driveTrain;
        init(curveAngle, speed, radius, curveDirection, driveDirection, wheelBase, imu);
    }

    public DriveCurve(double curveAngle, double speed, double radius, CurveDirection curveDirection, DriveDirection driveDirection, double wheelBase, AdafruitIMU8863 imu) {
        this.logFile = null;
        enableLogging = false;
        init(curveAngle, speed, radius, curveDirection, driveDirection, wheelBase, imu);
    }

    private void init(double curveAngle, double speed, double radius, CurveDirection curveDirection, DriveDirection driveDirection, double wheelBase, AdafruitIMU8863 imu) {
        setCurveAngle(curveAngle);
        this.curveDirection = curveDirection;
        this.driveDirection = driveDirection;
        setSpeed(speed);
        this.radius = radius;
        this.wheelBase = wheelBase;
        this.imu = imu;

        timer = new ElapsedTime();
        timer.reset();

        curveState = CurveState.NOT_STARTED;

        rateOfTurn = calculateRateOfTurnShouldBe(radius);

        pidControl = new PIDControl();
        pidControl.setSetpoint(rateOfTurn);
        // was .2
        pidControl.setMaxCorrection(radius * 2);
        // threshold is not meaningful in this movement. It is normally used to say when the
        // movement is complete but since this movement goes forever, threshold does nothing.
        // But it has to be set to something!
        pidControl.setThreshold(10);
        //pidControl.setKp(0.011);
        // was 40
        pidControl.setKp(100.0);
        //pidControl.setKi(0.05/1000000);
        pidControl.reset();

        calculateWheelSpeeds(radius);
        driveTrain.setDistanceDrivenReference();
        initialHeading = imu.getHeading();
        lastHeading = initialHeading;

        // bug here was that the motor mode did not get set and was taking on whatever it was last set to.
        driveTrain.setDriveMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (logFile != null && enableLogging) {
            logFile.logData("Curve radius = " + radius + " speed = " + speed + " angle = " + curveAngle + " left wheel speed = " + leftWheelSpeed + " right wheel speed = " + rightWheelSpeed);
            logFile.logData("rate of turn should be = " + Double.toString(calculateRateOfTurnShouldBe(radius)));
            logFile.logData("distance traveled should be = " + Double.toString(2 * Math.PI * radius * curveAngle / 360));
        }
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Figure out what the right and left wheel speeds should be in order to follow a curve with a
     * certain radius. This must be run before a new curve is started. It is automatically run when
     * the constructor is run.
     * @param radius
     */
    private void calculateWheelSpeeds(double radius) {
        if (radius == 0) {
            leftWheelSpeed = speed;
            rightWheelSpeed = speed;
        } else {
            switch(driveDirection) {
                case FORWARD:
                    // these are for forward movement
                    switch (curveDirection) {
                        case CW:
                            // outside wheel is the left
                            leftWheelSpeed = calculateFasterWheelSpeed(radius);
                            // inside wheel is the right
                            rightWheelSpeed = calculateSlowerWheelSpeed(radius);
                            correctionSign = +1.0;
                            break;
                        case CCW:
                            // inside wheel is the left
                            leftWheelSpeed = calculateSlowerWheelSpeed(radius);
                            // outside wheel is the right
                            rightWheelSpeed = calculateFasterWheelSpeed(radius);
                            correctionSign = +1.0;
                            break;
                    }
                    break;
                case BACKWARD:
                    // these are for backward movement
                    switch (curveDirection) {
                        case CW:
                            // inside wheel is the left
                            leftWheelSpeed = calculateSlowerWheelSpeed(radius);
                            // outside wheel is the right
                            rightWheelSpeed = calculateFasterWheelSpeed(radius);
                            correctionSign = +1.0;
                            break;
                        case CCW:
                            // outside wheel is the left
                            leftWheelSpeed = calculateFasterWheelSpeed(radius);
                            // inside wheel is the right
                            rightWheelSpeed = calculateSlowerWheelSpeed(radius);
                            correctionSign = +1.0;
                            break;
                    }
                    break;
            }

        }

    }

    /**
     * Calculate the wheel speed for the wheel that follows the inner radius - the shorter lane
     * around the race track. This is the wheel the must turn more slowly.
     * @param radius
     * @return
     */
    private double calculateSlowerWheelSpeed(Double radius) {
        double wheelSpeed = speed * (1 - wheelBase / (2 * radius));
        return wheelSpeed;
    }

    /**
     * Calculate the wheel speed for the wheel that follows the outer radius - the longer lane
     * around the race track. This is the wheel the must turn more quickly.
     * @param radius
     * @return
     */
    private double calculateFasterWheelSpeed(Double radius) {
        double wheelSpeed = speed * (1 + wheelBase / (2 * radius));
        return wheelSpeed;
    }

    /**
     * Calculate the rate of turn. This is the change in heading / change in distance traveled
     * around the circle. This should be a value that does not change since the radius of the
     * circle does not change.
     * @param radius
     * @return
     */
    private double calculateRateOfTurnShouldBe(double radius) {
        double rateOfTurn = 360 / (2 * Math.PI * radius);
        switch (curveDirection) {
            // CCW is a postive change in heading
            case CCW:
                rateOfTurn = +rateOfTurn;
                break;
            // CW is a negitive change in heading
            case CW:
                rateOfTurn = -rateOfTurn;
                break;
        }
        return rateOfTurn;
    }

    /**
     * Calculate the actual rate of turn. This is the change in heading since the last time the
     * heading was read / the change in distance traveled since the last distance was read. This
     * value should be equal to the calculateRateOfTurnShouldBe() but it won't because stuff
     * happens.
     * @param currentHeading
     * @return the rate of turn in the last little instant of time
     */
    private double getActualRateOfTurn(double currentHeading) {
        double distanceDrivenSinceLast = driveTrain.getDistanceDrivenSinceLast();
        double headingChange = currentHeading - lastHeading;
        // setup for the next time this calculation is made
        lastHeading = currentHeading;
        // make sure there is not a divide by 0
        if (distanceDrivenSinceLast != 0){
            return headingChange / distanceDrivenSinceLast;
        } else {
            return 0;
        }
    }

    private double getEffectiveCurveRadius(double finalHeading) {
        // circumferance = 2 * pi * r
        // r = circumference / (2 * pi)
        // circumference = distance driven * 360 / change in heading
        return driveTrain.getDistanceDriven() * 360 / (finalHeading - initialHeading) * 1 / (2 * Math.PI);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************


    /**
     * Once a curve has been created, use this method to actually start the curve. After this, you
     * need to call the update() in a loop so that the angle can be looked at as the curve proceeds.
     */
    public void startCurve() {
        driveTrain.setLeftDriveMotorSpeed(getLeftWheelSpeed());
        driveTrain.setRightDriveMotorSpeed(getRightWheelSpeed());
        driveTrain.applyPowersToMotors();
        update();
    }

    /**
     * After the curve has been started with startCurve(), this method must be called until
     * isCurveComplete() returns true.
     * @return alternate method of telling when the curve is complete (true)
     */
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
                currentRateOfTurn = getActualRateOfTurn(currentHeading);

                // the PID control uses the rate of turn per distance traveled. This should be a constant
                // since the robot is traveling around a circle of a certain radius. But it might not
                // be since stuff happens. The PID is supposed to fix that.
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

                // log the distance driven
                if (logFile != null && enableLogging) {
                    driveTrain.updateDriveDistance();
                    logFile.logData(Double.toString(currentHeading), Double.toString(driveTrain.getDistanceDriven()), Double.toString(currentRateOfTurn));
                }
                // if the current heading is close enough to the desired heading indicate the turn is done
                if (Math.abs(currentHeading) > Math.abs(curveAngle) - curveThreshold && Math.abs(currentHeading) < Math.abs(curveAngle) + curveThreshold) {
                    // curve is complete, log the results
                    if (logFile != null && enableLogging) {
                        driveTrain.updateDriveDistance();
                        logFile.logData("final heading = " + Double.toString(currentHeading) + " distance driven = ", Double.toString(driveTrain.getDistanceDriven()));
                        logFile.logData("average rate of turn = " + Double.toString(currentHeading / driveTrain.getDistanceDriven()));
                        logFile.logData("effective curve radius = " + Double.toString(getEffectiveCurveRadius(currentHeading)));
                        logFile.blankLine();
                    }
                    // set the next state to complete
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

    /**
     * When the curve is complete, this method returns true.
     * @return
     */
    public boolean isCurveComplete(){
        if (curveState == CurveState.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Stop the curve and set the motor to either float or hold. Most of the time I think you will
     * want to float.
     * @param finishBehavior
     */
    public void stopCurve(DcMotor8863.FinishBehavior finishBehavior) {
        driveTrain.setFinishBehavior(finishBehavior);
        driveTrain.shutdown();
    }
}
