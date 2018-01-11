package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MoveBySteps {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

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
public MoveBySteps(Telemetry telemetry){
    this.telemetry = telemetry;
}

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private int convertDoubleToInt(double number) {
        return (int) (number * 1000);
    }

    private double convertIntToDouble(int number) {
        return (double) number / 1000;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************


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
        telemetry.addData("Starting position = ", "%3.2f", .200);
        telemetry.addData("Ending position = ", "%3.2f", endPosition);
        servoStepStartPosition = convertDoubleToInt(.200);
        servoStepEndPosition = convertDoubleToInt(endPosition);
        servoStepPositionIncrement = convertDoubleToInt(positionStepSize);
        servoStepTimeBetweenSteps = timeBetweenStepsInMilliseconds;
        servoStepTimer = new ElapsedTime();
        servoStepCurrentPosition = servoStepStartPosition;
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

        telemetry.addData("current position = ", "%d", servoStepCurrentPosition);
        telemetry.addData("end position     = ", "%d", servoStepEndPosition);

        // the start and end positions are the same so the movement is effectively already complete
        if (servoMovementDirection == ServoMovementDirection.NOT_MOVING) {
            isComplete = true;
            return isComplete;
        }
        // Are there more steps to be done in the series?
        if (Math.abs(servoStepCurrentPosition - servoStepEndPosition) > 0) {
            // there are still baby steps to be taken
            if (servoStepTimer.milliseconds() > servoStepTimeBetweenSteps) {
                // time between the steps has reached the point when we have to issue a new position
                // command so figure out the command
                // Note that the sign of the increment was figured out in the setup
                servoStepCurrentPosition = servoStepCurrentPosition + servoStepPositionIncrement;
                // issue the command
                //teamServo.setPosition(convertIntToDouble(servoStepCurrentPosition));
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
        telemetry.addData("isComplete = ", Boolean.toString(isComplete));
        return isComplete;
    }
}
