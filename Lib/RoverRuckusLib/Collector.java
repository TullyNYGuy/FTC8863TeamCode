package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import android.graphics.Color;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class Collector {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum MineralColor {
        YELLOW,
        WHITE,
        NONE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private CRServo collectionServoLeft;
    private CRServo collectionServoRight;

    private Servo8863 gateServo;
    private double collectionPositionGateServo = 0.52;
    private double keepPositionGateServo = 1;
    private double ejectPositionGateServo = 0;
    private double initPositionGateServo = 0.52;

    private ColorSensor sensorColor;
    private DistanceSensor sensorDistance;

    private MineralColor mineralColor = MineralColor.NONE;

    private double red = 0;
    private double blue = 0;
    private double green = 0;
    private double argb = 0;
    private double distance = 0;

    private float hsvValues[] = {0F, 0F, 0F};

    // values is a reference to the hsvValues array.
    private final float values[] = hsvValues;

    // sometimes it helps to multiply the raw RGB values with a scale factor
// to amplify/attentuate the measured values.
    private final double SCALE_FACTOR = 255;
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
    public Collector(HardwareMap hardwareMap, Telemetry telemetry) {
        collectionServoLeft = hardwareMap.get(CRServo.class, "collectionServoLeft");
        collectionServoRight = hardwareMap.get(CRServo.class, "collectionServoRight");
        collectionServoRight.setDirection(CRServo.Direction.REVERSE);
        gateServo = new Servo8863("gateServo", hardwareMap, telemetry, collectionPositionGateServo, keepPositionGateServo, ejectPositionGateServo, initPositionGateServo, Servo.Direction.FORWARD);
        sensorColor = hardwareMap.get(ColorSensor.class, "revColorSensor");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "revColorSensor");
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private MineralColor getMineralColor() {
        // convert the RGB values to HSV values.
        // multiply by the SCALE_FACTOR.
        // then cast it back to int (SCALE_FACTOR is a double)
        Color.RGBToHSV((int) (red * SCALE_FACTOR), (int) (green * SCALE_FACTOR), (int) (blue * SCALE_FACTOR), hsvValues);

    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void turnIntakeOff() {
        collectionServoLeft.setPower(0);
        collectionServoRight.setPower(0);
    }

    public void turnIntakeOnSuckIn() {
        collectionServoLeft.setPower(1);
        collectionServoRight.setPower(1);
    }

    public void turnIntakeOnSpitOut() {
        collectionServoLeft.setPower(-1);
        collectionServoRight.setPower(-1);
    }

    private void goToCollectionPosition() {
        gateServo.goHome();
    }

    private void goToKeepPosition() {
        gateServo.goUp();
    }

    private void goToEjectPosition() {
        gateServo.goDown();
    }

    private void goToInitPosition() {
        gateServo.goInitPosition();
    }

    public void initialize() {
        turnIntakeOff();
        goToInitPosition();
    }
}
