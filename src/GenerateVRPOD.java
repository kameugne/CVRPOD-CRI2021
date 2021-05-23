import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GenerateVRPOD {
    public VRPODInstances generated(String fileName, int numOD) throws Exception{
        Scanner s = new Scanner(new File(fileName)).useDelimiter("\\s+");

        // name of the instance
        String name = s.nextLine();
        // skip 3 line
        s.nextLine();
        s.nextLine();
        s.nextLine();
        // number of vehicle and capacity
        int numberOfVehicle = s.nextInt();
        int capacity = s.nextInt();
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
        City[] cities = new City[listCities.size()];
        for (int i = 0; i < cities.length; i++) {
            cities[i] = listCities.get(i);
        }

        Step[] steps = new Step[cities.length+numOD];

        Integer minX, maxX, minY, maxY, maxDemand;
        minX = minY = Integer.MAX_VALUE;
        maxDemand = maxX = maxY = Integer.MIN_VALUE;
        for(int i = 0; i < cities.length; i++){
            minX = Math.min(minX, cities[i].xCoordinate);
            minY = Math.min(minY, cities[i].yCoordinate);
            maxX = Math.max(maxX, cities[i].xCoordinate);
            maxY = Math.max(maxY, cities[i].yCoordinate);
            maxDemand = Math.max(maxDemand, cities[i].demand);
        }

        for(int i = 0; i < cities.length + numOD; i++) {
            if (i < cities.length) {
                steps[i] = new Step(i, cities[i].xCoordinate, cities[i].yCoordinate, cities[i].demand);
            }else{
                steps[i] = new Step(i, (int)(Math.random()*((maxX-minX)+1))+minX, (int)(Math.random()*((maxY-minY)+1))+minY, maxDemand);
            }
        }
        //System.out.println(Arrays.toString(steps));
        return new VRPODInstances(name + "OD"+numOD, cities.length-1, numOD, numberOfVehicle, capacity, steps);
    }


}
