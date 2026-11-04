package org.firstinspires.ftc.teamcode.lib.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LauncherImpl implements Runnable, Launcher {
    private static final double MANUAL_SPEED_HIGH = 1;
    private static final double MANUAL_SPEED_LOW = 0.5;
    private static final double ANGLE_3FT = 0.1;
    private static final double SPEED_3FT = 1;
    private static final double manualIncrementQuotient = 165; //Divides
    private static final double FAR_LAUNCHER_SPEED = 0.7000;
    private static final double FAR_HOOD_ANGLE =  0.1075;

    private static final double CLOSE_LAUNCHER_SPEED = 0.4;
    private static final double CLOSE_HOOD_ANGLE =  0.1;
    public static final double MINIMUM_HOOD_ANGLE = 0.0790;
    public static final double MAXIMUM_HOOD_ANGLE = 0.1157;
    private final Telemetry telemetry;
    private AprilTag aprilTag = null;
    private LinearOpMode opMode = null;
    private DcMotor flywheelMotor = null;
   // private DcMotorEx flywheelMotor = null;
    private Servo hoodServo = null;
    private double power = 0;
    private double hoodAngle = 0;
    private double triggerThreshold = 0.8;
    
    //Auto "emulation"
    public boolean autoFarLaunch;
    public boolean autoCloseLaunch;


    public LauncherImpl(LinearOpMode opMode, AprilTag aprilTag) {

        this.opMode = opMode;
        this.telemetry = opMode.telemetry;
        this.aprilTag = aprilTag;

        this.flywheelMotor = opMode.hardwareMap.get(DcMotor.class, "flywheelMotor");
        this.flywheelMotor = opMode.hardwareMap.get(DcMotorEx.class, "flywheelMotor");

        this.hoodServo = opMode.hardwareMap.get(Servo.class, "hoodServo");

        //flywheelMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients());
    }

//    public void start() {
//        Thread thread = new Thread(this);
//        thread.start();
//    }

    @Override
    public void run() {
        // Adjusting launcher based on april tags
        double distanceValue = aprilTag.getDistanceValue();
        double currentHoodAngle = ((FAR_HOOD_ANGLE-CLOSE_HOOD_ANGLE)*distanceValue)+CLOSE_HOOD_ANGLE;
        double currentLauncherSpeed = ((FAR_LAUNCHER_SPEED-CLOSE_LAUNCHER_SPEED)*distanceValue)+CLOSE_LAUNCHER_SPEED;

        // while (this.opMode.opModeIsActive()) {

        // Flywheel motor
        // Not sure about the trigger and how this is going to be used on the gamepad
        this.flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.flywheelMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        // MANUAL LOGIC

        if (this.opMode.gamepad2.right_trigger > 0 && this.opMode.gamepad2.right_trigger < triggerThreshold) {
            power = MANUAL_SPEED_LOW;
        } else if (this.opMode.gamepad2.right_trigger > 0 && this.opMode.gamepad2.right_trigger > triggerThreshold) {
            power = MANUAL_SPEED_HIGH;
        } else if (this.opMode.gamepad2.b){
            power = currentLauncherSpeed;
        } else {
            power = 0;
        }

        if (opMode.gamepad2.left_stick_y != 0) {
            hoodAngle = hoodServo.getPosition() + (opMode.gamepad2.left_stick_y / manualIncrementQuotient);
            hoodAngle = Math.max(hoodAngle, MINIMUM_HOOD_ANGLE);
            hoodAngle = Math.min(hoodAngle, MAXIMUM_HOOD_ANGLE);

            //.079 Max
            //.0.1157 Min
        } else if (this.opMode.gamepad2.b){
            hoodAngle = currentHoodAngle;
        }



        //AUTO LOGIC

//        if (opMode.gamepad1.right_bumper) {
//            //Set power to zero when the right trigger 2 is not pressed down
//            power = 0;
//            hoodAngle = ANGLE_3FT;
//            if (this.opMode.gamepad2.right_trigger > 0) {
//                power = SPEED_3FT;
//            }
//        }

        if (opMode.gamepad2.right_bumper || autoFarLaunch) {
            //Set power to zero when the right trigger 2 is not pressed down
            power = FAR_LAUNCHER_SPEED;
            hoodAngle = FAR_HOOD_ANGLE;
        }

        if (opMode.gamepad2.left_bumper || autoCloseLaunch) {
            //TODO Tune these values
            //Set power to zero when the right trigger 2 is not pressed down
            power = CLOSE_LAUNCHER_SPEED;
            hoodAngle = CLOSE_HOOD_ANGLE;
        }

        this.flywheelMotor.setPower(power);
        hoodServo.setPosition(hoodAngle);

        telemetry.addData("hoodAngle", hoodAngle);
        telemetry.addData("hoodservo", hoodServo.getPosition());
        telemetry.addData("distance percent", distanceValue);
//        telemetry.update();
        //yayayayaya good yes code josh waz here

//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }


    }



}
//}