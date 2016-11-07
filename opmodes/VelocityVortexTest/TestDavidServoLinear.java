package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a double ball shooter
 * <p>
 * This code assumes a DC motor configured with the name "leftDriveMotor"  and "rightDriveMotor"
 */
@TeleOp(name = "Test David Servo Linear", group = "Test")
//@Disabled
public class TestDavidServoLinear extends LinearOpMode {

    boolean genericServoActive = true;
    double forwardPosition = 1;
    double backwardsPosition = 0;
    double homePosition = .5;
    double initPosition = homePosition;
    Servo8863 genericServo;


    @Override
    public void runOpMode() {


        genericServo = new Servo8863(RobotConfigMappingForGenericTest.getgenericServo(), hardwareMap, telemetry, homePosition, forwardPosition, backwardsPosition, initPosition, Servo.Direction.REVERSE);

        genericServo.setPositionOne(forwardPosition);
        genericServo.setPositionTwo(backwardsPosition);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();
        waitForStart();

        genericServo.goHome();

        while (opModeIsActive()) {
            //gamepad button configuration:
            //      Y
            //  X      B
            //      A

            // if the right bumper is pressed, reverse the direction of the motor speed increase
            // or decrease. Example. Suppose that X and B presses are increasing the speed of the
            // motors. Press the right bumper. Now presses of the X or B buttons will decrease the
            // speed of the motors. Press the right bumper again and the X or B buttons will now
            // increase the speed.
            // I have to check the status of the bumper. It is possible to hold it down for a long
            // time and if I do, then I don't want to flip speedIncrementDirection back and forth.
            // So I make sure that the button has been released before doing anything to the
            // speedIncrementDirection.

            if (gamepad1.a) {
                genericServo.goPositionOne();
                telemetry.addData("Servo", "is forward");
            }

            if (gamepad1.x) {
                genericServo.goPositionTwo();
                telemetry.addData("Servo", "is backward");
            }


            // Display the current position

            telemetry.addData("Position",  "Position: " + String.format("%.2f", genericServo.getPosition()));
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        telemetry.addData(">", "Done");

        telemetry.update();

    }
}
