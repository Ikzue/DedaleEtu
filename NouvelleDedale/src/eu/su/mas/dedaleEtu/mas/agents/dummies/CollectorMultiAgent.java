package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behavioursCollect.CollectorMultiBehaviour;
import jade.core.behaviours.Behaviour;

/**
 * ExploreSolo agent. 
 * It explore the map using a DFS algorithm.
 * It stops when all nodes have been visited
 *  
 *  
 * @author hc
 *
 */

public class CollectorMultiAgent extends BasicAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	public int backpackMaxCapacity;
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	
	protected void setup(){
		super.setup();
		this.backpackMaxCapacity = this.getBackPackFreeSpace();
		List<Behaviour> lb=new ArrayList<Behaviour>();
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		CollectorMultiBehaviour fsm = new CollectorMultiBehaviour(this);
		
		lb.add(fsm);
		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");
	}
	
	@Override
	public String getType() {
		return "Collecteur";
	}
	public int getBackpackCapacity() {
		return backpackMaxCapacity;
	}
	
	
	
}
