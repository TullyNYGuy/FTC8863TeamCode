package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

// to do list
// finish the state initialization for the servo in all 3 constructors
// check the state diagram to see if it all covered

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
 * Position command for a CRServo is really the speed (throttle) command.
 * 0 is full power backward.
 * somewhere around 0.5 is stop
 * +1 is full power forward
 * This setup is not intuitive. I think this is more intuitive:
 * -1 is full power backward
 * 0 is stop
 * +1 is full power forward
 * So I am choosing to make the input range -1 to +1.
 */
public class CRServoGB {

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

    public enum CRServoState {
        AT_BACK_SWITCH,
        AT_FRONT_SWITCH,
        AT_BACK_POSITION,
        AT_FRONT_POSITION,
        MOVING_TO_BACK_SWITCH,
        MOVING_TO_FRONT_SWITCH,
        MOVING_TO_BACK_POSITION,
        MOVING_TO_FRONT_POSITION;
    }

    public enum SwitchLocation {
        FRONT,
        BACK;
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
     * speed of the servo is set to stop
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
     * The direction that the servo must move. Note that this is different from the direction of the
     * servo. Direction of the servo reverses the meaning of 0 and 1 (forwards and backwards).
     * See setDirection(). Basically that re-defines the meaning of forward and backwards. This is
     * just the direction to move. Move forwards. Move backwards. It does not re-define the meaning
     * of forwards and backwards.
     */
    private CRServoDirection directionToMove = CRServoDirection.FORWARD;

    /**
     * The last command sent to the servo. If the new command = last command then we don't actually
     * send out the new command.
     */
    private double lastThrottleCommand = 0;

    private double currentCommand = 0;
    private double commandIncrement;

    private CRServoState crServoState = CRServoState.AT_BACK_POSITION;
    private boolean backLimitSwitchPresent = false;
    private boolean frontLimitSwitchPresent = false;
    private Switch frontLimitSwitch;
    private Switch backLimitSwitch;

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

    public CRServoGB(String servoName, HardwareMap hardwareMap, double centerValueForward,
                     double centerValueReverse, double deadBandRange, Servo.Direction direction) {
        initialize(servoName, hardwareMap, centerValueForward, centerValueReverse, deadBandRange,
                direction);
        // initialize the CRServo State ????
    }

    /**
     * Create a CRServo with one limit switch.
     *
     * @param servoName
     * @param hardwareMap
     * @param centerValueForward
     * @param centerValueReverse
     * @param deadBandRange
     * @param direction
     * @param switchName
     * @param switchLocation
     * @param switchType
     */
    public CRServoGB(String servoName, HardwareMap hardwareMap, double centerValueForward,
                     double centerValueReverse, double deadBandRange, Servo.Direction direction,
                     String switchName, SwitchLocation switchLocation, Switch.SwitchType switchType) {

        initialize(servoName, hardwareMap, centerValueForward, centerValueReverse, deadBandRange, direction);
        // create the limit switch
        if (switchLocation == SwitchLocation.BACK) {
            backLimitSwitch = new Switch(hardwareMap, switchName, switchType);
            backLimitSwitchPresent = true;
            frontLimitSwitchPresent = false;
        } else {
            frontLimitSwitch = new Switch(hardwareMap, switchName, switchType);
            frontLimitSwitchPresent = true;
            backLimitSwitchPresent = false;
        }
        // check to see if the servo is against the limit switch in order to initialize the state
    }

    public CRServoGB(String servoName, HardwareMap hardwareMap, double centerValueForward,
                     double centerValueReverse, double deadBandRange, Servo.Direction direction,
                     String frontSwitchName, Switch.SwitchType frontSwitchType,
                     String backSwitchName, Switch.SwitchType backSwitchType) {

        initialize(servoName, hardwareMap, centerValueForward, centerValueReverse, deadBandRange, direction);
        // create the limit switches
        frontLimitSwitch = new Switch(hardwareMap, frontSwitchName, frontSwitchType);
        backLimitSwitch = new Switch(hardwareMap, backSwitchName, backSwitchType);
        backLimitSwitchPresent = true;
        frontLimitSwitchPresent = true;
        // check to see if the servo is against the limit switches in order to initialize the state
    }

