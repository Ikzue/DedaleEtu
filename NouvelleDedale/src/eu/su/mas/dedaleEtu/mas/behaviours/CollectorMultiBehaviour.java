package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.CollectorMultiAgent;
import eu.su.mas.dedaleEtu.mas.behavioursCollect.MoveCollector;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.DemandeCarte;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.EnvoieCarte;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ExploMultiSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.IntegrerCarte;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ProtocoleInterBlocage;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ProtocoleInterBlocagePriorite;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.RecevoirMessage;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.Repondre;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.FSMBehaviour;


/**
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.</br>
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs.</br> 
 * This (non optimal) behaviour is done until all nodes are explored. </br> 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.</br> 
 * Warning, this behaviour is a solo exploration and does not take into account the presence of other agents (or well) and indefinitely tries to reach its target node
 * @author hc
 *
 */
public class CollectorMultiBehaviour extends FSMBehaviour {
	private static final long serialVersionUID = 8567689731496787661L;
	/**
	 * Current knowledge of the agent regarding the environment
	 */
	
	public CollectorMultiBehaviour(final AbstractDedaleAgent myagent, final MapRepresentation myMap) {
		super(myagent);

		//states
		this.registerFirstState(new RecevoirMessage(myagent),"recevoirMessage");
		this.registerState(new ExploMultiSendMessageBehaviour(myagent), "IsAnyoneThere?");
		this.registerState(new MoveCollector(myagent), "move");
		this.registerState(new Repondre(myagent), "sendI'mHere!");		System.out.println();
		this.registerState(new DemandeCarte(myagent), "demandeCarte");
		this.registerState(new EnvoieCarte(myagent),"envoieCarte");
		this.registerState(new IntegrerCarte(myagent), "integrerCarte");
		this.registerState(new ProtocoleInterBlocage(myagent), "protocoleInterBlocage");
		this.registerState(new ProtocoleInterBlocagePriorite(myagent), "protocoleInterBlocagePriorite");
		//transitions
		this.registerDefaultTransition("recevoirMessage","move");
		this.registerDefaultTransition("move", "IsAnyoneThere?");
		this.registerDefaultTransition("IsAnyoneThere?", "recevoirMessage");
		this.registerTransition("recevoirMessage","sendI'mHere!",4);   //receive: IsAnyoneThere?    
		this.registerDefaultTransition("sendI'mHere!", "demandeCarte");    // apres avoir envoyer "I'm here!", demander aussi la carte
		this.registerDefaultTransition("demandeCarte", "recevoirMessage");	// apres avoir envoyer une demande de carte, attendre le message
		this.registerTransition("recevoirMessage","demandeCarte",6);    // receve:   I'm here! 	
		this.registerTransition("recevoirMessage", "envoieCarte",3);
		this.registerDefaultTransition("envoieCarte", "recevoirMessage");
		this.registerTransition("recevoirMessage","integrerCarte", 7);  //apre avoir recu la carte , l'integrer
		this.registerDefaultTransition("integrerCarte","recevoirMessage");
		this.registerTransition("move", "protocoleInterBlocagePriorite", 8);
		this.registerTransition("protocoleInterBlocage", "recevoirMessage",0);
		this.registerTransition("protocoleInterBlocage", "move",1);
		this.registerTransition("protocoleInterBlocagePriorite", "protocoleInterBlocage",2);
		this.registerTransition("protocoleInterBlocagePriorite", "recevoirMessage",0);
		this.registerTransition("protocoleInterBlocagePriorite", "move",1);
		
//		this.registerDefaultTransition("receiveMessage", "receiveMessage");

	}

	@Override
	public int onEnd(){
		System.out.println("FSM behaviour terminé");
		myAgent.doDelete();
		return super.onEnd();
	}



	

	

}