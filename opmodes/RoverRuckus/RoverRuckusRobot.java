package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.Collector;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorArm;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

import java.util.EmptyStackException;

public class RoverRuckusRobot {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum RobotMode {
        AUTONOMOUS,
        TELEOP
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    // Here are all of the objects that make up the entire robot
    // note that the IMU is an object in the drive train
    public RobotMode robotMode;
    public DriveTrain driveTrain;
    public CollectorGB collector;
    public Telemetry telemetry;
    public DeliveryLiftSystem deliveryLiftSystem;
    public CollectorArm collectorArm;

    private ElapsedTime timer;
    private DataLogging logFile;

    private TransferScoringCommands previousTransferScoringCommand;
    private TransferScoringStates previousTransferScoringState;

    private ToCollectStates previousToCollectState;
    private ToCollectCommands previousToCollectCommand;

    private boolean loggingOn = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    public void enableDataLogging() {
        this.loggingOn = true;
        collectorArm.setDataLog(logFile);
        collectorArm.enableDataLogging();
        collector.setDataLog(logFile);
        collector.enableDataLogging();
        deliveryLiftSystem.setDataLog(logFile);
        deliveryLiftSystem.enableDataLogging();
    }

    public void disableDataLogging() {
        this.loggingOn = false;
        collectorArm.disableDataLogging();
        collector.disableDataLogging();
        deliveryLiftSystem.disableDataLogging();
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    private RoverRuckusRobot(HardwareMap hardwareMap, RobotMode robotMode, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging logFile) {
        this.telemetry = telemetry;

        collector = new CollectorGB(hardwareMap, telemetry);
        collectorArm = new CollectorArm(hardwareMap, telemetry);
        deliveryLiftSystem = new DeliveryLiftSystem(hardwareMap, telemetry);

        timer = new ElapsedTime();

        // note that we are ignoring the logFile that is passed as an argument. Decided to set that up
        // here
        // What? Why? No way.
        //this.logFile = new DataLogging("Teleop", telemetry);
        this.logFile = logFile;
        logFile.logData("****ROBOT INTIALIZING!****");

        if (robotMode == RobotMode.AUTONOMOUS) {
            // create the robot for autonomous
            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
            driveTrain.setLogFile(logFile);
            driveTrain.enableLogTurns();
            driveTrain.enableLogDrive();
        } else {
            // create the robot for teleop
            driveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap, telemetry);
        }
        init(telemetry);
    }

    public static RoverRuckusRobot createRobotForAutonomous(HardwareMap hardwareMap, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        RoverRuckusRobot robot = new RoverRuckusRobot(hardwareMap, RobotMode.AUTONOMOUS, telemetry, teamColor, dataLog);
        return robot;
    }

    public static RoverRuckusRobot createRobotForTeleop(HardwareMap hardwareMap, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        RoverRuckusRobot robot = new RoverRuckusRobot(hardwareMap, RobotMode.TELEOP, telemetry, teamColor, dataLog);
        return robot;
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

    public void init(Telemetry telemetry) {
        enableDataLogging();
        collector.setDataLog(logFile);
        collector.init();

        collectorArm.setDataLog(logFile);
        collectorArm.init();

        deliveryLiftSystem.setDataLog(logFile);
        deliveryLiftSystem.init();

        transferScoringInit();
        toCollectionPositionInit();
        toggleTransferButtonCommandInit();
        hangingStateMachineInit();
    }

    public void setupForRun() {
        logFile.logData("****ROBOT RUNNING!****");
    }

    public void update() {
        collector.update();
        deliveryLiftSystem.update();
        collectorArm.update();
        transferScoringStateMachine();
        toCollectStateMachine();
        updateHangingStateMachine();
    }

    //*********************************************************************************************
    // robot commands
    //*********************************************************************************************

    public void dehang() {
        deliveryLiftSystem.deliveryBoxToOutOfWay();
        deliveryLiftSystem.dehang();
    }

//    public void undehang() {
//        deliveryLiftSystem.undehang();
//        delay(2000);
//    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        driveTrain.shutdown();
        // collector.shutdown();
    }

//    public void setupForHang() {
//        log("DRIVER COMMANDED SETUP FOR HANG");
//        deliveryLiftSystem.deliveryBoxToOutOfWay();
//        deliveryLiftSystem.goToSetupHang();
//        collector.gateServoToResetPosition();
//        collectorArm.extensionArmReset();
//        //sleep(2000);
//        collectorArm.rotationArmGoToHome();
//    }

