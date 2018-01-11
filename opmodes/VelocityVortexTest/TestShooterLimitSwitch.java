package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexShooter;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a drive train
 * <p>
 * This code assumes a DC motor configured with the name "leftMotor"  and "rightMotor"
 */
@TeleOp(name = "Test Shooter Limit Switch", group = "Test")
@Disabled
public class TestShooterLimitSwitch extends LinearOpMode {

    // enums for a state machine for the shooter motor

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    VelocityVortexShooter shooter;

    // for use in debouncing the button. A long press will only result in one transition of the
    // shooterDirection
    boolean rightBumperIsReleased = true;
    boolean leftBumperIsReleased = true;
    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;
    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;

    // used to track the direction of the shooter. It can be run forwards to collect balls
    // or backwards to shoot balls
    boolean shooterDirectionForward = true;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************
        shooter = new VelocityVortexShooter(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************

        // Tank drive using gamepad joysticks
        while (opModeIsActive()) {

            telemetry.addData("Switch Pressed = ", shooter.isLimitSwitchPressed());

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
    }
}

