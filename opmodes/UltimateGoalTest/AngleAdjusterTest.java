package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AngleChanger;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Angle Adjuster", group = "Test")
//@Disabled
public class AngleAdjusterTest extends LinearOpMode {

    // Put your variable declarations here
    public AngleChanger angleChanger;
    private ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        angleChanger = new AngleChanger(hardwareMap, telemetry);
        angleChanger.clearAngleChanger();

        timer = new ElapsedTime();
        angleChanger.setAngleReference();
        double angle = 0.0;

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        angle = 30.0;
        angleChanger.setCurrentAngle(AngleUnit.DEGREES, angle);
        telemetry.addData("Setting angle of shooter to ", angle);
        telemetry.update();
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {
            angleChanger.update();
            idle();
        }

        telemetry.addData("motor encoder count = ", angleChanger.getMotorEncoderCount());
        telemetry.update();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000){
            idle();
        }

        // reset Angle changer
        angleChanger = null;
        angleChanger = new AngleChanger(hardwareMap, telemetry);
        telemetry.addData("resetting angle changer", "!");
        telemetry.addData("motor encoder count = ", angleChanger.getMotorEncoderCount());
        telemetry.update();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000){
            idle();
        }

        angle = 20.0;
        angleChanger.setCurrentAngle(AngleUnit.DEGREES, angle);
        telemetry.addData("Setting angle of shooter to ", angle);
        telemetry.update();
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {
            angleChanger.update();
            idle();
        }

        // reset Angle changer
        angleChanger = null;
        angleChanger = new AngleChanger(hardwareMap, telemetry);
        telemetry.addData("resetting angle changer", "!");
        telemetry.addData("motor encoder count = ", angleChanger.getMotorEncoderCount());
        telemetry.update();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000){
            idle();
        }

        angle = 10.0;
        angleChanger.setCurrentAngle(AngleUnit.DEGREES, angle);
        telemetry.addData("Setting angle of shooter to ", angle);
        telemetry.update();
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {
            angleChanger.update();
            idle();
        }

        // reset Angle changer
        angleChanger = null;
        angleChanger = new AngleChanger(hardwareMap, telemetry);
        telemetry.addData("resetting angle changer", "!");
        telemetry.addData("motor encoder count = ", angleChanger.getMotorEncoderCount());
        telemetry.update();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000){
            idle();
        }

        angle = 30.0;
        angleChanger.setCurrentAngle(AngleUnit.DEGREES, angle);
        telemetry.addData("Setting angle of shooter to ", angle);
        telemetry.update();
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {
            angleChanger.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
