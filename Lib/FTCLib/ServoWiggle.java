package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This class allows you to setup an automatic servo "wiggle". This is where the servo will
 * automatically move back and forth between 2 positions for a given period of time. Each movement
 * in the wiggle will wait for a timer to expire and will make sure that the movement has completed.
 * This wiggle is useful for creating a vibration in the object controlled by the servo.
 *
 * THIS CLASS WAS CREATED FROM THE SERVO8863 CLASS. IT WORKED THERE BUT HAS NEVER BEEN TESTED AS A
 * STANDALONE CLASS. IT WAS REFACTORED OUT OF THAT CLASS SO THAT IT COULD BE USED STANDALONE
 */
public class ServoWiggle {

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

    /**
     * Which direction is the servo position changing?
     */
    private ServoMovementDirection servoMovementDirection;

    private enum ServoMovementDirection {
        INCREASING, // moving from a low command (like .1) to a high command (.9)
        DECREASING, // moving from a high command (like .8) to a low command (.4)
        NOT_MOVING // servo is not moving, probably because the start position = end position
    }

    /**
     * Variable for holding the state of the servo wiggle
     */
    private ServoWiggleState servoWiggleState;
    /**
     * readonly
     *
     * @return wiggle state of the servo
     */
    public ServoWiggleState getServoWiggleState() {
        return servoWiggleState;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

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

    private Servo servo;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

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
    public void ServoWiggle(Servo8863New servo, double wiggleStartPosition, double wiggleDelay, double wiggleDelta, double wiggleTime) {
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

    /**
     * Then start the servo wiggle. It is assumed that you already setup the wiggle.
     * Should add error checking to make sure of this but won't now due to time.
     */
    public void startWiggle() {
        servoWiggleState = ServoWiggleState.STARTPOSITION;
        servo.setPosition(wiggleStartPosition);
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
                    if (Math.abs(wiggleStartPosition - servo.getPosition()) < wigglePositionTolerance) {
                        // the position has been reached. Reset the movement timer, set the position to the wiggle position
                        elapsedTimeEachWiggle.reset();
                        servo.setPosition(wiggleStartPosition + wiggleDelta);
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
                    if (Math.abs(wiggleStartPosition + wiggleDelta - servo.getPosition()) < wigglePositionTolerance) {
                        // the position has been reached. Reset the movement timer, set the next position to the start position
                        elapsedTimeEachWiggle.reset();
                        servo.setPosition(wiggleStartPosition);
                        // move to the next state
                        servoWiggleState = ServoWiggleState.STARTPOSITION;
                    }
                }
                break;

            case WIGGLECOMPLETE:
                // The wiggle has completed, set the position to the starting position of the wiggle
                servo.setPosition(wiggleStartPosition);
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
        servo.setPosition(wiggleStartPosition);
    }

}
