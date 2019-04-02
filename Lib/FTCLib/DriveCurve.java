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

    /**
     * wheelbase of 6 wheel drive robot in cm
     */
    private double wheelBase = 37.38;

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
    private double initialDistance = 0;

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

    private int loopCount = 0;

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

    public double getWheelBase() {
        return wheelBase;
    }

    public void setWheelBase(double wheelBase) {
        this.wheelBase = wheelBase;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * Create a drive curve object and setup the curve. You will need to call
     * Note that you cannot run a curve that has a radius less than 1/2 the wheelbase. That would be
     * a spin turn in which one wheel turns backwards and one forwards. DriveCurve is meant to move
     * the robot forwards. So if you try to do that, the drive curve will limit you to a pivot curve
     * where one wheel is stationary and the other is moving. If the spin turn were allowed the PID
     * could suddenly make a wheel that is going forwards switch to going backwards. This would not
     * be good.
     * @param curveAngle
     * @param speed
     * @param radius
     * @param curveDirection
     * @param driveDirection
     * @param imu
     * @param logFile
     * @param driveTrain
     */
    public DriveCurve(double curveAngle, double speed, double radius, CurveDirection curveDirection, DriveDirection driveDirection, AdafruitIMU8863 imu, DataLogging logFile, DriveTrain driveTrain) {
        this.logFile = logFile;
        enableLogging();
        this.driveTrain = driveTrain;
        init(curveAngle, speed, radius, curveDirection, driveDirection, imu);
    }

    public DriveCurve(double curveAngle, double speed, double radius, CurveDirection curveDirection, DriveDirection driveDirection, AdafruitIMU8863 imu) {
        this.logFile = null;
        enableLogging = false;
        init(curveAngle, speed, radius, curveDirection, driveDirection, imu);
    }

    private void init(double curveAngle, double speed, double radius, CurveDirection curveDirection, DriveDirection driveDirection, AdafruitIMU8863 imu) {
        this.imu = imu;

        pidControl = new PIDControl();
        // threshold is not really meaningful when controlling something that continues on and on
        // but it has to be set to something
        pidControl.setThreshold(10);
        // was 40
        pidControl.setKp(100.0);
        //pidControl.setKi(0.05/1000000);

        timer = new ElapsedTime();

        setupDriveCurve(curveAngle, speed, radius, curveDirection, driveDirection);
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
                            correctionSign = -1.0;
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
                            correctionSign = -1.0;
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
        double wheelSpeed;
        // when the radius of the turn becomes less than 1/2 of the wheelbase the robot starts to do
        // a spin turn where the slower wheel has to turn backwards. We don't want this because it
        // stops the forward movement of the robot so limit the wheel speed to 0 and no less.
        if (radius <= wheelBase / 2) {
            wheelSpeed = 0;
        } else {
            wheelSpeed = speed * (1 - wheelBase / (2 * radius));
        }
        return wheelSpeed;
    }

    /**
     * Calculate the wheel speed for the wheel that follows the outer radius - the longer lane
     * around the race track. This is the wheel the must turn more quickly.
     * @param radius
     * @return
     */
    private double calculateFasterWheelSpeed(Double radius) {
        double wheelSpeed;
        // when the radius of the turn becomes less than 1/2 of the wheelbase the robot starts to do
        // a spin turn where the slower wheel has to turn backwards while the faster wheel turns
        // forwards. We don't want this because it
        // stops the forward movement of the robot. So limit the slower wheel speed to 0. It can't
        // be negative. At the same time, when the slower wheel = 0, the faster wheel = 2 * speed:
        /// speed * (1 + wheelbase / (2* radius))
        // radius = wheelbase / 2;
        // speed * (1+ wheelbase / (2 * wheelbase / 2)) = speed * (1+ wheelbase / wheelbase) = speed * (1 + 1) = speed * 2
        if (radius <= wheelBase / 2) {
            wheelSpeed = 2 * speed;
        } else {
            wheelSpeed = speed * (1 + wheelBase / (2 * radius));
        }
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
        double distanceDrivenSinceLast = Math.abs(driveTrain.getDistanceDrivenSinceLast());
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

    private double getEffectiveCurveRadius(double headingChange, double distanceDriven) {
        // circumferance = 2 * pi * r
        // r = circumference / (2 * pi)
        // circumference = distance driven * 360 / change in heading
        // r = distance driven *360 / ((change in heading) * 2 * pi)
        return (distanceDriven * 360) / (headingChange * 2 * Math.PI);
    }

    private double getDistanceToBeTraveled() {
        double distance = 2 * Math.PI * radius * curveAngle / 360;
        if (driveDirection == DriveDirection.BACKWARD) {
            distance = -distance;
        }
        return distance;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * You can use the constructor to setup a curve. Once you run that first curve, you can use this
     * method to setup another curve rather than creating another curve object.
     * Note that you cannot run a curve that has a radius less than 1/2 the wheelbase. That would be
     * a spin turn in which one wheel turns backwards and one forwards. DriveCurve is meant to move
     * the robot forwards. So if you try to do that, the drive curve will limit you to a pivot curve
     * where one wheel is stationary and the other is moving. If the spin turn were allowed the PID
     * could suddenly make a wheel that is going forwards switch to going backwards. This would not
     * be good.
     * @param curveAngle
     * @param speed this should always be positive. The driveDirection takes care of driving backwards.
     * @param radius
     * @param curveDirection
     * @param driveDirection
     */
    public void setupDriveCurve(double curveAngle, double speed, double radius, CurveDirection curveDirection, DriveDirection driveDirection) {
        setCurveAngle(curveAngle);
        this.curveDirection = curveDirection;
        this.driveDirection = driveDirection;
        setSpeed(speed);
        this.radius = radius;

        curveState = CurveState.NOT_STARTED;

        rateOfTurn = calculateRateOfTurnShouldBe(radius);

        pidControl.setSetpoint(rateOfTurn);
        pidControl.setMaxCorrection(radius * 2);
        pidControl.reset();

        calculateWheelSpeeds(radius);

        // reset loopCount and timer so they can be used to determine the average loop time at the end of the curve
        loopCount = 0;
        timer.reset();

        // bug here was that the motor mode did not get set and was taking on whatever it was last set to.
        driveTrain.setDriveMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (logFile != null && enableLogging) {
            logFile.logData("CURVE DESIRED radius = " + radius + " speed = " + speed + " angle = " + curveAngle + " curve_direction = " + curveDirection.toString() + " drive_direction = " + driveDirection.toString());
            logFile.logData("left wheel speed = " + leftWheelSpeed + " right wheel speed = " + rightWheelSpeed + " rate of turn should be = " + Double.toString(calculateRateOfTurnShouldBe(radius)) + " distance traveled should be = " + Double.toString(getDistanceToBeTraveled()));
        }
    }

    /**
     * Once a curve has been created, use this method to actually start the curve. After this, you
     * need to call the update() in a loop so that the angle can be looked at as the curve proceeds.
     */
    public void startDriveCurve() {
        initialHeading = imu.getHeading();
        initialDistance = driveTrain.updateDistanceDriven();
        if (logFile != null && enableLogging) {
            logFile.logData("CURVE INITIAL_HEADING_DISTANCE", initialHeading, initialDistance);
        }
        lastHeading = initialHeading;
        driveTrain.setDistanceDrivenReference();
        driveTrain.setLeftDriveMotorSpeed(getLeftWheelSpeed());
        driveTrain.setRightDriveMotorSpeed(getRightWheelSpeed());
        driveTrain.applyPowersToMotors();
        update();
    }

    /**
     * After the curve has been started with startCurve(), this method must be called until
     * isCurveComplete() returns true. Loop time on this is about 31 mSec.
     * @return alternate method of telling when the curve is complete (true)
     */
    public boolean update() {
        boolean returnValue = false;
        double currentHeading;
        double newRadius = this.radius;
        double currentRateOfTurn = 0;
        double correction = 0;

        // The first time this update() is run, the curve will be started and the robot will be
        // turning
        switch (curveState) {
            case NOT_STARTED:
                timer.reset();
                curveState = CurveState.TURNING;
                returnValue = false;
                break;
            case TURNING:
                loopCount++;
                currentHeading = imu.getHeading();
                currentRateOfTurn = getActualRateOfTurn(currentHeading);

                // the PID control uses the rate of turn per distance traveled. This should be a constant
                // since the robot is traveling around a circle of a certain radius. But it might not
                // be since stuff happens. The PID is supposed to fix that.
                // commented out to save loop time
                //if (usePID) {
//                    if (currentRateOfTurn == 0) {
//                        correction = 0;
//                    } else {
//                        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
//                    }
                    correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
                    newRadius = radius + correction;
                    // commented out to see if loop time can be improved
                    //logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
                    calculateWheelSpeeds(newRadius);
                    driveTrain.setLeftDriveMotorSpeed(leftWheelSpeed);
                    driveTrain.setRightDriveMotorSpeed(rightWheelSpeed);
                    // commented out to see if loop time can be improved
                    //logFile.logData(leftWheelSpeed, rightWheelSpeed);
                    driveTrain.applyPowersToMotors();
                //}

                // log the distance driven
                // commented out to see if loop time can be improved
//                if (logFile != null && enableLogging) {
//                    driveTrain.updateDriveDistance();
                    logFile.logData("CURVE HEADING_DISTANCE_RATE", currentHeading, driveTrain.getDistanceDriven(), currentRateOfTurn);
//                }
                // if the current heading is close enough to the desired heading indicate the turn is done
                if (Math.abs(currentHeading) > Math.abs(curveAngle) - curveThreshold && Math.abs(currentHeading) < Math.abs(curveAngle) + curveThreshold) {
                    // curve is complete, log the results
                    if (logFile != null && enableLogging) {
                        // commented out to save loop time since I don't think it is needed
                        //driveTrain.updateDriveDistance();
                        double distanceDriven = driveTrain.getDistanceDriven()- initialDistance;
                        double headingChange = currentHeading - initialHeading;
                        logFile.logData("CURVE FINAL_HEADING_DISTANCE", currentHeading, driveTrain.getDistanceDriven());
                        logFile.logData("distance driven during this movement = " + distanceDriven);
                        logFile.logData("average rate of turn = " + Double.toString(headingChange / distanceDriven));
                        logFile.logData("effective curve radius = " + Double.toString(getEffectiveCurveRadius(headingChange, distanceDriven)));
                        logFile.logData("Average loop time = " + Double.toString(timer.milliseconds() / loopCount));
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
     * After the curve has been started with startCurve(), this method must be called until
     * isCurveComplete() returns true. It does not use any PID and does not log until end.
     * Loop time was 13 mSec.
     * @return alternate method of telling when the curve is complete (true)
     */
    public boolean updatefastest() {
        boolean returnValue = false;
        double currentHeading;
        double newRadius = this.radius;
        double currentRateOfTurn = 0;
        double correction = 0;

        // The first time this update() is run, the curve will be started and the robot will be
        // turning
        switch (curveState) {
            case NOT_STARTED:
                loopCount++;
                curveState = CurveState.TURNING;
                returnValue = false;
                break;
            case TURNING:
                loopCount++;
                currentHeading = driveTrain.imu.getHeading();
                // if the current heading is close enough to the desired heading indicate the turn is done
                if (Math.abs(currentHeading) > Math.abs(curveAngle) - curveThreshold && Math.abs(currentHeading) < Math.abs(curveAngle) + curveThreshold) {
                    // curve is complete, log the results
                    if (logFile != null && enableLogging) {
                        driveTrain.updateDriveDistance();
                        double distanceDriven = driveTrain.getDistanceDriven()- initialDistance;
                        double headingChange = currentHeading - initialHeading;
                        logFile.logData("heading at curve completion = " + Double.toString(currentHeading) + " distance driven = ", Double.toString(distanceDriven));
                        logFile.logData("average rate of turn = " + Double.toString(headingChange / distanceDriven));
                        logFile.logData("effective curve radius = " + Double.toString(getEffectiveCurveRadius(headingChange, distanceDriven)));
                        logFile.logData("Average loop time = " + Double.toString(timer.milliseconds() / loopCount));
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
     * Stop the curve and set the motor to either float or hold. Most of the time I think you will
     * want to float.
     * @param finishBehavior
     */
    public void stopCurve(DcMotor8863.FinishBehavior finishBehavior) {
        driveTrain.setFinishBehavior(finishBehavior);
        driveTrain.shutdown();
    }

    public void testDriveCurveCalculations() {
        logFile.blankLine();
        // wheel speeds should be positive
        // left wheel speed should be bigger than right wheel speed
        // imu angle should become more negative / distance drive should become more positive so rate of turn should be negative
        logFile.logData("curve direction = " + CurveDirection.CW.toString() + " drive direction = " + DriveDirection.FORWARD.toString());
        logFile.logData("should be wheel speed +, left > right, rate of turn negative");
        setupDriveCurve(90, .5, 100, CurveDirection.CW, DriveDirection.FORWARD);
        logFile.blankLine();

        // wheel speeds should be positive
        // left wheel speed should be smaller than right wheel speed
        // imu angle should become more positive / distance drive should become more positive so rate of turn should be positive
        logFile.logData("curve direction = " + CurveDirection.CCW.toString() + " drive direction = " + DriveDirection.FORWARD.toString());
        logFile.logData("should be wheel speed +, left < right, rate of turn positive");
        setupDriveCurve(90, .5, 100, CurveDirection.CCW, DriveDirection.FORWARD);
        logFile.blankLine();

        // wheel speeds should be negative
        // left wheel speed should be smaller than right wheel speed
        // imu angle should become more negative / distance drive should become more positive so rate of turn should be negative
        logFile.logData("curve direction = " + CurveDirection.CW.toString() + " drive direction = " + DriveDirection.BACKWARD.toString());
        logFile.logData("should be wheel speed -, left < right, rate of turn negative");
        setupDriveCurve(90, .5, 100, CurveDirection.CW, DriveDirection.BACKWARD);
        logFile.blankLine();

        // wheel speeds should be negative
        // left wheel speed should be bigger than right wheel speed
        // imu angle should become more positive / distance drive should become more positive so rate of turn should be positive
        logFile.logData("curve direction = " + CurveDirection.CCW.toString() + " drive direction = " + DriveDirection.BACKWARD.toString());
        logFile.logData("should be wheel speed -, left > right, rate of turn positive");
        setupDriveCurve(90, .5, 100, CurveDirection.CCW, DriveDirection.BACKWARD);
        logFile.blankLine();
    }

    public void testDriveCurvePID() {
        double currentRateOfTurn;
        double correction;
        double newRadius;

        logFile.blankLine();

        // wheel speeds should be positive
        // left wheel speed should be bigger than right wheel speed
        // imu angle should become more negative / distance drive should become more positive so rate of turn should be negative
        setupDriveCurve(90, .5, 100, CurveDirection.CW, DriveDirection.FORWARD);

        logFile.logData("simulate a rate of turn that is less than it should be. So robot is not turning fast enough.");
        logFile.logData("PID should increase rate of turn by reducing turn radius.");
        logFile.logData("this means left wheel speed should become faster and right wheel speed should become slower");
        // calculated rate of turn should be -.572
        currentRateOfTurn = -.372;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();

        logFile.logData("now simulate a rate of turn that is more than it should be. So robot is turning too fast.");
        logFile.logData("PID should decrease rate of turn by increasing turn radius.");
        logFile.logData("this means left wheel speed should become slower and right wheel speed should become faster");
        // calculated rate of turn should be -.572
        currentRateOfTurn = -.772;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();


        // wheel speeds should be positive
        // left wheel speed should be smaller than right wheel speed
        // imu angle should become more positive / distance drive should become more positive so rate of turn should be positive
        setupDriveCurve(90, .5, 100, CurveDirection.CCW, DriveDirection.FORWARD);

        logFile.logData("now simulate a rate of turn that is less than it should be. So robot is not turning fast enough.");
        logFile.logData("PID should increase rate of turn by reducing turn radius.");
        logFile.logData("this means left wheel speed should become slower and right wheel speed should become faster");
        // calculated rate of turn should be +.572
        currentRateOfTurn = +.372;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();

        logFile.logData("now simulate a rate of turn that is more than it should be. So robot is turning too fast.");
        logFile.logData("PID should decrease rate of turn by increasing turn radius.");
        logFile.logData("this means left wheel speed should become faster and right wheel speed should become slower");
        // calculated rate of turn should be -.572
        currentRateOfTurn = +.772;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();

        // wheel speeds should be negative
        // left wheel speed should be smaller than right wheel speed
        // imu angle should become more negative / distance drive should become more positive so rate of turn should be negative
        setupDriveCurve(90, .5, 100, CurveDirection.CW, DriveDirection.BACKWARD);

        logFile.logData("now simulate a rate of turn that is less than it should be. So robot is not turning fast enough.");
        logFile.logData("PID should increase rate of turn by reducing turn radius.");
        logFile.logData("this means left wheel speed should become slower and right wheel speed should become faster");
        // calculated rate of turn should be -.572
        currentRateOfTurn = -.372;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();

        logFile.logData("now simulate a rate of turn that is more than it should be. So robot is turning too fast.");
        logFile.logData("PID should decrease rate of turn by increasing turn radius.");
        logFile.logData("this means left wheel speed should become faster and right wheel speed should become slower");
        // calculated rate of turn should be -.572
        currentRateOfTurn = -.772;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();


        // wheel speeds should be negative
        // left wheel speed should be bigger than right wheel speed
        // imu angle should become more positive / distance drive should become more positive so rate of turn should be positive
        setupDriveCurve(90, .5, 100, CurveDirection.CCW, DriveDirection.BACKWARD);

        logFile.logData("now simulate a rate of turn that is less than it should be. So robot is not turning fast enough.");
        logFile.logData("PID should increase rate of turn by reducing turn radius.");
        logFile.logData("this means left wheel speed should become faster and right wheel speed should become slower");
        // calculated rate of turn should be +.572
        currentRateOfTurn = +.372;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();

        logFile.logData("now simulate a rate of turn that is more than it should be. So robot is turning too fast.");
        logFile.logData("PID should decrease rate of turn by increasing turn radius.");
        logFile.logData("this means left wheel speed should become slower and right wheel speed should become faster");
        // calculated rate of turn should be -.572
        currentRateOfTurn = +.772;
        pidControl.getCorrection(currentRateOfTurn);
        correction = correctionSign * pidControl.getCorrection(currentRateOfTurn);
        newRadius = radius + correction;
        logFile.logData("rate of turn = " + currentRateOfTurn + " correction = " + correction + " new radius = " + newRadius);
        calculateWheelSpeeds(newRadius);
        logFile.logData("left wheel speed = " + Double.toString(leftWheelSpeed) + " right wheel speed = " + Double.toString(rightWheelSpeed));
        logFile.blankLine();
    }
}
