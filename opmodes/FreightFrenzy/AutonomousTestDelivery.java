package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousDuckSpinVisionLoadParkStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousStateMachineFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousTestDeliveryStateMachine;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyMatchInfo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Autonomous Test Delivery", group = "AA")
//@Disabled
public class AutonomousTestDelivery extends LinearOpMode {

    // Put your variable declarations her
    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public Configuration config;
    DataLogging dataLog = null;
    public FreightFrenzyField field;
    private AutonomousStateMachineFreightFrenzy autonomous;

    @Override
    public void runOpMode() {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        dataLog = new DataLogging("Autonomous", telemetry);
        config = null;
        field = new FreightFrenzyField();
        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        robot.freightSystem.setPhaseAutonomus();

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);
        autonomous = new AutonomousTestDeliveryStateMachine(robot, field, telemetry);

        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        telemetry.update();

        // Wait for the start button
        waitForStart();

        autonomous.start();
        while (opModeIsActive() && !autonomous.isComplete()) {
            autonomous.update();
            robot.update();
            telemetry.addData("current state is", autonomous.getCurrentState());
            telemetry.update();
            idle();
        }

        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    public void enableBulkReads(HardwareMap hardwareMap, LynxModule.BulkCachingMode mode) {
        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(mode);
        }
    }
}
