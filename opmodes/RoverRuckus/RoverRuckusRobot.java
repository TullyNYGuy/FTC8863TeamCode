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

    private RoverRuckusRobot(HardwareMap hardwareMap, RobotMode robotMode, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        this.telemetry = telemetry;

        collector = new CollectorGB(hardwareMap, telemetry);
        collectorArm = new CollectorArm(hardwareMap, telemetry);
        deliveryLiftSystem = new DeliveryLiftSystem(hardwareMap, telemetry);
        timer = new ElapsedTime();

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
        collector.initialize();
        collectorArm.init();
        deliveryLiftSystem.init();
        transferScoringInit();
    }

    public void setupForRun() {
    }

    public void update() {
        collector.update();
        deliveryLiftSystem.update();
        collectorArm.update();
        transferScoringStateMachine();
    }

    public void dehang (){
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

    public void undehang() {
        deliveryLiftSystem.undehang();
        delay(2000);
    }

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

    //transfer & scoring state machine

    public enum TransferScoringStates{
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

    public enum TransferScoringCommands{
        TRANSFER,
        CONFIRM_TRANSFER,
        FIX_JAM,
        SCORE,
        EMPTY;
    }

    private TransferScoringCommands scoringCommand;
    private TransferScoringStates scoringState;

    public void transferMinerals(){
        scoringCommand = TransferScoringCommands.TRANSFER;
    }

    public void confirmTransfer(){
        scoringCommand = TransferScoringCommands.CONFIRM_TRANSFER;
    }

    public void clearTransferJam(){
        scoringCommand = TransferScoringCommands.FIX_JAM;
    }

    public void score(){
        scoringCommand = TransferScoringCommands.SCORE;
    }

    public void transferScoringInit(){
        scoringState = TransferScoringStates.START;
        scoringCommand = TransferScoringCommands.EMPTY;
    }

    public void transferScoringStateMachine(){
        telemetry.addData("Current State Of Transfer = ",scoringState.toString());
        telemetry.addData("Current Command Of Transfer = ",scoringCommand.toString());
        switch(scoringState){
            case START:
                switch(scoringCommand){
                    case TRANSFER:
                        collector.turnCollectorOff();
                        scoringState = TransferScoringStates.COLLECTOR_OFF;
                        break;
                    case CONFIRM_TRANSFER:
                    case EMPTY:
                    case FIX_JAM:
                    case SCORE:
                        break;
                }
                break;
            case COLLECTOR_OFF:
                switch (scoringCommand){
                    case TRANSFER:
                        deliveryLiftSystem.goToTop();
                        scoringState = TransferScoringStates.LIFT_HEIGHT;
                        break;
                    case CONFIRM_TRANSFER:
                    case EMPTY:
                    case FIX_JAM:
                    case SCORE:
                        scoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case LIFT_HEIGHT:
                switch(scoringCommand){
                    case TRANSFER:
                        collectorArm.raiseArm();
                        scoringState = TransferScoringStates.COLLECTOR_ARM;
                        break;
                    case CONFIRM_TRANSFER:
                    case EMPTY:
                    case FIX_JAM:
                    case SCORE:
                        scoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case COLLECTOR_ARM:
                switch (scoringCommand){
                    case TRANSFER:
                        if (deliveryLiftSystem.isLiftMovementComplete() && collectorArm.isRotationExtensionComplete()){
                            timer.reset();
                            deliveryLiftSystem.deliveryBoxToTransfer();
                            scoringState = TransferScoringStates.TRANSFER_READY;
                        }
                    case CONFIRM_TRANSFER:
                    case EMPTY:
                    case FIX_JAM:
                    case SCORE:
                        scoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case TRANSFER_READY:
                switch (scoringCommand){
                    case TRANSFER:
                        if (timer.milliseconds() > 1000){
                            collector.deliverMineralsOn();
                            scoringState = TransferScoringStates.TRANSFER;
                        }
                    case CONFIRM_TRANSFER:
                    case EMPTY:
                    case FIX_JAM:
                    case SCORE:
                        scoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                }
                break;
            case TRANSFER:
                switch (scoringCommand){
                    case CONFIRM_TRANSFER:
                        collector.deliverMineralsOff();
                        deliveryLiftSystem.goToScoringPosition();
                        scoringState = TransferScoringStates.SETUP_FOR_SCORE;
                        break;
                    case FIX_JAM:
                        collector.fixTransferJam();
                        scoringState = TransferScoringStates.TRANSFER_READY;
                        scoringCommand = TransferScoringCommands.TRANSFER;
                        break;
                    case TRANSFER:
                    case EMPTY:
                    case SCORE:
                        break;
                }
                break;
            case SETUP_FOR_SCORE:
                switch (scoringCommand){
                    case SCORE:
                        if (deliveryLiftSystem.isLiftMovementComplete()){
                            timer.reset();
                            deliveryLiftSystem.deliveryBoxToDump();
                            scoringState = TransferScoringStates.SCORED;
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
                if(timer.milliseconds() > 1500){
                    deliveryLiftSystem.deliveryBoxToHome();
                    scoringState = TransferScoringStates.RESET;
                }
                break;
            case RESET:
                scoringState = TransferScoringStates.START;
                break;

        }
    }

}
