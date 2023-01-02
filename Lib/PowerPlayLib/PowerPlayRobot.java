package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoopTimer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLED;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDBlinker;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFBlinkinLed;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyMatchInfo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.MecanumDriveFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PowerPlayRobot implements FTCRobot {

    public enum HardwareName {
        IMU("imu"),

        CONFIG_FL_MOTOR("leftFrontMotor"),
        CONFIG_FR_MOTOR("rightFrontMotor"),
        CONFIG_BL_MOTOR("leftRearMotor"),
        CONFIG_BR_MOTOR("rightRearMotor"),

        CONFIG_LEFT_ODOMETRY_MODULE("LeftOdometryModule"),
        CONFIG_RIGHT_ODOMETRY_MODULE("RightOdometryModule"),
        CONFIG_BACK_ODOMETRY_MODULE("BackOdometryModule"),

        ODOMETRY_MODULE_LEFT("leftFrontMotor"),
        ODOMETRY_MODULE_RIGHT("rightRearMotor"),
        ODOMETRY_MODULE_BACK("leftRearMotor"),

        WEBCAM_LEFT("WebcamLeft"),
        WEBCAM_RIGHT("WebcamRight"),

        CONE_GRABBER_SERVO("coneGrabberServo"),
        CONE_GRABBER_ARM_SERVO("coneGrabberArmServo"),

        LEFT_LIFT_MOTOR("leftLiftMotor"),
        LEFT_LIFT_LIMIT_SWITCH_RETRACTION("leftLiftRetractionLimitSwitch"),
        LEFT_LIFT_LIMIT_SWITCH_EXTENSION("leftLiftExtensionLimitSwitch"),

        LED_PORT1("ledPort1"),
        LED_PORT2("ledPort2"),
        LED_STRIP("ledStrip");


        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM_DRIVE,
        ODOMETRY,
        WEBCAM_LEFT,
        WEBCAM_RIGHT,
        LEFT_LIFT,
        CONE_GRABBER
        //LED_BLINKER,
        //LED_STRIP,
    }

    Set<Subsystem> capabilities;
    public OpenCvCamera activeWebcam;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    private FreightFrenzyMatchInfo robotMode;
    Map<String, FTCRobotSubsystem> subsystemMap;
    private String activeWebcamName;
    private AllianceColor allianceColor;
    private ElapsedTime timer;
    private LinearOpMode opMode;


    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    int cameraMonitorViewId;

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
    }

    private AdafruitIMU8863 imu;
    public MecanumDriveFreightFrenzy mecanum;
    public LoopTimer loopTimer;
    public OpenCvWebcam webcamLeft;
    public OpenCvWebcam webcamRight;
    public PowerPlayLeftLift leftLift;
    public PowerPlayConeGrabber coneGrabber;
    //public RevLEDBlinker ledBlinker;
    //public FFBlinkinLed ledStrip;

    public PowerPlayRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config, DataLogging dataLog, DistanceUnit units, LinearOpMode opMode, AllianceColor allianceColor) {
        timer = new ElapsedTime();
        loopTimer = new LoopTimer();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        this.opMode = opMode;
        this.allianceColor = allianceColor;
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        setCapabilities(Subsystem.values());
        enableDataLogging();
    }

    /*
     * This function should be called, if needed, before createRobot() call
     */
    public void setCapabilities(Subsystem[] subsystems) {
        capabilities = new HashSet<Subsystem>(Arrays.asList(subsystems));
    }

    /**
     * Create the robot should be called from the teleop or auto opmode.
     *
     * @return
     */

    @Override
    public boolean createRobot() {
        imu = new AdafruitIMU8863(hardwareMap, null, "IMU", HardwareName.IMU.hwName);
        if (capabilities.contains(Subsystem.MECANUM_DRIVE)) {
            mecanum = new MecanumDriveFreightFrenzy(HardwareName.CONFIG_FL_MOTOR.hwName, HardwareName.CONFIG_BL_MOTOR.hwName, HardwareName.CONFIG_FR_MOTOR.hwName, HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);
            subsystemMap.put(mecanum.getName(), mecanum);
        }

        // THE WEBCAM PROCESSING TAKES UP A BUNCH OF RESOURCES. PROBABLY NOT A GOOD IDEA TO RUN THIS IN TELEOP
//        if (FreightFrenzyMatchInfo.getMatchPhase() == FreightFrenzyMatchInfo.MatchPhase.AUTONOMOUS)
//            switch (color) {
//                case RED_WALL:
//                case RED_WAREHOUSE:
//                    if (capabilities.contains(Subsystem.WEBCAM_LEFT)) {
//                        // Timeout for obtaining permission is configurable. Set before opening.
//                        //activeWebcam = webcamLeft;
//                        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//                        webcamLeft = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "WebcamLeft"), cameraMonitorViewId);
//                        activeWebcam = webcamLeft;
//                        activeWebcamName = "webcamLeft";
//                        webcamLeft.setMillisecondsPermissionTimeout(2500);
//                    }
//                    break;
//                case BLUE_WAREHOUSE:
//                case BLUE_WALL:
//                    if (capabilities.contains(Subsystem.WEBCAM_RIGHT)) {
//
//
//                        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//                        webcamRight = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "WebcamRight"), cameraMonitorViewId);
//                        activeWebcam = webcamRight;
//                        activeWebcamName = "webcamRight";
//                        webcamRight.setMillisecondsPermissionTimeout(2500);
//                    }
//                    break;
//            }

        if (capabilities.contains(Subsystem.LEFT_LIFT)) {
            leftLift = new PowerPlayLeftLift(hardwareMap, telemetry);
            subsystemMap.put(leftLift.getName(), leftLift);
        }

        if (capabilities.contains(Subsystem.CONE_GRABBER)) {
            coneGrabber = new PowerPlayConeGrabber(hardwareMap, telemetry);
            subsystemMap.put(coneGrabber.getName(), coneGrabber);
        }

//        if (capabilities.contains(Subsystem.LED_BLINKER)) {
//            ledBlinker = new RevLEDBlinker(2, RevLED.Color.GREEN, hardwareMap,
//                    HardwareName.LED_PORT1.hwName, HardwareName.LED_PORT2.hwName);
//            subsystemMap.put(ledBlinker.getName(), ledBlinker);
//        }
//
//        if (capabilities.contains(Subsystem.LED_STRIP)) {
//            ledStrip = new FFBlinkinLed(hardwareMap);
//            subsystemMap.put(ledStrip.getName(), ledStrip);
//        }

//        if (capabilities.contains(Subsystem.FREIGHT_SYSTEM)) {
//            freightSystem = new FFFreightSystem(arm, intake, lift, hardwareMap, telemetry, allianceColor, ledBlinker, ledStrip);
//            // The freight system has to know whether this autonomous or telelop before the init is run.
//            if (FreightFrenzyMatchInfo.getMatchPhase() == FreightFrenzyMatchInfo.MatchPhase.AUTONOMOUS) {
//                freightSystem.setPhaseAutonomus();
//            } else {
//                freightSystem.setPhaseTeleop();
//            }
//            subsystemMap.put(freightSystem.getName(), freightSystem);
//        }

        init();
        return true;
    }

    /**
     * Every system has an init. Call it.
     */
    @Override
    public void init() {
        dataLog.logData("Robot Init starting");
        timer.reset();

        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.setDataLog(dataLog);
            subsystem.enableDataLogging();
            if (!subsystem.init(config)) {
                if (dataLoggingEnabled)
                    dataLog.logData(subsystem.getName() + " initialization failed");
            }
        }

        // wait until all the updates are complete or until the timer has expired
        timer.reset();
        while (!isInitComplete()) {
            update();

            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                break;
            }
            telemetry.update();
            opMode.idle();
        }
        //ledStrip.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_FOREST_PALETTE);
    }

    /*
     * Every system must tell us when its init is complete. When all of the inits are complete, the
     * robot init is complete.
     *
     * @return
     */
    @Override
    public boolean isInitComplete() {
        boolean result = true;
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            // the subsystems now take care of their own logging for init and init completion
            // This is so we don't dump huge amounts of repetitive data into the log file.
//            if (subsystem.isInitComplete()) {
//                if (dataLoggingEnabled) {
//                    dataLog.logData("Init complete for " + subsystem.getName());
//                }
//
//            } else {
//                dataLog.logData("Init is not complete for " + subsystem.getName());
//            }
            result &= subsystem.isInitComplete();
        }
        if (dataLoggingEnabled && result == true) {
            dataLog.logData("Robot init complete");
        }
        return result;
    }

    /**
     * Every system has an update() method that can be used to run a state machine for that system.
     * Note that some systems don't have a state machine but the update() method will be there
     * anyway just in case that changes in the future.
     */
    @Override
    public void update() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.update();
        }
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.timedUpdate(timerValueMsec);
        }
    }

    @Override
    public void shutdown() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.shutdown();
        }
    }

    /**
     * For each subsystem that supports logging turn it on.
     */
    public void enableDataLogging() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.enableDataLogging();
        }
        dataLoggingEnabled = true;
    }

    /**
     * For each subsystem that supports logging, turn it off
     */
    public void disableDataLogging() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.disableDataLogging();
        }
        dataLoggingEnabled = false;
    }

    private void log(String stringToLog) {
        if (dataLog != null && dataLoggingEnabled) {
            dataLog.logData(stringToLog);

        }
    }

    public boolean getCurrentPosition(Position position) {
        return true;
    }

    @Override
    public double getCurrentRotation(AngleUnit unit) {
        return 0;
    }

    public double getCurrentRotationIMU(AngleUnit unit) {
        return unit.fromDegrees(imu.getHeading());
    }

    public boolean getCurrentRobotPosition(RobotPosition position) {
        return true;
    }

    public String getWebcamName() {
        return activeWebcamName;
    }
}

