package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Math Movements", group = "Test")
//@Disabled
public class TestMathMovements extends LinearOpMode {

    JewelArm leftJewelArm;
    public AdafruitColorSensor8863.ColorFromSensor ballColor;
    DataLogging dataLog;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;

    public double answerD;
    public double answerA;
    public double answerC;
    public double answerB;
    public double answerZ;
    public double answerY;


    public void runOpMode() {

        // Put your initializations here
        dataLog = new DataLogging("jewelArmTest", telemetry);
        leftJewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, teamColor, dataLog );
        leftJewelArm.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        answerD = leftJewelArm.calculateDistanceToBallStraight(38);
        answerB = leftJewelArm.calculateServoToBallDistance(answerD);
        answerC = leftJewelArm.calculateAngleC(answerB);
        answerA = leftJewelArm.calculateAngleA(answerB, answerC);
        answerZ = leftJewelArm.calculateAngleZ(answerB);
        answerY = leftJewelArm.calculateAngleY(answerZ, answerA);
        double elbowServoCommand;
      // elbowServoCommand= leftJewelArm.setUpMathMovements(answerY, answerC, 90);

        telemetry.addData("answerD = ", "%3.2f", answerD);
        telemetry.addData("answerB = ", "%3.2f", answerB);
        telemetry.addData("answerC = ", "%3.2f", answerC);
        telemetry.addData("answerA = ", "%3.2f", answerA);
        telemetry.addData("answerZ = ", "%3.2f", answerZ);
        telemetry.addData("answerY = ", "%3.2f", answerY);
       // telemetry.addData("elbowServoCommand ", "%3.2f", elbowServoCommand);
        telemetry.update();
        leftJewelArm.elbowServo.setPosition(leftJewelArm.getElbowServoCommandFromAngle(answerC));
        //leftJewelArm.upDownServo.setPosition(leftJewelArm.getElbowServoCommandFromAngle(answerY));
        leftJewelArm.upDownServo.setPosition(leftJewelArm.getUpDownServoCommandFromAngle(answerY));
        //leftJewelArm.elbowServo.setupMoveBySteps(leftJewelArm.getElbowServoCommandFromAngle(145),0.01,5);
        while(opModeIsActive()){
            //leftJewelArm.updateMathMovements();
            //leftJewelArm.elbowServo.updateMoveBySteps();
            idle();
        }





}}
