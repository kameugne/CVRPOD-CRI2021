import java.util.Arrays;

public class runVRPMIPModel {
    public static void main(String[] args) throws Exception {
        VRPInstance data = new VRPInstance("Data/CVRP/A 2/A-n32-k5.vrp");
        System.out.println(data.name + "\n" + data.numberOfSteps + "\n" + data.numberOfVehicle + "\n" + data.capacity);
        System.out.println(Arrays.toString(data.steps));
        new VRPMIPModel().solve(data);
    }
}
