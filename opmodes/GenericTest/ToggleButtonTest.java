package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ToggleButtonTest extends OpMode {

    Servo testServo;
    double servoWiggleReturn;


    @Override
    public void init(){
        Time = new ElapsedTime();
        testServo = hardwareMap.servo.get("testServo");
    }

    @Override
    public void start(){
        testServo.setPosition(0);
    }

    @Override
    public void loop(){
        servoWiggle(gamepad1.y, 2, 0, .7, .5);
        testServo.setPosition(servoWiggleReturn);
    }

    ElapsedTime Time;

    public double servoWiggle(boolean Button, float timeDif,double Home, double pos1, double pos2){
        if(Button){
            if(Time.time() > timeDif){
                servoWiggleReturn = pos1;
            }
            else if(Time.time() > timeDif + timeDif){
                servoWiggleReturn = pos2;
                Time.reset();
            }
        }
        if(!Button){
            servoWiggleReturn = Home;
        }
        return servoWiggleReturn;
    }
}
