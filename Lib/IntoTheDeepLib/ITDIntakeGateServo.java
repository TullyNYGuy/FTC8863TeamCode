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
public class ITDIntakeGateServo implements FTCRobotSubsystem {

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
    private Servo8863New intakeGateServo;

    private final String INTAKE_GATE_SERVO_NAME = ITDRobot.HardwareName.INTAKE_GATE_SERVO.hwName;

    private DataLogging logFile;

    private boolean loggingOn = false;

    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;

    // no index position
    private double openPosition = 0.2;
    private double closePosition = 0;
    private double pickupPosition = 0.7;
    private double initPosition = closePosition;
    private double shutdownPosition = closePosition;

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

    public ITDIntakeGateServo(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeGateServo = new Servo8863New(INTAKE_GATE_SERVO_NAME, hardwareMap, telemetry);

        intakeGateServo.addPosition("initPosition", initPosition, 100, TimeUnit.MILLISECONDS);
        intakeGateServo.addPosition("openPosition", openPosition, 100, TimeUnit.MILLISECONDS);
        intakeGateServo.addPosition("closePosition", closePosition, 100, TimeUnit.MILLISECONDS);
        intakeGateServo.addPosition("pickupPosition", closePosition, 100, TimeUnit.MILLISECONDS);

        intakeGateServo.setDirection(Servo.Direction.FORWARD);
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
        intakeGateServo.setPosition("initPosition");
        logCommand("Init position");
    }

    public void openPosition() {
        intakeGateServo.setPosition("openPosition");
        logCommand("Intake position");
    }

    public void closePosition() {
        intakeGateServo.setPosition("closePosition");
        logCommand("Transfer position");
    }

    @Override
    public void shutdown() {
        closePosition();
        logCommand("shutdown");
    }


    public void bumpUpBig() {
        intakeGateServo.bump(0.1);
    }

    public void bumpDownBig() {
        intakeGateServo.bump(-0.1);
    }

    public void bumpUpSmall() {
        intakeGateServo.bump(0.01);
    }

    public void bumpDownSmall() {
        intakeGateServo.bump(-0.01);
    }

    //*********************************************************************************************
    //          Feedback
    //*********************************************************************************************
    public boolean isPositionReached() {
        return intakeGateServo.isPositionReached();
    }

    //*********************************************************************************************
    //          Wrappers
    //*********************************************************************************************

    public double getCurrentPosition() {
        return intakeGateServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return intakeGateServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        intakeGateServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        intakeGateServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        intakeGateServo.setupServoPositionsUsingGamepad(opmode);
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return INTAKE_GATE_SERVO_NAME;
    }

    /**
     * Since the bucket gate servo will probably not be a robot subsystem directly, this should not
     * get called. The intake arm / bucket arm / extension arm controller will contain this subsystem.
     *
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
        intakeGateServo.isPositionReached();
    }
}
