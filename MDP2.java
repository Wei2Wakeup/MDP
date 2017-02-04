/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mdp2;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
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
public class MDP2 {
    
    DomainGenerator				dg;
    SADomain				    domain;
    State			      initialState;
    RewardFunction                              rf;
    TerminalFunction                            tf;
    SimpleHashableStateFactory             hashFactory;
    int                                  numStates;
    double                                   p1,p2;

		
    public MDP2(double p1, double p2) {
        this.numStates = 6;
        this.dg = new GraphDefinedDomain(numStates);
        this.p1=p1;
        this.p2=p2;
        //actions from initial state 0
        ((GraphDefinedDomain) this.dg).setTransition(0, 0, 0, p1);
        ((GraphDefinedDomain) this.dg).setTransition(0, 0, 1, 1.-p1);
        ((GraphDefinedDomain) this.dg).setTransition(0, 1, 2, 1.);
        
         //actions from initial state 1
        ((GraphDefinedDomain) this.dg).setTransition(1, 0, 5, p2);
        ((GraphDefinedDomain) this.dg).setTransition(1, 0, 3, 1.-p2);
        ((GraphDefinedDomain) this.dg).setTransition(1, 1, 4, 1.);
        
         //actions from initial state 2
        ((GraphDefinedDomain) this.dg).setTransition(2, 0, 1, 1.);
        
        //actions from initial state 3
        ((GraphDefinedDomain) this.dg).setTransition(3, 0, 1, 1.);
        
        //actions from initial state 4
        ((GraphDefinedDomain) this.dg).setTransition(4, 0, 5, 1.);
        

        // Please add your transitions above this line.
        this.rf = new SpecficRF();
        ((GraphDefinedDomain) this.dg).setRf(this.rf);
        this.tf = new SingleStateTF(5);
        ((GraphDefinedDomain) this.dg).setTf(this.tf);
        
        this.domain = ((GraphDefinedDomain) this.dg).generateDomain();
        
        this.initialState = new GraphStateNode(0);  // Initial state is created
        //((GraphStateNode) this.initialState).setId(0); // Initial state is initialized
        //this.rf = new SpecficRF();
        //this.tf = new SingleStateTF(5);
        this.hashFactory = new SimpleHashableStateFactory();
    }
    public static class SpecficRF implements RewardFunction {
     
        @Override
		public double reward(State s, Action  a, State t){
                    
                 
                    int sid= ((GraphStateNode) s).getId();
                    int tid= ((GraphStateNode) t).getId();
                    double r=0;
                    
                    if (sid==0){
                        if(tid==0) r=-1;
                        if(tid==1) r=3;
                        if(tid==2) r=1;
                    }
                    else if (sid==1){
                        if(tid==3) r=1;
                        if(tid==4) r=2;
                        if(tid==5) r=0;
                    }
                    else if (sid>=2 && sid <=4){
                        r=0;
                    }
                    else if (sid==5){
                        throw new RuntimeException("No transition from state:"+sid);
                    }
                    else{
                        throw new RuntimeException("Unknown state:"+sid);
                    }
                   
                    return r;
		}
    }
    public static class SingleStateTF implements TerminalFunction {
         int terminalSid;
         
         public SingleStateTF(int sid){
             this.terminalSid = sid;
         }
         
         @Override
         public boolean isTerminal(State s){
             
             int sid;
             sid = ((GraphStateNode) s).getId();
             return sid==this.terminalSid;
         }
         
         
     } 
    
    private ValueIteration computeValue(double gamma){
        double maxDelta=0.001;
        int maxIterations=1000;
        ValueIteration vi= new ValueIteration(this.domain, gamma,this.hashFactory, maxDelta,maxIterations);
        vi.planFromState(this.initialState);
        return vi;
    }
    
    public String bestActions(double gamma) {
        // Return one of the following Strings
        // "a,c"
        // "a,d"
        // "b,c" 
        // "b,d"
        // based on the optimal actions at states S0 and S1. If 
        // there is a tie, break it in favor of the letter earlier in
        // the alphabet (so if "a,c" and "a,d" would both be optimal, 
        // return "a,c").
        ValueIteration vi=computeValue(gamma);
        Policy p= new GreedyQPolicy(vi);
        State s0,s1;
        s0= new GraphStateNode(0);
        //((GraphStateNode) s0).setId(0);
        s1= new GraphStateNode(1);
        //((GraphStateNode) s1).setId(1);
        
        String s0Action = p.action(s0).actionName().replaceAll("action0", 
                "a").replaceAll("action1", "b");
        String s1Action = p.action(s1).actionName().replaceAll("action0", 
                "c").replaceAll("action1", "d");
        
        String policy = s0Action + "," + s1Action;
        
        return policy;
        
        
    }
    
    public static void main(String[] args) {
		double p1 = 0.5;
		double p2 = 0.5;
		MDP2 mdp = new MDP2(p1,p2);
		
		double gamma = 0.5;
		System.out.println("Best actions: " + mdp.bestActions(gamma));
	}
    
}
