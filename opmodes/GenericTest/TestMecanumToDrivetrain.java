package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;
import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_40;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Mecanum To Drivetrain", group = "AATest")
//@Disabled
public class TestMecanumToDrivetrain extends LinearOpMode {

    // Put your variable declarations here
    public enum IntakeState {
        IN,
        OUT,
        STOP
    }
    private Configuration config = new Configuration();
    private boolean configLoaded = false;


    private boolean loadConfiguration() {
        configLoaded = false;
        config.clear();
        configLoaded = config.load();
        return configLoaded;
    }
    public IntakeState intakeState;

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
         GamepadButtonMultiPush gamepad1DpadUp;
         GamepadButtonMultiPush gamepad1DpadDown;
         GamepadButtonMultiPush gamepad1DpadLeft;
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
        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        gamepad1DpadLeft = new GamepadButtonMultiPush(1);
        HaloControls haloControls = new HaloControls(gamepad1LeftJoyStickY, gamepad1LeftJoyStickX, gamepad1RightJoyStickX, robot, telemetry);
    haloControls.setMode(HaloControls.Mode.DRIVER_MODE);

        // Note from Glenn:
        // None of the following are needed using the class AdafruitIMU8863. They are handled in the
        // initialization of the imu as part of the constructor.

        //**************************************************************

        waitForStart();
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

            if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
                haloControls.resetHeading();
            }
            if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
                haloControls.toggleMode();
            }
            if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
                haloControls.togglePowerModifier();
            }

            /*
            if (gamepad1.dpad_left) {
                rightIntake.setPower(0);
                leftIntake.setPower(0);
                telemetry.addData("Intake = ", "STOP");
            }

            if (gamepad1.dpad_down) {
                rightIntake.setPower(-1.0);
                leftIntake.setPower(-1.0);
                telemetry.addData("Intake = ", "OUT");
            }
*/
            // This would also work. Is there a performance advantage to it?
            //frontLeft.setPower(wheelVelocities.getFrontLeft());

            telemetry.addData("Mecanum:", mecanumCommands.toString());
            telemetry.addData("front left = ", mecanum.getFrontLeft());
            telemetry.addData("front right = ", mecanum.getFrontRight());
            telemetry.addData("back left = ", mecanum.getBackLeft());
            telemetry.addData("back right = ", mecanum.getBackRight());
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
