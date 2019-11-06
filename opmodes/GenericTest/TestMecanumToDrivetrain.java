package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20;
import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Mecanum to Drive Train", group = "Test")
//@Disabled
public class TestMecanumToDrivetrain extends LinearOpMode {

    // Put your variable declarations here
    //BNO055IMU imu;
    AdafruitIMU8863 imu;


    Mecanum mecanum;
    Mecanum.WheelVelocities wheelVelocities;
    HaloControls haloControls;
    MecanumCommands mecanumCommands;
    JoyStick gamepad1LeftJoyStickX;
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;
    DcMotor8863 frontLeft;
    DcMotor8863 backLeft;
    DcMotor8863 frontRight;
    DcMotor8863 backRight;
    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        mecanumCommands = new MecanumCommands();

        gamepad1LeftJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.X);
        gamepad1LeftJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.Y);

        gamepad1RightJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.X);
        gamepad1RightJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.Y);
        mecanum = new Mecanum();
        haloControls = new HaloControls(gamepad1LeftJoyStickY, gamepad1LeftJoyStickX, gamepad1RightJoyStickX, this);

        frontLeft = new DcMotor8863("FrontLeft", hardwareMap);
        backLeft = new DcMotor8863("BackLeft", hardwareMap);
        frontRight = new DcMotor8863("FrontRight", hardwareMap);
        backRight = new DcMotor8863("BackRight", hardwareMap);
        // these motors are orbital (planetary gear) motors. The type of motor sets up the number
        // of encoder ticks per revolution. Since we are not using encoder feedback yet, this is
        // really not important now. But it will be once we hook up the encoders and set a motor
        // mode that uses feedback.
        frontLeft.setMotorType(ANDYMARK_20_ORBITAL);
        backLeft.setMotorType(ANDYMARK_20_ORBITAL);
        frontRight.setMotorType(ANDYMARK_20_ORBITAL);
        backRight.setMotorType(ANDYMARK_20_ORBITAL);
        // This value will get set to some distance traveled per revolution later.
        frontLeft.setMovementPerRev(360);
        backLeft.setMovementPerRev(360);
        frontRight.setMovementPerRev(360);
        backRight.setMovementPerRev(360);

        // The encoder tolerance is used when you give the motor a target encoder tick count to rotate to. 5 is
        // probably too tight. 10 is pretty good based on experience. Note that 10 is set as the
        // default when you create a motor object so this statement is not needed.
        //frontLeft.setTargetEncoderTolerance(10);

        // FLOAT  is also the default when you create a new motor object
        //frontLeft.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);

        // powers are also defaulted to -1 and 1
        //frontLeft.setMinMotorPower(-1);
        //frontLeft.setMaxMotorPower(1);

        // setDirection() is a software control that controls which direction the motor moves when
        // you give it a positive power. We may have to change this once we see which direction the
        // motor actually moves.
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // set the running mode for the motor. The motor initializes at STOP_AND_RESET_ENCODER which
        // resets the encoder count to zero. After this you have to choose a mode that will allow
        // the motor to run.
        // In this case, we do not have the encoder connected from the motor. So we only have one
        // choice. We must run the motor without any feedback (open loop). This call is not really
        // needed since later I use runAtConstantPower() and that sets the mode too. But since you
        // are coming up to speed on the motors, I put this here for you to see (like my pun?).
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // The other 2 options would be:
        // RUN_TO_POSITION - run until the targeted encoder count is reached using PID
        // RUN_WITH_ENCODER - run at a velocity controlled by a PID
        // For more details, see this page and start reading at Running the motor and continue down
        // https://ftc-tricks.com/dc-motors/

        // Make sure the motor does not start moving. This is not really needed because
        // runAtConstantPower(0) below does the same thing. But I put it here so you can see this
        // call exists.
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);

        // The runAtConstantPower() and runAtConstantSpeed() methods setup the motor to do that.
        // They are initialzation methods. So they should not be inside the while loop.
        //
        // We can't use runAtConstantSpeed because there is no encoder feedback. I suspect this
        // is why the motor did not turn. runAtConstantSpeed uses the encoder and PID control
        // to turn the motor at a constant velocity.
        //frontLeft.runAtConstantSpeed(mecanum.getFrontLeft());
        //
        // Instead we will run the motor open loop (without controlling its speed, just feeding
        // it a power. Initialize the motor power to 0 for now.
        frontLeft.runAtConstantPower(0);
        backLeft.runAtConstantPower(0);
        frontRight.runAtConstantPower(0);
        backRight.runAtConstantPower(0);

        // Note from Glenn:
        // None of the following are needed using the class AdafruitIMU8863. They are handled in the
        // initialization of the imu as part of the constructor.
