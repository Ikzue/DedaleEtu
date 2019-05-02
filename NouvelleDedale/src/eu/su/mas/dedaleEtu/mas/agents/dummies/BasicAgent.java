package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

public abstract class BasicAgent extends AbstractDedaleAgent{

	private static final long serialVersionUID = -6431752665590433727L;
	
	/**
	 * Current knowledge of the agent regarding the environment
	 */
    public MapRepresentation myMap;
    public String destination;
    public String yourCarte;
	/**
	 * Nodes known but not yet visited
	 */
    public List<String> openNodes;
    public List<Couple<String,List<Couple<Observation,Integer>>>> treasureNodes;
    public HashMap<String,List<Couple<Observation,Integer>>> myTreasureNodes;
    public String prochainTresor;
    public List<String> chemin;
    public String tankerPosition;
	/**
	 * Visited nodes
	 */
    public Set<String> closedNodes;
    public String receiver;
	
    public int tache;
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
    protected void setup() {
		super.setup();
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.treasureNodes = new ArrayList<Couple<String,List<Couple<Observation,Integer>>>>();
		this.myTreasureNodes = new HashMap<String,List<Couple<Observation,Integer>>>();
		this.prochainTresor = null;
		if (getLocalName().contains("Explore"))
			this.tache = 10000;
		else if(getLocalName().contains("Collect"))
			this.tache = 20000;
    }
}
