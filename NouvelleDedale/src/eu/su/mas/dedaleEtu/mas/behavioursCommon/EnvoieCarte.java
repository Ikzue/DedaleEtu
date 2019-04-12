package eu.su.mas.dedaleEtu.mas.behavioursCommon;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class EnvoieCarte extends OneShotBehaviour{
	private static final long serialVersionUID = 2019022314407795289L;
	private BasicAgent monAgent;
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param receiverName The local name of the receiver agent
	 */
	public EnvoieCarte(final AbstractDedaleAgent myagent) {
		super(myagent);
		monAgent = (BasicAgent) myagent;
	}
	public void action() {
		//1°Create the message
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(monAgent.receiver, AID.ISLOCALNAME));  
		//2° encoder la connaissance
	    //2.1 encoder l'ensemble des noeuds fermes
		String myCarte = "";
		try {
			String nc = "";
			for (String n:monAgent.closedNodes) {
				nc = nc + "*" + n;
			}
			//2.2 encoder l'ensemble des noeuds ouverts
			String no = "";
			for (String n:monAgent.openNodes) {
				no = no + "*" + n;
			}
			myCarte = "carte" + "=" + nc + "|" + no + "|" +monAgent.myMap.expo();
		}catch(Exception ex) {};
		msg.setContent(myCarte);
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);                   
		System.out.println(this.myAgent.getLocalName()+" sent to "+monAgent.receiver+" ,content= "+msg.getContent());
	}
}
