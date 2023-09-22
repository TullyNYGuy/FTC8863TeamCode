package org.firstinspires.ftc.teamcode.Lib.LauncherBot;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class LauncherBotDualMotorGearBox implements FTCRobotSubsystem {

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

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    private int speed;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed (int aSpeed){
        if (aSpeed > 6000 ){
            aSpeed = 6000;
        }
        if (aSpeed < -6000){
            aSpeed = -6000;
        }
        this.speed = aSpeed;
        leftMotor.runAtConstantRPM(this.speed);
        rightMotor.runAtConstantRPM(this.speed);

    }
    /**
     * Property that holds the direction of the output shaft.
     */
    private Direction direction;
    /**
     * Shows which direction the output shaft is turning.
     *
     * @return The direction that the output shaft is spinning
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Sets the direction of the output shaft
     * @param direction
     */
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

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;
    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }
    //*********************************************************************************************
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
    public LauncherBotDualMotorGearBox(String leftMotorName, String rightMotorName, HardwareMap hardwareMap, Telemetry telemetry) {
        leftMotor = new DcMotor8863(leftMotorName, hardwareMap, telemetry);
        leftMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_6000);
        leftMotor.setMovementPerRev(360);
        leftMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);

        rightMotor = new DcMotor8863(rightMotorName, hardwareMap, telemetry);
        rightMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_6000);
        rightMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        rightMotor.setMovementPerRev(360);

        setDirection(Direction.REVERSE);
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

    /**
     * Stops the gearbox
     */
    public void stopGearbox() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        leftMotor.interrupt();
        rightMotor.interrupt();
    }
    @Override
    public String getName() {
        return "intake";
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
