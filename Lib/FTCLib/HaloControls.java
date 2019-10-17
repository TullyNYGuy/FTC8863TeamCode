package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class HaloControls {

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
    private JoyStick yJoystick;
    private JoyStick xJoystick;
    private JoyStick speedOfRotationJoystick;
    private OpMode opmode;
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

    public HaloControls(JoyStick yJoystick, JoyStick xJoystick, JoyStick speedOfRotationJoystick, OpMode opmode) {
        this.yJoystick = yJoystick;
        this.xJoystick = xJoystick;
        this.speedOfRotationJoystick = speedOfRotationJoystick;
        this.opmode = opmode;
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
    public void getMechanumdata(MecanumData data) {
        if (data == null)
            return;
        double yValue = yJoystick.getValue();
        double xValue = xJoystick.getValue();
        double rValue = speedOfRotationJoystick.getValue();
        double translationSpeed = java.lang.Math.hypot(xValue, yValue);
        // Divide pi by 2 to shift axis. add pi to get correct range
        double angleOfTranslation = (java.lang.Math.atan2(yValue, xValue));
        if (angleOfTranslation > Math.PI / 2 && angleOfTranslation <= Math.PI) {
            angleOfTranslation = angleOfTranslation - (Math.PI / 2);
        } else {
            angleOfTranslation = angleOfTranslation + 3 * Math.PI / 2;
        }
        if (translationSpeed > 1) {
            translationSpeed = 1;
        }
        //return new MecanumData(translationSpeed, angleOfTranslation, rValue);
        data.setAngleOfTranslation(angleOfTranslation);
        data.setSpeed(translationSpeed);
        data.setSpeedOfRotation(rValue);
    }
}
