import ilog.concert.*;
import ilog.cplex.*;

public class CVRPModel {
    public double optimumCost;
    public double runningTime;
    public double gapOptimum;
    public void solve(CVRPInstance data) {
        int numberOfCities = data.cities.length; // n+1 ville
        int numberOfVehicle = data.numberOfVehicle; // m vehicules
        int capacity = data.capacity; // capacite des vehicule
        int[] demand = new int[numberOfCities];
        int[] service = new int[numberOfCities];
        int[] est = new int[numberOfCities];
        int[] lst = new int[numberOfCities];
        for(int i = 0; i < numberOfCities; i++) {
            demand[i] = data.cities[i].demand; // d_i
            service[i] = data.cities[i].service; // s_i
            est[i] = data.cities[i].earliestStartService; // a_i
            lst[i] = data.cities[i].latestStartService; // b_i
        }
        double[][] cost = new double[numberOfCities][numberOfCities];
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                cost[i][j] = Math.sqrt(Math.pow(data.cities[i].xCoordinate - data.cities[j].xCoordinate, 2) + Math.pow(data.cities[i].yCoordinate - data.cities[j].yCoordinate, 2));
            }
        }

        try {
            // create a new model
            IloCplex model = new IloCplex();
            // variables of the model
            IloNumVar[][][] x = new IloNumVar[numberOfVehicle][numberOfCities][];
            for (int r = 0; r < numberOfVehicle; r++) {
                for (int i = 0; i < numberOfCities; i++) {
                    x[r][i] = model.boolVarArray(numberOfCities);
                }
            }
            // Objective function
            IloLinearNumExpr objective = model.linearNumExpr();
            for (int r = 0; r < numberOfVehicle; r++) {
                for (int i = 0; i < numberOfCities; i++) {
                    for (int j = 0; j < numberOfCities; j++){
                        if(j != i){
                            objective.addTerm(cost[i][j], x[r][i][j]);
                        }
                    }
                }
            }
            model.addMinimize(objective);
            // constraints
            // first set of constraints
            // case i = numberOfCities-1
            for (int k = 0; k < numberOfVehicle; k++) {
                IloLinearNumExpr expression = model.linearNumExpr();
                for(int j = 0; j <numberOfCities; j++) {
                    expression.addTerm(1.0, x[k][j][numberOfCities-1]);
                    expression.addTerm(-1,x[k][numberOfCities-1][j]);
                }
                model.addEq(expression, 1);
            }
            // case i = 0
            for (int k = 0; k < numberOfVehicle; k++) {
                IloLinearNumExpr expression = model.linearNumExpr();
                for(int j = 0; j <numberOfCities; j++) {
                    expression.addTerm(1.0, x[k][j][0]);
                    expression.addTerm(-1,x[k][0][j]);
                }
                model.addEq(expression, -1);
            }
            // case i = 1,2,....,numberOfCities-2
            for (int k = 0; k < numberOfVehicle; k++) {
                for(int i = 1; i < numberOfCities-1; i++) {
                    IloLinearNumExpr expression = model.linearNumExpr();
                    for(int j = 0; j <numberOfCities; j++) {
                        expression.addTerm(1.0, x[k][j][i]);
                        expression.addTerm(-1,x[k][i][j]);
                    }
                    model.addEq(expression, 0);
                }
            }
            // second set of constraints (constraint 7)
            for(int i = 1; i < numberOfCities-1; i++) {
                IloLinearNumExpr expression = model.linearNumExpr();
                for(int k = 0; k < numberOfVehicle; k++) {
                    for(int j = 0; j < numberOfCities; j++) {
                        expression.addTerm(1.0, x[k][i][j]);
                    }
                }
                model.addEq(expression, 1);
            }
            // third set of constraints (constraint 8)
            for (int k = 0; k < numberOfVehicle; k++) {
                for (int i = 0; i < numberOfCities; i++) {
                    model.addEq(x[k][i][i], 0);
                }
            }
            // fouth set of constraints (constraint 9)
            for (int k = 0; k < numberOfVehicle; k++) {
                IloLinearNumExpr globalExpr = model.linearNumExpr();
                for(int i = 1; i < numberOfCities-1; i++) {
                    IloLinearNumExpr innerExpr = model.linearNumExpr();
                    for(int j = 0; j < numberOfCities; j++) {
                        innerExpr.addTerm(demand[i], x[k][i][j]);
                    }
                    globalExpr.add(innerExpr);
                }
                model.addLe(globalExpr, capacity);
            }
            // five th set of constraints (constraint 10)
            IloNumVar[] w = model.numVarArray(numberOfCities, 0, Double.MAX_VALUE);
            for (int k = 0; k < numberOfVehicle; k++) {
                for(int i = 0; i < numberOfCities; i++) {
                    for(int j = 0; j < numberOfCities; j++) {
                        IloLinearNumExpr expr = model.linearNumExpr();
                        expr.addTerm(1.0, w[i]);
                        expr.addTerm(-1.0, w[j]);
                        expr.addTerm(Math.max(0, lst[i]+cost[i][j]+service[i]-est[j]), x[k][i][j]);
                        model.addLe(expr, -cost[i][j] - service[i] + Math.max(0, lst[i]+cost[i][j]+service[i]-est[j]));
                    }
                }
            }
            // six th set of constraints (constraint 11)
            for(int i = 0; i < numberOfCities; i++) {
                model.addGe(w[i], est[i]);
                model.addLe(w[i], lst[i]);
            }
            //model.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.1);
            //model.setParam(IloCplex.Param.TimeLimit, 150);
            //model.setParam(IloCplex.Param.MIP.Strategy.Branch, 1);
            //model.setParam(IloCplex.Param.MIP.Strategy.NodeSelect, 0);
            //model.setParam(IloCplex.Param.MIP.Display, 0);
            if (model.solve()) {
                optimumCost = model.getObjValue();
            }else {
                optimumCost = -1;
            }
            gapOptimum = model.getMIPRelativeGap();
            runningTime = model.getCplexTime();
            System.out.println("Objective : " + optimumCost + "\nGap to Optimun : " + gapOptimum + "\nRunningTime : " + runningTime);
            model.end();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}
