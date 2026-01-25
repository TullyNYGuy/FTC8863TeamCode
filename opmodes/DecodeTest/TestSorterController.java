package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeColorSensorController;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeMotor;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRampServo;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeShooterMotor;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterContoller;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
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
    DecodeShooterMotor decodeShooterMotor;
    DecodeColorSensorController decodeColorSensorController;
    DataLogging dataLog;

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
        dataLog = new DataLogging("SorterControllerTest", telemetry);

        decodeColorSensorController = new DecodeColorSensorController(hardwareMap, telemetry);
        decodeColorSensorController.setDataLog(dataLog);
        decodeColorSensorController.enableDataLogging();
        decodeColorSensorController.init(null);

        decodeSorterMotor = new DecodeSorterMotor(hardwareMap, telemetry);
        decodeSorterMotor.setDataLog(dataLog);
        decodeSorterMotor.enableDataLogging();
        decodeSorterMotor.init(null);

        decodeIntakeMotor = new DecodeIntakeMotor(hardwareMap, telemetry);
        decodeIntakeMotor.setDataLog(dataLog);
        decodeIntakeMotor.enableDataLogging();
        decodeIntakeMotor.init(null);

        decodeShooterMotor = new DecodeShooterMotor(hardwareMap, telemetry);
        decodeShooterMotor.setDataLog(dataLog);
        decodeShooterMotor.enableDataLogging();
        decodeShooterMotor.init(null);

        decodeRampServo = new DecodeRampServo(hardwareMap, telemetry);
        decodeRampServo.setDataLog(dataLog);
        decodeRampServo.enableDataLogging();
        decodeRampServo.init(null);

        decodeSorterContoller = new DecodeSorterContoller(hardwareMap, telemetry,
                decodeColorSensorController,
                decodeSorterMotor,
                decodeRampServo,
                decodeIntakeMotor);
        decodeSorterContoller.setDataLog(dataLog);
        decodeSorterContoller.enableDataLogging();
        decodeSorterContoller.init(null);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        //decodeShooterMotor.setRPM(3100);
        // Put your calls here - they will not run in a loop


        while (opModeIsActive()) {
            decodeSorterContoller.update();

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

            if (debouncedDpadUp.isPressed(gamepad2.dpad_up)) {
                decodeSorterContoller.intake();
            }

            if (debouncedDpadDown.isPressed(gamepad2.dpad_down)) {
                decodeSorterContoller.intakeOff();
            }

            decodeIntakeMotor.update();
            decodeSorterMotor.update();
            decodeColorSensorController.update();
            decodeSorterContoller.update();

            // limit the rpm between 0 and 1


            telemetry.addData("Y = ", "Shoot three");
            telemetry.addData("X = ", "Nothing");
            telemetry.addData("B = ", "Shoot two");
            telemetry.addData("A = ", "Shoot one");
            telemetry.addData("Dpad up =", "Intake on");
            telemetry.addData("Dpad down =", "Intake off");
            telemetry.addLine();
            decodeSorterContoller.displayState(telemetry);
            decodeSorterContoller.displayCommand(telemetry);
            decodeSorterContoller.displayCommandComplete(telemetry);
            decodeSorterContoller.displaySensorStatus(telemetry);
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
