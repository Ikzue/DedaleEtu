package eu.su.mas.dedaleEtu.mas.behaviours;


import java.util.Scanner;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.FSMBehaviour;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import eu.su.mas.dedaleEtu.mas.behavioursCollect.MoveCollector;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.DemandeCarte;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.EnvoieCarte;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ExploMultiSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.IntegrerCarte;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ProtocoleInterBlocage;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ProtocoleInterBlocagePriorite;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.RecevoirMessage;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.Repondre;

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
public class TankerMultiBehaviour extends FSMBehaviour {
	private static final long serialVersionUID = 8567689731496787661L;
	/**
	 * Current knowledge of the agent regarding the environment
	 */
	public TankerMultiBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
		//states
		this.registerFirstState(new TankerRecevoirMessage(myagent),"recevoirMessage");
		this.registerState(new ExploMultiSendMessageBehaviour(myagent), "IsAnyoneThere?");
		this.registerState(new MoveCollector(myagent), "move");
		this.registerState(new Repondre(myagent), "sendI'mHere!");		System.out.println();
		this.registerState(new DemandeCarte(myagent), "demandeCarte");
		this.registerState(new EnvoieCarte(myagent),"envoieCarte");
		this.registerState(new IntegrerCarte(myagent), "integrerCarte");
		this.registerState(new ProtocoleInterBlocage(myagent), "protocoleInterBlocage");
		this.registerState(new ProtocoleInterBlocagePriorite(myagent), "protocoleInterBlocagePriorite");
		//transitions
		this.registerDefaultTransition("recevoirMessage","recevoirMessage"); 
		this.registerTransition("recevoirMessage","sendI'mHere!",4);   //receive: IsAnyoneThere?    
		this.registerDefaultTransition("sendI'mHere!", "demandeCarte");    // apres avoir envoyer "I'm here!", demander aussi la carte
		this.registerDefaultTransition("demandeCarte", "recevoirMessage");	// apres avoir envoyer une demande de carte, attendre le message
		this.registerTransition("recevoirMessage", "envoieCarte",3);
		this.registerDefaultTransition("envoieCarte", "recevoirMessage");
		this.registerTransition("recevoirMessage","integrerCarte", 7);  //apre avoir recu la carte , l'integrer
		this.registerDefaultTransition("integrerCarte","recevoirMessage");

	}

	@Override
	public int onEnd(){
		System.out.println("FSM behaviour terminé");
		myAgent.doDelete();
		return super.onEnd();
	}

    //debut du comportement ExploMultiReceiveMessageBehaviour
	public class TankerRecevoirMessage extends OneShotBehaviour{
		private static final long serialVersionUID = 2019244314537795289L;
		private int next;
        private BasicAgent monAgent;
		/**
		 * 
		 * This behaviour is a one Shot.
		 * It receives a message tagged with an inform performative, destroy itlself
		 * @param myagent
		 */
		public TankerRecevoirMessage(final AbstractDedaleAgent myagent) {
			super(myagent);
			// si l'agent silo ne bouge pas alors il suffit de ajouter sa position initiale dans closedNodes
			monAgent = (BasicAgent)myagent;
		}
		public void action() {
			String myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
	    	monAgent.closedNodes.add(myPosition);
	    	monAgent.myMap.addNode(myPosition,MapAttribute.closed);
	    	System.out.print(monAgent.getLocalName()+" openNodes"+monAgent.closedNodes);
			this.myAgent.doWait(300);
			//1) receive the message
			MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			
			ACLMessage msg = this.myAgent.receive(msgTemplate);
			if (msg != null) {		
				String content = msg.getContent();
				switch(content) {
				  case "Send me your map.": next=3;break;
				  case "Is anyone there?": next = 4;break;
				  default: String[] ss = content.split("=");
					       if (ss[0].equals("carte")) {
					    	   monAgent.yourCarte = ss[1];
					    	   next = 7;
					       }else {
					    	  monAgent.yourCarte = "";
					    	  next=1;
					       }
				}
				monAgent.receiver = msg.getSender().getLocalName();
				System.out.println(this.myAgent.getLocalName()+"<----Result received from "+
						    msg.getSender().getLocalName()+" ,content= "+msg.getContent()+", next = "+next);
			}else {next = 1;};
		}
		public int onEnd() {
			try {
				return next;
			}catch(Exception e) {return 0;}
		}
	}
    //fin du comportement TankerMessageBehaviour    
}