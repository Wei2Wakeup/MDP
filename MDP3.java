/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mdp3;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.singleagent.SADomain;

/**
 *
 * @author wewang
 */
public class MDP3 {
    
    DomainGenerator				dg;
    SADomain				    domain;
    State			      initialState;
    RewardFunction                              rf;
    TerminalFunction                            tf;
    SimpleHashableStateFactory             hashFactory;
    int                                  numStates,numActions;


    public MDP3(int numStates, int numActions, double[][][] probabilitiesOfTransitions, 
                     double[][][] rewards) {
        // add your code here
        this.numStates=numStates;
        this.numActions=numActions;
        this.dg = new GraphDefinedDomain(numStates);
        
        for(int i=0;i<this.numStates;i++){
            for(int j=0;j<this.numActions;j++){
                for(int k=0;k<this.numStates;k++){
                   // if (probabilitiesOfTransitions[i][j][k]>0){
                    //   System.out.println("Probability is " + probabilitiesOfTransitions[i][j][k]);
                      ((GraphDefinedDomain) this.dg).setTransition(i, j, k, probabilitiesOfTransitions[i][j][k]);  
                  //  }
                }
            }
        }
        

        // Please add your transitions above this line.
        this.rf = new SpecficRF(rewards);
        ((GraphDefinedDomain) this.dg).setRf(this.rf);
        this.tf = new NullTermination();
         ((GraphDefinedDomain) this.dg).setTf(this.tf);
        
        this.domain = ((GraphDefinedDomain) this.dg).generateDomain();
        this.initialState = new GraphStateNode(0);  // Initial state is created
        //((GraphStateNode) this.initialState).setId(0); // Initial state is initialized
        
        this.hashFactory = new SimpleHashableStateFactory();
    }
    
    public static class SpecficRF implements RewardFunction {
     
        double[][][] rewards;
         public  SpecficRF(double[][][] rewards) {
            this.rewards = rewards;
        }
        @Override
		public double reward(State s, Action  a, State t){
                    
                 
                    int sid= ((GraphStateNode) s).getId();
                    int tid= ((GraphStateNode) t).getId();
                    String aids= a.actionName().replaceAll("action","");
                    int aid = Integer.parseInt(aids);
                    double r;
                    
                    r = this.rewards[sid][aid][tid];
                   
                    return r;
		}
    }
   
    
    public double valueOfState(int state, double gamma) {
        // add your code here
        double maxDelta=0.001;
        int maxIterations=2000;
        double value;
        State s;
        ValueIteration vi= new ValueIteration(this.domain, gamma, 
                this.hashFactory, maxDelta, maxIterations);
        vi.planFromState(this.initialState);
        s= new GraphStateNode(state);
        value = vi.value(s);
        return value;
    }
       
    
    public static void main(String[] args) {
		int numStates = 3;
                int numActions = 1;
                double[][][] probabilitiesOfTransitions = {{{0.0,0.6,0.4}},{{0.0,1.0,0.0}},{{0.0,0.0,1.0}}};
                double[][][] rewards = {{{0.0,-3.0,2.0}},{{0.0,0.0,0.0}},{{0.0,0.0,0.0}}};
		MDP3 mdp = new MDP3(numStates,numActions,probabilitiesOfTransitions,rewards);
		
		int state = 0;
                double gamma = 0.5;
		System.out.println("value of the state is " + mdp.valueOfState(state,gamma));
	}
    
}


