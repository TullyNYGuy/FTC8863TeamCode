package org.firstinspires.ftc.teamcode.Lib.DecodeLib.Autonomous;
//
//
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This is the interface class for all autonomous state machines for Decode.
 */
public interface DecodeAutonomousStateMachine {

    /**
     * This method will be called when you want to start the autonomous. Typically, your class will
     * have some variable that saves whether the autonomous is complete or not. So your start
     * method will probably set that variable to indicate that the autonomous is not complete. If
     * your class uses a state machine to sequence your auto, then this method should start the
     * state machine
     */
    void start();

    /**
     * This method will contain statements that create all of the robot movements. This should get called
     * from the constructor for the autonomous state machine.
     */
    void createMovements();

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
