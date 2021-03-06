package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by FTC8863 on 12/2/2015.
 * Class allows you to control a servo with one of 6 positions:
 * home, up, down, position1, position2, position3
 * It also allows you to setup an automatic servo "wiggle". This is where the servo will
 * automatically move back and forth between 2 positions for a given period of time. Each movement
 * in the wiggle will wait for a timer to expire and will make sure that the movement has completed.
 * This wiggle is useful for creating a vibration in the object controlled by the servo.
 */
public class Servo8863 {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * The state of the servo wiggle.
     */
    public enum ServoWiggleState {
        WIGGLECOMPLETE, STARTPOSITION, WIGGLEPOSITION, NOWIGGLE
    }

    private enum ServoMovementDirection {
        INCREASING, // moving from a low command (like .1) to a high command (.9)
        DECREASING, // moving from a high command (like .8) to a low command (.4)
        NOT_MOVING // servo is not moving, probably because the start position = end position
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The home or default position of the servo
     */
    private double homePosition;

    /**
     * The up position of the servo
     */
    private double upPosition;

    /**
     * The down position of the servo
     */
    private double downPosition;

    /**
     * A generic position for the servo
     */
    private double positionOne;

    /**
     * A generic position for the servo
     */
    private double positionTwo;

    /**
     * A generic position for the servo
     */
    private double positionThree;

    /**
     * The position that the servo initially gets set to
     */
    private double initPosition;

    /**
     * The servo that this class wraps around
     */
    private Servo teamServo;

    /**
     * The direction of the servo
     */
    private Servo.Direction direction;

    /**
     * A servo can be wiggled back and forth a little. This is the time delay between the
     * two positions of the wiggle.
     */
    private double wiggleDelay;

    /**
     * The wiggle of the servo is based off a position. The wiggle will start at the position
     * and then move to position + wiggleDelta. For example, if the position is .7 and the
     * wiggleDelta is -.2, the servo will wiggle between .7 and (.7-.2)= .5.
     */
    private double wiggleDelta = 0;

    /**
     * The position to start the wiggle from.
     */
    private double wiggleStartPosition = 0;

    /**
     * The tolerance used to see if the position for the wiggle movement has been reached
     */
    private double wigglePositionTolerance = 0;

    /**
     * The total time to wiggle the servo.
     */
    private double wiggleTime = 0;

    /**
     * A timer to use for controlling the total wiggle time of the servo.
     */
    private ElapsedTime elapsedTimeTotalWiggle;

    /**
     * A timer to use for controlling the time for each wiggle movement.
     */
    private ElapsedTime elapsedTimeEachWiggle;

    /**
     * Variable for holding the state of the servo wiggle
     */
    private ServoWiggleState servoWiggleState;

    /**
     * Enter debug mode
     */
    private boolean debug = false;

    /**
     * Sets how much the servo moves within the set time period for the calibration routine.
     */
    private double servoCalibrationPositionIncrement;

    /**
     * Sets the time period between each servo movement for the calibration routine.
     */
    private double servoCalibrationTimeBetweenSteps;

    /**
     * Sets the position the servo will start at for the calibration routine.
     */
    private double servoCalibrationStartPosition;

    /**
     * Sets the position the servo will end at for the calibration routine.
     */
    private double servoCalibrationEndPosition;

    /**
     * Tells you how much time has passed in the calibration routine.
     */
    private ElapsedTime calibrationRoutineTimer;

    /**
     * Tells you what position the servo is at in the calibration routine.
     */
    private double calibrationRoutineCurrentPosition;

    /**
     * Starting position for a series of step movements of a servo - INT!
     */
    private int servoStepStartPosition;

    /**
     * Ending position for a series of step movements of a servo - INT!
     */
    private int servoStepEndPosition;

    /**
     * Current position in a series of step movements of a servo - INT!
     */
    private int servoStepCurrentPosition;

    /**
     * Increment between steps for a series of step movements of a servo - INT!
     */
    private int servoStepPositionIncrement;

    /**
     * Which direction is the servo position changing?
     */
    private ServoMovementDirection servoMovementDirection;

    /**
     * A timer to use to time the interval between steps
     */
    private ElapsedTime servoStepTimer;

