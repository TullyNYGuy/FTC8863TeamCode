package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Jewel Arm", group = "Test")
//@Disabled
public class TestJewelArm extends LinearOpMode {

    JewelArm leftJewelArm;
    public AdafruitColorSensor8863.ColorFromSensor ballColor;

    public void runOpMode() {

        // Put your initializations here
        leftJewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry);
        leftJewelArm.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        //leftJewelArm.elbowServo.setUpServoCalibration(0, 1, 0.05, 1000);
       //leftJewelArm.frontBackServo.setUpServoCalibration(.5, 6, 0.01, 2000);
        //leftJewelArm.upDownServo.setUpServoCalibration(.05, .55,0.01, 5);



        // Put your calls here - they will not run in a loop
//        leftJewelArm.goAboveBall2();
//        ballColor = leftJewelArm.getBallColor();
//        telemetry.addData("Ball color = ", ballColor.toString());
//        leftJewelArm.moveBetweenBalls();
//        leftJewelArm.knockOffBall2(AllianceColor.TeamColor.RED, ballColor);
        //ballColor = leftJewelArm.getBallColorAndKnockOffBall(AllianceColor.TeamColor.BLUE);
        //telemetry.addData("Ball color = ", ballColor.toString());
        //telemetry.update();

        sleep (1000);

        //leftJewelArm.testServoMotions();

        //leftJewelArm.elbowServo.setPosition(0);
      //leftJewelArm.shutdown();
        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            leftJewelArm.updateGoAboveBall();

            //leftJewelArm.elbowServo.updateServoCalibration();
            //leftJewelArm.frontBackServo.updateServoCalibration();
            //leftJewelArm.upDownServo.updateServoCalibration();

            //ballColor = leftJewelArm.getBallColor();
            //telemetry.addData("Ball color = ", ballColor.toString());
            //telemetry.addData("Ball color = ", ballColor.toString());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        //telemetry.addData("Ball color = ", printBallColor.toString());
        //telemetry.addData(">", "Done");
       // telemetry.update();


    }
}
