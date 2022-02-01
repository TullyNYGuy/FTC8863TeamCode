package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Arm", group = "Test")
//@Disabled
public class TestArm extends LinearOpMode {

    // Put your variable declarations her
    Servo8863 clawServo;
    double openPosition = 0;
    double closePosition = .58;

    Servo8863 wristServo;
    double wristUpPosition = .60;
    double wristMidPosition = .18;
    double wristDownPosition = .05;

    Servo8863 shoulderServo;
    double shoulderUpPosition = .63 ;
    double shoulderDownPosition = .1;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        clawServo = new Servo8863("clawServo",hardwareMap, telemetry);
        clawServo.setPositionOne(openPosition);
        clawServo.setPositionTwo(closePosition);

        wristServo = new Servo8863("wristServo",hardwareMap, telemetry);
        wristServo.setPositionOne(wristUpPosition);
        wristServo.setPositionTwo(wristDownPosition);
        wristServo.setPositionThree(wristMidPosition);

        shoulderServo = new Servo8863("shoulderServo",hardwareMap, telemetry);
        shoulderServo.setPositionOne(shoulderUpPosition);
        shoulderServo.setPositionTwo(shoulderDownPosition);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        openClaw();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            if (gamepad1.a) {
                openClaw();
                telemetry.addData("Claw open", "V");
            }

            if (gamepad1.b) {
                closeClaw();
                telemetry.addData("Claw closed", "||");
            }

            if (gamepad1.dpad_down) {
                goDown();
                telemetry.addData("Wrist up", "/");
            }

            if (gamepad1.dpad_up) {
                goUp();
                telemetry.addData("wrist down", "|");
            }

            if (gamepad1.dpad_left) {
                wristMid();
                telemetry.addData("shoulder down", "|");
            }

            if (gamepad1.dpad_right) {
                wristDown();
                telemetry.addData("shoulder up", "/");
            }

            if (gamepad1.x){

            }
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    public void openClaw() {
        clawServo.goPositionOne();
    }

    public void closeClaw() {
        clawServo.goPositionTwo();
    }

    public void wristUp() {
        wristServo.goPositionOne();
    }

    public void wristMid() {
        wristServo.goPositionThree();
    }

    public void wristDown() {
        wristServo.goPositionTwo();
    }

    public void shoulderUp() {
        shoulderServo.goPositionOne();
    }

    public void shoulderDown() {
        shoulderServo.goPositionTwo();
    }

    public void goDown(){
        wristUp();
        shoulderDown();
        openClaw();
    }

    public void goUp(){
        wristMid();
        shoulderUp();
    }

}
