package org.firstinspires.ftc.teamcode.Lib.VelocityVortex;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

public class FrontBeaconPusherControlGB {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum FrontBeaconControlState {
        MOVING_TO_BOTH_BACK,
        AT_BOTH_BACK,
        MOVING_TO_BOTH_MIDDLE,
        AT_BOTH_MIDDLE,
        DRIVE_TO_BEACON,
        LOOKING_FOR_BEACON,
        MOVING_TO_BOTH_FORWARD,
        AT_BOTH_FORWARD,
        MOVING_TO_LEFT_BACK_RIGHT_FORWARD,
        AT_LEFT_BACK_RIGHT_FORWARD,
        MOVING_TO_LEFT_FORWARD_RIGHT_BACK,
        AT_LEFT_FORWARD_RIGHT_BACK,
        CHECK_BEACON_PUSHED,
        DRIVE_BACK_TO_START,
        IDLE
    }

    public enum AllianceColor {
        RED,
        BLUE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private FrontBeaconPusher frontBeaconPusher;
    private DriveTrain driveTrain;

    private FrontBeaconControlState frontBeaconControlState;

    private AllianceColor allianceColor = AllianceColor.RED;

    private FrontBeaconPusher.BeaconPusherState frontBeaconPusherState;

    private FrontBeaconPusher.BeaconColor beaconColor;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public FrontBeaconPusherControlGB(HardwareMap hardwareMap, Telemetry telemetry,
                                      MuxPlusColorSensors muxPlusColorSensors,
                                      DriveTrain driveTrain,
                                      AllianceColor allianceColor) {
        frontBeaconPusher = new FrontBeaconPusher(hardwareMap, telemetry, muxPlusColorSensors);
        this.allianceColor = allianceColor;
        this.driveTrain = driveTrain;
        frontBeaconControlState = FrontBeaconControlState.IDLE;
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

    public void initialize() {
        frontBeaconControlState = frontBeaconControlState.MOVING_TO_BOTH_MIDDLE;
    }

    public void startFrontBeaconPusherControl() {
        frontBeaconControlState = FrontBeaconControlState.DRIVE_TO_BEACON;
    }

    public FrontBeaconControlState update() {
        double distance = 10; // in cm
        double power = .3;
        double heading = 0;

        frontBeaconPusherState = frontBeaconPusher.updateState();
        beaconColor = frontBeaconPusher.getBeaconColor();

        switch (frontBeaconControlState) {
            case IDLE:
                break;
            case MOVING_TO_BOTH_BACK:
                break;
            case AT_BOTH_BACK:
                break;
            case MOVING_TO_BOTH_MIDDLE:
                // check to see if the pushers are at the  position
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.BOTH_MIDDLE) {
                    // if the pushers are at the  position then change the state
                    frontBeaconControlState = FrontBeaconControlState.AT_BOTH_MIDDLE;
                } else {
                    // if the pushers are not at the position and not moving to the position start them moving
                    if (frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.MOVING_TO_BOTH_MIDDLE) {
                        frontBeaconPusher.moveBothMidway();
                    }
                }
                // if the pushers are moving to the position but not there yet do nothing
                break;
            case AT_BOTH_MIDDLE:
                // sit and wait for a command to get us out of this state
                break;
            case DRIVE_TO_BEACON:
                // start a drive towards the beacon for a known distance
            driveTrain.setupDriveDistanceUsingIMU(heading, power, distance, AdafruitIMU8863.AngleMode.RELATIVE,
                    0, .3, 1000);
                frontBeaconControlState = FrontBeaconControlState.LOOKING_FOR_BEACON;
                break;
            case LOOKING_FOR_BEACON:
                // check to see if the drive towards the beacon has finished. If it has finished
                // and we are still in this state we never found the beacon.
                if (driveTrain.updateDriveDistanceUsingIMU()) {
                    // we finished the drive and did not find the beacon, back up to where we started
                    frontBeaconControlState = FrontBeaconControlState.DRIVE_BACK_TO_START;
                } else {
                    // the drive distance has not finished
                    // look for beacon color to determine where to move the pushers
                    if (allianceColor == AllianceColor.BLUE && beaconColor == FrontBeaconPusher.BeaconColor.RED_BLUE
                            || allianceColor == AllianceColor.RED && beaconColor == FrontBeaconPusher.BeaconColor.BLUE_RED) {
                        frontBeaconControlState = FrontBeaconControlState.MOVING_TO_LEFT_BACK_RIGHT_FORWARD;
                    }
                    if (allianceColor == AllianceColor.BLUE && beaconColor == FrontBeaconPusher.BeaconColor.BLUE_RED
                            || allianceColor == AllianceColor.RED && beaconColor == FrontBeaconPusher.BeaconColor.RED_BLUE) {
                        frontBeaconControlState = FrontBeaconControlState.MOVING_TO_LEFT_FORWARD_RIGHT_BACK;
                    }
                }
                break;
            case MOVING_TO_BOTH_FORWARD:
                break;
            case AT_BOTH_FORWARD:
                break;
            case MOVING_TO_LEFT_BACK_RIGHT_FORWARD:
                // check to see if the drive distance towards the beacon has finished
                if (driveTrain.updateDriveDistanceUsingIMU()) {
                    // we finished the drive, check to see if the beacon changed
                    frontBeaconControlState = FrontBeaconControlState.CHECK_BEACON_PUSHED;
                }
                // check to see if the pushers are at the  position
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.LEFT_BACK_RIGHT_FORWARD) {
                    // if the pushers are at the  position then change the state
                    frontBeaconControlState = FrontBeaconControlState.AT_LEFT_BACK_RIGHT_FORWARD;
                } else {
                    // if the pushers are not at the position and not moving to the position start them moving
                    if (frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.MOVING_TO_LEFT_BACK_RIGHT_FORWARD) {
                        frontBeaconPusher.moveLeftPusherBackRightPusherForward();
                    }
                }
                // if the pushers are moving to the position but not there yet do nothing
                break;
            case AT_LEFT_BACK_RIGHT_FORWARD:
                // check to see if the drive distance towards the beacon has finished
                if (driveTrain.updateDriveDistanceUsingIMU()) {
                    // we finished the drive, check to see if the beacon changed
                    frontBeaconControlState = FrontBeaconControlState.CHECK_BEACON_PUSHED;
                }
                // if the drive has not finished then just sit and wait for it to finish
                break;
            case MOVING_TO_LEFT_FORWARD_RIGHT_BACK:
                // check to see if the drive distance towards the beacon has finished
                if (driveTrain.updateDriveDistanceUsingIMU()) {
                    // we finished the drive, check to see if the beacon changed
                    frontBeaconControlState = FrontBeaconControlState.CHECK_BEACON_PUSHED;
                }
                // check to see if the pushers are at the  position
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.LEFT_FORWARD_RIGHT_BACK) {
                    // if the pushers are at the  position then change the state
                    frontBeaconControlState = FrontBeaconControlState.AT_LEFT_FORWARD_RIGHT_BACK;
                } else {
                    // if the pushers are not at the position and not moving to the position start them moving
                    if (frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.MOVING_TO_LEFT_FORWARD_RIGHT_BACK) {
                        frontBeaconPusher.moveLeftPusherForwardRightPusherBack();
                    }
                }
                break;
            case AT_LEFT_FORWARD_RIGHT_BACK:
                // check to see if the drive distance towards the beacon has finished
                if (driveTrain.updateDriveDistanceUsingIMU()) {
                    // we finished the drive, check to see if the beacon changed
                    frontBeaconControlState = FrontBeaconControlState.CHECK_BEACON_PUSHED;
                }
                // if the drive has not finished then just sit and wait for it to finish
                break;
            case CHECK_BEACON_PUSHED:
                // there are four possible situations for alliance color and beacon color. With one
                // color sensor on the right side we can check to see if the beacon changed color
                // in 2 out of the 4 states
                // alliance color | beacon red / blue | beacon blue / red
                // ---------------|-------------------|-------------------
                // red            | can check         | cannot check
                // blue           | cannot check      | can check
                if (allianceColor == AllianceColor.RED && beaconColor == FrontBeaconPusher.BeaconColor.RED_BLUE
                        || allianceColor == AllianceColor.BLUE && beaconColor == FrontBeaconPusher.BeaconColor.BLUE_RED) {
                    if (beaconColor == frontBeaconPusher.getBeaconColor()) {
                        // The beacon did not change. Do something.
                        // Maybe back up and give it one more try?
                    }
                } else {
                    // cannot check that the beacon changed
                }
                // start to move the pushers back to the middle
                frontBeaconPusher.moveBothMidway();
                // start a move distance backwards
                driveTrain.setupDriveDistanceUsingIMU(heading, power, -distance, AdafruitIMU8863.AngleMode.RELATIVE,
                        0, power, 1000);
                // move to next state
                frontBeaconControlState = FrontBeaconControlState.DRIVE_BACK_TO_START;
                break;
            case DRIVE_BACK_TO_START:
                // check to see if the drive distance back to the starting point has finished
                if (driveTrain.updateDriveDistanceUsingIMU()) {
                    // if it has then we are done so change state back to MOVING_TO_BOTH_MIDDLE
                    frontBeaconControlState = FrontBeaconControlState.MOVING_TO_BOTH_MIDDLE;
                }
                // if not then sit and wait for it
                break;
        }
        return frontBeaconControlState;
    }

}
