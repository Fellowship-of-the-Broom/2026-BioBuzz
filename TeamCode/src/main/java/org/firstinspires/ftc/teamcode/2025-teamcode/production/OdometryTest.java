package org.firstinspires.ftc.teamcode.production;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;

@TeleOp(name = "Odometry Test", group = "Test")
public class OdometryTest extends LinearOpMode {

    @Override
    public void runOpMode() {

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap, telemetry);


        waitForStart();

        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        //Set values to 0
        pinpoint.resetPosAndIMU();

        while (opModeIsActive()) {

            drive.update();

            Pose2d pose = drive.getPoseEstimate();

//            telemetry.addData("x ticks", pose.getX());
//            telemetry.addData("y ticks", pose.getY());
//            telemetry.addData("heading (deg)", Math.toDegrees(pose.getHeading()));
//            telemetry.addData("x cm", (pose.getX()/1.9894));
//            telemetry.addData("y cm", (pose.getY()/1.9894));
//            telemetry.update();

            telemetry.addData("x in", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("y in", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("heading (deg)", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.addData("getWheelPositions", drive.getWheelPositions());


            Pose2d rrPose = drive.getPoseEstimate();
            Pose2d error = drive.getLastError();

            telemetry.addData(
                    "Pinpoint raw heading",
                    pinpoint.getHeading(AngleUnit.DEGREES)
            );
            telemetry.addData(
                    "RR heading",
                    Math.toDegrees(rrPose.getHeading())
            );
            telemetry.addData(
                    "RR heading error",
                    Math.toDegrees(error.getHeading())
            );
            telemetry.update();

        }
    }
}
