package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ServoPosition;

import java.util.concurrent.TimeUnit;

@Config
public class CenterStageArmDeployServoRight {

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
    private Servo8863New armDeployServoRight;

    private final String ARM_SERVO_NAME = CenterStageRobot.HardwareName.RIGHT_DEPLOY_SERVO.hwName;

    private double readyPosition = 0.00;
    private double deployPosition = 0.00;

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

    public CenterStageArmDeployServoRight(HardwareMap hardwareMap, Telemetry telemetry) {
        armDeployServoRight = new Servo8863New(ARM_SERVO_NAME, hardwareMap, telemetry);
        
        armDeployServoRight.addPosition("readyPosition", readyPosition, 1000, TimeUnit.MILLISECONDS);
        armDeployServoRight.addPosition("deployPosition", deployPosition, 1000, TimeUnit.MILLISECONDS);

        armDeployServoRight.setDirection(Servo.Direction.FORWARD);
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

    public void readyPositon() {
        armDeployServoRight.setPosition("readyPosition");
    }

    public void deployPositon() {
        armDeployServoRight.setPosition("deployPosition");
    }
    
    public void bumpUpBig (){
        armDeployServoRight.bump(0.1);
    }

    public void bumpDownBig () {
        armDeployServoRight.bump(-0.1);
    }

    public void bumpUpSmall () {
        armDeployServoRight.bump(0.01);
    }

    public void bumpDownSmall () {
        armDeployServoRight.bump(-0.01);
    }

    // wrappers

    public double getCurrentPosition(){
        return armDeployServoRight.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return armDeployServoRight.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        armDeployServoRight.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        armDeployServoRight.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        armDeployServoRight.setupServoPositionsUsingGamepad(opmode);
    }
}
