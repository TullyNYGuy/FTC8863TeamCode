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

import java.util.concurrent.TimeUnit;

@Config
public class DecodeRampServo implements FTCRobotSubsystem {

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
    private Servo8863New rampServo;

  //  private final String RAMP_SERVO_NAME = ITDRobot.HardwareName.HOOD_SERVO.hwName;
    private final String RAMP_SERVO_NAME = "rampServo";
    private DataLogging logFile;

    private boolean loggingOn = false;

    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;

    private double initPosition = 0.13;
    private double upPosition = 0.42;
    private double downPosition = 0.13;

    //New index position (hood down)  = .13
    //Max position (hood up)  = .42

    private double shutdownPosition = initPosition;

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

    public DecodeRampServo(HardwareMap hardwareMap, Telemetry telemetry) {
        rampServo = new Servo8863New(RAMP_SERVO_NAME, hardwareMap, telemetry);

        rampServo.addPosition("initPosition", initPosition, 170, TimeUnit.MILLISECONDS);
        rampServo.addPosition("upPosition", upPosition, 170, TimeUnit.MILLISECONDS);
        rampServo.addPosition("downPosition", downPosition, 170, TimeUnit.MILLISECONDS);
        rampServo.addPosition("shutdownPosition", shutdownPosition, 170, TimeUnit.MILLISECONDS);
        // old lift was forward direction
        rampServo.setDirection(Servo.Direction.REVERSE);
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
        rampServo.setPosition("initPosition");
        logCommand("Init position");
    }

    public void upPosition() {
        rampServo.setPosition("upPosition");
        logCommand("up Position");
    }

    public void downPosition() {
        rampServo.setPosition("downPosition");
        logCommand("down Position");
    }

    @Override
    public void shutdown() {
        initPosition();
        logCommand("shutdown");
    }


    public void bumpUpBig() {
        rampServo.bump(0.1);
    }

    public void bumpDownBig() {
        rampServo.bump(-0.1);
    }

    public void bumpUpSmall() {
        rampServo.bump(0.01);
    }

    public void bumpDownSmall() {
        rampServo.bump(-0.01);
    }

    //*********************************************************************************************
    //          Feedback
    //*********************************************************************************************
    public boolean isPositionReached() {
        return rampServo.isPositionReached();
    }

    //*********************************************************************************************
    //          Wrappers
    //*********************************************************************************************

    public double getCurrentPosition() {
        return rampServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return rampServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        rampServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        rampServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        rampServo.setupServoPositionsUsingGamepad(opmode);
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return RAMP_SERVO_NAME;
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
        rampServo.isPositionReached();
    }
}
