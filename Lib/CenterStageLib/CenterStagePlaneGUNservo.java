package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


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
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayConeGrabber;

import java.util.concurrent.TimeUnit;

@Config
public class CenterStagePlaneGUNservo implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum State {
        WAITING_FOR_KILL,
        READY
    }

    private State state = State.READY;

    public State getState() {
        return state;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863New gunServo;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logCommandOnchange;

    private final String PLANE_GUN_SERVO_NAME = CenterStageRobot.HardwareName.PLANE_GUN_SERVO.hwName;

    private double killPosition = .75;
    private double nonKillPosition = 0;

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

    public CenterStagePlaneGUNservo(HardwareMap hardwareMap, Telemetry telemetry) {
        gunServo = new Servo8863New(PLANE_GUN_SERVO_NAME, hardwareMap, telemetry);

        gunServo.addPosition("killPosition", killPosition, 1000, TimeUnit.MILLISECONDS);
        gunServo.addPosition("nonKillPosition", nonKillPosition, 1000, TimeUnit.MILLISECONDS);

        gunServo.setDirection(Servo.Direction.REVERSE);
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

    public void killPosition() {
        gunServo.setPosition("killPosition");
        state=State.WAITING_FOR_KILL;
    }

    public void nonKillPosition() {
        gunServo.setPosition("nonKillPosition");
    }

    public boolean isPositionReached() {
        return gunServo.isPositionReached();
    }

    // wrappers
    public ServoPosition getServoPosition(String positionName) {
        return gunServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        gunServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        gunServo.testPositionsUsingJoystick(opmode);
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init started");
        logCommand("Init complete");
        return true;
    }

    public boolean isInitComplete() {
        return true;
    }

    @Override
    public String getName() {
        return PLANE_GUN_SERVO_NAME;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
    }

    @Override
    public void update() {
        switch (state){
            case READY:
                break;
            case WAITING_FOR_KILL:
                if (gunServo.isPositionReached()){
                    nonKillPosition();
                    state=State.READY;
                }
                break;
        }
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }
}
