package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * This class wraps the normal FTC Servo class and gives you two new features:
 * - You can name a position and refer to the position name from then on. (like setPosition(position name)
 * - You can as whether the servo has completed a movememt to that position based on how long said
 * the movement is supposed to take to complete.
 */
public class Servo8863New {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The servo that this class wraps around
     */
    private Servo servo;

    /**
     * The direction of the servo
     */
    private Servo.Direction direction;

    /**
     * Data structure that holds a list of servo position names and the position associated with the
     * name
     */
    private HashMap<String, ServoPosition> positions;

    private ElapsedTime timer;

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

    public Servo8863New(String servoName, HardwareMap hardwareMap, Telemetry telemetry) {
        servo = hardwareMap.get(Servo.class, servoName);
        positions = new HashMap<>();
        timer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private ServoPosition getServoPosition(String positionName) {
        if (positions.containsKey(positionName)) {
            return positions.get(positionName);
        } else {
            throw new NullPointerException("tried to locate servo position but it has not been setup " + positionName);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Add a position associated with the servo. You can have any number of positions and you can
     * refer to them by their name. This is one functionality that enhances the normal servo class.
     *
     * @param positionName        - name associated with this position
     * @param position            - the value of the position (ranges from 0 to 1)
     * @param timeToReachPosition - a time that is needed for the servo to reach its position. This
     *                            is an estimate. If the servo experiences a higher than normal
     *                            load then it will take longer than this time. This can lead to the
     *                            servo movement being reported as complete when it is not.
     * @param timeUnits           - units for the time you are providing
     */
    public void addPosition(String positionName, double position, long timeToReachPosition, TimeUnit timeUnits) {
        ServoPosition servoPosition = new ServoPosition(position, timeToReachPosition, timeUnits);
        positions.put(positionName, servoPosition);
    }

    /**
     * I'm using this method to effectively replace the setPosition method of the servo class.
     *
     * @param positionName
     */
    public void setPosition(String positionName) {
        ServoPosition position = positions.get(positionName);
        servo.setPosition(position.getPosition());
        position.startMoveToPosition();
    }

    /**
     * Has the servo reached the position it was commanded to go to? Note this is an estimate based
     * on the time you told me the servo would take to reach the position. The servo has no position
     * feedback so we don't really know where it is at. So you tell me your guess on how long the
     * servo takes to reach the position when you setup the position. Then after you start a movement
     * I'll tell you if it has reached position based on how long it is since you told the servo
     * to move to the position. This is the other functionality that makes this servo unique from
     * the normal servo class.
     *
     * @param positionName
     * @return
     */
    public boolean isPositionReached(String positionName) {
        ServoPosition position = positions.get(positionName);
        return position.isPositionReached();
    }

    //*************************************************************************************************
    // Wrapper function for Servo
    // Since I can't extend servo, I need to wrap the rest of its methods so that they are still
    // available for the user.
    //*************************************************************************************************

    public ServoController getController() {
        return servo.getController();
    }

    public int getPortNumber() {
        return servo.getPortNumber();
    }

    public void setDirection(Servo.Direction direction) {
        servo.setDirection(direction);
    }

    public Servo.Direction getDirection() {
        return servo.getDirection();
    }

    public void scaleRange(double min, double max) {
        servo.scaleRange(min, max);
    }

    // I am not going to wrap the getPosition call. It is very misleading since it does not return
    // the position of the servo. A servo has no position feedback so all it does is return the
    // value of the last setPosition.
    // public double SetPosition()

}
