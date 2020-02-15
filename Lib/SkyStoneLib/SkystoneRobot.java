package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;
import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_40;

public class SkystoneRobot implements FTCRobot {

    public enum HardwareName {

        FRONT_LEFT_MOTOR("FrontLeft"),
        FRONT_RIGHT_MOTOR("FrontRight"),
        BACK_LEFT_MOTOR("BackLeft"),
        BACK_RIGHT_MOTOR("BackRight"),
        IMU("IMU"),
        IMU_2000("IMU_2000"),
        ODOMETRY_MODULE_LEFT("FrontLeft"),
        ODOMETRY_MODULE_RIGHT("FrontRight"),
        ODOMETRY_MODULE_BACK("BackLeft"),
        LIFT_RIGHT_MOTOR("LiftMotorRight"),
        LIFT_RIGHT_NAME("LiftRight"),
        LIFT_RIGHT_EXTENSION_SWITCH("LiftExtensionLimitSwitchRight"),
        LIFT_RIGHT_RETRACTION_SWITCH("LiftRetractionLimitSwitchRight"),
        LIFT_LEFT_MOTOR("LiftMotorLeft"),
        LIFT_LEFT_NAME("LiftLeft"),
        LIFT_LEFT_EXTENSION_SWITCH("LiftExtensionLimitSwitchLeft"),
        LIFT_LEFT_RETRACTION_SWITCH("LiftRetractionLimitSwitchLeft"),
        INTAKE_RIGHT_MOTOR("IntakeMotorRight"),
        INTAKE_LEFT_MOTOR("IntakeMotorLeft"),
        INTAKE_SWITCH_BACK_LEFT("IntakeSwitchBackLeft"),
        INTAKE_SWITCH_BACK_RIGHT("IntakeSwitchBackRight"),
        INTAKE_SWITCH_FRONT_LEFT("IntakeSwitchFrontLeft"),
        INTAKE_SWITCH_FRONT_RIGHT("IntakeSwitchFrontRight"),
        EXT_ARM_SERVO("ExtensionArmServoMotor"),
        EXT_ARM_MOTOR_NAME_FOR_ENCODER_PORT("BackRight"),
        EXT_ARM_RETRACTION_SWITCH("RetractionLimitSwitchArm"),
        EXT_ARM_EXTENSION_SWITCH("ExtensionLimitSwitchArm"),
        GRIPPER_SERVO("Gripper"),
        INTAKE_PUSHER_RIGHT_SERVO("IntakePusherRight"),
        INTAKE_PUSHER_LEFT_SERVO("IntakePusherLeft"),
        GRIPPER_ROTATOR_SERVO("GripperRotator"),
        INTAKE_SWITCH("IntakeLimitSwitch"),
        BASE_MOVER_RIGHT_SERVO("FoundationGrabberRight"),
        BASE_MOVER_LEFT_SERVO("FoundationGrabberLeft"),
        ;

        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM,
        INTAKE_MOTORS,
        INTAKE_PUSHER,
        // these are now part of the IntakeWheels object
        //INTAKE_LIMIT_SW,
        ODOMETRY,
        LIFT,
        EXT_ARM,
        BASE_MOVER
    }

    Set<Subsystem> capabilities;

    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    Map<String, FTCRobotSubsystem> subsystemMap;

