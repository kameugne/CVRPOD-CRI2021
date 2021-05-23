public class City {
    public int index;
    public int xCoordinate;
    public int yCoordinate;
    public int demand;
    public int earliestStartService;
    public int latestStartService;
    public int service;

    public City(int index, int xCoordinate, int yCoordinate, int demand, int earliestStartService, int latestStartService, int service){
        this.index = index;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.demand = demand;
        this.earliestStartService = earliestStartService;
        this.latestStartService = latestStartService;
        this.service = service;
    }

    @Override
    public String toString() {
        return " index : "+ index + ", coordinates("+ xCoordinate + ","+yCoordinate+")" + ", demand : "+ demand + "["+earliestStartService+ " , "+ latestStartService+"]" + ", service: "+ service;
    }
}
