package com.model;

public class Model {
    private Simplex simplex;
    private Simplex.OutputData simplexOutputData;
    //private Graph.OutputData graphOutputData;
    //private Graph graph;

    public Model(){
        simplex = new Simplex();
        //graph = new Graph();
    }

    private Simplex.OutputData getSimplexSolution(Restriction[] Restrictions, Objective objective){
        simplexOutputData = new Simplex.OutputData();
        simplexOutputData = simplex.getResult(Restrictions, objective);
        return simplexOutputData;
    }
/*
    private Graph.OutputData getGraphSolution(Restriction[] Restrictions, MainFunc mainFunc){
        //Надо сначала сделать graph
        //graphOutputData = new Graph.OutputData();
        //graphOutputData = graph.getResult(Restrictions, mainFunc);
        //return graphOutputData;
    }
*/
}
