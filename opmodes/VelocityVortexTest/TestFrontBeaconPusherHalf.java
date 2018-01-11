package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitI2CMux;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This opmode shows how to operate a CR Servo
 */
@TeleOp(name = "Test Front Beacon Pusher Half", group = "Test")
@Disabled
public class TestFrontBeaconPusherHalf extends LinearOpMode {

    // Put your variable declarations here
    double noMovePositionReverse = .48;
    double noMovePositionForward = .51;
    double deadZone = .1;
    CRServo testServo;
    CRServo.CRServoState currentState = CRServo.CRServoState.FORWARD_AT_SWITCH;

    ElapsedTime timer;

    String servoName;
    String backSwitchName;
    String frontSwitchName;
    boolean testLeftServo = false;


    @Override
    public void runOpMode() {

        if (testLeftServo) {
            servoName = RobotConfigMappingForGenericTest.getFrontLeftBeaconServoName();
            backSwitchName = RobotConfigMappingForGenericTest.getLeftBackLimitSwitchName();
            frontSwitchName = RobotConfigMappingForGenericTest.getLeftFrontLimitSwitchName();
        } else {
            servoName = RobotConfigMappingForGenericTest.getFrontRightBeaconServoName();
            backSwitchName = RobotConfigMappingForGenericTest.getRightBackLimitSwitchName();
            frontSwitchName = RobotConfigMappingForGenericTest.getRightFrontLimitSwitchName();
        }

        // Put your initializations here
        testServo = new CRServo(servoName, hardwareMap,
                noMovePositionForward, noMovePositionReverse, deadZone, Servo.Direction.FORWARD,
                frontSwitchName, Switch.SwitchType.NORMALLY_OPEN,
                backSwitchName, Switch.SwitchType.NORMALLY_OPEN,
                telemetry);
        if (testLeftServo) {
            testServo.setDirection(Servo.Direction.REVERSE);
            testServo.setForwardCMPerSecond(2.96);
            testServo.setBackwardCMPerSecond(3.21);
        } else {
            testServo.setDirection(Servo.Direction.FORWARD);
            testServo.setForwardCMPerSecond(1.90);
            testServo.setBackwardCMPerSecond(2.07);
        }

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // display the initial state that was figured out during the constructor
        currentState = testServo.update();
        telemetry.addData("current state ", currentState.toString());
        // since the entire robot is not moving, don't debounce the switches
        telemetry.addData("back switch ", testServo.backSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE));
        telemetry.addData("front switch ", testServo.frontSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE));
        telemetry.update();
        sleep(4000);

        timer.reset();
        testServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        while (opModeIsActive() && currentState != CRServo.CRServoState.FORWARD_AT_SWITCH) {
            currentState = testServo.update();
            telemetry.addData("current state ", currentState.toString());
            telemetry.addData("back switch ", testServo.backSwitch.isPressed());
            telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
            telemetry.update();
            idle();
        }
        telemetry.addData("current state ", currentState.toString());
        telemetry.addData("back switch ", testServo.backSwitch.isPressed());
        telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
        telemetry.update();
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
        telemetry.addData("current state", currentState.toString());
        telemetry.addData("back switch ", testServo.backSwitch.isPressed());
        telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
        telemetry.update();
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
        telemetry.addData("current state", currentState.toString());
        telemetry.addData("back switch ", testServo.backSwitch.isPressed());
        telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
        telemetry.update();
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
        telemetry.addData("current state", currentState.toString());
        telemetry.addData("back switch ", testServo.backSwitch.isPressed());
        telemetry.addData("front switch ", testServo.frontSwitch.isPressed());
        telemetry.update();

        // Display the current value
        // Put your cleanup code here - it runs as the application shuts down
        sleep(4000);
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
