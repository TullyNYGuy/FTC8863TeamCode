package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import android.graphics.Color;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class CollectorGB {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum MineralColor {
        GOLD,
        SILVER,
        NONE,
        EITHER
    }

    public enum CollectorState {
        OFF,
        NO_MINERAL,
        MINERAL_DETECTED,
        MINERAL_COLOR_DETERMINED,
        HOLD_MINERAL,
        STORE_INTAKE_ON,
        STORE_GATE_SERVO_TO_STORE,
        STORE_STORAGE_STAR_RUNNING,
        STORE_WAIT_FOR_GATE,
        STORE_CHECK_SUCCESS,
        STORE_WAIT_FOR_UNJAM,
        HALF_EJECT_POSITION,
        EJECT_MINERAL,
        EJECT_WAIT_FOR_GATE,
        DELIVER_MINERAL,
        FIX_TRANSFER_JAM,
        COMPLETE_DELIVERY,
        WAIT_FOR_GOLD_MINERAL_UNSTICK
    }

    private CollectorState collectorState = CollectorState.OFF;

    private enum ActionToTake {
        STORE,
        HOLD,
        EJECT,
        NO_ACTION
    }

    private enum CollectorCommand {
        OFF,
        ON,
        DELIVER_ON,
        DELIVER_OFF,
        FIX_TRANSFER_JAM,
        COMPLETE_DELIVERY,
        RESET,
        NONE
    }

    private enum CollectorMode {
        NORMAL,
        EJECT_ONLY,
        STORE_ONLY
    }

    private CollectorMode collectorMode = CollectorMode.NORMAL;

    private CollectorCommand collectorCommand = CollectorCommand.NONE;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private CRServo collectionServoLeft;
    private CRServo collectionServoRight;
    private CRServo storageStarServo;

    private Servo8863 gateServo;
    private double collectionPositionGateServo = 0.6;
    private double keepPositionGateServo = 0.1;
    private double halfEjectPositionGateServoSilver = 0.7;
    private double halfEjectPositionGateServoGold = 0.75;
    private double ejectPositionGateServo = 1;
    private double initPositionGateServo = 0.6;
    private double resetPositionGateServo = 0.25;
    private double fixJamPositionGateServo = 0.75;
    private ColorSensor sensorColor;
    private DistanceSensor sensorDistance;

    private MineralColor actualMineralColor = MineralColor.NONE;
    private MineralColor desiredMineralColor = MineralColor.GOLD;

    /**
     * The number of times in a row that gold has been seen.
     */
    private int mineralColorGoldCounter = 0;

    /**
     * The number of times in a row that silver has been seen.
     */
    private int mineralColorSilverCounter = 0;

    /**
     * A mineral color has to be read at least this many times in a row in order to say is it
     * actually this color. This is to prevent false reporting of the mineral color.
     */
    private int mineralColorCounterLimit = 5;
    private int mineralCheckSuccessCounter = 0;
    private double mineralGoldLimit = 60;
    private double mineralSilverLimit = 70;

    /**
     * The number of times in a row that a mineral has been detected.
     */
    private int mineralDetectedCounter = 0;
    /**
     * The number of times in a row that a mineral has be to be seen in order to say it is actually
     * there. This is done to prevent falsely saying there is a mineral present. The detector has
     * to see it several times in a row to say there is a mineral actually present.
     */
    private int mineralDetectedLimit = 5;

    private int numberOfMineralsStored = 0;

    private ElapsedTime timer;
    private ElapsedTime gateServoTimer;
    private ElapsedTime storageStarTimer;
    private ElapsedTime intakeTimer;

    private double red = 0;
    private double blue = 0;
    private double green = 0;
    private double hue = 0;
    private double argb = 0;
    private double distance = 100;

    private float hsvValues[] = {0F, 0F, 0F};

    // values is a reference to the hsvValues array.
    private final float values[] = hsvValues;

    // sometimes it helps to multiply the raw RGB values with a scale factor
// to amplify/attentuate the measured values.
    private final double SCALE_FACTOR = 255;

    private DataLogging logFile = null;
    private boolean loggingOn = false;

    private boolean debugOn = false;

    private Telemetry telemetry;

    private CollectorState previousCollectorState;
    private CollectorCommand previousCollectorCommand;

    private double mineralStorageTimerLimit = 1000;
    private double preEjectDelay = 500;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    public void enableDataLogging() {
        this.loggingOn = true;
    }

    public void disableDataLogging() {
        this.loggingOn = false;
    }

    public void setDebugOn() {
        this.debugOn = true;
    }

    public void setDebugOff() {
        this.debugOn = false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CollectorGB(HardwareMap hardwareMap, Telemetry telemetry) {
        collectionServoLeft = hardwareMap.get(CRServo.class, "collectionServoLeft");
        collectionServoRight = hardwareMap.get(CRServo.class, "collectionServoRight");
        collectionServoRight.setDirection(CRServo.Direction.REVERSE);

        storageStarServo = hardwareMap.get(CRServo.class, "storageStarServo");

        gateServo = new Servo8863("gateServo", hardwareMap, telemetry, collectionPositionGateServo, keepPositionGateServo, ejectPositionGateServo, initPositionGateServo, Servo.Direction.FORWARD);
        gateServo.setPositionTwo(resetPositionGateServo);
        gateServo.setPositionThree(halfEjectPositionGateServoSilver);
        gateServo.setPositionFour(halfEjectPositionGateServoGold);
        gateServo.setPositionFive(fixJamPositionGateServo);

        sensorColor = hardwareMap.get(ColorSensor.class, "revColorSensor");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "revColorSensor");

        timer = new ElapsedTime();
        gateServoTimer = new ElapsedTime();
        storageStarTimer = new ElapsedTime();
        intakeTimer = new ElapsedTime();

        this.telemetry = telemetry;
        init();
    }
    //*********************************************************************************************
    // PRIVATE COMMANDS
    //*********************************************************************************************

    private void turnIntakeOff() {
        collectionServoLeft.setPower(0);
        collectionServoRight.setPower(0);
    }

    public void turnIntakeOnSuckIn() {
        collectionServoLeft.setPower(-.8);
        collectionServoRight.setPower(-1);
    }

    public void turnIntakeOnSpitOut() {
        collectionServoLeft.setPower(1);
        collectionServoRight.setPower(1);
    }

    private void turnIntakeOnEject() {
        collectionServoLeft.setPower(-.8);
    }

    private void turnRightStarOnFixJam () { collectionServoRight.setPower(-1);}

    private void turnStorageStarOff() {
        storageStarServo.setPower(0);
    }

    private void turnStorageStarOnStore() {
        storageStarServo.setPower(0.5);
    }

    private void turnStorageStarOnUnstore() {
        storageStarServo.setPower(-0.5);
    }

    private void gateServoGoToCollectionPosition() {
        gateServo.goHome();
    }

    private void gateServoGoToFixJamPosition() {
        gateServo.goPositionFive();
    }

    private void gateServoGoToStorePosition() {
        gateServo.goUp();
    }

    private void gateServoGoToEjectPosition() {
        gateServo.goDown();
    }

    private void gateServoGoToInitPosition() {
        gateServo.goInitPosition();
    }

    public void gateServoToResetPosition() {
        gateServo.goPositionTwo();
    }

    public void gateServoGoToHalfEjectPositionSilver() {
        gateServo.goPositionThree();
    }

    public void gateServoGoToHalfEjectPositionGold() {
        gateServo.goPositionFour();
    }



    private void turnCollectorSystemsOff() {
        turnIntakeOff();
        turnStorageStarOff();
        gateServoGoToInitPosition();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private double readDistanceToMineral() {
        return sensorDistance.getDistance(DistanceUnit.CM);
    }

    private boolean isMineralDetected() {
        if (readDistanceToMineral() < 10) {
            // There is a mineral there, increment the number of times in a row it has been seen
            mineralDetectedCounter = mineralDetectedCounter + 1;
            // If it has been seen enough times in a row, then we say it is really there
            if (mineralDetectedCounter > mineralDetectedLimit) {
                //reset counter for next time
                mineralDetectedCounter = 0;
                return true;
            } else {
                return false;
            }
        } else {
            // if there is one time that a mineral is not detected set the counmt back to 0. This
            // enforces that a mineral has to be detected a number of times in a row.
            mineralDetectedCounter = 0;
            return false;
        }
    }

    private MineralColor readMineralColor() {
        MineralColor currentMineralColor = MineralColor.NONE;

        red = sensorColor.red();
        green = sensorColor.green();
        blue = sensorColor.blue();
        // convert the RGB values to HSV values.
        // multiply by the SCALE_FACTOR.
        // then cast it back to int (SCALE_FACTOR is a double)
        Color.RGBToHSV((int) (red * SCALE_FACTOR), (int) (green * SCALE_FACTOR), (int) (blue * SCALE_FACTOR), hsvValues);
        hue = hsvValues[0];

        if (hue < mineralGoldLimit) {
            currentMineralColor = MineralColor.GOLD;
        }
        if (hue > mineralSilverLimit) {
            currentMineralColor = MineralColor.SILVER;
        }
        return currentMineralColor;
    }

    private MineralColor determineMineralColor() {
        MineralColor currentMineralColor = MineralColor.NONE;
        MineralColor filteredMineralColor = MineralColor.NONE;
        MineralColor result = MineralColor.NONE;

        currentMineralColor = readMineralColor();
        switch (currentMineralColor) {
            case GOLD:
                mineralColorGoldCounter = mineralColorGoldCounter + 1;
                mineralColorSilverCounter = mineralColorDecrement(mineralColorSilverCounter);
                if (mineralColorGoldCounter > mineralColorCounterLimit) {
                    mineralColorGoldCounter = 0;
                    mineralColorSilverCounter = 0;
                    filteredMineralColor = MineralColor.GOLD;
                }
                break;
            case SILVER:
                mineralColorSilverCounter = mineralColorSilverCounter + 1;
                mineralColorGoldCounter = mineralColorDecrement(mineralColorGoldCounter);
                if (mineralColorSilverCounter > mineralColorCounterLimit) {
                    mineralColorGoldCounter = 0;
                    mineralColorSilverCounter = 0;
                    filteredMineralColor = MineralColor.SILVER;
                }
                break;
            case NONE:
                mineralColorGoldCounter = mineralColorDecrement(mineralColorGoldCounter);
                mineralColorSilverCounter = mineralColorDecrement(mineralColorSilverCounter);
                filteredMineralColor = MineralColor.NONE;
                break;
        }
        return filteredMineralColor;
    }

    private int mineralColorDecrement(int counter) {
        if (counter > 0) {
            return counter - 1;
        } else {
            return 0;
        }
    }

    private ActionToTake getActionForDetectedMineral(MineralColor actualMineralColor) {
        ActionToTake actionToTake = ActionToTake.NO_ACTION;

        switch (desiredMineralColor) {
            case EITHER:
                if (numberOfMineralsStored == 0) {
                    actionToTake = ActionToTake.STORE;
                } else {
                    actionToTake = ActionToTake.HOLD;
                }
                break;
            case GOLD:
                switch (actualMineralColor) {
                    case GOLD:
                        if (numberOfMineralsStored == 0) {
                            actionToTake = ActionToTake.STORE;
                        } else {
                            actionToTake = ActionToTake.HOLD;
                        }
                        break;
                    case SILVER:
                        actionToTake = ActionToTake.EJECT;
                        break;
                    case NONE:
                        actionToTake = ActionToTake.NO_ACTION;
                        break;
                }
                break;
            case SILVER:
                switch (actualMineralColor) {
                    case GOLD:
                        actionToTake = ActionToTake.EJECT;
                        break;
                    case SILVER:
                        if (numberOfMineralsStored == 0) {
                            actionToTake = ActionToTake.STORE;
                        } else {
                            actionToTake = ActionToTake.HOLD;
                        }
                        break;
                    case NONE:
                        actionToTake = ActionToTake.NO_ACTION;
                        break;
                }
                break;
        }
        return actionToTake;
    }

    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);

        }
    }

    private void logState(CollectorState collectorState, CollectorCommand collectorCommand) {
        if (logFile != null && loggingOn) {
            if (collectorState != previousCollectorState || collectorCommand != previousCollectorCommand) {
                logFile.logData("Collector about to enter", collectorState.toString(), collectorCommand.toString());
                previousCollectorState = collectorState;
                previousCollectorCommand = collectorCommand;
            }
        }
    }

    private void debug(String stringToDisplay) {
        if (debugOn) {
            telemetry.addLine(stringToDisplay);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************


    public void init() {
        log("Collector system initializing");
        setDesiredMineralColorToSilver();
        mineralDetectedCounter = 0;
        mineralColorSilverCounter = 0;
        mineralColorGoldCounter = 0;
        numberOfMineralsStored = 0;
        turnCollectorSystemsOff();
        collectorState = CollectorState.OFF;
        collectorCommand = CollectorCommand.NONE;
        collectorMode = CollectorMode.NORMAL;
        gateServoToResetPosition();
    }


    private void reset() {
        softReset();
    }

    private void hardReset() {
        init();
    }

    private void softReset() {
        log("Collector system soft reseting");
        mineralDetectedCounter = 0;
        mineralColorSilverCounter = 0;
        mineralColorGoldCounter = 0;
        turnCollectorSystemsOff();
        collectorState = CollectorState.OFF;
        collectorCommand = CollectorCommand.NONE;
        collectorMode = CollectorMode.NORMAL;
        gateServoToResetPosition();
    }

    public void shutdown() {
        hardReset();
    }

    //*********************************************************************************************
    // PUBLIC COMMANDS
    //*********************************************************************************************

    public void setDesiredMineralColorToGold() {
        log("COMMANDED DESIRED MINERAL = GOLD");
        mineralStorageTimerLimit = 2000;
        //telemetry.addLine("Collecting Gold");
        desiredMineralColor = MineralColor.GOLD;
    }

    public void setDesiredMineralColorToSilver() {
        log("COMMANDED DESIRED MINERAL = SILVER");
        mineralStorageTimerLimit = 1500;
        //telemetry.addLine("Collecting Silver");
        desiredMineralColor = MineralColor.SILVER;
    }

    public void setDesiredMineralColorToEither() {
        log("COMMANDED DESIRED MINERAL = EITHER");
        desiredMineralColor = MineralColor.EITHER;
    }

    // Commands to control collector

    public void turnCollectorOn() {
        log("COLLECTOR COMMANDED TURN ON");
        collectorCommand = CollectorCommand.ON;
    }

    public void turnCollectorOff() {
        log("COLLECTOR COMMANDED TURN OFF");
        collectorCommand = CollectorCommand.OFF;
    }

    public void deliverMineralsOn() {
        log("COLLECTOR COMMANDED DELIVER MINERALS ON");
        collectorCommand = CollectorCommand.DELIVER_ON;
    }

    public void deliverMineralsOff() {
        log("COLLECTOR COMMANDED DELIVER MINERALS OFF");
        collectorCommand = CollectorCommand.DELIVER_OFF;
    }

    public void fixTransferJam() {
        log("COLLECTOR COMMANDED FIX TRANSFER JAM");
        collectorCommand = CollectorCommand.FIX_TRANSFER_JAM;
    }

    public void deliverMineralsComplete() {
        log("COLLECTOR DELIVER MINERALS COMPLETE");
        collectorCommand = CollectorCommand.COMPLETE_DELIVERY;
    }

    public void resetCollector() {
        log("COLLECTOR COMMANDED RESET COLLECTOR");
        collectorCommand = CollectorCommand.RESET;
    }

    public void forceEjectOnly() {
        log("Collector commanded force eject only");
        collectorMode = CollectorMode.EJECT_ONLY;
    }

    public void forceStoreOnly() {
        log("Collector commanded force store");
        collectorMode = CollectorMode.STORE_ONLY;
    }

    public void forceNormalOperation() {
        log("Collector commanded force normal operation");
        collectorMode = CollectorMode.NORMAL;
    }

    //*********************************************************************************************
    // STATE MACHINE
    //*********************************************************************************************

    public CollectorState update() {

        ActionToTake actionToTake = ActionToTake.NO_ACTION;
        double mineralDeliverTimerLimit = 1500;
        double mineralEjectTimerLimit = 1500;

        logState(collectorState, collectorCommand);

        switch (collectorState) {
            // the collector is off
            case OFF:
                switch (collectorCommand) {
                    // since the collector is off, off and reset commands do not mean anything
                    case OFF:
                        break;
                    case RESET:
                        // just in case something is way wrong, perform a reset even though the
                        // collector is off
                        collectorState = CollectorState.OFF;
                        softReset();
                        // only need to do the reset once
                        collectorCommand = CollectorCommand.OFF;
                        break;
                    case ON:
                        // turn the collector on, but only if there are less than 2 minerals in it.
                        // 2 is the limit allowed according to the rules
                        gateServoGoToCollectionPosition();
                        if (numberOfMineralsStored < 2) {
                            collectorState = CollectorState.NO_MINERAL;
                            turnIntakeOnSuckIn();
                            log("Turn Collector On");
                            log("Desired mineral color = " + desiredMineralColor.toString());
                            debug("Turn Collector On");
                            debug("Desired mineral color = " + desiredMineralColor.toString());
                        } else {
                            // although the command was to turn on, since there are 2 minerals already
                            // override the command and turn the collector off
                            collectorState = CollectorState.OFF;
                            collectorCommand = CollectorCommand.OFF;
                        }
                        break;
                    case DELIVER_ON:
                        // deliver whatever is in the collector, even if there are no minerals known
                        // to be in the collector. Bascially assume the driver knows what they are
                        // doing
                        collectorState = CollectorState.DELIVER_MINERAL;
                        if(desiredMineralColor == MineralColor.GOLD){
                            turnStorageStarOnUnstore();
                        }
                        else{
                            turnStorageStarOnStore();
                        }
                        //gateServoGoToStorePosition();
                        storageStarTimer.reset();
                        log("Delivery started");
                        debug("Delivery started");
                        break;
                    case DELIVER_OFF:
                        // the collector is already off so this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.OFF;
                        break;
                    case FIX_TRANSFER_JAM:
                        // the collector is already off so this command is not relevant
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        collectorCommand = CollectorCommand.OFF;
                        break;
                    case COMPLETE_DELIVERY:
                        // the collector is already off so this command is not relevant
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        collectorCommand = CollectorCommand.OFF;
                        break;
                    // no command, do nothing
                    case NONE:
                        break;
                }
                break;

            // **********
            // INTAKE
            // **********

            case NO_MINERAL:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case ON:
                        if (isMineralDetected()) {
                            collectorState = CollectorState.MINERAL_DETECTED;
                            turnIntakeOff();
                            timer.reset();
                            // reset the mineral detected counter for next detection run
                            mineralDetectedCounter = 0;
                            log("Mineral detected");
                            debug("Mineral detected");
                        }
                        break;
                    case DELIVER_ON:
                        // The driver has asked the collector to deliver minerals so obey them.
                        collectorState = CollectorState.DELIVER_MINERAL;
                        if(desiredMineralColor == MineralColor.GOLD){
                            turnStorageStarOnUnstore();
                        }
                        else{
                            turnStorageStarOnStore();
                        }
                        //gateServoGoToStorePosition();
                        storageStarTimer.reset();
                        log("Delivery started");
                        debug("Delivery started");
                        break;
                    case DELIVER_OFF:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case FIX_TRANSFER_JAM:
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        // this command is not relevant
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case COMPLETE_DELIVERY:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;

            case MINERAL_DETECTED:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case ON:
                        actualMineralColor = determineMineralColor();
                        if (actualMineralColor == MineralColor.SILVER || actualMineralColor == MineralColor.GOLD) {
                            collectorState = CollectorState.MINERAL_COLOR_DETERMINED;
                            log("Mineral Color = " + actualMineralColor.toString());
                            debug("Mineral Color = " + actualMineralColor.toString());
                        }
                        if (actualMineralColor == MineralColor.NONE) {
                            // we really need to move it in just a little, not to the store position
                            // gateServoGoToStorePosition();
                        }
                        break;
                    case DELIVER_ON:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_ON);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case DELIVER_OFF:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case FIX_TRANSFER_JAM:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case COMPLETE_DELIVERY:
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        // this command is not relevant
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;

            case MINERAL_COLOR_DETERMINED:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case ON:
                        switch (collectorMode) {
                            case NORMAL:
                                actionToTake = getActionForDetectedMineral(actualMineralColor);
                                break;
                            case STORE_ONLY:
                                // for use in testing the collector, it will store everything
                                actionToTake = ActionToTake.STORE;
                                break;
                            case EJECT_ONLY:
                                // for use in testing the collector, it will eject everything
                                actionToTake = ActionToTake.EJECT;
                                break;
                        }
                        log("Action taken = " + actionToTake.toString());
                        debug("Action taken = " + actionToTake.toString());
                        log("Number of minerals stored = " + numberOfMineralsStored);
                        switch (actionToTake) {
                            case NO_ACTION:
                                collectorState = CollectorState.NO_MINERAL;
                                break;
                            case HOLD:
                                collectorState = CollectorState.HOLD_MINERAL;
                                mineralColorSilverCounter = 0;
                                mineralColorGoldCounter = 0;
                                turnIntakeOff();
                                turnStorageStarOff();
                                gateServoGoToCollectionPosition();
                                numberOfMineralsStored = 2;
                                log("Number of minerals stored = " + numberOfMineralsStored);
                                timer.reset();
                                break;
                            case STORE:
                                // collectorState = CollectorState.STORE_INTAKE_ON;
                                mineralColorSilverCounter = 0;
                                mineralColorGoldCounter = 0;
                                //turn on the storage star and start its timer
                                turnStorageStarOnStore();
                                storageStarTimer.reset();
                                // turn on the intake for a short time to push the mineral back towards the storage gate
                                // if the intakes are on then the collector can suck in another mineral so disable this
                                // and skip to the next state
                                //turnIntakeOnSuckIn();
                                collectorState = CollectorState.STORE_GATE_SERVO_TO_STORE;
                                //intakeTimer.reset();
                                // move gate servo towards back of collector and start its timer
                                gateServoGoToStorePosition();
                                gateServoTimer.reset();
                                timer.reset();
                                break;
                            case EJECT:
                                collectorState = CollectorState.EJECT_WAIT_FOR_GATE;
                                mineralColorSilverCounter = 0;
                                mineralColorGoldCounter = 0;
                                //changed it to run one star not both to stop from intaking during eject
                                turnIntakeOnEject();
                                gateServoTimer.reset();
                                if (desiredMineralColor == MineralColor.SILVER) {
                                    preEjectDelay = 100;
                                } else {
                                    preEjectDelay = 500;
                                }
                                break;
                        }

                        break;
                    case DELIVER_ON:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_ON);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case DELIVER_OFF:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case FIX_TRANSFER_JAM:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case COMPLETE_DELIVERY:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;

            case HOLD_MINERAL:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case ON:
                        if (numberOfMineralsStored < 2) {
                            collectorState = CollectorState.NO_MINERAL;
                            softReset();
                            turnIntakeOnSuckIn();
                        } else {
                            // there are 2 minerals in the collector. Turn off the collector.
                            collectorState = CollectorState.OFF;
                            collectorCommand = CollectorCommand.OFF;
                            softReset();
                            gateServoGoToCollectionPosition();
                            break;
                        }
                        break;
                    case DELIVER_ON:
                        collectorState = CollectorState.DELIVER_MINERAL;
                        if(desiredMineralColor == MineralColor.GOLD){
                            turnStorageStarOnUnstore();
                        }
                        else{
                            turnStorageStarOnStore();
                        }
                        //gateServoGoToStorePosition();
                        storageStarTimer.reset();
                        log("Delivery started");
                        debug("Delivery started");
                        break;
                    case DELIVER_OFF:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case FIX_TRANSFER_JAM:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case COMPLETE_DELIVERY:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;

            // **********
            // STORAGE
            // **********

            case STORE_INTAKE_ON:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case ON:
                        if (intakeTimer.milliseconds() > 500) {
                            turnIntakeOff();
                            collectorState = CollectorState.STORE_GATE_SERVO_TO_STORE;
                        }
                        break;
                    case DELIVER_ON:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_ON);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case DELIVER_OFF:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case FIX_TRANSFER_JAM:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case COMPLETE_DELIVERY:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;

            case STORE_GATE_SERVO_TO_STORE:
                // send the gate servo back to the collect position while the storage star is
                // still running. The amount subtracted from the mineralStorageTimerLimit is how
                // much sooner the gate start to move before the storage star turns off.
                if (gateServoTimer.milliseconds() > mineralStorageTimerLimit - 500) {
                    gateServoGoToCollectionPosition();
                    gateServoTimer.reset();
                    collectorState = CollectorState.STORE_STORAGE_STAR_RUNNING;
                }
                break;

            case STORE_STORAGE_STAR_RUNNING:
                // if the storage star has been running for its allowed time, shut it off
                if (storageStarTimer.milliseconds() > mineralStorageTimerLimit) {
                    numberOfMineralsStored = 1;
                    log("Number of minerals stored = " + numberOfMineralsStored);
                    debug("stored");
                    turnStorageStarOff();
                    collectorState = CollectorState.STORE_WAIT_FOR_GATE;
                }
                break;

            case STORE_WAIT_FOR_GATE:
                // give the gate some time to make sure it has returned to the collect position
                if (gateServoTimer.milliseconds() > 500) {
                    // The silver minerals do not jam when they are being stored. The gold sometimes do.
                    // So if a gold mineral is being stored, check to see if there is still a mineral
                    // in the sorting chamber.
                    if (desiredMineralColor == MineralColor.SILVER) {
                        // a silver mineral does not jam. Just go straight to NO_MINERAL to prep for
                        // intake.
                        collectorState = CollectorState.NO_MINERAL;
                        turnIntakeOnSuckIn();
                    } else {
                        // a gold was supposed to be stored. Check and see if it jammed.
                        collectorState = CollectorState.STORE_CHECK_SUCCESS;
                    }
                }
                break;

            case STORE_CHECK_SUCCESS:
                // if a mineral is detected in the sorting chamber after the store then there was a jam.
                if (isMineralDetected()) {
                    // yup there was a jam, try to clear it.
                    gateServoGoToFixJamPosition();
                    turnRightStarOnFixJam();
                    gateServoTimer.reset();
                    collectorState = CollectorState.STORE_WAIT_FOR_UNJAM;
                    mineralDetectedCounter = 0;
                    log("Mineral detected");
                    debug("Mineral detected");
                    mineralCheckSuccessCounter = 0;
                } else {
                    // the mineral detection has to run multiple times in order to say there is a mineral
                    // in the sorting chamber. We are allowing it to run 10 times before we say the sorting
                    // chamber does not have a jam in it.
                    if (mineralCheckSuccessCounter > 10) {
                        // there was no jam so prep for intake
                        mineralCheckSuccessCounter = 0;
                        gateServoGoToCollectionPosition();
                        turnIntakeOnSuckIn();
                        log("Turn Collector On");
                        log("Desired mineral color = " + desiredMineralColor.toString());
                        debug("Turn Collector On");
                        debug("Desired mineral color = " + desiredMineralColor.toString());
                        collectorState = CollectorState.NO_MINERAL;
                    } else {
                        // the number of times through the mineral detection routine has not been met.
                        // increment the counter and let it run again
                        mineralCheckSuccessCounter = mineralCheckSuccessCounter + 1;
                    }

                }
                break;

            case STORE_WAIT_FOR_UNJAM:
                // let the gate servo move to the unjam position for a period of time
                if (gateServoTimer.milliseconds() > 500) {
                    // once it has moved long enough, run the intake cycle again
                    //forcing actual mineral color to gold
                    actualMineralColor = MineralColor.GOLD;
                    collectorState = CollectorState.MINERAL_COLOR_DETERMINED;
                    numberOfMineralsStored = 0;
                    log("Number of minerals stored = " + numberOfMineralsStored);
                }
                break;

            // **********
            // EJECTION
            // **********

            case EJECT_WAIT_FOR_GATE:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case ON:
                        if (gateServoTimer.milliseconds() > preEjectDelay) {
                            // move the gate servo out part way to the eject position and give the intake stars
                            // time to move the mineral out along the gate servo arm. This reduces the fight
                            // between the gate servo driving the mineral out through the intake star. Net
                            // result is less time to eject the mineral.
                            // The gate servo part way positions have to be different for the gold and the
                            // silver minerals.
                            if (desiredMineralColor == MineralColor.GOLD) {
                                // keeping gold, so ejecting silver
                                gateServoGoToHalfEjectPositionSilver();
                            } else {
                                // keeping silcer so ejecting gold
                                gateServoGoToHalfEjectPositionGold();
                            }
                            gateServoTimer.reset();
                            collectorState = CollectorState.HALF_EJECT_POSITION;
                        }
                        break;
                    case DELIVER_ON:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_ON);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case DELIVER_OFF:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case FIX_TRANSFER_JAM:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case COMPLETE_DELIVERY:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }

            case HALF_EJECT_POSITION:
                // the gate servo sits in the half eject position for a period of time to allow the
                // intake star to move the mineral along the gate servo arm
                if (gateServoTimer.milliseconds() > 500) {
                    // now go to the full eject position
                    gateServoGoToEjectPosition();
                    gateServoTimer.reset();
                    collectorState = CollectorState.EJECT_MINERAL;
                }
                break;

            case EJECT_MINERAL:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case ON:
                        if (gateServoTimer.milliseconds() > mineralEjectTimerLimit) {
                            collectorState = CollectorState.NO_MINERAL;
                            turnCollectorSystemsOff();
                            turnIntakeOnSuckIn();
                            log("ejected");
                            debug("ejected");
                        }
                        break;
                    case DELIVER_ON:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_ON);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case DELIVER_OFF:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.DELIVER_OFF);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case FIX_TRANSFER_JAM:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.FIX_TRANSFER_JAM);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case COMPLETE_DELIVERY:
                        // this command is not relevant
                        logIgnoreCommand(CollectorCommand.COMPLETE_DELIVERY);
                        collectorCommand = CollectorCommand.ON;
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;

            // **********
            // DELIVERY
            // **********

            case DELIVER_MINERAL:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        numberOfMineralsStored = 0;
                        log("Number of minerals stored = " + numberOfMineralsStored);
                        log("Delivery completed");
                        debug("Delivery completed");
                        break;
                    case ON:
                        // there should never be an ON command by the time we get to this state
                        // If there is, force it to DELIVERY_ON
                        collectorCommand = CollectorCommand.DELIVER_ON;
                        break;
                    case DELIVER_ON:
                        if(desiredMineralColor == MineralColor.GOLD){
                            turnStorageStarOnUnstore();
                        }
                        else{
                            turnStorageStarOnStore();
                        }
                        //gateServoGoToStorePosition();
                        storageStarTimer.reset();
                        collectorState = CollectorState.WAIT_FOR_GOLD_MINERAL_UNSTICK;

                        // delivery has already been started. So just sit in this state and wait for
                        // the driver to tell us the delivery is complete or to fix a transfer jam.
                        break;
                    case DELIVER_OFF:
                        // turn off the delivery. But it is not complete yet so stay in this state
                        turnStorageStarOff();
                        gateServoGoToCollectionPosition();
                        collectorCommand = CollectorCommand.NONE;
                        collectorState = CollectorState.DELIVER_MINERAL;
                        break;
                    case FIX_TRANSFER_JAM:
                        // reverse the storage star for a bit and see if that will clear the jam
                        turnStorageStarOnUnstore();
                        timer.reset();
                        collectorState = CollectorState.FIX_TRANSFER_JAM;
                        break;
                    case COMPLETE_DELIVERY:
                        collectorState = CollectorState.OFF;
                        collectorCommand = CollectorCommand.OFF;
                        softReset();
                        numberOfMineralsStored = 0;
                        log("Number of minerals stored = " + numberOfMineralsStored);
                        log("Delivery completed");
                        debug("Delivery completed");
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;
            case WAIT_FOR_GOLD_MINERAL_UNSTICK:
                switch (collectorCommand){
                    case DELIVER_ON:
                        if(storageStarTimer.milliseconds() > 1000){

                            turnStorageStarOnStore();
                            gateServoGoToStorePosition();
                        }
                        break;
                    case COMPLETE_DELIVERY:
                        collectorState = CollectorState.OFF;
                        collectorCommand = CollectorCommand.OFF;
                        softReset();
                        numberOfMineralsStored = 0;
                        log("Number of minerals stored = " + numberOfMineralsStored);
                        log("Delivery completed");
                        debug("Delivery completed");
                        break;
                }
                break;

            case FIX_TRANSFER_JAM:
                switch (collectorCommand) {
                    case OFF:
                        collectorState = CollectorState.OFF;
                        softReset();
                        break;
                    case RESET:
                        collectorState = CollectorState.OFF;
                        softReset();
                        numberOfMineralsStored = 0;
                        log("Number of minerals stored = " + numberOfMineralsStored);
                        log("Delivery completed");
                        debug("Delivery completed");
                        break;
                    case ON:
                        break;
                    case DELIVER_ON:
                        collectorState = CollectorState.DELIVER_MINERAL;
                        turnStorageStarOnStore();
                        gateServoGoToStorePosition();
                        timer.reset();
                        log("Delivery started");
                        debug("Delivery started");
                        break;
                    case DELIVER_OFF:
                        // turn off the delivery. But it is not complete yet so move to the DELIVER_MINERAL state
                        turnStorageStarOff();
                        gateServoGoToCollectionPosition();
                        collectorCommand = CollectorCommand.NONE;
                        collectorState = CollectorState.DELIVER_MINERAL;
                        break;
                    case FIX_TRANSFER_JAM:
                        // after a period of time running the storage star backwards, run it forwards
                        // again
                        if (timer.milliseconds() > 500) {
                            timer.reset();
                            turnStorageStarOnStore();
                            collectorCommand = CollectorCommand.DELIVER_ON;
                            collectorState = CollectorState.DELIVER_MINERAL;
                        }
                        break;
                    case COMPLETE_DELIVERY:
                        collectorState = CollectorState.OFF;
                        collectorCommand = CollectorCommand.OFF;
                        softReset();
                        numberOfMineralsStored = 0;
                        log("Number of minerals stored = " + numberOfMineralsStored);
                        log("Delivery completed");
                        debug("Delivery completed");
                        break;
                    case NONE:
                        // no command, do nothing
                        break;
                }
                break;
