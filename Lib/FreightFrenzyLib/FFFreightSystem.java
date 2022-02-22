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
        WAITING_FOR_TRANSFER,
        WAITING_FOR_CLAW_REPOSITION,
        WAITING_FOR_EXTENSION,
        WAITING_TO_DUMP,
        WAITING_FOR_DUMP,
        WAITING_FOR_RETRACTION,

        //ONLY IN AUTO STATES//
        WAITING_FOR_INTAKE_REPOSITION,

        //Uh-Oh states//
        EMERGENCY_EJECT,
        DEFCON_1,
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
    private boolean isClawInTheWay = true;

    private ElapsedTime timer;
    private Telemetry telemetry;
    private Configuration configuration;



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

    }

    @Override
    public void disableDataLogging() {

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
        //puts the state machine into the actual freight loop
        if(state == State.READY_TO_CYCLE){
            state = State.START_CYCLE;

        }
        else{
            //you are a loser who pushed the button on accident. so we arent doing anything
        }
    }

    public void extend(){
        if(state == State.WAITING_FOR_CLAW_REPOSITION){
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
        }
        else{
            //you are a loser who pushed the button on accident. so we arent doing anything
        }
    }

    public void dump() {
        if(state == State.WAITING_TO_DUMP ){
            extensionArm.dump();
            state = State.WAITING_FOR_DUMP;
            timer.reset();
        }
        else{
            //you are a loser who pushed the button on accident. so we arent doing anything
        }
    }

    public void ejectOntoFLoor(){
        if(state == State.WAITING_FOR_TRANSFER){
            intake.ejectOntoFloor();
            timer.reset();
            state = State.EMERGENCY_EJECT;
        }
    }


    public void intakeShutoff(){
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






    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************



    public void update() {
        intake.update();
        extensionArm.update();
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
                extensionArm.resetDump();
                extensionArm.resetRetraction();
                //just chillin and resetin some variables

                intake.everythingIsOk();
                //this is just in case things go horribly wrong. hopefully it wont ever do anything.
            }
            break;


            //********************************************************************************
            // RUNNING states
            //********************************************************************************

            // todo, Now that I am thinking about it, why not just have the pickup arm move the
            // claw as soon as play is pressed?

            case START_CYCLE: {
                if (isClawInTheWay){
                    if (phase == Phase.AUTONOMUS) {
                        arm.storageWithElement();
                        // todo are you sure this is the state for autonomous?
                        state = State.WAITING_FOR_TRANSFER;
                        isClawInTheWay = false;
                    }
                    if (phase == Phase.TELEOP) {
                        arm.storageWithElement();
                        intake.intakeAndTransfer();
                        state = State.WAITING_FOR_TRANSFER;
                        isClawInTheWay = false;
                    }
                }
                else {
                    if (phase == Phase.AUTONOMUS) {
                        // todo are you sure this is the state for autonomous?
                        state = State.WAITING_FOR_TRANSFER;
                    }
                    if (phase == Phase.TELEOP) {
                        intake.intakeAndTransfer();
                        state = State.WAITING_FOR_TRANSFER;
                    }
                }

            }
            break;

            case WAITING_FOR_TRANSFER: {
                if(intake.Uh_Oh()){
                    //for emergencies only. should do nothing usually
                    state = State.READY_TO_CYCLE;
                }
                else {
                    if (intake.isTransferComplete()) {
                        state = State.WAITING_FOR_CLAW_REPOSITION;
                    }
                }

            }
            break;
            // the name of this state is innacurate, but i am too lazy to change it.
            case WAITING_FOR_CLAW_REPOSITION: {
                   if(phase == Phase.AUTONOMUS){
                       //just hanging out waiting for tanya to extend the delivery
                   }
                   else{
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
                if(extensionArm.isReadyToDump()){
                    state = State.WAITING_TO_DUMP;
                }
            }
            break;

            case WAITING_TO_DUMP: {
                //just chillin
            }
            break;

            case WAITING_FOR_DUMP: {
                if(extensionArm.isDumpComplete() ){
                    state = State.WAITING_FOR_RETRACTION;
                }
            }
            break;

            case  WAITING_FOR_RETRACTION: {
                // todo this seems sketchy. IDLE state in the extension arm can mean a couple of
                // different things. It would be better to nail down exactly when retraction is complete.
                // So maybe a different way for the extension arm to tell you is has retracted? Like
                // the delivery box is ready for a transfer?
                if(extensionArm.isRetractionComplete()){
                    switch (phase) {
                        case TELEOP: {
                            //all done time to chill
                            state = State.READY_TO_CYCLE;
                        }
                        break;

                        case AUTONOMUS: {
                            //gotta do something with the intake. probably just tuck it back in to transfer position
                            intake.toTransferPosition();
                            state = State.WAITING_FOR_INTAKE_REPOSITION;
                        }
                        break;
                    }
                }
            }
            break;

            case WAITING_FOR_INTAKE_REPOSITION: {
                if (intake.isRotationComplete()){
                    state = State.READY_TO_CYCLE;
                }
            }
            break;


            ////////////////////////////  Uh-Oh states //////////////////////////////

            case EMERGENCY_EJECT: {
                if(!intake.isIntakeFull() && timer.milliseconds() > 500){
                    intake.toVerticalPosition();
                    state = State.DEFCON_1;
                }
            }
            break;

            case DEFCON_1: {
                if(intake.isRotationComplete()){
                    state = State.READY_TO_CYCLE;
                }
            }
        }
    }


        ///extra copy of internal level state machine for me to copy and paste. please disregard
    /*

    if (phase == Phase.AUTONOMUS){
                    switch(level) {
                        case TOP: {

                        }
                        break;
                        case MIDDLE: {

                        }
                        break;
                        case BOTTOM: {

                        }
                        break;
                    }
                }
                if (phase == Phase.TELEOP){
                    switch(level) {
                        case TOP: {

                        }
                        break;
                        case MIDDLE: {

                        }
                        break;
                        case BOTTOM: {

                        }
                        break;
                    }
                }

     */













    /*public void update() {
        ffIntake.update();
        ffExtensionArm.update();
        switch (state) {
            //init states
            case WAIT_FOR_ARM_INIT: {
                if (ffExtensionArm.isInitComplete()) {
                    state = State.INIT_INTAKE;
                }
            }
            break;

            case INIT_INTAKE: {
                ffIntake.init(configuration);
                state = State.WAIT_FOR_INTAKE_INIT;
            }
            break;

            case WAIT_FOR_INTAKE_INIT: {
                if (ffIntake.isInitComplete()) {
                    state = State.INIT_DONE;
                }
            }
            break;

            case INIT_DONE: {
                // does nothing. we just hanging out
            }
            break;

            //running states
            case INTAKE_STUFF: {
                //AUTO MODE//
                if (mode == Mode.AUTO) {
                    //the intake is doing intake stuff. not our problem until the transfer happens
                    if (ffIntake.isTransferComplete()) {
                        state = State.DELIVERY_STUFF;
                        ffExtensionArm.extendToTop();
                    }
                }
                //MANUAL MODE//
                if (mode == Mode.MANUAL) {
                    //still runs the state machine to make sure that we are in the right state if/when we switch to auto mode
                    if (ffIntake.isTransferComplete()) {
                        state = State.DELIVERY_STUFF;
                    }
                }
            }
            break;

            case DELIVERY_STUFF: {
                //the delivery is doing delivery stuff. not our problem until it gets back
                //if the state is idle then we know that the delivery process is complete
                //AUTO MODE//
                if (mode == Mode.AUTO) {
                    if (ffExtensionArm.isStateWaitingToDump()) {
                        state = State.RETRACT_EXTENSION_ARM;
                    }
                }
                //for the moment these two are identical, but we might want to change one of them eventually.
                // i just made two for the sake of symmetry
                //MANUAL MODE//
                if (mode == Mode.MANUAL) {
                    if (ffExtensionArm.isStateWaitingToDump()) {
                        state = State.RETRACT_EXTENSION_ARM;
                    }
                }
            }

            //we need a state that waits for the arm to be pulled in or else the distance sensor
            // will put the mode directly back to delivery stuff. No extending for you :/
            case RETRACT_EXTENSION_ARM: {
                if (ffExtensionArm.isStateIdle()) {
                    state = State.INTAKE_STUFF;
                }
            }
            break;
        }
        telemetry.addData("state = ", state.toString());
    }*/
}

