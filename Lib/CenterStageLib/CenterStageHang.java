package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStageHang implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum State {
        READY,
        HANGING,
        DONE,
        STOPPED
    }

    private enum Phase {
        TELEOP,
        AUTONOMOUS
    }

    private State state = State.READY;

    // so we can remember which extend command was given
    private enum Command {
        NONE,
        HANG,
        STOP
    }

    private Command command = Command.NONE;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private final String HANG_NAME = CenterStageRobot.HardwareName.HANG.hwName;

    private DcMotor8863 leftHangMotor;
    private DcMotor8863 rightHangMotor;
    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;

    // flags used in this class

    // initialization is complete
    private boolean initComplete = true;

    //hang position
    private double hangPosition = -6.0; // inches

    private Phase phase = Phase.TELEOP;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStageHang(HardwareMap hardwareMap, Telemetry telemetry) {

        // create the motor for the lift
        leftHangMotor = new DcMotor8863(CenterStageRobot.HardwareName.LEFT_HANG_MOTOR.hwName, hardwareMap, telemetry);
        leftHangMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_312);
        leftHangMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftHangMotor.setMovementPerRev(25/25.4 * Math.PI); // 25mm diameter spool, convert to inches

        rightHangMotor = new DcMotor8863(CenterStageRobot.HardwareName.RIGHT_HANG_MOTOR.hwName, hardwareMap, telemetry);
        rightHangMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_312);
        rightHangMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightHangMotor.setMovementPerRev(25/25.4 * Math.PI); // 25mm diameter spool, convert to inches

        // init has not been started yet
        initComplete = true;
        // the lift can be commanded to do something, like the init
        commandComplete = true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String getName() {
        return HANG_NAME;
    }

    @Override
    public void shutdown() {
        //todo figure out what to do for shutdown
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
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
    public void timedUpdate(double timerValueMsec) {

    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + state.toString());
        }
    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public String getState() {
        return state.toString();
    }

    @Override
    public boolean init(Configuration config) {
        // there is no init for this mechanism yet
        logCommand("Init complete");
        commandComplete = true;
        return true;
    }

    @Override
    // There is no init for the hang mechanism since it is currently only two motors
    public boolean isInitComplete() {
        return initComplete;
    }

    public boolean isPositionReached() {
        if (state == State.DONE) {
            return true;
        } else {
            return false;
        }
    }

    public void setPhaseAutonomous() {
        phase = Phase.AUTONOMOUS;
    }

    public void setPhaseTeleop() {
        phase = Phase.TELEOP;
    }

    //********************************************************************************
    // Public commands for controlling the lift
    //********************************************************************************

    public void hang() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Hanging");
            commandComplete = false;
            //command to start extension
            state = State.HANGING;
            // remember the command for later
            command = Command.HANG;
            logCommand(command.toString());
            leftHangMotor.moveToPosition(0.5, hangPosition, DcMotor8863.FinishBehavior.HOLD);
            rightHangMotor.moveToPosition(0.5, hangPosition, DcMotor8863.FinishBehavior.HOLD);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void stop() {
        // no lockout for this
        logCommand("Stop Hanging");
        commandComplete = true;
        leftHangMotor.stop();
        rightHangMotor.stop();
        state = State.STOPPED;
    }

    public void runHangMotors(double power) {
        leftHangMotor.setPower(power);
        rightHangMotor.setPower(power);
    }

    //********************************************************************************
    // Public commands for testing the hang
    //********************************************************************************

    public boolean isCommandComplete() {
        return commandComplete;
    }

    public int getLeftMotorEncoder() {
        return leftHangMotor.getCurrentPosition();
    }

    public int getRightMotorEncoder() {
        return rightHangMotor.getCurrentPosition();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        leftHangMotor.update();
        rightHangMotor.update();
        logState();

        switch (state) {
            //********************************************************************************
            // INIT states
            //********************************************************************************

            case READY:
                // do nothing. The lift is waiting for a command
                break;

            case HANGING: {
                if (leftHangMotor.isMovementComplete() && rightHangMotor.isMovementComplete()) {
                    commandComplete = true;
                    state = State.DONE;
                }
            }
            break;

            case DONE: {
                // do nothing here
            }
            break;

            case STOPPED: {
                // do nothing, just wait for another command
            }
            break;
        }
    }
}