    private ElapsedTime timer;
    private LinearOpMode opMode;

    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
    }

    private AdafruitIMU8863 imu;
    private Mecanum mecanum;
    private OdometrySystem odometry;
    // private DualLift dualLift;

    /* TODO: Needs initialization */
    private IntakeWheels intake;
    private DualLift lift;
    private ExtensionArm extensionArm;
    private GripperRotator gripperRotator;
    private Gripper gripper;
    private IntakePusherServos intakePusherServos;
    Switch intakeLimitSwitch;
    private BaseGrabberServo baseGrabberServo;
    private Double deportHeight;

    public SkystoneRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config, DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        enableDataLogging();
        this.opMode = opMode;
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        setCapabilities(Subsystem.values());
        capabilities.remove(Subsystem.INTAKE_PUSHER);
        capabilities.remove(Subsystem.EXT_ARM);
        capabilities.remove(Subsystem.BASE_MOVER);
        capabilities.remove(Subsystem.ODOMETRY);
        capabilities.remove(Subsystem.LIFT);
    }

    /*
     * This function should be called, if needed, before createRobot() call
     */
    public void setCapabilities(Subsystem[] subsystems) {
        capabilities = new HashSet<Subsystem>(Arrays.asList(subsystems));
    }

    @Override
    public boolean createRobot() {
        imu = new AdafruitIMU8863(hardwareMap, null, "IMU", HardwareName.IMU.hwName);
        if (capabilities.contains(Subsystem.MECANUM)) {
            DcMotor8863 frontLeft = new DcMotor8863(HardwareName.FRONT_LEFT_MOTOR.hwName, hardwareMap);
            DcMotor8863 backLeft = new DcMotor8863(HardwareName.BACK_LEFT_MOTOR.hwName, hardwareMap);
            DcMotor8863 frontRight = new DcMotor8863(HardwareName.FRONT_RIGHT_MOTOR.hwName, hardwareMap);
            DcMotor8863 backRight = new DcMotor8863(HardwareName.BACK_RIGHT_MOTOR.hwName, hardwareMap);

            // these motors are orbital (planetary gear) motors. The type of motor sets up the number
            // of encoder ticks per revolution. Since we are not using encoder feedback yet, this is
            // really not important now. But it will be once we hook up the encoders and set a motor
            // mode that uses feedback.
            frontLeft.setMotorType(ANDYMARK_20_ORBITAL);
            backLeft.setMotorType(ANDYMARK_20_ORBITAL);
            frontRight.setMotorType(ANDYMARK_20_ORBITAL);
            backRight.setMotorType(ANDYMARK_20_ORBITAL);


            // This value will get set to some distance traveled per revolution later.
            frontLeft.setMovementPerRev(360);
            backLeft.setMovementPerRev(360);
            frontRight.setMovementPerRev(360);
            backRight.setMovementPerRev(360);

            // The encoder tolerance is used when you give the motor a target encoder tick count to rotate to. 5 is
            // probably too tight. 10 is pretty good based on experience. Note that 10 is set as the
            // default when you create a motor object so this statement is not needed.
            //frontLeft.setTargetEncoderTolerance(10);

            // FLOAT  is also the default when you create a new motor object
            //frontLeft.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);

            // powers are also defaulted to -1 and 1
            //frontLeft.setMinMotorPower(-1);
            //frontLeft.setMaxMotorPower(1);

            // setDirection() is a software control that controls which direction the motor moves when
            // you give it a positive power. We may have to change this once we see which direction the
            // motor actually moves.
            frontLeft.setDirection(DcMotor.Direction.REVERSE);
            backLeft.setDirection(DcMotor.Direction.REVERSE);
            frontRight.setDirection(DcMotor.Direction.FORWARD);
            backRight.setDirection(DcMotor.Direction.FORWARD);

            // set the running mode for the motor. The motor initializes at STOP_AND_RESET_ENCODER which
            // resets the encoder count to zero. After this you have to choose a mode that will allow
            // the motor to run.
            // In this case, we do not have the encoder connected from the motor. So we only have one
            // choice. We must run the motor without any feedback (open loop). This call is not really
            // needed since later I use runAtConstantPower() and that sets the mode too. But since you
            // are coming up to speed on the motors, I put this here for you to see (like my pun?).
            frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            // The other 2 options would be:
            // RUN_TO_POSITION - run until the targeted encoder count is reached using PID
            // RUN_WITH_ENCODER - run at a velocity controlled by a PID
            // For more details, see this page and start reading at Running the motor and continue down
            // https://ftc-tricks.com/dc-motors/

            // Make sure the motor does not start moving. This is not really needed because
            // runAtConstantPower(0) below does the same thing. But I put it here so you can see this
            // call exists.
            frontLeft.setPower(0);
            backLeft.setPower(0);
            frontRight.setPower(0);
            backRight.setPower(0);

            // The runAtConstantPower() and runAtConstantSpeed() methods setup the motor to do that.
            // They are initialzation methods. So they should not be inside the while loop.
            //
            // We can't use runAtConstantSpeed because there is no encoder feedback. I suspect this
            // is why the motor did not turn. runAtConstantSpeed uses the encoder and PID control
            // to turn the motor at a constant velocity.
            //frontLeft.runAtConstantSpeed(mecanum.getFrontLeft());
            //
            // Instead we will run the motor open loop (without controlling its speed, just feeding
            // it a power. Initialize the motor power to 0 for now.
            frontLeft.runAtConstantPower(0);
            backLeft.runAtConstantPower(0);
            frontRight.runAtConstantPower(0);
            backRight.runAtConstantPower(0);
            mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight, telemetry);
        }
        if (capabilities.contains(Subsystem.ODOMETRY)) {
            OdometryModule left = new OdometryModule(1440, 3.8, units, HardwareName.ODOMETRY_MODULE_LEFT.hwName, hardwareMap);
            OdometryModule right = new OdometryModule(1440, 3.8, units, HardwareName.ODOMETRY_MODULE_RIGHT.hwName, hardwareMap);
            OdometryModule back = new OdometryModule(1440, 3.8, units, HardwareName.ODOMETRY_MODULE_BACK.hwName, hardwareMap);
            odometry = new OdometrySystem(units, left, right, back);
            subsystemMap.put(odometry.getName(), odometry);
        }

        // My preference is to encapsulate as much as possible so that creation code can be reused.
        // So move this stuff into the IntakeWheels. It should know how to create itself. I should
        // not have to know that at the robot level.

