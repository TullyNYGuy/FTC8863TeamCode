package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy.TeleopUsingRoadRunnerFreightFrenzy;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class FFExtensionArm {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
private enum LiftState {
        IDLE,
        //WAITING_FOR_INTAKE,
        EXTEND_TO_1ST_POSITION,
        EXTENDED_AT_1ST_POSITION,
        EXTEND_TO_2ND_POSITION,
        EXTENDED_AT_2ND_POSTION,
        EXTEND_TO_FINAL_POSITION,
        EXTENDED_AT_FINAL_POSITION,
        WAITING_TO_DUMP,
        DUMP,
        RETRACT_TO_2,
        RETRACTED_2,
        RETRACT_TO_1ST_POSITION,
        RETRACTED_1,
        RETRACT_TO_0,
        RETRACTED_0



    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Switch retractionSwitch;
    private Switch extensionSwitch;
    private ExtensionRetractionMechanism ffExtensionArm;
    private DataLogging log;
    private FFExtensionArm.LiftState liftState = LiftState.IDLE;
    private Servo8863New deliveryServo;
    private ElapsedTime timer;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
   public FFExtensionArm(HardwareMap hardwareMap, Telemetry telemetry){
       ffExtensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
               "Lift",
               "ExtensionLimitSwitch",
               "RetractionLimitSwitch",
               "LiftMotor",
               DcMotor8863.MotorType.GOBILDA_435,
               4.517);
       ffExtensionArm.reverseMotorDirection();

       ffExtensionArm.setResetTimerLimitInmSec(25000);
       ffExtensionArm.setExtensionPower(0.9);
       ffExtensionArm.setExtensionPositionInMechanismUnits(31.0);
       ffExtensionArm.setRetractionPower(-0.5);
       ffExtensionArm.setRetractionPositionInMechanismUnits(3.0);
       ffExtensionArm.setDataLog(log);
       ffExtensionArm.enableDataLogging();

       deliveryServo = new Servo8863New("deliveryServo" , hardwareMap, telemetry);
       deliveryServo.addPosition( "1.5 Extension",0.94,500, TimeUnit.MILLISECONDS);
       deliveryServo.addPosition( "3 Extension",0.90,500, TimeUnit.MILLISECONDS);
       deliveryServo.addPosition( "Transfer",0.97,500, TimeUnit.MILLISECONDS);
       deliveryServo.addPosition( "Parallel",0.83,500, TimeUnit.MILLISECONDS);
       deliveryServo.addPosition( "Dump",0.13,500, TimeUnit.MILLISECONDS);

       timer = new ElapsedTime();
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

    public void dump(){
       //this commnad is the button press for dumping freight into the hub
       liftState = LiftState.DUMP;
    }


    public void update() {
        switch (liftState) {
            case IDLE: {
                // do nothing
            }
            break;

            // YOU NEED TO THINK ABOUT HOW YOU ARE GOING TO HANDLE 3 DIFFERENT ARM/SERVO COMBINATIONS
            // EXTENDING / ROTATING THE ARM TO:
            //    LEVEL 3 OF THE SHIPPING HUB
            //    LEVEL 2 OF THE SHIPPING HUB
            //    LEVEL 1 OF THE SHIPPING HUB

            // AND 3 DIFFERENT RETRACT / ROTATION SEQUENCES IN THE OPPOSITE DIRECTIONS

            // AND THE DIFFERENT DUMPS - DON'T FORGET THE DRIVER MAY WANT TO LINE UP, THEN DUMP

            // I'M NOT SEEING THE DIFFERENCES BETWEEN THEM IN THE CODE BELOW.
            
            case EXTEND_TO_1ST_POSITION: {
                //starts the extension to 1.5 inches
                ffExtensionArm.goToPosition(1.5, 0.3);
                // DON'T YOU NEED A SEPARATE STATE FOR THIS? YOU WILL BE ISSUING THE ABOVE COMMAND
                // ABOUT A BAZILLION TIMES WHILE THIS STATE SITS AND WAITS FOR THE POSITION TO BE
                // REACHED. PROBABLY BETTER TO ONLY ISSUE IT ONCE AND THEN MOVE TO A NEW STATE TO
                // CEHCK FOR THE POSITION TO BE REACHED.
                if (ffExtensionArm.isPositionReached()) {
                    //the arm has extended to 1.5 so now the servo moves to the correct position
                    deliveryServo.setPosition("1.5 Extension");
                    timer.reset();
                    liftState = LiftState.EXTENDED_AT_1ST_POSITION;
                }


            }
            break;


            case EXTENDED_AT_1ST_POSITION: {
                // this is to make sure the servo has moved. the time delay is a testing thing.
                //this also starts the movement to 3 inches
                // THE SERVO HAS A BUILT IN TIMER TO ALLOW FOR ITS MOVEMENT. SO THE STATEMENT BELOW
                // REALLY BOILS DOWN TO WHICH IS THE LONGER TIME, THE SERVO TIMER, OR THE TIMER YOU
                // STARTED. THE LENGTH OF TIME IS NOT THE SUM OF THE SERVO TIMER AND YOUR TIMER.
                // THIS MAY BE WHAT YOU INTEND BUT I WANTED TO POINT IT OUT.
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    ffExtensionArm.goToPosition(3, 0.3);
                    liftState = LiftState.EXTEND_TO_2ND_POSITION;
                }


            }
            break;


            case EXTEND_TO_2ND_POSITION: {
                // if the extension arm has made it to 3 inches, it starts the servo movement
                if (ffExtensionArm.isPositionReached()) {
                    deliveryServo.setPosition("3 Extension");
                    timer.reset();
                    liftState = LiftState.EXTENDED_AT_2ND_POSTION;
                }
            }
            break;


            case EXTENDED_AT_2ND_POSTION: {
                //once again checks servo movement and the timer is for testing. starts movement to top level extension.
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 3000) {
                    ffExtensionArm.goToPosition(24.75, 0.3);
                    liftState = LiftState.EXTEND_TO_FINAL_POSITION;
                    timer.reset();
                }
            }
            break;


            case EXTEND_TO_FINAL_POSITION: {
                // checks extension and the timer is for testing
                if (ffExtensionArm.isPositionReached() && timer.milliseconds() > 5000) {
                    liftState = LiftState.WAITING_TO_DUMP;
                }
            }
            break;


            case WAITING_TO_DUMP: {
                //this is essentially just Idle with a different name. waiting for driver to line up & push dump button
                // also resets a timer constantly for use in dump state
                timer.reset();
            }
            break;


            case DUMP: {
                // dump freight into top level. here the timer does serve the purpose of making sure the delivery is clear,
                // but the time can probably be shortened
                deliveryServo.setPosition("Dump");
                if (deliveryServo.isPositionReached() && timer.milliseconds() > 1000) {

                }
            }
            break;
            case RETRACT_TO_2: {
              // move servo to 3 inch positon
            }
            break;


            case RETRACTED_2: {
            // retract to 3 inch
            }
            break;


            case RETRACT_TO_1ST_POSITION: {
            //move servo to 1.5 inch
            }
            break;


            case RETRACTED_1: {
            // retract to 1.5 inch
            }
            break;


            case RETRACT_TO_0: {
            // move servo to transfer  position
            }
            break;

            case RETRACTED_0: {
            //retract to transfer
            }
            break;
        }
    }

}
