package org.firstinspires.ftc.teamcode.roadrunner.drive.opmode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;

/*
 * This is a simple routine to test translational drive capabilities.
 */
@Config
@Autonomous(group = "drive")
public class StraightTest extends LinearOpMode {
    public static double DISTANCE = 60; // in

    @Override
    public void runOpMode() throws InterruptedException {
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap, telemetry);

        Trajectory trajectory = drive.trajectoryBuilder(new Pose2d())
                .forward(DISTANCE)
                .build();

        waitForStart();

        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.resetPosAndIMU();

        if (isStopRequested()) return;

        drive.followTrajectory(trajectory);

        Pose2d pose = drive.getPoseEstimate();
        Pose2d error = drive.getLastError();

        telemetry.addData("Final X", pose.getX());
        telemetry.addData("Final Y", pose.getY());
        telemetry.addData("Final Heading", Math.toDegrees(pose.getHeading()));

        telemetry.addData("Error X", error.getX());
        telemetry.addData("Error Y", error.getY());
        telemetry.addData("Error Heading", Math.toDegrees(error.getHeading()));

        telemetry.update();

        sleep(10000);   // Leave it on the screen for 10 seconds

//        Pose2d poseEstimate = drive.getPoseEstimate();
//        telemetry.addData("finalX", poseEstimate.getX());
//        telemetry.addData("finalY", poseEstimate.getY());
//        telemetry.addData("finalHeading", poseEstimate.getHeading());

//        telemetry.addData("x in", pinpoint.getPosX(DistanceUnit.INCH));
//        telemetry.addData("y in", pinpoint.getPosY(DistanceUnit.INCH));
//        telemetry.addData("heading (deg)", pinpoint.getHeading(AngleUnit.DEGREES));
//        telemetry.update();


        while (!isStopRequested() && opModeIsActive()) ;


    }
}
