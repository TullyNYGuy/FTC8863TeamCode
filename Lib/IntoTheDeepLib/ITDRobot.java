package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageDeliveryController;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageHangMechanism;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageIntakeController;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageMecanumDrive;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePlaneGUNservo;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageRobotModes;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoopTimer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ITDRobot implements FTCRobot {

    public enum HardwareName {
        IMU("imu"),

        FRONT_LEFT_DRIVE_MOTOR("leftFrontMotor"),
        FRONT_RIGHT_DRIVE_MOTOR("rightFrontMotor"),
        REAR_LEFT_DRIVE_MOTOR("leftRearMotor"),
        REAR_RIGHT_DRIVE_MOTOR("rightRearMotor"),
        MECANUM_DRIVE("mecanumDrive"),

        EXTENSION_ARM("extensionArm"),
        EXTENSION_ARM_RETRACTION_LIMIT_SWITCH("extensionArmRetractionLimitSwitch"),
        EXTENSION_ARM_EXTENSION_LIMIT_SWITCH("extensionArmExtensionLimitSwitch"),
        EXTENSION_ARM_MOTOR("extensionArmMotor"),

//
//        WEBCAM("Webcam"),

        INTAKE_ARM_SERVO("intakeArmServo"),

        BUCKET_ARM_SERVO("bucketArmServo"),
        BUCKET_GATE_SERVO("bucketGateServo"),

        LIFT("lift"),
        LIFT_MOTOR("liftMotor"),
        LIFT_LIMIT_SWITCH_RETRACTION("liftRetractionLimitSwitch"),
        LIFT_LIMIT_SWITCH_EXTENSION("liftExtensionLimitSwitch"),

        EXTENSION_ARM_INTAKE_CONTROLLER("extensionArmIntakeController"),
        LIFT_BUCKET_ARM_BUCKET_GATE_CONTROLLER("liftBucketArmBucketGateController"),
        INTAKE_BUCKET_CONTROLLER("intakeBucketController"),

        PLANE_GUN_SERVO("planeGunServo"),

        HANG_MECHANISM("hangMechanism"),
        LEFT_HANG_MOTOR("leftHangMotor"),
        RIGHT_HANG_MOTOR("rightHangMotor"),
        LEFT_DEPLOY_SERVO("armDeployServoLeft"),
        RIGHT_DEPLOY_SERVO("armDeployServoRight");


//        LED_PORT1("ledPort1"),
//        LED_PORT2("ledPort2"),
//        LED_STRIP("ledStrip");

        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM_DRIVE,
        EXTENSION_ARM_INTAKE_CONTROLLER,
        LIFT_BUCKET_ARM_BUCKET_GATE_CONTROLLER,
        INTAKE_BUCKET_CONTROLLER,
        HANG_MECHANISM

        //LED_BLINKER,
        //LED_STRIP,
    }

    Set<Subsystem> capabilities;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    private MatchPhase matchPhase;
    Map<String, FTCRobotSubsystem> subsystemMap;
    private ElapsedTime timer;
    private LinearOpMode opMode;


    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    private AdafruitIMU8863 imu;
    public ITDMecanumDrive mecanumDrive;
    public ITDExtensionArmIntakeController extensionArmIntakeController;
    public ITDLiftBucketArmBucketGateController liftBucketArmBucketGateController;
    public ITDIntakeBucketController intakeBucketController;
    public LoopTimer loopTimer;
    public ITDRobotModes robotModes;
    public ITDHangMechanism hangMechanism;
    //public PowerPlayWebcam webcam;
    //public RevLEDBlinker ledBlinker;
    //public FFBlinkinLed ledStrip;

    public ITDRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config,
                    DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        loopTimer = new LoopTimer();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        this.opMode = opMode;
        // sets up robot driving modes (normal/inverse) and drive train power
        robotModes = new ITDRobotModes();
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

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
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
            mecanumDrive = new ITDMecanumDrive(
                    ITDRobot.HardwareName.FRONT_LEFT_DRIVE_MOTOR.hwName,
                    ITDRobot.HardwareName.REAR_LEFT_DRIVE_MOTOR.hwName,
                    ITDRobot.HardwareName.FRONT_RIGHT_DRIVE_MOTOR.hwName,
                    ITDRobot.HardwareName.REAR_RIGHT_DRIVE_MOTOR.hwName,
                    hardwareMap);
            subsystemMap.put(mecanumDrive.getName(), mecanumDrive);
        }

        if (capabilities.contains(Subsystem.INTAKE_BUCKET_CONTROLLER)) {
            intakeBucketController = new ITDIntakeBucketController(hardwareMap, telemetry);
            subsystemMap.put(intakeBucketController.getName(), intakeBucketController);
        }

        if (capabilities.contains(Subsystem.EXTENSION_ARM_INTAKE_CONTROLLER)) {
            extensionArmIntakeController = new ITDExtensionArmIntakeController(hardwareMap, telemetry);
            subsystemMap.put(extensionArmIntakeController.getName(), extensionArmIntakeController);
            extensionArmIntakeController.setIntakeBucketController(intakeBucketController);
            intakeBucketController.setExtensionArmIntakeController(extensionArmIntakeController);
        }

        if (capabilities.contains(Subsystem.LIFT_BUCKET_ARM_BUCKET_GATE_CONTROLLER)) {
            liftBucketArmBucketGateController = new ITDLiftBucketArmBucketGateController(hardwareMap, telemetry);
            subsystemMap.put(liftBucketArmBucketGateController.getName(), liftBucketArmBucketGateController);
            liftBucketArmBucketGateController.setIntakeBucketController(intakeBucketController);
            intakeBucketController.setLiftBucketArmBucketGateController(liftBucketArmBucketGateController);
        }

        // Only setup and init the camera if this is autonomous. It takes up CPU and memory and is not needed in teleop.
        // Note that this does not actually start the camera streaming. The autonomous opmode must do that because it
        // needs to set the pipeline for the camera.
//        if (capabilities.contains(Subsystem.WEBCAM) && matchPhase == MatchPhase.AUTONOMOUS) {
//            webcam = new PowerPlayWebcam(hardwareMap, telemetry, HardwareName.WEBCAM.hwName);
//            subsystemMap.put(webcam.getName(), webcam);
//        }

        if (capabilities.contains(Subsystem.HANG_MECHANISM)) {
            hangMechanism = new ITDHangMechanism(hardwareMap, telemetry);
            subsystemMap.put(hangMechanism.getName(), hangMechanism);
        }

//            armServo = new CenterStageArmServo(hardwareMap, telemetry);
//            armServo.intakePosition();
//            wristServo = new CenterStageWristServo(hardwareMap, telemetry);
//            wristServo.intakePosition();

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

    public void setAllianceColor(AllianceColor color) {
        extensionArmIntakeController.setupAllianceColor(color);
    }

}

