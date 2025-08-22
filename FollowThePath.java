import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.ShortestPathFinder;

public class FollowThePath {
    final static float WHEEL_DIAMETER = 51;
    final static float AXLE_LENGTH = 44;
    final static float ANGULAR_SPEED = 100;
    final static float LINEAR_SPEED = 70;

    public static void main(String[] args) throws DestinationUnreachableException {
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

        // Define obstacles and boundaries
        Line[] obstacles = {
            new Line(300, 200, 300, 600),
            new Line(500, 500, 700, 500)
        };
        Rectangle boundary = new Rectangle(0, 0, 1000, 1000);
        LineMap map = new LineMap(obstacles, boundary);

        // Define start and goal
        Pose startPose = new Pose(50, 50, 0);
        Waypoint goal = new Waypoint(900, 900);

        // Create shortest path finder
        ShortestPathFinder pathFinder = new ShortestPathFinder(map);

        // Calculate path
        lejos.robotics.pathfinding.Path path = pathFinder.findRoute(startPose, goal);

        // Follow path
        navigator.followPath(path);
        navigator.waitForStop();

        // Print final pose
        System.out.println("Final Pose: " + poseProvider.getPose());
    }
}
