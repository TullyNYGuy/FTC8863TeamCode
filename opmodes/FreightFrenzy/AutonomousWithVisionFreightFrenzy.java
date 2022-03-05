package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousWallVisionDuckSpinDeliverParkStorage;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousStateMachineFreightFrenzy;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousWarehouseVisionDeliverParkWarehouse;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ListSelector;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyMatchInfo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Autonomous Freight Frenzy", group = "AA")
//@Disabled
public class AutonomousWithVisionFreightFrenzy extends LinearOpMode {

    // Put your variable declarations her
    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public Configuration config;
    public FreightFrenzyStartSpot startSpot;
    private ElapsedTime timer;
    public boolean autoDone;
    private ShippingElementPipeline.ShippingPosition position;
    DataLogging dataLog = null;
    int cameraMonitorViewId;
    public FreightFrenzyField field;
    public double distance = 0;
    public double angleBetween = 0;
    private AutonomousStateMachineFreightFrenzy autonomous;
    private ShippingElementPipeline pipeline;

    @Override
    public void runOpMode() {
        FreightFrenzyMatchInfo.setMatchPhase(FreightFrenzyMatchInfo.MatchPhase.AUTONOMOUS);
        pipeline = new ShippingElementPipeline();
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        dataLog = new DataLogging("Autonomous", telemetry);
        config = null;
        //cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        timer = new ElapsedTime();
        field = new FreightFrenzyField();
        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        startSpot = PersistantStorage.getStartSpot();

        //int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        //robot.activeWebcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, robot.getCameraName()), cameraMonitorViewId);
        //robot.activeWebcam .setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        robot.activeWebcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                robot.activeWebcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
        robot.activeWebcam.setPipeline(pipeline);
        timer.reset();
        while (pipeline.getAnalysis() == ShippingElementPipeline.ShippingPosition.UNKNOWN) {
            // TANYA - need the idle so we don't hog all the CPU
            //pipeline.getAnalysis();
            idle();
            //autonomous.update();
        }

        position = pipeline.getAnalysis();
        //PersistantStorage.setShippingElementPosition(position);
        telemetry.addData("shipping element postion is", position);
        telemetry.update();
       /* switch(startSpot){
            case BLUE_WALL:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamLeft.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });

                break;
            case RED_WALL:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamRight.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });
                break;
            case BLUE_WAREHOUSE:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamLeft.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });
                break;
            case RED_WAREHOUSE:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamRight.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });
                break;
        }*/

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);
        Map<String, AutonomousStateMachineFreightFrenzy> stateMachines = new HashMap<>();
/*
        stateMachines.put("WarehouseDuckParkDepot", new AutonomousVisionLoadFrmWarehouseDuckSpinParkDepot(robot, field, telemetry));
        stateMachines.put("WarehouseDuckParkShipping", new AutonomousVisionLoadFrmWarehouseDuckSpinParkShippingArea(robot, field, telemetry));
        stateMachines.put("WarehouseDuckNoPark", new AutonomousVisionLoadFrmWarehouseDuckSpinNoParkNearWall(robot, field, telemetry));
 */
        stateMachines.put("WarehouseDeliverParkWarehouse", new AutonomousWarehouseVisionDeliverParkWarehouse(robot, field, telemetry));
        stateMachines.put("WallDuckDeliverParkStorage", new AutonomousWallVisionDuckSpinDeliverParkStorage(robot, field, telemetry));
        ListSelector selector = new ListSelector(telemetry, gamepad1, new ArrayList<String>(stateMachines.keySet()));
        String selected = selector.getSelection();
        autonomous = stateMachines.get(selected);

        timer.reset();
        robot.loopTimer.startLoopTimer();


        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        telemetry.addData("shipping element postion is", position);
        telemetry.update();
        robot.freightSystem.setPhaseAutonomus();

        // Wait for the start button
        waitForStart();
        pipeline.setPosition(ShippingElementPipeline.ShippingPosition.UNKNOWN);
        robot.activeWebcam.setPipeline(pipeline);

        while (pipeline.getAnalysis() == ShippingElementPipeline.ShippingPosition.UNKNOWN) {
            // TANYA - need the idle so we don't hog all the CPU
            //pipeline.getAnalysis();
            telemetry.addData("position is" , position.toString());
            telemetry.update();
            idle();
            //autonomous.update();
        }


        position = pipeline.getAnalysis();
        PersistantStorage.setShippingElementPosition(position);
        robot.activeWebcam.closeCameraDevice();

        /*while(opModeIsActive()){
            telemetry.addData("position is" , position.toString());
            telemetry.update();
            idle();
        }*/

        autonomous.start();
        while (opModeIsActive() && !autonomous.isComplete()) {
            autonomous.update();
            telemetry.addData("current state is", autonomous.getCurrentState());
            telemetry.update();
            robot.update();
            idle();
        }
        robot.ledStrip.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE);
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
