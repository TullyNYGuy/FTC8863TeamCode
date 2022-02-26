package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import android.provider.ContactsContract;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDBlinker;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;


import java.util.concurrent.TimeUnit;

public class FFIntake implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum IntakeState {
        PRE_INIT,
        WAIT_FOR_INIT_POSITION,

        READY_TO_INTAKE,
        INTAKE,
        WAIT_FOR_INTAKE_POSITION_FLOOR,
        WAIT_FOR_FREIGHT,
        WAIT_FOR_FREIGHT_TO_SETTLE,
        WAIT_FOR_ROTATION,
        OUTAKE,
        WAIT_FOR_OUTAKE,
        WAITING_FOR_READY_POSITION,
        // states for rotate to vertical
        BACK_TO_HOLD_FREIGHT,
        WAIT_FOR_VERTICAL,
        HOLDING_FREIGHT,
        // states for ejecting into level 1
        TO_LEVEL_ONE,
        WAIT_FOR_LEVEL_ONE,
        EJECT_INTO_LEVEL_ONE,
        WAIT_FOR_EJECT_INTO_LEVEL_ONE,
        //in these cases the "intake" is the position name
        TO_INTAKE,
        WAIT_FOR_INTAKE,
        EJECT_AT_INTAKE,
        // states for ejecting into level 2
        TO_LEVEL_TWO,
        WAIT_FOR_LEVEL_TWO,
        EJECT_INTO_LEVEL_TWO,
        WAIT_FOR_EJECT_INTO_LEVEL_TWO,
        STOP,
    }

    // This tells us what do once we intake a freight
    private enum WhatToDoWithFreight {
        DELIVER_TO_BUCKET,
        HOLD_IT
    }

    private WhatToDoWithFreight whatToDoWithFreight = WhatToDoWithFreight.DELIVER_TO_BUCKET;

    public enum ReadyPosition {
        VERTICAL,
        TRANSFER
    }

    private ReadyPosition readyPosition = ReadyPosition.VERTICAL;

    public void setReadyPosition(ReadyPosition readyPosition) {
        this.readyPosition = readyPosition;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private NormalizedColorSensor intakeSensor;
    private NormalizedColorSensor transferSensor;
    private DcMotor8863 intakeSweeperMotor;
    private ElapsedTime timer;
    private Servo8863New rotateServo;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private final String INTAKE_NAME = "Intake";
    private IntakeState intakeState = IntakeState.PRE_INIT;
    private final String INTAKE_SWEEPER_MOTOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_SWEEPER_MOTOR.hwName;
    private final String INTAKE_SENSOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_SENSOR.hwName;
    private final String INTAKE_ROTATOR_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_ROTATE_SERVO.hwName;
    private final String TRANSFER_SENSOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.TRANSFER_SENSOR.hwName;
    private RevLEDBlinker ledBlinker;

    // flags used in this class

    // says if init is complete
    private boolean initComplete = false;
    // says if the intake has freight in it
    private boolean hasIntakeIntaked = false;
    // the intake failed to eject freight into the delivery bucket. It is stuck in the intake.
    private boolean didTransferFail = false;
    // the intake failed to eject freight into the delivery bucket. It is not in the intake so it
    // is lost. Like maybe in the body of the robot?
    private boolean freightIsLost = false;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFIntake(HardwareMap hardwareMap, Telemetry telemetry, RevLEDBlinker ledBlinker) {
        intakeSweeperMotor = new DcMotor8863(INTAKE_SWEEPER_MOTOR_NAME, hardwareMap);
        intakeSweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        intakeSweeperMotor.setMovementPerRev(360);
        intakeSweeperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        timer = new ElapsedTime();
        this.ledBlinker= ledBlinker;
        intakeSensor = hardwareMap.get(NormalizedColorSensor.class, INTAKE_SENSOR_NAME);
        transferSensor = hardwareMap.get(NormalizedColorSensor.class, TRANSFER_SENSOR_NAME);

        if (intakeSensor instanceof SwitchableLight) {
            ((SwitchableLight) intakeSensor).enableLight(true);
        }

        if (transferSensor instanceof SwitchableLight) {
            ((SwitchableLight) transferSensor).enableLight(true);
        }

        rotateServo = new Servo8863New(INTAKE_ROTATOR_SERVO_NAME, hardwareMap, telemetry);
        rotateServo.addPosition("Intake", .01, 500, TimeUnit.MILLISECONDS);
        rotateServo.addPosition("Level 1", .23, 1000, TimeUnit.MILLISECONDS);
        rotateServo.addPosition("Vertical", .5, 500, TimeUnit.MILLISECONDS);
        rotateServo.addPosition("Transfer", 1.0, 750, TimeUnit.MILLISECONDS);
        rotateServo.addPosition("Level 2", .45, 1000, TimeUnit.MILLISECONDS);
        ledBlinker.off();
        PersistantStorage.isDeliveryFull = true;
        intakeState = IntakeState.PRE_INIT;
        readyPosition = ReadyPosition.VERTICAL;
    }

    //*********************************************************************************************
    //          INTERNAL PRIVATE METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    
    private void toIntakePosition() {
        rotateServo.setPosition("Intake");
    }
    
    private void toLevel1Position() {
        rotateServo.setPosition("Level 1");
    }

    private void toLevel2Position() {
        rotateServo.setPosition("Level 2");
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        loggingOn = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
        update();
    }

    private void logState() {
        if (loggingOn && logFile != null) {
            logStateOnChange.log(getName() + " state = " + intakeState.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    public void toTransferPosition() {
        rotateServo.setPosition("Transfer");
    }

    public void toVerticalPosition() {
        rotateServo.setPosition("Vertical");
    }

    // This method allows the intake ready position to be selected by the FFFreightSystem. For
    // autonomous the ready position is the transfer position because we don't want a duck to fall
    // into the robot.
    private void toReadyPosition() {
        if (readyPosition == ReadyPosition.VERTICAL) {
            toVerticalPosition();
        } else {
            toTransferPosition();
        }
    }

    public boolean isRotationComplete(){
        if (rotateServo.isPositionReached()){
            return true;
        }
        else{
            return false;
        }
    }

    //*********************************************************************************************
    //          STATUS METHODS
    //*********************************************************************************************

    public void displaySwitches(Telemetry telemetry) {
        telemetry.addData("distance=", ((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM));
    }

    @Override
    public String getName() {
        return INTAKE_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    public boolean isIntakeFull() {
        if (((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM) < 4.5) {
            return true;
        } else {
            return false;
        }
    }

    //this checks the transfer sensor to see whether or not the object has been transfered.
    // In the robot code this should be used to start the extension.
    // The reason for checking if the intake has intaked freight is to elminate a false trigger of the sensor
    // when the bucket wall is flopping around while lining up.
    public boolean isTransferComplete(){
        if (((DistanceSensor) transferSensor).getDistance(DistanceUnit.CM) < 7 && hasIntakeIntaked) {
            return true;
        } else {
            return false;
        }
    }

    public boolean didTransferFail(){
        //theoretically this is pointless. it should only be used if somehow the intake goes to transfer with nothing in it or
        //something gets stuck in the intake and wont transfer. this tells the freight system that the transfer isn't going to happen basically.
        return didTransferFail;
    }

    // When the transfer took place, the freight did not end up in the delivery box and it was not in the intake.
    // So it is probably somewhere in the guts of the robot or bounced out on the floor.
    public boolean isFreightLost() {
        return freightIsLost;
    }

    //*********************************************************************************************
    //          PUBLIC COMMANDs
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public boolean init(Configuration config) {
        logCommand("init");
        toTransferPosition();
        intakeState = IntakeState.WAIT_FOR_INIT_POSITION;
        return true;
    }
    
    public void turnOff() {
        intakeState = IntakeState.STOP;
    }

    // Can only do an intake if the intake is empty. If the transfer failed then the intake has
    // freight in it and a intake cycle is not a good move.
    public void intakeAndTransfer(){
        if (!didTransferFail){
            logCommand("intakeAndTransfer");
            intakeState = IntakeState.INTAKE;
            whatToDoWithFreight = WhatToDoWithFreight.DELIVER_TO_BUCKET;
        }
    }

    // Can only do an intake if the intake is empty. If the transfer failed then the intake has
    // freight in it and a intake cycle is not a good move.
    public void intakeAndHold() {
        if (!didTransferFail) {
            logCommand("intakeAndHold");
            intakeState = IntakeState.INTAKE;
            whatToDoWithFreight = WhatToDoWithFreight.HOLD_IT;
        }
    }

    // This method is intended to be used when freight is held in the intake and the transfer was not
    // started. This method restarts the transfer. So like when the intake picks up freight but the
    // extension arm was not retracted completely. Freight is held until the arm is fully retracted.
    //in order for this to work properly(at least in my mind) I cant have it check to see if position is reached, or if a state is reached
    //as a result of this, someone *cough* tanya *cough* could use this inncorrectly and break a lot of stuff. so you know... dont.
    // IN other words only FFFreightSystem should use this call.
    public void transfer(){
        // todo this should be only allowed if the intake is in the proper state
        toTransferPosition();
        intakeState = IntakeState.WAIT_FOR_ROTATION;
    }

    public void ejectIntoLevel1(){
        logCommand("eject into level 1");
        intakeSweeperMotor.runAtConstantRPM(-480);
        intakeState = IntakeState.EJECT_INTO_LEVEL_ONE;
    }

    public void lineUpToEjectIntoLevel1() {
        intakeState = IntakeState.TO_LEVEL_ONE;
    }

    public void ejectIntoLevel2(){
        intakeState = IntakeState.TO_LEVEL_TWO;
    }

    public void ejectOntoFloor(){
        logCommand("eject onto floor");
        intakeState = IntakeState.TO_INTAKE;
    }
    
    @Override
    public void shutdown() {
        turnOff();
    }

    //*********************************************************************************************
    //         State machine
    //*********************************************************************************************

    public void update() {
        logState();
        switch (intakeState) {

            // **********************************
            //States for INIT
            // **********************************

            case PRE_INIT: {
                // just hang out waiting for init to start
            }
            break;

            case WAIT_FOR_INIT_POSITION: {
                if (rotateServo.isPositionReached()) {
                    initComplete = true;
                    intakeState = IntakeState.READY_TO_INTAKE;
                }
            }
            break;

            // **********************************
            //States for intaking freight
            // **********************************
            case READY_TO_INTAKE: {
                // wait for someone to start an intake cycle
                hasIntakeIntaked = false;
            }
            break;

            case INTAKE: {
                // rotate to the floor
                didTransferFail = false;
                freightIsLost = false;
                toIntakePosition();
                intakeState = IntakeState.WAIT_FOR_INTAKE_POSITION_FLOOR;
            }
            break;

            case WAIT_FOR_INTAKE_POSITION_FLOOR: {
                if (rotateServo.isPositionReached()) {
                    intakeSweeperMotor.runAtConstantPower(.6);
                    ledBlinker.steadyRed();
                    intakeState = IntakeState.WAIT_FOR_FREIGHT;
                }
            }

            case WAIT_FOR_FREIGHT: {
                // do we have something?
                if (isIntakeFull()) {
                    //resets timer for the next state so that we can make sure the freight is actually in the intake.
                    timer.reset();
                    intakeState = IntakeState.WAIT_FOR_FREIGHT_TO_SETTLE;
                }
            }
            break;

            case WAIT_FOR_FREIGHT_TO_SETTLE: {
                if(timer.milliseconds() > 250){
                    hasIntakeIntaked = true;
                    PersistantStorage.isDeliveryFull = false;
                    ledBlinker.steadyAmber();
                    // yup stop the motor and try to cage the freight
                    intakeSweeperMotor.runAtConstantRPM(180);
                    if (whatToDoWithFreight == WhatToDoWithFreight.DELIVER_TO_BUCKET) {
                        toTransferPosition();
                        intakeState = IntakeState.WAIT_FOR_ROTATION;
                    }
                    if (whatToDoWithFreight == WhatToDoWithFreight.HOLD_IT) {
                        intakeState = IntakeState.BACK_TO_HOLD_FREIGHT;
                    }
                    //intakeSweeperMotor.moveToPosition(.3, 300, DcMotor8863.FinishBehavior.HOLD);
                }
            }
            break;

            case WAIT_FOR_ROTATION: {
                //checking to make sure we arent getting some sort of false posititive on is intake full. if there really is something it
                //goes to transfer, but if not it goes back to intaking
                if(isIntakeFull()){
                    if (rotateServo.isPositionReached()) {
                        // eject the freight
                        intakeSweeperMotor.runAtConstantRPM(-400);
                        intakeState = IntakeState.OUTAKE;
                        timer.reset();
                    }
                }
                else{
                    intakeState = IntakeState.INTAKE;
                }
            }
            break;
            
            //this is the version that uses the transfer sensor

            case OUTAKE: {
                if(isTransferComplete()){
                    //transfer is done time to chill
                    PersistantStorage.isDeliveryFull = true;
                    // The ready position will be transfer position in auto, vertical in teleop.
                    toReadyPosition();
                    ledBlinker.steadyGreen();
                    intakeState = IntakeState.WAITING_FOR_READY_POSITION;
                    didTransferFail = false;
                }
                else{
                    if(timer.milliseconds() > 3000){
                        //this is only for if something is jammed in the intake or the freight ejected
                        // from the intake and did not land in the delivery bucket.
                        // Figure out which it is.
                        if (isIntakeFull()) {
                            // the freight is stuck in the intake
                            // the intake goes back to vertical so that the driver can spit out the freight onto the ground.
                            toVerticalPosition();
                            intakeSweeperMotor.setPower(0);
                            intakeState = IntakeState.WAITING_FOR_READY_POSITION;
                            didTransferFail = true;
                        } else {
                            // the freight is not in the delivery bucket and not in the intake. So
                            // it must be int the body of the robot or out on the floor. Nothing we
                            // can do about that so go ahead like normal.
                            freightIsLost = true;
                            toReadyPosition();
                            intakeState = IntakeState.WAITING_FOR_READY_POSITION;
                        }

                    }
                }
            }
            break;

            case WAITING_FOR_READY_POSITION: {
                if (rotateServo.isPositionReached()) {
                    intakeState = IntakeState.READY_TO_INTAKE;
                }
            }
            break;

            //original non transfer sensor version
            /*case OUTAKE: {
                // hopefully the freight ejects in this amount of time
                if (!isIntakeFull() && timer.milliseconds() > 1000) {
                    intakeSweeperMotor.setPower(0);
                    timer.reset();
                    intakeState = IntakeState.WAIT_FOR_OUTAKE;
                }
            }
            break;*/



            //this is the original just in case the sensor version doesn't work
          /*  case WAIT_FOR_OUTAKE: {
                if (timer.milliseconds() > 2000) {
                    if (isIntakeFull()){
                        intakeState = IntakeState.WAIT_FOR_ROTATION;
                    }
                    else {
                        // done ejecting, time to go back to sleep
                        PersistantStorage.isDeliveryFull = true;
                        toVerticalPosition();
                        ledBlinker.steadyGreen();
                        intakeState = IntakeState.IDLE;
                    }
                }
            }
            break;*/

            // **********************************
            //States for ejecting onto the floor in case freight is stuck in the intake
            // **********************************

            case TO_INTAKE: {
                toIntakePosition();
                intakeState = IntakeState.WAIT_FOR_INTAKE;
            }
            break;

            case WAIT_FOR_INTAKE: {
                if (rotateServo.isPositionReached()) {
                    intakeSweeperMotor.runAtConstantRPM(-300);
                    timer.reset();
                    intakeState = IntakeState.EJECT_AT_INTAKE;
                }
            }
            break;

            case EJECT_AT_INTAKE: {
                if (!isIntakeFull() || timer.milliseconds() > 2000) {
                    toReadyPosition();
                    intakeSweeperMotor.setPower(0);
                    intakeState = IntakeState.WAITING_FOR_READY_POSITION;
                }
            }
            break;

            // **********************************
            // not sure what this is for
            // **********************************

            case STOP: {
                intakeSweeperMotor.setPower(0);
                toIntakePosition();
            }
            break;

            // **********************************
            //States for ejecting into level 1
            // **********************************

            case TO_LEVEL_ONE: {
                // move to shoot into the first level of the shipping hub
                toLevel1Position();
                //timer.reset();
                intakeState = IntakeState.WAIT_FOR_LEVEL_ONE;
            }
            break;

            case WAIT_FOR_LEVEL_ONE: {
                // wait for the intake to reach position
                if (rotateServo.isPositionReached()) {
                    //intakeSweeperMotor.runAtConstantRPM(-480);
                    //intakeState = IntakeState.EJECT_INTO_LEVEL_ONE;
                    intakeState = IntakeState.READY_TO_INTAKE;
                }

            }
            break;

            case EJECT_INTO_LEVEL_ONE: {
                // shooty shooty into level 1 of the shipping hub
                if (!isIntakeFull()) {
                    timer.reset();
                    // if you don't want a wait while ejecting then change the timer check to 0
                    // in the next state
                    intakeState = IntakeState.WAIT_FOR_EJECT_INTO_LEVEL_ONE;
                }
            }
            break;

            case WAIT_FOR_EJECT_INTO_LEVEL_ONE: {
                if (timer.milliseconds() > 500) {
                    intakeSweeperMotor.setPower(0);
                    intakeState = IntakeState.BACK_TO_HOLD_FREIGHT;
                }
            }
            break;

            // **********************************
            // States to move the intake vertical
            // **********************************

            case BACK_TO_HOLD_FREIGHT: {
                //back to hold position
                toVerticalPosition();
                intakeState = IntakeState.WAIT_FOR_VERTICAL;
            }
            break;

            // need this wait in order to say to the user that the move is complete
            case WAIT_FOR_VERTICAL: {
                if (rotateServo.isPositionReached()) {
                    intakeState = IntakeState.HOLDING_FREIGHT;
                    intakeSweeperMotor.setPower(0);
                }
            }
            break;

            case HOLDING_FREIGHT: {
                //just waiting for someone to transfer the freight
            }

            // **********************************
            // States to eject into level 2
            // **********************************

            case TO_LEVEL_TWO: {
                // move to shoot into the first level of the shipping hub
                toLevel2Position();
                intakeState = IntakeState.WAIT_FOR_LEVEL_TWO;
            }
            break;

            case WAIT_FOR_LEVEL_TWO: {
                // wait for the intake to reach position
                if (rotateServo.isPositionReached()) {
                    intakeSweeperMotor.runAtConstantRPM(-1200);
                    intakeState = IntakeState.EJECT_INTO_LEVEL_TWO;
                }

            }
            break;

            case EJECT_INTO_LEVEL_TWO: {
                // shooty shooty into level 2 of the shipping hub
                if (!isIntakeFull()) {
                    timer.reset();
                    // if you don't want a wait while ejecting then change the timer check to 0
                    // in the next state
                    intakeState = IntakeState.WAIT_FOR_EJECT_INTO_LEVEL_TWO;
                }
            }
            break;

            case WAIT_FOR_EJECT_INTO_LEVEL_TWO: {
                if (timer.milliseconds() > 500) {
                    intakeSweeperMotor.setPower(0);
                    intakeState = IntakeState.BACK_TO_HOLD_FREIGHT;
                }
            }
            break;
        }
    }


    // These are the public commands for the intake



}

