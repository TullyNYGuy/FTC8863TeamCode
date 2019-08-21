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
private JoyStick yjoy;
private JoyStick xjoy;
private JoyStick speedOfRotationjoy;
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

    public HaloControls(JoyStick yjoy, JoyStick xjoy, JoyStick speedOfRotationjoy, OpMode opmode) {
        this.yjoy = yjoy;
        this.xjoy = xjoy;
        this.speedOfRotationjoy = speedOfRotationjoy;
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
    public MecanumData getMechanumdata() {
       double yValue = yjoy.scaleInput(opmode.gamepad1.right_stick_y);
       double xValue = xjoy.scaleInput(opmode.gamepad1.right_stick_x);
       double rValue = speedOfRotationjoy.scaleInput(opmode.gamepad1.left_stick_x);
      double translationSpeed =  java.lang.Math.hypot(xValue, yValue);
       double angleOfTranslation = java.lang.Math.atan2(yValue, xValue);
       if (translationSpeed > 1){
           translationSpeed = 1;
       }
       return new MecanumData(translationSpeed, angleOfTranslation, rValue);
    }
}
