package eu.su.mas.dedaleEtu.mas.behaviours;


import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Set;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ExploMultiSendMessageBehaviour;
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
public class TankerMultiBehaviour extends FSMBehaviour {
	private static final long serialVersionUID = 8567689731496787661L;
	/**
	 * Current knowledge of the agent regarding the environment
	 */
    MapRepresentation myMap;
    
    private String yourCarte;
	/**
	 * Nodes known but not yet visited
	 */
	private List<String> openNodes;
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	private String receiver;
	
	public TankerMultiBehaviour(final AbstractDedaleAgent myagent, final MapRepresentation myMap) {
		super(myagent);
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();

		//states
		this.registerFirstState(new RecevoirMessage(myagent),"recevoirMessage");
		this.registerState(new ExploMultiSendMessageBehaviour(myagent), "IsAnyoneThere?");
		this.registerState(new Repondre(myagent), "sendI'mHere!");
		this.registerState(new DemandeCarte(myagent), "demandeCarte");
		this.registerState(new EnvoieCarte(myagent),"envoieCarte");
		this.registerState(new IntegrerCarte(myagent), "integrerCarte");
		//transitions
		this.registerDefaultTransition("IsAnyoneThere?", "recevoirMessage");
		this.registerTransition("recevoirMessage","sendI'mHere!",4);   //receive: IsAnyoneThere?    
		this.registerDefaultTransition("sendI'mHere!", "demandeCarte");    // apres avoir envoyer "I'm here!", demander aussi la carte
		this.registerDefaultTransition("demandeCarte", "recevoirMessage");	// apres avoir envoyer une demande de carte, attendre le message
		this.registerTransition("recevoirMessage","demandeCarte",6);    // receve:   I'm here! 	
		this.registerTransition("recevoirMessage", "envoieCarte",3);
		this.registerDefaultTransition("envoieCarte", "recevoirMessage");
		this.registerTransition("recevoirMessage","integrerCarte", 7);  //apre avoir recu la carte , l'integrer
		this.registerDefaultTransition("integrerCarte","recevoirMessage");
		
//		this.registerDefaultTransition("receiveMessage", "receiveMessage");
	}

	@Override
	public int onEnd(){
		System.out.println("FSM behaviour terminé");
		myAgent.doDelete();
		return super.onEnd();
	}

    //debut du comportement ExploMultiReceiveMessageBehaviour
	public class RecevoirMessage extends OneShotBehaviour{
		private static final long serialVersionUID = 2019244314537795289L;
		private int next;
		/**
		 * 
		 * This behaviour is a one Shot.
		 * It receives a message tagged with an inform performative, destroy itlself
		 * @param myagent
		 */
		public RecevoirMessage(final AbstractDedaleAgent myagent) {
			super(myagent);
		}
		public void action() {
			this.myAgent.doWait(300);
			//1) receive the message
			MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			
			ACLMessage msg = this.myAgent.receive(msgTemplate);
			if (msg != null) {		
				String content = msg.getContent();
				switch(content) {
				  case "Send me your map.": next=3;break;
				  case "Is anyone there?": next = 4;break;
				  case "I'm here!": next = 6;break; 
				  default: String[] ss = content.split("=");
					       if (ss[0].equals("carte")) {
					    	   yourCarte = ss[1];
					    	   next = 7;
					       }else {
					    	  yourCarte = "";
					    	  next=1;
					       }
				}
				receiver = msg.getSender().getLocalName();
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
    //fin du comportement ExploMultiReceiveMessageBehaviour
	
	//debut de reponse 1
	public class Repondre extends OneShotBehaviour{
		private static final long serialVersionUID = 2019022316207795289L;
		/**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param receiverName The local name of the receiver agent
		 */
		public Repondre(final AbstractDedaleAgent myagent) {
			super(myagent);
		}
		public void action() {
			//1°Create the message
			final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
			//2° compute the random value		
			msg.setContent("I'm here!");
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);                   
			System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}
	}
	//fin de Reponse 1
	
	//debut de demandeCarte
	public class DemandeCarte extends OneShotBehaviour{
		private static final long serialVersionUID = 2019022316207795289L;
		/**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param receiverName The local name of the receiver agent
		 */
		public DemandeCarte(final AbstractDedaleAgent myagent) {
			super(myagent);
		}
		public void action() {
			//1°Create the message
			final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
			//2° compute the random value		
			msg.setContent("Send me your map.");
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);                   
			System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}
	}
	//fin de demandeCarte
	
