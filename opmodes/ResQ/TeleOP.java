package org.firstinspires.ftc.teamcode.opmodes.ResQ;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Lib.ResQLib.ResQRobot;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;

public class TeleOP extends OpMode {


    ResQRobot robot;

    DcMotor tapeMotor;
    DcMotor sweeperMotor;

    Servo bucketSlide360Servo;
    Servo rampServo;
    Servo barGrabber;


    double driveSpeed = 1;
    double direction = 1;


    @Override
    public void init(){

        robot = ResQRobot.ResQRobotTeleop(hardwareMap, telemetry);

        tapeMotor = hardwareMap.dcMotor.get(RobotConfigMapping.getTapeMeasureMotorName());
        sweeperMotor = hardwareMap.dcMotor.get(RobotConfigMapping.getSweeperMotorName());

        //servo init
        bucketSlide360Servo = hardwareMap.servo.get(RobotConfigMapping.getLinearSlideServoName());
        bucketSlide360Servo.setPosition(.52);
        rampServo = hardwareMap.servo.get(RobotConfigMapping.getRampServoName());
        rampServo.setPosition(0);
        barGrabber = hardwareMap.servo.get("barGrabberServo");
        barGrabber.setPosition(.7);

    }//init

    @Override
    public void start(){

    }//start


    @Override
    public void loop(){


        //updateTelemetry();

        sweeperDirectionToggle(gamepad2.a);
        sweeperRun();

        bucketSliderServoRun(gamepad2.left_stick_x);
        rampRun(gamepad2.b);

        servoSideToggle(gamepad2.right_bumper);
        zipServoTogglePosition(gamepad2.y);

        //climberServoRun(gamepad2.dpad_right);

        barGrab(gamepad1.b);

        tapeMotorAim(gamepad2.dpad_up, gamepad2.dpad_down);

        speedToggle(gamepad1.a);
        driveDirectionToggle(gamepad1.right_bumper);
        driveTypeToggle(gamepad1.left_bumper);
        Drive();

    }//loop


    @Override
    public void stop(){

    }//stop

    void updateTelemetry(){
        telemetry.addData("01", "speed: " + speedMessage);
        telemetry.addData("02", "drive type: " + driveTypeMessage);
        telemetry.addData("03", "direction: " + directionMessage);
        telemetry.addData("04", "servo: " + servoSide);
    }

    void Drive(){
        //tank drive
        if(driveType == DriveType.TANK) {
            robot.driveTrain.tankDrive(((-gamepad1.left_stick_y)*driveSpeed)*direction, ((-gamepad1.right_stick_y)*driveSpeed)*direction);
            tapeMotor.setPower(-gamepad2.right_stick_y);
        }

        //forward/backward (differential)
        if(driveType == DriveType.JOYSTICK){
            robot.driveTrain.differentialDrive(-gamepad1.right_stick_y, -gamepad1.right_stick_x);
            tapeMotor.setPower(-gamepad2.left_stick_y);
        }
    }

    boolean tapeMotorAimPressedUp = false;
    boolean tapeMotorAimPressedDown = false;

    void tapeMotorAim(boolean Up, boolean Down){
        if(Up && !tapeMotorAimPressedUp){
            tapeMotorAimPressedUp = true;
            robot.tapeMeasureWinch.incrementServoPosition(.05);
        }
        if(!Up && tapeMotorAimPressedUp){
            tapeMotorAimPressedUp = false;
        }
        if(Down && !tapeMotorAimPressedDown){
            tapeMotorAimPressedDown = true;
            robot.tapeMeasureWinch.incrementServoPosition(-.05);
        }
        if (!Down && tapeMotorAimPressedDown){
            tapeMotorAimPressedDown = false;
        }
    }

    enum DriveType{
        TANK, JOYSTICK
    }
    DriveType driveType = DriveType.TANK;
    String driveTypeMessage = "tank";
    boolean drivePressed = false;

    void driveTypeToggle(boolean Button){
        if(Button && !drivePressed){
            if(driveType == DriveType.TANK){
                driveType = DriveType.JOYSTICK;
                driveTypeMessage = "joystick";
            }
            else{
                driveType = DriveType.TANK;
                driveTypeMessage = "tank";
            }
            drivePressed = true;
        }
        if(!Button && drivePressed){
            drivePressed = false;
        }
    }

    enum DriveDirection{
        FORWARD, REVERSE
    }
    DriveDirection driveDirection = DriveDirection.FORWARD;
    String directionMessage = "forward";
    boolean directionPressed = false;

    void driveDirectionToggle(boolean Button){
        if(Button && !directionPressed){
            if(driveDirection == DriveDirection.FORWARD){
                direction = -1;
                driveDirection = DriveDirection.REVERSE;
                directionMessage = "reverse";
            }
            else{
                direction = 1;
                driveDirection = DriveDirection.FORWARD;
                directionMessage = "forward";
            }
            directionPressed = true;
        }
        if(!Button && directionPressed){
            directionPressed = false;
        }
    }

