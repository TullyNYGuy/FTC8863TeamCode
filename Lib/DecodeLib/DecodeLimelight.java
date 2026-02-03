package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DecodeLimelight implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Pipeline {
        RED_GOAL(0),
        BLUE_GOAL(1),
        OBELISK(2);
        public final int pipleineNumber;

        Pipeline(int pipleineNumber) {
            this.pipleineNumber = pipleineNumber;
        }
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    public Limelight3A limelight;
    private DecodeIMU decodeIMU;
    private final double HEIGHT_TO_LENS = 16;
    private final double HEIGHT_TO_APRIL_TAG = 29.5;
    private DistanceUnit distanceUnit = DistanceUnit.INCH;
    private final double LENS_ANGLE = 22;
    private AngleUnit angleUnit = AngleUnit.DEGREES;
    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    private boolean aprilTagAcquired = false;
    public boolean isAprilTagAcquired() {
        return aprilTagAcquired;
    }

    private LLResult limelightResult;

    public LLResult getLimelightResult() {
        return limelightResult;
    }

    private double tx = 0;

    /**
     * // How far left or right the target is (degrees)
     * @return
     */
    public double getTx() {
        return tx;
    }

    /**
     *
     */
    private double ty = 0;

    /**
     * // How far up or down the target is (degrees)
     * @return
     */
    public double getTy() {
        return ty;
    }

    /**
     *
     */
    private double ta = 0;

    /**
     * How big the target looks (0%-100% of the image)
     * @return
     */
    public double getTa() {
        return ta;
    }

    private int aprilTagIDFirst;

    /**
     * Get the first of the april tag IDs that is seen. There can be more but this is the first one
     * in the list
     * @return
     */
    public int getFirstAprilTagID() {
        return aprilTagIDFirst;
    }
    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods For Implementing FTCRobotSubsystem
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private final String SUB_SYSTEM_NAME = DecodeRobot.HardwareName.LIMELIGHT.hwName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeLimelight(HardwareMap hardwareMap, Telemetry telemetry, DecodeIMU decodeIMU) {
        limelight = hardwareMap.get(Limelight3A.class, SUB_SYSTEM_NAME);
        this.decodeIMU = decodeIMU;
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
    public void start(int pollRateInHz) {
        limelight.setPollRateHz(pollRateInHz);
        limelight.start();
    }

    public void pipelineSwitch(int pipelineNumber) {
        limelight.pipelineSwitch(pipelineNumber);
    }

    public boolean getFreshData() {
        boolean gotGoodData = false;
        limelightResult = limelight.getLatestResult();
        if (limelightResult != null) {
            gotGoodData = true;
            if (limelightResult.isValid()) {
                aprilTagAcquired = true;
                tx = limelightResult.getTx(); // How far left or right the target is (degrees)
                ty = limelightResult.getTy(); // How far up or down the target is (degrees)
                ta = limelightResult.getTa(); // How big the target looks (0%-100% of the image)
            } else {
                aprilTagAcquired = false;
            }
        }
        return gotGoodData;
    }

    public double getDistaceToGoal(DistanceUnit requestedUnits){
        double distanceToGoal = 0;
        double robotYaw = decodeIMU.getYaw();
        double ty = 0;
        limelight.updateRobotOrientation(robotYaw);
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            ty = result.getTy(); // Vertical offset in degrees
            double angleToGoalDegrees = LENS_ANGLE + ty;
            double angleToGoalRadians = angleToGoalDegrees * (3.14159 / 180.0);
            distanceToGoal = (HEIGHT_TO_APRIL_TAG - HEIGHT_TO_LENS) / Math.tan(angleToGoalRadians);
        }
        return distanceToGoal;

    }
    public Double getTxHorizontalAngle() {
        double robotYaw = decodeIMU.getYaw();
        Double tx = 0.0;
        limelight.updateRobotOrientation(robotYaw);
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            tx = result.getTx(); // Horizontal offset in degrees
        } else {
            tx = null;
        }
        return tx;
    }

//    public int getAprilTagIDNumber() {
//        List<FiducialResult> fiducials = result.getFiducialResults();
//        for (FiducialResult fiducial : fiducials) {
//            int id = fiducial.getFiducialId(); // The ID number of the fiducial
//            double x = detection.getTargetXDegrees(); // Where it is (left-right)
//            double y = detection.getTargetYDegrees(); // Where it is (up-down)
//            double StrafeDistance_3D = fiducial.getRobotPoseTargetSpace().getY();
//            telemetry.addData("Fiducial " + id, "is " + distance + " meters away");
//        }
//    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public void update() {
    }

    @Override
    public String getName() {
        return SUB_SYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }
}
