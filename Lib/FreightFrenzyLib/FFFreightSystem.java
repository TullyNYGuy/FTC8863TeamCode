package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
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
        WAITING_FOR_INTAKE_TO_VERTICAL_AUTONOMOUS,
        EXTEND_AUTONOMOUS,
        WAITING_FOR_EXTENSION_COMPLETE,
        WAITING_TO_DUMP,
        WAITING_FOR_DUMP_COMPLETE,
        WAITING_FOR_RETRACTION_COMPLETE,

        START_INTAKE,


        WAITING_FOR_INTAKE_REPOSITION1,
        WAITING_FOR_INTAKE_TO_TRANSFER_POSITION_AUTONOMOUS,


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
        BOTTOM,
        SHARED
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
    private Mode mode = Mode.AUTO;
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

    // indicates if the delivery system is ready to cycle
    private boolean readyToCycle = false;

    private ElapsedTime timer;
    private Telemetry telemetry;
    private Configuration configuration;
    private boolean loggingOn;
    private DataLogging logFile;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;


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
            logCommand("Init complete");
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
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
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
        intake.setDataLog(logFile);
        extensionArm.setDataLog(logFile);
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        intake.enableDataLogging();
        extensionArm.enableDataLogging();
        loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        intake.disableDataLogging();
        extensionArm.disableDataLogging();
        loggingOn = false;
    }


    private void logState() {
        if (loggingOn && logFile != null) {
            logStateOnChange.log(getName() + " state = " + state.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
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

    public String getLevel() {
        return level.toString();
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
            if (phase == Phase.TELEOP) {
                logCommand("extend(teleop)");
                switch (level) {
                    case TOP: {
                        logCommand("(teleop)extending to top");
                        extensionArm.extendToTop();
                        state = State.WAITING_FOR_EXTENSION_COMPLETE;
                    }
                    break;

                    case MIDDLE: {
                        logCommand("(teleop)extending to middle");
                        extensionArm.extendToMiddle();
                        state = State.WAITING_FOR_EXTENSION_COMPLETE;
                    }
                    break;

                    case BOTTOM: {
                        logCommand("(teleop)extending to bottom");
                        extensionArm.extendToBottom();
                        state = State.WAITING_FOR_EXTENSION_COMPLETE;
                    }
                    break;

                    case SHARED: {
                        logCommand("(teleop)extending to shared");
                        extensionArm.extendToShared();
                        state = State.WAITING_FOR_EXTENSION_COMPLETE;
                    }
                    break;
                }
            }
            if (phase == Phase.AUTONOMUS) {
                logCommand("extend(autonomous)");
                intake.toVerticalPosition();
                state = State.WAITING_FOR_INTAKE_TO_VERTICAL_AUTONOMOUS;
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


    // todo We need a command to prepare the freight system for shutting down. And a method for
    // checking when the preparation is complete. At the end of autonomous, the intake is vertical
    // and the arm is at 1/2" extension. Currently, when the robot loses power, the intake rotates
    // down to the floor, causing problems with "completely inside" the park zone. It does this due
    // to the balance of the intake causing it to rotate when it starts out vertical with no power.
    // A command to prepare for shutdown would move the intake to the transfer position so that it
    // has a pretty good chance of staying there once it loses power. We might also consider moving
    // the extension arm to the reset position just to save a bit of time once the freight system
    // has to init for teleop. Moving the extension arm to reset would require an addition to the
    // FFExtensionArm class to give it a way to reset the extension arm position. I don't think
    // moving the arm to the reset is super important so it may be that the extra complexity is worth
    // it.

    // todo Currently in autonomous, when we dump into the low level, the arm has to extend out in
    // order to start the retraction sequence. The arm is hitting the shipping hub when it extends.
    // We currently have no control over when the arm retraction starts. It just happens automatically.
    // We are going to have to back away from the hub before the extension arm starts to retract if we
    // are going to avoid hitting it. So
    // we need a way of telling FFExtensionArm to wait to start the retraction until it is told to do
    // it. It looks like it is pretty easy make this change in the FFExtensionArm state machine.
    //
    // In FFExtensionArm you would have to:
    //    provide a method that FFFreightSystem can use to set a mode in the FFExtensionArm that holds
    //        off the retraction until FFFreightSystem tells FFExtensionArm to retract
    //    provide a command in FFExtensionArm that FFFreightSystem can use to trigger the retraction.
    //    alter the state machine to add a state where it is waiting for a retraction command
    //
    // In FFFreightSystem you would have to:
    //    provide a method that an autonomous state machine can use to set the mode for holding off
    //        the retraction
    //    provide a command that an autonomous state machine can use to trigger the retraction
    //    you may or may not want to add a state to the FFFreightSystem state machine that is entered
    //       once the dump is complete and when a retraction is waiting to be triggered. I can see
    //       it being useful for double checking the command to trigger a retraction is issued at
    //       at the proper time. And also for logging the state in the log file. But looking at the
    //       current state machine, I don't think it is actually needed.
    //
    // Once you have this new functionality, the autonomous state machine can use it. It can be used
    // during a movement, such as a lineToHeading, by adding a displacemment Marker to the trajectory
    // that triggers the retraction after the robot has moved a certain distance into the trajectory.
    // See the section titled Global Displacement Markers on this page:
    // https://learnroadrunner.com/markers.html#displacement-markers-basics

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

    public void setShared() {
        level = Level.SHARED;
    }




    //methods for testing// used for telemtry in teleop and autonomous

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

    public boolean isReadyToCycle() {
        return readyToCycle;
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
                    logCommand("extension arm init complete. waiting for intake init");
                    intake.init(configuration);
                    state = State.WAITING_FOR_INTAKE_INIT;
                }
            }
            break;

            case WAITING_FOR_INTAKE_INIT: {
                if (intake.isInitComplete()) {
                    logCommand("intake init complete");
                    if (phase == Phase.AUTONOMUS) {
                        state = State.WAITING_TO_EXTEND;
                    }
                    if (phase == Phase.TELEOP) {
                        state = State.READY_TO_CYCLE;
                    }
                }
            }
            break;

            //********************************************************************************
            // RUNNING states
            //********************************************************************************

            case READY_TO_CYCLE: {
                //just chillin waitin for a command
            }
            break;

            case START_CYCLE: {
                //we have to reset variables here because this is the first state it goes to the second+ times
                readyToExtend = false;

                if (phase == Phase.AUTONOMUS) {
                    logCommand("(auto)moving to vertical position");
                    intake.toVerticalPosition();
                    state = State.WAITING_FOR_INTAKE_REPOSITION1;
                }
                if (phase == Phase.TELEOP) {

                    if(isRetractionComplete()){
                        logCommand("(teleop)retraction complete. intaking and transferring...");
                        intake.intakeAndTransfer();
                        state = State.WAITING_FOR_TRANSFER;
                    }
                    else{
                        logCommand("(teleop)intaking and holding...");
                        intake.intakeAndHold();
                        state = State.HOLD_FREIGHT;
                    }
                }
            }
            break;

            case HOLD_FREIGHT: {
                if (extensionArm.isRetractionComplete() && intake.hasIntakeIntaked()) {
                    logCommand("retraction and intake complete. transferring...");
                    intake.transfer();
                    state = State.WAITING_FOR_TRANSFER;
                }
            }
            break;


            case WAITING_FOR_TRANSFER: {
                if (intake.didTransferFail()) {
                    logCommand("transfer failed!");
                    //for emergencies only. should do nothing usually
                    state = State.READY_TO_CYCLE;
                } else {
                    if (intake.isTransferComplete()) {
                        logCommand("transfer completed. waiting for extension...");
                        state = State.WAITING_TO_EXTEND;
                    }
                }

            }
            break;

            case WAITING_FOR_INTAKE_REPOSITION1: {
                if (intake.isRotationComplete()) {
                    logCommand("rotation completed. waiting for extension...");
                    state = State.WAITING_TO_EXTEND;
                    readyToExtend = true;
                }
            }
            break;

            case WAITING_TO_EXTEND: {
                if (phase == Phase.AUTONOMUS) {
                    //just hanging out waiting for tanya to extend the delivery
                } else {
                    switch (mode) {
                        case AUTO: {
                            logCommand("auto mode. extending...");
                            extend();
                        }
                        break;
                        case MANUAL: {
                            //just chillin waitin for the driver to push the button for extend
                        }
                        break;

                    }
                }
            }
            break;

            case WAITING_FOR_INTAKE_TO_VERTICAL_AUTONOMOUS: {
                if (intake.isRotationComplete()) {
                    // intake is vertical, now extend the arm
                    switch (level) {
                        case TOP: {
                            logCommand("rotation complete. extending to top...");
                            extensionArm.extendToTop();
                            state = State.WAITING_FOR_EXTENSION_COMPLETE;
                        }
                        break;

                        case MIDDLE: {
                            logCommand("rotation complete. extending to middle...");
                            extensionArm.extendToMiddle();
                            state = State.WAITING_FOR_EXTENSION_COMPLETE;
                        }
                        break;

                        case BOTTOM: {
                            logCommand("rotation complete. extending to bottom...");
                            extensionArm.extendToBottom();
                            state = State.WAITING_FOR_EXTENSION_COMPLETE;
                        }
                        break;

                        case SHARED: {
                            logCommand("rotation complete. extending to shared...");
                            extensionArm.extendToShared();
                            state = State.WAITING_FOR_EXTENSION_COMPLETE;
                        }
                        break;
                    }
                }
            }
            break;

            case WAITING_FOR_EXTENSION_COMPLETE: {
                if (extensionArm.isReadyToDump()) {
                    logCommand("ready to dump");
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
                        logCommand("dump complete. starting new cycle...");

                        if(phase == Phase.AUTONOMUS){
                            state = State.WAITING_FOR_RETRACTION_COMPLETE;
                        }
                        else {
                            state = State.START_CYCLE;
                        }
                }
            }
            break;

            case WAITING_FOR_RETRACTION_COMPLETE: { /// autonomous only
                if (extensionArm.isRetractionComplete()) {
                    logCommand("retraction complete. intake to transfer position...");
                    //gotta do something with the intake. probably just tuck it back in to transfer position
                            intake.toTransferPosition();
                            state = State.WAITING_FOR_INTAKE_TO_TRANSFER_POSITION_AUTONOMOUS;
                }
            }
            break;

            case WAITING_FOR_INTAKE_TO_TRANSFER_POSITION_AUTONOMOUS: {
                if (intake.isRotationComplete()) {
                    logCommand("rotation complete. ready for a new cycle...");
                    state = State.READY_TO_CYCLE;
                }
            }
            break;

            ////////////////////////////  Uh-Oh states //////////////////////////////

            case EMERGENCY_EJECT: {
                if (!intake.isIntakeFull() && timer.milliseconds() > 500) {
                    logCommand("intake is empty. moving to vertical position...");
                    intake.toVerticalPosition();
                    state = State.EJECT_COMPLETE;
                }
            }
            break;

            case EJECT_COMPLETE: {
                if (intake.isRotationComplete()) {
                    logCommand("rotation complete. ready for a new cycle...");
                    state = State.READY_TO_CYCLE;
                }
            }
        }
    }
}