    private void initialize(String servoName, HardwareMap hardwareMap, double centerValueForward,
                            double centerValueReverse, double deadBandRange,
                            Servo.Direction direction) {
        crServo = hardwareMap.servo.get(servoName);
        this.centerValueReverse = centerValueReverse;
        this.centerValueForward = centerValueForward;
        // Set the direction for a positive position command and also sets the center value
        // The stupid center value that makes a CR servo not move seems to be different for Forwards
        // or backwards
        setDirection(direction);
        this.deadBandRange = deadBandRange;
        timer = new ElapsedTime();
        // If I don't put a delay here the next setPosition command seems to get lost and the servo
        // starts to move at init.
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
     *
     * @param distanceToMove in cm
     * @param direction      FORWARDS or BACKWARDS
     * @return seconds to run the servo to get the desired movement
     */
    private double getMilliSecondsForMovement(double distanceToMove, CRServoDirection direction) {
        if (direction == CRServoDirection.BACKWARD) {
            return distanceToMove * 1 / backwardCMPerSecond * 1000;
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
     * Turn on the servo and run it at the given speed. 0 is stop. +1 is max speed forwards. -1 is
     * max speed backwards.
     * Note that a real servo only takes commands between 0 and 1. In this case I am treating the
     * servo more like a motor without position feedback. It would seem weird and not intuitive to
     * make the stop command 0.5. Instead I make extend the range and then will have to translate it
     * to the 0-1 range that a servo can take.
     *
     * @param throttle between -1 and 1 - how fast to run the servo
     */
    public void setSpeed(double throttle) {
        double servoCommand;
        // if the servo command is within the deadband range for the servo, then send out the
        // center value (value that produces no movement) instead. The deadband is a range around 0.
        if (-deadBandRange < throttle && throttle < deadBandRange) {
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if (centerValue != lastThrottleCommand) {
                crServo.setPosition(centerValue);
                lastThrottleCommand = centerValue;
            }
        } else {
            // I have to translate the -1 to +1 input range to a 0 to +1 range that a servo can take.
            servoCommand = 0.5 * throttle + 0.5;
            servoCommand = Range.clip(servoCommand, 0, 1);
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if (throttle != lastThrottleCommand) {
                crServo.setPosition(servoCommand);
                lastThrottleCommand = throttle;
            }
        }
    }

    /**
     * Turn on the servo and run it at the given speed. Use this call if you don't want any position
     * control and just want to run the servo.
     *
     * @param position 0 = full speed backwards, 1 = full speed forwards
     */
    public void setPosition(double position) {
        crServo.setPosition(position);
    }

    /**
     * Set the direction the servo turns when it is sent a positive command. This method re-defines
     * the meaning of forwards and backwards.
     * Normally, the servo will not turn when the command is set to 0.5. However each servo varies.
     * The actual command that creates no movement varies from servo to servo. It also varies if the
     * direction is reversed. Which is why the center value (value that produces no movement) is set
     * based on the direction chosen.
     *
     * @param direction
     */
    public void setDirection(Servo.Direction direction) {
        if (direction == Servo.Direction.FORWARD) {
            centerValue = centerValueForward;
        } else {
            centerValue = centerValueReverse;
        }
        crServo.setDirection(direction);
    }

    /**
     * If you want something attached to the servo to move a certain distance, this method sets up
     * the movement
     *
     * @param distanceToMove distance to move the object
     * @param direction      direction to move the object
     */
    public void startMoveDistance(double distanceToMove, CRServoDirection direction) {
        if (backwardCMPerSecond == 0 || forwardCMPerSecond == 0) {
            // the user never setup the rate per second for this servo
            // throw an error
            throw new IllegalArgumentException("CRServo backwardCMPerSecond or forwardCMPerSecond was never set");
        }
        milliSecondsToMove = getMilliSecondsForMovement(distanceToMove, direction);
        directionToMove = direction;
        timer.reset();
        if (direction == CRServoDirection.FORWARD) {
            crServoState = CRServoState.MOVING_TO_FRONT_POSITION;
            setSpeed(1);
        } else {
            crServoState = CRServoState.MOVING_TO_BACK_POSITION;
            setSpeed(-1);
        }
    }

    /**
     * This method gets called by the controlling routine once every loop cycle
     *
     * @return true if the movement has finished, false if it still in process
     */
    public boolean updateMoveDistance() {
        // run until the time has been exceeded
        if (timer.milliseconds() >= milliSecondsToMove) {
            // stop the movement
            setSpeed(0);
            // indicate that the movement has completed
            return true;
        } else {
            // indicate that the movement is still in process
            return false;
        }
    }

    public void moveUntilLimitSwitch(CRServoDirection direction) {
        if (direction == CRServoDirection.FORWARD) {
            crServoState = CRServoState.MOVING_TO_FRONT_SWITCH;
            setSpeed(1);
        } else {
            crServoState = CRServoState.MOVING_TO_BACK_SWITCH;
            setSpeed(-1);
        }
    }

    public CRServoState updateCRServo() {
        switch (crServoState) {
            case AT_BACK_SWITCH:
                // the only way out of this state is for the user to command a movement
                // make sure the servo is not moving while it is here
                setSpeed(0);
                break;
            case AT_FRONT_SWITCH:
                // the only way out of this state is for the user to command a movement
                // make sure the servo is not moving
                setSpeed(0);
                break;
            case AT_BACK_POSITION:
                // the only way out of this state is for the user to command a movement
                // make sure the servo is not moving
                setSpeed(0);
                break;
            case AT_FRONT_POSITION:
                // the only way out of this state is for the user to command a movement
                // make sure the servo is not moving
                setSpeed(0);
                break;
            case MOVING_TO_BACK_POSITION:
                // if the servo has a back limit switch check to make sure the servo has not hit it
                if (backLimitSwitch != null) {
                    if (backLimitSwitch.isPressed()) {
                        // it is against the back switch, set the state and stop the servo
                        crServoState = CRServoState.AT_BACK_SWITCH;
                        setSpeed(0);
                        // now something went wrong here. It should not have it the back switch since
                        // it was supposed to reach a position. Not sure what to do about that yet.
                    }
                }
                // if not, then has the servo reached the desired position?
                if(updateMoveDistance()) {
                    // movement to position has finished
                    crServoState = CRServoState.AT_BACK_POSITION;
                }
                break;
            case MOVING_TO_FRONT_POSITION:
                // if the servo has a front limit switch check to make sure the servo has not hit it
                if (frontLimitSwitch != null) {
                    if (frontLimitSwitch.isPressed()) {
                        // it is against the back switch, set the state and stop the servo
                        crServoState = CRServoState.AT_FRONT_SWITCH;
                        setSpeed(0);
                        // now something went wrong here. It should not have it the back switch since
                        // it was supposed to reach a position. Not sure what to do about that yet.
                    }
                }
                // if not, then has the servo reached the desired position?
                if(updateMoveDistance()) {
                    // movement to position has finished
                    crServoState = CRServoState.AT_FRONT_POSITION;
                }
                break;
            case MOVING_TO_BACK_SWITCH:
                // if the servo has a back limit switch check to make sure the servo has not hit it
                if (backLimitSwitch.isPressed()) {
                    // it is against the back switch, set the state and stop the servo
                    crServoState = CRServoState.AT_BACK_SWITCH;
                    setSpeed(0);
                }
                break;
            case MOVING_TO_FRONT_SWITCH:
                // if the servo has a front limit switch check to make sure the servo has not hit it
                if (frontLimitSwitch.isPressed()) {
                    // it is against the front switch, set the state and stop the servo
                    crServoState = CRServoState.AT_FRONT_SWITCH;
                    setSpeed(0);
                }
                break;
        }
        return crServoState;
    }

    public void setupFindNoMovementCommand() {
        timer.reset();
        // start the testing for no movement at 0.4
        currentCommand = .4;
        crServo.setPosition(currentCommand);
    }

    public String updateFindNoMovementCommand() {
        // increment the command by .01 each time 500 milliseconds has passed
        double commandIncrement = .01;
        int stepLength = 500; // milliseconds
        String result = "Undefined";
        if (currentCommand >= .6) {
            // testing is done, we hit all the values from .4 to .6
            return "Finished";
        }
        if (currentCommand < .6 && timer.milliseconds() > stepLength) {
            // timer has expired and we have not hit the end of the range to test
            currentCommand = currentCommand + commandIncrement;
            // format a string to be displayed on the driver station phone
            result = "Command = " + String.format("%1.2f", currentCommand);
            crServo.setPosition(currentCommand);
            timer.reset();
        }
        return result;
    }

    public void updatePosition(double throttle) {
        // this is just a place holder method to avoid having to change DeliveryBox
        // delete it later
    }
}
