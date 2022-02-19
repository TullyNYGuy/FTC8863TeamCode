package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

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
        INTAKE_STUFF,
        DELIVERY_STUFF,
        RETRACT_EXTENSION_ARM,

        WAIT_FOR_ARM_INIT,
        INIT_INTAKE,
        WAIT_FOR_INTAKE_INIT,
        INIT_DONZO
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
    private State state = State.INTAKE_STUFF;
    private Mode mode = Mode.MANUAL;
    private FFExtensionArm ffExtensionArm;
    private FFIntake ffIntake;
    private final String FREIGHT_SYSTEM_NAME = "FreightSystem";
    private Telemetry telemetry;
    private Configuration configuration;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFFreightSystem(FFIntake ffIntake, FFExtensionArm ffExtensionArm, HardwareMap hardwareMap, Telemetry telemetry, AllianceColor allianceColor, RevLEDBlinker ledBlinker) {
        state = State.INTAKE_STUFF;
        this.ffExtensionArm = ffExtensionArm;
        this.ffIntake = ffIntake;
        this.telemetry = telemetry;
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
        if (state == State.INIT_DONE) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean init(Configuration config) {
        this.configuration = config;
        ffExtensionArm.init(config);
        state = State.WAIT_FOR_ARM_INIT;
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
        state = State.INTAKE_STUFF;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void update() {
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
                    state = State.INIT_DONZO;
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
    }
}
