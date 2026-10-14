package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;

/*
 * This is an example of a more complex path to really test the tuning.
 */
@Autonomous(group = "drive")
public class DriveToHive extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap, telemetry);


        waitForStart();

        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.resetPosAndIMU();

        if (isStopRequested()) return;

//        Working Example

//        TrajectorySequence coolBeans = drive.trajectorySequenceBuilder(new Pose2d())
//                .splineTo(new Vector2d(36, 24), 0)
//                .waitSeconds(2.5)
//                .lineToLinearHeading(new Pose2d (0,0,0))
//                .waitSeconds(2.5)
//                .splineToLinearHeading(new Pose2d(36, 24), 0)
//                .waitSeconds(2.5)
//                .splineToSplineHeading(new Pose2d (0,0), 0)
//                .build();
//        drive.followTrajectorySequence(coolBeans);

        TrajectorySequence toRedSquare = drive.trajectorySequenceBuilder(new Pose2d())
                .lineToSplineHeading(new Pose2d(28.287402, -41.3125, 180))
                .lineToSplineHeading(new Pose2d(0,0,0))
                .build();

        drive.followTrajectorySequence(toRedSquare);

    }
}
