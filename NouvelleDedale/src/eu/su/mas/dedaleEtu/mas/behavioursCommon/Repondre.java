package eu.su.mas.dedaleEtu.mas.behavioursCommon;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class Repondre extends OneShotBehaviour{
	private static final long serialVersionUID = 2019022316207795289L;
	private BasicAgent monAgent;
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param receiverName The local name of the receiver agent
	 */
	public Repondre(final AbstractDedaleAgent myagent) {
		super(myagent);
		monAgent = (BasicAgent) myagent;
	}
	public void action() {
		System.out.println(this.myAgent.getLocalName()+ " execute le comportement Repondre.");
		//1°Create the message
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(monAgent.receiver, AID.ISLOCALNAME));  
		//2° compute the random value		
		msg.setContent("I'm here!");
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);                   
		//System.out.println(this.myAgent.getLocalName()+" sent to "+monAgent.receiver+" ,content= "+msg.getContent());
	}
}
