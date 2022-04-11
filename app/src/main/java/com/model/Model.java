package com.model;

//import android.os.Build;

//import androidx.annotation.RequiresApi;

//import com.model.graphdata.GraphObjective;
//import com.model.graphdata.GraphOutputData;
//import com.model.graphdata.GraphRestriction;
import com.model.simplexdata.Objective;
import com.model.simplexdata.Restriction;
import com.model.simplexdata.SimplexOutputData;
//import com.usecase.GraphSolver;
import com.usecase.Simplex;

import java.util.List;

public class Model {
    private Simplex simplex;
    private SimplexOutputData simplexOutputData;
    //private GraphOutputData graphOutputData;
    //private GraphSolver graph;

    public Model() {
        simplex = new Simplex();
        //graph = new GraphSolver();
    }

    public SimplexOutputData getSimplexSolution(Restriction[] Restrictions, Objective objective) {
        simplexOutputData = new SimplexOutputData();
        simplexOutputData = simplex.getResult(Restrictions, objective);
        return simplexOutputData;
    }

    static public class MatrixItem {
        public boolean isHeader;
        public String value;
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.O)
    public GraphOutputData getGraphSolution(List<GraphRestriction> graphRestrictList,
                                                   GraphObjective graphObjective) {
        graphOutputData = new GraphOutputData();
        graphOutputData = graph.calculateGraphOutputData(graphRestrictList, graphObjective);
        return graphOutputData;
    }
    */
}
