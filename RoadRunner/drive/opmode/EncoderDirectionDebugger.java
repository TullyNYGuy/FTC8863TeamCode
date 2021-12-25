package org.firstinspires.ftc.teamcode.RoadRunner.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RoadRunner.drive.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.RoadRunner.util.Encoder;

/**
 * This is a teleop routine for testing the direction settings of the encoders. You must set the
 * the direections of the motors properly first. See MotorDirectionDebugger opmode for that.
 * You need to establish the "normal" orientation of your robot. In other words what direction is
 * the front, back, left and right. These directions need to match for the motors and encoders.
 *
 * First, rotate your encoder wheels, one at a time. Check to make sure that the encoder count is
 * changing. If you get no reading check that the encoder is plugged into the proper motor port and
 * that your configuration maps that motor port to the encoder. See the StandardTrackingWheelLocalizer
 * class for that setting. If that is not the problem check that your wires are fully plugged in.
 *
 * Second, push to robot forward. The left and right encoder counts should be positive and
 * increasing. If not, then reverse the encoder direction in the StandardTrackingWheelLocalizer class.
 *
 * Last, push the bot to the left. The front/lateral encoder count should be positive and increasing.
 * If not, then reverse the encoder direction in the StandardTrackingWheelLocalizer class.
 */
@TeleOp(group = "drive")
public class EncoderDirectionDebugger extends LinearOpMode {

    private double leftEncoderInitialPosition = 0;
    private double rightEncoderInitialPosition = 0;
    private double frontEncoderInitialPosition = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        StandardTrackingWheelLocalizer localizer = new StandardTrackingWheelLocalizer(hardwareMap);

        Encoder leftEncoder = localizer.getLeftEncoder();
        Encoder rightEncoder = localizer.getRightEncoder();
        Encoder frontEncoder = localizer.getFrontEncoder();

        leftEncoderInitialPosition = leftEncoder.getCurrentPosition();
        rightEncoderInitialPosition = rightEncoder.getCurrentPosition();
        frontEncoderInitialPosition = frontEncoder.getCurrentPosition();

        waitForStart();

        while (!isStopRequested()) {
            telemetry.addData("left encoder count ", leftEncoder.getCurrentPosition() - leftEncoderInitialPosition);
            telemetry.addData("right encoder count ", rightEncoder.getCurrentPosition() - rightEncoderInitialPosition);
            telemetry.addData("front/lateral encoder count ", frontEncoder.getCurrentPosition() - frontEncoderInitialPosition);
            telemetry.update();
        }
    }
}
