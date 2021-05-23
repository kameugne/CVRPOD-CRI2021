import java.util.Arrays;

public class solveVRPOD {
    public static void main(String[] args) throws Exception {
        VRPODInstances data = new VRPODInstances("Data/VRPOD/C101OD50.txt");
        System.out.println(data.numberCustomers + "\n"+ data.numberOccasDriver +"\n"+data.numberOfVehicle + "\n"+data.capacity);
        System.out.println(Arrays.toString(data.steps));
        new VRPODMIPModel().solve(data, 1.3, 0.2);
    }
}
