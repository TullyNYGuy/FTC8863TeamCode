package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ITDIntakeSweeperVertical implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum SweeperState {
        INTAKING,
        OUTTAKING,
        OUTTAKING_BEFORE_STOPPING,
        STOPPED,
        INTAKING_BEFORE_STOPPING
    }

    private SweeperState sweeperState;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private CRServo intakeSweeperServoLeft;
    private CRServo intakeSweeperServoRight;
    private ElapsedTime timer;
    private double delayTime;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logCommandOnchange;

    private boolean initComplete = false;
    private final String INTAKE_SWEEPER_SERVO_NAME = "Sweeper Servo";

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDIntakeSweeperVertical(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeSweeperServoLeft = hardwareMap.get(CRServo.class, "intakeSweeperServoLeft");
        intakeSweeperServoLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeSweeperServoRight = hardwareMap.get(CRServo.class, "intakeSweeperServoRight");
        intakeSweeperServoRight.setDirection(DcMotorSimple.Direction.FORWARD);

        timer = new ElapsedTime();
        stop();
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

    /**
     * Stop the rotation of the sweeper
     */
    public void stop() {
        intakeSweeperServoLeft.setPower(0);
        intakeSweeperServoRight.setPower(0);
        sweeperState = SweeperState.STOPPED;
    }

    /**
     * After a delay, stop the sweeper
     *
     * @param delayTimeInMillisec amount of time to delay the before stopping
     */
    public void intakeThenStop(double delayTimeInMillisec) {
        // if the sweeper is already in a delay before stopping, don't start it all over again
        if(sweeperState != SweeperState.INTAKING_BEFORE_STOPPING) {
            timer.reset();
            this.delayTime = delayTime;
            sweeperState = SweeperState.INTAKING_BEFORE_STOPPING;
        }
    }

    /**
     * Rotate the sweeper so it intakes
     */
    public void intake() {
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
        sweeperState = SweeperState.INTAKING;
    }

    /**
     * Rotate the sweeper to it outtakes.
     */
    public void outtake() {
        intakeSweeperServoLeft.setPower(-1);
        intakeSweeperServoRight.setPower(-1);
        sweeperState = SweeperState.OUTTAKING;
    }

    public void outtakeThenStop(double timeToRotateInMillisec) {
        // if the sweeper is already running an outtake before stopping, don't start all over again
        if(sweeperState != SweeperState.OUTTAKING_BEFORE_STOPPING) {
            outtake();
            this.delayTime = timeToRotateInMillisec;
            timer.reset();
            sweeperState = SweeperState.OUTTAKING_BEFORE_STOPPING;
        }
    }

    @Override
    public String getName() {
        return INTAKE_SWEEPER_SERVO_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        return true;
    }

    @Override
    public void update() {
        switch (sweeperState) {
            case INTAKING:
                break;
            case OUTTAKING:
                break;
            case OUTTAKING_BEFORE_STOPPING:
                if (timer.milliseconds() > delayTime) {
                    stop();
                    sweeperState = SweeperState.STOPPED;
                }
                break;
            case INTAKING_BEFORE_STOPPING:
                if (timer.milliseconds() > delayTime) {
                    stop();
                    sweeperState = SweeperState.STOPPED;
                }
                break;
            case STOPPED:
                break;
        }
    }

    @Override
    public void shutdown() {
        stop();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
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
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}
