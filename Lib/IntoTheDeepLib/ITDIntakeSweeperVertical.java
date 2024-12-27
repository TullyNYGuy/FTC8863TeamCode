package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorDetectorHSV;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorInHSV;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorSensorUpdatable;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ITDIntakeSweeperVertical implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum SweeperState {
        INTAKING,
        OUTTAKING,
        OUTTAKING_BEFORE_STOPPING,
        STOPPED,
        INTAKING_BEFORE_STOPPING,
        TRANSFERRING
    }

    private SweeperState sweeperState;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private CRServo intakeSweeperServoLeft;
    private CRServo intakeSweeperServoRight;
    private ElapsedTime timer;
    private ColorSensorUpdatable intakeColorSensor;
    private ColorDetectorHSV intakeColorDetector;
    private double delayTime;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logCommandOnchange;

    private boolean initComplete = false;
    private final String INTAKE_SWEEPER_SERVO_NAME = "Sweeper Servo";

    // define the colors the intake is looking for
    // f here means float instead of double type. HSV are float type.
    private ColorInHSV red = new ColorInHSV(Color.RED,
            0, 60,
            0.2f, 0.4f,
            0.07f, 0.09f);
    private ColorInHSV yellow = new ColorInHSV(Color.YELLOW,
            60, 120,
            0.5f, 0.65f,
            .13f, .16f);

    private ColorInHSV blue = new ColorInHSV(Color.BLUE,
            180, 240,
            0.54f, 0.66f,
            0.08f, 0.2f);

    private ColorInHSV[] possibleColors = new ColorInHSV[]{red, yellow, blue};

    private Color sampleColor = Color.UNKNOWN;

    /**
     * Returns the sample color. The sample color is updated once per update by reading the color sensor
     * HSV values and passing them to the colorDetector.
     * @return
     */
    public Color getSampleColor() {
        return sampleColor;
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDIntakeSweeperVertical(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeSweeperServoLeft = hardwareMap.get(CRServo.class, "intakeSweeperServoLeft");
        intakeSweeperServoLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeSweeperServoRight = hardwareMap.get(CRServo.class, "intakeSweeperServoRight");
        intakeSweeperServoRight.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeColorSensor = new ColorSensorUpdatable(hardwareMap, telemetry, "intakeColorSensorV3Left");
        // set up the color detector to look for one of the three possible colors
        intakeColorDetector = new ColorDetectorHSV(possibleColors);

        timer = new ElapsedTime();
        stop();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    //*********************************************************************************************
    //          Commands
    //*********************************************************************************************

    /**
     * Turn on the color sensor. LED will turn on.
     */
    public void colorSensorOn() {
        switch(sweeperState) {
            case STOPPED:
                intakeColorSensor.turnSensorOn();
                break;
            case INTAKING:
            case OUTTAKING:
            case TRANSFERRING:
            case OUTTAKING_BEFORE_STOPPING:
            case INTAKING_BEFORE_STOPPING:
                // ignore the command, do nothing
                break;
        }

    }

    public void ColorSensorOff() {
        switch(sweeperState) {
            case STOPPED:
            case INTAKING:
            case OUTTAKING:
            case TRANSFERRING:
            case OUTTAKING_BEFORE_STOPPING:
            case INTAKING_BEFORE_STOPPING:
                // ignore the command, do nothing
                break;
        }

    }

    /**
     * Stop the rotation of the sweeper
     */
    public void stop() {
        intakeSweeperServoLeft.setPower(0);
        intakeSweeperServoRight.setPower(0);
        sweeperState = SweeperState.STOPPED;
    }

    /**
     * After a delay, stop the sweeper
     *
     * @param delayTimeInMillisec amount of time to delay the before stopping
     */
    public void intakeThenStop(double delayTimeInMillisec) {
        // if the sweeper is already in a delay before stopping, don't start it all over again
        if (sweeperState != SweeperState.INTAKING_BEFORE_STOPPING) {
            timer.reset();
            this.delayTime = delayTime;
            sweeperState = SweeperState.INTAKING_BEFORE_STOPPING;
        }
    }

    /**
     * Rotate the sweeper so it intakes
     */
    public void intake() {
        intakeColorSensor.turnSensorOn();
        // force an update to get fresh distance and color data
        intakeColorSensor.update();
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
        sweeperState = SweeperState.INTAKING;
    }

    /**
     * Rotate the sweeper to it outtakes.
     */
    public void outtake() {
        intakeSweeperServoLeft.setPower(-1);
        intakeSweeperServoRight.setPower(-1);
        sweeperState = SweeperState.OUTTAKING;
    }

    public void outtakeThenStop(double timeToRotateInMillisec) {
        // if the sweeper is already running an outtake before stopping, don't start all over again
        if (sweeperState != SweeperState.OUTTAKING_BEFORE_STOPPING) {
            outtake();
            this.delayTime = timeToRotateInMillisec;
            timer.reset();
            sweeperState = SweeperState.OUTTAKING_BEFORE_STOPPING;
        }
    }

    /**
     * Rotate the sweeper so it transfers a sample to the bucket on the lift
     */
    public void transfer() {
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
        sweeperState = SweeperState.TRANSFERRING;
    }

    //*********************************************************************************************
    //          Color sensor related functions
    //*********************************************************************************************

    public double getDistanceToSample(DistanceUnit distanceUnit) {
        return intakeColorSensor.getDistance(distanceUnit);
    }

    public void displayDistanceToSample(Telemetry telemetry){
        intakeColorSensor.displayColorSensorDistance(telemetry);
    }

    public void displayColorData(Telemetry telemetry) {
        intakeColorSensor.displayColorData(telemetry);
    }

    public void displaySampleColor(Telemetry telemetry) {
        telemetry.addData("Sample color = ", sampleColor.toString());
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return INTAKE_SWEEPER_SERVO_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        return true;
    }

    @Override
    public void shutdown() {
        stop();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {
        intakeColorSensor.update();
        // using the just updated HSV values, determine the color seen by the sample
        sampleColor = intakeColorDetector.getMostLikelyColor(intakeColorSensor.getHsvValues());
        switch (sweeperState) {
            case INTAKING:

                break;
            case OUTTAKING:
                break;
            case OUTTAKING_BEFORE_STOPPING:
                if (timer.milliseconds() > delayTime) {
                    stop();
                    sweeperState = SweeperState.STOPPED;
                }
                break;
            case INTAKING_BEFORE_STOPPING:
                if (timer.milliseconds() > delayTime) {
                    stop();
                    sweeperState = SweeperState.STOPPED;
                }
                break;
            case STOPPED:
                break;
        }
    }

}
