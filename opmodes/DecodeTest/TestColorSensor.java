package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeColorSensorA;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeColorSensorB;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeMotor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Color Sensor", group = "Test")
//@Disabled
public class TestColorSensor extends LinearOpMode {

    // Put your variable declarations her
    DecodeColorSensorA colorSensorA;
    DecodeColorSensorB colorSensorB;

    DecodeIntakeMotor decodeIntakeMotor;

    @Override
    public void runOpMode() {


        // Put your initializations here
        colorSensorA = new DecodeColorSensorA(hardwareMap, telemetry, "colorSensorLeft");
        colorSensorB = new DecodeColorSensorB(hardwareMap, telemetry, "colorSensorRight");
        decodeIntakeMotor = new DecodeIntakeMotor(hardwareMap, telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        colorSensorA.sensor.turnSensorOn();
        colorSensorB.sensor.turnSensorOn();
        decodeIntakeMotor.on();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {


            colorSensorA.sensor.updateDataDistanceAndColor();
            colorSensorA.sensor.displayColorSensorDistance(telemetry);
            colorSensorA.sensor.displayColorData(telemetry);
            telemetry.addData("A Color......", colorSensorA.sensor.getMostLikelyColor().toString());
            telemetry.addData("","");
            colorSensorB.sensor.updateDataDistanceAndColor();
            colorSensorB.sensor.displayColorSensorDistance(telemetry);
            colorSensorB.sensor.displayColorData(telemetry);
            telemetry.addData("B Color......", colorSensorB.sensor.getMostLikelyColor().toString());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