//            case COMPLETE_DELIVERY:
//                break;
        }
        return collectorState;
    }

    private void logIgnoreCommand(CollectorCommand collectorCommand) {
        if (logFile != null && loggingOn) {
            logFile.logData("Ignoring command = ", collectorCommand.toString());
        }
    }

    public void displayCollectorState() {
        telemetry.addData("Collector State = ", collectorState.toString());
    }

    public void displayCollectorCommand() {
        telemetry.addData("Collector Command = ", collectorCommand.toString());
    }

    public void displayWhichMineralCollecting() {
        telemetry.addData("Collecting ", desiredMineralColor.toString());
    }

    //*********************************************************************************************
    // TESTS
    //*********************************************************************************************

    public void testMovements(Telemetry telemetry) {
        turnIntakeOnSuckIn();
        telemetry.addLine("Intake sucking in");
        telemetry.update();
        delay(4000);

        turnIntakeOff();
        telemetry.addLine("Intake off");
        telemetry.update();
        delay(4000);

        turnIntakeOnSpitOut();
        telemetry.addLine("Intake spitting out");
        telemetry.update();
        delay(4000);

        turnIntakeOnEject();
        telemetry.addLine("Intake ejecting");
        telemetry.update();
        delay(4000);

        turnIntakeOff();

        gateServoGoToInitPosition();
        telemetry.addLine("Gate servo at home position");
        telemetry.update();
        delay(4000);

        gateServoGoToEjectPosition();
        telemetry.addLine("Gate servo at eject position");
        telemetry.update();
        delay(4000);

        gateServoGoToCollectionPosition();
        telemetry.addLine("Gate servo at collect position");
        telemetry.update();
        delay(4000);

        gateServoGoToStorePosition();
        telemetry.addLine("Gate servo at store position");
        telemetry.update();
        delay(4000);

        gateServoGoToCollectionPosition();

        turnStorageStarOnStore();
        telemetry.addLine("Storage star on - storing");
        telemetry.update();
        delay(4000);


        turnStorageStarOnUnstore();
        telemetry.addLine("Storage star on - un-storing");
        telemetry.update();
        delay(4000);

        turnStorageStarOff();

    }


    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private ElapsedTime testTimer;
    private CollectorState testCollectorState;
    private MineralColor testMineralColorState;

    public void testSetup() {
        testTimer = new ElapsedTime();
        testCollectorState = CollectorState.NO_MINERAL;
        testMineralColorState = MineralColor.NONE;
    }

    public void testMineralDetection(Telemetry telemetry) {
        switch (testCollectorState) {
            case NO_MINERAL:
                if (isMineralDetected()) {
                    testCollectorState = CollectorState.MINERAL_DETECTED;
                    testTimer.reset();
                }
                telemetry.addLine("No Mineral present");
                break;
            case MINERAL_DETECTED:
                // hold for 5 seconds so users can read driver station phone
                if (testTimer.milliseconds() > 5000) {
                    testCollectorState = CollectorState.NO_MINERAL;
                }
                telemetry.addLine("Mineral detected");
                break;
        }
    }

    public void testMineralColorDetection(Telemetry telemetry) {
        MineralColor currentMineralColor = determineMineralColor();
        switch (testMineralColorState) {
            case NONE:
                if (currentMineralColor == MineralColor.SILVER) {
                    testMineralColorState = MineralColor.SILVER;
                    telemetry.addLine("color = silver");
                }
                if (currentMineralColor == MineralColor.GOLD) {
                    testMineralColorState = MineralColor.GOLD;
                    telemetry.addLine("color = gold");
                }
                if (currentMineralColor == MineralColor.NONE) {
                    testMineralColorState = MineralColor.NONE;
                    telemetry.addLine("color = none");
                }
                break;
            case SILVER:
                telemetry.addLine("color = silver");
                break;
            case GOLD:
                telemetry.addLine("color = gold");
                break;
        }
    }

    public void testReadColorSensor(Telemetry telemetry) {
        red = sensorColor.red();
        green = sensorColor.green();
        blue = sensorColor.blue();
        // convert the RGB values to HSV values.
        // multiply by the SCALE_FACTOR.
        // then cast it back to int (SCALE_FACTOR is a double)
        Color.RGBToHSV((int) (red * SCALE_FACTOR), (int) (green * SCALE_FACTOR), (int) (blue * SCALE_FACTOR), hsvValues);
        hue = hsvValues[0];
        telemetry.addData("hue = ", hue);
    }

    public void testActionToTake(MineralColor desiredMineralColor, MineralColor actualMineralColor, int numberOfMineralsStored, Telemetry telemetry) {
        ActionToTake actionToTake = ActionToTake.NO_ACTION;
        this.desiredMineralColor = desiredMineralColor;
        this.actualMineralColor = actualMineralColor;
        this.numberOfMineralsStored = numberOfMineralsStored;

        actionToTake = getActionForDetectedMineral(actualMineralColor);
        telemetry.addLine("Desired Color = " + desiredMineralColor.toString() + " Actual Color = " + actualMineralColor.toString() + " Number stored = " + numberOfMineralsStored + " Action - " + actionToTake.toString());
    }

    public void testGateServo() {
        gateServoGoToInitPosition();
        delay(1000);
        gateServoGoToEjectPosition();
        delay(1000);
        gateServoGoToInitPosition();
        delay(1000);
        gateServoGoToStorePosition();
        delay(1000);
    }
}
