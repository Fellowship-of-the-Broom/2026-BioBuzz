package org.firstinspires.ftc.teamcode.lib.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.production.AllianceColor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AprilTag {

    /* RED APPROX.
    Distance =
    Heading =
    Yaw =
     */

    //This might have to be changed like before every competition ): this is for the far launch position

    double finalDesiredDistance = 0;
    double finalDesiredHeading = 0;
    double finalDesiredYaw = 0;


    //Changes in the (physical) chassis change these values due to small differences in how the wheels move, so ajustment will be needed before competitions
    final double FAR_DESIRED_DISTANCE = 121; // this is how close the camera should get to the target (inches)
    final double FAR_DESIRED_HEADING = -1; // Defaults are for Blue alliance
    final double FAR_DESIRED_YAW = 36;

    final double CLOSE_DISTANCE = 6;

    final double PARK_DESIRED_DISTANCE = 127.6;
    final double PARK_DESIRED_HEADING = -5;
    final double PARK_DESIRED_YAW = 17.5;

    private final LinearOpMode opMode;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    final double SPEED_GAIN = 0.02;   //  Forward Speed Control "Gain". e.g. Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN = 0.015;   //  Strafe Speed Control "Gain".  e.g. Ramp up to 37% power at a 25 degree Yaw error.   (0.375 / 25.0)
    final double TURN_GAIN = 0.01;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE = 0.5;   //  Clip the strafing speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)
    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int TAG_BLUE = 20;
    private static final int TAG_RED = 24;
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AllianceColor allianceColor;

    private AprilTagDetection desiredTag;
    public boolean autoAprilTagDetect;
    private double currentDistance = 0;


    public AprilTag(LinearOpMode opMode) {
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = opMode.telemetry;
        this.opMode = opMode;

    }

    public void init(AllianceColor allianceColor) {

        this.allianceColor = allianceColor;

        // Initialize the Apriltag Detection process
        initAprilTag();

        if (USE_WEBCAM)
            setManualExposure(6, 175);  // Use low exposure time to reduce motion blur

        // Wait for driver to press start
        telemetry.addData("Camera preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        //telemetry.update();
    }

    public AprilTagValues checkAprilTag() {
        boolean targetFound = false;    // Set to true when an AprilTag target is detected
        double drive = 0;        // Desired forward power/speed (-1 to +1)
        double strafe = 0;        // Desired strafe power/speed (-1 to +1)
        double turn = 0;        // Desired turning power/speed (-1 to +1)

        desiredTag = null;

        // Step through the list of detected tags and look for a matching tag
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                //  Check to see if we want to track towards this tag.
                if (detection.id == TAG_RED && allianceColor == AllianceColor.RED_ALLIANCE) {
                    // Yes, we want to use this tag.
                    desiredTag = detection;
                } else if (detection.id == TAG_BLUE && allianceColor == AllianceColor.BLUE_ALLIANCE) {
                    // Yes, we want to use this tag.
                    desiredTag = detection;
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                }

            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
            }
        }

        //Far launch
        AprilTagDetection targetTag = desiredTag;
        if ((opMode.gamepad1.right_bumper || autoAprilTagDetect) &&
                (desiredTag != null)) {
            targetFound = true;

            //Sets final desired positions for far launch
            finalDesiredDistance = FAR_DESIRED_DISTANCE;
            finalDesiredHeading = FAR_DESIRED_HEADING;
            finalDesiredYaw = FAR_DESIRED_YAW;
        }

        //Park
        //No april tag detection needed for parking
        if ((opMode.gamepad1.a) &&
                (desiredTag != null)) {
            targetFound = true;

            //Sets final desired positions for parking
            finalDesiredDistance = PARK_DESIRED_DISTANCE;
            finalDesiredHeading = PARK_DESIRED_HEADING;
            finalDesiredYaw = PARK_DESIRED_YAW;
        }

        // Tell the driver what we see, and what to do.
        if (targetFound) {
            //telemetry.addData("\n>", "HOLD Left-Bumper to Drive to Target\n");
            //telemetry.addData("Found", "ID %d (%s)", targetTag.id, targetTag.metadata.name);

        } else {
            //telemetry.addData("\n>", "Drive using joysticks to find valid target\n");
        }

        if (targetTag != null) {
            telemetry.addData("Range", "%5.1f inches", targetTag.ftcPose.range); //130.3
            telemetry.addData("Heading", "%3.0f degrees", targetTag.ftcPose.bearing); //4
            telemetry.addData("Yaw", "%3.0f degrees", targetTag.ftcPose.yaw);//28
//            telemetry.update();
        }

        if (targetTag != null) {
            currentDistance = targetTag.ftcPose.range;
        }

        // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
        if (targetFound) {

            double rangeError = -(targetTag.ftcPose.range - finalDesiredDistance);
            double headingError = (targetTag.ftcPose.bearing - finalDesiredHeading);
            double yawError = -(targetTag.ftcPose.yaw - finalDesiredYaw);


            if (allianceColor == AllianceColor.RED_ALLIANCE) {
                //rangeError = -(targetTag.ftcPose.range - finalDesiredDistance);
                headingError = (targetTag.ftcPose.bearing + finalDesiredHeading);
                yawError = -(targetTag.ftcPose.yaw + finalDesiredYaw);
            }


            // Determine heading, range and Yaw (tag image rotation) error so we can use them to control the robot automatically.


            // Use the speed and turn "gains" to calculate how we want the robot to move.
            drive = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
            turn = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
            strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

            //telemetry.addData("Auto", "Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
        }
        //telemetry.update();


        sleep(10);

        return new AprilTagValues(drive, strafe, turn);
    }

    private void setManualExposure(int exposureMS, int gain) {
        // Wait for the camera to be open, then use the controls

        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            //telemetry.addData("Camera", "Waiting");
            //telemetry.update();
            //while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
            while ((visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                sleep(20);
            }
            //telemetry.addData("Camera", "Ready");
            //telemetry.update();
        }

        // Set camera controls unless we are stopping.
        //if (!isStopRequested())
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
        }
    }

    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // e.g. Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "aprilTagCam"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public double getDistanceValue() {
        double distancePercent = (currentDistance - CLOSE_DISTANCE) / (FAR_DESIRED_DISTANCE - CLOSE_DISTANCE);
        distancePercent = Math.max(distancePercent, 0);
        distancePercent = Math.min(distancePercent, 1);
        telemetry.addData("current distance", currentDistance);
        return distancePercent;


    }
}
