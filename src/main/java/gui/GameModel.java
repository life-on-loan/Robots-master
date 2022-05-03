package gui;

import utils.AdditionalMathMethods;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import static constants.TextConstants.MOVE_ROBOT;
import static constants.TextConstants.REPAINT;

/**
 * Класс, описывающий модель игры
 */
public class GameModel extends Observable
{
    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.01;

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
             * Уведомление наблюдателя, ответсвенного за отрисовку
             */
            @Override
            public void run()
            {
                setChanged();
                notifyObservers(REPAINT);
                clearChanged();
            }
        }, 0, 10);
        m_timer.schedule(new TimerTask()
        {
            /**
             * Уведомление наблюдателя, ответсвенного за вывод координат
             */
            @Override
            public void run()
            {
                setChanged();
                updateDataMovingRobot();
                notifyObservers(MOVE_ROBOT);
                clearChanged();
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

    protected void updateDataMovingRobot() {
        double distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        if (distance <= 0.5) {
            return;
        }
        angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        double differentRobotAndTargetDirection = angleToTarget - m_robotDirection;
        double inaccuracy = 0.075d; //погрешность
        if ((differentRobotAndTargetDirection >= Math.PI)
                || (differentRobotAndTargetDirection < inaccuracy && differentRobotAndTargetDirection >= -Math.PI)) {
            angularVelocity = -maxAngularVelocity;
        }
        if ((differentRobotAndTargetDirection < -Math.PI)
                || (differentRobotAndTargetDirection > inaccuracy && differentRobotAndTargetDirection < Math.PI)) {
            angularVelocity = maxAngularVelocity;
        }
        double duration = 10;
        if (Math.abs(differentRobotAndTargetDirection) > 0.5d) {
            duration = 5;
        }

        moveRobot(angularVelocity, duration);
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