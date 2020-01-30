package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;

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
    final private double RED_NEAR_BRIDGE_X = 10.0;
    final private double RED_NEAR_BRIDGE_Y = 10.0;
    final private double BLUE_NEAR_BRIDGE_X = 10.0;
    final private double BLUE_NEAR_BRIDGE_Y = 10.0;
    final private DistanceUnit distanceUnit = DistanceUnit.CM;

    /*
     * Interval in milliseconds in which movement control task runs
     */
    final private long MOVEMENT_THREAD_INTERVAL = 50;

    private enum Areas {
        BUILDSITE, BRIDGE, BLOCK, PLATFORM, HOME, NEARBRIDGE
    }

    public boolean blockState;
    Areas place;

    private enum Color {
        BLUE, RED
    }

    private Color color;

    private Position currentDestination;

    private Map<Areas, Position> places;

    private SkystoneRobot robot;
    private Telemetry telemetry;

    private ScheduledExecutorService scheduler;
    private MovemenetThread movementThread;
    private ScheduledFuture<?> movementTask;

    class MovemenetThread implements Runnable {

        private PIDControl xControl;
        private PIDControl yControl;
        private PIDControl rotationControl;
        private MecanumCommands commands;
        private Position current;
        private DistanceUnit distanceUnit;
        private MecanumCommands zeroMovement;

        public MovemenetThread(DistanceUnit distanceUnit) {
            xControl = new PIDControl(0.2, 20.0, .3);
            yControl = new PIDControl(0.2, 20.0, .3);
            rotationControl = new PIDControl(0.2, 20.0, .3);
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

        @Override
        public void run() {
            double valX;
            double valY;
            double valRot;
            robot.getCurrentPosition(current);
            double currentRotation = robot.getCurrentRotation(AngleUnit.RADIANS);
            synchronized (this) {
                valX = xControl.getCorrection(current.x);
                valY = yControl.getCorrection(current.y);
                valRot = rotationControl.getCorrection(currentRotation);
            }
            telemetry.addData("current X: ", current.x);
            telemetry.addData("current Y: ", current.y);
            telemetry.update();
            commands.setSpeed(Math.sqrt(valX * valX + valY * valY));
            commands.setAngleOfTranslation(AngleUnit.RADIANS, Math.atan2(valY, valX));
            commands.setSpeedOfRotation(valRot);
            robot.setMovement(commands);
        }
    }

    public AutonomousController(SkystoneRobot robot, Telemetry telemetry) {
        places = new HashMap<Areas, Position>();
        this.robot = robot;
        this.telemetry = telemetry;
        movementThread = new MovemenetThread(distanceUnit);
        scheduler = Executors.newScheduledThreadPool(2);
        movementTask = null;
    }


    public void startController() {
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


    public void moveTo(DistanceUnit distanceUnit, double x, double y) {
        movementThread.setDestination(distanceUnit, x, y);
        movementThread.setDestinationRotation(AngleUnit.RADIANS, 0);
    }

    public void initPlaces() {
        if (color == Color.BLUE) {
            places.put(Areas.BUILDSITE, new Position(distanceUnit, BLUE_BUILDSITE_X, BLUE_BUILDSITE_Y, 0, 0));
            places.put(Areas.BRIDGE, new Position(distanceUnit, BLUE_BRIDGE_X, BLUE_BRIDGE_Y, 0, 0));
            places.put(Areas.PLATFORM, new Position(distanceUnit, BLUE_PLATFORM_X, BLUE_PLATFORM_Y, 0, 0));
            places.put(Areas.HOME, new Position(distanceUnit, BLUE_HOME_X, BLUE_HOME_Y, 0, 0));
            places.put(Areas.NEARBRIDGE, new Position(distanceUnit, BLUE_NEAR_BRIDGE_X, BLUE_NEAR_BRIDGE_Y, 0, 0));

        } else {
            places.put(Areas.BUILDSITE, new Position(distanceUnit, RED_BUILDSITE_X, RED_BUILDSITE_Y, 0, 0));
            places.put(Areas.BRIDGE, new Position(distanceUnit, RED_BRIDGE_X, RED_BRIDGE_Y, 0, 0));
            places.put(Areas.PLATFORM, new Position(distanceUnit, RED_PLATFORM_X, RED_PLATFORM_Y, 0, 0));
            places.put(Areas.HOME, new Position(distanceUnit, RED_HOME_X, RED_HOME_Y, 0, 0));
            places.put(Areas.NEARBRIDGE, new Position(distanceUnit, RED_NEAR_BRIDGE_X, RED_NEAR_BRIDGE_Y, 0, 0));

        }
    }

    public void goTo(Areas place) {
        this.place = place;
        Position p = places.get(place);
        if (p != null) {
            moveTo(distanceUnit, p.x, p.y);
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
        //use vison
    }


    private void intakeBlock() {
        //use intake system

    }

    private void dropBlock() {
//just let go of the block with the arm
    }

    public void park() {
        goTo(Areas.BRIDGE);
    }

    public void parkWithArm(double rotationToBridge) {
        goTo(Areas.NEARBRIDGE);
        movementThread.setDestinationRotation(AngleUnit.DEGREES, rotationToBridge);
        extendArm();


    }

    private void extendArm() {
        //use the arm class
    }

    public void moveBase(double rotation) {
        goTo(Areas.PLATFORM);
        grabBase();
        movementThread.setDestinationRotation(AngleUnit.DEGREES, rotation);
        goTo(Areas.BUILDSITE);
        letGoBase();
    }

    private void letGoBase() {
        //use the new servo we'll be putting on
    }

    private void grabBase() {
        //use then new servo
    }



    public void putBlockOnBase() {
        findBlock();
        pickUpBlock();
        goTo(Areas.PLATFORM);
        armUp();
        extendArm();
        alignBlock();
        armDown();
        dropBlock();

    }

    private void alignBlock() {
        //use vision and/or arm
    }

    private void armDown() {
        //use arm
    }

    private void armUp() {
        //use arm
    }

}
