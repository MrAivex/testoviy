package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.Objects;

// Дополнительные зависимости для одометрии
import com.qualcomm.hardware.bosch.BNO055IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;


import java.util.Locale;

@Autonomous(name="NoMind", group="Auto_drive")

public class Auto extends LinearOpMode {

    // Доп переменные для одометрии {{

    // Датчики одометрии (как моторы)
    private DcMotor parallelPod = null;    // Параллельное колесо (вперед/назад)
    private DcMotor perpendicularPod = null; // Перпендикулярное колесо (влево/вправо)

    // IMU для угла
    private BNO055IMU imu = null;

    // Константы для одометрии (КАЛИБРИРУЙТЕ!)
    private static final double ODOM_TICKS_PER_REV = 8192; // Для REV Through Bore
    private static final double ODOM_WHEEL_RADIUS = 0.944882; // Дюймы (48mm диаметр)
    private static final double ODOM_COUNTS_PER_INCH = (ODOM_TICKS_PER_REV) / (ODOM_WHEEL_RADIUS * 2 * Math.PI);

    // Смещения колес от центра робота (в дюймах) - ИЗМЕРЬТЕ!
    private static final double PARALLEL_Y_OFFSET = -5.0;  // Параллельное колесо смещено по Y
    private static final double PERPENDICULAR_X_OFFSET = -3.0; // Перпендикулярное колесо смещено по X

    // Текущая позиция (глобальные переменные)
    private double currentX = 0;  // Вперед/назад (дюймы)
    private double currentY = 0;  // Влево/вправо (дюймы)
    private double currentHeading = 0; // Угол (радианы)

    // Предыдущие значения для расчета дельт
    private int lastParallelPos = 0;
    private int lastPerpendicularPos = 0;
    private double lastHeading = 0;

    // Флаг использования одометрии
    private boolean useOdometry = true; // Можно включить/выключить

    // Конец нововведений }}


