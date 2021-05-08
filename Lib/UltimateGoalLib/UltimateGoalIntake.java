package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import android.sax.StartElementListener;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
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
    private State previousState = currentState;

    private ElapsedTime turnOnTimer;

    private NormalizedColorSensor stage1Sensor;     /** The colorSensor field will contain a reference to our color sensor hardware object */

    private Switch stage2ASwitch;
    private Switch stage2BSwitch;
    private Switch stage3Switch;

    private DcMotor8863 stage1Motor;

    private CRServo stage2CRServo;
    private CRServo stage3CRServo;

    private int numberOfRingsAtStage3 = 0;

    private boolean commandComplete = true;

    private Commands currentCommand = Commands.OFF;
    private Commands previousCommand = currentCommand;

    private RingsAt currentRingsAt = RingsAt.NO_RINGS;
    private RingsAt previousRingsAt = currentRingsAt;

    private boolean firstCommand = true;
    private int turnOnDelay = 1000;

    private DataLogging logFile;
    private boolean loggingOn = false;
    // this says that the first line in the data log is about to be written
    private boolean firstLogLine = true;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getNumberOfRingsAtStage3() {
        return numberOfRingsAtStage3;
    }

    /**
     * Set the data log file
     * @param logFile
     */
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    public void enableDataLogging() {
        this.loggingOn = true;
    }

    public void disableDataLogging() {
        this.loggingOn = false;
    }

    public RingsAt getCurrentRingsAt() {
        return currentRingsAt;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public UltimateGoalIntake(HardwareMap hardwareMap, Telemetry telemetry) {
        stage1Sensor = hardwareMap.get(NormalizedColorSensor.class, UltimateGoalRobotRoadRunner.HardwareName.STAGE_1_SENSOR.hwName);
        if (stage1Sensor instanceof SwitchableLight) {
            ((SwitchableLight)stage1Sensor).enableLight(true);
        }
        stage2ASwitch = new Switch(hardwareMap, UltimateGoalRobotRoadRunner.HardwareName.STAGE_2A_SWITCH.hwName, Switch.SwitchType.NORMALLY_OPEN);
        stage2BSwitch = new Switch(hardwareMap, UltimateGoalRobotRoadRunner.HardwareName.STAGE_2B_SWITCH.hwName, Switch.SwitchType.NORMALLY_OPEN);
        stage3Switch = new Switch(hardwareMap, UltimateGoalRobotRoadRunner.HardwareName.STAGE_3_SWITCH.hwName, Switch.SwitchType.NORMALLY_OPEN);

        stage1Motor = new DcMotor8863(UltimateGoalRobotRoadRunner.HardwareName.STAGE_1_MOTOR.hwName, hardwareMap, telemetry);
        stage1Motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        stage1Motor.setMovementPerRev(360);
        stage1Motor.setDirection(DcMotorSimple.Direction.FORWARD);
        stage1Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        stage1Motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stage2CRServo = hardwareMap.get(CRServo.class, UltimateGoalRobotRoadRunner.HardwareName.STAGE_2_SERVO.hwName);
        stage2CRServo.setDirection(DcMotorSimple.Direction.FORWARD);
        stage3CRServo = hardwareMap.get(CRServo.class, UltimateGoalRobotRoadRunner.HardwareName.STAGE_3_SERVO.hwName);

        turnOnTimer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Log the state and command into the log file. But only if the value of one of them has changed
     * from the last time the state machine was run.
     * @param state - value of the state (in the state machine)
     * @param command - the current command to the state machine
     */

    /**
     * Log the state and command and ring locations into the log file. But only if the value of one
     * of them has changed from the last time the state machine was run
     * @param state
     * @param command
     * @param ringsAt
     */
    private void logState(State state, Commands command, RingsAt ringsAt) {
        // is there a log file and is logging enabled?
        if (logFile != null && loggingOn) {
            // has the state or the command or ring location changed since the last run through the
            // state machine? Or is this the first line in the log file?
            if (state != previousState || command != previousCommand || currentRingsAt != previousRingsAt || firstLogLine) {
                // yes so write the info into the log file
                logFile.logData("Intake", state.toString(), command.toString(), ringsAt.toString());
                // save the current state and command and ring location so they can be used next time
                previousState = state;
                previousCommand = command;
                previousRingsAt = ringsAt;
                // if this was the first line in the log file, change the flag so that later on
                // we know that the first line has already been written
                firstLogLine = false;
            }
        }
    }

    public void intakeEmpty() {
        numberOfRingsAtStage3 = 0;
    }

    //*********************************************************************************************
    //          Ask the switch for the stage whether a ring is present
    //*********************************************************************************************

    private boolean ringAtStage1() {
        boolean result=false;
        if (stage1Sensor instanceof DistanceSensor) {
            if (((DistanceSensor) stage1Sensor).getDistance(DistanceUnit.CM)<8) {
                result=true;
            }
        }
        return result;
    }

    private boolean ringAtStage2() {
        return stage2ASwitch.isPressed()|| stage2BSwitch.isPressed();
    }

    private boolean ringAtStage3() {
        return stage3Switch.isPressed();
    }

    //*********************************************************************************************
    //          Give a command to the intake
    //*********************************************************************************************

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

    /**
     * The state machine for the Intake. We need this because the timing of turning on the 3 stages
     * has to be managed
     */
    public void update() {
        // only get the switch info once per state machine once per state machine update
        currentRingsAt = whereAreRings();
        // log the state, command and where the rings are located into the log file
        logState(currentState, currentCommand, currentRingsAt);
        switch (currentState) {
            case OFF:
                switch (currentCommand) {
                    case TURN_ON_123:
                        if (firstCommand) {
                            turnStage2On();
                            turnStage3On();
                            turnOnTimer.reset();
                            commandComplete = false;
                            currentState = State.DELAY;
                        }
                        else {
                            turnStage1On();
                            turnStage2On();
                            turnStage3On();
                            commandComplete= true;
                            currentState= State.ONE_TWO_THREE_ON;
                        }

                        break;
                    case TURN_ON_1:
                        turnStage1On();
                        commandComplete = true;
                        currentState = State.ONE_ON;
                        firstCommand = false;
                        break;
                    case TURN_ON_12:
                        if (firstCommand) {
                            turnStage2On();
                            turnOnTimer.reset();
                            commandComplete = false;
                            currentState = State.DELAY;
                        }
                        else {
                            turnStage1On();
                            turnStage2On();
                            commandComplete= true;
                            currentState= State.ONE_TWO_ON;
                        }
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
                            firstCommand= false;
                        }
                        break;
                    case TURN_ON_12:
                        if (turnOnTimer.milliseconds() > turnOnDelay) {
                            turnStage1On();
                            commandComplete = true;
                            currentState = State.ONE_TWO_ON;
                            firstCommand=false;
                        }
                        break;
                    case TURN_ON_23:
                    case TURN_ON_1:
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
                        turnStage1Off();
                        turnStage2On();
                        turnStage3On();
                        commandComplete= true;
                        currentState= State.TWO_THREE_ON;
                        break;
                    case TURN_ON_3:
                        turnStage1Off();
                        turnStage3On();
                        commandComplete= true;
                        currentState= State.THREE_ON;
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
                        turnStage2Off();
                        turnStage3Off();
                        turnStage1On();
                        commandComplete= true;
                        currentState= State.ONE_ON;
                        break;
                    case TURN_ON_12:
                        turnStage3Off();
                        turnStage1On();
                        commandComplete= true;
                        currentState= State.ONE_TWO_ON;
                        break;
                    case TURN_ON_23:
                        //already on
                        break;
                    case TURN_ON_3:
                        turnStage2Off();
                        commandComplete= true;
                        currentState= State.THREE_ON;
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
                        turnStage3Off();
                        turnStage1On();
                        commandComplete= true;
                        currentState= State.ONE_ON;
                        break;
                    case TURN_ON_12:
                        turnStage3Off();
                        turnStage1On();
                        turnStage2On();
                        commandComplete= true;
                        currentState= State.ONE_TWO_ON;
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
                        turnStage2Off();
                        turnStage3Off();
                        commandComplete= true;
                        currentState= State.ONE_ON;
                        break;
                    case TURN_ON_12:
                        turnStage3Off();
                        commandComplete= true;
                        currentState= State.ONE_TWO_ON;
                        break;
                    case TURN_ON_23:
                        turnStage1Off();
                        commandComplete= true;
                        currentState= State.TWO_THREE_ON;
                        break;
                    case TURN_ON_3:
                        turnStage2Off();
                        turnStage1Off();
                        commandComplete= true;
                        currentState= State.THREE_ON;
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
                        turnStage2Off();
                        commandComplete=true;
                        currentState= State.ONE_ON;
                        break;
                    case TURN_ON_12:
                        //already on
                        break;
                    case TURN_ON_23:
                        turnStage1Off();
                        turnStage3On();
                        commandComplete= true;
                        currentState= State.TWO_THREE_ON;
                        break;
                    case TURN_ON_3:
                        turnStage1Off();
                        turnStage2Off();
                        turnStage3On();
                        commandComplete= true;
                        currentState= State.THREE_ON;
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

    //*********************************************************************************************
    //          Requests for the intake to do something
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
        if (commandComplete) {
            currentCommand = Commands.OFF;
        }
    }

    /**
     * Get the state of the switches for each stage and return it
     * @return
     */
    private RingsAt whereAreRings () {
        RingsAt answer= RingsAt.NO_RINGS;
        boolean ringAtStage1 = ringAtStage1();
        boolean ringAtStage2 = ringAtStage2();
        boolean ringAtStage3 = ringAtStage3();
        if (ringAtStage1 && !ringAtStage2 && !ringAtStage3) {
            answer= RingsAt.ONE;
        }
        if (!ringAtStage1 && ringAtStage2 && !ringAtStage3) answer= RingsAt.TWO;
        if (!ringAtStage1 && !ringAtStage2 && ringAtStage3) answer= RingsAt.THREE;
        if (ringAtStage1 && ringAtStage2 && !ringAtStage3) answer= RingsAt.ONE_TWO;
        if (!ringAtStage1 && ringAtStage2 && ringAtStage3) answer= RingsAt.TWO_THREE;
        if (ringAtStage1 && !ringAtStage2 && ringAtStage3) answer= RingsAt.ONE_THREE;
        if (ringAtStage1 && ringAtStage2 && ringAtStage3) answer= RingsAt.ONE_TWO_THREE;
        return answer;
    }

    /**
     * Debug method to display switch value on the driver station
     * @param telemetry
     */
    public void displaySWitches (Telemetry telemetry) {
        telemetry.addData("distance=", ((DistanceSensor) stage1Sensor).getDistance(DistanceUnit.CM));

        if (stage2ASwitch.isPressed() ) {
            telemetry.addData("switch 2A is pressed", ":)");
        }
        else {
            telemetry.addData("switch 2A is NOT pressed", ":(");
        }

        if (stage2BSwitch.isPressed() ) {
            telemetry.addData("switch 2B is pressed", ":)");
        }
        else {
            telemetry.addData("switch 2B is NOT pressed", ":(");
        }

        if (stage3Switch.isPressed() ) {
            telemetry.addData("switch 3 is pressed", ":)");
        }
        else {
            telemetry.addData("switch 3 is NOT pressed", ":(");
        }
    }

}
