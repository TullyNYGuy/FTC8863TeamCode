package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeColorSensorA;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeColorSensorB;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeColorSensorController;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeMotor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Color Sensor Controller!!!!!!!!", group = "Test")
//@Disabled
public class TestColorSensorController extends LinearOpMode {

    // Put your variable declarations her
    DecodeColorSensorController colorSensorController;
    DecodeIntakeMotor intakeMotor;

    @Override
    public void runOpMode() {


        // Put your initializations here
        colorSensorController = new DecodeColorSensorController(hardwareMap, telemetry);
        intakeMotor = new DecodeIntakeMotor("intakeMotor", hardwareMap,telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        colorSensorController.colorSensorsOn();
        intakeMotor.setRPM(500);


        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {


            telemetry.addData("Ball present = ", Boolean.toString(colorSensorController.isArtifactPresent()));
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
