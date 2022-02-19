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
private enum State{
        INTAKE_STUFF,
        DELIVERY_STUFF
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
private State state = State.INTAKE_STUFF;
private FFExtensionArm ffExtensionArm;
private FFIntake ffIntake;
    private final String FREIGHT_SYSTEM_NAME = "FreightSystem";
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
public FFFreightSystem(FFIntake ffIntake, FFExtensionArm ffExtensionArm, HardwareMap hardwareMap, Telemetry telemetry, AllianceColor allianceColor, RevLEDBlinker ledBlinker){
    state = State.INTAKE_STUFF;

}
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
public String getName(){
    return FREIGHT_SYSTEM_NAME;
}

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public boolean init(Configuration config) {
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

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
        public void update(){
            switch (state) {
                case INTAKE_STUFF: {
                    //the intake is doing intake stuff. not our problem until the transfer happens
                    if (ffIntake.isTransferComplete()) {
                        state = State.DELIVERY_STUFF;
                        ffExtensionArm.extendToTop();
                    }
                }
                break;

                case DELIVERY_STUFF: {
                    //the delivery is doing delivery stuff. not our problem until it gets back
                    //if the state is idle then we know that the delivery process is complete
                    if(ffExtensionArm.isStateIdle()){
                        state = State.INTAKE_STUFF;
                    }
                }
            }
        }
}
