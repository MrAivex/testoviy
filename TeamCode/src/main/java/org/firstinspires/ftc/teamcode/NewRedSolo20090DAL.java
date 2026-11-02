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
@Autonomous(name = "SoloAutoRedDal6", group = "Autonom")
public class NewRedSolo20090DAL extends LinearOpMode {

    public static double X_toballs = 76;
    public static double Y_toballs = -10;

    public static double BWait = 1.2;
    public static double Wait = 0.6;

    public static double X_throw = 114;
    public static double Y_throw = -35;

    public static double Y_takeballs = -36;

    DcMotor throw_motor = null;
    DcMotor leftUpperMotor  = null;
    DcMotor rightUpperMotor = null;
    CRServo get_right  = null;
    CRServo get_left  = null;
    CRServo fd_roll_servo = null;
    CRServo bk_roll_servo = null;
    Servo rt_up_servo = null;
    Servo lt_up_servo = null;


    public class Gun{
        private DcMotor gun;
        public Gun(HardwareMap hardwareMap) {
            gun = hardwareMap.get(DcMotor.class, "throw_motor");
            gun.setDirection(DcMotor.Direction.FORWARD);
            gun.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        public Action ActGun(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    gun.setPower(0.6);
                    return false;
                }
            };
        }
        public Action StopGun() {
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    gun.setPower(0);
                    return false;
                }
            };
        }
    }

    public class Balls {

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

        public Action RollTakeOn(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    fd_roll_servo.setPower(1);
                    bk_roll_servo.setPower(-1);
                    return false;
                }
            };
        }
        
        public Action RollTakeOff(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    fd_roll_servo.setPower(0);
                    bk_roll_servo.setPower(0);
                    return false;
                }
            };
        }
        public Action ScTakeOn(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    get_right.setPower(1);
                    get_left.setPower(1);
                    return false;
                }
            };
        }

        public Action ScTakeOff(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    get_right.setPower(0);
                    get_left.setPower(0);
                    return false;
                }
            };
        }

        public Action ToGun(){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    rt_up_servo.setPosition(0.6);
                    lt_up_servo.setPosition(0.45);
                    fd_roll_servo.setPower(0);
                    return false;
                }
            };
        }

        public Action NoGun(){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    rt_up_servo.setPosition(0.3);
                    lt_up_servo.setPosition(0.15);
                    fd_roll_servo.setPower(1);
                    return false;
                }
            };
        }
    }

    @Override
    public void runOpMode() {



        // Начальная позиция: (0, 0), робот смотрит ВПРАВО (0 градусов)
        Pose2d beginPose = new Pose2d(0, 0, Math.toRadians(0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Gun gun = new Gun(hardwareMap);
        Balls robot = new Balls(hardwareMap);


        // Строим цепочку действий
        Action trajectory = drive.actionBuilder(beginPose)

                // 1. ПРЯМАЯ (STRAFE) - Ехать боком/прямо без поворота корпуса
                // Робот сместится в точку (20, 0), но нос будет смотреть туда же (0 рад)
                .lineToX(20)

                // 2. SPLINE (КЛАССИКА) - Ехать "как машина"
                // Робот поедет по дуге в точку (40, 20).
                // Нос робота будет поворачиваться по ходу движения.
                // Math.PI / 2 (90 град) - это касательная (куда робот должен "влететь" в точку)
                .splineTo(new Vector2d(40, 20), Math.PI / 2)
                .waitSeconds(4)
                // 3. SPLINE CONSTANT HEADING - Ехать по дуге "лунной походкой"
                // Робот сделает дугу к точке (20, 40), но его НОС останется смотреть
                // в ту же сторону, где закончил предыдущий шаг (на 90 градусов)
                // tangent = Math.PI (180 град) задает форму дуги (выгибается влево)
                .splineToConstantHeading(new Vector2d(20, 40), Math.PI)
                .waitSeconds(4)
                // 4. SPLINE LINEAR HEADING - Самое сложное и крутое
                // Едем в точку (0, 0)
                // При этом плавно крутимся, чтобы в конце смотреть на 180 градусов (Math.PI)
                // Tangent (Math.PI * 1.5) задает подход к точке снизу
                .splineToLinearHeading(new Pose2d(0, 0, Math.PI), Math.PI * 1.5)
                .waitSeconds(4)
                // 5. TURN - Просто разворот на месте
                // Повернуться на 90 градусов (радианы) против часовой
                .turn(Math.toRadians(90))
                .waitSeconds(4)
                .build();

        Action toballs = drive.actionBuilder(beginPose)
                .lineToX(X_throw-10)
                .stopAndAdd(gun.ActGun())
                .setTangent(180+45)
                .splineToLinearHeading(new Pose2d(X_throw, Y_throw, Math.toRadians(-53.28)), Math.toRadians(-53.28))
                .stopAndAdd(robot.ScTakeOn()).stopAndAdd(robot.RollTakeOn()).waitSeconds(BWait)//.stopAndAdd(Robot.ScTakeOn())
                .stopAndAdd(robot.ToGun()).waitSeconds(Wait).stopAndAdd(robot.NoGun()).waitSeconds(BWait).stopAndAdd(robot.ToGun())
                .waitSeconds(Wait).stopAndAdd(robot.NoGun()).waitSeconds(BWait).stopAndAdd(robot.ToGun()).waitSeconds(Wait)
                .stopAndAdd(robot.NoGun()) //стреляем

                .setTangent(Math.toRadians(-53.28+180)).splineToLinearHeading(new Pose2d(X_toballs, Y_toballs, Math.toRadians(-90)), -Math.PI/2).stopAndAdd(robot.ScTakeOn()) //отъезжаем
                .setTangent(Math.toRadians(-90)).lineToY(Y_takeballs,new TranslationalVelConstraint(12)).stopAndAdd(robot.ScTakeOff())//.stopAndAdd(Robot.RollTakeOff()) //берем шарики

                .setTangent(Math.toRadians(120)).splineToLinearHeading(new Pose2d(X_throw, Y_throw, Math.toRadians(-53.28)), Math.toRadians(-53.28)).stopAndAdd(robot.ScTakeOn()).stopAndAdd(robot.RollTakeOn())
                .waitSeconds(BWait+0.1).stopAndAdd(robot.ToGun()).waitSeconds(Wait+0.1).stopAndAdd(robot.NoGun()).waitSeconds(BWait+0.1).stopAndAdd(robot.ToGun())
                .waitSeconds(Wait+0.1).stopAndAdd(robot.NoGun()).waitSeconds(BWait+0.1).stopAndAdd(robot.ToGun()).waitSeconds(Wait+0.1)
                .stopAndAdd(robot.NoGun())

//                .setTangent(Math.toRadians(180+45)).splineToLinearHeading(new Pose2d(X_toballs, Y_toballs-24, 0), -Math.PI/2).stopAndAdd(Robot.ScTakeOn()) //отъезжаем
//                .setTangent(0).lineToX(X_takeballs,new TranslationalVelConstraint(12)).stopAndAdd(Robot.ScTakeOff())//.stopAndAdd(Robot.RollTakeOff()) //берем шарики

//                .setTangent(Math.toRadians(120)).splineToLinearHeading(new Pose2d(X_throw, Y_throw, Math.toRadians(90-53.28)), Math.toRadians(90-53.28)) //едем к пушке
//                .stopAndAdd(Gun.ActGun()).waitSeconds(1) .stopAndAdd(Robot.ToGun()).waitSeconds(0.5).stopAndAdd(Robot.NoGun()).waitSeconds(0.5).stopAndAdd(Robot.ToGun())
//                .waitSeconds(0.5).stopAndAdd(Robot.NoGun()).waitSeconds(0.5).stopAndAdd(Robot.ToGun()).waitSeconds(0.5)
//                .stopAndAdd(Robot.NoGun()).stopAndAdd(Gun.StopGun())//стреляем.
//
//                .setTangent(Math.toRadians(180+45)).splineToLinearHeading(new Pose2d(X_toballs, Y_toballs-48, 0), -Math.PI/2).stopAndAdd(Robot.ScTakeOn()) //отъезжаем
//                .setTangent(0).lineToX(X_takeballs,new TranslationalVelConstraint(15)).stopAndAdd(Robot.ScTakeOff())//.stopAndAdd(Robot.RollTakeOff()) //берем шарики

                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(toballs);
    }
}