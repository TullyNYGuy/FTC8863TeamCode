package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.internal.tfod.Timer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "test robot calibration", group = "Test")
//@Disabled
public class TestRobotCalibration extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations - THESE ARE JUST SETTING UP THE VARIABLES (RESERVING MEMORY FOR THEM)
    //*********************************************************************************************

    // Here are declarations for all of the hardware

    // Here is the declaration for the hardware map - it contains information about the configuration
    // of the robot and how to talk to each piece of hardware
    //public HardwareMap hardwareMap;

    // GAMEPAD 1 - declare all of the objects on game pad 1

    // declare the buttons on the gamepad as multi push button objects



    // GAMEPAD 2 - declare all of the objects on game pad 2



    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************
        DcMotor8863 frontLeft = new DcMotor8863("FrontLeft", hardwareMap);
        DcMotor8863 backLeft = new DcMotor8863("BackLeft", hardwareMap);
        DcMotor8863 frontRight = new DcMotor8863("FrontRight", hardwareMap);
        DcMotor8863 backRight = new DcMotor8863("BackRight", hardwareMap);
        frontLeft.setMotorType(ANDYMARK_20_ORBITAL);
        backLeft.setMotorType(ANDYMARK_20_ORBITAL);
        frontRight.setMotorType(ANDYMARK_20_ORBITAL);
        backRight.setMotorType(ANDYMARK_20_ORBITAL);
        frontLeft.setMovementPerRev(360);
        backLeft.setMovementPerRev(360);
        frontRight.setMovementPerRev(360);
        backRight.setMovementPerRev(360);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.runAtConstantPower(0);
        backLeft.runAtConstantPower(0);
        frontRight.runAtConstantPower(0);
        backRight.runAtConstantPower(0);
        ElapsedTime rotationTime = new ElapsedTime();

        OdometryModule left = new OdometryModule(1440, 3.8, DistanceUnit.CM, "BackLeft", hardwareMap);
        OdometryModule right = new OdometryModule(1440, 3.8, DistanceUnit.CM, "BackRight", hardwareMap);
        OdometryModule back = new OdometryModule(1440, 3.8, DistanceUnit.CM, "FrontRight", hardwareMap);
        OdometrySystem trial = new OdometrySystem(DistanceUnit.CM, left, right, back);
        trial.initializeRobotGeometry(DistanceUnit.CM, 0, 1, DcMotorSimple.Direction.REVERSE,0, 1, DcMotorSimple.Direction.FORWARD, 1,0, DcMotorSimple.Direction.FORWARD);
        AdafruitIMU8863 imu = new AdafruitIMU8863(hardwareMap);

        MecanumCommands shower = new MecanumCommands();
        Mecanum mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight, telemetry);
        double oldHeading = imu.getHeading();

        waitForStart();

        rotationTime.reset();

        shower.setAngleOfTranslation(AngleUnit.RADIANS, 0);
        shower.setSpeed(0);
        shower.setSpeedOfRotation(0);
        trial.startCalibration();
        rotationTime.reset();

        shower.setSpeedOfRotation(0.3);
        mecanum.setMotorPower(shower);
        telemetry.addData("Mecanum: ", shower);
        telemetry.update();

        // turn until the robot has rotated 90 degrees
        while (opModeIsActive() && (imu.getHeading() - oldHeading < 90)) {
            idle();
        }

        mecanum.stopMotor();
        double newHeading = imu.getHeading();
        double heading = newHeading - oldHeading;
        trial.finishCalibration(AngleUnit.DEGREES, -heading);

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************

}

