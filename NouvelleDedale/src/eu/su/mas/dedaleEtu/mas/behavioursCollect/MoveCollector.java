package eu.su.mas.dedaleEtu.mas.behavioursCollect;

import java.util.Iterator;
import java.util.List;


import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.CollectorMultiAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;

public class MoveCollector extends OneShotBehaviour{
	private static final long serialVersionUID = 2019355514537795289L;
	private int next;
	private BasicAgent monAgent;
	public MoveCollector(final AbstractDedaleAgent myagent) {
	    this.myAgent = myagent;
	    monAgent = (BasicAgent) myagent;
	}
	//dubut de l'action
    public void action() {
    	next = 0;
    	System.out.println(this.myAgent.getLocalName()+ " execute le comportement Move.");
		if(monAgent.myMap==null)
			monAgent.myMap= new MapRepresentation();
		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
	
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			System.out.println(lobs);

			//Just added here to let you see what the agent is doing, otherwise he will be too quick
			agent_wait(500);

			//1) remove the current node from openlist and add it to closedNodes.
			remove_current_node_from_open_list_and_add_to_closedNodes(myPosition);
			
			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes, and return a directly accessible openNode
			String nextNode = majGrapheOpenNodesClosedNodes(lobs,myPosition);
			
			//2.5)Add Treasures
			majTreasureNodes(lobs);

			System.out.println("Noeud myTreasureNode");
			System.out.println(monAgent.myTreasureNodes);
			//3) while openNodes is not empty, continues.
			if (monAgent.openNodes.isEmpty()){
				System.out.println();
				System.out.println("*****************Exploration successufully done.****************************************************");
	            if (!monAgent.myTreasureNodes.isEmpty()) {
	            	if(monAgent.prochainTresor == null) {
	            		monAgent.prochainTresor = monAgent.myTreasureNodes.keySet().iterator().next();
	            	}
	            	List<Couple<Observation, Integer>>  listobs = monAgent.myTreasureNodes.get(monAgent.prochainTresor);
	            	if(myPosition.equals(monAgent.prochainTresor)) {
	            		//System.out.println("*****************L AGENT RAMASSE****************************************************");
	            		openTreasure(listobs);
	            		((AbstractDedaleAgent)this.myAgent).pick();
	            		majTresor(monAgent.prochainTresor);
	            		monAgent.prochainTresor = null;
	            	}
	            	else {
	            		//System.out.println("\"*****************GO TO TREASURE.****************************************************\"");
		            	nextNode=getCheminTresor(myPosition).get(0);
			            moveToTreasure(myPosition);
		            	monAgent.tache = 1000; // type or
						for (Couple<Observation, Integer> obs : listobs ) {
							if (obs.getLeft().getName().contains("Diamond")){
								monAgent.tache = 2000;
								break;
							}	
						}
	            	}
				}
        		int spaceBackpack = ((AbstractDedaleAgent)this.myAgent).getBackPackFreeSpace();
        		int capacite = ((CollectorMultiAgent)this.myAgent).getBackpackCapacity();
        		if(spaceBackpack==0||(spaceBackpack!=capacite && monAgent.myTreasureNodes.isEmpty())) {
        			next = 9;
        		}
	            
			}else{
				
				
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				moveToDirectlyAccessibleOpenNodeOrChosenNode(nextNode,myPosition,monAgent.openNodes);
				
				
                //si l'agent n'a pas reussi a se deplacer et que le testInterBllocage a reussit alors l'agent est dans un interblocage
				Boolean succes = testMovementSucces(((AbstractDedaleAgent)this.myAgent).getCurrentPosition(),myPosition);
                if (!succes && testInterBlocage(nextNode,myPosition)) next=8;
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
	
    private void agent_wait(int T) {
		try {
			this.myAgent.doWait(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
    
	private void majTreasureNodes(List<Couple<String, List<Couple<Observation, Integer>>>> lobs){
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
		Couple<String, List<Couple<Observation, Integer>>> pos;
		while(iter.hasNext()){
			pos = iter.next();
			ajoutNoeudSiTresor2(pos);
		}
	}
	
    private void moveToDirectlyAccessibleOpenNodeOrChosenNode(String nextNode,String myPosition,List<String> openNodes) {
		if (nextNode==null){
			System.out.println("IF");
			System.out.println("CHEMIN" + monAgent.chemin);
			System.out.println("MY POSITION" + myPosition);
			//no directly accessible openNode
			//chose one, compute the path and take the first step.
			nextNode=getChemin(myPosition).get(0);
			if (!((AbstractDedaleAgent)this.myAgent).moveTo(nextNode))
				monAgent.chemin.clear();
			else 
				monAgent.chemin.remove(nextNode);
		}else {
			System.out.println("ELSE");
			monAgent.chemin.clear();
			((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
		}
    }
    private void moveToTreasure(String myPosition) {
		String nextNode=getCheminTresor(myPosition).get(0);
		if (!((AbstractDedaleAgent)this.myAgent).moveTo(nextNode))
			monAgent.cheminTresor.clear();
		else 
			monAgent.cheminTresor.remove(nextNode);
    }
    
    private Boolean testMovementSucces(String newPosition,String oldPosition) {
    	return newPosition.equals(oldPosition)?false:true;
    }
    
	private Boolean testInterBlocage(String nextNode,String myPosition) {
		Boolean moveSucces = false;
		long start = System.currentTimeMillis();
		while (!moveSucces && System.currentTimeMillis()-start < 500){    //on essaie d'acceder au nextNode pendant 500ms
			if (nextNode != null)
				((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
			moveSucces = testMovementSucces(((AbstractDedaleAgent)this.myAgent).getCurrentPosition(),myPosition);
		}	
    return moveSucces?false:true;
	}
	private List<String> getChemin(String myPosition){
		return monAgent.chemin = monAgent.myMap.getShortestPath(myPosition, monAgent.openNodes.get(0));
	}
	private List<String> getCheminTresor(String myPosition){
		return monAgent.cheminTresor = monAgent.myMap.getShortestPath(myPosition, monAgent.prochainTresor); 
	}
	/*
	private List<String> getChemin(String myPosition){
		if (monAgent.chemin.isEmpty())
			monAgent.chemin = monAgent.myMap.getShortestPath(myPosition, monAgent.openNodes.get(0));
		System.out.println(monAgent.chemin);
		return monAgent.chemin;
	}
	private List<String> getCheminTresor(String myPosition){
		if (monAgent.cheminTresor.isEmpty())
			monAgent.cheminTresor = monAgent.myMap.getShortestPath(myPosition, monAgent.prochainTresor); 
		System.out.println("My position" + myPosition);
		System.out.println("Prochain tresor" + monAgent.prochainTresor);
		System.out.println(monAgent.cheminTresor);
		return monAgent.cheminTresor;
	}
	*/
}