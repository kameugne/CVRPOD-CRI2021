public class Main {
    public static void main(String[] args) throws Exception{
        CVRPInstance data = new CVRPInstance("Data/Solomon/c1/c101.txt");
        System.out.println(data.name);
         new CVRPModel().solve(data);
    }
}
