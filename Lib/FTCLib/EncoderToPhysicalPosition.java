package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public interface EncoderToPhysicalPosition<T> {
    int getEncoderCountForMovement(double movement, T value);
    int getEncoderCountForPosition(double position, T value);
    double getMovementForEncoderCount(int encoderCount, T value);
    double getPositionForEncoderCount(int encoderCount, T value);
}
