package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.ClawServo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.ShoulderServo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.WristServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Arm Glenn", group = "Test")
//@Disabled
public class TestArmGlenn extends LinearOpMode {

    // Put your variable declarations her
    ClawServo clawServo;
    WristServo wristServo;
    ShoulderServo shoulderServo;

    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        clawServo = new ClawServo(hardwareMap, telemetry);
        wristServo = new WristServo(hardwareMap, telemetry);
        shoulderServo = new ShoulderServo(hardwareMap, telemetry);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        clawServo.open();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            if (gamepad1.a) {
                clawServo.open();
                telemetry.addData("Claw open", "V");
            }

            if (gamepad1.b) {
                clawServo.close();
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
                wristServo.wristMid();
                telemetry.addData("shoulder down", "|");
            }

            if (gamepad1.dpad_right) {
                wristServo.wristDown();
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

    public void goDown(){
        wristServo.wristUp();
        shoulderServo.down();
        clawServo.open();
    }

    public void goUp(){
        wristServo.wristMid();
        shoulderServo.up();
    }

}
