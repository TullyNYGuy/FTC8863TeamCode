package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class HaloControls {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Mode {
        DRIVER_MODE,
        ROBOT_MODE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private SmartJoystick yJoystick;
    private SmartJoystick xJoystick;
    private SmartJoystick speedOfRotationJoystick;
    private double adjustAngle = 0;
    private Mode mode = Mode.DRIVER_MODE;
    private double heading = 0;
    private FTCRobot robot;
    private int powerModifier = 0;

    private Telemetry telemetry;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private commands fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public HaloControls(SmartJoystick xJoystick, SmartJoystick yJoystick, SmartJoystick speedOfRotationJoystick, FTCRobot robot, Telemetry telemetry) {
        this.xJoystick = xJoystick;
        this.yJoystick = yJoystick;
        this.speedOfRotationJoystick = speedOfRotationJoystick;
        this.robot = robot;
        this.telemetry = telemetry;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public double getPowerModifier() {
        return 1 / (((double)powerModifier + 1));
    }



    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void calculateMecanumCommands(MecanumCommands commands) {
       // if (commands == null)
      //      return;
        heading = robot.getCurrentRotation(AngleUnit.RADIANS);

        double yValue = yJoystick.getValue();
        double xValue = xJoystick.getValue();
        double rValue = speedOfRotationJoystick.getValue();
        double translationSpeed = java.lang.Math.hypot(xValue, yValue);
        telemetry.addData("speed: ", translationSpeed);
        // Divide pi by 2 to shift axis. add pi to get correct range
        double angleOfTranslation = (java.lang.Math.atan2(yValue, xValue));
        if (angleOfTranslation > Math.PI / 2 && angleOfTranslation <= Math.PI) {
            angleOfTranslation = angleOfTranslation - (Math.PI / 2);
        } else {
            angleOfTranslation = angleOfTranslation + 3 * Math.PI / 2;
        }
        if (mode == Mode.DRIVER_MODE)
            angleOfTranslation -= heading - adjustAngle;

        if (translationSpeed > 1) {
            translationSpeed = 1;
        }
        double powerModifier = getPowerModifier();
        telemetry.addData("power modifier: ", powerModifier);
        translationSpeed *= powerModifier;
        rValue *= powerModifier;

        telemetry.addData("Halo: ", String.format("x: %.2f, y: %.2f, sp: %.2f", xValue, yValue, translationSpeed));
        commands.setAngleOfTranslation(AngleUnit.RADIANS, angleOfTranslation);
        commands.setSpeed(translationSpeed);
        commands.setSpeedOfRotation(rValue);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.DRIVER_MODE) {
            adjustAngle = heading - adjustAngle;
        }
    }

    public void toggleMode() {
        if (mode == Mode.DRIVER_MODE) {
            mode = Mode.ROBOT_MODE;
        } else {
            mode = Mode.DRIVER_MODE;
            // get the difference in angle between the robot referenced coordinate system and the
            // driver / field referenced coordinate system
            adjustAngle = heading - adjustAngle;
        }
    }

    public void resetHeading() {
        adjustAngle = heading;
    }

    public void togglePowerModifier() {
        powerModifier = 1 - powerModifier;
    }
}
