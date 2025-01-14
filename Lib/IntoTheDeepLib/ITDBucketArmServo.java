package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ServoPosition;

import java.util.concurrent.TimeUnit;

@Config
public class ITDBucketArmServo implements FTCRobotSubsystem {

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
    private Servo8863New bucketArmServo;

    private final String BUCKET_ARM_SERVO_NAME = ITDRobot.HardwareName.BUCKET_ARM_SERVO.hwName;

    private DataLogging logFile;

    private boolean loggingOn = false;

    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;

    private double initPosition = 0.98;
    private double intakePosition = 0.03;

    // index when arm is vertical and servo = .52
    private double safeForVerticalMovementPosition = 0.52;
    private double deliveryPosition = 0.42;
    private double transferPosition = 0.98;
    private double specimenPickupPosition = 0.7;
    private double specimenHangLowBarPosition = 0.7;
    private double specimenHangHighBarPosition = 0.75;

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

    public ITDBucketArmServo(HardwareMap hardwareMap, Telemetry telemetry) {
        bucketArmServo = new Servo8863New(BUCKET_ARM_SERVO_NAME, hardwareMap, telemetry);

        bucketArmServo.addPosition("initPosition", initPosition, 700, TimeUnit.MILLISECONDS);
        bucketArmServo.addPosition("intakePosition", intakePosition, 700, TimeUnit.MILLISECONDS);
        bucketArmServo.addPosition("safeForVerticalMovementPosition", safeForVerticalMovementPosition, 700, TimeUnit.MILLISECONDS);
        bucketArmServo.addPosition("deliveryPosition", deliveryPosition, 700, TimeUnit.MILLISECONDS);
        bucketArmServo.addPosition("transferPosition", transferPosition, 1000, TimeUnit.MILLISECONDS);
        bucketArmServo.addPosition("specimenPickupPosition", specimenPickupPosition, 1000, TimeUnit.MILLISECONDS);
        bucketArmServo.addPosition("specimenHangLowBarPosition", specimenHangLowBarPosition, 1000, TimeUnit.MILLISECONDS);
        bucketArmServo.addPosition("specimenHangHighBarPosition", specimenHangHighBarPosition, 800, TimeUnit.MILLISECONDS);

        bucketArmServo.setDirection(Servo.Direction.FORWARD);
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
        bucketArmServo.setPosition("initPosition");
        logCommand("Init position");
    }

    public void intakePosition() {
        bucketArmServo.setPosition("intakePosition");
        logCommand("Intake position");
    }

    public void safeForVerticalMovementPosition() {
        bucketArmServo.setPosition("safeForVerticalMovementPosition");
        logCommand("Safe for vertical movement position");
    }

    public void deliveryPosition() {
        bucketArmServo.setPosition("deliveryPosition");
        logCommand("Delivery position");
    }

    public void transferPosition() {
        bucketArmServo.setPosition("transferPosition");
        logCommand("Transfer position");
    }

    public void specimenPickupPosition() {
        bucketArmServo.setPosition("specimenPickupPosition");
        logCommand("Specimen pickup position");
    }

    public void specimenHangLowBarPosition() {
        bucketArmServo.setPosition("specimenHangLowBarPosition");
        logCommand("Specimen low bar position");
    }

    public void specimenHangHighBarPosition() {
        bucketArmServo.setPosition("specimenHangHighBarPosition");
        logCommand("Specimen high bar position");
    }

    @Override
    public void shutdown() {
        transferPosition();
        logCommand("shutdown");
    }


    public void bumpUpBig() {
        bucketArmServo.bump(0.1);
    }

    public void bumpDownBig() {
        bucketArmServo.bump(-0.1);
    }

    public void bumpUpSmall() {
        bucketArmServo.bump(0.01);
    }

    public void bumpDownSmall() {
        bucketArmServo.bump(-0.01);
    }

    //*********************************************************************************************
    //          Feedback
    //*********************************************************************************************
    public boolean isPositionReached() {
        return bucketArmServo.isPositionReached();
    }

    //*********************************************************************************************
    //          Wrappers
    //*********************************************************************************************

    public double getCurrentPosition() {
        return bucketArmServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return bucketArmServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        bucketArmServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        bucketArmServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        bucketArmServo.setupServoPositionsUsingGamepad(opmode);
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return BUCKET_ARM_SERVO_NAME;
    }

    /**
     * Since the bucket arm servo will probably not be a robot subsystem directly, this should not
     * get called. The intake arm / bucket arm / extension arm controller will contain this subsystem.
     *
     * @param config
     * @return
     */
    @Override
    public boolean init(Configuration config){
        return true;
    }

    /**
     * Since the bucket arm servo will probably not be a robot subsystem directly, this should not
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
        bucketArmServo.isPositionReached();
    }
}
