package org.firstinspires.ftc.teamcode.roadrunner.drive.opmode;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

// Replace with your actual driver import if different
//import org.firstinspires.ftc.teamcode.hardware.GoBildaPinpointDriver;

public class PinpointLocalizer implements Localizer {

    private Pose2d poseEstimate = new Pose2d(0, 0, 0);
    private Pose2d poseVelocity = new Pose2d(0, 0, 0);

    private final GoBildaPinpointDriver pinpoint;

    public PinpointLocalizer(HardwareMap hardwareMap) {
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.resetPosAndIMU();
    }

    @NonNull
    @Override
    public Pose2d getPoseEstimate() {
        return poseEstimate;
    }

    @Override
    public void setPoseEstimate(@NonNull Pose2d pose) {
        poseEstimate = pose;

        Pose2D pinpointPose = new Pose2D(
                DistanceUnit.INCH,
                -pose.getX(),
                -pose.getY(),
                AngleUnit.RADIANS,
                pose.getHeading()
        );

        pinpoint.setPosition(pinpointPose);
    }

    @Override
    public Pose2d getPoseVelocity() {
        return poseVelocity;
    }

    @Override
    public void update() {
        // Pull fresh data from Pinpoint
        pinpoint.update();

        // TODO Other place to tune values
        // ---- READ VALUES ----
        double x = pinpoint.getPosX(DistanceUnit.INCH);           // VERIFY UNITS
        double y = pinpoint.getPosY(DistanceUnit.INCH);           // VERIFY UNITS
        double heading = pinpoint.getHeading(AngleUnit.RADIANS); // should be radians

        // ---- UNIT CONVERSION (adjust if needed) ----
        // If Pinpoint returns mm:
        // x /= 25.4;
        // y /= 25.4;

        // ---- AXIS CORRECTION (adjust if needed) ----
        // If axes are swapped or inverted, fix here:
        // Example:
        // double correctedX = y;
        // double correctedY = -x;

        double correctedX = -x;
        double correctedY = -y;
        double correctedHeading = heading;

        poseEstimate = new Pose2d(correctedX, correctedY, correctedHeading);

//        // ---- VELOCITY (optional but helpful) ----
//        double vx = pinpoint.getVelocityX();
//        double vy = pinpoint.getVelocityY();
//        double omega = pinpoint.getHeadingVelocity();
//
//        // If needed, convert mm/s → in/s:
//        // vx /= 25.4;
//        // vy /= 25.4;

      //  poseVelocity = new Pose2d(vx, vy, omega);
    }
}
