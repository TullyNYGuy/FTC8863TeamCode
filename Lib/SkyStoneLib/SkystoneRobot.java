package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

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
        ODOMETRY_MODULE_LEFT("BackLeft"),
        ODOMETRY_MODULE_RIGHT("BackRight"),
        ODOMETRY_MODULE_BACK("FrontRight"),
        LIFT_RIGHT_MOTOR("LiftMotortRight"),
        LIFT_RIGHT_ENCODER("LiftRight"),
        LIFT_RIGHT_EXTENSION_SWITCH("LiftExtensionLimitSwitchRight"),
        LIFT_RIGHT_RETRACTION_SWITCH("LiftRetractionLimitSwitchRight"),
        LIFT_LEFT_MOTOR("LiftMotortLeft"),
        LIFT_LEFT_ENCODER("LiftLeft"),
        LIFT_LEFT_EXTENSION_SWITCH("LiftExtensionLimitSwitchLeft"),
        LIFT_LEFT_RETRACTION_SWITCH("LiftRetractionLimitSwitchLeft"),
        INTAKE_RIGHT_MOTOR("IntakeMotorRight"),
        INTAKE_LEFT_MOTOR("IntakeMotorLeft"),
        EXT_ARM_SERVO("ExtensionArmServoMotor"),
        EXT_ARM_ENCODER("ExtensionArmEncoder"),
        EXT_ARM_RETRACTION_SWITCH("RetractionLimitSwitchArm"),
        EXT_ARM_EXTENSION_SWITCH("ExtensionLimitSwitchArm"),
        GRIPPER_SERVO("gripper"),
        INTAKE_PUSHER_RIGHT_SERVO("intakePusherRight"),
        INTAKE_PUSHER_LEFT_SERVO("intakePusherLeft"),
        GRIPPER_ROTATOR_SERVO("gripperRotator"),
        INTAKE_SWITCH("intakeLimitSwitch"),
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
        INTAKE_LIMIT_SW,
        ODOMETRY,
        LIFT,
        EXT_ARM
    }

    Subsystem[] currentCaps = new Subsystem[]{Subsystem.MECANUM, Subsystem.INTAKE_MOTORS, Subsystem.INTAKE_LIMIT_SW};

    Set<Subsystem> capabilities = new HashSet<Subsystem>(Arrays.asList(currentCaps));

    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    Map<String, FTCRobotSubsystem> subsystemMap;

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
    private DualLift dualLift;

    /* TODO: Needs initialization */
    private IntakeWheels intake;
    private DualLift lift;
    private ExtensionArm extensionArm;
    private GripperRotator gripperRotator;
    private Gripper gripper;
    private IntakePusherServos intakePusherServos;
    Switch intakeLimitSwitch;

    public SkystoneRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config, DataLogging dataLog, DistanceUnit units) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        Set<Subsystem> capabilities = new HashSet<Subsystem>();
        capabilities.addAll(new ArrayList<Subsystem>());
    }

    boolean createRobot() {
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
                    HardwareName.INTAKE_LEFT_MOTOR.hwName);
            subsystemMap.put(intake.getName(), intake);
            if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW)) {
                intakeLimitSwitch = new Switch(hardwareMap,
                        HardwareName.INTAKE_SWITCH.hwName,
                        Switch.SwitchType.NORMALLY_OPEN);
            }
        }

        if (capabilities.contains(Subsystem.LIFT)) {
            dualLift = new DualLift(hardwareMap,
                    HardwareName.LIFT_RIGHT_ENCODER.hwName,
                    HardwareName.LIFT_RIGHT_MOTOR.hwName,
                    HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName,
                    HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName,
                    HardwareName.LIFT_LEFT_ENCODER.hwName,
                    HardwareName.LIFT_LEFT_MOTOR.hwName,
                    HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName,
                    HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName,
                    telemetry);
            subsystemMap.put(dualLift.getName(), dualLift);
        }
        if (capabilities.contains(Subsystem.EXT_ARM)) {

            // Extension Arm
            extensionArm = new ExtensionArm(hardwareMap, telemetry, HardwareName.EXT_ARM_SERVO.hwName, HardwareName.EXT_ARM_EXTENSION_SWITCH.hwName, HardwareName.EXT_ARM_RETRACTION_SWITCH.hwName, HardwareName.EXT_ARM_ENCODER.hwName, ANDYMARK_40, 2.75 * Math.PI * 2);
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
            intakePusherServos = new IntakePusherServos(
                    hardwareMap,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    telemetry);
            subsystemMap.put(intakePusherServos.getName(), intakePusherServos);
        }

        //Intake pusher servo
        if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW)) {
            intakePusherServos = new IntakePusherServos(
                    hardwareMap,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    HardwareName.INTAKE_PUSHER_RIGHT_SERVO.hwName,
                    telemetry);
            subsystemMap.put(intakePusherServos.getName(), intakePusherServos);
        }
        return true;
    }

    /**
     * Every system has an init. Call it.
     */
    public void init(Configuration config) {
        dataLog.logData("Init starting");
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            if (!subsystem.init(config)) {
                if (dataLoggingEnabled)
                    dataLog.logData(subsystem.getName() + " initialization failed");
            }
        }
        // put the init() method for each subsytem here
    }

    /**
     * Every system must tell us when its init is complete. When all of the inits are complete, the
     * robot init is complete.
     *
     * @return
     */
    public boolean isInitComplete() {
        boolean result = true;
        // put the isInitComplete for each subsystem here. In other words repeat this block of code
        // for each subsystem
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
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
    public void update() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.update();
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        intakeBlockUpdate();
        gripStateUpdate();
        deportStateUpdate();
        liftBlockStateUpdate();
        placeBlockStateUpdate();
        prepareIntakeUpdate();
        if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW))
            updateIntakeSwitches();
    }

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

    public void getCurrentPosition(Position position) {
        odometry.getCurrentPosition(position);
    }

    /*
     * Return current robot rotation. If odometry is initialized odometry is used, otherwise IMU is used
     */
    @Override
    public double getCurrentRotation(AngleUnit unit) {
        if (odometry != null && odometry.isInitComplete())
            return odometry.getCurrentRotation(unit);
        else
            return unit.fromDegrees(imu.getHeading());
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
    public ElapsedTime intakeTimer;

    public enum IntakeStates {
        IDLE,
        START,
        LIFT_MOVING_TO_POSITION,
        INTAKE_ON,
        OUTTAKE
    }

    private IntakeStates intakeState = IntakeStates.IDLE;

    public void intakeBlock() {
        intakeState = IntakeStates.START;
    }

    public void intakeBlockUpdate() {
        switch (intakeState) {
            case IDLE:
                break;
            case START:
                //lift.goToPosition(2, 1);
                intakeState = IntakeStates.LIFT_MOVING_TO_POSITION;
                break;
            case LIFT_MOVING_TO_POSITION:
                //if (lift.isPositionReached()) {
                intake.intake();
                intakeState = IntakeStates.INTAKE_ON;
                //  }
                break;
            case INTAKE_ON:
                //Do nothing
                break;
            case OUTTAKE:

                if (intakeTimer.milliseconds() > 2000) {
                    intakeOff();
                }
                break;
        }
    }

    public void intakeOff() {
        intake.stop();
        intakeState = IntakeStates.IDLE;
    }

    public void intakeSpitOut() {
        intake.outtake();
        intakeTimer.reset();
        intakeState = IntakeStates.OUTTAKE;
    }

    public void updateIntakeSwitches() {
        boolean intakeSwitchPressed = false;
        if (intakeLimitSwitch != null && intakeLimitSwitch.isPressed()) {
            intakeSwitchPressed = true;
        }
        if (intakeSwitchPressed)
            intake.stop();

        if (intakeSwitchPressed) {
            telemetry.addLine("intake limit switch pressed");
        } else {
            telemetry.addLine("intake limit switch NOT pressed");
        }
    }
    //*********************************************
    //BLOCK GRIPPING//
    //********************************************
    private ElapsedTime gripTimer;

    public enum GripStates {
        IDLE,
        START,
        PUSHER_ARMS_MOVING,
        GRIPPING,
        COMPLETE
    }

    private GripStates gripState = GripStates.IDLE;

    private double gripTimerLimit = 1000;

    public void gripBlock() {
        gripState = GripStates.START;
    }

    public void gripStateUpdate() {
        switch (gripState) {
            case IDLE:
                //nothing just chilling
                break;
            case START:
                intakePusherServos.pushIn();
                gripState = GripStates.PUSHER_ARMS_MOVING;
                break;
            case PUSHER_ARMS_MOVING:
                if (intakePusherServos.isPushComplete()) {
                    gripper.grip();
                }
                break;
            case GRIPPING:
                if (gripper.IsGripComplete()) {
                    gripState = GripStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
    }

    public boolean isGripComplete() {
        if (gripState == GripStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }


    //*********************************************
    //BLOCK DEPORTATION//
    //********************************************
    private ElapsedTime deportTimer;

    public enum DeportStates {
        IDLE,
        START,
        ARM_EXTENDING,
        GRIPPER_ROTATING,
        LIFT_LOWERING,
        COMPLETE
    }

    private DeportStates deportState = DeportStates.IDLE;

    private double deportTimerLimit = 1000;

    public void deportBlock() {
        deportState = DeportStates.START;
    }


    public void deportStateUpdate() {
        switch (deportState) {
            case IDLE:
                //nothing just chilling
                break;
            case START:
                deportTimer.reset();
                deportState = DeportStates.ARM_EXTENDING;
                ///////////////Ask about inches vs centimeters the method asks for inch////////////
                extensionArm.goToPosition(21, 1);
                break;
            case ARM_EXTENDING:
                if (deportTimer.milliseconds() > deportTimerLimit) {
                    gripperRotator.rotateOutward();
                    deportTimer.reset();
                }
                break;
            case GRIPPER_ROTATING:
                if (deportTimer.milliseconds() > 500) {
                    deportState = DeportStates.LIFT_LOWERING;
                    ////////////////ASK MR  BALL ABOUT HOME POSITION////////////////
                    // lift.goToPosition(1, 1);
                    deportTimer.reset();
                }
                break;
            case LIFT_LOWERING:
                //if (lift.isPositionReached()) {
                deportState = DeportStates.COMPLETE;
                //}
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
    }

    public boolean isDeportBlockComplete() {
        if (deportState == DeportStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
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

    private double liftBlockTimerLimit;

    private int skyscraperLevel = 0;

    public void increaseDesiredHeightForLift() {
        skyscraperLevel = skyscraperLevel + 1;
        if (skyscraperLevel > 8) {
            skyscraperLevel = 0;
            telemetry.addData("DESIRED HEIGHT =", skyscraperLevel);
        }
    }

    public void liftBlock(int skyscraperLevel) {
        liftBlockState = LiftBlockStates.START;
    }

    public void liftBlockStateUpdate() {
        switch (liftBlockState) {
            case IDLE:
                //nothing just chilling
                break;
            case START:
                //lift.goToBlockHeights(skyscraperLevel);
                liftBlockTimer.reset();
                liftBlockState = LiftBlockStates.BLOCK_LIFTING;
                break;
            case BLOCK_LIFTING:
                //if (lift.isPositionReached()) {
                liftBlockState = LiftBlockStates.COMPLETE;
                //}
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
    }

    public boolean isLiftBlockComplete() {
        if (liftBlockState == LiftBlockStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
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

    private double placeBlockTimerLimit;


    public void placeBlock() {
        placeBlockState = PlaceBlockStates.EXTENDING;
    }


    public void placeBlockStateUpdate() {
        switch (placeBlockState) {
            case IDLE:
                //The Driver will extend the arm using joystick then call place block
                break;
            case EXTENDING:
                gripper.release();
                break;
            case GRIPPER_RELEASING:
                if (gripper.isReleaseComplete()) {
                    placeBlockState = PlaceBlockStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
    }

    public boolean isPlaceBlockComplete() {
        if (placeBlockState == PlaceBlockStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************
    //Prepare to intake//
    //********************************************

    public enum PrepareIntakeStates {
        IDLE,
        START,
        PREPARING,
        COMPLETE
    }

    private PrepareIntakeStates prepareIntakeState = PrepareIntakeStates.IDLE;


    public void prepareToIntakeBlock() {
        prepareIntakeState = PrepareIntakeStates.START;
    }


    public void prepareIntakeUpdate() {
        switch (prepareIntakeState) {
            case IDLE:
                //chillin' like a villain
                break;
            case START:
                extensionArm.goToPosition(0, 1);
                lift.goToPosition(0, 1);
                prepareIntakeState = PrepareIntakeStates.PREPARING;
                break;
            case PREPARING:
                // if (lift.isPositionReached() && extensionArm.isPositionReached()) {
                prepareIntakeState = PrepareIntakeStates.COMPLETE;
                //}
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
    }

    public boolean isPrepareIntakeComplete() {
        if (prepareIntakeState == PrepareIntakeStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }


}