//        DcMotor8863 rightIntake = new DcMotor8863("intakeMotorRight", hardwareMap);
//        DcMotor8863 leftIntake = new DcMotor8863("intakeMotorLeft", hardwareMap);
//        rightIntake.setMotorType(ANDYMARK_20_ORBITAL);
//        leftIntake.setMotorType(ANDYMARK_20_ORBITAL);

        if (capabilities.contains(Subsystem.INTAKE_MOTORS)) {
            intake = new IntakeWheels(hardwareMap,
                    HardwareName.INTAKE_RIGHT_MOTOR.hwName,
                    HardwareName.INTAKE_LEFT_MOTOR.hwName,
                    HardwareName.INTAKE_SWITCH_BACK_LEFT.hwName,
                    HardwareName.INTAKE_SWITCH_BACK_RIGHT.hwName,
                    HardwareName.INTAKE_SWITCH_FRONT_LEFT.hwName,
                    HardwareName.INTAKE_SWITCH_FRONT_RIGHT.hwName);
            subsystemMap.put(intake.getName(), intake);
            // the intake switches are now part of the IntakeWheels class
//            if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW)) {
//                intakeLimitSwitch = new Switch(hardwareMap,
//                        HardwareName.INTAKE_SWITCH.hwName,
//                        Switch.SwitchType.NORMALLY_OPEN);
//            }
        }

        if (capabilities.contains(Subsystem.BASE_MOVER)) {
            baseGrabberServo = new BaseGrabberServo(hardwareMap,
                    HardwareName.BASE_MOVER_RIGHT_SERVO.hwName,
                    HardwareName.BASE_MOVER_LEFT_SERVO.hwName, telemetry);
            subsystemMap.put(baseGrabberServo.getName(), baseGrabberServo);

        }

        if (capabilities.contains(Subsystem.LIFT)) {
            lift = new DualLift(hardwareMap,
                    HardwareName.LIFT_RIGHT_NAME.hwName,
                    HardwareName.LIFT_RIGHT_MOTOR.hwName,
                    HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName,
                    HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName,
                    HardwareName.LIFT_LEFT_NAME.hwName,
                    HardwareName.LIFT_LEFT_MOTOR.hwName,
                    HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName,
                    HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName,
                    telemetry);
            subsystemMap.put(lift.getName(), lift);
        }

        if (capabilities.contains(Subsystem.EXT_ARM)) {

            // Extension Arm
            extensionArm = new ExtensionArm(hardwareMap, telemetry,
                    ExtensionArmConstants.mechanismName,
                    SkystoneRobot.HardwareName.EXT_ARM_EXTENSION_SWITCH.hwName,
                    SkystoneRobot.HardwareName.EXT_ARM_RETRACTION_SWITCH.hwName,
                    SkystoneRobot.HardwareName.EXT_ARM_MOTOR_NAME_FOR_ENCODER_PORT.hwName,
                    ExtensionArmConstants.motorType,
                    ExtensionArmConstants.movementPerRevolution);
            subsystemMap.put(extensionArm.getName(), extensionArm);

            // Gripper
            gripper = new Gripper(hardwareMap, HardwareName.GRIPPER_SERVO.hwName, telemetry);
            subsystemMap.put(gripper.getName(), gripper);

            // GripperRotator
            gripperRotator = new GripperRotator(hardwareMap, HardwareName.GRIPPER_ROTATOR_SERVO.hwName, telemetry);
            subsystemMap.put(gripperRotator.getName(), gripperRotator);
        }

        //Intake pusher servo
        if (capabilities.contains(Subsystem.INTAKE_PUSHER)) {
            /*
            intakePusherServos = new IntakePusherServos(
                    hardwareMap,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    telemetry);
            subsystemMap.put(intakePusherServos.getName(), intakePusherServos);
             */
        }

        //Intake pusher servo
       /* if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW)) {
            intakePusherServos = new IntakePusherServos(
                    hardwareMap,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    telemetry);
            subsystemMap.put(intakePusherServos.getName(), intakePusherServos);
        }*/
        init();
        return true;
    }

    /**
     * Every system has an init. Call it.
     */
    @Override
    public void init() {
        dataLog.logData("Init starting");
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.setDataLog(dataLog);
            subsystem.enableDataLogging();
            if (!subsystem.init(config)) {
                if (dataLoggingEnabled)
                    dataLog.logData(subsystem.getName() + " initialization failed");
            }
        }

        // inits for the command state machines
        initDeportStateMachine();
        initLiftBlockStateMachine();
        initPlaceBlockStateMachine();
        initPrepareBlockStateMachine();

        // wait until all the updates are complete or until the timer has expired
        timer.reset();
        while (!isInitComplete()) {
            update();

            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                //How cheerful. How comforting...
                break;
            }
            telemetry.update();
            opMode.idle();
        }
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
        // put the isInitComplete for each subsystem here. In other words repeat this block of code
        // for each subsystem
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            if (subsystem.isInitComplete()) {
                if (dataLoggingEnabled) {
                    dataLog.logData("Init complete for " + subsystem.getName());
                }

            } else {
                dataLog.logData("Init is not complete for " + subsystem.getName());
            }
            result &= subsystem.isInitComplete();
        }
        if (dataLoggingEnabled && result == true) {
            dataLog.logData("Init complete");
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

        deportStateUpdate();
        liftBlockStateUpdate();
        placeBlockStateUpdate();
        prepareIntakeUpdate();


//        if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW))
//            updateIntakeSwitches();
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
        dataLoggingEnabled = true;
    }

    /**
     * For each subsystem that supports logging, turn it off
     */
    public void disableDataLogging() {
        dataLoggingEnabled = false;

    }

    private void log(String stringToLog) {
        if (dataLog != null && dataLoggingEnabled) {
            dataLog.logData(stringToLog);

        }
    }

    public boolean getCurrentPosition(Position position) {
        if (odometry != null && odometry.isInitComplete()) {
            odometry.getCurrentPosition(position);
            return true;
        } else {
            return false;
        }
    }

    /*
     * Return current robot rotation. If odometry is initialized odometry is used, otherwise IMU is used
     */
    @Override
    public double getCurrentRotation(AngleUnit unit) {
        if (odometry != null && odometry.isInitComplete())
            return odometry.getCurrentRotation(unit);
        else if (imu != null)
            return -unit.fromDegrees(imu.getHeading());
        else
            return 0;
    }

    public void setMovement(MecanumCommands commands) {
        mecanum.setMotorPower(commands);
    }

    //********************************************
    //********************************************
    //State Machines for robot commands
    //********************************************
    //********************************************
    ///////////////NOTE the state machines have been edited to run without moving the lift////////////////


    //*********************************************
    //INTAKE//
    //********************************************

    public void intakeBlock() {
        if (intake != null)
            intake.intake();
    }

    public boolean isIntakeBlockComplete() {
        if (intake != null)
            return intake.isIntakeComplete();
        else
            return false;
    }

    public void intakeOff() {
        if (intake != null)
            intake.stop();
    }

    public void intakeSpitOut() {
        if (intake != null)
            intake.outtake();
    }

    public boolean setIntakeStateAfterOuttake(IntakeWheels.IntakeStates state) {
        if (intake != null)
            return intake.setStateAfterOuttake(state);
        else
            return false;
    }

    //*********************************************
    //BLOCK GRIPPING//
    //********************************************

    public void gripBlock() {
        if (gripper != null)
            gripper.gripBlock();
    }

    public boolean isGripBlockComplete() {
        if (gripper != null)
            return gripper.isGripComplete();
        else
            return false;
    }

    public Gripper.State getCurrentGripperState() {
        if (gripper != null)
            return gripper.getGripperState();
        else
            return Gripper.State.IDLE;
    }

    /*
    public enum GripStates {
        IDLE,
        START,
        GRIPPING,
        COMPLETE
    }

    private GripStates gripState = GripStates.IDLE;

    public void initGripStateMachine() {
        gripState = GripStates.IDLE;
    }

    public void gripBlock() {
        gripState = GripStates.START;
    }

    public void gripStateUpdate() {
        switch (gripState) {
            case IDLE:
                //nothing just chilling
                break;
            case START:
                gripper.grip();
                gripState = GripStates.GRIPPING;
                break;

            case GRIPPING:
                if (gripper.isGripComplete()) {
                    gripState = GripStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
    }

    public boolean isGripBlockComplete() {
        if (gripState == GripStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public GripStates getCurrentGripperState() {
        return gripState;
    }
    */


    //*********************************************
    //BLOCK DEPORTATION//
    //********************************************
    private ElapsedTime deportTimer;

    public enum DeportStates {
        IDLE,
        START,
        LIFT_RAISING,
        ARM_EXTENDING,
        GRIPPER_ROTATING,
        COMPLETE
    }

    private DeportStates deportState = DeportStates.IDLE;
    private DeportStates previousDeportState;

    private void logState(DeportStates deportState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (deportState != previousDeportState) {
                dataLog.logData("Deport state is now ", deportState.toString());
                previousDeportState = deportState;
            }
        }
    }

    public void initDeportStateMachine() {
        deportState = DeportStates.IDLE;
    }


    public void deportBlock() {
        if (deportState == DeportStates.IDLE || deportState == DeportStates.COMPLETE) {
            deportHeight = 6.0;
            log("Robot commanded to deport stone");
            deportState = DeportStates.START;
        }
    }

    public void deportBlockCapstone() {
        //Special Secret Sauce Height//
        if (deportState == DeportStates.IDLE || deportState == DeportStates.COMPLETE) {
            deportHeight = 10.0;
            log("Robot commanded to deport stone(capstone)");
            deportState = DeportStates.START;
        } else {
            log("Robot command to deport stone IGNORED");
        }
    }


    public void deportStateUpdate() {
        switch (deportState) {
            case IDLE:
                //nothing just chilling
                break;
            case START:
                deportState = DeportStates.LIFT_RAISING;
                if (lift != null)
                    lift.goToPosition(deportHeight, 1);
                break;
            case LIFT_RAISING:
                if (lift != null)
                    if (lift.isPositionReached()) {
                        if (extensionArm != null)
                            extensionArm.goToPosition(5, 1);
                        deportState = DeportStates.ARM_EXTENDING;
                    }
            case ARM_EXTENDING:
                if (extensionArm != null && extensionArm.isPositionReached()) {
                    if (gripperRotator != null)
                        gripperRotator.rotateOutward();
                    deportState = DeportStates.GRIPPER_ROTATING;
                }
                break;
            case GRIPPER_ROTATING:
                if (gripperRotator != null && gripperRotator.isRotateOutwardComplete()) {
                    deportState = DeportStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
        logState(deportState);
    }

    public boolean isDeportBlockComplete() {
        if (deportState == DeportStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public DeportStates getCurrentDeportState() {
        return deportState;
    }

    //*********************************************
    //BLOCK LIFTING//
    //********************************************
    private ElapsedTime liftBlockTimer;

    public enum LiftBlockStates {
        IDLE,
        START,
        BLOCK_LIFTING,
        COMPLETE
    }

    private LiftBlockStates liftBlockState = LiftBlockStates.IDLE;
    private LiftBlockStates previousliftBlockState;

    private void logState(LiftBlockStates liftBlockState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (liftBlockState != previousliftBlockState) {
                dataLog.logData("LiftBlock state is now ", liftBlockState.toString());
                previousliftBlockState = liftBlockState;
            }
        }
    }

    private double liftBlockTimerLimit;

    int skyscraperLevel = 0;

    public void resetSkyscraperLevel() {
        log("Robot commanded to reset skyscraper level");
        skyscraperLevel = 0;
    }

    public int getSkyscraperLevel() {
        return skyscraperLevel;
    }

    public void increaseDesiredHeightForLift() {
        log("Robot commanded to increase skyscraper level");
        skyscraperLevel = skyscraperLevel + 1;
        if (skyscraperLevel > 8) {
            skyscraperLevel = 0;
            telemetry.addData("DESIRED HEIGHT =", skyscraperLevel);
        }
        log("Robot commanded to change skyscraper level = " + skyscraperLevel);
    }

    public void initLiftBlockStateMachine() {
        liftBlockTimer = new ElapsedTime();
        liftBlockState = LiftBlockStates.IDLE;
    }


    public void liftBlock() {
        if (liftBlockState == LiftBlockStates.IDLE || liftBlockState == LiftBlockStates.COMPLETE) {
            log("Robot commanded to lift stone");
            liftBlockState = LiftBlockStates.START;
        } else {
            log("Robot command to lift stone IGNORED");
        }
    }

    public void liftBlockStateUpdate() {
        switch (liftBlockState) {
            case IDLE:
                skyscraperLevel = 3;
                //nothing just chilling
                break;
            case START:
                if (lift != null)
                    lift.goToBlockHeights(skyscraperLevel);
                liftBlockState = LiftBlockStates.BLOCK_LIFTING;
                break;
            case BLOCK_LIFTING:
                if (lift != null && lift.isPositionReached()) {
                    liftBlockState = LiftBlockStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
        logState(liftBlockState);
    }

    public boolean isLiftBlockComplete() {
        if (liftBlockState == LiftBlockStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public LiftBlockStates getCurrentLiftState() {
        return liftBlockState;
    }

    //*********************************************
    //BLOCK PLACING//
    //********************************************
    private ElapsedTime placeBlockTimer;

    public enum PlaceBlockStates {
        IDLE,
        EXTENDING,
        GRIPPER_RELEASING,
        COMPLETE
    }

    private PlaceBlockStates placeBlockState = PlaceBlockStates.IDLE;
    private PlaceBlockStates previousPlaceBlockState;

    private void logState(PlaceBlockStates placeBlockState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (placeBlockState != previousPlaceBlockState) {
                dataLog.logData("PlaceBlock state is now ", placeBlockState.toString());
                previousPlaceBlockState = placeBlockState;
            }
        }
    }

    private double placeBlockTimerLimit;

    public void initPlaceBlockStateMachine() {
        placeBlockTimer = new ElapsedTime();
        placeBlockState = PlaceBlockStates.IDLE;
    }


    public void placeBlock() {
        if (placeBlockState == PlaceBlockStates.IDLE.IDLE || placeBlockState == PlaceBlockStates.COMPLETE) {
            log("Robot commanded to place stone");
            placeBlockState = PlaceBlockStates.EXTENDING;
        } else {
            log("Robot command to place stone IGNORED");
        }
    }


    public void placeBlockStateUpdate() {
        switch (placeBlockState) {
            case IDLE:
                //The Driver will extend the arm using joystick then call place block
                break;
            case EXTENDING:
                if (gripper != null)
                    gripper.releaseBlock();
                break;
            case GRIPPER_RELEASING:
                if (gripper != null && gripper.isReleaseComplete()) {
                    placeBlockState = PlaceBlockStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
        logState(placeBlockState);
    }

    public boolean isPlaceBlockComplete() {
        if (placeBlockState == PlaceBlockStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public PlaceBlockStates getCurrentPlaceBlockState() {
        return placeBlockState;
    }

    //*********************************************
    //Prepare to intake//
    //********************************************

    public enum PrepareIntakeStates {
        IDLE,
        START,
        PREPARATION_PHASE_1_ROTATOR,
        PREPARATION_PHASE_2_RETRACTION,
        PREPARATION_PHASE_3_LOWERING,
        COMPLETE
    }

    private PrepareIntakeStates prepareIntakeState = PrepareIntakeStates.IDLE;
    private PrepareIntakeStates previousPrepareIntakeState;

    private void logState(PrepareIntakeStates prepareIntakeState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (prepareIntakeState != previousPrepareIntakeState) {
                dataLog.logData("PlaceBlock state is now ", prepareIntakeState.toString());
                previousPrepareIntakeState = prepareIntakeState;
            }
        }
    }

    public void initPrepareBlockStateMachine() {
        prepareIntakeState = PrepareIntakeStates.IDLE;
    }


    public void prepareToIntakeBlock() {
        if (prepareIntakeState == PrepareIntakeStates.IDLE || prepareIntakeState == PrepareIntakeStates.COMPLETE) {
            log("Robot commanded to prepare to intake a stone");
            prepareIntakeState = PrepareIntakeStates.START;
        } else {
            log("Robot command to prepare to intake IGNORED");
        }
    }


    public void prepareIntakeUpdate() {
        switch (prepareIntakeState) {
            case IDLE:
                //chillin' like a villain
                break;
            case START:
                if (gripperRotator != null)
                    gripperRotator.rotateInward();
                if (gripper != null)
                    gripper.releaseBlock();
                prepareIntakeState = PrepareIntakeStates.PREPARATION_PHASE_1_ROTATOR;
                break;
            case PREPARATION_PHASE_1_ROTATOR:
                if (gripper != null && gripper.isReleaseComplete()
                        && gripperRotator != null && gripperRotator.isRotateInwardComplete()) {
                    if (extensionArm != null)
                        extensionArm.goToPosition(0, 1);
                    prepareIntakeState = PrepareIntakeStates.PREPARATION_PHASE_2_RETRACTION;
                }
                break;
            case PREPARATION_PHASE_2_RETRACTION:
                if (extensionArm != null && extensionArm.isPositionReached()) {
                    if (lift != null)
                        lift.goToPosition(0, 1);
                    prepareIntakeState = PrepareIntakeStates.PREPARATION_PHASE_3_LOWERING;
                }
                break;
            case PREPARATION_PHASE_3_LOWERING:
                if (lift != null && lift.isPositionReached() && extensionArm != null && extensionArm.isPositionReached()) {
                    prepareIntakeState = PrepareIntakeStates.COMPLETE;
                }
                break;

            case COMPLETE:
                //we chillin'
                break;
        }
        logState(prepareIntakeState);
    }

    public boolean isPrepareIntakeComplete() {
        if (prepareIntakeState == PrepareIntakeStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public PrepareIntakeStates getCurrentPrepareIntakeState() {
        return prepareIntakeState;
    }

    public void baseGrab() {
        if (baseGrabberServo != null) {
            log("Robot commanded to grab foundation");
            baseGrabberServo.grabBase();
        }
    }

    public void baseRelease() {
        if (baseGrabberServo != null) {
            log("Robot commanded to release foundation");
            baseGrabberServo.releaseBase();
        }
    }

}
