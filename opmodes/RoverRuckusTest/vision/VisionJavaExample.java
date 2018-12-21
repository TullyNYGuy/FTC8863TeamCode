package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest.vision;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest.vision.MasterVision;

@TeleOp(name = "TensorFlow Test", group = "Test")
//@Disabled
public class VisionJavaExample extends LinearOpMode{
    MasterVision vision;
    SampleRandomizedPositions goldPosition;

    @Override
    public void runOpMode() throws InterruptedException {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;// recommended camera direction
        parameters.vuforiaLicenseKey = "AdFJpV3/////AAAAGfTNpwzHLEWLhTazSUptJDdvoaO1q58UH3Ix0gMjIizeGqeRTy/mHyHpZI3hX3VrQk0S4VJKsiwBIUrTZy57oWoQQGsD/6JXnrC/R2zQ2ruhxmV9JYc6zr5Lhu+aUFdce/WJezBkcUv7fD2y6kmNHAWlYyMx3ZP8YX2bSfTWu4PjiO3N/CFelgIJSz5BCRtYeFb1gKkCYhsqKUNfkWXznEFvX8ppW72yjbfq62QwqGFeuql/3cPce8asiOVo9NLiG9mIuADM+FWLairEHQ4h2euGHa+JNrk36EO0zVAFk9G2RBQJRkwgA7jUOGpCEW0Qqt0XJoM+0T0sOVORrn3Lqp9M4ecaQYsQsR8RPoZRL0Ox";

        vision = new MasterVision(parameters, hardwareMap, true, MasterVision.TFLiteAlgorithm.INFER_NONE);
        vision.init();// enables the camera overlay. this will take a couple of seconds
        vision.enable();// enables the tracking algorithms. this might also take a little time

        waitForStart();

        vision.disable();// disables tracking algorithms. this will free up your phone's processing power for other jobs.

        goldPosition = vision.getTfLite().getLastKnownSampleOrder();

        while(opModeIsActive()){
            telemetry.addData("goldPosition was", goldPosition);// giving feedback

            switch (goldPosition){ // using for things in the autonomous program
                case LEFT:
                    telemetry.addLine("going to the left");
                    break;
                case CENTER:
                    telemetry.addLine("going straight");
                    break;
                case RIGHT:
                    telemetry.addLine("going to the right");
                    break;
                case UNKNOWN:
                    telemetry.addLine("staying put");
                    break;
            }

            telemetry.update();
        }

        vision.shutdown();
    }
}
