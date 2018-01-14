package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Jewel Arm Angle Calculation", group = "Test")
@Disabled
public class TestAngleCalculation extends LinearOpMode {

    JewelArm jewelArm;
    public AdafruitColorSensor8863.ColorFromSensor ballColor;
    DataLogging dataLog;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;

    double upDownServoAngle;
    double elbowServoAngle;
    double distanceToBallStraight;
    double servoToBallDistance;
    double elbowAngle;
    double armAngleInTriangle;
    double Z;
    double armServoAngle;

    public void runOpMode() {

        // Put your initializations here
        dataLog = new DataLogging("jewelArmTest", telemetry);
        jewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, teamColor, dataLog );
        jewelArm.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        distanceToBallStraight = jewelArm.calculateDistanceToBallStraight(41.5);
        telemetry.addData("distance to ball straight = ", "%5.3f", distanceToBallStraight);

        servoToBallDistance = jewelArm.calculateServoToBallDistance(distanceToBallStraight);
        telemetry.addData("servo to ball distance = ", "%5.3f", servoToBallDistance);

        elbowAngle  = jewelArm.calculateElbowAngle(servoToBallDistance);
        telemetry.addData("elbow angle = ", "%5.3f", elbowAngle);

        armAngleInTriangle = jewelArm.calculateArmAngleInTriangle(servoToBallDistance);
        telemetry.addData("angle A = ", "%5.3f", armAngleInTriangle);

        Z = jewelArm.calculateZ(servoToBallDistance);
        telemetry.addData("angle Z = ", "%5.3f", Z);

        armServoAngle = jewelArm.calculateArmServoAngle(Z, armAngleInTriangle);
        telemetry.addData("arm servo angle = ", "%5.3f", armServoAngle);

        jewelArm.getServoAngles(41.5);
        upDownServoAngle = jewelArm.upDownServoAngle;
        elbowServoAngle = jewelArm.elbowServoAngle;
        telemetry.addData("combined Elbow angle = ", "%5.3f", elbowServoAngle);
        telemetry.addData("combined UpDown angle = ", "%5.3f", upDownServoAngle);
        telemetry.update();

        while(opModeIsActive()){
            idle();
        }
        // Put your cleanup code here - it runs as the application shuts down
        //telemetry.addData("Ball color = ", printBallColor.toString());
        //telemetry.addData(">", "Done");
       // telemetry.update();


    }
}
