package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

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
    public HashMap<String,List<Couple<Observation,Integer>>> myTreasureNodes;
    public HashMap<String,List<Couple<Observation,Integer>>> myTreasureNodesNonAccessible;
    public String prochainTresor;
    public List<String> chemin;
    public List<String> cheminTresor;
    public String tankerPosition;
    public int nbMessageEnvoye;
	/**
	 * Visited nodes
	 */
    public Set<String> closedNodes;
    public String receiver;
	
    public int priorite = 0;
    public int compteur = 10;
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
    protected void setup() {
		super.setup();
		register();
		this.openNodes=new ArrayList<String>();
		this.nbMessageEnvoye = 0;
		this.closedNodes=new HashSet<String>();
		//this.treasureNodes = new ArrayList<Couple<String,List<Couple<Observation,Integer>>>>();
		this.myTreasureNodes = new HashMap<String,List<Couple<Observation,Integer>>>();
		this.myTreasureNodesNonAccessible = new HashMap<String,List<Couple<Observation,Integer>>>();
		this.prochainTresor = null;
		this.chemin = new ArrayList<String>();
		this.cheminTresor = new ArrayList<String>();
        this.priorite = genererPriorite();
    }
    
    private void register() {
    	DFAgentDescription dfd = new DFAgentDescription();
    	dfd.setName(getAID());
    	ServiceDescription sd = new ServiceDescription();
    	sd.setType(getType());
    	sd.setName(getLocalName());
    	dfd.addServices(sd);
    	try {
    		DFService.register(this, dfd);
    	}catch(FIPAException fe) {
    		fe.printStackTrace();
    	}
    }
    public List<String> getNameAgents(){
    	String[] listTypes = {"Collecteur","Explorateur","Tanker"};
    	List<String> nameAgents = new ArrayList<String>();
    	DFAgentDescription dfd = new DFAgentDescription();
    	ServiceDescription sd = new ServiceDescription();
    	for(String type:listTypes) {
    		sd.setType(type);
    		dfd.addServices(sd);
    		try {
				DFAgentDescription[] result = DFService.search(this, dfd);
				for(DFAgentDescription r:result) {
					nameAgents.add(r.getName().getLocalName());
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
    	}
    	return nameAgents;
    	
    }
    public List<String> getNameAgents(String type){
    	List<String> nameAgents = new ArrayList<String>();
    	DFAgentDescription dfd = new DFAgentDescription();
    	ServiceDescription sd = new ServiceDescription();
    	sd.setType(type);
    	dfd.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			for(DFAgentDescription r:result) {
				nameAgents.add(r.getName().getLocalName());
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
    	return nameAgents;
    }
    public int genererPriorite() {
        if ((compteur%10) == 0) {
            compteur = 1;
            if(this.getType().contains("Collect")) {
                this.priorite = 500;
                Random n = new Random();
                this.priorite += n.nextInt(1000);
            }
            if(this.getType().contains("Tank")) {
                this.priorite = this.priorite == 0? 10000:0;
            }
        }else {
            compteur += 1;
        }
        return this.priorite;
    }
	public abstract String getType();
}
