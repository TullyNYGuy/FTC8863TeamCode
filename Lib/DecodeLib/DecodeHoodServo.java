package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


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
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDRobot;

import java.util.concurrent.TimeUnit;

@Config
public class DecodeHoodServo implements FTCRobotSubsystem {

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
    private Servo8863New hoodServo;

    private DataLogging logFile;

    private boolean loggingOn = false;

    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;

    private double initPosition = 0.1;
    private double shortPosition = 0.1;
    private double longPosition = 0.32;

    //New index position (hood down)  = .1
    //Max position (hood up)  = .41

    private double shutdownPosition = initPosition;

    private final String SUB_SYSTEM_NAME = DecodeRobot.HardwareName.HOOD_SERVO.hwName;

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

    public DecodeHoodServo(HardwareMap hardwareMap, Telemetry telemetry) {
        hoodServo = new Servo8863New(SUB_SYSTEM_NAME, hardwareMap, telemetry);

        hoodServo.addPosition("initPosition", initPosition, 250, TimeUnit.MILLISECONDS);
        hoodServo.addPosition("shortPosition", shortPosition, 250, TimeUnit.MILLISECONDS);
        hoodServo.addPosition("longPosition", longPosition, 250, TimeUnit.MILLISECONDS);
        hoodServo.addPosition("shutdownPosition", shutdownPosition, 250, TimeUnit.MILLISECONDS);
        // old lift was forward direction
        hoodServo.setDirection(Servo.Direction.FORWARD);
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
        hoodServo.setPosition("initPosition");
        logCommand("Init position");
    }

    public void shortPosition() {
        hoodServo.setPosition("shortPosition");
        logCommand("short Position");
    }

    public void longPosition() {
        hoodServo.setPosition("longPosition");
        logCommand("long Position");
    }

    @Override
    public void shutdown() {
        initPosition();
        logCommand("shutdown");
    }


    public void bumpUpBig() {
        hoodServo.bump(0.1);
    }

    public void bumpDownBig() {
        hoodServo.bump(-0.1);
    }

    public void bumpUpSmall() {
        hoodServo.bump(0.01);
    }

    public void bumpDownSmall() {
        hoodServo.bump(-0.01);
    }

    //*********************************************************************************************
    //          Feedback
    //*********************************************************************************************
    public boolean isPositionReached() {
        return hoodServo.isPositionReached();
    }

    //*********************************************************************************************
    //          Wrappers
    //*********************************************************************************************

    public double getCurrentPosition() {
        return hoodServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return hoodServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        hoodServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        hoodServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        hoodServo.setupServoPositionsUsingGamepad(opmode);
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return SUB_SYSTEM_NAME;
    }

    /**
     * Since the bucket arm servo will probably not be a robot subsystem directly, this should not
     * get called. The intake arm / bucket arm / extension arm controller will contain this subsystem.
     *
     * @param config
     * @return
     */
    @Override
    public boolean init(Configuration config) {
        initPosition();
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
    }
}
