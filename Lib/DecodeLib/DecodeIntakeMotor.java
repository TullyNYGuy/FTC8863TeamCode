package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DecodeIntakeMotor implements FTCRobotSubsystem {

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

    private DcMotor8863 intakeMotor;
    private final String INTAKE_MOTOR_NAME = DecodeRobot.HardwareName.INTAKE_MOTOR.hwName;
    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    private double RPM;

    public double getCommandedRPM() {
        return RPM;
    }

    public void setRPM (double aRPM){
        double maxRPM = intakeMotor.getNoLoadRPM();
        if (aRPM > maxRPM ){
            aRPM = maxRPM;
        }
        if (aRPM < -maxRPM){
            aRPM = -maxRPM;
        }
        this.RPM = aRPM;
        intakeMotor.runAtConstantRPM(this.RPM);
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
            intakeMotor.setDirection(FORWARD);
            direction = Direction.FORWARD;
        }

        if (direction == Direction.REVERSE) {
            intakeMotor.setDirection(REVERSE);
            direction = Direction.REVERSE;
        }

    }

    private final String SUB_SYSTEM_NAME = DecodeRobot.HardwareName.INTAKE_MOTOR.hwName;

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;
    @Override
    public void setDataLog(DataLogging logFile) {
        logCommandOnchange = new DataLogOnChange(logFile);
        logCommentOnChange = new DataLogOnChange(logFile);
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

    private DataLogOnChange logCommandOnchange;
    private DataLogOnChange logCommentOnChange;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * @param hardwareMap    Hardware map from the FTC robot
     * @param telemetry      The telemetry from the FTC robot
     */
    public DecodeIntakeMotor(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeMotor = new DcMotor8863(INTAKE_MOTOR_NAME, hardwareMap, telemetry);
        intakeMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_1150);
        intakeMotor.setMovementPerRev(360);
        intakeMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        setDirection(Direction.REVERSE);
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    private void logComment(String comment) {
        if (loggingOn && logFile != null) {
            logCommentOnChange.log(getName() + " " + comment);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void intake(){
        logCommand("intake");
        setRPM (500);
    }

    public void off(){
        logCommand("off");
        setRPM (0);
    }

    public void outtake(){
        logCommand("outake");
        setRPM (-500);
    }

    public void setPower(double power) {
        intakeMotor.setPower(power);
    }

    public double getActualRPM() {
        return intakeMotor.getCurrentRPM();
    }

    public int getCurrentPosition() {
        return intakeMotor.getCurrentPosition();
    }

    public double getCurrent() {
        return intakeMotor.getCurrent(CurrentUnit.AMPS);
    }

    public double getNoLoadRPM(){
        return intakeMotor.getNoLoadRPM();
    }

    @Override
    public void update() {
        intakeMotor.update();
    }

    /**
     * Stops the gearbox
     */
    public void stop() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        intakeMotor.interrupt();
    }
    @Override
    public String getName() {
        return SUB_SYSTEM_NAME;
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
        setRPM(0);
        return true;
    }
}
