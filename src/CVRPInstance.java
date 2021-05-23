import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class CVRPInstance {
    public String name;
    public int numberOfVehicle;
    public int capacity;
    public City[] cities;
    private int numberOfCities;
    public CVRPInstance(String name, int numberVehicle, int capacity, City[] cities) {
        this.name = name;
        this.numberOfVehicle = numberVehicle;
        this.capacity = capacity;
        this.cities = cities;
    }

    public CVRPInstance(String fileName) throws Exception {
        Scanner s = new Scanner(new File(fileName)).useDelimiter("\\s+");

        // name of the instance
        name = s.nextLine();
        // skip 3 line
        s.nextLine();
        s.nextLine();
        s.nextLine();
        // number of vehicle and capacity
        numberOfVehicle = s.nextInt();
        capacity = s.nextInt();
        // skip 5 line
        s.nextLine();
        s.nextLine();
        s.nextLine();
        s.nextLine();
        ArrayList<City> listCities = new ArrayList<>();
        while(s.hasNextInt()) {
            s.nextLine();
            int ind = s.nextInt();
            int xCord = s.nextInt();
            int yCord = s.nextInt();
            int demand = s.nextInt();
            int est = s.nextInt();
            int lst = s.nextInt();
            int service = s.nextInt();
            listCities.add(new City(ind, xCord, yCord, demand, est, lst, service));
        }
        cities = new City[listCities.size()+1];
        for (int i = 0; i < cities.length-1; i++) {
            cities[i] = listCities.get(i);
        }
        cities[101] = cities[0];
    }
}
