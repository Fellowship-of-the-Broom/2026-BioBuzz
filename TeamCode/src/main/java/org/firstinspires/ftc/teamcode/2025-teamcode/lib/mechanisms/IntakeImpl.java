package org.firstinspires.ftc.teamcode.lib.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class IntakeImpl implements Runnable, Intake {
    private LinearOpMode opMode = null;
    private DcMotor motor = null;
    private final static double POWER = 0.75;
    public IntakeImpl(LinearOpMode OpMode) {
        opMode = OpMode;
        this.motor = opMode.hardwareMap.get(DcMotor.class, "intakeMotor");
        //Jimmy is a placeholder
        //But I like the name Jimmy :(

    }

//    public void start() {
//        Thread thread = new Thread(this);
//        thread.start();
//    }

    @Override
    public void run() {

       // while (this.opMode.opModeIsActive()) {
            //1. read inputs
            //2. process
            //3. set outputs
           this.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           if (this.opMode.gamepad2.x) {
                this.motor.setDirection(DcMotorSimple.Direction.FORWARD);
                this.motor.setPower(POWER);

            } else if(this.opMode.gamepad2.y) {
                this.motor.setDirection(DcMotorSimple.Direction.REVERSE);
                this.motor.setPower(POWER);

            } else {
                this.motor.setPower(0);
            }
            //try {
               // Thread.sleep(100);
            //} catch (InterruptedException e) {
               // throw new RuntimeException(e);
           // }
        }
    }
//}
