package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DecodeSorterMotor implements FTCRobotSubsystem {

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

    private DcMotor8863 sorterMotor;
    private Telemetry telemetry;
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
        double maxRPM = sorterMotor.getNoLoadRPM();
        if (aRPM > maxRPM ){
            aRPM = maxRPM;
        }
        if (aRPM < -maxRPM){
            aRPM = -maxRPM;
        }
        this.RPM = aRPM;
        sorterMotor.runAtConstantRPM(this.RPM);
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
            sorterMotor.setDirection(FORWARD);
            direction = Direction.FORWARD;
        }

        if (direction == Direction.REVERSE) {
            sorterMotor.setDirection(REVERSE);
            direction = Direction.REVERSE;
        }

    }

    private final String SUB_SYSTEM_NAME = DecodeRobot.HardwareName.SORTER_MOTOR.hwName;

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
     * @param hardwareMap    Hardware map from the FTC robot
     * @param telemetry      The telemetry from the FTC robot
     */
    public DecodeSorterMotor(HardwareMap hardwareMap, Telemetry telemetry) {
        sorterMotor = new DcMotor8863(SUB_SYSTEM_NAME, hardwareMap, telemetry);
        sorterMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_435);
        sorterMotor.setMovementPerRev(360);
        sorterMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        setDirection(Direction.FORWARD);
        this.telemetry = telemetry;
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

    public double getCurrent(CurrentUnit currentUnit){
        return sorterMotor.getCurrent(currentUnit);
    }
    public double getCurrentRPM(){
        return sorterMotor.getCurrentRPM();
    }

    public void shoot(){
        setRPM(180);

    }
    public void intake(){
        setRPM(40);

    }
    public void off(){
        setRPM(0);
    }

    public void moveToPosition(double position){
        sorterMotor.moveToPosition(1, position, DcMotor8863.FinishBehavior.HOLD);
    }
    public void displaySorterAngle() {
        telemetry.addData("encoder value ", sorterMotor.getCurrentPosition());
        telemetry.addData("sorter angle ", sorterMotor.getPositionInTermsOfAttachment());
    }

    public double getPositionInTermsOfAttachment(){
        return sorterMotor.getPositionInTermsOfAttachment();
    }

    public void setPower(double power) {
        sorterMotor.setPower(power);
    }

    public double getActualRPM() {
        return sorterMotor.getCurrentRPM();
    }

    public double getCurrent() {
        return sorterMotor.getCurrent(CurrentUnit.AMPS);
    }

    public double getNoLoadRPM(){
        return sorterMotor.getNoLoadRPM();
    }

    /**
     * Stops the gearbox
     */
    public void stop() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        sorterMotor.interrupt();
    }
    public boolean isMovementComplete() {
        return sorterMotor.isMovementComplete();
    }
    public void resetEncoder() {
        sorterMotor.resetEncoder();
    }
    @Override
    public void update() {
        sorterMotor.update();
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
