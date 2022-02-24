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
        IDLE,
        INTAKE,
        WAIT_FOR_INTAKE_POSITION_FLOOR,
        WAIT_FOR_FREIGHT,
        HOLD_FREIGHT,
        WAIT_FOR_ROTATION,
        OUTAKE,
        WAIT_FOR_OUTAKE,
        WAITING_FOR_VERTICAL,
        // states for rotate to vertical
        BACK_TO_HOLD_FREIGHT,
        WAIT_FOR_VERTICAL,
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
    private Boolean initComplete = false;
    private final String INTAKE_NAME = "Intake";
    private IntakeState intakeState = IntakeState.IDLE;
    private final String INTAKE_SWEEPER_MOTOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_SWEEPER_MOTOR.hwName;
    private final String INTAKE_SENSOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_SENSOR.hwName;
    private final String INTAKE_ROTATOR_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_ROTATE_SERVO.hwName;
    private final String TRANSFER_SENSOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.TRANSFER_SENSOR.hwName;
    private RevLEDBlinker ledBlinker;


    // flags used in this class

    // says if the intake has freight in it
    private boolean hasIntakeIntaked = false;
    // the intake failed to eject freight
    private boolean didTransferFail = false;

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
    public void toTransferPosition() {
        rotateServo.setPosition("Transfer");
    }

    public void toVerticalPosition() {
        rotateServo.setPosition("Vertical");
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
        if (rotateServo.isPositionReached()) {
            initComplete = true;
            return true;
        } else {
            return false;
        }
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
    // The reason for checking if the intake has freight is to elminate a false trigger of the sensor
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






    //*********************************************************************************************
    //          PUBLIC COMMANDs
    //
    // public methods that give the class its functionality
    //*********************************************************************************************



    @Override
    public boolean init(Configuration config) {
        logCommand("init");
        toTransferPosition();
        return true;
    }
    
    public void turnOff() {
        intakeState = IntakeState.STOP;
    }

    public void intakeAndTransfer(){
        logCommand("intakeAndTransfer");
        intakeState = IntakeState.INTAKE;
        whatToDoWithFreight = WhatToDoWithFreight.DELIVER_TO_BUCKET;
    }

    public void intakeAndHold() {
        logCommand("intakeAndHold");
        intakeState = IntakeState.INTAKE;
        whatToDoWithFreight = WhatToDoWithFreight.HOLD_IT;
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

    // Best to make it private or eliminate it.
    @Deprecated
    private void getOutOfWay(){
        logCommand("get out of the way");
        toVerticalPosition();
    }

    // Best to make it private or eliminate it.
    @Deprecated
    private boolean isComplete() {
        if (intakeState == IntakeState.IDLE) {
            return true;
        } else {
            return false;
        }
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
            case IDLE: {
                hasIntakeIntaked = false;
                // do nothing
            }
            break;

            // **********************************
            //States for intaking freight
            // **********************************

            case INTAKE: {
                // rotate to the floor
                didTransferFail = false;
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
                    toVerticalPosition();;
                    ledBlinker.steadyGreen();
                    intakeState = IntakeState.WAIT_FOR_VERTICAL;
                }
                else{
                    if(timer.milliseconds() > 3000){
                        //this is only for if something is jammed in the intake.
                        // the intake goes back to vertical so that the driver can spit out the freight onto the ground.
                        toVerticalPosition();;
                        intakeSweeperMotor.setPower(0);
                        intakeState = IntakeState.IDLE;
                        didTransferFail = true;
                    }
                }
            }
            break;


            case WAITING_FOR_VERTICAL:{
                if(rotateServo.isPositionReached()){
                    intakeSweeperMotor.setPower(0);
                    intakeState = IntakeState.IDLE;
                }
            }

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
                        toVerticalPosition();;
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
                    intakeState = IntakeState.EJECT_AT_INTAKE;
                }
            }
            break;

            case EJECT_AT_INTAKE: {
                toVerticalPosition();;
                intakeState = IntakeState.IDLE;
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
                    intakeState = IntakeState.IDLE;
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
                    intakeState = IntakeState.IDLE;
                    intakeSweeperMotor.setPower(0);
                }
            }
            break;

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

            case HOLD_FREIGHT: {
                // is the caging done?
                if (intakeSweeperMotor.isMovementComplete()) {
                    // yup, now the human has to rotate the intake because that intake guy has not completed the rotation hardware yet :-)

                    intakeState = IntakeState.WAIT_FOR_ROTATION;
                }
            }
            break;
        }
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;

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
            logFile.logOnChange(getName() + " state = " + intakeState.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logFile.logOnChange(getName() + " command = " + command);
        }
    }

    // These are the public commands for the intake



}

