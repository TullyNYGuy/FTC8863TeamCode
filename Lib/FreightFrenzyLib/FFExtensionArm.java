package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy.TeleopUsingRoadRunnerFreightFrenzy;

import java.io.PipedOutputStream;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class FFExtensionArm implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum LiftState {
        FULLY_RETRACTED_READY_FOR_ACTION,
        //WAITING_FOR_INTAKE,

        // INIT STATES
        PRE_INIT,
        WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE,
        WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED,
        WAITING_FOR_BUCKET_INIT_POSITION_REACHED,
        INIT_COMPLETE,

        EXTEND_TO_TOP,
        EXTEND_TO_MIDDLE,
        EXTEND_TO_BOTTOM,
        EXTEND_TO_SHARED,
        MOVE_SERVO_TO_1,
        //EXTEND_TO_2ND_POSITION,
        MOVE_SERVO_TO_2,
        //EXTEND_TO_3RD_POSITION,
        MOVE_SERVO_TO_3,
        // EXTEND_TO_FINAL_POSITION,
        WAITING_FOR_EXTENSION_COMPLETE,
        LINING_UP_DUMP,
        WAITING_TO_DUMP,
        DUMP_INTO_TOP,
        DUMP_INTO_MIDDLE,
        DUMP_INTO_BOTTOM,
        DUMP_INTO_SHARED,
        IS_DUMPED_INTO_TOP,
        IS_DUMPED_INTO_MIDDLE,
        IS_DUMPED_INTO_BOTTOM,
        IS_DUMPED_INTO_SHARED,
        RETRACT_FROM_TOP,
        RETRACT_FROM_MIDDLE,
        RETRACT_FROM_BOTTOM,
        RETRACT_FROM_SHARED,
        MOVE_SERVO_TO_3R,
        // RETRACT_TO_3RD_POSITION,
        RETRACT_TO_TRANSFER,
        MOVE_SERVO_TO_2R,
        // RETRACT_TO_2ND_POSITION,
        MOVE_SERVO_TO_1R,
        //RETRACT_TO_1ST_POSITION,
        MOVE_SERVO_TO_TRANSFER,
        WAITING_FOR_RETRACTION_COMPLETE,
        //RETRACT_TO_0,
    }

    private LiftState liftState = LiftState.PRE_INIT;

    // so we can remember where the delivery bucket is located
    private enum DeliveryBucketLocation {
        TRANSFER,
        TOP,
        MIDDLE,
        BOTTOM,
        SHARED
    }

    private DeliveryBucketLocation currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;

    // so we can remember which extend command was given
    private enum ExtendCommand {
        NONE,
        EXTEND_TO_TOP,
        EXTEND_TO_MIDDLE,
        EXTEND_TO_BOTTOM,
        EXTEND_TO_SHARED
    }

    private ExtendCommand extendCommand = ExtendCommand.NONE;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private ExtensionRetractionMechanism ffExtensionArm;
    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;


    private Servo8863New deliveryServo;
    private ElapsedTime timer;
    private AllianceColor allianceColor;

    // flags used in this class

    // initialization is complete
    private boolean initComplete = false;
    // dump into shipping hub is complete
    private boolean dumpComplete = false;
    // arm is fully retracted
    private boolean retractionComplete = true;
    // ready and waiting to dump into the shipping hub
    private boolean readyToDump = false;

    //extension arm positions
    private double topPosition;
    private double midPosition;
    private double bottomPosition;
    private double sharedPosition;





    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFExtensionArm(AllianceColor allianceColor, HardwareMap hardwareMap, Telemetry telemetry) {
        this.allianceColor = allianceColor;

        deliveryServo = new Servo8863New("deliveryServo", hardwareMap, telemetry);
        deliveryServo.addPosition("1.5 Extension", 0.96, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("3 Extension", 0.90, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("5 Extension", 0.85, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("Transfer", 0.98, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("Init", 1, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("Parallel", 0.83, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("DumpIntoTop", 0.05, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("DumpIntoMiddle", 0.04, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("DumpIntoBottom", 0, 500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition("DumpIntoShared", 0, 500, TimeUnit.MILLISECONDS);

        deliveryServo.addPosition("LineUpDump", 0.17, 10, TimeUnit.MILLISECONDS);

        if (allianceColor == AllianceColor.BLUE) {
            ffExtensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                    "lift",
                    FreightFrenzyRobotRoadRunner.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                    FreightFrenzyRobotRoadRunner.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                    FreightFrenzyRobotRoadRunner.HardwareName.LIFT_MOTOR.hwName,
                    DcMotor8863.MotorType.GOBILDA_435,
                    6.3238);
            ffExtensionArm.forwardMotorDirection();
            deliveryServo.setDirection(Servo.Direction.FORWARD);
            topPosition = 27.7;
            midPosition = 12;
            bottomPosition = 6;
            sharedPosition = 2;
        } else {
            // alliance is RED
            ffExtensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                    "lift",
                    FreightFrenzyRobotRoadRunner.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                    FreightFrenzyRobotRoadRunner.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                    FreightFrenzyRobotRoadRunner.HardwareName.LIFT_MOTOR.hwName,
                    DcMotor8863.MotorType.GOBILDA_435,
                    5.713);
            ffExtensionArm.reverseMotorDirection();
            deliveryServo.setDirection(Servo.Direction.REVERSE);
            topPosition = 25.7;
            midPosition = 11.25;
            bottomPosition = 5.75;
            sharedPosition = 1.75;
        }

        ffExtensionArm.setResetTimerLimitInmSec(25000);
        ffExtensionArm.setExtensionPower(0.9);
        ffExtensionArm.setExtensionPositionInMechanismUnits(31.0);
        ffExtensionArm.setRetractionPower(-0.5);
        ffExtensionArm.setRetractionPositionInMechanismUnits(0.5);

        timer = new ElapsedTime();

        currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;
        liftState = LiftState.PRE_INIT;
        // init has not been started yet
        initComplete = false;
        // the lift can be commanded to do something, like the init
        commandComplete = true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************


    public boolean isDumpComplete(){
        return dumpComplete;
    }

    public boolean isRetractionComplete(){
        return retractionComplete;
    }

    public boolean isReadyToDump(){
        return readyToDump;
    }

    @Override
    public String getName() {
        return "DeliverySystem";
    }

    @Override
    public void shutdown() {
        retract();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
        ffExtensionArm.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
        ffExtensionArm.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
        ffExtensionArm.disableDataLogging();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + liftState.toString());
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
    public String getLiftState() {
        return liftState.toString();
    }

    @Override
    public boolean init(Configuration config) {
        // start the init for the extension retraction mechanism
        logCommand("Init starting");
        ffExtensionArm.init();
        commandComplete = false;
        liftState = LiftState.WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE;
        logCommand("Init");
        return false;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    //********************************************************************************
    // Delivery bucket servo position commands
    //********************************************************************************

    public void deliveryServoToTransferPosition() {
        deliveryServo.setPosition("Transfer");
    }

    public void deliveryServoToDumpIntoTopPosition() {
        deliveryServo.setPosition("DumpIntoTop");
    }

    private void deliveryServoToDumpIntoMiddlePosition() {
        deliveryServo.setPosition("DumpIntoMiddle");
    }

    private void deliveryServoToDumpIntoBottomPosition() {
        deliveryServo.setPosition("DumpIntoBottom");
    }
    private void deliveryServoToDumpIntoSharedPosition() {
        deliveryServo.setPosition("DumpIntoShared");
    }


    public void deliveryServoToParallelPosition() {
        deliveryServo.setPosition("Parallel");
    }

    private void deliveryServoToLineUpDumpPosition() {
        deliveryServo.setPosition("LineUpDump");
    }

    public boolean isDeliverServoPositionReached() {
        return deliveryServo.isPositionReached();
    }

    //********************************************************************************
    // Public commands for controlling the extension arm / delivery bucket
    //********************************************************************************

    public void dump() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            //this command is the button press for dumping freight into the hub
            // Find where the bucket is located and then start the dump
            switch (currentDeliverBucketLocation) {
                case TRANSFER:
                    // if you get a dump command and the bucket is still in the transfer location
                    // ignore the command
                    break;
                case TOP:
                    liftState = LiftState.DUMP_INTO_TOP;
                    logCommand("Dump into top");
                    break;
                case MIDDLE:
                    liftState = LiftState.DUMP_INTO_MIDDLE;
                    logCommand("Dump into middle");
                    break;
                case BOTTOM:
                    liftState = LiftState.DUMP_INTO_BOTTOM;
                    logCommand("Dump into bottom");
                    break;
                case SHARED:
                    liftState = LiftState.DUMP_INTO_SHARED;
                    logCommand("dump into shared");
            }
        } else {
            logCommand("Dump command ignored");
            // you can't start a new command when the old one is not finished
        }
    }

    public void extendToTop() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to top");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.EXTEND_TO_TOP;
            // remember the command for later
            extendCommand = ExtendCommand.EXTEND_TO_TOP;
            logCommand(extendCommand.toString());
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void extendToMiddle() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to middle");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.EXTEND_TO_MIDDLE;
            // remember the command for later
            extendCommand = ExtendCommand.EXTEND_TO_MIDDLE;
            logCommand(extendCommand.toString());
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void extendToBottom() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to bottom");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.EXTEND_TO_BOTTOM;
            // remember the command for later
            extendCommand = ExtendCommand.EXTEND_TO_BOTTOM;
            logCommand(extendCommand.toString());
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void extendToShared() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to shared");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.EXTEND_TO_SHARED;
            // remember the command for later
            extendCommand = ExtendCommand.EXTEND_TO_SHARED;
            logCommand(extendCommand.toString());
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void retract() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            // Find where the bucket is located and then start the retract
            switch (currentDeliverBucketLocation) {
                case TRANSFER:
                    // if you get a RETRACT command and the bucket is still in the transfer location
                    // ignore the command
                    break;
                case TOP:
                    liftState = LiftState.RETRACT_FROM_TOP;
                    logCommand("Retract from top");
                    break;
                case MIDDLE:
                    liftState = LiftState.RETRACT_FROM_MIDDLE;
                    logCommand("Retract from middle");
                    break;
                case BOTTOM:
                    liftState = LiftState.RETRACT_FROM_BOTTOM;
                    logCommand("Retract from bottom");
                    break;
                case SHARED:
                    liftState = LiftState.RETRACT_FROM_SHARED;
                    logCommand("Retract from shared");
                    break;
            }
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("Retract command ignored");
        }
    }


    //********************************************************************************
    // Public commands for testing the extension arm / delivery bucket
    //********************************************************************************

    public boolean isStateWaitingToDump() {
        //this is just for use in the freight system.
        if (liftState == LiftState.WAITING_TO_DUMP) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isStateIdle() {
        //this is just for use in the freight system.
        if (liftState == LiftState.FULLY_RETRACTED_READY_FOR_ACTION) {
            return true;
        } else {
            return false;
        }
    }

    public void extendToPosition(double position, double power) {
        ffExtensionArm.goToPosition(position, power);
    }

    public void rotateToPosition(double position) {
        // this is probably not the best way to do this
        //deliveryServo.addPosition("position", position, 500, TimeUnit.MILLISECONDS);
        //deliveryServo.setPosition("position");
        // I added a method in Servo8863New to expose the setPosition(double position)
        deliveryServo.setPosition(position);
    }

    public boolean isExtensionMovementComplete() {
        return ffExtensionArm.isMovementComplete();
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        ffExtensionArm.update();
        logState();
        switch (liftState) {
            // YOU NEED TO THINK ABOUT HOW YOU ARE GOING TO HANDLE 3 DIFFERENT ARM/SERVO COMBINATIONS
            // EXTENDING / ROTATING THE ARM TO:
            //    LEVEL 3 OF THE SHIPPING HUB
            //    LEVEL 2 OF THE SHIPPING HUB
            //    LEVEL 1 OF THE SHIPPING HUB

            // AND 3 DIFFERENT RETRACT / ROTATION SEQUENCES IN THE OPPOSITE DIRECTIONS

            // AND THE DIFFERENT DUMPS - DON'T FORGET THE DRIVER MAY WANT TO LINE UP, THEN DUMP

            // I'M NOT SEEING THE DIFFERENCES BETWEEN THEM IN THE CODE BELOW.


            //********************************************************************************
            // INIT states
            //********************************************************************************

            case PRE_INIT: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // do nothing, waiting to get the init command
            }
            break;

            case WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE: {
                if (ffExtensionArm.isInitComplete()) {
                    ffExtensionArm.goToPosition(0.5, 0.3);
                    liftState = LiftState.WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED;
                }
            }
            break;

            case WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED: {
                if (ffExtensionArm.isPositionReached()) {
                    deliveryServoToTransferPosition();
                    liftState = LiftState.WAITING_FOR_BUCKET_INIT_POSITION_REACHED;
                }
            }
            break;

            case WAITING_FOR_BUCKET_INIT_POSITION_REACHED: {
                if (deliveryServo.isPositionReached()) {
                    liftState = LiftState.INIT_COMPLETE;
                    commandComplete = true;
                    initComplete = true;
                    currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;
                }
            }
            break;

            case INIT_COMPLETE: {
                // the extension arm is now ready to go
                liftState = LiftState.FULLY_RETRACTED_READY_FOR_ACTION;
            }
            break;

            //********************************************************************************
            // The normal ready to go to work state of the delivery system
            //********************************************************************************

            case FULLY_RETRACTED_READY_FOR_ACTION: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // do nothing
            }
            break;

            //********************************************************************************
            // Extend to top level states
            //********************************************************************************

            case EXTEND_TO_TOP: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(topPosition, 1.0);
                liftState = LiftState.MOVE_SERVO_TO_1;

            }
            break;

            //********************************************************************************
            // Extend to middle level states
            //********************************************************************************

            case EXTEND_TO_MIDDLE: {
                ffExtensionArm.goToPosition(15, 1.0);
                // rest of the movements are the same as the extend to top
                liftState = LiftState.MOVE_SERVO_TO_1;
            }
            break;

            //********************************************************************************
            // Extend to bottom level states
            //********************************************************************************

            // same as middle right now but may need to change later so make it separate from middle
            case EXTEND_TO_BOTTOM: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(15, 1.0);
                // rest of the movements are the same as the extend to top
                liftState = LiftState.MOVE_SERVO_TO_1;
            }
            break;

            //********************************************************************************
            // Extend to Shared level states
            //********************************************************************************

            // same as middle right now but may need to change later so make it separate from middle
            case EXTEND_TO_SHARED: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(15, 1.0);
                // rest of the movements are the same as the extend to top
                liftState = LiftState.MOVE_SERVO_TO_1;
            }
            break;


            //********************************************************************************
            // Sequence to extend the arm and get the delivery bucket out of the robot
            //********************************************************************************

            case MOVE_SERVO_TO_1: {
                if (ffExtensionArm.getPosition() > 2.9) {
                    //the arm has extended to 1.5 so now the servo moves to the correct position
                    deliveryServo.setPosition("1.5 Extension");
                    liftState = LiftState.MOVE_SERVO_TO_2;
                }
            }
            break;


            case MOVE_SERVO_TO_2: {
                // if the extension arm has made it to 3 inches, it starts the servo movement
                if (ffExtensionArm.getPosition() > 3.8) {
                    deliveryServo.setPosition("3 Extension");
                    liftState = LiftState.MOVE_SERVO_TO_3;
                }
            }
            break;


            case MOVE_SERVO_TO_3: {
                // if the extension arm has made it to 5 inches, it starts the servo movement
                if (ffExtensionArm.getPosition() > 8) {
                    deliveryServoToLineUpDumpPosition();
                    liftState = LiftState.WAITING_FOR_EXTENSION_COMPLETE;
                }
            }
            break;


            case WAITING_FOR_EXTENSION_COMPLETE: {
                // checks extension and the timer is for testing
                if (ffExtensionArm.isPositionReached()) {
                    // figure out where we are extended to, and set the location of the delivery bucket
                    // Was extend command = ?
                    switch (extendCommand) {
                        case NONE:
                            // should not ever happen
                            break;
                        case EXTEND_TO_TOP:
                            currentDeliverBucketLocation = DeliveryBucketLocation.TOP;
                            break;
                        case EXTEND_TO_MIDDLE:
                            ffExtensionArm.goToPosition(midPosition, .6);
                            currentDeliverBucketLocation = DeliveryBucketLocation.MIDDLE;
                            break;
                        case EXTEND_TO_BOTTOM:
                            ffExtensionArm.goToPosition(bottomPosition, .6);
                            currentDeliverBucketLocation = DeliveryBucketLocation.BOTTOM;
                            break;
                        case EXTEND_TO_SHARED:
                            ffExtensionArm.goToPosition(sharedPosition, .6);
                            currentDeliverBucketLocation = DeliveryBucketLocation.SHARED;
                            break;
                    }
                    liftState = LiftState.LINING_UP_DUMP;
                }
            }
            break;

            case LINING_UP_DUMP: {
                if (isDeliverServoPositionReached() && ffExtensionArm.isPositionReached()) {
                    liftState = LiftState.WAITING_TO_DUMP;
                }
            }
            break;


            case WAITING_TO_DUMP: {
                //this is essentially just Idle with a different name. waiting for driver to line up & push dump button
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // readyToDump was previously set in the LINING_UP_DUMP state but that caused a bug.
                // The dump command is only accepted if the state is WAITING_TO_DUMP so it has to be
                // set in this state.
                readyToDump = true;
            }
            break;

            //********************************************************************************
            // DUMP INTO TOP states
            //********************************************************************************

            case DUMP_INTO_TOP: {
                // dump freight into top level. here the timer does serve the purpose of making sure the delivery is clear,
                // but the time can probably be shortened
                deliveryServoToDumpIntoTopPosition();
                liftState = LiftState.IS_DUMPED_INTO_TOP;
                timer.reset();
                readyToDump = false;

            }
            break;

            case IS_DUMPED_INTO_TOP: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 600) {
                    liftState = LiftState.RETRACT_FROM_TOP;
                    dumpComplete = true;
                }
            }
            break;

            //********************************************************************************
            // DUMP INTO MIDDLE states
            //********************************************************************************

            case DUMP_INTO_MIDDLE: {
                // dump freight into middle level. here the timer does serve the purpose of making sure the delivery is clear,
                // but the time can probably be shortened
                deliveryServoToDumpIntoMiddlePosition();
                liftState = LiftState.IS_DUMPED_INTO_MIDDLE;
                timer.reset();
                readyToDump = false;

            }
            break;

            case IS_DUMPED_INTO_MIDDLE: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 600) {
                    liftState = LiftState.RETRACT_FROM_MIDDLE;
                    dumpComplete = true;
                }
            }
            break;

            //********************************************************************************
            // DUMP INTO BOTTOM states
            //********************************************************************************

            case DUMP_INTO_BOTTOM: {
                // dump freight into bottom level. here the timer does serve the purpose of making sure the delivery is clear,
                // but the time can probably be shortened
                deliveryServoToDumpIntoBottomPosition();
                liftState = LiftState.IS_DUMPED_INTO_BOTTOM;
                timer.reset();
                readyToDump = false;

            }
            break;

            case IS_DUMPED_INTO_BOTTOM: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 600) {
                    liftState = LiftState.RETRACT_FROM_BOTTOM;
                    dumpComplete = true;
                }
            }
            break;



            //********************************************************************************
            // DUMP INTO SHARED states
            //********************************************************************************

            case DUMP_INTO_SHARED: {
                // dump freight into shared level. here the timer does serve the purpose of making sure the delivery is clear,
                // but the time can probably be shortened
                deliveryServoToDumpIntoSharedPosition();
                liftState = LiftState.IS_DUMPED_INTO_SHARED;
                timer.reset();
                readyToDump = false;

            }
            break;

            case IS_DUMPED_INTO_SHARED: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 600) {
                    liftState = LiftState.RETRACT_FROM_SHARED;
                    dumpComplete = true;
                }
            }
            break;

            //********************************************************************************
            // RETRACT from top states
            //********************************************************************************

            // The retraction sequences are all the same for now because we don't have time to figure out
            // the proper movements. Eventually they should probably be different so we are more
            // efficient.
            case RETRACT_FROM_TOP: {
                // starts retraction
                ffExtensionArm.goToPosition(15, 1);
                liftState = LiftState.MOVE_SERVO_TO_3R;
                //dumpComplete = false;
            }
            break;

            //********************************************************************************
            // RETRACT from middle states
            //********************************************************************************

            // The retraction sequences are all the same for now because we don't have time to figure out
            // the proper movements. Eventually they should probably be different so we are more
            // efficient.
            case RETRACT_FROM_MIDDLE: {
                // starts retraction
                ffExtensionArm.goToPosition(14, 1);
                liftState = LiftState.MOVE_SERVO_TO_3R;
                //dumpComplete = false;
            }
            break;

            //********************************************************************************
            // RETRACT from bottom states
            //********************************************************************************

            // The retraction sequences are all the same for now because we don't have time to figure out
            // the proper movements. Eventually they should probably be different so we are more
            // efficient.
            case RETRACT_FROM_BOTTOM: {
                // starts retraction
                ffExtensionArm.goToPosition(14, 1);
                liftState = LiftState.MOVE_SERVO_TO_3R;
                //dumpComplete = false;
            }
            break;




            //********************************************************************************
            // RETRACT from shared states
            //********************************************************************************

            // The retraction sequences are all the same for now because we don't have time to figure out
            // the proper movements. Eventually they should probably be different so we are more
            // efficient.
            case RETRACT_FROM_SHARED: {
                // starts retraction
                ffExtensionArm.goToPosition(14, 1);
                liftState = LiftState.MOVE_SERVO_TO_3R;
                //dumpComplete = false;
            }
            break;

            //********************************************************************************
            // sequence to retract arm and get bucket into the robot - same for all retractions right now
            //********************************************************************************

            case MOVE_SERVO_TO_3R: {
                // move servo to 5 inch positon
                deliveryServo.setPosition("5 Extension");
                liftState = LiftState.RETRACT_TO_TRANSFER;
                timer.reset();
            }
            break;


            case RETRACT_TO_TRANSFER: {
                // starts rest of retraction to transfer, othewise the timer is so the delivery box
                // can stop swinging before
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 1250) {
                    ffExtensionArm.goToPosition(0.5, 0.5);
                    liftState = LiftState.MOVE_SERVO_TO_2R;
                }

            }
            break;


            case MOVE_SERVO_TO_2R: {
                // move servo to 3 inch positon
                if (ffExtensionArm.getPosition() < 8) {
                    deliveryServo.setPosition("3 Extension");
                    liftState = LiftState.MOVE_SERVO_TO_1R;
                }
            }
            break;


            case MOVE_SERVO_TO_1R: {
                //move servo to 1.5 inch
                if (ffExtensionArm.getPosition() < 5.8) {
                    deliveryServo.setPosition("1.5 Extension");
                    liftState = LiftState.MOVE_SERVO_TO_TRANSFER;
                }
            }
            break;


            case MOVE_SERVO_TO_TRANSFER: {
                // move servo to transfer  position
                if (ffExtensionArm.getPosition() < 4.2) {
                    deliveryServoToTransferPosition();
                    currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;
                    liftState = LiftState.WAITING_FOR_RETRACTION_COMPLETE;
                }
            }
            break;

            case WAITING_FOR_RETRACTION_COMPLETE: {
                if (ffExtensionArm.isRetractionComplete() && deliveryServo.isPositionReached()){
                    // reset dump complete flag
                    dumpComplete = false;
                    retractionComplete = true;
                    liftState = LiftState.FULLY_RETRACTED_READY_FOR_ACTION;
                }
            }
            break;
        }
    }