	//debut de EnvoieCarte
	public class EnvoieCarte extends OneShotBehaviour{
		private static final long serialVersionUID = 2019022314407795289L;
		/**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param receiverName The local name of the receiver agent
		 */
		public EnvoieCarte(final AbstractDedaleAgent myagent) {
			super(myagent);
		}
		public void action() {
			//1°Create the message
			final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
			//2° encoder la connaissance
		    //2.1 encoder l'ensemble des noeuds fermes
			String myCarte = "";
			try {
				String nc = "";
				for (String n:closedNodes) {
					nc = nc + "*" + n;
				}
				//2.2 encoder l'ensemble des noeuds ouverts
				String no = "";
				for (String n:openNodes) {
					no = no + "*" + n;
				}
				myCarte = "carte" + "=" + nc + "|" + no + "|" +myMap.expo();
			}catch(Exception ex) {};
			msg.setContent(myCarte);
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);                   
			System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}
	}
	//fin de EnvoieCarte

	//debut de IntegrerCarte
	public class IntegrerCarte extends OneShotBehaviour{
		private static final long serialVersionUID = 2019022314407795289L;
		/**
		 * 
		 * @param myagent the Agent this behaviour is linked to
		 * @param receiverName The local name of the receiver agent
		 */
		public IntegrerCarte(final AbstractDedaleAgent myagent) {
			super(myagent);
		}
		public void action() {
			if (!yourCarte.equals("")) {
				System.out.println(this.myAgent.getLocalName()+" integrer une carte ");
				System.out.println("Carte avant:");
				System.out.print("closedNodes :");
				for (String nc:closedNodes) {
					System.out.print(nc);
				}
				System.out.println();
				System.out.print("openNodes :");
				for (String no:openNodes) {
					System.out.print(no);
				}
				System.out.println();
				
				try {
					// traitement de la chaine de caractere
					String[] ss = yourCarte.split("\\|");
					String[] ncs = ss[0].split("\\*");
					String[] nos = ss[1].split("\\*");
					String[] aretes = ss[2].split("\\*");
                    //traitement de noeds fermes
					for (String nc:ncs) {
						if (nc.equals(""))
							continue;
						if (!closedNodes.contains(nc)) {
							closedNodes.add(nc);
						    openNodes.remove(nc);
						    myMap.addNode(nc, MapAttribute.closed);
					    }    
					}

					//traitement de noeuds ouverts
					for (String no:nos) {
						if (no.equals(""))
							continue;
						if (closedNodes.contains(no))
							continue;
						if (openNodes.contains(no))
							continue;				
						openNodes.add(no);
					    myMap.addNode(no, MapAttribute.open);    
					}
					//traitement des aretes
					for (String e:aretes) {
						if (e.equals(""))
							continue;
						String[] n = e.split("-");
						myMap.addEdge(n[0], n[1]);
					}
				}catch(Exception e) {};
				
				System.out.println();
				System.out.println("Apres:");
				System.out.print("closedNodes:");
				for (String nc:closedNodes) {
					System.out.print(nc+" ");
				}				
				System.out.println();
				System.out.print("openNodes :");
				for (String no:openNodes) {
					System.out.print(no+" ");
				}
				System.out.println();
			}
		}
	}
	//fin de IntegrerCarte

	//#######################################################fonctions #####################################
/*	private void dfs() {
		while(iter.hasNext()){
			String nodeId=iter.next().getLeft();
			if (!closedNodes.contains(nodeId)){
				if (!openNodes.contains(nodeId)){
					openNodes.add(nodeId);
					myMap.addNode(nodeId, MapAttribute.open);
					myMap.addEdge(myPosition, nodeId);	
				}else{
					//the node exist, but not necessarily the edge
					myMap.addEdge(myPosition, nodeId);
				}
			    if (nextNode==null) nextNode=nodeId;
			}
		}
	}*/

    
}