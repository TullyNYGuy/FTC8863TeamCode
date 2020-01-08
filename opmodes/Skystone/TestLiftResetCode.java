package org.firstinspires.ftc.teamcode.opmodes.Skystone;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Left & Right Go To Position", group = "Test")
//@Disabled
public class TestLiftResetCode extends LinearOpMode {

    // Put your variable declarations here

    public Lift liftRight;


    public Lift.ExtensionRetractionStates extensionRetractionStateRight;


    public int encoderValueRight = 0;
    public int encoderValueMaxRight = 0;

    public int encoderValueMinRight = 0;


    public DataLogging logFileRight;
    public CSVDataFile timeEncoderValueFile;
    public double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    public double movementPerRevolution = spoolDiameter * Math.PI * 5;

    public ElapsedTime timerRight;
    public double startTime = 0;

    public double endUpTimeRight = 0;

    public double endDownTimeRight = 0;

    public String buffer = "";

    public double speed = 1.0;



    @Override
    public void runOpMode() {


        // Put your initializations here
        liftRight = new Lift(hardwareMap, telemetry, "extensionRetractionRight",
                "extensionLimitSwitchRight", "retractionLimitSwitchRight", "extensionRetractionMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        liftRight.reverseMotor();


        timerRight = new ElapsedTime();


        logFileRight = new DataLogging("ExtensionRetractionTestRight", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues", telemetry);





        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        liftRight.setDataLog(logFileRight);
        liftRight.enableDataLogging();
        liftRight.enableCollectData();
        liftRight.setResetPower(-0.1);
        liftRight.setRetractionPower(-speed);
        liftRight.setExtensionPower(+speed);

        liftRight.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        liftRight.reset();
        sleep(3000);


        liftRight.goToPosition(24.0,speed);

        timerRight.reset();

        while (opModeIsActive() && !liftRight.isResetComplete()) {

            extensionRetractionStateRight = liftRight.update();


            encoderValueRight = liftRight.getCurrentEncoderValue();


            if (encoderValueRight > encoderValueMaxRight) {
                encoderValueMaxRight = encoderValueRight;
            }


            telemetry.addData("Right state = ", extensionRetractionStateRight.toString());

            telemetry.addData("Right encoder = ", encoderValueRight);
            telemetry.update();
            idle();
        }

        // have to update the state machine in order to generate the last state update

        //liftRight.update();




        buffer = String.format(String.format("%.2f", endUpTimeRight));
        telemetry.addData("time up = ", buffer);
        telemetry.update();



        //*******************************************************************************************
        //*****************************************************************************************




        liftRight.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);

        liftRight.shutdown();


        buffer = String.format( String.format("%.2f", endUpTimeRight));
        telemetry.addData("time up = ", buffer);
        buffer = String.format(String.format("%.2f", endDownTimeRight));
        telemetry.addData("time down = ", buffer);
        buffer = String.format( String.format("%d", encoderValueMaxRight));
        telemetry.addData("max encoder value = ", buffer);
        buffer = String.format(String.format("%d", encoderValueMinRight));
        telemetry.addData("min encoder value = ", buffer);
        telemetry.addData(">", "Done");
        telemetry.update();

        // wait for user to kill the app
        while (opModeIsActive()) {
            idle();
        }
    }

    // Put your cleanup code here - it runs as the application shuts down
}
