package eu.su.mas.dedaleEtu.mas.behavioursCommon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

/***
 * This behaviour allows the agent who possess it to send nb random int within [0-100[ to another agent whose local name is given in parameters
 * 
 * There is not loop here in order to reduce the duration of the behaviour (an action() method is not preemptive)
 * The loop is made by the behaviour itslef
 * 
 * @author Cédric Herpson
 *
 */


public class ExploMultiSendMessageBehaviour extends OneShotBehaviour{
	private static final long serialVersionUID = 2019022316207795289L;
	private BasicAgent monAgent;
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param receiverName The local name of the receiver agent
	 */
	public ExploMultiSendMessageBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
		monAgent = (BasicAgent)myagent;
	}


	public void action() {
		System.out.println(this.myAgent.getLocalName()+ " execute le comportement ExploMultiSendBehaviour.");
		List<String> receivers = monAgent.getNameAgents();
		//1°Create the message
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))     
				continue;                   //pas de message pour lui meme
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
		}
		//2° set message content		
		msg.setContent("Is anyone there?");
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))   //afficher tous les messages envoyes
				continue;                   
			//System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}
	}
}
