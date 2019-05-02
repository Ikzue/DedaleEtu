package eu.su.mas.dedaleEtu.mas.behavioursTanker;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
        next = 0;
		System.out.println(monAgent.getLocalName()+"execute Tanker RecevoirMessage");
    	System.out.println(monAgent.getLocalName()+" openNodes"+monAgent.openNodes);
    	System.out.println(monAgent.getLocalName()+" closedNodes"+monAgent.closedNodes);
		this.myAgent.doWait(300);
		//1) receive the message
		MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			
		ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {		
			String content = msg.getContent();
			switch(content) {
			  case "Is anyone there?": next = 4;break;
			  default: String[] ss = content.split("=");
				       if (ss[0].equals("carte")) {
				    	   monAgent.yourCarte = ss[1];
				    	   next = 1;
				       }
			}
			monAgent.receiver = msg.getSender().getLocalName();
			System.out.println(this.myAgent.getLocalName()+"<----Result received from "+
					    msg.getSender().getLocalName()+" ,content= "+msg.getContent()+", next = "+next);
		}
		System.out.println(monAgent.getLocalName()+"next ="+next);
	}
	public int onEnd() {
		try {
			return next;
		}catch(Exception e) {return 0;}
	}
}