/*
        // State used for updating telemetry
        Orientation angles;
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        // parameters.
    parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
    parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
    //parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
    parameters.loggingEnabled      = true;
    parameters.loggingTag          = "IMU";
    parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

 */
        imu = new AdafruitIMU8863(hardwareMap);


        double adjustAngle = 0;
        boolean bPressed = false;
        boolean driverMode = true;
        //**************************************************************
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here

            // b button on the gamepad toggles between driver point of view mode (angles are based
            // on coordinate system relative to field) and robot point of view mode (angles are based
            // on coordinate system relative to the robot)
            if (!bPressed && gamepad1.b)
                driverMode = !driverMode;
            if (!gamepad1.b)
                bPressed = false;
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            haloControls.calculateMecanumCommands(mecanumCommands);
            // mecanum commands could come from joysticks or from autonomous calculations. That is why HaloControls is not part of Mecanum class
            //*****************************************************************
            // Is this any better than mecanum.getFrontLeft() etc?
            //*****************************************************************

            //angles  = imu.getAngularOrientation(AxesReference.EXTRINSIC , AxesOrder.ZXY , AngleUnit.RADIANS);

            //mecanumCommands.setAngleOfTranslation(  mecanumCommands.getAngleOfTranslation() + angles.firstAngle);
            // convert the imu heading, which is in degrees, to radians
            double heading = imu.getHeading() * Math.PI / 180.0;
            // could also use
            //double heading = Math.toRadians(imu.getHeading());

            // y button resets the coordinate system for the driver point of view to the same as the
            // the robot based coordinate system at the time the y button is pressed. After that
            // the coordinate system is based off the coordinate system in effect when the y button
            // was pressed.
            if (gamepad1.y)
                adjustAngle = heading;

            double adj = 0;

            if (driverMode)
                // get the difference in angle between the robot referenced coordinate system and the
                // driver / field referenced coordinate system
                adj = heading - adjustAngle;

            // translate between the two coordinate systems using the difference just calculated
            mecanumCommands.setAngleOfTranslation(mecanumCommands.getAngleOfTranslation() - adj);
            mecanum.calculateWheelVelocity(mecanumCommands);


            // update() is a call that runs the state machine for the motor. It is really only needed
            // when we are running the motor using feedback.
            //frontLeft.update();

            // set the power to the motor. This is the call to use when changing power after the
            // motor is set up for a mode.

            frontLeft.setPower(mecanum.getFrontLeft());
            backLeft.setPower(mecanum.getBackLeft());
            frontRight.setPower(mecanum.getFrontRight());
            backRight.setPower(mecanum.getBackRight());


            // This would also work. Is there a performance advantage to it?
            //frontLeft.setPower(wheelVelocities.getFrontLeft());

            telemetry.addData("Mecanum:", mecanumCommands.toString());
            telemetry.addData("front left = ", mecanum.getFrontLeft());
            telemetry.addData("front right = ", mecanum.getFrontRight());
            telemetry.addData("back left = ", mecanum.getBackLeft());
            telemetry.addData("back right = ", mecanum.getBackRight());
            telemetry.addData(">", "Press Stop to end test.");


            telemetry.addData("left Y ", gamepad1LeftJoyStickY.getValue());
            telemetry.addData("left X ", gamepad1LeftJoyStickX.getValue());
            telemetry.addData("righ Y ", gamepad1RightJoyStickY.getValue());
            telemetry.addData("right X ", gamepad1RightJoyStickX.getValue());


            //telemetry.addData("robot angles are " , angles.firstAngle);
            telemetry.addData("robot angles are ", heading);

            telemetry.update();

            idle();
        }
        frontLeft.stop();
        backLeft.stop();
        frontRight.stop();
        backRight.stop();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
