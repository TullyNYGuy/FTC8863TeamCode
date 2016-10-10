package org.firstinspires.ftc.teamcode.opmodes.ResQTest;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by ball on 1/9/2016.
 */
public class TestTrapDoor extends OpMode{

    double doorPosition = .25;
    Servo trapDoorServo;

    public TestTrapDoor() {

    }

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {
        trapDoorServo = hardwareMap.servo.get("slideServo");

    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {

        if (gamepad1.a) {
            doorPosition = .25;
            trapDoorServo.setPosition(doorPosition);
        }
        if (gamepad1.b) {
            doorPosition = 0;
            trapDoorServo.setPosition(doorPosition);
        }
    }

    @Override
    public void stop() {

    }







}