    public void goToLatchPosition() {
        log("DRIVER COMMANDED GO TO LATCH POSITION");
        deliveryLiftSystem.deliveryBoxToOutOfWay();
        deliveryLiftSystem.goToLatch();
    }

    public void hang() {
        log("DRIVER COMMANDED HANG");
        deliveryLiftSystem.deliveryBoxToOutOfWay();
        deliveryLiftSystem.goToHang();
    }

    //*********************************************************************************************
    //*********************************************************************************************
    //transfer & scoring control
    //*********************************************************************************************
    //*********************************************************************************************

    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);

        }
    }

    public enum TransferScoringStates {
        START,
        COLLECTOR_OFF,
        LIFT_HEIGHT,
        COLLECTOR_ARM,
        TRANSFER_READY,
        TRANSFER,
        TRANSFER_ADJUST,
        SETUP_FOR_SCORE,
        SCORED,
        RESET;
    }

    public enum TransferScoringCommands {
        GO_TO_TRANSFER_POSITION,
        ADJUST_TRANSFER_ANGLE_TOWARDS_STOP,
        ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR,
        TRANSFER_MINERALS,
        CONFIRM_TRANSFER_SUCCESS,
        CONFIRM_TRANSFER_FAILED,
        FIX_JAM,
        SCORE,
        EMPTY;
    }

    private TransferScoringCommands transferScoringCommand;
    private TransferScoringStates transferScoringState;


    /**
     * This is for a state machine that handles the button pushes to control the transfer.
     */
    private enum TransferButtonCommands {
        WAIT_FOR_GO_TO_TRANSFER,
        WAIT_FOR_START_TRANSFER,
        WAIT_FOR_CONFIRM_TRANSFER_COMPLETE
    }

    private TransferButtonCommands transferButtonCommand = TransferButtonCommands.WAIT_FOR_GO_TO_TRANSFER;
    private TransferButtonCommands previousTransferButtonCommand = TransferButtonCommands.WAIT_FOR_GO_TO_TRANSFER;

    //*********************************************************************************************
    //transfer & scoring commands
    //*********************************************************************************************

    private void toggleTransferButtonCommandInit() {
        transferButtonCommand = TransferButtonCommands.WAIT_FOR_GO_TO_TRANSFER;
        previousTransferButtonCommand = TransferButtonCommands.WAIT_FOR_GO_TO_TRANSFER;
    }
    /**
     * This is a state machine that controls a single button that is meant to control the transfer.
     * It goes from moving the collector arm to transfer position, starting the transfer, then
     * confirming the transfer is complete and then back to waiting to move the arm again.
     */
    public void toggleTransferButtonCommand() {
        logTransferButtonCommand(transferButtonCommand);
        switch (transferButtonCommand) {
            case WAIT_FOR_GO_TO_TRANSFER:
                // sit and wait for the A button to be pushed
                // Only allow the A button to command a go to transfer position if the robot is not already
                // started into the transfer cycle.
                if (transferScoringState == TransferScoringStates.START) {
                    goToTransferPosition();
                    // the next button push will be to start the transfer
                    transferButtonCommand = TransferButtonCommands.WAIT_FOR_START_TRANSFER;
                }
                break;
            // sit and wait for the A button to be pushed
            // Only allow the A button to command start transfer if we are in the
            // TRANSFER_ADJUST state of the transfer sooring state machine
            // This means the driver can only start a transfer when the collector arm is in the proper
            // vertical position.
            case WAIT_FOR_START_TRANSFER:
                if (transferScoringState == TransferScoringStates.TRANSFER_ADJUST) {
                    transferMinerals();
                    // the next button push will be to confirm the transfer is complete
                    transferButtonCommand = TransferButtonCommands.WAIT_FOR_CONFIRM_TRANSFER_COMPLETE;
                }
                break;
            // sit and wait for the A button to be pushed
            // Only allow the A button to command confirm transfer complete if we are in the
            // TRANSFER state of the transfer sooring state machine
            // This means the driver can only confirm a transfer is complete after the robot has started
            // to transfer.
            case WAIT_FOR_CONFIRM_TRANSFER_COMPLETE:
                if (transferScoringState == TransferScoringStates.TRANSFER) {
                    confirmTransferSuccess();
                    // the button pushes have gone full cycle. Move to the beginning of the cycle
                    transferButtonCommand = TransferButtonCommands.WAIT_FOR_GO_TO_TRANSFER;
                }
                break;
        }
    }

    private void logTransferButtonCommand(TransferButtonCommands transferButtonCommand) {
        if (logFile != null && loggingOn) {
            if (transferButtonCommand != previousTransferButtonCommand) {
                logFile.logData("Transfer Button command ", transferButtonCommand.toString());
                previousTransferButtonCommand = transferButtonCommand;
            }
        }
    }

    public void goToTransferPosition() {
        log("DRIVER COMMANDED TO GO TO TRANSFER POSITION");
        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
    }

    public void adjustCollectorArmTranferAngleTowardsStop() {
        log("DRIVER COMMANDED ADJUST TRANSFER ANGLE TOWARDS STOP");
        transferScoringCommand = TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP;
    }

    public void adjustCollectorArmTranferAngleTowardsFloor() {
        log("DRIVER COMMANDED ADJUST TRANSFER ANGLE TOWARDS FLOOR");
        transferScoringCommand = TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR;
    }

    public void transferMinerals() {
        log("DRIVER COMMANDED TRANSFER MINERALS");
        transferScoringCommand = TransferScoringCommands.TRANSFER_MINERALS;
    }

    public void confirmTransferSuccess() {
        log("DRIVER CONFIRMED TRANSFER MINERALS COMPLETE");
        transferScoringCommand = TransferScoringCommands.CONFIRM_TRANSFER_SUCCESS;
    }

    public void confirmTransferFailed() {
        log("DRIVER CONFIRMED TRANSFER MINERALS FAILURE");
        transferScoringCommand = TransferScoringCommands.CONFIRM_TRANSFER_FAILED;
    }

    public void clearTransferJam() {
        log("DRIVER COMMANDED TO FIX TRANSFER JAM");
        transferScoringCommand = TransferScoringCommands.FIX_JAM;
    }

    public void score() {
        log("DRIVER COMMANDED TO SCORE MINERALS");
        transferScoringCommand = TransferScoringCommands.SCORE;
    }

    public void resetTransferScoringControl() {
        log("DRIVER COMMANDED TRANSER SCORING STATE MACHINE RESET");
        transferScoringInit();
    }

    //*********************************************************************************************
    //transfer & scoring state machine
    //*********************************************************************************************

    private void transferScoringInit() {
        transferScoringState = TransferScoringStates.START;
        previousTransferScoringState = transferScoringState;
        transferScoringCommand = TransferScoringCommands.EMPTY;
        previousTransferScoringCommand = transferScoringCommand;
    }

    private void transferScoringStateMachine() {
        //telemetry.addData("Current State Of Transfer = ", transferScoringState.toString());
        //telemetry.addData("Current Command Of Transfer = ", transferScoringCommand.toString());
        logTransferScoringState(transferScoringState, transferScoringCommand);

        switch (transferScoringState) {
            case START:
                switch (transferScoringCommand) {
                    case GO_TO_TRANSFER_POSITION:
                        collector.turnCollectorOff();
                        transferScoringState = TransferScoringStates.COLLECTOR_OFF;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case TRANSFER_MINERALS:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_SUCCESS);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_FAILED);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case EMPTY:
                        break;
                    case FIX_JAM:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.FIX_JAM);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case SCORE:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.SCORE);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                }
                break;

            case COLLECTOR_OFF:
                switch (transferScoringCommand) {
                    case GO_TO_TRANSFER_POSITION:
                        deliveryLiftSystem.goToTransfer();
                        transferScoringState = TransferScoringStates.LIFT_HEIGHT;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case TRANSFER_MINERALS:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.CONFIRM_TRANSFER_SUCCESS);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_FAILED);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case EMPTY:
                        break;
                    case FIX_JAM:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.FIX_JAM);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case SCORE:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.SCORE);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                }
                break;

            case LIFT_HEIGHT:
                switch (transferScoringCommand) {
                    case GO_TO_TRANSFER_POSITION:
                        collectorArm.raiseArm();
                        timer.reset();
                        deliveryLiftSystem.deliveryBoxToTransfer();
                        transferScoringState = TransferScoringStates.COLLECTOR_ARM;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case TRANSFER_MINERALS:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.CONFIRM_TRANSFER_SUCCESS);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_FAILED);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case EMPTY:
                        break;
                    case FIX_JAM:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.FIX_JAM);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case SCORE:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.SCORE);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                }
                break;

            case COLLECTOR_ARM:
                switch (transferScoringCommand) {
                    case GO_TO_TRANSFER_POSITION:
                        if (deliveryLiftSystem.isLiftMovementComplete() && collectorArm.isRotationExtensionComplete()) {
                            transferScoringState = TransferScoringStates.TRANSFER_ADJUST;
                        }
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case TRANSFER_MINERALS:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.CONFIRM_TRANSFER_SUCCESS);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_FAILED);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case EMPTY:
                        break;
                    case FIX_JAM:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.FIX_JAM);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case SCORE:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.SCORE);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                }
                break;

            // once the rotation arm is in position, give the driver the opportunity to adjust
            // its position
            case TRANSFER_ADJUST:
                switch (transferScoringCommand) {
                    case GO_TO_TRANSFER_POSITION:
                        // sit and wait for one of 3 commands:
                        //    ADJUST_TRANSFER_ANGLE_TOWARDS_STOP
                        //    ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR
                        //    TRANSFER_MINERALS
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        collectorArm.rotationArmTransferAngleTowardsStop();
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        collectorArm.rotationArmTransferAngleTowardsFloor();
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case TRANSFER_MINERALS:
                        if (deliveryLiftSystem.isLiftMovementComplete() && collectorArm.isRotationExtensionComplete()) {
                            transferScoringState = TransferScoringStates.TRANSFER_READY;
                        }
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.CONFIRM_TRANSFER_SUCCESS);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_FAILED);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case EMPTY:
                        break;
                    case FIX_JAM:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.FIX_JAM);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case SCORE:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.SCORE);
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                }
                break;

            case TRANSFER_READY:
                switch (transferScoringCommand) {
                    case GO_TO_TRANSFER_POSITION:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case TRANSFER_MINERALS:
                        // When enough time has passed so that delivery box has moved into position,
                        // tell the collector to deliver the minerals
                        if (timer.milliseconds() > 1000) {
                            collector.deliverMineralsOn();
                            transferScoringState = TransferScoringStates.TRANSFER;
                        }
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.CONFIRM_TRANSFER_SUCCESS);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_FAILED);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case EMPTY:
                        break;
                    case FIX_JAM:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.FIX_JAM);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case SCORE:
                        // another command cannot interrupt the transfer, reset the command
                        logIgnoreCommand(TransferScoringCommands.SCORE);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                }
                break;

            case TRANSFER:
                switch (transferScoringCommand) {
                    case GO_TO_TRANSFER_POSITION:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.GO_TO_TRANSFER_POSITION);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                    case TRANSFER_MINERALS:
                        // hang out and wait for the driver to confirm transfer complete or to fix a
                        // a jam
                        //logIgnoreCommand(TransferScoringCommands.TRANSFER);
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // tell the collector to stop the delivery process
                        collector.deliverMineralsComplete();
                        // raise the lift to scoring position
                        deliveryLiftSystem.deliveryBoxToOutOfWay();
                        deliveryLiftSystem.goToScoringPosition();
                        transferScoringState = TransferScoringStates.SETUP_FOR_SCORE;
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // tell the collector to stop the delivery process
                        collector.deliverMineralsComplete();
                        // raise the lift to scoring position
                        deliveryLiftSystem.goToHome();
                        // reset the state machine
                        transferScoringState = TransferScoringStates.RESET;
                        break;
                    case FIX_JAM:
                        collector.fixTransferJam();
                        transferScoringState = TransferScoringStates.TRANSFER_READY;
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        break;
                    case EMPTY:
                        break;
                    case SCORE:
                        // can't score yet, the transfer has not been confirmed. Wait for the confirm
                        // transfer or fix jam commands
                        logDoNothingCommand(TransferScoringCommands.SCORE);
                        logDoNothingCommand(TransferScoringCommands.TRANSFER_MINERALS);
                        break;
                }
                break;

            case SETUP_FOR_SCORE:
                switch (transferScoringCommand) {
                    case SCORE:
                        if (deliveryLiftSystem.isLiftMovementComplete()) {
                            timer.reset();
                            deliveryLiftSystem.deliveryBoxToDump();
                            transferScoringState = TransferScoringStates.SCORED;
                        }
                        break;
                    case FIX_JAM:
                        // Can't fix a jam, the transfer has already been finished. Do nothing but
                        // wait for a score command.
                        logDoNothingCommand(TransferScoringCommands.FIX_JAM);
                        break;
                    case GO_TO_TRANSFER_POSITION:
                        // driver is asking to transfer again. I guess it is possible this is valid.
                        // Maybe they did not see a mineral that stayed in the collector. Start the
                        // the process over again.
                        transferScoringCommand = TransferScoringCommands.GO_TO_TRANSFER_POSITION;
                        transferScoringState = TransferScoringStates.START;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_STOP:
                        // command is not relevant. Don't do anything.
                        logIgnoreCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case ADJUST_TRANSFER_ANGLE_TOWARDS_FLOOR:
                        // command is not relevant. Don't do anything.
                        logIgnoreCommand(TransferScoringCommands.ADJUST_TRANSFER_ANGLE_TOWARDS_STOP);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case CONFIRM_TRANSFER_FAILED:
                        // command is not relevant. Don't do anything.
                        logDoNothingCommand(TransferScoringCommands.CONFIRM_TRANSFER_FAILED);
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                    case EMPTY:
                        // wait in this state for a score command
                        break;
                    case CONFIRM_TRANSFER_SUCCESS:
                        // Confirm transfer got us into this state. So this command is not relevant.
                        // wait for a real command
                        transferScoringCommand = TransferScoringCommands.EMPTY;
                        break;
                }
                break;

            case SCORED:
                if (timer.milliseconds() > 1500) {
                    // return the delivery box to home from scoring position
                    deliveryLiftSystem.deliveryBoxToHome();
                    // lower lift to transfer position - added to save time in prepping for lowering the arm to collect
                    // does waste time if the next move is to setup for hang.
                    deliveryLiftSystem.goToTransfer();
                    transferScoringState = TransferScoringStates.RESET;
                }
                break;

            case RESET:
                transferScoringState = TransferScoringStates.START;
                transferScoringCommand = TransferScoringCommands.EMPTY;
                break;
        }
    }

    private void logTransferScoringState(TransferScoringStates transferScoringState, TransferScoringCommands transferScoringCommand) {
        if (logFile != null && loggingOn) {
            if (transferScoringState != previousTransferScoringState || transferScoringCommand != previousTransferScoringCommand) {
                logFile.logData("Transfer Scoring Control ", transferScoringState.toString(), transferScoringCommand.toString());
                previousTransferScoringState = transferScoringState;
                previousTransferScoringCommand = transferScoringCommand;
            }
        }
    }

    private void logIgnoreCommand(TransferScoringCommands transferScoringCommand) {
        if (logFile != null && loggingOn) {
            logFile.logData("Ignoring command = ", transferScoringCommand.toString());
        }
    }

    private void logDoNothingCommand(TransferScoringCommands transferScoringCommand) {
        if (logFile != null && loggingOn) {
            logFile.logData("Doing nothing about command = ", transferScoringCommand.toString());
        }
    }

    //*********************************************************************************************
    //*********************************************************************************************
    //to collection position control
    //*********************************************************************************************
    //*********************************************************************************************

    private enum ToCollectStates {
        START,
        LOWER_LIFT,
        READY_TO_COLLECT,
        DONE;
    }

    private enum ToCollectCommands {
        LOWER_COLLECTION_SYSTEM,
        EMPTY;
    }

    private ToCollectStates toCollectState;
    private ToCollectCommands toCollectCommand;

    private void toCollectionPositionInit() {
        toCollectCommand = ToCollectCommands.EMPTY;
        previousToCollectCommand = toCollectCommand;
        toCollectState = ToCollectStates.START;
        previousToCollectState = toCollectState;
    }

    //*********************************************************************************************
    //to collection position commands
    //*********************************************************************************************

    public void lowerCollectorArmToCollect() {
        log("DRIVER COMMANDED LOWER COLLECTOR ARM TO COLLECT POSITION");
        toCollectCommand = ToCollectCommands.LOWER_COLLECTION_SYSTEM;
        toCollectState = ToCollectStates.START;
    }

    public void resetToColletionPositionControl() {
        log("DRIVER COMMANDED TO COLLECT STATE MACHINE RESET");
        toCollectionPositionInit();
    }

    //*********************************************************************************************
    //to collection position state machine
    //*********************************************************************************************

    private void toCollectStateMachine() {
        logToCollectState(toCollectState, toCollectCommand);

        switch (toCollectState) {
            case START:
                switch (toCollectCommand) {
                    case LOWER_COLLECTION_SYSTEM:
                        // move the lift to transfer, which is almost the bottom. This avoids moving
                        // it later when the transfer is started.
                        deliveryLiftSystem.goToTransfer();
                        deliveryLiftSystem.deliveryBoxToHome();
                        toCollectState = ToCollectStates.LOWER_LIFT;
                        break;
                    case EMPTY:
                        break;
                }
            case LOWER_LIFT:
                switch (toCollectCommand) {
                    case LOWER_COLLECTION_SYSTEM:
                        collectorArm.dropArm();
                        toCollectState = ToCollectStates.READY_TO_COLLECT;
                        break;
                    case EMPTY:
                        break;
                }
            case READY_TO_COLLECT:
                switch (toCollectCommand) {
                    case LOWER_COLLECTION_SYSTEM:
                        if (collectorArm.isRotationExtensionComplete()) {
                            collectorArm.rotationArmFloatArm();
                            toCollectState = ToCollectStates.DONE;
                        }
                        break;
                    case EMPTY:
                        break;
                }
            case DONE:
                toCollectState = ToCollectStates.START;
                toCollectCommand = ToCollectCommands.EMPTY;
                break;
        }
    }

    private void logToCollectState(ToCollectStates toCollectState, ToCollectCommands toCollectCommand) {
        if (logFile != null && loggingOn) {
            if (toCollectState != previousToCollectState || toCollectCommand != previousToCollectCommand) {
                logFile.logData("To Collection Control ", toCollectState.toString(), toCollectCommand.toString());
                previousToCollectState = toCollectState;
                previousToCollectCommand = toCollectCommand;
            }
        }
    }

    private void logIgnoreCommand(ToCollectCommands toCollectCommand) {
        if (logFile != null && loggingOn) {
            logFile.logData("Ignoring command = ", toCollectCommand.toString());
        }
    }
    //*********************************************************************************************
    //state machine for hanging
    //*********************************************************************************************

    /**
     * Command to be called when button is pressed. Ignores button press when the setup for hang
     * is already running.
     */
    public void setupForHang() {
        if (hangingSetupState == HangingSetup.IDLE) {
            hangingSetupState = HangingSetup.START;
        }
    }

    private enum HangingSetup {
        IDLE,
        START,
        WAIT_FOR_LIFT,
        READY_TO_LATCH
    }

    private HangingSetup hangingSetupState = HangingSetup.START;
    private HangingSetup previousHangingSetupState = HangingSetup.START;

    private void hangingStateMachineInit() {
        hangingSetupState = HangingSetup.IDLE;
        previousHangingSetupState = hangingSetupState;
    }

    public void updateHangingStateMachine() {
        logHangingStateMachineState(hangingSetupState);
        switch (hangingSetupState) {
            case IDLE:
                // do nothing until the driver presses the button
                break;
            case START:
                deliveryLiftSystem.deliveryBoxToOutOfWay();
                deliveryLiftSystem.goToSetupHang();
                collector.gateServoToResetPosition();
                collectorArm.extensionArmReset();
                hangingSetupState = HangingSetup.WAIT_FOR_LIFT;
                break;
            case WAIT_FOR_LIFT:
                if (deliveryLiftSystem.isLiftMovementComplete()&& collectorArm.isExtensionArmMovementComplete()) {
                    collectorArm.rotationArmGoToHome();
                    hangingSetupState = HangingSetup.READY_TO_LATCH;
                }
                break;
            case READY_TO_LATCH:
                hangingSetupState = HangingSetup.IDLE;
                break;
        }
    }

    private void logHangingStateMachineState(HangingSetup hangingSetupState) {
        if (logFile != null && loggingOn) {
            if (hangingSetupState != previousHangingSetupState) {
                logFile.logData("Hanging Setup ", hangingSetupState.toString());
                previousHangingSetupState = hangingSetupState;
            }
        }
    }


}
