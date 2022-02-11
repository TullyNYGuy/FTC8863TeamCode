package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
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

        EXTEND,
        MOVE_SERVO_TO_1,
        //EXTEND_TO_2ND_POSITION,
        MOVE_SERVO_TO_2,
        //EXTEND_TO_3RD_POSITION,
        MOVE_SERVO_TO_3,
        // EXTEND_TO_FINAL_POSITION,
        EXTENDED_AT_FINAL_POSITION,
        WAITING_TO_DUMP,
        DUMP,
        IS_DUMPED,
        RETRACT_TO_BUFFER,
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
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFExtensionArm(HardwareMap hardwareMap, Telemetry telemetry){
        ffExtensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "lift",
                "extensionLimitSwitch",
                "retractionLimitSwitch",
                "extensionArmMotor",
                DcMotor8863.MotorType.GOBILDA_435,
                4.517);
        ffExtensionArm.reverseMotorDirection();

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
        deliveryServo.addPosition( "Dump",0.05,500, TimeUnit.MILLISECONDS);

        timer = new ElapsedTime();
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
                    deliveryServo.setPosition("Transfer");
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
        return result;
    }

    public void deliveryServoToTransferPosition() {
        deliveryServo.setPosition("Transfer");
    }

    public void deliveryServoToDumpPosition() {
        deliveryServo.setPosition("Dump");
    }

    public void deliveryServoToParallelPosition() {
        deliveryServo.setPosition("Parallel");
    }

    public boolean isDeliverServoPositionReached() {
        return deliveryServo.isPositionReached();
    }

    public void dump(){
        //this commnad is the button press for dumping freight into the hub
        liftState = LiftState.DUMP;
    }
    public void extend(){
        //command to start extension
        liftState = LiftState.EXTEND;
    }

    public void retract(){
        //command to start extension
        liftState = LiftState.RETRACT_TO_BUFFER;
    }


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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
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
            case EXTEND: {
                //starts the extension to 1.5 inches the positions are a little weird but this goes to actually 1.5
                ffExtensionArm.goToPosition(25.7, 0.9);
                liftState = liftState.MOVE_SERVO_TO_1;

            }
            break;


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
                    liftState = LiftState.WAITING_TO_DUMP;
                }
            }
            break;


            case WAITING_TO_DUMP: {
                //this is essentially just Idle with a different name. waiting for driver to line up & push dump button




            }
            break;


            case DUMP: {
                // dump freight into top level. here the timer does serve the purpose of making sure the delivery is clear,
                // but the time can probably be shortened
                deliveryServo.setPosition("Dump");
                liftState = LiftState.IS_DUMPED;
                timer.reset();
            }
            break;

            case IS_DUMPED: {
                //checks if dump was did or not
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    liftState = LiftState.RETRACT_TO_BUFFER;
                }
            }
            break;

            case RETRACT_TO_BUFFER: {
                // starts retraction
                ffExtensionArm.goToPosition(14, 1);
                liftState = LiftState.MOVE_SERVO_TO_3R;
            }
            break;

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
                    deliveryServo.setPosition("Transfer");
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
                deliveryServo.setPosition("Dump");
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
