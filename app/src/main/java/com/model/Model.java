package com.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Model {
    private Simplex simplex;
    private Simplex.OutputData simplexOutputData;
    private GraphSolver.OutputData graphOutputData;
    private GraphSolver graph;

    public Model() {
        simplex = new Simplex();
        graph = new GraphSolver();
    }

    public Simplex.OutputData getSimplexSolution(Restriction[] Restrictions, Objective objective) {
        simplexOutputData = new Simplex.OutputData();
        simplexOutputData = simplex.getResult(Restrictions, objective);
        return simplexOutputData;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GraphSolver.OutputData getGraphSolution(List<GraphRestriction> graphRestrictList,
                                                    GraphObjective graphObjective) {
        graphOutputData = new GraphSolver.OutputData();
        graphOutputData = graph.calculateGraphOutputData(graphRestrictList, graphObjective);
        return graphOutputData;
    }
}
