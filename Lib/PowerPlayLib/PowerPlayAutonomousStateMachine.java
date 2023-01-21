package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

public interface PowerPlayAutonomousStateMachine {

    /**
     * This method will be called when you want to start the autonomous. Typically, your class will
     * have some variable that saves whether the autonomous is complete or not. So your start
     * method will probably set that variable to indicate that the autonomous is not complete. If
     * your class uses a state machine to sequence your auto, then this method should start the
     * state machine
     */
    void start();

    /**
     * This method will contain statements that create all of the road runner trajectories for your
     * auto. This method should be called from the constructor for your class. Each trajectory takes
     * about 1/2 second to generate so generating them all before you actually run the auto saves
     * time.
     */
    void createTrajectories();

    /**
     * This method has to pick which of the previously calculated trajectories to use to get to
     * the parking location.
     *
     * @param parkLocation
     */
    void setParkLocation(PowerPlayField.ParkLocation parkLocation);

    /**
     * This method will typically run the state machine for your auto.
     */
    void update();

    /**
     * This method will tell the user if your auto is finished or not.
     *
     * @return - true if complete, false if still running
     */
    boolean isComplete();

    String getCurrentState();

    void setDataLog(DataLogging logFile);

    void enableDataLogging();

    void disableDataLogging();

    String getName();
}