    static final double COUNTS_PER_MOTOR_REV = 1440;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // No External Gearing.
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    private DcMotor frontLeftDrive      = null;
    private DcMotor backLeftDrive       = null;
    private DcMotor frontRightDrive     = null;
    private DcMotor backRightDrive      = null;

//    private DcMotor Hand1 = null;
//
    private CRServo servo1 = null;
    private CRServo servo2 = null;
//    private CRServo servo3 = null;
//    private CRServo servo4 = null;
    private double          headingError  = 0;
    ElapsedTime ttimer = new ElapsedTime();
    @Override
    public void runOpMode() {

        // ===== ИНИЦИАЛИЗАЦИЯ ОДОМЕТРИИ ===== {{

        if (useOdometry) {
            initOdometry();
        }

        // ... остальной существующий код ...
        waitForStart();

        // Сброс позиции при старте автономного режима
        if (useOdometry) {
            resetOdometryPose(0, 0, 0);
        }

        // Конец нововведений }}

        frontLeftDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
//        Hand1 = hardwareMap.get(DcMotor.class, "Hand1");


        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        servo1 = hardwareMap.get(CRServo.class, "servo1");
        servo2 = hardwareMap.get(CRServo.class, "servo2");
//        servo3 = hardwareMap.get(CRServo.class, "servo3");
//        servo4 = hardwareMap.get(CRServo.class, "servo4");

//        maxMotor.setDirection(DcMotor.Direction.FORWARD);
//        servoUpper.setDirection(CRServo.Direction.FORWARD);
//        servo2.setDirection(CRServo.Direction.FORWARD);
//        servo3.setDirection(CRServo.Direction.FORWARD);
//        servo4.setDirection(CRServo.Direction.FORWARD);
//        Hand1.setDirection(DcMotor.Direction.FORWARD);

        ttimer.reset();
//        Hand1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        servoHolder.scaleRange(0.4, 0.8);
//        servoRotater.scaleRange(0.4, 0.6);
        waitForStart();

        driveToPosition(50,0,0,1);

    }
    void encoderDrive(double inches, double vel, String dir){
        int newLeftFrontTarget;
        int newRightFrontTarget;
        int newLeftBackTarget;
        int newRightBackTarget;

        // {{

        double startX = currentX;
        double startY = currentY;
        double startHeading = currentHeading;

        // }}

        double speedAx;

        double[] target = {0, 0, 0, 0};

        if (Objects.equals(dir, "fd") && opModeIsActive()) { // вперёд
            target[0] = inches;
            target[1] = inches;
            target[2] = inches;
            target[3] = inches;
        }
        if ((Objects.equals(dir, "bk") || Objects.equals(dir, "bkk")) && opModeIsActive()) { // назад
            target[0] = -inches;
            target[1] = -inches;
            target[2] = -inches;
            target[3] = -inches;
        }
        if (Objects.equals(dir, "lt") && opModeIsActive()) { // лево
            target[0] = -inches;
            target[1] = -inches;
            target[2] = inches;
            target[3] = inches;
        }
        if (Objects.equals(dir, "rt") && opModeIsActive()) { // право
            target[0] = inches;
            target[1] = inches;
            target[2] = -inches;
            target[3] = -inches;
        }
        if (Objects.equals(dir, "rrt") && opModeIsActive()){ // поворот право. для 90 градусов нужно 7,5 дюймов
            target[0] = -inches;
            target[1] = inches;
            target[2] = -inches;
            target[3] = inches;
        }
        if (Objects.equals(dir, "rlt") && opModeIsActive()){ //поворот лево. для 90 градусов нужно 7,5 дюймов
            target[0] = inches;
            target[1] = -inches;
            target[2] = inches;
            target[3] = -inches;
        }
        if (Objects.equals(dir, "klt") && opModeIsActive()) { // диагоняль вперёд лево
            target[0] = 0;
            target[1] = 0;
            target[2] = inches;
            target[3] = inches;
        }
        if (Objects.equals(dir, "krt") && opModeIsActive()) { // диагоняль вперёд право
            target[0] = inches;
            target[1] = inches;
            target[2] = 0;
            target[3] = 0;
        }
        if (Objects.equals(dir, "blt") && opModeIsActive()) { // диагоняль назад лево
            target[0] = 0;
            target[1] = 0;
            target[2] = inches;
            target[3] = inches;
        }
        if (Objects.equals(dir, "brt") && opModeIsActive()) { // диагоняль назад право
            target[0] = inches;
            target[1] = inches;
            target[2] = 0;
            target[3] = 0;
        }

        newLeftFrontTarget = frontLeftDrive.getCurrentPosition() + (int) (target[0] * COUNTS_PER_INCH);
        newRightFrontTarget = backRightDrive.getCurrentPosition() + (int) (target[1] * COUNTS_PER_INCH);
        newLeftBackTarget = backLeftDrive.getCurrentPosition() + (int) (target[2] * COUNTS_PER_INCH);
        newRightBackTarget = frontRightDrive.getCurrentPosition() + (int) (target[3] * COUNTS_PER_INCH);

        backLeftDrive.setTargetPosition(newLeftBackTarget);
        frontLeftDrive.setTargetPosition(newLeftFrontTarget);
        frontRightDrive.setTargetPosition(newRightBackTarget);
        backRightDrive.setTargetPosition(newRightFrontTarget);

        backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        double startPos = backLeftDrive.getCurrentPosition();

        while (Math.abs(frontLeftDrive.getCurrentPosition() - frontLeftDrive.getTargetPosition()) > 5 &&
                Math.abs(backLeftDrive.getCurrentPosition() - backLeftDrive.getTargetPosition()) > 5 &&
                Math.abs(frontRightDrive.getCurrentPosition() - frontRightDrive.getTargetPosition()) > 5 &&
                Math.abs(backRightDrive.getCurrentPosition() - backRightDrive.getTargetPosition()) > 5) {

            speedAx = deltavel(Math.abs(-startPos + backLeftDrive.getCurrentPosition()), inches * COUNTS_PER_INCH, 0.15, 0.7, 0.075, vel);

            backLeftDrive.setPower(speedAx);
            frontLeftDrive.setPower(speedAx);
            frontRightDrive.setPower(speedAx);
            backRightDrive.setPower(speedAx);

            // {{
            if (useOdometry) {
                updateOdometry();
            }

            // Отладочная информация
            telemetry.addData("Driving", "%s: %.1f in", dir, inches);
            if (useOdometry) {
                telemetry.addData("Position", "(%.1f, %.1f)", currentX, currentY);
                telemetry.addData("Heading", "%.1f°", Math.toDegrees(currentHeading));
            }
            telemetry.update();

            // }}
        }

        backLeftDrive.setPower(0);
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backRightDrive.setPower(0);

        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // {{
        if (useOdometry) {
            updateOdometry();
        }
        // }}
    }

