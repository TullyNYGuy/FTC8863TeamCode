package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class AngleChanger {

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

    // internal units are radians
    private AngleUnit angleUnit = AngleUnit.RADIANS;
    private DistanceUnit distanceUnit = DistanceUnit.METER;

    private DcMotor8863 motor;

    // The current angle is stored in PersistantStorage.shooterAngle
    private final double MAX_ANGLE = angleUnit.fromDegrees(40);
    private final double MIN_ANGLE = angleUnit.fromDegrees(0);

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getCurrentAngle(AngleUnit desiredUnits) {
        return desiredUnits.fromUnit(PersistantStorage.angleUnit, PersistantStorage.shooterAngle);
    }

    public void setInitialAngle(AngleUnit units, double initialAngle) {
        initialAngle = angleUnit.fromUnit(units, initialAngle);
        if (initialAngle > MAX_ANGLE) {
            initialAngle = MAX_ANGLE;
        }
        if (initialAngle < MIN_ANGLE) {
            initialAngle = MIN_ANGLE;
        }
        PersistantStorage.shooterAngle = PersistantStorage.angleUnit.fromUnit(angleUnit, initialAngle);
    }

    public void setCurrentAngle(AngleUnit units, double desiredAngle) {
        desiredAngle = angleUnit.fromUnit(units, desiredAngle);
        if (desiredAngle > MAX_ANGLE) {
            desiredAngle = MAX_ANGLE;
        }
        if (desiredAngle < MIN_ANGLE) {
            desiredAngle = MIN_ANGLE;
        }
        PersistantStorage.shooterAngle = PersistantStorage.angleUnit.fromUnit(angleUnit, desiredAngle);
        motor.moveToPosition(1, calculateLeadScrewPosition(angleUnit, desiredAngle), DcMotor8863.FinishBehavior.HOLD);
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public AngleChanger(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = new DcMotor8863(UltimateGoalRobotRoadRunner.HardwareName.LEAD_SCREW_MOTOR.hwName, hardwareMap, telemetry);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        motor.setMovementPerRev(8);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        if (PersistantStorage.shooterAngle == null) {
            PersistantStorage.shooterAngle = new Double(0);
        }
        if (PersistantStorage.angleChangerMotorEncoderCount == null) {
            PersistantStorage.angleChangerMotorEncoderCount = new Integer(0);
        } else {
            motor.setBaseEncoderCount(PersistantStorage.angleChangerMotorEncoderCount);
        }
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private double calculateLeadScrewPosition(AngleUnit units, double desiredAngle) {
        desiredAngle = angleUnit.fromUnit(units, desiredAngle);
        //constants
        double initialLength = toMM(1.345);
        double initialAngle = angleUnit.fromDegrees(9.961);
        //Side A is the bottom side side B is the shooter
        double sideA = toMM(6.593);
        double sideB = toMM(7.207);
        if (desiredAngle > 0) {
            double leadScrewPosition = Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2) - 2 * sideA * sideB * Math.cos(desiredAngle + initialAngle)) - initialLength;
            return leadScrewPosition;
        } else {
            desiredAngle = Math.abs(desiredAngle);
            double leadScrewPosition = -Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2) - 2 * sideA * sideB * Math.cos(desiredAngle + initialAngle)) - initialLength;
            return leadScrewPosition;
        }
    }

    private double toMM(double inches) {
        return inches * 25.4;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void setAngleNegative(AngleUnit units, double desiredAngle) {
        desiredAngle = angleUnit.fromUnit(units, desiredAngle);
        if (desiredAngle > MAX_ANGLE) {
            desiredAngle = MAX_ANGLE;
        }
        PersistantStorage.shooterAngle = desiredAngle;
        motor.moveToPosition(0.3, calculateLeadScrewPosition(angleUnit, desiredAngle), DcMotor8863.FinishBehavior.HOLD);
    }

    public static void clearAngleChanger() {
        PersistantStorage.shooterAngle = null;
        PersistantStorage.angleChangerMotorEncoderCount = null;
    }

    public void setAngleReference() {
        PersistantStorage.shooterAngle = 0.0;
    }

    public int getMotorEncoderCount() {
        return motor.getCurrentPosition();
    }

    public void update() {
        motor.update();
    }

    public boolean isAngleAdjustComplete() {
        if (motor.isMovementComplete()) {
            // note that since the shooter angle is already stored in PersistantStorage by setCurrentAngle, it is already saved for later use
            PersistantStorage.angleChangerMotorEncoderCount = motor.getCurrentPosition();
            return true;
        } else {
            return false;
        }
    }

    public boolean init(Configuration config) {
        return true;
    }
}
