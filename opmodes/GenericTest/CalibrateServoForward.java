package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This opmode is used to calibrate a servo so you can find the positions that the servo should
 * be set to when it is actually running on the real robot. You setup a start position and end
 * position. The servo will step from the start position to the end position in an increment you
 * pick. You can also tell the servo how long to spend at each position. For example, you need to
 * determine the position that a servo must be in to open a gate. You start it at position = 0.0
 * and end it at position = 1.0. The increment you pick is .05 and the time at each position is 1000
 * milli-seconds (1 second). The servo will start at position = 0, stay there for 1 second, then
 * move to position = .05 and stay there for one second, then move to position = .10 and stay there
 * for one second and so on until it reaches position = 1.0. While looking at the servo, you might
 * see that position = .45 is where the gate opens. So you can now program your servo on the robot
 * to move to .45 when you want the gate to open.
 * <p>
 * When you run this opmode, you are going to have to configure the robot controller phone with a
 * name for the servo you want to calibrate. That name will also have to be set in this code. See
 * below.
 */
@TeleOp(name = "Calibrate Servo Forward", group = "Test")
//@Disabled
public class CalibrateServoForward extends LinearOpMode {

    // Put your variable declarations here
    Servo8863 servoToCalibrate;


    // SETUP THE CALIBRATION HERE
    double startPosition = 0;
    double endPosition = 1;
    double positionIncrement = .05;
    double timeBetweenPosition = 1000; // in milli-seconds (1000 mSec = 1 sec)
    String servoName = "Servo"; // change this name to the one you configure on the phone

    //@Override
    public void runOpMode() {

        // Put your initializations here
        servoToCalibrate = new Servo8863(servoName, hardwareMap, telemetry);
        servoToCalibrate.setDirection(Servo.Direction.FORWARD);
        servoToCalibrate.setHomePosition(0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        servoToCalibrate.setUpServoCalibration(startPosition, endPosition, positionIncrement, timeBetweenPosition);

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            // call the method that updates the position of the servo
            servoToCalibrate.updateServoCalibration();

            // send a message to the driver station phone
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            // give the robot code a chance to do other stuff in the background
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
        servoToCalibrate.goHome();

    }
}