    public double deltavel(double x, double l, double c1, double c2, double v_min, double v_max) {
        double l1 = l * c1;
        double l2 = l * c2;
        double k1 = (v_max - v_min) / l1;
        double k2 = (v_max - v_min) / (l - l2);
        if (Math.abs(x) < l1) return v_min + Math.abs(k1 * x);
        if (Math.abs(x) >= l1 && Math.abs(x) <= l2) return v_max;
        else return v_max - Math.abs(k2 * (x - l2));
        //push2
    }

    // Новые классы одометрии {{
    // ===== МЕТОДЫ ОДОМЕТРИИ =====

    /**
     * Инициализация датчиков одометрии и IMU
     */
    private void initOdometry() {
        try {
            // Инициализация датчиков одометрии (подключаются к I2C Bus 2)
            parallelPod = hardwareMap.get(DcMotor.class, "parallelPod");
            perpendicularPod = hardwareMap.get(DcMotor.class, "perpendicularPod");

            // Настройка режимов работы
            parallelPod.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            perpendicularPod.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            parallelPod.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            perpendicularPod.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            // Инициализация IMU
            imu = hardwareMap.get(BNO055IMU.class, "imu");
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
            parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.calibrationDataFile = "BNO055IMUCalibration.json";
            parameters.loggingEnabled = false;

            imu.initialize(parameters);

            // Ждем инициализации IMU
            sleep(1000);

            // Сохраняем начальные значения
            lastParallelPos = parallelPod.getCurrentPosition();
            lastPerpendicularPos = perpendicularPod.getCurrentPosition();
            lastHeading = getIMUHeading();

            telemetry.addData("Odometry", "Initialized successfully");
            telemetry.addData("IMU Heading", "%.2f°", Math.toDegrees(lastHeading));

        } catch (Exception e) {
            useOdometry = false;
            telemetry.addData("Odometry Error", "Disabled: %s", e.getMessage());
        }
        telemetry.update();
    }

    /**
     * Получение текущего угла с IMU
     */
    private double getIMUHeading() {
        if (imu == null) return 0;

        Orientation orientation = imu.getAngularOrientation(
                AxesReference.INTRINSIC,
                AxesOrder.ZYX,
                AngleUnit.RADIANS
        );

        // Обработка угла: -PI до PI
        double angle = -orientation.firstAngle;
        return angle;
    }

    /**
     * Конвертация тиков энкодера в дюймы
     */
    private double odomTicksToInches(int ticks) {
        return ticks / ODOM_COUNTS_PER_INCH;
    }

    /**
     * Обновление позиции по одометрии (вызывайте в цикле или после движений)
     */
    public void updateOdometry() {
        if (!useOdometry) return;

        // Текущие значения
        int currentParallelPos = parallelPod.getCurrentPosition();
        int currentPerpendicularPos = perpendicularPod.getCurrentPosition();
        double currentHeading = getIMUHeading();

        // Изменения (дельты)
        double deltaParallel = odomTicksToInches(currentParallelPos - lastParallelPos);
        double deltaPerpendicular = odomTicksToInches(currentPerpendicularPos - lastPerpendicularPos);
        double deltaHeading = currentHeading - lastHeading;

        // Средний угол для компенсации
        double avgHeading = lastHeading + deltaHeading / 2.0;

        // Компенсация смещения колес при повороте
        double deltaX = deltaParallel;
        double deltaY = deltaPerpendicular;

        if (Math.abs(deltaHeading) > 0.001) {
            // Учитываем смещение колес от центра
            deltaX = deltaParallel - PARALLEL_Y_OFFSET * deltaHeading;
            deltaY = deltaPerpendicular + PERPENDICULAR_X_OFFSET * deltaHeading;
        }

        // Преобразование в глобальную систему координат
        double globalDeltaX = deltaX * Math.cos(avgHeading) - deltaY * Math.sin(avgHeading);
        double globalDeltaY = deltaX * Math.sin(avgHeading) + deltaY * Math.cos(avgHeading);

        // Обновление позиции
        currentX += globalDeltaX;
        currentY += globalDeltaY;
        this.currentHeading = currentHeading;

        // Сохранение для следующего обновления
        lastParallelPos = currentParallelPos;
        lastPerpendicularPos = currentPerpendicularPos;
        lastHeading = currentHeading;

        // Отладочная информация
        telemetry.addData("Odometry X", "%.2f in", currentX);
        telemetry.addData("Odometry Y", "%.2f in", currentY);
        telemetry.addData("Odometry Heading", "%.2f°", Math.toDegrees(currentHeading));
    }

