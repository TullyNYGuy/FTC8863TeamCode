package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import android.sax.StartElementListener;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

public class UltimateGoalIntake {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum State {
        OFF,
        DELAY,
        ONE_ON,
        ONE_TWO_ON,
        ONE_TWO_THREE_ON,
        TWO_THREE_ON,
        THREE_ON
    }

    private enum Commands {
        TURN_ON_123,
        TURN_ON_12,
        TURN_ON_1,
        TURN_ON_23,
        TURN_ON_3,
        OFF
    }

    public enum RingsAt {
        NO_RINGS,
        ONE,
        TWO,
        THREE,
        ONE_TWO,
        TWO_THREE,
        ONE_THREE,
        ONE_TWO_THREE;
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private State currentState = State.OFF;

    private ElapsedTime turnOnTimer;

    private Switch stage1Switch;
    private Switch stage2Switch;
    private Switch stage3Switch;

    private DcMotor8863 stage1Motor;

    private CRServo stage2CRServo;
    private CRServo stage3CRServo;

    private int numberOfRingsAtStage3 = 0;

    private boolean commandComplete = true;

    private Commands currentCommand = Commands.OFF;

    private boolean firstCommand = true;
    private int turnOnDelay = 1000;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getNumberOfRingsAtStage3() {
        return numberOfRingsAtStage3;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public UltimateGoalIntake(HardwareMap hardwareMap, Telemetry telemetry) {
        stage1Switch = new Switch(hardwareMap, "stage1Switch", Switch.SwitchType.NORMALLY_OPEN);
        stage2Switch = new Switch(hardwareMap, "stage2Switch", Switch.SwitchType.NORMALLY_OPEN);
        stage3Switch = new Switch(hardwareMap, "stage2Switch", Switch.SwitchType.NORMALLY_OPEN);

        stage1Motor = new DcMotor8863("stage1Motor", hardwareMap, telemetry);
        stage1Motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        stage1Motor.setMovementPerRev(360);
        stage1Motor.setDirection(DcMotorSimple.Direction.REVERSE);
        stage1Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        stage1Motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stage2CRServo = hardwareMap.get(CRServo.class, "stage2CRServo");
        stage2CRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        stage3CRServo = hardwareMap.get(CRServo.class, "stage3CRServo");

        turnOnTimer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    public void updateIntake() {
        switch (currentState) {
            case OFF:
                if (!firstCommand) {
                    turnOnDelay=0;
                }
                switch (currentCommand) {
                    case TURN_ON_123:
                        turnStage2On();
                        turnStage3On();
                        turnOnTimer.reset();
                        commandComplete = false;
                        currentState = State.DELAY;
                        firstCommand = false;
                        break;
                    case TURN_ON_1:
                        turnStage1On();
                        commandComplete = true;
                        currentState = State.ONE_ON;
                        firstCommand = false;
                        break;
                    case TURN_ON_12:
                        turnStage2On();
                        turnOnTimer.reset();
                        commandComplete = false;
                        currentState = State.DELAY;
                        firstCommand = false;
                        break;
                    case TURN_ON_23:
                        turnStage2On();
                        turnStage3On();
                        commandComplete = true;
                        currentState = State.TWO_THREE_ON;
                        firstCommand = false;
                        break;
                    case TURN_ON_3:
                        turnStage3On();
                        commandComplete = true;
                        currentState = State.THREE_ON;
                        firstCommand = false;
                        break;
                    case OFF:
                        turnIntakeOff();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
            case DELAY:
                switch (currentCommand) {
                    case TURN_ON_123:
                        if (turnOnTimer.milliseconds() > turnOnDelay) {
                            turnStage1On();
                            commandComplete = true;
                            currentState = State.ONE_TWO_THREE_ON;
                        }
                        break;
                    case TURN_ON_1:
                        //not a valid command
                        break;
                    case TURN_ON_12:
                        if (turnOnTimer.milliseconds() > turnOnDelay) {
                            turnStage1On();
                            commandComplete = true;
                            currentState = State.ONE_TWO_ON;
                        }
                        break;
                    case TURN_ON_23:
                        //not valid command
                        break;
                    case TURN_ON_3:
                        //not valid command
                        break;
                    case OFF:
                        turnIntakeOff();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
            case ONE_ON:
                switch (currentCommand) {
                    case TURN_ON_123:
                        turnStage2On();
                        turnStage3On();
                        commandComplete = true;
                        currentState = State.ONE_TWO_THREE_ON;
                        break;
                    case TURN_ON_1:
                        //already on
                        break;
                    case TURN_ON_12:
                        turnStage2On();
                        commandComplete = true;
                        currentState = State.ONE_TWO_ON;
                        break;
                    case TURN_ON_23:
                        //command invalid
                        break;
                    case TURN_ON_3:
                        //command invalid
                        break;
                    case OFF:
                        turnIntakeOff();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
            case TWO_THREE_ON:
                switch (currentCommand) {
                    case TURN_ON_123:
                        turnStage1On();
                        commandComplete = true;
                        currentState = State.ONE_TWO_THREE_ON;
                        break;
                    case TURN_ON_1:
                        //command invalid
                        break;
                    case TURN_ON_12:
                        //command invalid
                        break;
                    case TURN_ON_23:
                        //already on
                        break;
                    case TURN_ON_3:
                        //command invalid
                        break;
                    case OFF:
                        turnIntakeOff();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
            case THREE_ON:
                switch (currentCommand) {
                    case TURN_ON_123:
                        turnStage2On();
                        turnOnTimer.reset();
                        commandComplete = false;
                        currentState = State.DELAY;
                        break;
                    case TURN_ON_1:
                        //command invalid
                        break;
                    case TURN_ON_12:
                        //command invalid
                        break;
                    case TURN_ON_23:
                        turnStage2On();
                        commandComplete = true;
                        currentState = State.TWO_THREE_ON;
                        break;
                    case TURN_ON_3:
                        //already on
                        break;
                    case OFF:
                        turnIntakeOff();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
            case ONE_TWO_THREE_ON:
                switch (currentCommand) {
                    case TURN_ON_123:
                        //already on
                        break;
                    case TURN_ON_1:
                        //invalid
                        break;
                    case TURN_ON_12:
                        //invalid
                        break;
                    case TURN_ON_23:
                        //invalid
                        break;
                    case TURN_ON_3:
                        //invalid
                        break;
                    case OFF:
                        turnIntakeOff();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
            case ONE_TWO_ON:
                switch (currentCommand) {
                    case TURN_ON_123:
                        turnStage3On();
                        commandComplete = true;
                        currentState = State.ONE_TWO_THREE_ON;
                        break;
                    case TURN_ON_1:
                        //invalid
                        break;
                    case TURN_ON_12:
                        //already on
                        break;
                    case TURN_ON_23:
                        //invalid
                        break;
                    case TURN_ON_3:
                        //invalid
                        break;
                    case OFF:
                        turnIntakeOff();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
        }
        if (stage3Switch.isBumped()) {
            numberOfRingsAtStage3 = numberOfRingsAtStage3++;
        }
    }

    public void intakeEmpty() {
        numberOfRingsAtStage3 = 0;
    }

    public boolean ringAtStage1() {
        return stage1Switch.isPressed();
    }

    public boolean ringAtStage2() {
        return stage2Switch.isPressed();
    }

    public boolean ringAtStage3() {
        return stage3Switch.isPressed();
    }

    private void turnStage1On() {
        stage1Motor.runAtConstantPower(1);

    }

    private void turnStage1Off() {
        stage1Motor.stop();
    }

    private void turnStage2On() {
        stage2CRServo.setPower(1);
    }

    private void turnStage2Off() {
        stage2CRServo.setPower(0);
    }

    private void turnStage3On() {
        stage3CRServo.setPower(1);
    }

    private void turnStage3Off() {
        stage3CRServo.setPower(0);
    }

    private void turnIntakeOff() {
        turnStage1Off();
        turnStage2Off();
        turnStage3Off();
        currentState = State.OFF;
    }

    private void turnIntakeOn() {
        turnStage2On();
        turnStage1On();
        turnStage3On();
    }

    private void turnIntake123On() {
        turnOnTimer.reset();
        currentState = State.DELAY;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void requestTurnStage1On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_1;
        }
    }

    public void requestTurnStage12On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_12;
        }
    }

    public void requestTurnStage123On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_123;
        }
    }

    public void requestTurnStage23On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_23;
        }
    }

    public void requestTurnStage3On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_3;
        }
    }

    public void requestTurnIntakeOFF() {
        currentCommand = Commands.OFF;
    }

    public RingsAt whereAreRings () {
        RingsAt answer= RingsAt.NO_RINGS;
        if (ringAtStage1() && !ringAtStage2() && !ringAtStage3()) answer= RingsAt.ONE;
        if (!ringAtStage1() && ringAtStage2() && !ringAtStage3()) answer= RingsAt.TWO;
        if (!ringAtStage1() && !ringAtStage2() && ringAtStage3()) answer= RingsAt.THREE;
        if (ringAtStage1() && ringAtStage2() && !ringAtStage3()) answer= RingsAt.ONE_TWO;
        if (!ringAtStage1() && ringAtStage2() && ringAtStage3()) answer= RingsAt.TWO_THREE;
        if (ringAtStage1() && !ringAtStage2() && ringAtStage3()) answer= RingsAt.ONE_THREE;
        if (ringAtStage1() && ringAtStage2() && ringAtStage3()) answer= RingsAt.ONE_TWO_THREE;
        return answer;
    }
}
