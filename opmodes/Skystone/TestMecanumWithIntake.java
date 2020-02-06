package org.firstinspires.ftc.teamcode.opmodes.Skystone;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

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
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.HaloControlsWithIntake;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakeWheels;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20;
import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Mecanum with intake", group = "Run")
//@Disabled
public class TestMecanumWithIntake extends LinearOpMode {

    // Put your variable declarations here

    @Override
    public void runOpMode() {


        // Put your initializations here
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
        DcMotor8863 rightIntake = new DcMotor8863("intakeMotorRight", hardwareMap);
        DcMotor8863 leftIntake = new DcMotor8863("intakeMotorLeft", hardwareMap);
        IntakeWheels intakeWheels = new IntakeWheels(rightIntake, leftIntake);
        // these motors are orbital (planetary gear) motors. The type of motor sets up the number
        // of encoder ticks per revolution. Since we are not using encoder feedback yet, this is
        // really not important now. But it will be once we hook up the encoders and set a motor
        // mode that uses feedback.
        frontLeft.setMotorType(ANDYMARK_20_ORBITAL);
        backLeft.setMotorType(ANDYMARK_20_ORBITAL);
        frontRight.setMotorType(ANDYMARK_20_ORBITAL);
        backRight.setMotorType(ANDYMARK_20_ORBITAL);

        //rightIntake.setMotorType(ANDYMARK_20_ORBITAL);
        //leftIntake.setMotorType(ANDYMARK_20_ORBITAL);

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
        Mecanum mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight);
        HaloControlsWithIntake haloControls = new HaloControlsWithIntake(gamepad1, imu, telemetry);
        ElapsedTime outtakeTimer = new ElapsedTime();

        //Switch intakeLimitSwitchLeft = new Switch(hardwareMap, "IntakeSwitchLeft", Switch.SwitchType.NORMALLY_OPEN);
        Switch intakeLimitSwitchRight = new Switch(hardwareMap, "IntakeSwitchRight", Switch.SwitchType.NORMALLY_OPEN);

        boolean inOuttake = false;
        final double OUTTAKE_TIME = 2.0;



        // Note from Glenn:
        // None of the following are needed using the class AdafruitIMU8863. They are handled in the
        // initialization of the imu as part of the constructor.

        //**************************************************************

        waitForStart();
        intakeWheels.init();
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

            if (haloControls.isIntakeOutPressed()) {
                intakeWheels.outtake();
                outtakeTimer.reset();
                inOuttake = true;
            } else if (haloControls.isIntakeInPressed())
                intakeWheels.intake();
            else if (haloControls.isIntakeStopPressed())
                intakeWheels.stop();

            if(inOuttake && outtakeTimer.seconds() >= OUTTAKE_TIME) {
                inOuttake = false;
                intakeWheels.intake();
            }
           //
            // boolean intakeSwitchLeftPressed = false;
            boolean intakeSwitchRightPressed = false;

/*
            if (intakeLimitSwitchLeft != null && intakeLimitSwitchLeft.isPressed()) {
                intakeSwitchLeftPressed = true;
            }
            */
            if (intakeLimitSwitchRight != null && intakeLimitSwitchRight.isPressed()) {
                intakeSwitchRightPressed = true;

            }
            if (/*intakeSwitchLeftPressed || */intakeSwitchRightPressed)
                intakeWheels.stop();

            // This would also work. Is there a performance advantage to it?
            //frontLeft.setPower(wheelVelocities.getFrontLeft());

            //telemetry.addData("Mecanum:", mecanumCommands.toString());
           // telemetry.addData("front left = ", mecanum.getFrontLeft());
           // telemetry.addData("front right = ", mecanum.getFrontRight());
           // telemetry.addData("back left = ", mecanum.getBackLeft());
           // telemetry.addData("back right = ", mecanum.getBackRight());
            telemetry.addData("Mode: ", haloControls.getMode() == HaloControls.Mode.DRIVER_MODE?"Driver":"Robot");
/*            if (intakeSwitchLeftPressed) {
                telemetry.addLine("left limit switch pressed");
            } else {
                telemetry.addLine("left limit switch NOT pressed");

            }
*/
            if (intakeSwitchRightPressed) {
                telemetry.addLine("right limit switch pressed");
            } else {
                telemetry.addLine("right limit switch NOT pressed");
            }


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
