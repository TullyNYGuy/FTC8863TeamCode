package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class PowerPlaySpeedController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum ControllerState {

        // INIT STATES
        PRE_INIT,
        INIT_COMPLETE,

        // NORMAL DRIVING STATES
        MAX_SPEED,
        HIGH_SPEED,
        LOW_SPEED,

        // SCORING STATES
        HEADED_FOR_JUNCTION,
        CLOSE_TO_JUNCTION,
        CLOSE_TO_GROUND_JUNCTION,
        AT_JUNCTION,
        FINISH_LINEUP_FOR_JUNCTION,

        // PICKUP STATES
        HEADED_FOR_SUBSTATION,
        CLOSE_TO_SUBSTATION,
        AT_SUBSTATION,
        FINISH_LINEUP_FOR_SUBSTATION
    }

    private ControllerState controllerState = ControllerState.PRE_INIT;

    private enum Command {
        NONE,
        SWITCH_SPEED,
        TEST_THE_DROP,
        DROP_CONE,
        PICKUP_CONE,
        APPROACHING_JUNCTION_POLE,
        APPROACHING_GROUND_JUNCTION,
        APPROACHING_SUBSTATION
    }

    private Command command = Command.NONE;

    /**
     * What the driver is intending to score on. Set by button press by driver 2
     */
    private enum ScoringTarget {
        UNKNOWN,
        GROUND_JUNCTION,
        LOW_POLE,
        MEDIUM_POLE,
        HIGH_POLE
    }

    private ScoringTarget scoringTarget = ScoringTarget.UNKNOWN;

    public static double FULL_POWER = 1.0;
    public static double HIGH_POWER = .75;
    public static double LOW_POWER = .60;

    private double currentPower = HIGH_POWER;

    public double getCurrentPower() {
        return currentPower;
    }

    private double previousPower = currentPower;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime stopTimer;

    // initialization is complete
    private boolean initComplete = false;

    // Rev 2m distance sensor, one for robot facing forward and one for robot facing backward (inverse)
    private PowerPlay2mDistanceSensor distanceSensorForNormal;
    private PowerPlay2mDistanceSensor distanceSensorForInverse;
    private PowerPlay2mDistanceSensor distanceSensorToUse;
    private final double DISTANCE_LIMIT_FOR_JUNCTION = 12; // INCHES
    private final double DISTANCE_LIMIT_FOR_CONE = 12; // INCHES

    private PowerPlayRobot robot;
    private double distance;

    private boolean foundJunction = false;
    private boolean foundCoune = false;


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlaySpeedController(HardwareMap hardwareMap,
                                    Telemetry telemetry,
                                    PowerPlayRobot robot) {
        this.robot = robot;
        this.distanceSensorForNormal = robot.dualDistanceSensors.distanceSensorNormal;
        // for now there is only one distance sensor so just make the inverse distance sensor the same as the normal one
        this.distanceSensorForInverse = robot.dualDistanceSensors.distanceSensorInverse;
        stopTimer = new ElapsedTime();

        controllerState = ControllerState.PRE_INIT;
        // init has not been started yet
        initComplete = false;
        commandComplete = true;
        // start at high power (75%)
        setPower(HIGH_POWER);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String getName() {
        return "SpeedController";
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + controllerState.toString());
        }
    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    private void setPower(double power) {
        logCommand("speed set to " + power);
        currentPower = power;
        robot.robotModes.setMaxDrivingPower(power);
    }

    private void setDistanceSensorToUse() {
        if (robot.robotModes.getDirectionSwap() == PowerPlayRobotModes.DirectionSwap.NORMAL) {
            distanceSensorToUse = distanceSensorForNormal;
        } else {
            // robot is driving inversed
            distanceSensorToUse = distanceSensorForNormal;
        }
    }

    private void setupForConeScore() {
        switch(scoringTarget) {
            case UNKNOWN:{
                // uh oh. This should not happen. Do nothing.
            }
            break;
            case GROUND_JUNCTION: {
                robot.coneGrabberArmController.moveToGroundThenPrepareToRelease();
            }
            break;
            case LOW_POLE: {
                robot.coneGrabberArmController.moveToLowThenPrepareToRelease();
            }
            break;
            case MEDIUM_POLE: {
                robot.coneGrabberArmController.moveToMediumThenPrepareToRelease();
            }
            break;
            case HIGH_POLE: {
                robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
            }
            break;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public String getSpeedControllerState() {
        return controllerState.toString();
    }

    @Override
    public boolean init(Configuration config) {
        // start the init for the extension retraction mechanism
        logCommand("Init starting");
        // There is no direct control of hardware so there is no init. It goes directly to complete.
        commandComplete = false;
        controllerState = ControllerState.INIT_COMPLETE;
        logCommand("Init");
        return false;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    //********************************************************************************
    // Public commands for setting target and possibly adjusting speed
    //********************************************************************************

    public void approachingGroundJunction() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            logCommand("approaching ground junction command");
            scoringTarget = ScoringTarget.GROUND_JUNCTION;
            command = Command.APPROACHING_GROUND_JUNCTION;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("approaching ground junction command ignored");
        }
    }

    public void approachingLowJunction() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            logCommand("approaching low junction command");
            scoringTarget = ScoringTarget.LOW_POLE;
            approachingJunction();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("approaching low junction command ignored");
        }
    }

    public void approachingMediumJunction() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            logCommand("approaching medium junction command");
            scoringTarget = ScoringTarget.MEDIUM_POLE;
            approachingJunction();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("approaching medium junction command ignored");
        }
        ;
    }

    public void approachingHighJunction() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            logCommand("approaching high junction command");
            scoringTarget = ScoringTarget.HIGH_POLE;
            approachingJunction();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("approaching high junction command ignored");
        }
    }

    private void approachingJunction() {
        // additional commands are already locked out by the call to setting the target
        // command was already logged, so only thing to do is change the state
        command = Command.APPROACHING_JUNCTION_POLE;
    }

    public void approachingSubstation() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            commandComplete = false;
            logCommand("approaching substation command");
            command = Command.APPROACHING_SUBSTATION;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("approaching substation command ignored");
        }
    }

    //********************************************************************************
    // Public commands for setting speed
    //********************************************************************************

    public void switchSpeed() {
        if (commandComplete) {
            commandComplete = false;
            logCommand("switch speed");
            command = Command.SWITCH_SPEED;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("switch speed command ignored");
        }
    }

    //********************************************************************************
    // Public commands for controlling robot
    //********************************************************************************

    public void dropCone() {
        if (commandComplete) {
            commandComplete = false;
            logCommand("drop cone");
            command = Command.DROP_CONE;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("drop cone command ignored");
        }
    }

    public void pickupCone() {
        if (commandComplete) {
            commandComplete = false;
            logCommand("pickup cone");
            command = Command.PICKUP_CONE;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("pickup cone command ignored");
        }
    }

    public void testTheDrop() {
        if (commandComplete) {
            commandComplete = false;
            command = Command.TEST_THE_DROP;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("test the drop command ignored");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        logState();
        switch (controllerState) {
            //********************************************************************************
            // INIT states - just shell for now. Doesn't really do anything.
            //********************************************************************************

            case PRE_INIT: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // do nothing, waiting to get the init command
            }
            break;

            case INIT_COMPLETE: {
                // do nothing.
                commandComplete = true;
                initComplete = true;
                controllerState = ControllerState.HIGH_SPEED;
            }
            break;

            //********************************************************************************
            // normal driving states
            //********************************************************************************

            case MAX_SPEED:
            case LOW_SPEED: {
                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HIGH_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        if(scoringTarget != ScoringTarget.GROUND_JUNCTION) {
                            robot.leftLift.droppingOnPole();
                            logCommand("test the drop");
                        } else {
                            logCommand("test the drop ignored since this is a ground junction");
                        }
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case DROP_CONE: {
                        // drivers are not using automatic speed control
                        robot.coneGrabberArmController.releaseThenMoveToPickup();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_SUBSTATION;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case PICKUP_CONE: {
                        // drivers are not using automatic speed control
                        robot.coneGrabber.closeThenCarryPosition();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_JUNCTION;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        setPower(LOW_POWER);
                        controllerState = ControllerState.CLOSE_TO_JUNCTION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        setPower(LOW_POWER);
                        setupForConeScore();
                        controllerState = ControllerState.FINISH_LINEUP_FOR_JUNCTION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        setPower(LOW_POWER);
                        // set which distance sensor to use
                        setDistanceSensorToUse();
                        controllerState = ControllerState.CLOSE_TO_SUBSTATION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                }
            }
            break;

            case HIGH_SPEED: {
                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        setPower(FULL_POWER);
                        controllerState = ControllerState.MAX_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        if(scoringTarget != ScoringTarget.GROUND_JUNCTION) {
                            robot.leftLift.droppingOnPole();
                            logCommand("test the drop");
                        } else {
                            logCommand("test the drop ignored since this is a ground junction");
                        }
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case DROP_CONE: {
                        // drivers are not using automatic speed control
                        robot.coneGrabberArmController.releaseThenMoveToPickup();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_SUBSTATION;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case PICKUP_CONE: {
                        // drivers are not using automatic speed control
                        robot.coneGrabber.closeThenCarryPosition();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_JUNCTION;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        setPower(LOW_POWER);
                        // set which distance sensor to use
                        setDistanceSensorToUse();
                        controllerState = ControllerState.CLOSE_TO_JUNCTION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        setPower(LOW_POWER);
                        setupForConeScore();
                        controllerState = ControllerState.FINISH_LINEUP_FOR_JUNCTION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        setPower(LOW_POWER);
                        // set which distance sensor to use
                        setDistanceSensorToUse();
                        controllerState = ControllerState.CLOSE_TO_SUBSTATION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                }
            }
            break;

            //********************************************************************************
            // scoring states
            //********************************************************************************

            case HEADED_FOR_JUNCTION: {
                // other than a switch speed command, just do nothing while waiting for the driver
                // to push the button setting a target, which will also set low speed and change
                // the state to close to junction
                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        setPower(LOW_POWER);
                        controllerState = ControllerState.LOW_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        // Cannot test the cone drop
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since not finish the linup");
                    }
                    break;
                    case DROP_CONE: {
                        // Cannot drop cone when headed to junction
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since headed to junction");
                    }
                    break;
                    case PICKUP_CONE: {
                        // the driver missed the cone and has to try again
                        robot.coneGrabber.closeThenCarryPosition();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_JUNCTION;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        setPower(LOW_POWER);
                        // set which distance sensor to use
                        setDistanceSensorToUse();
                        setupForConeScore();
                        controllerState = ControllerState.CLOSE_TO_JUNCTION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        setPower(LOW_POWER);
                        setupForConeScore();
                        controllerState = ControllerState.FINISH_LINEUP_FOR_JUNCTION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        // allow other commands to be active
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("approaching substation command ignored since robot is headed for junction");
                    }
                    break;
                }
            }
            break;

            case CLOSE_TO_JUNCTION: {
                // other than a switch speed command, just do nothing while waiting for the distance
                // sensor to see the junction, which will also stop the robot, send the lift, and
                // change the state to at junction
                if (distanceSensorToUse.isLessThanDistance(DISTANCE_LIMIT_FOR_JUNCTION, DistanceUnit.INCH)) {
                    // There is a junction in front of the sensor
                    // Maybe want to lockout any other commands at this point?
                    commandComplete = false;
                    // Stop the robot
                    setPower(0);
                    // Raise the lift and lower the arm. This will start at the same time the robot
                    // coasts to a stop.
                    //setupForConeScore();
                    // Set the timer that will make sure the robot stops before the driver can start it again
                    stopTimer.reset();
                    controllerState = ControllerState.AT_JUNCTION;
                }

                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HIGH_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        // Cannot test the cone drop
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since not finish the linup");
                    }
                    break;
                    case DROP_CONE: {
                        // Cannot drop cone when headed to junction
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since robot is close to junction");
                    }
                    break;
                    case PICKUP_CONE: {
                        // cannot pickup cone when headed to junction
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("pickup cone command ignored since robot is close to junction");
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        // this command could be set if the driver changed the target. Like maybe
                        // they initially set a high junction by mistake and then switched to a
                        // medium junction. Since the lift is not raised yet there is not really
                        // anything to do
                        setPower(LOW_POWER);
                        controllerState = ControllerState.CLOSE_TO_JUNCTION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        // this should never happen. If the target is a ground junction CLOSE_TO_JUNCTION state
                        // is skipped
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching ground junction ignored since robot is close to junction");
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching substation command ignored since robot is close to junction");
                    }
                    break;
                }
            }
            break;

            case AT_JUNCTION: {
                // wait for the timer to expired indicating the robot is stopped
                if(stopTimer.milliseconds() > 500) {
                    setPower(LOW_POWER);
                    controllerState = ControllerState.FINISH_LINEUP_FOR_JUNCTION;
                    // re-enable commands
                    commandComplete = true;
                }

                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        // Wait for the robot to stop before switching speeds
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("switch speed command ignored since robot is stopping");
                    }
                    break;
                    case TEST_THE_DROP: {
                        // Cannot test the cone drop
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since not finish the linup");
                    }
                    break;
                    case DROP_CONE: {
                        // Cannot drop cone yet
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since robot is at junction");
                    }
                    break;
                    case PICKUP_CONE: {
                        // cannot pickup cone when at junction
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("pickup cone command ignored since robot is at junction");
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        // cannot change target, the lift is already raising
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("approaching command ignored since robot is at junction");
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        // this should never happen. If the target is a ground junction AT_JUNCTION state
                        // is skipped
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching ground junction ignored since robot is close to junction");
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching substation command ignored since robot is at junction");
                    }
                    break;
                }
            }
            break;

            case FINISH_LINEUP_FOR_JUNCTION: {
                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        // dangerous since the lift is up
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HIGH_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        if(scoringTarget != ScoringTarget.GROUND_JUNCTION) {
                            robot.leftLift.droppingOnPole();
                            logCommand("test the drop");
                        } else {
                            logCommand("test the drop ignored since this is a ground junction");
                        }
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case DROP_CONE: {
                        robot.coneGrabberArmController.releaseThenMoveToPickup();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_SUBSTATION;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case PICKUP_CONE: {
                        // cannot pickup cone when headed to junction
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("pickup cone command ignored since robot is at junction");
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        // this command could be set if the driver changed the target. Like maybe
                        // they initially set a high junction by mistake and then switched to a
                        // medium junction.
                        // OR the driver was not lined up, tested the drop and saw it missed so he
                        // raises the lift again.
                        // Assuming this is what is going on, make it happen
                        // Raise the lift and lower the arm
                        setupForConeScore();
                        // The lift is up so only low power
                        setPower(LOW_POWER);
                        // they are at the junction so stay in the lineup, don't change state
                        // allow other commands to be active
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        // I have no idea why the driver would press the ground junction button again
                        // but just in case let's do it.
                        setPower(LOW_POWER);
                        setupForConeScore();
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching substation command ignored since robot is at junction");
                    }
                    break;
                }
            }
            break;

            //********************************************************************************
            // pickup states
            //********************************************************************************

            case HEADED_FOR_SUBSTATION: {
                // other than a switch speed command, just do nothing while waiting for the driver
                // to push the button that says the robot is close to the substation, which will
                // also set low speed and change the state to close to substation
                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        setPower(LOW_POWER);
                        controllerState = ControllerState.LOW_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        // Cannot test the cone drop
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since not finish the linup");
                    }
                    break;
                    case DROP_CONE: {
                        // Cannot drop cone when headed to substation
                        commandComplete = true;
                        logCommand("drop cone command ignored since headed to substation");
                    }
                    break;
                    case PICKUP_CONE: {
                        robot.coneGrabber.closeThenCarryPosition();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_JUNCTION;
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("pickup cone command even though robot is headed to substation");
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        // can't be approaching junction when headed to substation
                        // allow other commands to be active
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("approaching junction command ignored since robot is headed for substation");
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        // can't be approaching junction when headed to substation
                        // allow other commands to be active
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("approaching ground junction command ignored since robot is headed for substation");
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        setPower(LOW_POWER);
                        // set which distance sensor to use
                        setDistanceSensorToUse();
                        controllerState = ControllerState.CLOSE_TO_SUBSTATION;
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                    }
                    break;
                }
            }
            break;

            case CLOSE_TO_SUBSTATION: {
                // other than a switch speed command, just do nothing while waiting for the distance
                // sensor to see the cone, which will also stop the robot and
                // change the state to at substation
                if (distanceSensorToUse.isLessThanDistance(DISTANCE_LIMIT_FOR_CONE, DistanceUnit.INCH)) {
                    // There is a cone in front of the sensor
                    // Maybe want to lockout any other commands at this point?
                    commandComplete = false;
                    // Stop the robot
                    setPower(0);
                    // Right now, no other robot actions are taken. The drivers have probably already
                    // lowered the arm and opened the cone grabber.
                    // Set the timer that will make sure the robot stops before the driver can start it again
                    stopTimer.reset();
                    controllerState = ControllerState.AT_SUBSTATION;
                }

                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HIGH_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        // Cannot test the cone drop
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since not finish the linup");
                    }
                    break;
                    case DROP_CONE: {
                        // Cannot drop cone when you don't already have one
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since robot is close to substation");
                    }
                    break;
                    case PICKUP_CONE: {
                        // cannot pickup cone when getting close to substation
                        // But what if the distance sensor does not see the cone? That will lock
                        // the driver out of picking up a cone. So go ahead and do it.
                        robot.coneGrabber.closeThenCarryPosition();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_JUNCTION;
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("pickup cone command even though robot is close to substation");
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        // can't be approaching junction when headed to substation
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching junction command ignored since robot is close to substation");
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        // can't be approaching junction when headed to substation
                        // allow other commands to be active
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("approaching ground junction command ignored since robot is close to substation");
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching substation command ignored since robot is close to substation");
                    }
                    break;
                }
            }
            break;

            case AT_SUBSTATION: {
                // wait for the timer to expired indicating the robot is stopped
                if(stopTimer.milliseconds() > 500) {
                    setPower(LOW_POWER);
                    controllerState = ControllerState.FINISH_LINEUP_FOR_SUBSTATION;
                    // re-enable commands
                    commandComplete = true;
                }

                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        // Wait for the robot to stop before switching speeds
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("switch speed command ignored since robot is stopping");
                    }
                    break;
                    case TEST_THE_DROP: {
                        // Cannot test the cone drop
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since not finish the linup");
                    }
                    break;
                    case DROP_CONE: {
                        // Cannot drop cone when the robot does not have one
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since robot is at substation");
                    }
                    break;
                    case PICKUP_CONE: {
                        // let the robot pick up a cone even though it is coasting to a stop
                        robot.coneGrabber.closeThenCarryPosition();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_JUNCTION;
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("pickup cone command even though robot is still stopping");
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        // can't be approaching junction when headed to substation
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching command ignored since robot is at substation");
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        // can't be approaching junction when headed to substation
                        // allow other commands to be active
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("approaching ground junction command ignored since robot is at substation");
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching substation command ignored since robot is at substation");
                    }
                    break;
                }
            }
            break;

            case FINISH_LINEUP_FOR_SUBSTATION: {
                switch (command) {
                    case NONE: {
                        // do nothing
                    }
                    break;
                    case SWITCH_SPEED: {
                        // Ok if the driver really wants more speed ...
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HIGH_SPEED;
                        command = Command.NONE;
                        commandComplete = true;
                    }
                    break;
                    case TEST_THE_DROP: {
                        // Cannot test the cone drop
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since not finish the linup");
                    }
                    break;
                    case DROP_CONE: {
                        // Cannot drop cone when the robot does not have one
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("drop cone command ignored since robot is lining up at substation");
                    }
                    break;
                    case PICKUP_CONE: {
                        // pick up the cone
                        robot.coneGrabber.closeThenCarryPosition();
                        setPower(HIGH_POWER);
                        controllerState = ControllerState.HEADED_FOR_JUNCTION;
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("pickup cone command");
                    }
                    break;
                    case APPROACHING_JUNCTION_POLE: {
                        // can't be approaching junction when at substation
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching command ignored since robot is lining up at substation");
                    }
                    break;
                    case APPROACHING_GROUND_JUNCTION: {
                        // can't be approaching junction when headed to substation
                        // allow other commands to be active
                        command = Command.NONE;
                        commandComplete = true;
                        logCommand("approaching ground junction command ignored since robot is lining up at substation");
                    }
                    break;
                    case APPROACHING_SUBSTATION: {
                        command = Command.NONE;
                        // allow other commands to be active
                        commandComplete = true;
                        logCommand("approaching substation command ignored since robot is lining up at substation");
                    }
                    break;
                }
            }
            break;
        }
    }
}
