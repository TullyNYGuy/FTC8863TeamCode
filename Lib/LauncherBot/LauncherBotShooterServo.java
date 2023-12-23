package org.firstinspires.ftc.teamcode.Lib.LauncherBot;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

import java.util.concurrent.TimeUnit;

public class LauncherBotShooterServo implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum Command {
        INIT,
        NO_COMMAND,
        SHOOT_ONE,
        SHOOT_THREE,
        SHOOT_EIGHT,
        SHOOT
    }

    private Command command = Command.NO_COMMAND;

    private enum State {
        PRE_INIT,
        MOVING_TO_INIT,
        INIT_COMPLETE,
        SHOOTING,
        RETRACTING,
        READY
    }

    private State state = State.PRE_INIT;

    private boolean commandComplete = true;
    private int numberToShoot = 0;
    private int shotsTaken = 0;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863New shooterServo;
    private final String SHOOTER_SERVO = LauncherBotRobot.HardwareName.SHOOTER_SERVO.hwName;
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

    public LauncherBotShooterServo(HardwareMap hardwareMap, Telemetry telemetry) {
        shooterServo = new Servo8863New(SHOOTER_SERVO, hardwareMap, telemetry);
        shooterServo.setDirection(Servo.Direction.REVERSE);
        shooterServo.addPosition("Init", .80, 500, TimeUnit.MILLISECONDS);
        shooterServo.addPosition("Hit", 1.00, 150, TimeUnit.MILLISECONDS);
        shooterServo.addPosition("Drop", .80, 150, TimeUnit.MILLISECONDS);
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

    public void init() {
        command = Command.INIT;
        commandComplete = false;
    }

    public void kick() {
        shooterServo.setPosition("Hit");
    }

    public void retract() {
        shooterServo.setPosition("Drop");
    }

    public boolean isPositionReached() {
        return shooterServo.isPositionReached();
    }

    public void shoot1() {
        // only start this command if there is not a command running already
        if (commandComplete) {
            command = Command.SHOOT;
            numberToShoot = 1;
            shotsTaken = 0;
            commandComplete = false;
        }
    }

    public void shoot3() {
        // only start this command if there is not a command running already
        if (commandComplete) {
            command = Command.SHOOT;
            numberToShoot = 3;
            shotsTaken = 0;
            commandComplete = false;
        }
    }

    public void shoot8() {
        // only start this command if there is not a command running already
        if (commandComplete) {
            command = Command.SHOOT;
            numberToShoot = 8;
            shotsTaken = 0;
            commandComplete = false;
        }
    }

    @Override
    public String getName() {
        return "shooter servo";
    }

    @Override
    public boolean isInitComplete() {
        if (state == State.INIT_COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean init(Configuration config) {
        return false;
    }

    @Override
    public void update() {
        switch(command) {
            case NO_COMMAND: {
                // don't do anything!
            }

            // if there is an init command follow this sequence
            case INIT: {
                switch (state) {
                    case PRE_INIT: {
                        // start the arm moving to the init position
                        shooterServo.setPosition("Init");
                        // next we have to wait for it to get there
                        state = State.MOVING_TO_INIT;
                    }
                    break;
                    case MOVING_TO_INIT: {
                        // wait for the arm to finish moving to the init position
                        if (isPositionReached()) {
                            commandComplete = true;
                            command = Command.NO_COMMAND;
                            state = State.INIT_COMPLETE;
                        }
                    }
                    break;
                }
            }
            break;

            // the command is to shoot. For each shot requested, a kick and retract is needed
            case SHOOT: {
                if(shotsTaken < numberToShoot) {
                    switch(state) {
                        // The shooter is ready for a shot
                        case INIT_COMPLETE:
                        case READY: {
                            // start the arm moving into the magazine
                            kick();
                            state = State.SHOOTING;
                        }
                        break;
                        case SHOOTING: {
                            // has the arm reached the shooting position?
                            if (isPositionReached()) {
                                // yes. So start the arm retracting
                                retract();
                                state = State.RETRACTING;
                            }
                        }
                        break;
                        case RETRACTING: {
                            // has the arm fully retracted?
                            if (isPositionReached()) {
                                // yes so it is ready for the next shot
                                state = State.READY;
                                shotsTaken++;
                            }
                        }
                    }
                } else {
                    // the requested number of shots has been taken
                    commandComplete = true;
                    command = Command.NO_COMMAND;
                    // note that the state was already set to READY when the arm finished retracting
                }
            }
            break;
        }

    }

    @Override
    public void shutdown() {
        retract();
    }

    @Override
    public void setDataLog(DataLogging logFile) {

    }

    @Override
    public void enableDataLogging() {

    }

    @Override
    public void disableDataLogging() {

    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}
