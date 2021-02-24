package org.firstinspires.ftc.teamcode.opmodes.UltimateGoal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RampControl;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/*
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Calibrate Odometry", group = "ATest")
//@Disabled
public class CalibrateOdometry extends LinearOpMode {

    class TestRobot implements FTCRobot {

        AdafruitIMU8863 imu;

        public TestRobot(AdafruitIMU8863 imu) {
            this.imu = imu;
        }

        @Override
        public boolean createRobot() {
            return false;
        }

        @Override
        public void init() {

        }

        @Override
        public boolean isInitComplete() {
            return false;
        }

        @Override
        public void update() {

        }

        @Override
        public void shutdown() {

        }

        @Override
        public void timedUpdate(double timerValueMsec) {

        }

        @Override
        public double getCurrentRotation(AngleUnit unit) {
            return unit.fromDegrees(imu.getHeading());
        }
    }


    // Put your variable declarations here
    private Configuration config = new Configuration();
    private boolean configLoaded = false;
    private final double INIT_ODOMETRY_TIMER_MSEC = 1000.0;

    private boolean loadConfiguration() {
        configLoaded = false;
        config.clear();
        configLoaded = config.load();
        return configLoaded;
    }

    private boolean saveConfiguration() {
        return config.store();
    }

    private void initializeOdometry(OdometrySystem odometry, Mecanum mecanum, AdafruitIMU8863 imu) {
        if (configLoaded && odometry.loadConfiguration(config)) {
            telemetry.addData("init", "Loaded Odometry configuration");
            return;
        }
        MecanumCommands commands = new MecanumCommands();
        commands.setSpeed(0);
        commands.setAngleOfTranslation(AngleUnit.RADIANS, 0);
        commands.setSpeedOfRotation(.3);
        ElapsedTime timer = new ElapsedTime();
        double originalAngle = imu.getHeading();
        odometry.startCalibration();
        timer.reset();
        mecanum.setMotorPower(commands);
        while (opModeIsActive() && (timer.milliseconds() < INIT_ODOMETRY_TIMER_MSEC)) {
            idle();
        }
        RampControl rampControl = new RampControl(.3, 0, 1000);
        rampControl.enable();
        rampControl.start();
        while(rampControl.isRunning() && opModeIsActive()){
            commands.setSpeedOfRotation(rampControl.getRampValueLinear(0));

            mecanum.setMotorPower(commands);
            idle();
        }
        commands.setSpeedOfRotation(0);
        mecanum.setMotorPower(commands);
        sleep(1500);
        odometry.finishCalibration(AngleUnit.DEGREES, AngleUnit.DEGREES.normalize(-(imu.getHeading() - originalAngle)));
        odometry.saveConfiguration(config);
        odometry.reset();
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
        DcMotor8863 frontLeft = DcMotor8863.createMotorFromFile(config, "FLMotor", hardwareMap);
        DcMotor8863 backLeft = DcMotor8863.createMotorFromFile(config, "BLMotor", hardwareMap);
        DcMotor8863 frontRight = DcMotor8863.createMotorFromFile(config, "FRMotor", hardwareMap);
        DcMotor8863 backRight = DcMotor8863.createMotorFromFile(config, "BRMotor", hardwareMap);

        AdafruitIMU8863 imu = new AdafruitIMU8863(hardwareMap);
        Mecanum mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight, telemetry);
        TestRobot robot = new TestRobot(imu);
        // Game Pad 1 joysticks
        SmartJoystick gamepad1LeftJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        SmartJoystick gamepad1LeftJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);
        SmartJoystick gamepad1RightJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        SmartJoystick gamepad1RightJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);
        // Mecanum Controls
        HaloControls haloControls = new HaloControls(gamepad1LeftJoyStickX, gamepad1LeftJoyStickY, gamepad1RightJoyStickX, null, telemetry);

        DistanceUnit units = DistanceUnit.CM;
        OdometryModule left = new OdometryModule(1440, 3.8 * Math.PI, units, "FrontLeft", hardwareMap);
        OdometryModule right = new OdometryModule(1440, 3.8 * Math.PI, units, "BackLeft", hardwareMap);
        OdometryModule back = new OdometryModule(1440, 3.8 * Math.PI, units, "BackRight", hardwareMap);
        OdometrySystem odometry = new OdometrySystem(units, left, right, back);
        odometry.initializeRobotGeometry(DistanceUnit.CM, 0, 1, DcMotorSimple.Direction.REVERSE, 0, 1, DcMotorSimple.Direction.FORWARD, 1, 0, DcMotorSimple.Direction.FORWARD);
        Position position = new Position(DistanceUnit.CM, 0.0, 0.0, 0.0, 0);


        waitForStart();
        initializeOdometry(odometry, mecanum, imu);
        saveConfiguration();

    }
}
