package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Units;

@TeleOp(name = "AAAAAAAAAAH odometry test", group = "Test")
//@Disabled
public class Read3OdometryModules extends LinearOpMode {
    //Odometry Wheels


    double odometryEncoderValue1 = 0;
    double odometryEncoderValue2 = 0;
    double odometryEncoderValue3 = 0;

    OdometryModule odometryModule1;
    OdometryModule odometryModule2;
    OdometryModule odometryModule3;

    @Override
    public void runOpMode() throws InterruptedException {

        odometryModule1 = new OdometryModule(OdometryModule.Position.LEFT, 1440, 3.8, Units.CM, "BackRight", hardwareMap);
        odometryModule2 = new OdometryModule(OdometryModule.Position.RIGHT, 1440, 3.8, Units.CM, "FrontRight", hardwareMap);
        odometryModule3 = new OdometryModule(OdometryModule.Position.FRONT, 1440, 3.8, Units.CM, "BackLeft", hardwareMap);

        //Odometry System Calibration Init Complete
        telemetry.addData("Odometry System Calibration Status", "Init Complete");
        odometryEncoderValue1 = odometryModule1.getDistanceSinceReset(Units.CM);
        odometryEncoderValue2 = odometryModule2.getDistanceSinceReset(Units.CM);
        odometryEncoderValue3 = odometryModule3.getDistanceSinceReset(Units.CM);
        telemetry.addData("Odometry encoder 1 value = ", odometryEncoderValue1);
        telemetry.addData("Odometry encoder 2 value = ", odometryEncoderValue2);
        telemetry.addData("Odometry encoder 3 value = ", odometryEncoderValue3);
        telemetry.update();


        waitForStart();

        while (opModeIsActive()) {
            odometryEncoderValue1 = odometryModule1.getEncoderValue();
            odometryEncoderValue2 = odometryModule2.getEncoderValue();
            odometryEncoderValue3 = odometryModule3.getEncoderValue();
            telemetry.addData("Odometry encoder value1 = ", odometryEncoderValue1);
            telemetry.addData("Odometry Encoder Value1 (In)", String.format("%.2f", odometryEncoderValue1 / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Value1 (cm)", String.format("%.2f", odometryModule1.getDistanceSinceReset(Units.CM)));
            telemetry.addData("Odometry encoder value2 = ", odometryEncoderValue2);
            telemetry.addData("Odometry Encoder Value2 (In)", String.format("%.2f", odometryEncoderValue2 / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Value2 (cm)", String.format("%.2f", odometryModule2.getDistanceSinceReset(Units.CM)));
            telemetry.addData("Odometry encoder value3 = ", odometryEncoderValue3);
            telemetry.addData("Odometry Encoder Value3 (In)", String.format("%.2f", odometryEncoderValue3 / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Value3 (cm)", String.format("%.2f", odometryModule3.getDistanceSinceReset(Units.CM)));
            //Update values
            telemetry.update();
        }
    }
}

