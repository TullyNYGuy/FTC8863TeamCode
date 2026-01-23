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
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;

public class DecodeTurntableMotor implements FTCRobotSubsystem {

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

    private DcMotor8863 turntableMotor;
    private Telemetry telemetry;
    PIDFController controller;
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
        double maxRPM = turntableMotor.getNoLoadRPM();
        if (aRPM > maxRPM ){
            aRPM = maxRPM;
        }
        if (aRPM < -maxRPM){
            aRPM = -maxRPM;
        }
        this.RPM = aRPM;
        turntableMotor.runAtConstantRPM(this.RPM);
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
            turntableMotor.setDirection(FORWARD);
            direction = Direction.FORWARD;
        }

        if (direction == Direction.REVERSE) {
            turntableMotor.setDirection(REVERSE);
            direction = Direction.REVERSE;
        }

    }

    private double targetPosition = 0;

    public double getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
        controller.setTargetPosition(targetPosition);
    }

    private double positionError = 0;
    public double getPositionError() {
        return positionError;
    }

    public void setPositionError(double positionError) {
        this.positionError = positionError;
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
     * @param turntableMotorName  The name of the left motor
     * @param hardwareMap    Hardware map from the FTC robot
     * @param telemetry      The telemetry from the FTC robot
     */
    public DecodeTurntableMotor(String turntableMotorName, HardwareMap hardwareMap, Telemetry telemetry) {
        turntableMotor = new DcMotor8863(turntableMotorName, hardwareMap, telemetry);
        turntableMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_312);
        turntableMotor.setMovementPerRev(360*67.602/171*1.15);
        turntableMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        setDirection(Direction.REVERSE);

        controller = new PIDFController(new PIDCoefficients(0.008,0,.010),1.2,0,.002);
        // set the target position tolerance in degrees. If the turntable is within 3 degrees of the target,
        // the controller will say that it is on target
        controller.setTargetPositionTolerance(3.0);
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
    public void displayTurntableAngle() {
        telemetry.addData("encoder value ", turntableMotor.getCurrentPosition());
        telemetry.addData("turntable angle ", turntableMotor.getPositionInTermsOfAttachment());
    }

    public double getPositionInTermsOfAttachment(){
        return turntableMotor.getPositionInTermsOfAttachment();
    }

    public void setPower(double power) {
        turntableMotor.setPower(power);
    }

    public void moveToPosition(double Position) {
        turntableMotor.moveToPosition(1, Position, DcMotor8863.FinishBehavior.HOLD);
    }

    public double getActualRPM() {
        return turntableMotor.getCurrentRPM();
    }

    public double getNoLoadRPM(){
        return turntableMotor.getNoLoadRPM();
    }

    public double getCurrent() {
        return turntableMotor.getCurrent(CurrentUnit.AMPS);
    }

    /**
     * Stops the gearbox
     */
    public void stop() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        turntableMotor.interrupt();
    }

    public boolean isOnTarget() {
        return controller.isMovementComplete();
    }

    @Override
    public void update() {
        // Something outside this class will have to update us with the positionError.
        // That would most likely be the limelight. Use setPositionError() for that.
        controller.update(positionError);
    }

    @Override
    public String getName() {
        return "turntable";
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
