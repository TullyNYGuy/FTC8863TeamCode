package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControlExternalTimer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class AutonomousController {
    final private double BLUE_BUILDSITE_X = 10.0;
    final private double BLUE_BUILDSITE_Y = 10.0;
    final private double RED_BUILDSITE_X = 10.0;
    final private double RED_BUILDSITE_Y = 10.0;
    final private double BLUE_BRIDGE_X = 10.0;
    final private double BLUE_BRIDGE_Y = 10.0;
    final private double RED_BRIDGE_X = 10.0;
    final private double RED_BRIDGE_Y = 10.0;
    final private double BLUE_PLATFORM_X = 10.0;
    final private double BLUE_PLATFORM_Y = 10.0;
    final private double RED_PLATFORM_X = 10.0;
    final private double RED_PLATFORM_Y = 10.0;
    final private double BLUE_HOME_X = 10.0;
    final private double BLUE_HOME_Y = 10.0;
    final private double RED_HOME_X = 10.0;
    final private double RED_HOME_Y = 10.0;

    final private DistanceUnit distanceUnit = DistanceUnit.CM;

    /*
     * Interval in milliseconds in which movement control task runs
     */
    final private long MOVEMENT_THREAD_INTERVAL = 200;

    public enum Areas {
        BUILDSITE, BRIDGE, BLOCK, PLATFORM, HOME
    }

    public boolean blockState;
    Areas place;

    public enum Color {
        BLUE, RED
    }

    private Color color;

    private Position currentDestination;

    private Map<Areas, Position> places;

    private SkystoneRobot robot;

    private ScheduledExecutorService scheduler;
    private MovemenetThread movementThread;
    private ScheduledFuture<?> movementTask;
    private ElapsedTime time = new ElapsedTime();
    private Telemetry telemetry;
    private DataLogging dataLog;

    class MovemenetThread implements Runnable {

        private final double XY_Kp = 0.03;
        private final double XY_Ki = 0;
        private final double XY_Kd = 0;
        private final double XY_MAX_CORRECTION = 1;
        private final double ROT_Kp = .02;
        private final double ROT_Ki = 0;
        private final double ROT_Kd = 0;
        private final double ROT_MAX_CORRECTION = 1;
        private PIDControlExternalTimer xControl;
        private PIDControlExternalTimer yControl;
        private PIDControlExternalTimer rotationControl;
        private MecanumCommands commands;
        private Position current;
        private DistanceUnit distanceUnit;
        private MecanumCommands zeroMovement;
        private ElapsedTime elapsedTime;

        public MovemenetThread(DistanceUnit distanceUnit) {
            elapsedTime = new ElapsedTime();
            elapsedTime.reset();
            xControl = new PIDControlExternalTimer(XY_Kp, XY_Ki, XY_Kd, XY_MAX_CORRECTION);
            yControl = new PIDControlExternalTimer(XY_Kp, XY_Ki, XY_Kd, XY_MAX_CORRECTION);
            rotationControl = new PIDControlExternalTimer(ROT_Kp, ROT_Ki, ROT_Kd, ROT_MAX_CORRECTION);
            commands = new MecanumCommands();
            zeroMovement = new MecanumCommands();
            this.distanceUnit = distanceUnit;
            current = new Position(distanceUnit, 0, 0, 0, 0);
        }

        public void setDestination(DistanceUnit distanceUnit, double x, double y) {
            synchronized (this) {
                xControl.setSetpoint(this.distanceUnit.fromUnit(distanceUnit, x));
                yControl.setSetpoint(this.distanceUnit.fromUnit(distanceUnit, y));
            }
        }

        public void setDestinationRotation(AngleUnit angleUnit, double rotation) {
            synchronized (this) {
                rotationControl.setSetpoint(angleUnit.toRadians(rotation));
            }
        }

        public void resetTimers() {
            xControl.reset();
            yControl.reset();
            rotationControl.reset();
            elapsedTime.reset();
        }

        @Override
        public void run() {
            double valX;
            double valY;
            double valRot;
            double timerValue = elapsedTime.seconds();
            robot.timedUpdate(timerValue);
            robot.getCurrentPosition(current);
            double currentRotation = robot.getCurrentRotation(AngleUnit.RADIANS);
            synchronized (this) {
                valX = xControl.getCorrection(current.x, timerValue);
                valY = yControl.getCorrection(current.y, timerValue);
                valRot = rotationControl.getCorrection(currentRotation, timerValue);
            }
            commands.setSpeed(Math.sqrt(valX * valX + valY * valY));
            commands.setAngleOfTranslation(AngleUnit.RADIANS, Math.atan2(valY, valX));
            commands.setSpeedOfRotation(valRot);
            robot.setMovement(commands);
            //telemetry.addData("MT: ", String.format("x: %.2f, y: %.2f", valX, valY));
            dataLog.logData(String.format("Position (X, Y): (%.2f, %.2f)", current.x, current.y));
            dataLog.logData(String.format("Correction (X, Y): (%.2f, %.2f)", valX, valY));
        }
    }

    public AutonomousController(SkystoneRobot robot, DataLogging logger, Telemetry telemetry) {
        places = new HashMap<Areas, Position>();
        this.robot = robot;
        movementThread = new MovemenetThread(distanceUnit);
        scheduler = Executors.newScheduledThreadPool(2);
        movementTask = null;
        this.telemetry = telemetry;
        this.dataLog = logger;
    }


    public void startController() {
        movementThread.resetTimers();
        if (movementTask == null)
            movementTask = scheduler.scheduleAtFixedRate(movementThread, 0, MOVEMENT_THREAD_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void stopController() {
        if (movementTask != null) {
            while (!movementTask.isDone()) {
                movementTask.cancel(false);
            }
            movementTask = null;
        }
    }

    public void setBlockState(boolean state) {
        blockState = state;
    }

    public void setAllegiance(Color color) {
        this.color = color;
    }

    // these variables are place holders for now. in the furture they will be only the coordinates for the first
    // block in the line
    public void moveTo(DistanceUnit distanceUnit, double x, double y) {
        movementThread.setDestination(distanceUnit, x, y);
    }

    public void initPlaces() {
        if (color == Color.BLUE) {
            places.put(Areas.BUILDSITE, new Position(distanceUnit, BLUE_BUILDSITE_X, BLUE_BUILDSITE_Y, 0, 0));
            places.put(Areas.BRIDGE, new Position(distanceUnit, BLUE_BRIDGE_X, BLUE_BRIDGE_Y, 0, 0));
            places.put(Areas.PLATFORM, new Position(distanceUnit, BLUE_PLATFORM_X, BLUE_PLATFORM_Y, 0, 0));
            places.put(Areas.HOME, new Position(distanceUnit, BLUE_HOME_X, BLUE_HOME_Y, 0, 0));
        } else {
            places.put(Areas.BUILDSITE, new Position(distanceUnit, RED_BUILDSITE_X, RED_BUILDSITE_Y, 0, 0));
            places.put(Areas.BRIDGE, new Position(distanceUnit, RED_BRIDGE_X, RED_BRIDGE_Y, 0, 0));
            places.put(Areas.PLATFORM, new Position(distanceUnit, RED_PLATFORM_X, RED_PLATFORM_Y, 0, 0));
            places.put(Areas.HOME, new Position(distanceUnit, RED_HOME_X, RED_HOME_Y, 0, 0));
        }
    }

    public void goTo(Areas place) {
        this.place = place;
        time.reset();
        currentDestination = places.get(place);

        if (currentDestination != null) {
            moveTo(distanceUnit, currentDestination.x, currentDestination.y);
        }
    }

    public void pickUpBlock() {
        goTo(Areas.BLOCK);
        findBlock();
        intakeBlock();
        goTo(Areas.BUILDSITE);
        dropBlock();
    }

    private void findBlock() {
    }

    private void intakeBlock() {

    }

    private void dropBlock() {

    }

    public void park() {
        //go to (bridge coordinates)
    }

    public void parkWithArm() {
        //go to (coordinates near bridge)
        //rotate to face bridge
        //extend arm
    }

    public void moveBase() {
        //go to base
        //grab base
        //move so base is in build area
        //let go of base
    }

    public void moveBaseRotate() {
        //go to base
        //grab base
        //rotate 90 degrees
        //move so base is in build area
        //let go of base
    }

    public void putBlockOnBase() {
        pickUpBlock();
        //pick up block with arm
        //move to base
        //arm up
        //extend arm
        //lower arm
        //align qrm
        //drop arm
    }
    public boolean isActionCompleteTime(){
        if(currentDestination.acquisitionTime < time.milliseconds()){
            return true;

        }
        else{
            return false;
        }
    }
   public boolean isActionCompleteDistance(Position currentPosition){
        if((currentDestination.x - currentPosition.x) == 0 && (currentDestination.y-currentPosition.y == 0)){
          return true;
       }
        else{
            return false;
        }
   }
}
