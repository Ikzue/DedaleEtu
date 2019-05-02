package eu.su.mas.dedaleEtu.mas.behavioursTanker;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public class Initialization extends OneShotBehaviour {
	private BasicAgent monAgent;
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param receiverName The local name of the receiver agent
	 */
	public Initialization(final AbstractDedaleAgent myagent) {
		super(myagent);
		monAgent = (BasicAgent) myagent;
	}
	@Override
	public void action() {
		if(monAgent.myMap==null)
			monAgent.myMap= new MapRepresentation();
		monAgent.tankerPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		remove_current_node_from_open_list_and_add_to_closedNodes(monAgent.tankerPosition);
	}
	
    private void remove_current_node_from_open_list_and_add_to_closedNodes(String myPosition) {
    	monAgent.closedNodes.add(myPosition);
    	monAgent.openNodes.remove(myPosition);
    	monAgent.myMap.addNode(myPosition,MapAttribute.closed);
    }
}
