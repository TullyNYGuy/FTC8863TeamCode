package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServoGB;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;

/**
 * This opmode shows how to operate a CR Servo
 */
@TeleOp(name = "Test CR Servo State Machine", group = "Test")
//@Disabled
public class TestCRServoStateMachine extends LinearOpMode {

    // Put your variable declarations here
    double noMovePositionReverse = .48;
    double noMovePositionForward = .51;
    double deadZone = .1;
    CRServo testServo;
    CRServo.CRServoState currentState = CRServo.CRServoState.FORWARD_AT_SWITCH;

    ElapsedTime timer;

    int step = 1;
    double command = 0;
    double startCommand = .4;
    double endCommand = .5;
    double commandIncrement = .01;
    int stepLength = 1000; // milliseconds

    int timeToRunTest = 4000; // milliseconds
    boolean lockTimer = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        testServo = new CRServo(RobotConfigMappingForGenericTest.getcrServoName(), hardwareMap,
                noMovePositionForward, noMovePositionReverse, deadZone, Servo.Direction.FORWARD,
                RobotConfigMappingForGenericTest.getRightFrontLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN,
                RobotConfigMappingForGenericTest.getRightBackLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN,
                telemetry);
        //testServo.setDirection(Servo.Direction.REVERSE);
        testServo.setBackwardCMPerSecond(2.07);
        testServo.setForwardCMPerSecond(1.90);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();
        testServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        while (opModeIsActive() && currentState != CRServo.CRServoState.FORWARD_AT_SWITCH) {
            currentState = testServo.update();
            telemetry.addData("current state", currentState.toString());
            telemetry.addData("back switch ", testServo.backSwitch.isPressed());
            telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
            telemetry.update();
            idle();
        }
        sleep(4000);

        testServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
        while (opModeIsActive() && currentState != CRServo.CRServoState.BACK_AT_SWITCH) {
            currentState = testServo.update();
            telemetry.addData("current state", currentState.toString());
            telemetry.addData("back switch ", testServo.backSwitch.isPressed());
            telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
            telemetry.update();
            idle();
        }
        sleep(4000);

        testServo.startMoveDistance(4, CRServo.CRServoDirection.FORWARD);
        while (opModeIsActive() && currentState != CRServo.CRServoState.FORWARD_AT_POSITION) {
            currentState = testServo.update();
            telemetry.addData("current state", currentState.toString());
            telemetry.addData("back switch ", testServo.backSwitch.isPressed());
            telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
            telemetry.update();
            idle();
        }
        sleep(4000);

        testServo.startMoveDistance(3, CRServo.CRServoDirection.BACKWARD);
        while (opModeIsActive() && currentState != CRServo.CRServoState.BACK_AT_POSITION) {
            currentState = testServo.update();
            telemetry.addData("current state", currentState.toString());
            telemetry.addData("back switch ", testServo.backSwitch.isPressed());
            telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
            telemetry.update();
            idle();
        }

        // Display the current value
        // Put your cleanup code here - it runs as the application shuts down
        sleep(4000);
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
