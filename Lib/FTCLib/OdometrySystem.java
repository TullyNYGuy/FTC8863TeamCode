package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

/*
 * This Odometry system designed to be used with mecanum drive.
 * The idea is to split movement of the robot into rotational and translational.
 * The system is designed to work with three odometer modules with two of them
 * (left and right) set parallel to each other. The third module (back) is
 * perpendicular to the first two.
 * First the system calculates angle of rotation by cancelling translation motion from
 * the readings of the two parallel odometers. After the angle of rotation is found
 * the readings are adjusted by the number derived from the angle of rotation.
 * After that is adjusted parallel readings are averaged and that gives translation
 * motion along the length of the parallel odometers. The adjusted reading of
 * the back module gives translation across the length of the parallel odometers.
 */
public class OdometrySystem implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "Odometry";

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private boolean initComplete;

    private OdometryModule left;
    private OdometryModule right;
    private OdometryModule back;

    DcMotor.Direction leftDirection;
    DcMotor.Direction rightDirection;
    DcMotor.Direction backDirection;
    private double leftDirectionMultiplier = 1;
    private double rightDirectionMultiplier = 1;
    private double backDirectionMultiplier = 1;

    final private String PROP_UNIT = "OdometrySystem.unit";
    final private String PROP_LEFT_MULTIPLIER = "OdometrySystem.leftMultiplier";
    final private String PROP_LEFT_DIRECTION_MULTIPLIER = "OdometrySystem.leftDirectionMultiplier";
    final private String PROP_RIGHT_MULTIPLIER = "OdometrySystem.rightMultiplier";
    final private String PROP_RIGHT_DIRECTION_MULTIPLIER = "OdometrySystem.rightDirectionMultiplier";
    final private String PROP_BACK_MULTIPLIER = "OdometrySystem.backMultiplier";
    final private String PROP_BACK_DIRECTION_MULTIPLIER = "OdometrySystem.backDirectionMultiplier";

    /*
     * Units of measurement for the rest of linear variables
     */
    private DistanceUnit unit;

    // Values used for calibration
    private double leftStartingValue = 0.0;
    private double rightStartingValue = 0.0;
    private double backStartingValue = 0.0;

    /*
     * Left odometer module multiplier. Equals to
     * leftModuleDistance^2/leftOffsetWidth
     */
    private double leftMultiplier;

    /*
     * Right odometer module multiplier. Equals to
     * rightModuleDistance^2/rightOffsetWidth
     */
    private double rightMultiplier;

    /*
     * Back odometer module multiplier. Equals to
     * backModuleDistance^2/backOffsetDepth
     */
    private double backMultiplier;

    /*
     * Rotational multiplier. Equals to
     * 1/(leftModuleDistance^2/leftOffsetWidth + rightModuleDistance^2/rightOffsetWidth)
     */
    private double rotationalMultiplier;

    private double currentX = 0;
    private double currentY = 0;
    private double currentRotation = 0;

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
    public OdometrySystem(DistanceUnit unit, OdometryModule left, OdometryModule right, OdometryModule back) {
        this.left = left;
        this.right = right;
        this.back = back;
        this.unit = unit;
        this.initComplete = false;
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

    public void initializeRobotGeometry(
            DistanceUnit unit,
            double leftOffsetDepth, double leftOffsetWidth, DcMotor.Direction leftDirection,
            double rightOffsetDepth, double rightOffsetWidth, DcMotor.Direction rightDirection,
            double backOffsetDepth, double backOffsetWidth, DcMotor.Direction backDirection) {
        this.leftDirection = leftDirection;
        this.rightDirection = rightDirection;
        this.backDirection = backDirection;
        // adjust units
        leftOffsetDepth = this.unit.fromUnit(unit, leftOffsetDepth);
        leftOffsetWidth = this.unit.fromUnit(unit, leftOffsetWidth);
        rightOffsetDepth = this.unit.fromUnit(unit, rightOffsetDepth);
        rightOffsetWidth = this.unit.fromUnit(unit, rightOffsetWidth);
        backOffsetDepth = this.unit.fromUnit(unit, backOffsetDepth);
        backOffsetWidth = this.unit.fromUnit(unit, backOffsetWidth);
        if (backDirection == DcMotor.Direction.FORWARD)
            backDirectionMultiplier = 1.0;
        else
            backDirectionMultiplier = -1.0;
        if (rightDirection == DcMotor.Direction.FORWARD)
            rightDirectionMultiplier = 1.0;
        else
            rightDirectionMultiplier = -1.0;
        if (leftDirection == DcMotor.Direction.FORWARD)
            leftDirectionMultiplier = 1.0;
        else
            leftDirectionMultiplier = -1.0;
        double leftModuleDistanceSq = leftOffsetDepth * leftOffsetDepth + leftOffsetWidth * leftOffsetWidth;
        double rightModuleDistanceSq = rightOffsetDepth * rightOffsetDepth + rightOffsetWidth * rightOffsetWidth;
        double backModuleDistanceSq = backOffsetDepth * backOffsetDepth + backOffsetWidth * backOffsetWidth;
        leftMultiplier = leftModuleDistanceSq / leftOffsetWidth;
        rightMultiplier = rightModuleDistanceSq / rightOffsetWidth;
        backMultiplier = backModuleDistanceSq / backOffsetDepth;
        initializeInternal();
        initComplete = true;
    }

    protected void initializeInternal() {
        rotationalMultiplier = 1.0 / (leftMultiplier + rightMultiplier);
        reset();
    }

    double leftEncoderOld;
    double rightEncoderOld;
    double backEncoderOld;

    public void calculateMoveDistance() {

        double leftEncoderNew = (left != null) ? left.getDistanceSinceReset(unit) : 0.0;
        double rightEncoderNew = (right != null) ? right.getDistanceSinceReset(unit) : 0.0;
        double backEncoderNew = (back != null) ? back.getDistanceSinceReset(unit) : 0.0;

        double leftEncoderValue = (leftEncoderNew - leftEncoderOld) * leftDirectionMultiplier;
        double rightEncoderValue = (rightEncoderNew - rightEncoderOld) * rightDirectionMultiplier;
        double backEncoderValue = (backEncoderNew - backEncoderOld) * backDirectionMultiplier;


        // calculate angle of rotation
        double deltaRotation = (leftEncoderValue - rightEncoderValue) * rotationalMultiplier;

        // adjust values by canceling rotation
        double leftVal = leftEncoderValue - deltaRotation * leftMultiplier;
        double rightVal = rightEncoderValue + deltaRotation * rightMultiplier;
        double backVal = backEncoderValue - deltaRotation * backMultiplier;

        double deltaX = (leftVal + rightVal) / 2.0;
        double deltaY = backVal;

        currentX += deltaX * Math.cos(deltaRotation);
        currentY += deltaY * Math.sin(deltaRotation);
        currentRotation += deltaRotation;
        leftEncoderOld = leftEncoderNew;
        rightEncoderOld = rightEncoderNew;
        backEncoderOld = backEncoderNew;
    }

    /*
     * Used to start calibration process
     */
    public void startCalibration() {
        reset();
        if (left != null)
            leftStartingValue = left.getDistanceSinceReset(unit);
        if (right != null)
            rightStartingValue = right.getDistanceSinceReset(unit);
        if (back != null)
            backStartingValue = back.getDistanceSinceReset(unit);
    }

    /*
     * Used after calling {@link #startCalibration() startCalibration} and subsequent rotation of the robot by {@link #rotation rotation} radians
     * @param angleUnit units of measurement for rotation
     * @param rotation rotation of the robot in radians since the call to {@link #startCalibration() startCalibration}
     */
    public void finishCalibration(AngleUnit angleUnit, double rotation) {
        leftMultiplier = 0.0;
        rightMultiplier = 0.0;
        backMultiplier = 0.0;
        leftDirectionMultiplier = 1.0;
        rightDirectionMultiplier = 1.0;
        backDirectionMultiplier = 1.0;

        rotation = angleUnit.toRadians(rotation);

        if (Math.abs(rotation) > 0.0) {
            if (left != null) {
                double leftEndingValue = left.getDistanceSinceReset(unit);
                leftMultiplier = (leftEndingValue - leftStartingValue) / rotation;
                if (leftMultiplier > 0.0) {
                    leftDirectionMultiplier = 1.0;
                } else {
                    leftDirectionMultiplier = -1.0;
                    leftMultiplier = -leftMultiplier;
                }
            }
            if (right != null) {
                double rightEndingValue = right.getDistanceSinceReset(unit);
                rightMultiplier = (rightEndingValue - rightStartingValue) / rotation;
                if (rightMultiplier < 0.0) {
                    rightDirectionMultiplier = 1.0;
                    rightMultiplier = -rightMultiplier;
                } else {
                    rightDirectionMultiplier = -1.0;
                }
            }
            if (back != null) {
                double backEndingValue = back.getDistanceSinceReset(unit);
                backMultiplier = (backEndingValue - backStartingValue) / rotation;
                if (backMultiplier > 0.0) {
                    backDirectionMultiplier = 1.0;
                } else {
                    backDirectionMultiplier = -1.0;
                    backMultiplier = -backMultiplier;
                }
            }
        }

        initializeInternal();
    }

    public void reset() {
        if (left != null) {
            left.resetEncoderValue();
            leftEncoderOld = left.getDistanceSinceReset(unit);
        } else {
            leftEncoderOld = 0;
        }
        if (right != null) {
            right.resetEncoderValue();
            rightEncoderOld = right.getDistanceSinceReset(unit);
        } else {
            rightEncoderOld = 0;
        }
        if (back != null) {
            back.resetEncoderValue();
            backEncoderOld = back.getDistanceSinceReset(unit);
        } else {
            backEncoderOld = 0;
        }


        resetCoordinates();
    }

    public boolean saveConfiguration(Configuration config) {
        if (config == null)
            return false;
        String unitStr;
        switch (unit) {
            case INCH:
                unitStr = "in";
                break;
            case CM:
                unitStr = "cm";
                break;
            case METER:
                unitStr = "m";
                break;
            default:
                unitStr = "mm";
        }
        config.setProperty(PROP_UNIT, unitStr);
        config.setProperty(PROP_LEFT_MULTIPLIER, String.valueOf(leftMultiplier));
        config.setProperty(PROP_LEFT_DIRECTION_MULTIPLIER, String.valueOf(leftDirectionMultiplier));
        config.setProperty(PROP_RIGHT_MULTIPLIER, String.valueOf(rightMultiplier));
        config.setProperty(PROP_RIGHT_DIRECTION_MULTIPLIER, String.valueOf(rightDirectionMultiplier));
        config.setProperty(PROP_BACK_MULTIPLIER, String.valueOf(backMultiplier));
        config.setProperty(PROP_BACK_DIRECTION_MULTIPLIER, String.valueOf(backDirectionMultiplier));
        return true;
    }

    public boolean loadConfiguration(Configuration config) {
        if (config == null)
            return false;
        Pair<String, Boolean> strVal = config.getPropertyString(PROP_UNIT, "mm");
        boolean fullConfig = strVal.second;
        if (strVal.first.equalsIgnoreCase("in"))
            unit = DistanceUnit.INCH;
        else if (strVal.first.equalsIgnoreCase("cm"))
            unit = DistanceUnit.CM;
        else if (strVal.first.equalsIgnoreCase("m"))
            unit = DistanceUnit.METER;
        else
            unit = DistanceUnit.MM;
        Pair<Double, Boolean> dblVal = config.getPropertyDouble(PROP_LEFT_MULTIPLIER, 1.0);
        leftMultiplier = dblVal.first;
        fullConfig &= dblVal.second;
        dblVal = config.getPropertyDouble(PROP_LEFT_DIRECTION_MULTIPLIER, 1.0);
        leftDirectionMultiplier = dblVal.first;
        fullConfig &= dblVal.second;
        dblVal = config.getPropertyDouble(PROP_RIGHT_MULTIPLIER, 1.0);
        rightMultiplier = dblVal.first;
        fullConfig &= dblVal.second;
        dblVal = config.getPropertyDouble(PROP_RIGHT_DIRECTION_MULTIPLIER, 1.0);
        rightDirectionMultiplier = dblVal.first;
        fullConfig &= dblVal.second;
        dblVal = config.getPropertyDouble(PROP_BACK_MULTIPLIER, 1.0);
        backMultiplier = dblVal.first;
        fullConfig &= dblVal.second;
        dblVal = config.getPropertyDouble(PROP_BACK_DIRECTION_MULTIPLIER, 1.0);
        backDirectionMultiplier = dblVal.first;
        fullConfig &= dblVal.second;
        initializeInternal();
        initComplete = fullConfig;
        return initComplete;
    }

    public void resetCoordinates() {
        currentX = 0.0;
        currentY = 0.0;
        currentRotation = 0.0;
    }

    public void setCoordinates(DistanceUnit unit, double x, double y, AngleUnit angleUnit, double rotation) {
        currentRotation = angleUnit.toRadians(rotation);
        currentX = this.unit.fromUnit(unit, x);
        currentY = this.unit.fromUnit(unit, y);
    }

    public void getCurrentPosition(Position position) {
        position.x = position.unit.fromUnit(unit, currentX);
        position.y = position.unit.fromUnit(unit, currentY);
    }

    public double getCurrentY(DistanceUnit unit) {
        return unit.fromUnit(this.unit, currentY);
    }

    public double getCurrentX(DistanceUnit unit) {
        return unit.fromUnit(this.unit, currentX);
    }

    public double getCurrentRotation(AngleUnit angleUnit) {
        return angleUnit.fromRadians(currentRotation);
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        return loadConfiguration(config);
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void timedUpdate(double timerValueMsec) {
        calculateMoveDistance();
    }
}
