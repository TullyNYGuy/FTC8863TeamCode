package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Skystone;

public class DualLift implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "DualLift";

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum LiftSide {
        LEFT(0),
        RIGHT(1);

        public final int side;

        private LiftSide(int side) {
            this.side = side;
        }
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Lift liftRight;
    private Lift liftLeft;

    private DcMotor8863.MotorType motorType = org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_40;
    private double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    private double movementPerRevolution = spoolDiameter * Math.PI * 5;

    private double heightAboveTower = 1;

    private DataLogging logFileBoth;

    private int maxBlockNumber = 6;

    public int getMaxBlockNumber() {
        return maxBlockNumber;
    }

    public void setMaxBlockNumber(int maxBlockNumber) {
        this.maxBlockNumber = maxBlockNumber;
    }

    private ExtensionRetractionMechanism.ExtensionRetractionStates[] dualLiftStates;

    private int[] encoderValues;

    private boolean collectData = false;

    private boolean dataLogging = false;

    public CSVDataFile timeEncoderValueFile = null;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DualLift(HardwareMap hardwareMap,
                    String liftRightName,
                    String liftRightMotorName,
                    String liftRightExtensionLimitSwitchName,
                    String liftRightRetractionLimitSwitch,
                    String liftLeftName,
                    String liftLeftMotorName,
                    String liftLeftExtensionLimitSwitchName,
                    String liftLeftRetractionLimitSwitch,
                    Telemetry telemetry) {
        liftRight = new Lift(hardwareMap, telemetry, liftRightName,
                liftRightExtensionLimitSwitchName, liftRightRetractionLimitSwitch, liftRightMotorName,
                motorType, movementPerRevolution);
        liftLeft = new Lift(hardwareMap, telemetry, liftLeftName,
                liftLeftExtensionLimitSwitchName, liftLeftRetractionLimitSwitch, liftLeftMotorName,
                motorType, movementPerRevolution);
        liftRight.reverseMotor();

        dualLiftStates = new ExtensionRetractionMechanism.ExtensionRetractionStates[2];
        getState();

        encoderValues = new int[2];
        getEncoderValues();

        disableCollectData();
        disableDataLogging();
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    public int getCurrentEncoderValueLeft() {
        return liftLeft.getCurrentEncoderValue();
    }

    public int getCurrentEncoderValueRight() {
        return liftRight.getCurrentEncoderValue();
    }
    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return (liftRight.isResetComplete() && liftLeft.isResetComplete());
    }

    @Override
    public boolean init(Configuration config) {
        liftRight.reset();
        liftLeft.reset();
        return true;
    }

    public void reset() {
        liftRight.reset();
        liftLeft.reset();
    }

    public boolean isResetComplete() {
        return (liftRight.isResetComplete() && liftLeft.isResetComplete());
    }


    public void goToPosition(double positionInInches, double positionPower) {
        liftRight.goToPosition(positionInInches, positionPower);
        liftLeft.goToPosition(positionInInches, positionPower);
    }

    public void goToBlockHeights(int blockNumber) {
        if (blockNumber > maxBlockNumber) {
            blockNumber = maxBlockNumber;
        }
        goToPosition(Skystone.getHeightPlusNubIN() * blockNumber + heightAboveTower, .3);
    }

    public void goToBottom() {
        goToPosition(0.5, 0.5);
    }

    public void lowerBlockOntoTower() {

    }

    public void setRetractionPower(double power){
        liftRight.setRetractionPower(power);
        liftLeft.setRetractionPower(power);
    }

    public void setExtensionPower(double power){
        liftLeft.setExtensionPower(power);
        liftRight.setExtensionPower(power);
    }

    public void setDataLog(DataLogging logFileBoth){
        this.logFileBoth = logFileBoth;
        liftLeft.setDataLog(logFileBoth);
        liftRight.setDataLog(logFileBoth);
    }

    public void enableDataLogging() {
        liftLeft.enableDataLogging();
        liftRight.enableDataLogging();
        dataLogging = true;
    }

    public void disableDataLogging() {
        liftLeft.disableDataLogging();
        liftRight.disableDataLogging();
        dataLogging = false;
    }

    public void enableCollectData(String filename) {
        liftLeft.enableCollectData();
        liftRight.enableCollectData();
        timeEncoderValueFile = new CSVDataFile(filename);
        collectData = true;
    }

    public void disableCollectData() {
        liftLeft.enableCollectData();
        liftRight.enableCollectData();
        collectData = false;
    }

    public void setResetPower(double resetPower) {
        liftRight.setResetPower(resetPower);
        liftLeft.setResetPower(resetPower);
    }

    public void setExtensionPositionInMechanismUnits(double heightTimesSlides) {
        liftLeft.setExtensionPositionInMechanismUnits(heightTimesSlides);
        liftRight.setExtensionPositionInMechanismUnits(heightTimesSlides);
    }

    public boolean isPositionReached() {
        return (liftLeft.isPositionReached() && liftRight.isPositionReached());
    }

    public ExtensionRetractionMechanism.ExtensionRetractionStates[] getState() {
        dualLiftStates[LiftSide.RIGHT.side] = liftRight.getExtensionRetractionState();
        dualLiftStates[LiftSide.LEFT.side] = liftLeft.getExtensionRetractionState();
        return dualLiftStates;
    }

    public ExtensionRetractionMechanism.ExtensionRetractionStates getLeftState() {
        return liftLeft.getExtensionRetractionState();
    }

    public ExtensionRetractionMechanism.ExtensionRetractionStates getRightState() {
        return liftRight.getExtensionRetractionState();
    }

    public String stateToString() {
        // update the state property
        getState();
        return "state (L, R) = " + dualLiftStates[LiftSide.LEFT.side].toString() + " " + dualLiftStates[LiftSide.RIGHT.side].toString();
    }

    public int[] getEncoderValues() {
        encoderValues[LiftSide.RIGHT.side] = liftRight.getCurrentEncoderValue();
        encoderValues[LiftSide.LEFT.side] = liftLeft.getCurrentEncoderValue();
        return encoderValues;
    }

    public int getLeftEncoderValue() {
        return liftLeft.getCurrentEncoderValue();
    }

    public int getRightEncoderValue() {
        return liftRight.getCurrentEncoderValue();
    }

    public String encoderValuesToString() {
        getEncoderValues();
        return "encoder values (L, R) = " + encoderValues[LiftSide.LEFT.side] + " " + encoderValues[LiftSide.RIGHT.side];
    }

    @Override
    public void update() {
        liftRight.update();
        liftLeft.update();
        if (collectData) {
            liftLeft.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);
            liftRight.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);
        }
    }

    @Override
    public void shutdown() {
        liftRight.shutdown();
        liftLeft.shutdown();
    }
}
