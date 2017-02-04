/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mdp1;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.*;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.statehashing.simple.SimpleHashableStateFactory;

public class MDP1 {
		
    GraphDefinedDomain	gdd;
    Domain	        domain;
    State		initialState;
    RewardFunction	rf;
    TerminalFunction    tf;
    DiscreteStateHashFactory hashFactory;
    int                     numStates;	
    public MDP1(double p1, double p2, double p3, double p4) {
        this.numStates = 6;
        this.gdd = new GraphDefinedDomain(numStates);
        
        //actions from initial state 0
        this.gdd.setTransition(0, 0, 1, 1.);
        this.gdd.setTransition(0, 1, 2, 1.);
        this.gdd.setTransition(0, 2, 3, 1.);
        
		//transitions from action "a" outcome state
		this.gdd.setTransition(1, 0, 1, 1.);

		//transitions from action "b" outcome state
		this.gdd.setTransition(2, 0, 4, 1.);
		this.gdd.setTransition(4, 0, 2, 1.);

		//transitions from action "c" outcome state
		this.gdd.setTransition(3, 0, 5, 1.);
		this.gdd.setTransition(5, 0, 5, 1.);
        
        this.domain = this.gdd.generateDomain();
        this.initialState = GraphDefinedDomain.getState(this.domain,0);
		this.rf = new FourParamRF(p1,p2,p3,p4);
                this.tf = new NullTermination() ;
    }
    
    public static class FourParamRF implements RewardFunction {
		double p1;
		double p2;
		double p3;
		double p4;
		
		public FourParamRF(double p1, double p2, double p3, double p4) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
		}
		
		@Override
                public double reward(State s, GroundedAction a, State sprime){
                    int sid=GraphDefinedDomain.getNodeId(s);
                    double r;
                    
                    if (sid==0 || sid == 3){
                        r=0;
                    }
                    else if (sid==1){
                        r=this.p1;
                    }
                    else if (sid==2){
                        r=this.p2;
                    }
                    else if (sid==4){
                        r=this.p3;
                    }
                    else if (sid==5){
                        r=this.p4;
                    }
                    else {
                        throw new RuntimeException("invalid state"+sid);
                    }
                    return r;
                }
        // TODO:
		// Override the reward method to match the reward scheme from the state diagram.
		// See the documentation for the RewardFunction interface for the proper signature.
		// You may find the getNodeId method from GraphDefinedDomain class helpful.
    }
    
    // This method has only been added for the purpose of grading this quiz. DO NOT include 
    // it in an actual project, since it violates the principle of encapsulation by exposing
    // the private member domain.
    public Domain getDomain() {
        return this.domain;
    }
    
    public static void main(String[] args) {
        // You can add test code here that will be executed when you click "Test Run".
    	MDP1 fmdp = new MDP1();
    }
}
