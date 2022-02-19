package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
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
        IDLE,
        //WAITING_FOR_INTAKE,

        EXTEND_TO_TOP,
        EXTEND_TO_MIDDLE,
        EXTEND_TO_BOTTOM,
        MOVE_SERVO_TO_1,
        //EXTEND_TO_2ND_POSITION,
        MOVE_SERVO_TO_2,
        //EXTEND_TO_3RD_POSITION,
        MOVE_SERVO_TO_3,
        // EXTEND_TO_FINAL_POSITION,
        EXTENDED_AT_FINAL_POSITION,
        LINING_UP_DUMP,
        WAITING_TO_DUMP,
        DUMP_INTO_TOP,
        DUMP_INTO_MIDDLE,
        DUMP_INTO_BOTTOM,
        IS_DUMPED_INTO_TOP,
        IS_DUMPED_INTO_MIDDLE,
        IS_DUMPED_INTO_BOTTOM,
        RETRACT_FROM_TOP,
        RETRACT_FROM_MIDDLE,
        RETRACT_FROM_BOTTOM,
        MOVE_SERVO_TO_3R,
        // RETRACT_TO_3RD_POSITION,
        RETRACT_TO_TRANSFER,
        MOVE_SERVO_TO_2R,
        // RETRACT_TO_2ND_POSITION,
        MOVE_SERVO_TO_1R,
        //RETRACT_TO_1ST_POSITION,
        MOVE_SERVO_TO_TRANSFER,
        //RETRACT_TO_0,
    }
    
    private enum InitState {
        IDLE,
        ONE,
        TWO,
        DELIVERY_SERVO_MOVING,
        DONE,
    }

    // so we can remember where the delivery bucket is located
    private enum DeliveryBucketLocation {
        TRANSFER,
        TOP,
        MIDDLE,
        BOTTOM
    }
    private DeliveryBucketLocation currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;

    // so we can remember which extend command was given
    private enum ExtendCommand {
        NONE,
        EXTEND_TO_TOP,
        EXTEND_TO_MIDDLE,
        EXTEND_TO_BOTTOM
    }
    private ExtendCommand extendCommand = ExtendCommand.NONE;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Switch retractionSwitch;
    private Switch extensionSwitch;
    private ExtensionRetractionMechanism ffExtensionArm;
    private DataLogging log;
    private FFExtensionArm.LiftState liftState = LiftState.IDLE;
    private FFExtensionArm.InitState initState = InitState.IDLE;
    private Servo8863New deliveryServo;
    private ElapsedTime timer;
    private AllianceColor allianceColor;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFExtensionArm(AllianceColor allianceColor, HardwareMap hardwareMap, Telemetry telemetry){
        this.allianceColor = allianceColor;

        ffExtensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "lift",
                FreightFrenzyRobotRoadRunner.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                FreightFrenzyRobotRoadRunner.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                FreightFrenzyRobotRoadRunner.HardwareName.LIFT_MOTOR.hwName,
                DcMotor8863.MotorType.GOBILDA_435,
                6.3238);

        ffExtensionArm.setResetTimerLimitInmSec(25000);
        ffExtensionArm.setExtensionPower(0.9);
        ffExtensionArm.setExtensionPositionInMechanismUnits(31.0);
        ffExtensionArm.setRetractionPower(-0.5);
        ffExtensionArm.setRetractionPositionInMechanismUnits(0.5);
        ffExtensionArm.setDataLog(log);
        ffExtensionArm.enableDataLogging();

        deliveryServo = new Servo8863New("deliveryServo" , hardwareMap, telemetry);
        deliveryServo.addPosition( "1.5 Extension",0.96,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "3 Extension",0.90,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "5 Extension",0.85,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "Transfer",0.98,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "Init",1,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "Parallel",0.83,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "DumpIntoTop",0.05,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "DumpIntoMiddle",0.04,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "DumpIntoBottom",0.13,500, TimeUnit.MILLISECONDS);
        deliveryServo.addPosition( "LineUpDump",0.17,500, TimeUnit.MILLISECONDS);

        if (allianceColor == AllianceColor.BLUE) {
            ffExtensionArm.reverseMotorDirection();
            deliveryServo.setDirection(Servo.Direction.FORWARD);
        } else {
            // alliance is RED
            ffExtensionArm.forwardMotorDirection();
            deliveryServo.setDirection(Servo.Direction.REVERSE);
        }

        timer = new ElapsedTime();
        
        currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;
        liftState = LiftState.IDLE;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String getName() {
        return "lift";
    }



    @Override
    public void shutdown() {
        retract();
    }

    @Override
    public void setDataLog(DataLogging logFile) {

    }

    @Override
    public void enableDataLogging() {

    }

    @Override
    public void disableDataLogging() {

    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public String getLiftState(){
        return liftState.toString();
    }

    @Override
    public boolean init(Configuration config) {
        ffExtensionArm.init();
        initState = InitState.ONE;
        return false;
    }

    public boolean isInitComplete(){
        boolean result = false;
        ffExtensionArm.update();
        switch (initState) {
            case IDLE: {

            }
            break;

            case ONE: {
                if(ffExtensionArm.isInitComplete()){
                    ffExtensionArm.goToPosition(0.5, 0.3);
                    initState = InitState.TWO;
                }

            }
            break;

            case TWO: {
                if(ffExtensionArm.isPositionReached()){

                    deliveryServoToTransferPosition();
                    initState = InitState.DELIVERY_SERVO_MOVING;
                }

            }
            break;

            case DELIVERY_SERVO_MOVING:
                if (deliveryServo.isPositionReached()) {
                    initState  = InitState.DONE;
                }
                break;

            case DONE: {
                result = true;
            }
            break;
        }
        currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;
        return result;
    }

    //********************************************************************************
    // Delivery bucket servo position commands
    //********************************************************************************

    public void deliveryServoToTransferPosition() {
        deliveryServo.setPosition("Transfer");
    }

    private void deliveryServoToDumpIntoTopPosition() {
        deliveryServo.setPosition("DumpIntoTop");
    }

    private void deliveryServoToDumpIntoMiddlePosition() {
        deliveryServo.setPosition("DumpIntoMiddle");
    }

    private void deliveryServoToDumpIntoBottomPosition() {
        deliveryServo.setPosition("DumpIntoBottom");
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

    public void dump(){
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
                    break;
                case MIDDLE:
                    liftState = LiftState.DUMP_INTO_MIDDLE;
                    break;
                case BOTTOM:
                    liftState = LiftState.DUMP_INTO_BOTTOM;
                    break;
            }
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void extendToTop(){
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            //command to start extension
            liftState = LiftState.EXTEND_TO_TOP;
            // remember the command for later
            extendCommand = ExtendCommand.EXTEND_TO_TOP;
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void extendToMiddle(){
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            //command to start extension
            liftState = LiftState.EXTEND_TO_MIDDLE;
            // remember the command for later
            extendCommand = ExtendCommand.EXTEND_TO_MIDDLE;
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void extendToBottom(){
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            //command to start extension
            liftState = LiftState.EXTEND_TO_BOTTOM;
            // remember the command for later
            extendCommand = ExtendCommand.EXTEND_TO_BOTTOM;
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void retract(){
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
                break;
            case MIDDLE:
                liftState = LiftState.RETRACT_FROM_MIDDLE;
                break;
            case BOTTOM:
                liftState = LiftState.RETRACT_FROM_BOTTOM;
                break;
        }
        } else {
            // you can't start a new command when the old one is not finished
        }
    }


    //********************************************************************************
    // Public commands for testing the extension arm / delivery bucket
    //********************************************************************************

    public void extendToPosition(double position, double power) {
        ffExtensionArm.goToPosition(position, power);
    }

    public void rotateToPosition(double position){
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
        switch (liftState) {
            case IDLE: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
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

            //********************************************************************************
            // Extend to top level states
            //********************************************************************************

            case EXTEND_TO_TOP: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(25.7, 0.9);
                liftState = liftState.MOVE_SERVO_TO_1;

            }
            break;

            //********************************************************************************
            // Extend to middle level states
            //********************************************************************************

            case EXTEND_TO_MIDDLE: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(6.5, 0.9);
                // rest of the movements are the same as the extend to top
                liftState = liftState.MOVE_SERVO_TO_1;
            }
            break;

            //********************************************************************************
            // Extend to bottom level states
            //********************************************************************************

            // same as middle right now but may need to change later so make it separate from middle
            case EXTEND_TO_BOTTOM: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(6.5, 0.9);
                // rest of the movements are the same as the extend to top
                liftState = liftState.MOVE_SERVO_TO_1;
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
                if (ffExtensionArm.getPosition() > 3.8 ) {
                    deliveryServo.setPosition("3 Extension");
                    liftState = LiftState.MOVE_SERVO_TO_3;
                }
            }
            break;


            case MOVE_SERVO_TO_3: {
                // if the extension arm has made it to 5 inches, it starts the servo movement
                if (ffExtensionArm.getPosition() > 6) {
                    deliveryServo.setPosition("5 Extension");
                    liftState = LiftState.EXTENDED_AT_FINAL_POSITION;
                }
            }
            break;


            case EXTENDED_AT_FINAL_POSITION: {
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
                            currentDeliverBucketLocation = DeliveryBucketLocation.MIDDLE;
                            // help the driver line up the dump
                            deliveryServoToLineUpDumpPosition();
                            break;
                        case EXTEND_TO_BOTTOM:
                            currentDeliverBucketLocation = DeliveryBucketLocation.BOTTOM;
                            // help the driver line up the dump
                            deliveryServoToLineUpDumpPosition();
                            break;
                    }
                    liftState = LiftState.LINING_UP_DUMP;
                }
            }
            break;

            case LINING_UP_DUMP: {
                if (isDeliverServoPositionReached()) {
                    liftState = LiftState.WAITING_TO_DUMP;
                }
            }
            break;


            case WAITING_TO_DUMP: {
                //this is essentially just Idle with a different name. waiting for driver to line up & push dump button
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
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
            }
            break;

            case IS_DUMPED_INTO_TOP: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    liftState = LiftState.RETRACT_FROM_TOP;
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
            }
            break;

            case IS_DUMPED_INTO_MIDDLE: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    liftState = LiftState.RETRACT_FROM_MIDDLE;
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
            }
            break;

            case IS_DUMPED_INTO_BOTTOM: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    liftState = LiftState.RETRACT_FROM_BOTTOM;
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
                ffExtensionArm.goToPosition(14, 1);
                liftState = LiftState.MOVE_SERVO_TO_3R;
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
                // starts retraction
                if(ffExtensionArm.isPositionReached() && timer.milliseconds() > 1500){
                    ffExtensionArm.goToPosition(0.5, 0.5);
                    liftState = LiftState.MOVE_SERVO_TO_2R;
                }

            }
            break;


            case MOVE_SERVO_TO_2R: {
                // move servo to 3 inch positon
                if(ffExtensionArm.getPosition() < 8){
                    deliveryServo.setPosition("3 Extension");
                    liftState = LiftState.MOVE_SERVO_TO_1R;
                }
            }
            break;


            case MOVE_SERVO_TO_1R: {
                //move servo to 1.5 inch
                if (ffExtensionArm.getPosition() < 5.8 ){
                    deliveryServo.setPosition("1.5 Extension");
                    liftState = LiftState.MOVE_SERVO_TO_TRANSFER;
                }
            }
            break;


            case MOVE_SERVO_TO_TRANSFER: {
                // move servo to transfer  position
                if (ffExtensionArm.getPosition() < 4.2){
                    deliveryServoToTransferPosition();
                    currentDeliverBucketLocation = DeliveryBucketLocation.TRANSFER;
                    // commandComplete is set to true in IDLE state
                    liftState = LiftState.IDLE;
                }
            }
            break;
        }
    }
//this is just an extra copy of the state machine from before it was continuous. basically a back up in case things break.
    /*public void update() {
        ffExtensionArm.update();
        switch (liftState) {
            case IDLE: {
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
                    liftState = LiftState.EXTENDED_AT_FINAL_POSITION;
                    timer.reset();
                }
            }
            break;


            case EXTENDED_AT_FINAL_POSITION: {
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
                    liftState = LiftState.IDLE;
                }
            }
            break;
        }
    }
*/
}
