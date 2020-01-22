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
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        frontLeft.runAtConstantPower(0);
        backLeft.runAtConstantPower(0);
        frontRight.runAtConstantPower(0);
        backRight.runAtConstantPower(0);

        OdometryModule left = new OdometryModule(1440, 3.8, DistanceUnit.CM, "BackRight", hardwareMap);
        OdometryModule right = new OdometryModule(1440, 3.8, DistanceUnit.CM, "FrontRight", hardwareMap);
        OdometryModule back = new OdometryModule(1440, 3.8, DistanceUnit.CM, "BackLeft", hardwareMap);
        OdometrySystem trial = new OdometrySystem(DistanceUnit.CM, left, right, back);
        trial.initializeRobotGeometry(DistanceUnit.CM, 0, 1, DcMotorSimple.Direction.REVERSE,0, 1, DcMotorSimple.Direction.FORWARD, 1,0, DcMotorSimple.Direction.FORWARD);
        AdafruitIMU8863 imu = new AdafruitIMU8863(hardwareMap);
        ElapsedTime rotationTime = new ElapsedTime();

        trial.startCalibration();
        trial.calculateMoveDistance();
        trial.updateCoordinates();
        MecanumCommands shower = new MecanumCommands();
        Mecanum mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight);
        double oldHeading = imu.getHeading();
        shower.setAngleOfTranslation(AngleUnit.RADIANS, 0);
        shower.setSpeed(0);
        shower.setSpeedOfRotation(0);

        trial.startCalibration();
        rotationTime.startTime();

        while(rotationTime.milliseconds() < 200){
            shower.setSpeedOfRotation(0.3);
            mecanum.setMotorPower(shower);
        }

        double newHeading = imu.getHeading();
        double heading = newHeading - oldHeading;
        trial.finishCalibration(AngleUnit.DEGREES, heading);

        telemetry.addData("robot moved: ", shower);
        // create the robot. Tell the driver we are creating it since this can take a few seconds
        // and we want the driver to know what is going on.
        // telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();


        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        // The user pressed play so we start the robot and then check to make sure he or she has
        // not pressed stop. If they press stop, then opModeIsActive() will return false. It can
        // also return false if there is some kind of error in the robot software or hardware.

        while (opModeIsActive()) {

            //*************************************************************************************
            // Gamepad 1 buttons - look for a button press on gamepad 1 and then do the action
            // for that button
            //*************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }


            // update the drive motors with the new power




            idle();
        }

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

