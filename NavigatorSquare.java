import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;

public class NavigatorSquare {
    final static float WHEEL_DIAMETER = 51;
    final static float AXLE_LENGTH = 44;
    final static float ANGULAR_SPEED = 100;
    final static float LINEAR_SPEED = 70;

    public static void main(String[] args) {
        EV3LargeRegulatedMotor mL = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor mR = new EV3LargeRegulatedMotor(MotorPort.D);

        Wheel wLeft = WheeledChassis.modelWheel(mL, WHEEL_DIAMETER).offset(-AXLE_LENGTH / 2);
        Wheel wRight = WheeledChassis.modelWheel(mR, WHEEL_DIAMETER).offset(AXLE_LENGTH / 2);

        WheeledChassis chassis = new WheeledChassis(new Wheel[]{wRight, wLeft}, WheeledChassis.TYPE_DIFFERENTIAL);
        MovePilot pilot = new MovePilot(chassis);
        pilot.setLinearSpeed(LINEAR_SPEED);
        pilot.setAngularSpeed(ANGULAR_SPEED);

        OdometryPoseProvider poseProvider = new OdometryPoseProvider(pilot);
        Navigator navigator = new Navigator(pilot, poseProvider);

        // Create a square path
        Path squarePath = new Path();
        squarePath.add(new Waypoint(1000, 0));
        squarePath.add(new Waypoint(1000, 1000));
        squarePath.add(new Waypoint(0, 1000));
        squarePath.add(new Waypoint(0, 0));

        // Follow the path
        navigator.followPath(squarePath);
        navigator.waitForStop();

        // Print final pose
        System.out.println("Final Pose: " + poseProvider.getPose());
    }
}
