package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * A continuous rotation servo is a servo that can turn around and around like a motor but does not
 * have any position feedback like a normal servo does.
 * This class turns a continous rotation servo into a motor that can move a distance. It is not
 * completely accurate but it gets pretty close. It is done by characterizing a servo to see how
 * far it moves in a given time in both the forwards and backwards directions. Once the rate is
 * known, then a time to run the servo can be calculated given the desired distance to move. The
 * assumption here is that the load on the servo does not change. Note that testing showed that the
 * cm/Sec is different forwards and backwards.
 * In theory, if you give a CR servo a command of 0.5 (1/2 way between 0 and 1) it will not move.
 * But testing showed that 0.5 produced movement. So there are 2 values that need to be found for
 * each servo. The values are the ones that produce no movement in the forwards and backwards
 * direction. Testing found them to be different. Note that forwards and backwards are defined by
 * the direction that the servo is setup for.
 * Position for a CRServo is really the speed (throttle) command.
 */
public class CRServo {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * A constant to set the direction of movement of the servo
     */
    public enum CRServoDirection {
        FORWARD,
        BACKWARD
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The value that causes no movement of the servo when the direction of the servo is set to
     * forwards.
     */
    private double centerValueForward = 0.51;

    /**
     * The value that causes no movement of the servo when the direction of the servo is set to
     * backwards.
     */
    private double centerValueReverse = 0.46;

    /**
     * Store the current value that causes no movement of the servo
     */
    private double centerValue;

    /**
     * If the speed of the servo is set between -deadBandRange and + deadBandRange then the actual
     * speed of the servo gets set to 0
     */
    private double deadBandRange = 0.1;

    /**
     * The servo
     */
    private Servo crServo;

    // A timer
    private ElapsedTime timer;

    /**
     * The rate in CMPerSecond that the servo moves in the forward direction when the speed is set
     * to 1.0
     */
    private double forwardCMPerSecond = 0;

    /**
     * The rate in CMPerSecond that the servo moves in the backwards direction when the speed is set
     * to 1.0. In testing we found that forwards and backwards speeds are different.
     */
    private double backwardCMPerSecond = 0;

    /**
     * The time to move the servo to accomplish a certain distance movement.
     */
    private double milliSecondsToMove = 0;

    /**
     * The direction that the servo must move.
     */
    private CRServoDirection directionToMove = CRServoDirection.FORWARD;

    /**
     * The last command sent to the servo. If the new command = last command then we don't actually
     * send out the new command.
     */
    private double lastThrottleCommand = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getCenterValue() {
        return centerValue;
    }

    public void setCenterValue(double centerValue) {
        this.centerValue = centerValue;
    }

    public double getDeadBandRange() {
        return deadBandRange;
    }

    public void setDeadBandRange(double deadBandRange) {
        this.deadBandRange = deadBandRange;
    }

    public double getForwardCMPerSecond() {
        return forwardCMPerSecond;
    }

    public void setForwardCMPerSecond(double forwardCMPerSecond) {
        this.forwardCMPerSecond = forwardCMPerSecond;
    }

    public double getBackwardCMPerSecond() {
        return backwardCMPerSecond;
    }

    public void setBackwardCMPerSecond(double backwardCMPerSecond) {
        this.backwardCMPerSecond = backwardCMPerSecond;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public CRServo(String servoName, HardwareMap hardwareMap, double centerValueForward, double centerValueReverse, double deadBandRange, Servo.Direction direction) {
        crServo = hardwareMap.servo.get(servoName);
        this.centerValueReverse = centerValueReverse;
        this.centerValueForward = centerValueForward;
        // Set the direction for a positive position command and also sets the center value
        // The stupid center value that makes a CR servo not move seems to be different for Forwards
        // or backwards
        setDirection(direction);
        this.deadBandRange = deadBandRange;
        timer = new ElapsedTime();
        delay(100);

        crServo.setPosition(centerValue);
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Implements a delay
     *
     * @param mSec delay in milli Seconds
     */
    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Calculate the number of milliSeconds that the servo must be on in order to move a certain
     * distance.
     * @param distanceToMove in cm
     * @param direction FORWARDS or BACKWARDS
     * @return seconds to run the servo to get the desired movement
     */
    private double getMilliSecondsForMovement(double distanceToMove, CRServoDirection direction) {
        if (direction == CRServoDirection.BACKWARD) {
            return distanceToMove * 1 / backwardCMPerSecond *1000;
        } else {
            //forwards
            return distanceToMove * 1 / forwardCMPerSecond * 1000;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Turn on the servo and run it at the given speed. 0 is no speed. 1 is max speed.
     * @param throttle between 0 and 1 - how fast to run the servo
     */
    public void updatePosition(double throttle) {
        double servoPosition;
        if (-deadBandRange < throttle && throttle < deadBandRange) {
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if(centerValue != lastThrottleCommand) {
                crServo.setPosition(centerValue);
                lastThrottleCommand = centerValue;
            }
        } else {
            servoPosition = 0.5 * throttle + centerValue;
            servoPosition = Range.clip(servoPosition, 0, 1);
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if(throttle != lastThrottleCommand) {
                crServo.setPosition(servoPosition);
                lastThrottleCommand = throttle;
            }
        }
    }

    /**
     * Turn on the servo and run it at the given speed. 0 is no speed. 1 is max speed.
     * @param position between -1 and 1 - how fast to run the servo
     */
    public void setPosition(double position) {
        crServo.setPosition(position);
    }

    public double getPosition() {
        return crServo.getPosition();
    }

    /**
     * Set the direction the servo turns when it is sent a positive command
     * @param direction
     */
    public void setDirection(Servo.Direction direction) {
        if(direction == Servo.Direction.FORWARD) {
            centerValue = centerValueForward;
        } else {
            centerValue = centerValueReverse;
        }
        crServo.setDirection(direction);
    }

    /**
     * If you want something attached to the servo to move a certain distance, this method sets up
     * the movement
     * @param distanceToMove distance to move the object
     * @param direction direction to move the object
     */
    public void startMoveDistance(double distanceToMove, CRServoDirection direction) {
        if(backwardCMPerSecond == 0 || forwardCMPerSecond == 0) {
            // the user never setup the rate per second for this servo
            // throw an error
            throw new IllegalArgumentException("backwardCMPerSecond or forwardCMPerSecond was never set");
        }
        milliSecondsToMove = getMilliSecondsForMovement(distanceToMove, direction);
        directionToMove = direction;
        timer.reset();
        if(direction == CRServoDirection.FORWARD) {
            updatePosition(1);
        } else {
            updatePosition(-1);
        }
    }

    /**
     * This method gets called by the controlling routine once every loop cycle
     * @return true if the movement has finished
     */
    public boolean updateMoveDistance() {
        if(timer.milliseconds() >= milliSecondsToMove) {
            updatePosition(0);
            // indicate that the movement has completed
            return true;
        } else {
            return false;
        }
    }
}
