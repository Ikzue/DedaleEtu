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
				System.out.println(this.myAgent.getLocalName()+"*****************Exploration successufully done.****************************************************");
	            if (!monAgent.myTreasureNodes.isEmpty()) {
	            	if(monAgent.prochainTresor == null) {
	            		monAgent.prochainTresor = genererProchainTresor();
	            	}
	            	System.out.println("test1"+" prochainTresor:" + monAgent.prochainTresor);
	            	List<Couple<Observation, Integer>>  listobs = monAgent.myTreasureNodes.get(monAgent.prochainTresor);
	            	System.out.println("val: "+monAgent.myTreasureNodes.get(monAgent.prochainTresor));
	            	if(myPosition.equals(monAgent.prochainTresor)) {
	            		//System.out.println("*****************L AGENT RAMASSE****************************************************");
	            		openTreasure(listobs);
	            		((AbstractDedaleAgent)this.myAgent).pick();
	            		majTresor(monAgent.prochainTresor);
	            	}
	            	else {
	            		//System.out.println("\"*****************GO TO TREASURE.****************************************************\"");
		            	nextNode=getCheminTresor(myPosition).get(0);    
		            	assert(nextNode != null);
			            moveToTreasure(nextNode,myPosition);
		                //si l'agent n'a pas reussi a se deplacer et que le testInterBllocage a reussit alors l'agent est dans un interblocage
						Boolean succes = testMovementSucces(((AbstractDedaleAgent)this.myAgent).getCurrentPosition(),myPosition);
		                if (!succes)
		                	if (testInterBlocage(nextNode,myPosition)) next=8;
	            	}
				}
            	System.out.println("test4");
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
                if (!succes)
                	if (testInterBlocage(nextNode,myPosition)) next=8;
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
				if(obs.getLeft() == monAgent.getMyTreasureType()) {
					monAgent.myTreasureNodes.put(pos.getLeft(), pos.getRight());
					return;
				}  
				// TO DO :  garder aussi les noeudsTresor non accessibles
			}
		}
	private void majTresor(String prochainTresor) {
		Couple<String, List<Couple<Observation, Integer>>> pos = ((AbstractDedaleAgent)this.myAgent).observe().get(0);
		assert prochainTresor.equals(pos.getLeft()): "prochain tresor != pos.get";
		Boolean vide = true;
        for (Couple<Observation,Integer> description: pos.getRight())
        	if (description.getLeft() == ((AbstractDedaleAgent)this.myAgent).getMyTreasureType())
        		vide = false;
        if (vide) {
        	monAgent.myTreasureNodes.remove(prochainTresor);
        	monAgent.prochainTresor = null;
        	System.out.println("vide vide vide vide vide********************************");
        }else {
        	System.out.println("nonn non vide non non vide********************************");
        }
//		monAgent.myTreasureNodes.remove(prochainTresor);
//		ajoutNoeudSiTresor2(pos);
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
    private void moveToTreasure(String nextNode,String myPosition) {
		if (!((AbstractDedaleAgent)this.myAgent).moveTo(nextNode)) {
			monAgent.cheminTresor.clear();
	      //  if (testInterBlocage(nextNode,myPosition)) next=8;
		}
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
		monAgent.chemin = monAgent.myMap.getShortestPath(myPosition, monAgent.openNodes.get(0));
		monAgent.destination = monAgent.openNodes.get(0);
		return monAgent.chemin;
	}
	private List<String> getCheminTresor(String myPosition){
		monAgent.cheminTresor = monAgent.myMap.getShortestPath(myPosition, monAgent.prochainTresor);
		monAgent.destination = monAgent.prochainTresor;
		return monAgent.cheminTresor;
	}
	private String genererProchainTresor() {
		System.out.println("******************************* generer prochain Tresor**************************");
		String prochainTresor = null;
		int max = -1;
		for (String key:monAgent.myTreasureNodes.keySet()) {
			List<Couple<Observation,Integer>> obs = monAgent.myTreasureNodes.get(key);
			for (Couple<Observation,Integer> ob: obs)
				if (ob.getLeft() == monAgent.getMyTreasureType()&& ob.getRight()>max) { //To do:  ajouter les autres conditions
					max = ob.getRight();
					prochainTresor = key;
				}
		}
		return prochainTresor;
	}
}