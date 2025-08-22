import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class SquareCar {

    final static int ONE_METER_FORWARDS_TIME = 1000; // Adjust this value based on your robot's speed
    final static int NINETY_DEGREE_TURN_TIME = 500; // Adjust this value based on your robot's turning time

    public static void main(String[] args) {
        // Create motor objects
        EV3LargeRegulatedMotor mLeft = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor mRight = new EV3LargeRegulatedMotor(MotorPort.B);

        // Set speed for both motors
        mLeft.setSpeed(720);
        mRight.setSpeed(720);

        // Loop to repeat the square pattern four times
        for (int i = 0; i < 4; i++) {
            // Move forward
            mLeft.forward();
            mRight.forward();
            Delay.msDelay(ONE_METER_FORWARDS_TIME);

            // Turn 90 degrees (left)
            mLeft.backward();
            mRight.forward();
            Delay.msDelay(NINETY_DEGREE_TURN_TIME);

            // Stop the motors for a moment before the next side
            mLeft.stop();
            mRight.stop();
            Delay.msDelay(200);
        }

        // Close motor objects
        mLeft.close();
        mRight.close();
    }
}
