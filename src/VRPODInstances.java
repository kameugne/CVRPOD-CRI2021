import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class VRPODInstances {
    public String name;
    public int numberCustomers;
    public int numberOccasDriver;
    public int numberOfVehicle;
    public int capacity;
    public Step[] steps;
    private int numberOfCities;
    public VRPODInstances(String name, int numberCustomers, int numberOccasDriver, int numberVehicle, int capacity, Step[] steps) {
        this.name = name;
        this.numberCustomers = numberCustomers;
        this.numberOccasDriver = numberOccasDriver;
        this.numberOfVehicle = numberVehicle;
        this.capacity = capacity;
        this.steps = steps;
    }
    public VRPODInstances(String fileName) throws Exception {
        Scanner s = new Scanner(new File(fileName)).useDelimiter("\\s+");
        // name of the instance
        name = s.nextLine();
        // number of customer, of accasional drivers, number of vehicle and capacity
        numberCustomers = s.nextInt();
        numberOccasDriver = s.nextInt();
        numberOfVehicle = s.nextInt();
        capacity = s.nextInt();

        ArrayList<Step> listSteps = new ArrayList<>();
        while(s.hasNextInt()) {
            s.nextLine();
            int ind = s.nextInt();
            int xCord = s.nextInt();
            int yCord = s.nextInt();
            int demand = s.nextInt();
            listSteps.add(new Step(ind, xCord, yCord, demand));
        }
        steps = new Step[listSteps.size()];
        for (int i = 0; i < steps.length; i++) {
            steps[i] = listSteps.get(i);
        }
    }
}
