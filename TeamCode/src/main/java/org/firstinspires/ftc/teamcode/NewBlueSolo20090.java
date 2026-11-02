package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@Autonomous(name = "01.NewSoloauto_blue_CLEAN", group = "Autonom")
public class NewBlueSolo20090 extends LinearOpMode {

    // Настройки координат и таймингов
    public static double X_toballs = -30;
    public static double Y_toballs = 43;
    public static double BWait = 1.2;
    public static double Wait = 0.6;
    public static double X_throw = -2;
    public static double Y_throw = 2;
    public static double X_takeballs = 3;

    // Угол прицеливания (вынесен в переменную для удобства)
    public static double SHOOT_ANGLE = -Math.toRadians(90 - 53.28);

    public class Gun {
        private DcMotor gun;
        public Gun(HardwareMap hardwareMap) {
            gun = hardwareMap.get(DcMotor.class, "throw_motor");
            gun.setDirection(DcMotor.Direction.FORWARD);
            gun.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        public Action ActGun() { return packet -> { gun.setPower(0.6); return false; }; }
        public Action StopGun() { return packet -> { gun.setPower(0); return false; }; }
    }

    public class Balls {
        private CRServo get_right, get_left, fd_roll_servo, bk_roll_servo;
        private Servo rt_up_servo, lt_up_servo;

        public Balls(HardwareMap hardwareMap) {
            get_right = hardwareMap.get(CRServo.class, "get_right");
            get_left = hardwareMap.get(CRServo.class, "get_left");
            fd_roll_servo = hardwareMap.get(CRServo.class, "fd_roll_servo");
            bk_roll_servo = hardwareMap.get(CRServo.class, "bk_roll_servo");
            rt_up_servo = hardwareMap.get(Servo.class, "rt_up_servo");
            lt_up_servo = hardwareMap.get(Servo.class, "lt_up_servo");

            get_right.setDirection(CRServo.Direction.FORWARD);
            get_left.setDirection(CRServo.Direction.REVERSE);
            fd_roll_servo.setDirection(CRServo.Direction.FORWARD);
            bk_roll_servo.setDirection(CRServo.Direction.REVERSE);
            lt_up_servo.setDirection(Servo.Direction.FORWARD);
            rt_up_servo.setDirection(Servo.Direction.REVERSE);
        }

        public Action RollTakeOn() { return packet -> { fd_roll_servo.setPower(1); bk_roll_servo.setPower(-1); return false; }; }
        public Action RollTakeOff() { return packet -> { fd_roll_servo.setPower(0); bk_roll_servo.setPower(0); return false; }; }
        public Action ScTakeOn() { return packet -> { get_right.setPower(1); get_left.setPower(1); return false; }; }
        public Action ScTakeOff() { return packet -> { get_right.setPower(0); get_left.setPower(0); return false; }; }

        public Action ToGun() {
            return packet -> {
                rt_up_servo.setPosition(0.6);
                lt_up_servo.setPosition(0.45);
                fd_roll_servo.setPower(0);
                return false;
            };
        }
        public Action NoGun() {
            return packet -> {
                rt_up_servo.setPosition(0.3);
                lt_up_servo.setPosition(0.15);
                fd_roll_servo.setPower(1);
                return false;
            };
        }
    }

    @Override
    public void runOpMode() {
        // Начальная позиция
        Pose2d beginPose = new Pose2d(0, 0, SHOOT_ANGLE);

        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Gun gun = new Gun(hardwareMap);
        Balls robot = new Balls(hardwareMap);

        // Формирование основной траектории
        Action toballs = drive.actionBuilder(beginPose)
                // --- ПЕРВЫЙ ЗАЛП ---
                .stopAndAdd(gun.ActGun())
                .setTangent(Math.toRadians(-225))
                .splineToLinearHeading(new Pose2d(X_throw, Y_throw, SHOOT_ANGLE), SHOOT_ANGLE)
                .stopAndAdd(robot.ScTakeOn())
                .stopAndAdd(robot.RollTakeOn())
                .waitSeconds(BWait)
                .stopAndAdd(robot.ToGun()).waitSeconds(Wait)
                .stopAndAdd(robot.NoGun()).waitSeconds(BWait)
                .stopAndAdd(robot.ToGun()).waitSeconds(Wait)
                .stopAndAdd(robot.NoGun()).waitSeconds(BWait)
                .stopAndAdd(robot.ToGun()).waitSeconds(Wait + 0.4)
                .stopAndAdd(robot.NoGun())

                // --- ОТЪЕЗД ЗА ШАРАМИ #1 ---
                .setTangent(Math.toRadians(-225))
                .splineToLinearHeading(new Pose2d(X_toballs, Y_toballs, 0), Math.PI/2)
                .stopAndAdd(robot.ScTakeOn())
                .setTangent(0)
                .lineToX(X_takeballs, new TranslationalVelConstraint(10))
                .stopAndAdd(robot.ScTakeOff())

                // --- ВТОРОЙ ЗАЛП ---
                .setTangent(-Math.toRadians(120))
                .splineToLinearHeading(new Pose2d(X_throw, Y_throw, SHOOT_ANGLE), SHOOT_ANGLE)
                .stopAndAdd(robot.ScTakeOn())
                .stopAndAdd(robot.RollTakeOn())
                .waitSeconds(BWait)
                .stopAndAdd(robot.ToGun()).waitSeconds(Wait)
                .stopAndAdd(robot.NoGun()).waitSeconds(BWait)
                .stopAndAdd(robot.ToGun()).waitSeconds(Wait)
                .stopAndAdd(robot.NoGun()).waitSeconds(BWait)
                .stopAndAdd(robot.ToGun()).waitSeconds(Wait + 0.4)
                .stopAndAdd(robot.NoGun())

                // --- ОТЪЕЗД ЗА ШАРАМИ #2 ---
                .setTangent(-Math.toRadians(225))
                .splineToLinearHeading(new Pose2d(X_toballs, Y_toballs - 24, 0), Math.PI/2)
                .stopAndAdd(robot.ScTakeOn())
                .setTangent(0)
                .lineToX(X_takeballs, new TranslationalVelConstraint(10))
                .stopAndAdd(robot.ScTakeOff())
                .build();

        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(toballs);
    }
}