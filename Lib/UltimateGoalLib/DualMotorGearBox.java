package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class DualMotorGearBox {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Sets enums for direction to use.
     */
    public enum Direction {
        FORWARD,
        REVERSE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DcMotor8863 leftMotor;
    private DcMotor8863 rightMotor;
    private Direction direction;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Shows which direction the output shaft is turning.
     *
     * @return The direction that the motor is spinning
     */
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (direction == Direction.FORWARD) {
            leftMotor.setDirection(FORWARD);
            rightMotor.setDirection(FORWARD);
            direction = Direction.FORWARD;
        }

        if (direction == Direction.REVERSE) {
            leftMotor.setDirection(REVERSE);
            rightMotor.setDirection(REVERSE);
            direction = Direction.REVERSE;
        }
    }
    //*******************T**************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * @param leftMotorName  The name of the left motor
     * @param rightMotorName The name of the right motor
     * @param hardwareMap    Hardware map from the FTC robot
     * @param telemetry      The telemetry from the FTC robot
     */
    public DualMotorGearBox(String leftMotorName, String rightMotorName, HardwareMap hardwareMap, Telemetry telemetry) {
        leftMotor = new DcMotor8863(leftMotorName, hardwareMap, telemetry);
        leftMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_6000);
        leftMotor.setMovementPerRev(360);
        leftMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        //leftMotor.runAtConstantSpeed(0);
        rightMotor = new DcMotor8863(rightMotorName, hardwareMap, telemetry);
        rightMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_6000);
        rightMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        //rightMotor.runAtConstantSpeed(0);
        rightMotor.setMovementPerRev(360);
        setDirection(Direction.FORWARD);
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

    public void update() {
    }

    /**
     * When the user sets a speed the motors run at that speed.
     *
     * @param motorRPM The speed in a range from -6000 to 6000
     */
    public void setSpeed(int motorRPM) {
        //Limits the input to positive 1
        if (motorRPM > 6000) {
            motorRPM = 6000;
        }
        //Limits the input to negative 1
        if (motorRPM < -6000) {
            motorRPM = -6000;
        }
        leftMotor.runAtConstantRPM(motorRPM);
        rightMotor.runAtConstantRPM(motorRPM);
    }

    /**
     * Get the RPM for the output shaft
     * @return
     */
    public double getSpeed() {
        return (leftMotor.getCurrentRPM() + rightMotor.getCurrentRPM()) / 2;
    }

    /**
     * Stops the gearbox
     */
    public void stopGearbox() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        leftMotor.interrupt();
        rightMotor.interrupt();
    }

}
