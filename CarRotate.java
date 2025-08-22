import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class CarRotate {

    public static void main(String[] args) {
        // Create motor objects
        EV3LargeRegulatedMotor mLeft = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor mRight = new EV3LargeRegulatedMotor(MotorPort.B);

        // Set motor speeds
        mLeft.setSpeed(720);
        mRight.setSpeed(720);

        // Synchronize motors to rotate at the same time
        mLeft.synchronizeWith(new EV3LargeRegulatedMotor[] { mRight });

        // Loop to make the robot move around a square
        for (int i = 0; i < 4; i++) {
            // Move forward 1 meter (rotate both motors by 720 degrees)
            mLeft.rotate(720);
            mRight.rotate(720);

            // Turn 90 degrees by rotating only one motor
            mLeft.rotate(720);
            mRight.rotate(-720);

            // Wait for the motors to finish
            mLeft.waitComplete();
            mRight.waitComplete();
        }

        // Close motor objects
        mLeft.close();
        mRight.close();
    }
}
