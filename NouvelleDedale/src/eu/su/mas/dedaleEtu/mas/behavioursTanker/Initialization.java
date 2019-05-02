package eu.su.mas.dedaleEtu.mas.behavioursTanker;

import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
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
		String myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		if(monAgent.myMap==null)
			monAgent.myMap= new MapRepresentation();
		if(myPosition != null) {
			monAgent.tankerPosition=myPosition;
			remove_current_node_from_open_list_and_add_to_closedNodes(myPosition);
			majGrapheOpenNodesClosedNodes(((AbstractDedaleAgent)this.myAgent).observe(),myPosition);
		}
	}
	
    private void remove_current_node_from_open_list_and_add_to_closedNodes(String myPosition) {
    	monAgent.closedNodes.add(myPosition);
    	monAgent.openNodes.remove(myPosition);
    	monAgent.myMap.addNode(myPosition,MapAttribute.closed);
    }
    private String majGrapheOpenNodesClosedNodes(List<Couple<String, List<Couple<Observation, Integer>>>> lobs, String myPosition) {
		String nextNode=null;
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();		
		while(iter.hasNext()){
			String nodeId=iter.next().getLeft();
			if (!monAgent.closedNodes.contains(nodeId)){
				if (!monAgent.openNodes.contains(nodeId)){
					monAgent.openNodes.add(nodeId);
					monAgent.myMap.addNode(nodeId, MapAttribute.open);
					monAgent.myMap.addEdge(myPosition, nodeId);	
				}else{
					//the node exist, but not necessarily the edge
					monAgent.myMap.addEdge(myPosition, nodeId);
				}
			    if (nextNode==null) nextNode=nodeId;
			}
		}
		return nextNode;
    }
}
