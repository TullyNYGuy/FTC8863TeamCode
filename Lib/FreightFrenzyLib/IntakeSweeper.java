package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class IntakeSweeper implements FTCRobotSubsystem {

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

    private DcMotor8863 sweeperMotor;
    private Direction direction;
    private DataLogging logFile;
    private boolean loggingOn = false;
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
            sweeperMotor.setDirection(FORWARD);
            direction = Direction.FORWARD;
        }

        if (direction == Direction.REVERSE) {
            sweeperMotor.setDirection(REVERSE);
            direction = Direction.REVERSE;
        }

    }
    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }
    //*******************T**************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public IntakeSweeper(HardwareMap hardwareMap, Telemetry telemetry) {
        sweeperMotor = new DcMotor8863("sweeperMotor", hardwareMap, telemetry);
        sweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        sweeperMotor.setMovementPerRev(360);
        sweeperMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        //sweeperMotor.runAtConstantSpeed(0);
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
    @Override
    public void update() {
    }

    public void reset() {
        sweeperMotor.stop();
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
        sweeperMotor.runAtConstantRPM(motorRPM);
    }

    /**
     * Get the RPM for the output shaft
     * @return
     */
    public double getSpeed() {
        return (sweeperMotor.getCurrentRPM());
    }

    /**
     * Stops the gearbox
     */
    public void stop() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        sweeperMotor.interrupt();
    }

    public void intake() {
        sweeperMotor.runAtConstantPower(100);
    }

    public void eject() {
        sweeperMotor.runAtConstantPower(-100);
    }

    public void trapFreight() {
        sweeperMotor.runAtConstantPower(10);
    }

    @Override
    public String getName() {
        return "intake sweeper";
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }
}