//this is just an extra copy of the state machine from before it was continuous. basically a back up in case things break.
    /*public void update() {
        ffExtensionArm.update();
        switch (liftState) {
            case FULLY_RETRACTED_READY_FOR_ACTION: {
                // do nothing
            }
            break;

            // YOU NEED TO THINK ABOUT HOW YOU ARE GOING TO HANDLE 3 DIFFERENT ARM/SERVO COMBINATIONS
            // EXTENDING / ROTATING THE ARM TO:
            //    LEVEL 3 OF THE SHIPPING HUB
            //    LEVEL 2 OF THE SHIPPING HUB
            //    LEVEL 1 OF THE SHIPPING HUB

            // AND 3 DIFFERENT RETRACT / ROTATION SEQUENCES IN THE OPPOSITE DIRECTIONS

            // AND THE DIFFERENT DUMPS - DON'T FORGET THE DRIVER MAY WANT TO LINE UP, THEN DUMP

            // I'M NOT SEEING THE DIFFERENCES BETWEEN THEM IN THE CODE BELOW.
            case EXTEND_TO_1ST_POSITION: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(2.9, 0.3);
                liftState = liftState.MOVE_SERVO_TO_1;

            }
            break;


            case MOVE_SERVO_TO_1: {
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 3000) {
                    //the arm has extended to 1.5 so now the servo moves to the correct position
                    deliveryServo.setPosition("1.5 Extension");
                    timer.reset();
                    liftState = LiftState.EXTEND_TO_2ND_POSITION;
                }


            }
            break;


            case EXTEND_TO_2ND_POSITION: {
                // this is to make sure the servo has moved. the time delay is a testing thing.
                //this also starts the movement to 3 inches
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    ffExtensionArm.goToPosition(3.8, 0.3);
                    liftState = LiftState.MOVE_SERVO_TO_2;
                }


            }
            break;


            case MOVE_SERVO_TO_2: {
                // if the extension arm has made it to 3 inches, it starts the servo movement
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 3000) {
                    deliveryServo.setPosition("3 Extension");
                    timer.reset();
                    liftState = LiftState.EXTEND_TO_3RD_POSITION;
                }
            }
            break;


            case EXTEND_TO_3RD_POSITION: {
                // this is to make sure the servo has moved. the time delay is a testing thing.
                //this also starts the movement to 5 inches
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    ffExtensionArm.goToPosition(6, 0.3);
                    liftState = LiftState.MOVE_SERVO_TO_3;
                }


            }
            break;


            case MOVE_SERVO_TO_3: {
                // if the extension arm has made it to 5 inches, it starts the servo movement
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 3000) {
                    deliveryServo.setPosition("5 Extension");
                    timer.reset();
                    liftState = LiftState.EXTEND_TO_FINAL_POSITION;
                }
            }
            break;

            case EXTEND_TO_FINAL_POSITION: {
                //once again checks servo movement and the timer is for testing. starts movement to top level extension.
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    ffExtensionArm.goToPosition(25.25, 0.3);
                    liftState = LiftState.WAITING_FOR_EXTENSION_COMPLETE;
                    timer.reset();
                }
            }
            break;


            case WAITING_FOR_EXTENSION_COMPLETE: {
                // checks extension and the timer is for testing
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 5000) {
                    liftState = LiftState.WAITING_TO_DUMP;
                    timer.reset();
                }
            }
            break;


            case WAITING_TO_DUMP: {
                //this is essentially just Idle with a different name. waiting for driver to line up & push dump button
                // also resets a timer constantly for use in dump state

                ///for testing purposes i just set this to dump after like 3 seconds. this needs to be removed
                if(timer.milliseconds() > 3000){
                    timer.reset();
                    liftState = LiftState.DUMP;
                }
            }
            break;


            case DUMP: {
                // dump freight into top level. here the timer does serve the purpose of making sure the delivery is clear,
                // but the time can probably be shortened
                deliveryServo.setPosition("DumpIntoTop");
                liftState = LiftState.IS_DUMPED;
            }
            break;

            case IS_DUMPED: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    liftState = LiftState.MOVE_SERVO_TO_3R;
                }
            }
            break;


            case MOVE_SERVO_TO_3R: {
                // move servo to 3 inch positon
                deliveryServo.setPosition("5 Extension");
                timer.reset();
                liftState = LiftState.RETRACT_TO_3RD_POSITION;
            }
            break;


            case RETRACT_TO_3RD_POSITION: {
                // retract to 3 inch
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    ffExtensionArm.goToPosition(6.0, 0.3);
                    liftState = LiftState.MOVE_SERVO_TO_2R;
                    timer.reset();
                }
            }
            break;


            case MOVE_SERVO_TO_2R: {
                // move servo to 3 inch positon
                deliveryServo.setPosition("3 Extension");
                timer.reset();
                liftState = LiftState.RETRACT_TO_2ND_POSITION;
            }
            break;


            case RETRACT_TO_2ND_POSITION: {
                // retract to 3 inch
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    ffExtensionArm.goToPosition(3.8, 0.3);
                    liftState = LiftState.MOVE_SERVO_TO_1R;
                    timer.reset();
                }
            }
            break;


            case MOVE_SERVO_TO_1R: {
                //move servo to 1.5 inch
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 3000){
                    deliveryServo.setPosition("1.5 Extension");
                    liftState = LiftState.RETRACT_TO_1ST_POSITION;
                    timer.reset();
                }
            }
            break;


            case RETRACT_TO_1ST_POSITION: {
                // retract to 1.5 inch
                if(deliveryServo.isPositionReached() && timer.milliseconds() > 3000){
                    ffExtensionArm.goToPosition(2.2, 0.3);
                    liftState = LiftState.MOVE_SERVO_TO_TRANSFER;
                    timer.reset();
                }
            }
            break;


            case MOVE_SERVO_TO_TRANSFER: {
                // move servo to transfer  position
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 3000){
                    deliveryServo.setPosition("Transfer");
                    liftState = LiftState.RETRACT_TO_0;
                    timer.reset();
                }
            }
            break;

            case RETRACT_TO_0: {
                //retract to transfer
                if(deliveryServo.isPositionReached() && timer.milliseconds() > 3000){
                    ffExtensionArm.goToPosition(0.5, 0.3);
                    liftState = LiftState.FULLY_RETRACTED_READY_FOR_ACTION;
                }
            }
            break;
        }
    }
*/
}
