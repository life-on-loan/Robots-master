package gui;

import utils.AdditionalMathMethods;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Класс, описывающий модель игры
 */
public class GameModel extends Observable
{
    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private volatile double angleToTarget;

    public double getRobotPositionX() {
        return m_robotPositionX;
    }

    public double getRobotPositionY() {
        return m_robotPositionY;
    }

    public double getRobotDirection() {
        return m_robotDirection;
    }

    public int getTargetPositionX() {
        return m_targetPositionX;
    }

    public int getTargetPositionY() {
        return m_targetPositionY;
    }

    public void setTargetPosition(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    private final Timer m_timer = initTimer();

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameModel() {
        m_timer.schedule(new TimerTask()
        {
            /**
             * Уведомление наблюдателя, ответственного за вывод координат
             */
            @Override
            public void run()
            {
                updateDataMovingRobot();
                notifyObservers();
            }
        }, 0, 10);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return AdditionalMathMethods.asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * Метод изменения координат робота в процессе достижения им цели
     */
    protected void updateDataMovingRobot() {
        double distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            return;
        }
        angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;

        //место ошибки в поведении робота
        double differentRobotAndTargetDirection = angleToTarget - m_robotDirection;
        double inaccuracy = 0.025d; //погрешность
        if ((differentRobotAndTargetDirection >= Math.PI)
                || (differentRobotAndTargetDirection < inaccuracy && differentRobotAndTargetDirection >= -Math.PI)) {
            angularVelocity = -maxAngularVelocity;
        }
        if ((differentRobotAndTargetDirection < -Math.PI)
                || (differentRobotAndTargetDirection > inaccuracy && differentRobotAndTargetDirection < Math.PI)) {
            angularVelocity = maxAngularVelocity;
        }

        moveRobot(angularVelocity, 10);
        setChanged();
    }

    private void moveRobot(double angularVelocity, double duration)
    {
        double velocity = maxVelocity;
        angularVelocity = AdditionalMathMethods.applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) - Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) - Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        double newDirection = AdditionalMathMethods.asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = newDirection;
    }
}