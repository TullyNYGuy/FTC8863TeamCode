package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;


import java.util.concurrent.TimeUnit;

public class FFIntake implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum IntakeState {
        IDLE,
        INTAKE,
        WAIT_FOR_FREIGHT,
        HOLD_FREIGHT,
        WAIT_FOR_ROTATION,
        OUTAKE,
        TO_LEVEL_ONE,
        WAIT_FOR_LEVEL_ONE,
        EJECT_INTO_LEVEL_ONE,
        WAIT_FOR_EJECT,
        BACK_TO_HOLD_FREIGHT,
        E_STOP;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private NormalizedColorSensor intakeSensor;
    private DcMotor8863 intakeSweeperMotor;
    private ElapsedTime timer;
    private Servo8863New rotateServo;
    private DataLogging logFile;
    private boolean loggingOn = false;
    private Boolean initComplete = false;
    private final String INTAKE_NAME = "Intake";
    private IntakeState intakeState = IntakeState.IDLE;
    private final String INTAKE_SWEEPER_MOTOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_SWEEPER_MOTOR.hwName;
    private final String INTAKE_SENSOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_SENSOR.hwName;
    private final String INTAKE_ROTATOR_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.INTAKE_ROTATE_SERVO.hwName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFIntake(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeSweeperMotor = new DcMotor8863(INTAKE_SWEEPER_MOTOR_NAME, hardwareMap);
        intakeSweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        intakeSweeperMotor.setMovementPerRev(360);
        intakeSweeperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        timer = new ElapsedTime();
        intakeSensor = hardwareMap.get(NormalizedColorSensor.class, INTAKE_SENSOR_NAME);
        if (intakeSensor instanceof SwitchableLight) {
            ((SwitchableLight) intakeSensor).enableLight(true);
        }
        rotateServo = new Servo8863New(INTAKE_ROTATOR_SERVO_NAME, hardwareMap, telemetry);
        rotateServo.addPosition("Intake", .02, 1000, TimeUnit.MILLISECONDS);
        rotateServo.addPosition("Level 1", .23, 1000, TimeUnit.MILLISECONDS);
        rotateServo.addPosition("Vertical", .5, 1000, TimeUnit.MILLISECONDS);
        rotateServo.addPosition("Deliver", 1.0, 1000, TimeUnit.MILLISECONDS);
     }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public boolean isIntakeFull() {
        if (((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM) < 3) {
            return true;
        } else {
            return false;
        }
    }

    public void displaySwitches(Telemetry telemetry) {
        telemetry.addData("distance=", ((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM));
    }

    @Override
    public String getName() {
        return INTAKE_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        rotateServo.setPosition("intake");
        initComplete = true;
        return true;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void update() {
        switch (intakeState) {
            case IDLE: {
                // do nothing
            }
            break;

            case INTAKE: {
                // fire up that motor baby! Dang that thing is loud!
                intakeSweeperMotor.runAtConstantPower(.6);
                rotateServo.setPosition("Intake");
                intakeState = IntakeState.WAIT_FOR_FREIGHT;
            }
            break;

            case WAIT_FOR_FREIGHT: {
                // do we have something?
                if (isIntakeFull()) {
                    // yup stop the motor and try to cage the freight
                    intakeSweeperMotor.runAtConstantRPM(180);
                    timer.reset();
                    //intakeSweeperMotor.moveToPosition(.3, 300, DcMotor8863.FinishBehavior.HOLD);
                    intakeState = IntakeState.WAIT_FOR_ROTATION;
                }
            }
            break;

            case HOLD_FREIGHT: {
                // is the caging done?
                if (intakeSweeperMotor.isMovementComplete()) {
                    // yup, now the human has to rotate the intake because that intake guy has not completed the rotation hardware yet :-)
                    timer.reset();
                    rotateServo.setPosition("Vertical");
                    intakeState = IntakeState.WAIT_FOR_ROTATION;
                }
            }
            break;

            case WAIT_FOR_ROTATION: {
                // has the human done his thing?
                if (rotateServo.isPositionReached()) {
                    // hope so cause I'm about to eject the freight
                    intakeSweeperMotor.setPower(0);
                    timer.reset();
                    intakeState = IntakeState.IDLE;
                }
            }
            break;
//
//            case OUTAKE: {
//                // hopefully the freight ejects in this amount of time
//                if (timer.milliseconds() > 3500) {
//                    // done ejecting, time to go back to sleep
//                    intakeSweeperMotor.setPower(0);
//                    rotateServo.setPosition("intake");
//                    intakeState = IntakeState.IDLE;
//                }
//            }
//            break;

//            case E_STOP: {
//                intakeSweeperMotor.setPower(0);
//                rotateServo.setPosition("intake");
//            }
//            break;
        }
    }

    @Override
    public void shutdown() {
        turnOff();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        loggingOn = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
        update();
    }

    public void turnOff() {
        intakeState = IntakeState.E_STOP;
    }
    public void turnOn(){intakeState = IntakeState.INTAKE;}
}

