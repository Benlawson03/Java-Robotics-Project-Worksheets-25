import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class StopCar {

    public static void main(String[] args) {
        // Create the motor objects
        EV3LargeRegulatedMotor mLeft = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor mRight = new EV3LargeRegulatedMotor(MotorPort.B);

        // Set the motors to a speed of 720 degrees per second
        mLeft.setSpeed(720);
        mRight.setSpeed(720);

        // Start both motors
        mLeft.forward();
        mRight.forward();

        // Wait for the ENTER button to be pressed to stop the motors
        Button.ENTER.waitForPressAndRelease();

        // Stop both motors
        mLeft.stop();
        mRight.stop();

        // Display the tacho count (distance covered)
        LCD.drawInt(mLeft.getTachoCount(), 0, 0);

        // Wait for the ENTER button to be pressed before finishing the program
        Button.ENTER.waitForPressAndRelease();

        // Close the motor objects
        mLeft.close();
        mRight.close();
    }
}
