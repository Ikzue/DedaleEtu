package eu.su.mas.dedaleEtu.mas.agents.dummies;


import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.ReceiveTreasureTankerBehaviour;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behavioursTanker.TankerMultiBehaviour;
import jade.core.behaviours.Behaviour;


public class TankerMultiAgent extends BasicAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784843333772918359L;
	protected void setup(){

		
		super.setup();

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/		
		TankerMultiBehaviour fsm = new TankerMultiBehaviour(this);
		lb.add(fsm);
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
				
		addBehaviour(new startMyBehaviours(this,lb));
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}
	@Override
	public String getType() {
		return "Tanker";
	}
}