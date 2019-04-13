package eu.su.mas.dedaleEtu.mas.behavioursCommon;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RecevoirMessage extends OneShotBehaviour{
	private static final long serialVersionUID = 2019244314537795289L;
	private int next;
	private BasicAgent monAgent;
	/**
	 * 
	 * This behaviour is a one Shot.
	 * It receives a message tagged with an inform performative, destroy itlself
	 * @param myagent
	 */
	public RecevoirMessage(final AbstractDedaleAgent myagent) {
		super(myagent);
		monAgent = (BasicAgent)myagent;
	}
	public void action() {
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
