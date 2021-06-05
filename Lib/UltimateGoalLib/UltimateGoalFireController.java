package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import android.os.MessageQueue;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class UltimateGoalFireController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum States {
        CHECK_INTAKE_CONTROLLER,
        CHECK_SHOOTER_SPEED,
        FIRING,
        FIRING_REST,
        IDLE;
    }

    private enum Commands {
        OFF,
        FIRE_1,
        FIRE_2,
        FIRE_3,
        QUICK_FIRE_3;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private boolean commandComplete = true;
    private Commands currentCommand = Commands.OFF;
    private States currentState = States.IDLE;

    private UltimateGoalIntakeController intakeController;
    private Shooter shooter;
    private UltimateGoalIntake intake;

    private DataLogging logFile;
    private boolean loggingOn = false;
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
    public UltimateGoalFireController(UltimateGoalIntakeController intakeController, Shooter shooter, UltimateGoalIntake intake) {
        this.intakeController = intakeController;
        this.shooter = shooter;
        this.intake = intake;
        this.timer= new ElapsedTime();
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
    @Override
    public void update() {
        switch (currentState) {
            case IDLE:
                switch (currentCommand) {
                    case OFF:
                        // nothing
                        break;
                    case FIRE_1:
                        currentState = States.CHECK_INTAKE_CONTROLLER;
                        break;
                    case FIRE_2:
                        currentState = States.CHECK_INTAKE_CONTROLLER;
                        break;
                    case FIRE_3:
                        currentState = States.CHECK_INTAKE_CONTROLLER;
                        break;
                    case QUICK_FIRE_3:
                        currentState = States.CHECK_INTAKE_CONTROLLER;
                        break;
                }
                break;
            case CHECK_INTAKE_CONTROLLER:
                switch (currentCommand) {
                    case OFF:
                        //nothing, shouldn't happen
                        break;
                    case FIRE_1:
                        if (intakeController.isComplete()) {
                            currentState = States.CHECK_SHOOTER_SPEED;
                        }
                        break;
                    case FIRE_2:
                        if (intakeController.isComplete()) {
                            currentState = States.CHECK_SHOOTER_SPEED;
                        }
                        break;
                    case FIRE_3:
                        if (intakeController.isComplete()) {
                            currentState = States.CHECK_SHOOTER_SPEED;
                        }
                        break;
                    case QUICK_FIRE_3:
                        if (intakeController.isComplete()) {
                            currentState = States.CHECK_SHOOTER_SPEED;
                        }
                        break;
                }
                break;
            case CHECK_SHOOTER_SPEED:
                switch (currentCommand) {
                    case OFF:
                        //nothing
                        break;
                    case FIRE_1:
                        if (shooter.isReady()) {
                            intakeController.requestFire_1();
                            currentState = States.FIRING;
                        }
                        break;
                    case FIRE_2:
                        if (shooter.isReady()) {
                            intakeController.requestFire_1();
                            currentState = States.FIRING;
                        }
                        break;
                    case FIRE_3:
                        if (shooter.isReady()) {
                            intakeController.requestFire_1();
                            currentState = States.FIRING;
                        }
                        break;
                    case QUICK_FIRE_3:
                        if (shooter.isReady()) {
                            intake.requestTurnStage23On();
                            currentState = States.FIRING;
                           // timer.reset();
                        }
                        break;
                }
                break;
            case FIRING:
                switch (currentCommand) {
                    case OFF:
                        //nothing
                        break;
                    case FIRE_1:
                        if (intakeController.isComplete()) {
                            currentState = States.IDLE;
                            currentCommand = Commands.OFF;
                            commandComplete = true;
                        }
                        break;
                    case FIRE_2:
                        if (intakeController.isComplete()) {
                            currentState = States.IDLE;
                            currentCommand = Commands.FIRE_1;
                        }
                        break;
                    case FIRE_3:
                        if (intakeController.isComplete()) {
                            currentState = States.IDLE;
                            currentCommand = Commands.FIRE_2;
                        }
                        break;
                    case QUICK_FIRE_3:
                        if (timer.milliseconds()>250) {
                            intake.requestTurnStage123On();
                            currentState = States.FIRING_REST;
                        }
                        break;
                }
                break;
            case FIRING_REST:
                switch (currentCommand) {
                    case OFF:
                    case FIRE_1:
                    case FIRE_2:
                    case FIRE_3:
                        // should never get here
                        break;
                    case QUICK_FIRE_3:
                        if (timer.milliseconds()>3000) {
                            commandComplete= true;
                            currentCommand= Commands.OFF;
                            currentState= States.IDLE;
                            intake.requestTurnIntakeOFF();
                        }
                        break;
                }
                break;
        }
    }


    public void requestFire1() {
        if (commandComplete) {
            commandComplete = false;
            currentCommand = Commands.FIRE_1;
            currentState = States.IDLE;
        }
    }

    public void requestFire2() {
        if (commandComplete) {
            commandComplete = false;
            currentCommand = Commands.FIRE_2;
            currentState = States.IDLE;
        }
    }

    public void requestFire3() {
        if (commandComplete) {
            commandComplete = false;
            currentCommand = Commands.FIRE_3;
            currentState = States.IDLE;
        }
    }

    public void requestQuickFire3 () {
        if (commandComplete) {
            commandComplete = false;
            currentCommand = Commands.QUICK_FIRE_3;
            currentState = States.IDLE;
        }
    }

    public boolean isComplete () {
        return commandComplete;
    }

    @Override
    public String getName() {
        return "fire controller";
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

    @Override
    public boolean init(Configuration config) {
        return true;
    }
}
