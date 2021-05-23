import ilog.concert.*;
import ilog.cplex.*;

public class VRPODMIPModel {
    public void solve(VRPODInstances data, double zeta, double rho) {
        int numberOfCustumer = data.numberCustomers+1;
        int numberOfOccasionalDriver = data.numberOccasDriver;
        int numberOfVehicle = data.numberOfVehicle;
        int Capacity = data.capacity;
        int totalSteps = data.steps.length;
        int[] demand = new int[totalSteps];
        double[][] cost = new double[totalSteps][totalSteps];
        double[][] travel = new double[totalSteps][totalSteps];
        for (int i = 0; i < totalSteps; i++) {
            demand[i] = data.steps[i].demand;
            for (int j = 0; j < totalSteps; j++) {
                cost[i][j] =  Math.sqrt(Math.pow(data.steps[i].xCoordinate - data.steps[j].xCoordinate, 2) + Math.pow(data.steps[i].yCoordinate - data.steps[j].yCoordinate, 2));
                travel[i][j] =  Math.sqrt(Math.pow(data.steps[i].xCoordinate - data.steps[j].xCoordinate, 2) + Math.pow(data.steps[i].yCoordinate - data.steps[j].yCoordinate, 2));
            }
        }

        try {
            // create a new model
            IloCplex model = new IloCplex();
            // variables of the model
            // x(i,j) variable indicating whether a regular vehicle traverses arc (i,j).
            IloNumVar[][] x = new IloNumVar[totalSteps][];
            for (int i = 0; i < totalSteps; i++) {
                x[i] = model.boolVarArray(totalSteps);
            }
            // y(i,j) indicate the load a regular vehicle carries on the arc (i,j).
            IloNumVar[][] y = new IloNumVar[totalSteps][totalSteps];
            for (int i = 0; i < totalSteps; i++) {
                for (int j = 0; j < totalSteps; j++) {
                    y[i][j] = model.numVar(0, Double.MAX_VALUE, "y");
                }
            }
            // z(i) indicating whether a customer is visited by a regular vehicle
            IloNumVar[] z = new IloNumVar[numberOfCustumer];
            for (int i = 0; i < numberOfCustumer; i++) {
                z[i] = model.boolVar("z");
            }
            // w(i,k) binary variable indicating whether customer i is visited by occasional driver k
            IloNumVar[][] w = new IloNumVar[numberOfCustumer][];
            for (int i = 0; i < numberOfCustumer; i++) {
                w[i] = model.boolVarArray(numberOfOccasionalDriver);
            }

            // beta(i,k) indicate whether occasional driver k can serve customer i.
            // paid(i,k)  indicate the compensation paid to occasional driver k when delivering to customer i.
            int[][] beta = new int[numberOfCustumer][numberOfOccasionalDriver];
            double[][] paid = new double[numberOfCustumer][numberOfOccasionalDriver];
            for (int i = 0; i < numberOfCustumer; i++) {
                for (int k = 0; k < numberOfOccasionalDriver; k++) {
                    paid[i][k] = rho * travel[0][i];
                    if (travel[0][i] + travel[i][k] <= zeta * travel[0][k])
                        beta[i][k] = 1;
                    else
                        beta[i][k] = 0;
                }
            }

            // Objective function
            IloLinearNumExpr minimumCost = model.linearNumExpr();
            IloLinearNumExpr expression = model.linearNumExpr();
            for (int i = 0; i < totalSteps; i++) {
                for (int j = 0; j < totalSteps; j++){
                    minimumCost.addTerm(cost[i][j], x[i][j]);
                }
            }
            for (int i = 0; i < numberOfCustumer; i++) {
                for (int k = 0; k < numberOfOccasionalDriver; k++){
                    expression.addTerm(paid[i][k], w[i][k]);
                }
            }
            minimumCost.add(expression);
            model.addMinimize(minimumCost);

            // Constraint (1)
            for (int i = 0; i < numberOfCustumer; i++) {
                IloLinearNumExpr expression1 = model.linearNumExpr();
                IloLinearNumExpr expression2 = model.linearNumExpr();
                for (int j = 0; j < totalSteps; j++) {
                    expression1.addTerm(1.0, x[i][j]);
                    expression2.addTerm(1.0, x[j][i]);
                }
                model.addEq(expression1, expression2);
                model.addEq(expression1, z[i]);
                model.addEq(expression2, z[i]);
            }

            // constraint (2)
            IloLinearNumExpr expression1 = model.linearNumExpr();
            IloLinearNumExpr expression2 = model.linearNumExpr();
            for (int j = 0; j < totalSteps; j++) {
                expression1.addTerm(1.0, x[0][j]);
                expression2.addTerm(1.0, x[j][0]);
            }
            model.addEq(expression1, expression2);

            // constraint (3)
            for (int i = 0; i < numberOfCustumer; i++) {
                IloLinearNumExpr expression3 = model.linearNumExpr();
                IloLinearNumExpr expression4 = model.linearNumExpr();
                IloLinearNumExpr expression5 = model.linearNumExpr();
                IloLinearNumExpr expression6 = model.linearNumExpr();
                for (int j = 0; j < totalSteps; j++) {
                    expression3.addTerm(1.0, y[j][i]);
                    expression4.addTerm(-1.0, y[i][j]);
                }
                expression3.add(expression4);
                for (int l = 0; l < numberOfCustumer; l++) {
                    expression5.addTerm(-demand[i], z[i]);
                }
                expression6.addTerm(demand[i], z[i]);
                if (i == 0)
                    model.addEq(expression3, expression5);
                else
                    model.addEq(expression3, expression6);
            }

            // constraint (4)
            for (int i = 0; i < totalSteps; i++) {
                for (int j = 0; j < totalSteps; j++) {
                    model.addLe(y[i][j], model.prod(Capacity,x[i][j]));
                }
            }

            // constraint (5)
            for (int i = 0; i < numberOfCustumer; i++) {
                model.addEq(y[i][0], 0);
            }

            // constraint (6)
            for (int i = 0; i < numberOfCustumer; i++) {
                for (int k = 0; k < numberOfOccasionalDriver; k++) {
                    model.addLe(w[i][k], beta[i][k]);
                }
            }

            // constraint (7)
            for (int k = 0; k < numberOfOccasionalDriver; k++) {
                IloLinearNumExpr expression7 = model.linearNumExpr();
                for (int i = 0; i < numberOfCustumer; i++) {
                    expression7.addTerm(1.0, w[i][k]);
                }
                model.addLe(expression7, 1);
            }

            // constraint (8)
            for (int i = 0; i < numberOfCustumer; i++) {
                IloLinearNumExpr expression8 = model.linearNumExpr();
                for (int k = 0; k < numberOfOccasionalDriver; k++) {
                    expression8.addTerm(1.0, w[i][k]);
                }
                expression8.addTerm(1.0, z[i]);
                model.addEq(expression8, 1);
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
