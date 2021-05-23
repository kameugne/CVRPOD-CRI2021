public class Step {
    public int index;
    public int xCoordinate;
    public int yCoordinate;
    public int demand;

    public Step(int index, int xCoordinate, int yCoordinate, int demand){
        this.index = index;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.demand = demand;
    }

    @Override
    public String toString() {
        return " index : "+ index + ", coordinates("+ xCoordinate + ","+yCoordinate+")" + ", demand : "+ demand ;
    }
}
