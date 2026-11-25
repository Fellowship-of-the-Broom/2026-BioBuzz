package org.firstinspires.ftc.teamcode.lib.mechanisms;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.PinpointLocalizer;
import org.firstinspires.ftc.teamcode.production.AllianceColor;

public class TurretImpl implements Turret {
    private final Telemetry telemetry;
    private final HardwareMap hardwareMap;
    private PinpointLocalizer pinpointLocalizer;
    //TODO Measure constants
    private final double CELL1_X = 2;
    private final double CELL1_Y = 2;

    private final double CELL2_X = 3;
    private final double CELL2_Y = 2;
    private final double TICKS_PER_DEGREE = 2;

    //TODO Add more fields because there are two turrets
    public TurretImpl(LinearOpMode opMode, PinpointLocalizer pinpointLocalizer) {
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = opMode.telemetry;
        this.pinpointLocalizer = pinpointLocalizer;
    }

    @Override //hack into the mainframe
    public void run() {
        Pose2d robotPosition = pinpointLocalizer.getPose();
        double robotX = robotPosition.position.x;
        double robotY = robotPosition.position.y;
        double robotHeading = Math.toDegrees(robotPosition.heading.toDouble());

        CRServo turretServo = hardwareMap.get(CRServo.class, "turretServo");
        DcMotorEx turretEncoder = hardwareMap.get(DcMotorEx.class, "turretEncoder");

        double desiredTurretHeading = Math.toDegrees(Math.atan2(CELL1_Y - robotY, CELL1_X - robotX)) - robotHeading;
        //math.atan outputs in radians

        double encoderTicks = turretEncoder.getCurrentPosition();
        double encoderDegrees = encoderTicks / TICKS_PER_DEGREE;
        double error = desiredTurretHeading - encoderDegrees;

        if (error > 5){
            turretServo.setPower(-1);
        }

        if (error < -5){
            turretServo.setPower(1);
        }

    }
}