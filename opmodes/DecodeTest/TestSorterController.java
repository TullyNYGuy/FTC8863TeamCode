package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeColorSensorController;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeMotor;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRampServo;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterContoller;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Sorter Controller", group = "Test")
//@Disabled
public class TestSorterController extends LinearOpMode {

    // Put your variable declarations her

    DecodeSorterContoller decodeSorterContoller;
    DecodeRampServo decodeRampServo;
    DecodeIntakeMotor decodeIntakeMotor;
    DecodeSorterMotor decodeSorterMotor;
    DecodeColorSensorController decodeColorSensorController;

    @Override
    public void runOpMode() {

        // These debounce the buttons so that you only see a single press even if a button is held
        // down for a long time.
        Debouncer debouncedY = new Debouncer();
        Debouncer debouncedB = new Debouncer();
        Debouncer debouncedX = new Debouncer();
        Debouncer debouncedA = new Debouncer();
        Debouncer debouncedDpadUp = new Debouncer();
        Debouncer debouncedDpadDown = new Debouncer();
        Debouncer debouncedDpadLeft = new Debouncer();

        // Put your initializations here

        decodeColorSensorController = new DecodeColorSensorController(hardwareMap, telemetry);
        decodeColorSensorController.init(null);

        decodeSorterMotor = new DecodeSorterMotor(hardwareMap, telemetry);
        decodeSorterMotor.init(null);

        decodeIntakeMotor = new DecodeIntakeMotor(hardwareMap, telemetry);
        decodeIntakeMotor.init(null);

        decodeRampServo = new DecodeRampServo(hardwareMap, telemetry);
        decodeRampServo.init(null);

        decodeSorterContoller = new DecodeSorterContoller(hardwareMap, telemetry, decodeColorSensorController, decodeSorterMotor, decodeRampServo, decodeIntakeMotor);
        decodeSorterContoller.init(null);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        decodeSorterContoller.intakeOn();


        while (opModeIsActive()) {
            if (debouncedY.isPressed(gamepad2.y)) {
                decodeSorterContoller.shootThree();

            }
            if (debouncedA.isPressed(gamepad2.a)) {
                decodeSorterContoller.shootOne();
            }
            if (debouncedX.isPressed(gamepad2.x)) {

            }
            if (debouncedB.isPressed(gamepad2.b)) {
                decodeSorterContoller.shootTwo();
            }

            decodeIntakeMotor.update();
            decodeSorterMotor.update();
            decodeColorSensorController.update();
            decodeSorterContoller.update();

            // limit the rpm between 0 and 1


            telemetry.addData("Y = ", "120");
            telemetry.addData("X = ", "0");
            telemetry.addData("B = ", "360");
            telemetry.addData("A = ", "240");
            telemetry.addData("Actual RPM = ", decodeSorterMotor.getActualRPM());
            telemetry.addData("Sorter Controller State ", decodeSorterContoller.getState());
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
