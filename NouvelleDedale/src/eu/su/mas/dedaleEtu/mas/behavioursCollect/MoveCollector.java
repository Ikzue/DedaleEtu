package eu.su.mas.dedaleEtu.mas.behavioursCollect;

import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.CollectorMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;

public class MoveCollector extends OneShotBehaviour{
	private static final long serialVersionUID = 2019355514537795289L;
	private int next = 0;
	private CollectorMultiAgent monAgent;
	public MoveCollector(final AbstractDedaleAgent myagent) {
	    this.myAgent = myagent;
	    monAgent = (CollectorMultiAgent) myagent;
	}
	//dubut de l'action
    public void action() {
    	System.out.println(this.myAgent.getLocalName()+ " execute le comportement Move.");
		if(monAgent.myMap==null)
			monAgent.myMap= new MapRepresentation();
		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
	
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			System.out.println(lobs);
			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myAgent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//1) remove the current node from openlist and add it to closedNodes.
			remove_current_node_from_open_list_and_add_to_closedNodes(myPosition);
			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
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
			//2.5)Add Treasures
			iter=lobs.iterator();
			Couple<String, List<Couple<Observation, Integer>>> pos;
			List<Couple<Observation, Integer>> listobs;
			String isItTreasure;
			while(iter.hasNext()){
				pos = iter.next();
				ajoutNoeudSiTresor2(pos);
			}
			System.out.println("Noeuds TreasureNodes:");
			System.out.println(monAgent.treasureNodes);
			System.out.println("Noeuds myTreasureNodes");
			System.out.println(monAgent.myTreasureNodes);
			//3) while openNodes is not empty, continues.
			if (monAgent.openNodes.size()==0){
				System.out.println();
				System.out.println("*****************Exploration successufully done.****************************************************");
	            if (!monAgent.myTreasureNodes.isEmpty()) {
	            	if(monAgent.prochainTresor == null) {
	            		monAgent.prochainTresor = monAgent.myTreasureNodes.keySet().iterator().next();
	            	}
	            	listobs = monAgent.myTreasureNodes.get(monAgent.prochainTresor);
	            	if(myPosition.equals(monAgent.prochainTresor)) {
	            		System.out.println("L'agent ramasse:");
	            		openTreasure(listobs);
	            		((AbstractDedaleAgent)this.myAgent).pick();
	            		majTresor(monAgent.prochainTresor);
	            		monAgent.prochainTresor = null;
	            		nextNode = "2";
	            	}
	            	else {
		            	nextNode=monAgent.myMap.getShortestPath(myPosition, monAgent.prochainTresor).get(0);
		            	monAgent.tache = 1000; // type or
						for (Couple<Observation, Integer> obs : listobs ) {
							if (obs.getLeft().getName().contains("Diamond")){
								monAgent.tache = 2000;
								break;
							}	
						}
	            	}
				}
	            System.out.println("Noeud Tresor " + nextNode);
				((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNode=monAgent.myMap.getShortestPath(myPosition, monAgent.openNodes.get(0)).get(0);
				}else {
				    ((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
				}
				
				String myNewPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
				String myOldPosition = myPosition;
				//si la position n'a pas changer alors on est peut-etre dans interblocage
				if (myNewPosition.equals(myOldPosition)) {   //si apres moveTo la position n'a pas change
					long start = System.currentTimeMillis();
					while (myNewPosition.equals(myOldPosition) && System.currentTimeMillis()-start < 500){    //on essaie d'acceder au nextNode pendant 500ms
						if (nextNode != null)
							((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
						myNewPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
					}	
					if (myNewPosition.equals(myOldPosition)) {       //si on n'arrive toujours pas a changer d'aller a une autre position, alors on suppose que on est dans une situation d'interBlocage
						next = 8;           //resoudre le blocage
				    }
				};

			}
			//endif
		}
	}
    
    private void remove_current_node_from_open_list_and_add_to_closedNodes(String myPosition) {
    	monAgent.closedNodes.add(myPosition);
    	monAgent.openNodes.remove(myPosition);
    	monAgent.myMap.addNode(myPosition,MapAttribute.closed);
    }
	private boolean openTreasure(List<Couple<Observation, Integer>> listobs) {
		for (Couple<Observation, Integer> obs : listobs ) {
			if (obs.getLeft().getName().contains("Diamond")){
				((AbstractDedaleAgent)this.myAgent).openLock(Observation.DIAMOND);
				return true;
			}	
			else if(obs.getLeft().getName().contains("Gold")){
				((AbstractDedaleAgent)this.myAgent).openLock(Observation.GOLD);
				return true;
			}
		}
		return false;
	}
	private void ajoutNoeudSiTresor2(Couple<String, List<Couple<Observation, Integer>>> pos){
		List<Couple<Observation, Integer>> listobs;
		listobs = pos.getRight();
		String type;
			for (Couple<Observation, Integer> obs : listobs ) {
				type = obs.getLeft().getName();
				if(type.contains("Gold")||type.contains("Diamond")) {
					monAgent.myTreasureNodes.put(pos.getLeft(), pos.getRight());
					return;
				}
			}
		}
	private void majTresor(String prochainTresor) {
		Couple<String, List<Couple<Observation, Integer>>> pos = ((AbstractDedaleAgent)this.myAgent).observe().get(0);
		assert prochainTresor.equals(pos.getLeft()): "prochain tresor != pos.get";
		monAgent.myTreasureNodes.remove(prochainTresor);
		ajoutNoeudSiTresor2(pos);
		
	}
    //fin de l'action
	public int onEnd() {
		return next;
	}
}