    /**
     * Сброс позиции одометрии
     */
    public void resetOdometryPose(double x, double y, double headingRad) {
        if (!useOdometry) return;

        currentX = x;
        currentY = y;
        currentHeading = headingRad;

        // Сброс энкодеров
        parallelPod.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        perpendicularPod.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        parallelPod.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        perpendicularPod.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Обновление последних значений
        lastParallelPos = 0;
        lastPerpendicularPos = 0;
        lastHeading = headingRad;
    }

    /**
     * Получение текущей позиции
     */
    public double getOdometryX() { return currentX; }
    public double getOdometryY() { return currentY; }
    public double getOdometryHeadingRad() { return currentHeading; }
    public double getOdometryHeadingDeg() { return Math.toDegrees(currentHeading); }

    /**
     * Движение к целевой позиции с использованием одометрии
     */
    public void driveToPosition(double targetX, double targetY, double targetHeadingDeg, double maxSpeed) {
        if (!useOdometry) {
            telemetry.addData("Error", "Odometry not enabled");
            return;
        }

        final double tolerance = 0.5; // дюймы
        final double headingTolerance = 2.0; // градусы
        final double slowdownDistance = 6.0; // расстояние для замедления

        double targetHeadingRad = Math.toRadians(targetHeadingDeg);

        while (opModeIsActive()) {
            // Обновляем текущую позицию
            updateOdometry();

            // Вычисляем ошибки
            double errorX = targetX - currentX;
            double errorY = targetY - currentY;
            double errorHeading = targetHeadingRad - currentHeading;

            // Нормализуем ошибку угла
            while (errorHeading > Math.PI) errorHeading -= 2 * Math.PI;
            while (errorHeading < -Math.PI) errorHeading += 2 * Math.PI;

            // Расстояние до цели
            double distance = Math.sqrt(errorX * errorX + errorY * errorY);

            // Угол к цели
            double angleToTarget = Math.atan2(errorY, errorX);

            // Вычисляем мощности с учетом текущего угла
            double robotAngle = angleToTarget - currentHeading;

            // Компоненты движения
            double moveX = Math.cos(robotAngle) * distance;
            double moveY = Math.sin(robotAngle) * distance;
            double rotate = errorHeading;

            // Коэффициенты П-регулятора
            double kpDistance = 0.05;
            double kpRotate = 0.5;

            // Ограничение скорости
            double speed = Math.min(maxSpeed, distance * kpDistance);
            double rotation = rotate * kpRotate;

            // Замедление при приближении
            if (distance < slowdownDistance) {
                speed = maxSpeed * (distance / slowdownDistance);
            }

            // Вычисление мощностей для каждого мотора (Mecanum)
            double frontLeftPower = moveY + moveX + rotation;
            double frontRightPower = moveY - moveX - rotation;
            double backLeftPower = moveY - moveX + rotation;
            double backRightPower = moveY + moveX - rotation;

            // Нормализация
            double maxPower = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));
            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            // Применение мощностей
            frontLeftDrive.setPower(frontLeftPower * speed);
            frontRightDrive.setPower(frontRightPower * speed);
            backLeftDrive.setPower(backLeftPower * speed);
            backRightDrive.setPower(backRightPower * speed);

            // Отладочная информация
            telemetry.addData("Target", "(%.1f, %.1f, %.1f°)", targetX, targetY, targetHeadingDeg);
            telemetry.addData("Current", "(%.1f, %.1f, %.1f°)", currentX, currentY, Math.toDegrees(currentHeading));
            telemetry.addData("Errors", "d=%.1f, h=%.1f°", distance, Math.toDegrees(errorHeading));
            telemetry.addData("Power", "FL:%.2f, FR:%.2f, BL:%.2f, BR:%.2f",
                    frontLeftPower, frontRightPower, backLeftPower, backRightPower);
            telemetry.update();

            // Проверка достижения цели
            if (distance < tolerance && Math.abs(Math.toDegrees(errorHeading)) < headingTolerance) {
                // Стоп
                frontLeftDrive.setPower(0);
                frontRightDrive.setPower(0);
                backLeftDrive.setPower(0);
                backRightDrive.setPower(0);
                break;
            }

            sleep(20); // Небольшая задержка для стабильности
        }
    }
    // Конец новых классов }}

}


