package eu.su.mas.dedaleEtu.mas.behavioursTanker;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class TankerEnvoieCarte extends OneShotBehaviour {
	private BasicAgent monAgent;
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param receiverName The local name of the receiver agent
	 */
	public TankerEnvoieCarte(final AbstractDedaleAgent myagent) {
		super(myagent);
		monAgent = (BasicAgent) myagent;
	}
	@Override
	public void action() {
		System.out.println(monAgent.getLocalName()+"execute TankerEnvoieCarte");
		//Send the position of the Tanker
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(monAgent.receiver, AID.ISLOCALNAME));  
		msg.setContent("Tanker:" + monAgent.tankerPosition);
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);                   
		System.out.println(this.myAgent.getLocalName()+" sent to "+monAgent.receiver+" ,content= "+msg.getContent());
		//1°Create the message
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
        msg.setContent("InterBlocage:10000:"+myCarte);
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);                   
		System.out.println(this.myAgent.getLocalName()+" sent to "+monAgent.receiver+" ,content= "+msg.getContent());		
		msg.setContent("Send me your map.");
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);  
	}

}
