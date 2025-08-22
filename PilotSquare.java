import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class PilotSquare {
    final static float WHEEL_DIAMETER = 51; // Wheel diameter in mm
    final static float AXLE_LENGTH = 44;    // Distance between wheels in mm
    final static float ANGULAR_SPEED = 100; // Degrees per second
    final static float LINEAR_SPEED = 70;   // mm per second

    public static void main(String[] args) {
        // Initialize motors
        EV3LargeRegulatedMotor mL = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor mR = new EV3LargeRegulatedMotor(MotorPort.B);

        // Create wheels
        Wheel wLeft = WheeledChassis.modelWheel(mL, WHEEL_DIAMETER).offset(-AXLE_LENGTH / 2);
        Wheel wRight = WheeledChassis.modelWheel(mR, WHEEL_DIAMETER).offset(AXLE_LENGTH / 2);

        // Create chassis
        WheeledChassis chassis = new WheeledChassis(new Wheel[]{wRight, wLeft}, WheeledChassis.TYPE_DIFFERENTIAL);

        // Create MovePilot
        MovePilot pilot = new MovePilot(chassis);
        pilot.setLinearSpeed(LINEAR_SPEED);
        pilot.setAngularSpeed(ANGULAR_SPEED);

        // Create pose provider
        PoseProvider poseProvider = new OdometryPoseProvider(pilot);

        // Print initial pose
        Pose startPose = poseProvider.getPose();
        System.out.println("Start Pose: " + startPose);

        // Move in a square
        for (int i = 0; i < 4; i++) {
            pilot.travel(1000); // Move 1 meter
            pilot.rotate(90);   // Turn 90 degrees
        }

        // Print final pose
        Pose endPose = poseProvider.getPose();
        System.out.println("End Pose: " + endPose);
    }
}
