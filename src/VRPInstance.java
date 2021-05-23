import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class VRPInstance {
    public String name;
    public int numberOfVehicle;
    public int capacity;
    public Step[] steps;
    public int numberOfSteps;
    public VRPInstance(String name, int numberOfSteps, int numberVehicle, int capacity, Step[] steps) {
        this.name = name;
        this.numberOfSteps = numberOfSteps;
        this.numberOfVehicle = numberVehicle;
        this.capacity = capacity;
        this.steps = steps;
    }

    public VRPInstance(String fileName) throws Exception {
        Scanner s = new Scanner(new File(fileName)).useDelimiter("\\s+");

        // name of the instance
        String[] line = s.nextLine().split(" : ");
        name = line[1];
        // number of vehicle
        String[] lin = name.split("-k");
        numberOfVehicle = Integer.parseInt(lin[1]);
        s.nextLine();
        s.nextLine();
        // number of steps
        String[] line1 = s.nextLine().split(" : ");
        numberOfSteps = Integer.parseInt(line1[1]);
        s.nextLine();
        // capacity of vehicle
        String[] line2 = s.nextLine().split(" : ");
        capacity = Integer.parseInt(line2[1]);
        s.nextLine();
        // index and coordinates of steps
        int[] index = new int[numberOfSteps];
        int[] xCoord = new int[numberOfSteps];
        int[] yCoord = new int[numberOfSteps];
        int[] demand = new int[numberOfSteps];
        for (int i = 0; i < numberOfSteps; i++) {
            String[] lines = s.nextLine().split("\\s+");
            //index[i] = Integer.parseInt(lines[1]);
            xCoord[i] = Integer.parseInt(lines[2]);
            yCoord[i] = Integer.parseInt(lines[3]);
        }
        s.nextLine();
        // demand of steps
        for (int i = 0; i < numberOfSteps; i++) {
            String[] lines = s.nextLine().split("\\s+");
            demand[i] = Integer.parseInt(lines[1]);
        }
        steps = new Step[numberOfSteps];
        for (int i = 0; i < numberOfSteps; i++) {
            steps[i] = new Step(i, xCoord[i], yCoord[i], demand[i]);
        }
    }
}
