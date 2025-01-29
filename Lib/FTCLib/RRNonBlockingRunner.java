package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

/**
 * I don't really get RoadRunner's preference to run Actions as blocking. It totally makes putting
 * actions into your own state machine nasty. My preference is to use my own state machine and call
 * the run of an Action from within the state machine loop. This class makes it easy to run an Action
 * in a non-blocking way.
 */
public class RRNonBlockingRunner {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Action actionToRun;
    /**
     * A null Telemetry packet
     */
    private TelemetryPacket packet = new TelemetryPacket();


    private boolean isComplete = false;

    /**
     * Check if the action is complete.
     * @return
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * For those people who are familiar with the Drive.isBusy() from Road Runner 0.5 and prefer it
     * @return
     */
    public boolean isBusy() {
        return !isComplete;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public RRNonBlockingRunner(Action actionToRun) {
        this.actionToRun = actionToRun;
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


    /**
     * Just like roadrunner's runBlocking except that this is non blocking. runBlocking does not
     * require you to call it in a loop. You MUST call this in a loop.
     * @param packet
     * @return just like runBlocking, true if it needs to run again. False if it is complete. Or
     * just call isComplete() if you want the opposite logic.
     */
    public boolean runNonBlocking(TelemetryPacket packet) {
        // run returns true if it is not complete and needs to run again
        // run returns false if it is complete
        isComplete = !actionToRun.run(packet);
        return !isComplete;
    }

    /**
     * If you don't care about the telemetry packet, then use this call.
     * @return
     */
    public boolean runNonBlocking() {
        return runNonBlocking(new TelemetryPacket());
    }

}
