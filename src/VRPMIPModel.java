import ilog.concert.*;
import ilog.cplex.*;

public class VRPMIPModel {
    public void solve(VRPInstance data) {
        int numberOfCutomer = data.numberOfSteps;
        int numberOfVehicle = data.numberOfVehicle;
        int Capacity = data.capacity;
        int[] demand = new int[numberOfCutomer];
        double[][] cost = new double[numberOfCutomer][numberOfCutomer];
        double[][] travel = new double[numberOfCutomer][numberOfCutomer];
        for (int i = 0; i < numberOfCutomer; i++) {
            demand[i] = data.steps[i].demand;
            for (int j = 0; j < numberOfCutomer; j++) {
                cost[i][j] =  Math.sqrt(Math.pow(data.steps[i].xCoordinate - data.steps[j].xCoordinate, 2) + Math.pow(data.steps[i].yCoordinate - data.steps[j].yCoordinate, 2));
                travel[i][j] =  Math.sqrt(Math.pow(data.steps[i].xCoordinate - data.steps[j].xCoordinate, 2) + Math.pow(data.steps[i].yCoordinate - data.steps[j].yCoordinate, 2));
            }
        }

        try {
            // create a new model
            IloCplex model = new IloCplex();
            // variables of the model
            // assignement variable X(i,j,k)
            IloNumVar[][][] x = new IloNumVar[numberOfCutomer][numberOfCutomer][];
            for (int i = 0; i < numberOfCutomer; i++) {
                for (int j = 0; j < numberOfCutomer; j++) {
                    x[i][j] = model.boolVarArray(numberOfVehicle);
                }
            }
            // load variable y(i,j)
            IloNumVar[][]y = new IloNumVar[numberOfCutomer][numberOfCutomer];
            for (int i = 0; i < numberOfCutomer; i++) {
                for (int j = 0; j < numberOfCutomer; j++) {
                    y[i][j] = model.numVar(0.0, Double.MAX_VALUE);
                }
            }

            // Objective function
            IloLinearNumExpr minimumCost = model.linearNumExpr();
            for (int i = 0; i < numberOfCutomer; i++) {
                for (int j = 0; j < numberOfCutomer; j++){
                    for (int k = 0; k < numberOfVehicle; k++) {
                        minimumCost.addTerm(cost[i][j], x[i][j][k]);
                    }
                }
            }
            model.addMinimize(minimumCost);

            // constraints
            // constraint (2)
            for (int j = 1; j < numberOfCutomer; j++) {
                IloLinearNumExpr expres = model.linearNumExpr();
                for (int i = 0; i < numberOfCutomer; i++) {
                    if (i != j) {
                        for (int k = 0; k < numberOfVehicle; k++) {
                            expres.addTerm(1.0, x[i][j][k]);
                        }
                    }
                }
                model.addEq(expres, 1);
            }

            // constraint (3)
            for (int i = 1; i < numberOfCutomer; i++) {
                IloLinearNumExpr expres = model.linearNumExpr();
                for (int j = 0; j < numberOfCutomer; j++) {
                    if (j != i) {
                        for (int k = 0; k < numberOfVehicle; k++) {
                            expres.addTerm(1.0, x[i][j][k]);
                        }
                    }
                }
                model.addEq(expres, 1);
            }
            // constraint (4)
            for (int k = 0; k < numberOfVehicle; k++) {
                for (int j = 0; j < numberOfCutomer; j++) {
                    IloLinearNumExpr expres1 = model.linearNumExpr();
                    IloLinearNumExpr expres2 = model.linearNumExpr();
                    for (int i = 1; i < numberOfCutomer; i++) {
                        if (i != j) {
                            expres1.addTerm(1.0, x[i][j][k]);
                            expres2.addTerm(1.0, x[j][i][k]);
                        }
                    }
                    model.addEq(expres1, expres2);
                }
            }

            // constraint (5)
            IloLinearNumExpr expres = model.linearNumExpr();
            int sumDemand = 0;
            for (int j = 1; j < numberOfCutomer; j++) {
                expres.addTerm(1.0, y[0][j]);
                sumDemand += demand[j];
            }
            model.addGe(expres, sumDemand);

            // constraint (6)
            for (int j = 1; j < numberOfCutomer; j++) {
                IloLinearNumExpr expres1 = model.linearNumExpr();
                IloLinearNumExpr expres2 = model.linearNumExpr();
                for (int i = 0; i < numberOfCutomer; i++) {
                    expres1.addTerm(1.0, y[i][j]);
                    expres2.addTerm(-1.0, y[j][i]);
                }
                expres1.add(expres2);
                model.addEq(expres1, demand[j]);
            }

            // constraint (7)
            for (int i = 0; i < numberOfCutomer; i++) {
                for (int j = 1; j < numberOfCutomer; j++) {
                    IloLinearNumExpr expres3 = model.linearNumExpr();
                    for (int k = 0; k < numberOfVehicle; k++) {
                        expres3.addTerm(Capacity, x[i][j][k]);
                    }
                    model.addLe(y[i][j], expres3);
                }
            }
            if (model.solve()) {
                System.out.println("minimum Cost : " + model.getObjValue());
            }else {
                System.out.println("no solution for the problem");
            }

            model.end();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}
