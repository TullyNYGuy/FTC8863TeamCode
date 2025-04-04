package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ServoPosition;

import java.util.concurrent.TimeUnit;

@Config
public class ITDIntakeArmServo implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum IntakeHeight {
        READY_TO_INTAKE,
        BUCKET_CLEARANCE,
        HIGH_ALTITUDE_PREP,
        HIGH,
        LOW,
        REALLY_LOW
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863New intakeArmServo;

    private final String INTAKE_ARM_SERVO_NAME = ITDRobot.HardwareName.INTAKE_ARM_SERVO.hwName;

    private DataLogging logFile;

    private boolean loggingOn = false;

    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;

    private double initPosition = 0.9;

    // INDEX WHEN INTAKE IS ON FLOOR AND SERVO = .07
    private double intakePositionReallyLowAltitude = 0.13;
    private double intakePositionLowAltitude = 0.18;
    private double intakePositionHighAltitude = 0.18;
    private double intakePositionHighAltitudePrep = .25;

    private double bucketClearancePosition = 0.6;
    private double transferPosition = 0.86;

    private double shutdownPosition = transferPosition;

    private double readyToIntakePosition = 0.35;
    private double ejectPosition = .6;

    private double shortSidePassthroughPosition = .45;

    //  private double initPosition = 0.65;
    //
    //    // INDEX WHEN INTAKE IS ON FLOOR AND SERVO = .1
    //    private double intakePositionReallyLowAltitude = 0.13;
    //    private double intakePositionLowAltitude = 0.15;
    //    private double intakePositionHighAltitude = 0.17;
    //
    //    private double bucketClearancePosition = 0.3;
    //    private double transferPosition = 0.48;
    //
    //    private double shutdownPosition = transferPosition;
    //
    //    private double readyToIntakePosition = 0.38;

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

    public ITDIntakeArmServo(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeArmServo = new Servo8863New(INTAKE_ARM_SERVO_NAME, hardwareMap, telemetry);

        intakeArmServo.addPosition("initPosition", initPosition, 100, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("bucketClearancePosition", bucketClearancePosition, 100, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("intakePositionReallyLowAltitude", intakePositionReallyLowAltitude, 200, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("intakePositionLowAltitude", intakePositionLowAltitude, 200, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("intakePositionHighAltitudePrep", intakePositionHighAltitudePrep, 200, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("intakePositionHighAltitude", intakePositionHighAltitude, 200, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("transferPosition", transferPosition, 400, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("readyToIntakePosition", readyToIntakePosition, 100, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("ejectPosition", ejectPosition, 100, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("shortSidePassthroughPosition", shortSidePassthroughPosition, 100, TimeUnit.MILLISECONDS);

        intakeArmServo.setDirection(Servo.Direction.REVERSE);
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

    //*********************************************************************************************
    //          Commands
    //*********************************************************************************************

    public void initPosition() {
        intakeArmServo.setPosition("initPosition");
        logCommand("Init position");
    }

    public void bucketClearancePosition() {
        intakeArmServo.setPosition("bucketClearancePosition");
        logCommand("bucket clearance position");
    }

    public void intakePositionReallyLowAltitude() {
        intakeArmServo.setPosition("intakePositionReallyLowAltitude");
        logCommand("Intake position really low altitude");
    }
    public void intakePositionLowAltitude() {
        intakeArmServo.setPosition("intakePositionLowAltitude");
        logCommand("Intake position low altitude");
    }

    public void intakePositionHighAltitudePrep() {
        intakeArmServo.setPosition("intakePositionHighAltitudePrep");
        logCommand("Intake position high altitude prep");
    }

    public void intakePositionHighAltitude() {
        intakeArmServo.setPosition("intakePositionHighAltitude");
        logCommand("Intake position high altitude");
    }

    public void transferPosition() {
        intakeArmServo.setPosition("transferPosition");
        logCommand("Transfer position");
    }
    public void readyToIntakePosition() {
        intakeArmServo.setPosition("readyToIntakePosition");
        logCommand("Ready To Intake Position");
    }
    public void ejectPosition() {
        intakeArmServo.setPosition("ejectPosition");
        logCommand("Eject Position");
    }
    public void shortSidePassthroughPosition() {
        intakeArmServo.setPosition("shortSidePassthroughPosition");
        logCommand("shortSidePassthroughPosition");
    }
    @Override
    public void shutdown() {
        transferPosition();
        logCommand("shutdown");
    }

    public void bumpUpBig (){
        intakeArmServo.bump(0.1);
    }

    public void bumpDownBig () {
        intakeArmServo.bump(-0.1);
    }

    public void bumpUpSmall () {
        intakeArmServo.bump(0.01);
    }

    public void bumpDownSmall () {
        intakeArmServo.bump(-0.01);
    }

    //*********************************************************************************************
    //          Feedback
    //*********************************************************************************************
    public boolean isPositionReached() {
        return intakeArmServo.isPositionReached();
    }

    //*********************************************************************************************
    //          Wrappers
    //*********************************************************************************************

    public double getCurrentPosition(){
        return intakeArmServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return intakeArmServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        intakeArmServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        intakeArmServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        intakeArmServo.setupServoPositionsUsingGamepad(opmode);
    }

    public void setPwmDisable() {
        intakeArmServo.setPwmDisable();
    }

    public void setPwmEnable() {
        intakeArmServo.setPwmEnable();
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return INTAKE_ARM_SERVO_NAME;
    }

    /**
     * Since the intake arm servo will probably not be a robot subsystem directly, this should not
     * get called. The intake arm / bucket arm / extension arm controller will contain this subsystem.
     * @param config
     * @return
     */
    @Override
    public boolean init(Configuration config) {
        return true;
    }

    /**
     * Since the bucket gate servo will probably not be a robot subsystem directly, this should not
     * get called. The intake arm / bucket arm / extension arm controller will contain this subsystem.
     */
    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logDataOnchange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logDataOnchange.log(getName() + " command = " + command);
        }
    }

    public void displayState(Telemetry telemetry) {
        //telemetry.addData("State = ", armIntakeState.toString());
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    /**
     * This method does not need to be called since isPositionReached() is typically called directly.
     */
    public void update() {
        intakeArmServo.isPositionReached();
    }
}
