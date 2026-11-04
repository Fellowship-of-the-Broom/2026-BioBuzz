package org.firstinspires.ftc.teamcode.lib.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Chassis implements Runnable {

    private final LinearOpMode opMode;
    private final AprilTag aprilTag;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    //  Drive = Error * Gain    Make th  //  Clip the turn speed to this max value (adjust for your robot)

    private DcMotor frontLeftDrive = null;  //  Used to control the left front drive wheel
    private DcMotor frontRightDrive = null;  //  Used to control the right front drive wheel
    private DcMotor backLeftDrive = null;  //  Used to control the left back drive wheel
    private DcMotor backRightDrive = null;  //  Used to control the right back drive wheel

    private boolean driveBackwards = false;
    private boolean lastButtonState1B = false;
    public Chassis(LinearOpMode opMode, AprilTag aprilTag) {
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = opMode.telemetry;
        this.opMode = opMode;
        this.aprilTag = aprilTag;
    }

    public void init() {


        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        frontLeftDrive = hardwareMap.get(DcMotor.class, "motor0");
        frontRightDrive = hardwareMap.get(DcMotor.class, "motor1");
        backLeftDrive = hardwareMap.get(DcMotor.class, "motor2");
        backRightDrive = hardwareMap.get(DcMotor.class, "motor3");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);


        // Wait for driver to press start
        //telemetry.addData("Camera preview on/off", "3 dots, Camera Stream");
        //telemetry.addData(">", "Touch START to start OpMode");
        //telemetry.update();
    }

    //    public void start() {
//    Thread thread = new Thread(this);
//        thread.start();
//    }
    
    @Override
    public void run() {

        double drive = 0;        // Desired forward power/speed (-1 to +1)
        double strafe = 0;        // Desired strafe power/speed (-1 to +1)
        double turn = 0;        // Desired turning power/speed (-1 to +1)

        // while (opMode.opModeIsActive()) {

        drive = -opMode.gamepad1.left_stick_y / 2.0;  // Reduce drive rate to 50%.
        strafe = -opMode.gamepad1.left_stick_x / 2.0;  // Reduce strafe rate to 50%.
        turn = -opMode.gamepad1.right_stick_x / 3.0;  // Reduce turn rate to 33%.

        if ((opMode.gamepad1.b != lastButtonState1B) && opMode.gamepad1.b) {
            driveBackwards = !driveBackwards;
        }

        lastButtonState1B = opMode.gamepad1.b;
         
        if (driveBackwards) {
            drive = -drive;
            strafe = -strafe;
        }

        telemetry.addData("Manual", "Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);

        //telemetry.update();

        if ((drive == 0) && (strafe == 0) && (turn == 0)) {
            AprilTagValues aprilTagValues = aprilTag.checkAprilTag();

            drive = aprilTagValues.drive;
            strafe = aprilTagValues.strafe;
            turn = aprilTagValues.turn;
        }
        // Apply desired axes motions to the drivetrain.
        moveRobot(drive, strafe, turn);
        sleep(10);
        //}
    }

    /**
     * Move robot according to desired axes motions
     * <p>
     * Positive X is forward
     * <p>
     * Positive Y is strafe left
     * <p>
     * Positive Yaw is counter-clockwise
     */
    public void moveRobot(double drive, double strafe, double turn) {
        // Calculate wheel powers.
        double frontLeftPower = drive - strafe - turn;
        double frontRightPower = drive + strafe + turn;
        double backLeftPower = drive + strafe - turn;
        double backRightPower = drive - strafe + turn;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Send powers to the wheels.
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);

        telemetry.addData("FL Power:", frontLeftPower);
    }



    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
