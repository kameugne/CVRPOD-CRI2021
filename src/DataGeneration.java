import com.sun.tools.javah.Gen;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class DataGeneration {
    public static void main(String[] args) throws Exception {
        VRPODInstances data = new GenerateVRPOD().generated("Data/Solomon/rc2/rc201.txt", 50);
        BufferedWriter writer = new BufferedWriter(new FileWriter("Data/VRPOD/"+data.name +".txt"));
        System.out.println(data.name);
        writer.write(data.name);
        writer.newLine();
        System.out.println(data.numberCustomers + " " + data.numberOccasDriver + "  " + data.numberOfVehicle + "    " + data.capacity);
        writer.write(data.numberCustomers + " " + data.numberOccasDriver + "  " + data.numberOfVehicle + "    " + data.capacity);
        writer.newLine();
        for (int i = 0; i < data.steps.length; i++) {
            System.out.println(data.steps[i].index + "  " + data.steps[i].xCoordinate + "   "+ data.steps[i].yCoordinate + "    " + data.steps[i].demand);
            writer.write(data.steps[i].index + "  " + data.steps[i].xCoordinate + "   "+ data.steps[i].yCoordinate + "    " + data.steps[i].demand);
            writer.newLine();
        }
        writer.close();
    }
}
