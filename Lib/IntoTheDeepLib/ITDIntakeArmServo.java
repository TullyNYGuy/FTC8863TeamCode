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

    private double initPosition = 0.7;

    // INDEX POSITION
    private double intakePosition = 0.1;

    private double bucketClearancePosition = 0.5;
    private double transferPosition = 0.5;

    private double shutdownPosition = transferPosition;

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

        intakeArmServo.addPosition("initPosition", initPosition, 700, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("bucketClearancePosition", bucketClearancePosition, 700, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("intakePosition", intakePosition, 700, TimeUnit.MILLISECONDS);
        intakeArmServo.addPosition("transferPosition", transferPosition, 1000, TimeUnit.MILLISECONDS);

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
    public void intakePosition() {
        intakeArmServo.setPosition("intakePosition");
        logCommand("Intake position");
    }

    public void transferPosition() {
        intakeArmServo.setPosition("transferPosition");
        logCommand("Transfer position");
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