    /**
     * The time between steps in milliseconds.
     */
    private double servoStepTimeBetweenSteps;

    /**
     * Declare a telemetry object so that we can broadcast info to the driver station.
     */
    private Telemetry telemetry;


    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //*********************************************************************************************

    public double getHomePosition() {
        return homePosition;
    }

    public void setHomePosition(double homePosition) {
        this.homePosition = homePosition;
    }

    public double getUpPosition() {
        return upPosition;
    }

    public void setUpPosition(double upPosition) {
        this.upPosition = upPosition;
    }

    public double getDownPosition() {
        return downPosition;
    }

    public void setDownPosition(double downPosition) {
        this.downPosition = downPosition;
    }

    public Servo.Direction getDirection() {
        return direction;
    }

    public void setDirection(Servo.Direction direction) {
        this.direction = direction;
        teamServo.setDirection(direction);
    }

    public double getPositionOne() {
        return positionOne;
    }

    public void setPositionOne(double positionOne) {
        this.positionOne = positionOne;
    }

    public double getPositionTwo() {
        return positionTwo;
    }

    public void setPositionTwo(double positionTwo) {
        this.positionTwo = positionTwo;
    }

    public double getPositionThree() {
        return positionThree;
    }

    public void setPositionThree(double positionThree) {
        this.positionThree = positionThree;
    }

    public double getInitPosition() {
        return initPosition;
    }

    public void setInitPosition(double initPosition) {
        this.initPosition = initPosition;
    }

