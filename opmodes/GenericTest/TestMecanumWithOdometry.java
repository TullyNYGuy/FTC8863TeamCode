package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;


import java.io.IOException;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Mecanum with Odometry", group = "ATest")
//@Disabled
public class TestMecanumWithOdometry extends LinearOpMode {

    // Put your variable declarations here
    private Configuration config = new Configuration();
    private boolean configLoaded = false;
    private final double INIT_ODOMETRY_TIMER_MSEC = 1000.0;

    private boolean loadConfiguration() {
        configLoaded = false;
        try {
            config.clear();
            config.load();
            configLoaded = true;
        } catch (IOException e) {

        }
        return configLoaded;
    }

    private boolean saveConfiguration() {
        try {
            config.store();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void initializeOdometry(OdometrySystem odometry, Mecanum mecanum, AdafruitIMU8863 imu) {
        if (configLoaded && odometry.loadConfiguration(config)) {
            telemetry.addData("init", "Loaded Odometry configuration");
            return;
        }
        MecanumCommands commands = new MecanumCommands();
        commands.setSpeed(0);
        commands.setAngleOfTranslation(AngleUnit.RADIANS, 0);
        commands.setSpeedOfRotation(1);
        ElapsedTime timer = new ElapsedTime();
        double originalAngle = imu.getHeading();
        odometry.startCalibration();
        timer.reset();
        mecanum.setMotorPower(commands);
        while (opModeIsActive() && (timer.milliseconds() < INIT_ODOMETRY_TIMER_MSEC)) {
            idle();
        }
        commands.setSpeedOfRotation(0);
        mecanum.setMotorPower(commands);
        odometry.finishCalibration(AngleUnit.DEGREES, AngleUnit.DEGREES.normalize(imu.getHeading() - originalAngle));
        odometry.saveConfiguration(config);
    }

    @Override
    public void runOpMode() {


        // Put your initializations here

        loadConfiguration();

        MecanumCommands mecanumCommands = new MecanumCommands();
        boolean intakeState = false;

        /*
        gamepad1LeftJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.X);
        gamepad1LeftJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.Y);

        gamepad1RightJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.X);
        gamepad1RightJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.Y);
  */
        DcMotor8863 frontLeft = new DcMotor8863("FrontLeft", hardwareMap);
        DcMotor8863 backLeft = new DcMotor8863("BackLeft", hardwareMap);
        DcMotor8863 frontRight = new DcMotor8863("FrontRight", hardwareMap);
        DcMotor8863 backRight = new DcMotor8863("BackRight", hardwareMap);

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
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

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

        AdafruitIMU8863 imu = new AdafruitIMU8863(hardwareMap);
        Mecanum mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight, telemetry);
        HaloControls haloControls = new HaloControls(gamepad1, imu);
        DistanceUnit units = DistanceUnit.CM;
        OdometryModule left = new OdometryModule(1440, 3.8, units, "BackRight", hardwareMap);
        OdometryModule right = new OdometryModule(1440, 3.8, units, "FrontRight", hardwareMap);
        OdometryModule back = new OdometryModule(1440, 3.8, units, "BackLeft", hardwareMap);
        OdometrySystem odometry = new OdometrySystem(units, left, right, back);
        odometry.initializeRobotGeometry(DistanceUnit.CM, 0, 1, DcMotorSimple.Direction.REVERSE, 0, 1, DcMotorSimple.Direction.FORWARD, 1, 0, DcMotorSimple.Direction.FORWARD);
        Position position = new Position(DistanceUnit.CM, 0.0, 0.0, 0.0, 0);


        waitForStart();
        initializeOdometry(odometry, mecanum, imu);
        saveConfiguration();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here


            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            haloControls.calculateMecanumCommands(mecanumCommands);
            // mecanum commands could come from joysticks or from autonomous calculations. That is why HaloControls is not part of Mecanum class
            //*****************************************************************
            // Is this any better than mecanum.getFrontLeft() etc?
            //*****************************************************************


            mecanum.setMotorPower(mecanumCommands);


            // This would also work. Is there a performance advantage to it?
            //frontLeft.setPower(wheelVelocities.getFrontLeft());

            //telemetry.addData("Mecanum:", mecanumCommands.toString());
            // telemetry.addData("front left = ", mecanum.getFrontLeft());
            // telemetry.addData("front right = ", mecanum.getFrontRight());
            // telemetry.addData("back left = ", mecanum.getBackLeft());
            // telemetry.addData("back right = ", mecanum.getBackRight());
            odometry.calculateMoveDistance();
            odometry.getCurrentPosition(position);
            double rotation = odometry.getCurrentRotation(AngleUnit.DEGREES);
            telemetry.addData("Mode: ", haloControls.getMode() == HaloControls.Mode.DRIVER_MODE ? "Driver" : "Robot");
            telemetry.addData("Odometry (l/r/b): ", String.format("%.2f %.2f %.2f", left.getDistanceSinceReset(units), right.getDistanceSinceReset(units), back.getDistanceSinceReset(units)));
            telemetry.addData("Position: ", String.format("(%.2f %.2f)%s", position.x, position.y, position.unit));
            telemetry.addData("Rotation: ", rotation);
//            telemetry.addData("Potition: ", String.format("%.2f %.2f %.2f", odometry.getCurrentX(), odometry.getCurrentY(), odometry.getCurrentRotation()));
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        mecanum.stopMotor();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
