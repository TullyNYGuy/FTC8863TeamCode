package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class FreightFrenzyIntake implements FTCRobotSubsystem {


    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum States {
        AT_INTAKE_POSITION,
        AT_EJECT_POSITION,
        ROTATING_TO_INTAKE,
        ROTATING_TO_EJECT,
        INTAKING,
        EJECTING,
        TRAPPING;
    }
    private States currentState = States.AT_INTAKE_POSITION;

    private enum Commands{
        ROTATE_TO_INTAKE_POSITION,
        ROTATE_TO_EJECT_POSITION,
        INTAKE,
        EJECT,
        TRAP,
        NO_COMMAND;
    }
    private Commands currentCoammand = Commands.NO_COMMAND;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private IntakeProximitySensor intakeProximitySensor;
    private IntakeSweeper intakeSweeper;
    private IntakeRotator intakeRotator;

    private DataLogging logFile;
    private boolean loggingOn = false;

    private boolean commandComplete = true;
    private ElapsedTime timer;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public FreightFrenzyIntake(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeProximitySensor = new IntakeProximitySensor(hardwareMap);
        intakeSweeper = new IntakeSweeper(hardwareMap, telemetry);
        intakeRotator = new IntakeRotator(hardwareMap, telemetry);

        timer = new ElapsedTime();
        commandComplete = true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void rotateToEject() {
        intakeSweeper.stop();
        intakeRotator.rotateToEject();
    }

    private void rotateToIntake() {
        intakeSweeper.stop();
        intakeRotator.rotateToIntake();
    }

    private void startIntakeCycle() {
        rotateToIntake();
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void reset() {
        intakeRotator.reset();
        intakeSweeper.reset();
    }

    @Override
    public boolean init(Configuration config) {
        reset();
        return true;
    }

    public void runIntakeCycle() {
        if (commandComplete) {
            commandComplete = false;
            currentCoammand = Commands.INTAKE;
            currentState = States.ROTATING_TO_INTAKE;
            startIntakeCycle();
            timer.reset();
        }
    }

    public void rotateToIntakePosition() {
        if (commandComplete) {
            commandComplete = false;
            currentCoammand = Commands.ROTATE_TO_INTAKE_POSITION;
            currentState = States.ROTATING_TO_INTAKE;
            rotateToIntake();
            timer.reset();
        }
    }

    public void rotateToEjectPosition() {
        if (commandComplete) {
            commandComplete = false;
            currentCoammand = Commands.ROTATE_TO_EJECT_POSITION;
            currentState = States.ROTATING_TO_EJECT;
            rotateToEject();
            timer.reset();
        }
    }

    @Override
    public void update() {
        switch (currentCoammand){
            case NO_COMMAND:
            case TRAP:
            case EJECT:
                switch ( currentState) {
                    case ROTATING_TO_INTAKE:
                        break;
                    case ROTATING_TO_EJECT:
                        // should never happen
                        break;
                    case AT_EJECT_POSITION:
                        // should never happen
                        break;
                    case AT_INTAKE_POSITION:
                        // should never happen
                        break;
                    case EJECTING:
                        break;
                    case INTAKING:
                        break;
                    case TRAPPING:
                        break;
                }
                break;
            case ROTATE_TO_INTAKE_POSITION:
                switch ( currentState) {
                    case ROTATING_TO_INTAKE:
                        if (intakeRotator.isIntakeAtIntakePosition()) {
                            currentState = States.AT_INTAKE_POSITION;
                            currentCoammand = Commands.NO_COMMAND;
                            commandComplete = true;
                        }
                        break;
                    case ROTATING_TO_EJECT:
                        // should never happen
                        break;
                    case AT_EJECT_POSITION:
                        // should never happen
                        break;
                    case AT_INTAKE_POSITION:
                        // should never happen
                        break;
                    case EJECTING:
                        break;
                    case INTAKING:
                        break;
                    case TRAPPING:
                        break;
                }
                break;
            case INTAKE:
                switch ( currentState) {
                    case ROTATING_TO_INTAKE:
                        if (intakeRotator.isIntakeAtIntakePosition()) {
                            intakeSweeper.intake();
                            currentState = States.INTAKING;
                            currentCoammand = Commands.INTAKE;
                            commandComplete = false;
                        }
                        break;
                    case ROTATING_TO_EJECT:
                        if(intakeRotator.isIntakeAtEjectPosition()) {
                            intakeSweeper.eject();
                            currentCoammand = Commands.INTAKE;
                            currentState = States.EJECTING;
                            commandComplete = false;
                        }
                        break;
                    case AT_EJECT_POSITION:
                        // should never happen
                        break;
                    case AT_INTAKE_POSITION:
                        // should never happen
                        break;
                    case EJECTING:
                        if(intakeProximitySensor.isIntakeEmpty()) {
                            intakeSweeper.stop();
                            currentCoammand = Commands.NO_COMMAND;
                            currentState = States.AT_EJECT_POSITION;
                            commandComplete = true;
                        }
                        break;
                    case INTAKING:
                        if(!intakeProximitySensor.isIntakeEmpty())
                            // got something
                            intakeSweeper.trapFreight();
                            intakeRotator.rotateToEject();
                            currentCoammand = Commands.INTAKE;
                            currentState = States.ROTATING_TO_EJECT;
                            commandComplete = false;
                        break;
                    case TRAPPING:
                        break;
                }
            case ROTATE_TO_EJECT_POSITION:
                switch ( currentState) {
                    case ROTATING_TO_INTAKE:
                        break;
                    case ROTATING_TO_EJECT:
                        if(intakeRotator.isIntakeAtEjectPosition()) {
                            currentCoammand = Commands.NO_COMMAND;
                            currentState = States.AT_EJECT_POSITION;
                            commandComplete = true;
                        }
                        break;
                    case AT_EJECT_POSITION:
                        // should never happen
                        break;
                    case AT_INTAKE_POSITION:
                        // should never happen
                        break;
                    case EJECTING:
                        break;
                    case INTAKING:
                        break;
                    case TRAPPING:
                        break;
                }
                break;
        }
    }

    public boolean isComplete() {
        return commandComplete;
    }

    @Override
    public String getName() {
        return "intake rotator";
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }
}
