package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDBlinker;

public class FFFreightSystem implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum State {
        IDLE,
        WAITING_FOR_ARM_INIT,
        WAITING_FOR_INTAKE_INIT,
        READY_TO_CYCLE,
        START_CYCLE,
        HOLD_FREIGHT,
        WAITING_FOR_TRANSFER,
        WAITING_TO_EXTEND,
        WAITING_FOR_EXTENSION,
        WAITING_TO_DUMP,
        WAITING_FOR_DUMP_COMPLETE,
        WAITING_FOR_RETRACTION,

        START_INTAKE,


        WAITING_FOR_INTAKE_REPOSITION1,
        WAITING_FOR_INTAKE_REPOSITION2,


        //Uh-Oh states//
        EMERGENCY_EJECT,
        EJECT_COMPLETE,
    }

    /*
    private enum State {

        INTAKE_STUFF,
        DELIVERY_STUFF,
        RETRACT_EXTENSION_ARM,

        WAIT_FOR_ARM_INIT,
        INIT_INTAKE,
        WAIT_FOR_INTAKE_INIT,
        INIT_DONE
    }
     */


    private enum Phase {
        AUTONOMUS,
        TELEOP
    }

    private enum Level {
        TOP,
        MIDDLE,
        BOTTOM
    }

    private enum Mode {
        AUTO,
        MANUAL,
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private State state = State.IDLE;
    private Mode mode = Mode.MANUAL;
    private Phase phase = Phase.AUTONOMUS;
    private Level level = Level.TOP;


    private FFArm arm;
    private FFExtensionArm extensionArm;
    private FFIntake intake;
    private final String FREIGHT_SYSTEM_NAME = "FreightSystem";

    // flags used in this class

    // indicates if the capping arm is in the way of the delivery system.
    private boolean isClawInTheWay = true;

    // indicates if the arm is ready to be extended (which means that the intake is out of the way) specifically for autonomous
    private boolean readyToExtend = false;

    private ElapsedTime timer;
    private Telemetry telemetry;
    private Configuration configuration;
    private boolean loggingOn;
    private DataLogging logFile;


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFFreightSystem(FFArm ffArm, FFIntake ffIntake, FFExtensionArm ffExtensionArm, HardwareMap hardwareMap, Telemetry telemetry, AllianceColor allianceColor, RevLEDBlinker ledBlinker) {
        state = State.IDLE;
        this.arm = ffArm;
        this.extensionArm = ffExtensionArm;
        this.intake = ffIntake;
        this.telemetry = telemetry;
        timer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public String getName() {
        return FREIGHT_SYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (state == State.READY_TO_CYCLE) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean init(Configuration config) {
        this.configuration = config;
        extensionArm.init(config);
        state = State.WAITING_FOR_ARM_INIT;
        return false;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void setDataLog(DataLogging logFile) {

    }

    @Override
    public void enableDataLogging() {
        loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        loggingOn = false;
    }


    private void logState() {
        if (loggingOn && logFile != null) {
            logFile.logOnChange(getName() + " state = " + state.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logFile.logOnChange(getName() + " command = " + command);
        }
    }


    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    public void sportMode() {
        mode = Mode.AUTO;
    }

    public void manualMode() {
        mode = Mode.MANUAL;
    }

    public String getMode() {
        return mode.toString();
    }

    public String getState() {
        return state.toString();
    }

    public void start() {
        logCommand("start");
        //puts the state machine into the actual freight loop
        if (state == State.READY_TO_CYCLE) {
            state = State.START_CYCLE;

        } else {
            //you are a loser who pushed the button on accident. so we arent doing anything
        }
    }


    public void extend() {
        if (state == State.WAITING_TO_EXTEND) {
            logCommand("extend(manual)");
            switch (level) {
                case TOP: {
                    extensionArm.extendToTop();
                    state = State.WAITING_FOR_EXTENSION;
                }
                break;

                case MIDDLE: {
                    extensionArm.extendToMiddle();
                    state = State.WAITING_FOR_EXTENSION;
                }
                break;

                case BOTTOM: {
                    extensionArm.extendToBottom();
                    state = State.WAITING_FOR_EXTENSION;
                }
                break;
            }
        } else {
            //you are a loser who pushed the button on accident. so we arent doing anything
        }
    }

    public void dump() {
        if (state == State.WAITING_TO_DUMP) {
            logCommand("dump");
            extensionArm.dump();
            state = State.WAITING_FOR_DUMP_COMPLETE;
            timer.reset();
        } else {
            //you are a loser who pushed the button on accident. so we arent doing anything
        }
    }

    public void ejectOntoFLoor() {
        if (state == State.WAITING_FOR_TRANSFER) {
            logCommand("emergency eject");
            intake.ejectOntoFloor();
            timer.reset();
            state = State.EMERGENCY_EJECT;
        }
    }


    public void intakeShutoff() {
        intake.shutdown();
    }


    public void setPhaseTeleop() {
        phase = Phase.TELEOP;
    }

    public void setPhaseAutonomus() {
        phase = Phase.AUTONOMUS;
    }

    public void setTop() {
        level = Level.TOP;
    }

    public void setMiddle() {
        level = Level.MIDDLE;
    }

    public void setBottom() {
        level = Level.BOTTOM;
    }


    //methods for testing// used for telemtry in teleop

    public boolean isReadyToDump() {
        return extensionArm.isReadyToDump();
    }

    public boolean isDumpComplete() {
        return extensionArm.isDumpComplete();
    }

    public boolean isRetractionComplete() {
        return extensionArm.isRetractionComplete();
    }

    //Autonomus
    public boolean isReadyToExtend() {
        return readyToExtend;
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************


    public void update() {
        intake.update();
        extensionArm.update();
        logState();
        switch (state) {
            case IDLE: {
                //just chillin
            }
            break;


            //********************************************************************************
            // INIT states
            //********************************************************************************

            case WAITING_FOR_ARM_INIT: {
                if (extensionArm.isInitComplete()) {
                    intake.init(configuration);
                    state = State.WAITING_FOR_INTAKE_INIT;
                }
            }
            break;

            case WAITING_FOR_INTAKE_INIT: {
                if (intake.isInitComplete()) {
                    state = State.READY_TO_CYCLE;
                }
            }
            break;

            case READY_TO_CYCLE: {
                readyToExtend = false;
                //just chillin and resetin a variable
            }
            break;


            //********************************************************************************
            // RUNNING states
            //********************************************************************************

            case START_CYCLE: {

                if (phase == Phase.AUTONOMUS) {
                    intake.toVerticalPosition();
                    state = State.WAITING_FOR_INTAKE_REPOSITION1;
                }
                if (phase == Phase.TELEOP) {
                    intake.intakeAndTransfer();
                    state = State.WAITING_FOR_TRANSFER;
                }
            }
            break;

            case HOLD_FREIGHT: {
                if (extensionArm.isRetractionComplete()) {
                    intake.transfer();
                    state = State.WAITING_FOR_TRANSFER;
                }
            }
            break;


            case WAITING_FOR_TRANSFER: {
                if (intake.didTransferFail()) {
                    //for emergencies only. should do nothing usually
                    state = State.READY_TO_CYCLE;
                } else {
                    if (intake.isTransferComplete()) {
                        state = State.WAITING_TO_EXTEND;
                    }
                }

            }
            break;

            case WAITING_FOR_INTAKE_REPOSITION1: {
                if (intake.isRotationComplete()) {
                    state = State.WAITING_TO_EXTEND;
                    readyToExtend = true;
                }
            }
            break;


            // the name of this state is innacurate, but i am too lazy to change it.
            case WAITING_TO_EXTEND: {
                if (phase == Phase.AUTONOMUS) {
                    //just hanging out waiting for tanya to extend the delivery
                } else {
                    switch (mode) {
                        case AUTO: {
                            extend();
                        }
                        break;
                        case MANUAL: {
                            //just chillin
                        }
                        break;

                    }
                }
            }
            break;

            case WAITING_FOR_EXTENSION: {
                if (extensionArm.isReadyToDump()) {
                    state = State.WAITING_TO_DUMP;
                }
            }
            break;

            case WAITING_TO_DUMP: {
                //just chillin waiting for someone to tell me to dump
            }
            break;

            case WAITING_FOR_DUMP_COMPLETE: {
                if (extensionArm.isDumpComplete()) {


                    if (mode == Mode.AUTO) {
                        // todo This is not quite what Dade was asking for. You have the automatic
                        // start of the intake. But can the intake
                        // be down and running WHILE the retraction is taking place? Not after
                        // the retraction is finished. Sometimes Dade was already in the warehouse
                        // and waiting for the intake to be available while the arm was still
                        // retracting. If he could intake while the retraction was finishing,
                        // and the intake just held the freight in vertical position until
                        // retraction complete, he could save some time. Before you attempt this
                        // I would fix other problems, commit and then start this. It is going
                        // to take a mod to the intake state machine to make this happen. If you get
                        // into trouble you can roll back to the commit.
                        // FFFreightSystem will have to tell the intake what the status of the
                        // retraction is. Or the intake will have to ask the extension arm directly.
                        // Intake will then have to either hold the freight until
                        // retraction is complete, or transfer it right away if retraction is
                        // already complete. Essentially, before this, intake just did its thing without having
                        // to take any other system into consideration. Now it has to listen to
                        // what FFFreightSystem is telling it. You can do this by intake providing
                        // methods to set retraction status which FFFreightSystem can call. Or perhaps
                        // intake just calls isRetractionComplete() from the extension arm. Be careful with
                        // initializing the communication.
                        state = State.START_INTAKE;
                    } else {
                        state = State.WAITING_FOR_RETRACTION;
                    }

                }
            }
            break;

            case WAITING_FOR_RETRACTION: {
                if (extensionArm.isRetractionComplete()) {
                    switch (phase) {
                        case TELEOP: {
                            //all done time to chill
                            state = State.READY_TO_CYCLE;
                        }
                        break;

                        case AUTONOMUS: {
                            //gotta do something with the intake. probably just tuck it back in to transfer position
                            intake.toTransferPosition();
                            state = State.WAITING_FOR_INTAKE_REPOSITION2;
                        }
                        break;
                    }
                }
            }
            break;

            case WAITING_FOR_INTAKE_REPOSITION2: {
                if (intake.isRotationComplete()) {
                    state = State.READY_TO_CYCLE;
                }
            }
            break;

            case START_INTAKE: {
                intake.intakeAndHold();
                state = State.HOLD_FREIGHT;
            }


            ////////////////////////////  Uh-Oh states //////////////////////////////

            case EMERGENCY_EJECT: {
                if (!intake.isIntakeFull() && timer.milliseconds() > 500) {
                    intake.toVerticalPosition();
                    state = State.EJECT_COMPLETE;
                }
            }
            break;

            case EJECT_COMPLETE: {
                if (intake.isRotationComplete()) {
                    state = State.READY_TO_CYCLE;
                }
            }
        }
    }
}

