package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

public class IntakeRotator implements FTCRobotSubsystem {


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
        ROTATING_TO_EJECT;
    }
    private States currentState = States.AT_INTAKE_POSITION;

    private enum Commands{
        ROTATE_TO_INTAKE_POSITION,
        ROTATE_TO_EJECT_POSITION,
        NO_COMMAND;
    }
    private Commands currentCoammand = Commands.NO_COMMAND;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Servo8863 intakeRotatorServo;
    private final double INTAKE_POSITION = .3;
    private final double EJECT_POSITION = 0;

    private DataLogging logFile;
    private boolean loggingOn = false;

    private boolean commandComplete = true;
    private ElapsedTime timer;

    private boolean intakeAtIntakePosition;
    private boolean intakeAtEjectPosition;

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

    public IntakeRotator(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeRotatorServo = new Servo8863("intakeRotator", hardwareMap, telemetry);
        intakeRotatorServo.setDirection(Servo.Direction.FORWARD);
        intakeRotatorServo.setHomePosition(EJECT_POSITION);
        intakeRotatorServo.setPositionOne(INTAKE_POSITION);

        timer = new ElapsedTime();
        commandComplete = true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private void gotoIntakePosition() {
        intakeRotatorServo.goPositionOne();
        intakeAtIntakePosition = false;
        intakeAtEjectPosition = false;
    }

    private void gotoEjectPosition() {
        intakeRotatorServo.goHome();
        intakeAtIntakePosition = false;
        intakeAtEjectPosition = false;
    }

    private void toggleIntakeRotation(){
        if(intakeAtIntakePosition){
            gotoEjectPosition();
        }
        else{
            //do nothing;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void reset() {
        gotoIntakePosition();
    }

    @Override
    public boolean init(Configuration config) {
        reset();
        return true;
    }

    public void rotateToIntake() {
        if (commandComplete) {
            commandComplete = false;
            currentCoammand = Commands.ROTATE_TO_INTAKE_POSITION;
            currentState = States.ROTATING_TO_INTAKE;
            rotateToIntake();
            timer.reset();
        }
    }

    public void rotateToEject() {
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
                break;
            case ROTATE_TO_INTAKE_POSITION:
                switch ( currentState) {
                    case ROTATING_TO_INTAKE:
                        if (timer.milliseconds() > 500) {
                            timer.reset();
                            currentState = States.AT_INTAKE_POSITION;
                            currentCoammand = Commands.NO_COMMAND;
                            commandComplete = true;
                            intakeAtIntakePosition = true;
                            intakeAtEjectPosition = false;
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
                }
                break;
            case ROTATE_TO_EJECT_POSITION:
                switch ( currentState) {
                    case ROTATING_TO_INTAKE:
                        // should never happen
                        break;
                    case ROTATING_TO_EJECT:
                        if (timer.milliseconds() > 500) {
                            timer.reset();
                            currentState = States.AT_INTAKE_POSITION;
                            currentCoammand = Commands.NO_COMMAND;
                            commandComplete = true;
                            intakeAtIntakePosition = false;
                            intakeAtEjectPosition = true;
                        }
                        break;
                    case AT_EJECT_POSITION:
                        // should never happen
                        break;
                    case AT_INTAKE_POSITION:
                        // should never happen
                        break;
                }
                break;
        }
    }

    public boolean isComplete() {
        return commandComplete;
    }

    public boolean isIntakeAtIntakePosition () {
        return isIntakeAtIntakePosition();
    }

    public boolean isIntakeAtEjectPosition () {
        return isIntakeAtEjectPosition();
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
