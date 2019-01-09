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
        this.logFile = new DataLogging("Teleop", telemetry);

        if (robotMode == RobotMode.AUTONOMOUS) {
            // create the robot for autonomous
            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
            //allianceColorSwitch = new AllianceColorSwitch(hardwareMap, telemetry);
            //allianceColor = allianceColorSwitch.getAllianceColor();
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
        resetScoringInit();
    }

    public void setupForRun() {
    }

    public void update() {
        collector.update();
        deliveryLiftSystem.update();
        collectorArm.update();
        transferScoringStateMachine();
        toCollectStateMachine();
    }

    //*********************************************************************************************
    // robot commands
    //*********************************************************************************************

    public void dehang() {
//        collectorArm.goToDehang();
//        while (collectorArm.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {
//        }
        //delay(2000);
        //collectorArm.update();
        deliveryLiftSystem.dehang();
        //while (deliveryLiftSystem.update() != DcMotor8863.MotorState.COMPLETE_FLOAT){
        //}
        //delay(5000);
        //deliveryLiftSystem.update();
//        collectorArm.goToClearStar();
//        while (collectorArm.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {
//        }
        //collectorArm.update();
        //delay (1000);
        deliveryLiftSystem.deliveryBoxToTransfer();
//        delay(500);
//        collectorArm.goToHome();
//        while (collectorArm.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {
//        }
        //collectorArm.update();
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

    //*********************************************************************************************
    //transfer & scoring state machine
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
        SETUP_FOR_SCORE,
        SCORED,
        RESET;
    }

    public enum TransferScoringCommands {
        TRANSFER,
        CONFIRM_TRANSFER,
        FIX_JAM,
        SCORE,
        EMPTY;
    }

    private TransferScoringCommands transferScoringCommand;
    private TransferScoringStates transferScoringState;

    public void transferMinerals() {
        log("Commanded to transfer minerals");
        transferScoringCommand = TransferScoringCommands.TRANSFER;
    }

    public void confirmTransfer() {
        log("Transfer minerals confirmed complete by driver");
        transferScoringCommand = TransferScoringCommands.CONFIRM_TRANSFER;
    }

    public void clearTransferJam() {
        log("Commanded to fix transfer jam");
        transferScoringCommand = TransferScoringCommands.FIX_JAM;
    }

    public void score() {
        log("Commanded to score minerals");
        transferScoringCommand = TransferScoringCommands.SCORE;
    }

    public void transferScoringInit() {
        transferScoringState = TransferScoringStates.START;
        transferScoringCommand = TransferScoringCommands.EMPTY;
    }

    public void transferScoringStateMachine() {
        telemetry.addData("Current State Of Transfer = ", transferScoringState.toString());
        telemetry.addData("Current Command Of Transfer = ", transferScoringCommand.toString());
        logTransferScoringState(transferScoringState, transferScoringCommand);

        switch (transferScoringState) {
            case START:
                switch (transferScoringCommand) {
                    case TRANSFER:
                        collector.turnCollectorOff();
                        transferScoringState = TransferScoringStates.COLLECTOR_OFF;
                        break;
                    case CONFIRM_TRANSFER:
                    case EMPTY:
                    case FIX_JAM:
                    case SCORE:
                        break;
                }
                break;
            case COLLECTOR_OFF:
                switch (transferScoringCommand) {
                    case TRANSFER:
                        deliveryLiftSystem.goToBottom();
                        transferScoringState = TransferScoringStates.LIFT_HEIGHT;
                        break;
                    case CONFIRM_TRANSFER:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case EMPTY:
                    case FIX_JAM:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case SCORE:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case LIFT_HEIGHT:
                switch (transferScoringCommand) {
                    case TRANSFER:
                        collectorArm.raiseArm();
                        transferScoringState = TransferScoringStates.COLLECTOR_ARM;
                        break;
                    case CONFIRM_TRANSFER:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case EMPTY:
                    case FIX_JAM:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case SCORE:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case COLLECTOR_ARM:
                switch (transferScoringCommand) {
                    case TRANSFER:
                        if (deliveryLiftSystem.isLiftMovementComplete() && collectorArm.isRotationExtensionComplete()) {
                            timer.reset();
                            deliveryLiftSystem.deliveryBoxToTransfer();
                            transferScoringState = TransferScoringStates.TRANSFER_READY;
                        }
                    case CONFIRM_TRANSFER:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case EMPTY:
                    case FIX_JAM:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case SCORE:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case TRANSFER_READY:
                switch (transferScoringCommand) {
                    case TRANSFER:
                        if (timer.milliseconds() > 1000) {
                            collector.deliverMineralsOn();
                            transferScoringState = TransferScoringStates.TRANSFER;
                        }
                    case CONFIRM_TRANSFER:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case EMPTY:
                    case FIX_JAM:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                    case SCORE:
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case TRANSFER:
                switch (transferScoringCommand) {
                    case CONFIRM_TRANSFER:
                        collector.deliverMineralsComplete();
                        deliveryLiftSystem.goToScoringPosition();
                        transferScoringState = TransferScoringStates.SETUP_FOR_SCORE;
                        break;
                    case FIX_JAM:
                        collector.fixTransferJam();
                        transferScoringState = TransferScoringStates.TRANSFER_READY;
                        transferScoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                    case TRANSFER:
                        transferScoringCommand = TransferScoringCommands.CONFIRM_TRANSFER;
                    case EMPTY:
                    case SCORE:
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
                    case TRANSFER:
                    case EMPTY:
                    case CONFIRM_TRANSFER:
                        break;

                }
                break;
            case SCORED:
                if (timer.milliseconds() > 1500) {
                    deliveryLiftSystem.deliveryBoxToHome();
                    transferScoringState = TransferScoringStates.RESET;
                }
                break;
            case RESET:
                transferScoringState = TransferScoringStates.START;
                break;
        }
    }

    private void logTransferScoringState(TransferScoringStates transferScoringState, TransferScoringCommands transferScoringCommand) {
        if (logFile != null && loggingOn) {
            if(transferScoringState != previousTransferScoringState ||transferScoringCommand != previousTransferScoringCommand) {
                logFile.logData("Transfer Scoring Control ",transferScoringState.toString(), transferScoringCommand.toString());
                previousTransferScoringState = transferScoringState;
                previousTransferScoringCommand = transferScoringCommand;
            }
        }
    }

    //*********************************************************************************************
    //to collect state machine
    //*********************************************************************************************

    private enum ToCollectStates {
        START,
        LOWER_LIFT,
        READY_TO_COLLECT,
        DONE;
    }

    private enum ToCollectCommands {
        RESET_LIFT,
        RESET_DELIVERY_BOX,
        LOWER_COLLECTION_SYSTEM,
        EMPTY;
    }

    private void resetScoringInit() {
        toCollectCommand = ToCollectCommands.EMPTY;
        toCollectState = ToCollectStates.START;
    }

    private void resetScoring() {
        toCollectCommand = ToCollectCommands.RESET_LIFT;
    }

    public void lowerCollectorArmToCollect() {
        log("Commanded to lower collector arm to collect position");
        toCollectCommand = ToCollectCommands.LOWER_COLLECTION_SYSTEM;
        toCollectState = ToCollectStates.START;
    }

    private ToCollectStates toCollectState;
    private ToCollectCommands toCollectCommand;

    private void toCollectStateMachine() {
        logToCollectState(toCollectState, toCollectCommand);
        switch (toCollectState) {
            case START:
                switch (toCollectCommand) {
                    case LOWER_COLLECTION_SYSTEM:
                        deliveryLiftSystem.goToHome();
                        deliveryLiftSystem.deliveryBoxToTransfer();
                        toCollectState = ToCollectStates.LOWER_LIFT;
                        break;
                }
            case LOWER_LIFT:
                switch (toCollectCommand) {
                    case LOWER_COLLECTION_SYSTEM:
                        collectorArm.dropArm();
                        toCollectState = ToCollectStates.READY_TO_COLLECT;
                        break;
                }
            case READY_TO_COLLECT:
                switch (toCollectCommand) {
                    case LOWER_COLLECTION_SYSTEM:
                        if (collectorArm.isRotationExtensionComplete()) {
                            toCollectState = ToCollectStates.DONE;
                            collectorArm.rotationArmFloatArm();
                        }
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
            if(toCollectState != previousToCollectState || toCollectCommand != previousToCollectCommand) {
                logFile.logData("To Collection Control ",toCollectState.toString(), toCollectCommand.toString());
                previousToCollectState = toCollectState;
                previousToCollectCommand = toCollectCommand;
            }
        }
    }
}