    /**
     * readonly
     *
     * @return wiggle state of the servo
     */
    public ServoWiggleState getServoWiggleState() {
        return servoWiggleState;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setServoCalibrationPositionIncrement(double servoCalibrationPositionIncrement) {
        this.servoCalibrationPositionIncrement = servoCalibrationPositionIncrement;
    }

    public void setServoCalibrationTimeBetweenSteps(double servoCalibrationTimeBetweenSteps) {
        this.servoCalibrationTimeBetweenSteps = servoCalibrationTimeBetweenSteps;
    }

    public void setServoCalibrationStartPosition(double servoCalibrationStartPosition) {
        this.servoCalibrationStartPosition = servoCalibrationStartPosition;
    }

    public void setServoCalibrationEndPosition(double servoCalibrationEndPosition) {
        this.servoCalibrationEndPosition = servoCalibrationEndPosition;
    }

    //*********************************************************************************************
    //          Constructors
    //*********************************************************************************************

    public Servo8863(String servoName, HardwareMap hardwareMap, Telemetry telemetry, double homePosition, double upPosition, double downPosition, double initPosition, Servo.Direction direction) {
        this.initServo(servoName, hardwareMap, telemetry);
        setHomePosition(homePosition);
        setUpPosition(upPosition);
        setDownPosition(downPosition);
        setInitPosition(initPosition);
        this.setDirection(direction);
    }

    public Servo8863(String servoName, HardwareMap hardwareMap, Telemetry telemetry) {
        this.initServo(servoName, hardwareMap, telemetry);
    }

    private void initServo(String servoName, HardwareMap hardwareMap, Telemetry telemetry) {
        teamServo = hardwareMap.servo.get(servoName);
        setHomePosition(1);
        setDownPosition(0);
        setUpPosition(0);
        setPositionOne(0);
        setPositionTwo(0);
        setPositionThree(0);
        setInitPosition(0);
        setDirection(Servo.Direction.FORWARD);
        this.servoWiggleState = ServoWiggleState.NOWIGGLE;
        elapsedTimeTotalWiggle = new ElapsedTime();
        elapsedTimeEachWiggle = new ElapsedTime();
        this.telemetry = telemetry;
    }

    //*********************************************************************************************
    //          Public Methods
    //*********************************************************************************************

    public void goUp() {
        teamServo.setPosition(getUpPosition());
    }

    public void goDown() {
        teamServo.setPosition(getDownPosition());
    }

    public void goHome() {
        teamServo.setPosition(getHomePosition());
    }

    public void goPositionOne() {
        teamServo.setPosition(getPositionOne());
    }

    public void goPositionTwo() {
        teamServo.setPosition(getPositionTwo());
    }

    public void goPositionThree() {
        teamServo.setPosition(getPositionThree());
    }

    public void goInitPosition() {
        teamServo.setPosition(getInitPosition());
    }

    public void setPosition(double position) {
        position = Range.clip(position, 0.0, 1.0);
        teamServo.setPosition(position);
    }

    /**
     * Setup a servo wiggle. The servo can wiggle
     * between the wiggle start position and wiggleStartPosition + wiggleDelta. Each wiggle movement
     * will begin after the timer has expired and after the servo has reached the position
     * commanded +/- the wiggle position tolerance.
     * Note that you have to call startWiggle in order to start the wiggle.
     * Note that the looping routine will have to call updateWiggle after setting this up in order
     * to make the wiggle work.
     *
     * @param wiggleStartPosition The position to start the wiggle from.
     * @param wiggleDelay         The time to pass between each wiggle movement. If 0 then the only thing
     *                            that will be checked is if the servo has reached the wiggle position.
     * @param wiggleDelta         How much to move the servo from the starting position. Can be + or -.
     */
    public void setupWiggle(double wiggleStartPosition, double wiggleDelay, double wiggleDelta, double wiggleTime) {
        this.wiggleStartPosition = wiggleStartPosition;
        this.wiggleDelay = wiggleDelay;
        this.wiggleDelta = wiggleDelta;
        this.wiggleTime = wiggleTime;
        // the servo will be said to have reached its position if it is within the range of
        // desired position - wigglePositionTolerance to desired position + wigglePositionTolerance
        this.wigglePositionTolerance = .05;
        // for now there is no wiggle started. We only setup one for the future
        servoWiggleState = ServoWiggleState.NOWIGGLE;
    }

    /**
     * Setup a servo wiggle. Then start the wiggle. The servo will wiggle
     * between the wiggle position and wigglePosition + wiggleDelta. Each wiggle movement will begin
     * after the timer has expired and after the servo has reached the position commanded +/-
     * the wiggle position tolerance.
     * Note that the looping routine will have to call updateWiggle after setting this up in order
     * to make the wiggle work.
     *
     * @param wiggleStartPosition The position to start the wiggle from.
     * @param wiggleDelay         The time to pass between each wiggle movement. If 0 then the only thing
     *                            that will be checked is if the servo has reached the wiggle position.
     * @param wiggleDelta         How much to move the servo from the starting position. Can be + or -.
     */
    public void startWiggle(double wiggleStartPosition, double wiggleDelay, double wiggleDelta, double wiggleTime) {
        setupWiggle(wiggleStartPosition, wiggleDelay, wiggleDelta, wiggleTime);
        startWiggle();
    }

    /**
     * Then start the servo wiggle. It is assumed that you already setup the wiggle.
     * Should add error checking to make sure of this but won't now due to time.
     */
    public void startWiggle() {
        servoWiggleState = ServoWiggleState.STARTPOSITION;
        teamServo.setPosition(wiggleStartPosition);
        elapsedTimeTotalWiggle.reset();
        elapsedTimeEachWiggle.reset();
    }

    /**
     * This method needs to be called each time through the robot loop so that the wiggle is
     * controlled. It uses a state machine to keep track of things.
     */
    public ServoWiggleState updateWiggle() {

        // if there is no wiggle started or ongoing, just return because there is nothing to do
        if (getServoWiggleState() == ServoWiggleState.NOWIGGLE) {
            return servoWiggleState;
        }
        // check to see if the total time the servo is supposed to wiggle has been exceeded.
        // if it has, the wiggle is done
        if (elapsedTimeTotalWiggle.time() > wiggleTime) {
            servoWiggleState = ServoWiggleState.WIGGLECOMPLETE;
        }

        switch (servoWiggleState) {
            case STARTPOSITION:
                // the servo is headed for the start position of the wiggle
                // see if the timer for this wiggle movement has expired
                if (elapsedTimeEachWiggle.time() > wiggleDelay) {
                    // timer has expired for this wiggle movement, see if the position has been
                    // reached within the tolerance limit
                    if (Math.abs(wiggleStartPosition - teamServo.getPosition()) < wigglePositionTolerance) {
                        // the position has been reached. Reset the movement timer, set the position to the wiggle position
                        elapsedTimeEachWiggle.reset();
                        teamServo.setPosition(wiggleStartPosition + wiggleDelta);
                        // move to the next state
                        servoWiggleState = ServoWiggleState.WIGGLEPOSITION;
                    }
                }
                break;

            case WIGGLEPOSITION:
                // the servo is headed for the "wiggle" position of the wiggle (start position +
                // wiggleDelta)
                // see if the timer for this wiggle movement has expired
                if (elapsedTimeEachWiggle.time() > wiggleDelay) {
                    // timer has expired for this wiggle movement, see if the position has been
                    // reached within the tolerance limit
                    if (Math.abs(wiggleStartPosition + wiggleDelta - teamServo.getPosition()) < wigglePositionTolerance) {
                        // the position has been reached. Reset the movement timer, set the next position to the start position
                        elapsedTimeEachWiggle.reset();
                        teamServo.setPosition(wiggleStartPosition);
                        // move to the next state
                        servoWiggleState = ServoWiggleState.STARTPOSITION;
                    }
                }
                break;

            case WIGGLECOMPLETE:
                // The wiggle has completed, set the position to the starting position of the wiggle
                teamServo.setPosition(wiggleStartPosition);
                break;

            case NOWIGGLE:
                // there has not been a servo wiggle started. Don't do anything
                break;
        }
        return servoWiggleState;
    }

    /**
     * Stop or interrupt the wiggling of the servo. The servo position will be set to the starting
     * position of the wiggle.
     */
    public void stopWiggle() {
        servoWiggleState = ServoWiggleState.WIGGLECOMPLETE;
        teamServo.setPosition(wiggleStartPosition);
    }

    /**
     * Get the current position of the servo. The current position is relative to whereever 0
     * is set to.
     *
     * @return current position
     */
    public double getPosition() {
        return teamServo.getPosition();
    }

    /**
     * Setup for moving the servo from a start position to an end position by a certain increment with a
     * certain time between each movement. This is useful for find the locations you want for a certain
     * servo.
     *
     * @param startPosition                      start stepping the servo movements from this position
     * @param endPosition                        end the servo movements when it hits this position
     * @param positionIncrement                  move the servo by this much every time it moves
     * @param timeBetweenPositionsInMilliseconds wait for this much time between each movement
     */
    public void setUpServoCalibration(double startPosition, double endPosition, double positionIncrement, double timeBetweenPositionsInMilliseconds) {
        setServoCalibrationStartPosition(startPosition);
        setServoCalibrationEndPosition(endPosition);
        setServoCalibrationPositionIncrement(positionIncrement);
        setServoCalibrationTimeBetweenSteps(timeBetweenPositionsInMilliseconds);
        calibrationRoutineTimer = new ElapsedTime();
        calibrationRoutineCurrentPosition = startPosition;
    }

    /**
     * Call this method repeatedly in an opmode loop so that the servo movements get updated as it
     * steps through the calibration positions.
     */
    public void updateServoCalibration() {
        if (calibrationRoutineTimer.milliseconds() > servoCalibrationTimeBetweenSteps && calibrationRoutineCurrentPosition <= servoCalibrationEndPosition) {
            calibrationRoutineCurrentPosition = calibrationRoutineCurrentPosition + servoCalibrationPositionIncrement;
            teamServo.setPosition(calibrationRoutineCurrentPosition);
            calibrationRoutineTimer.reset();
        }
        telemetry.addData("servo cmd", "position" + String.format("%.2f", calibrationRoutineCurrentPosition));
        telemetry.addData("servo actual", "position" + String.format("%.2f", teamServo.getPosition()));
    }

    private int convertDoubleToInt(double number) {
        return (int) (number * 1000);
    }

    private double convertIntToDouble(int number) {
        return (double) number / 1000;
    }

    /**
     * Setup a servo so that it moves by little baby steps over time. This give better control for a
     * servo that does not have much load on it. The overshoot is reduced a lot.
     *
     * @param endPosition                    final desired position of the servo
     * @param positionStepSize               how big is the baby step
     * @param timeBetweenStepsInMilliseconds how much time passes between steps
     *                                       <p>
     *                                       NOTE: see the note in updateMoveBySteps about double vs int. In this case I am doing the
     *                                       conversion to int as part of the setup.
     */
    public void setupMoveBySteps(double endPosition, double positionStepSize, double timeBetweenStepsInMilliseconds) {
        //telemetry.addData("Starting position = ", "%3.2f", teamServo.getPosition());
        //telemetry.addData("Ending position = ", "%3.2f", endPosition);

        // set the variables - note the conversion to int from double
        servoStepStartPosition = convertDoubleToInt(teamServo.getPosition());
        servoStepEndPosition = convertDoubleToInt(endPosition);
        servoStepPositionIncrement = convertDoubleToInt(positionStepSize);
        servoStepTimeBetweenSteps = timeBetweenStepsInMilliseconds;

        // create an timer to measure the time between steps
        servoStepTimer = new ElapsedTime();
        servoStepCurrentPosition = servoStepStartPosition;

        // setup the incement to be either positive (increases the servo position) or negative
        // (decreases the servo position) based on the direction of travel
        if (servoStepStartPosition > servoStepEndPosition) {
            servoMovementDirection = ServoMovementDirection.DECREASING;
            // since the commands will be decreasing make sure the increment is negative
            servoStepPositionIncrement = -Math.abs(servoStepPositionIncrement);
        }
        if (servoStepStartPosition < servoStepEndPosition) {
            servoMovementDirection = ServoMovementDirection.INCREASING;
            // since the commands will be decreasing make sure the increment is positive
            servoStepPositionIncrement = Math.abs(servoStepPositionIncrement);
        }
        if (servoStepStartPosition == servoStepEndPosition) {
            servoMovementDirection = ServoMovementDirection.NOT_MOVING;
        }
    }

    /**
     * Moves a servo using a series of smaller stepped movements. Each movement has a time delay before
     * the next movement starts. This technique will eliminate overshoot on a lightly loaded servo.
     *
     * @return true if all of the movements in the series are completed.
     * <p>
     * NOTES: servo positions are double. But floating point math is not exact. Since we are doing
     * number comparisons we need exact math and one way to get it is to represent positions by
     * integers. So all calculations are done using integer math and then the result is converted
     * back to a double to output to the servo.
     */
    public boolean updateMoveBySteps() {
        boolean isComplete = false;

        // floating point numbers cannot represent a number with complete accuracy. Since we need to
        // perform comparisons between positions, we do need complete accuracy. One way to do this
        // is to turn the floating point numbers in to integers and do the math with the integers.
        // In order to maintain a level of resolution I am taking 3 decimal places for the positions.
        // I.E x1000 before I turn them into an int.

        //telemetry.addData("current position = ", "%d", servoStepCurrentPosition);
        //telemetry.addData("end position     = ", "%d", servoStepEndPosition);

        // the start and end positions are the same so the movement is effectively already complete
        if (servoMovementDirection == ServoMovementDirection.NOT_MOVING) {
            isComplete = true;
            return isComplete;
        }
        // If we made it here we are actually stepping.
        // Are there more steps to be done in the series?
        if (Math.abs(servoStepCurrentPosition - servoStepEndPosition) > 0) {
            // there are still baby steps to be taken
            if (servoStepTimer.milliseconds() > servoStepTimeBetweenSteps) {
                // time between the steps has reached the point when we have to issue a new position
                // command so figure out the command
                // Note that the sign of the increment was figured out in the setup
                servoStepCurrentPosition = servoStepCurrentPosition + servoStepPositionIncrement;
                // issue the command
                teamServo.setPosition(convertIntToDouble(servoStepCurrentPosition));
                // reset the timer for the step
                servoStepTimer.reset();
                isComplete = false;
            }
        } else {
            // the last step command to the servo was already issued so the MoveBySteps is complete
            // IMPORTANT NOTE: this does not mean the servo has reached the final position. It may
            // not have gotten there yet. A large load will slow it down. It may even stall and never
            // reach the final position. All this means is that the commanded position that was issued
            // is the last one in the series of steps.
            isComplete = true;
        }
        //telemetry.addData("isComplete = ", Boolean.toString(isComplete));
        return isComplete;
    }
}