    enum Speed{
        HALF, PARTIAL, FULL
    }
    Speed speed = Speed.FULL;
    String speedMessage = "Full";
    boolean speedPressed = false;

    void speedToggle(boolean Button){
        if(Button && !speedPressed){
            if(speed == Speed.FULL){
                speed = Speed.HALF;
                speedMessage = "Half";
            }
            else if(speed == Speed.HALF){
                speed = Speed.PARTIAL;
                speedMessage = "Partial";
            }
            else if(speed == Speed.PARTIAL){
                speed = Speed.FULL;
                speedMessage = "Full";
            }
            speedPressed = true;
        }
        if(!Button && speedPressed){
            speedPressed = false;
        }
    }


    enum ServoSide{
        LEFT_SIDE, RIGHT_SIDE
    }
    ServoSide servoSide;
    String servoSideMessage;
    boolean servoSidePressed = false;

    void servoSideToggle(boolean Button){
        if(Button && !servoSidePressed){
            if(servoSide == ServoSide.LEFT_SIDE){
                servoSide = ServoSide.RIGHT_SIDE;
                servoSideMessage = "right";
            }
            else if(servoSide == ServoSide.RIGHT_SIDE){
                servoSide = ServoSide.LEFT_SIDE;
                servoSideMessage = "left";
            }
            servoSidePressed = true;
        }
        if(!Button && servoSidePressed){
            servoSidePressed = false;
        }
    }


    enum ZipServoState{
        HOME, LOW, MID, HIGH
    }
    ZipServoState zipServoState = ZipServoState.HOME;
    boolean zipServoPressed = false;

    void zipServoTogglePosition(boolean Button){
        if(Button && !zipServoPressed){
            if(servoSide == ServoSide.LEFT_SIDE){
                switch(zipServoState){
                    case HOME:
                        robot.leftZipLineServo.goHome();
                        break;
                    case LOW:
                        robot.leftZipLineServo.goLowerGuy();
                        break;
                    case MID:
                        robot.leftZipLineServo.goMiddleGuy();
                        break;
                    case HIGH:
                        robot.leftZipLineServo.goUpperGuy();
                        break;
                    default:
                        break;
                }
            }
            else if(servoSide == ServoSide.RIGHT_SIDE){
                switch(zipServoState){
                    case HOME:
                        robot.rightZipLineServo.goHome();
                        break;
                    case LOW:
                        robot.rightZipLineServo.goLowerGuy();
                        break;
                    case MID:
                        robot.rightZipLineServo.goMiddleGuy();
                        break;
                    case HIGH:
                        robot.rightZipLineServo.goUpperGuy();
                        break;
                    default:
                        break;
                }
            }
            zipServoPressed = true;
        }
        if(!Button && zipServoPressed){
            zipServoPressed = false;
        }
    }


    enum SweeperDirection{
        FORWARDS, NEUTRAL, REVERSE
    }
    SweeperDirection sweeperDirection = SweeperDirection.NEUTRAL;
    String sweeperMessage;
    boolean sweeperPressed = false;

    void sweeperDirectionToggle(boolean Button){
        if(Button && !sweeperPressed){
            if(sweeperDirection == SweeperDirection.NEUTRAL){
                sweeperDirection = SweeperDirection.FORWARDS;
                sweeperMessage = "forwards";
            }
            else if(sweeperDirection == SweeperDirection.FORWARDS){
                sweeperDirection = SweeperDirection.REVERSE;
                sweeperMessage = "reverse";
            }
            else if(sweeperDirection == SweeperDirection.REVERSE){
                sweeperDirection = SweeperDirection.NEUTRAL;
                sweeperMessage = "neutral";
            }
            sweeperPressed = true;
        }
        if(!Button && sweeperPressed){
            sweeperPressed = false;
        }
    }

    void sweeperRun(){
        if(sweeperDirection == SweeperDirection.NEUTRAL){
            sweeperMotor.setPower(0);
        }
        else if(sweeperDirection == SweeperDirection.FORWARDS){
            sweeperMotor.setPower(1);
        }
        else if(sweeperDirection == SweeperDirection.REVERSE){
            sweeperMotor.setPower(-1);
        }
    }


    void climberServoRun(boolean Button){
        if(Button) {
            robot.climberDumpServo.goDumpClimber();
        }
        else{
            robot.climberDumpServo.goHome();
        }
    }

    void bucketSliderServoRun(double Joystick){
        robot.deliveryBox.updateDeliveryBox(Joystick);
    }

    boolean barPressed = false;

    void barGrab(boolean Button){
        if(Button && !barPressed){
            if(barGrabber.getPosition() == 0){
                barGrabber.setPosition(.7);
            }
            else if(barGrabber.getPosition() == .7){
                barGrabber.setPosition(0);
            }
            barPressed = true;
        }
        if(!Button && barPressed){
            barPressed = false;
        }
    }

    void rampRun(boolean Button){
        if(Button){
            robot.deliveryBox.raiseDumpRamp();
        }
        if(!Button){
            robot.deliveryBox.lowerDumpRamp();
        }
    }
}
