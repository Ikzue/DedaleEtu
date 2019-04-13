package eu.su.mas.dedaleEtu.mas.behavioursExplore;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;

public class MoveExplorer extends OneShotBehaviour{
	private static final long serialVersionUID = 2019355514537795289L;
	private int next = 0;
	private BasicAgent monAgent;
	public MoveExplorer(final AbstractDedaleAgent myagent) {
	    this.myAgent = myagent;
	    monAgent = (BasicAgent)myagent;;
	}
	//dubut de l'action
    public void action() {
    	System.out.println(this.myAgent.getLocalName()+ " execute le comportement Move.");
    	System.out.println(this.myAgent.getLocalName()+ " openNodes"+ monAgent.openNodes);
    	System.out.println(this.myAgent.getLocalName()+ " closedNodes"+ monAgent.closedNodes);
    	
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
			
            //remove current node from open list and add to closedNodes
			remove_current_node_from_open_list_and_add_to_closedNodes(myPosition);
			
			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes, and return a directly accessible openNode
			String nextNode = majGrapheOpenNodesClosedNodes(lobs,myPosition);

			//3) while openNodes is not empty 
			if (monAgent.openNodes.size()==0){
				//test
				System.out.println();System.out.println(monAgent.getLocalName()+"*********Exploration successufully done.****************************************************");					System.out.println();
				//choisir une position directement accessible, sauf la position actuelle
				choixPosDirectAccessibleAlea(lobs,myPosition);
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				moveToDirectlyAccessibleOpenNodeOrChosenNode(nextNode,myPosition,monAgent.openNodes);
				
                //si l'agent n'a pas reussi a se deplacer et que le testInterBllocage a reussit alors l'agent est dans un interblocage
				Boolean succes = testMovementSucces(((AbstractDedaleAgent)this.myAgent).getCurrentPosition(),myPosition);
                if (!succes) { 
                	if (testInterBlocage(nextNode,myPosition)) {
                		monAgent.destination = nextNode;
                		next = 8;
                /*		if (monAgent.nbProtocolePriorite<3) {
                			next = 8; // essayer protocole avec protocole
                		    monAgent.nbProtocolePriorite += 1;
                		}*/
                	}else {
            			remove_current_node_from_open_list_and_add_to_closedNodes(myPosition);
            			monAgent.nbProtocolePriorite = 0;
                	}
                }else {
                	/*
                	 * sans cette phrase , dans le cas ou le graphe est une chaine, il se peut que les agents ne saivent pas qu'ils ont fini l'exploration
                	 */
                    //remove current node from open list and add to closedNodes
        			remove_current_node_from_open_list_and_add_to_closedNodes(myPosition);    
//      			monAgent.nbProtocolePriorite = 0;
                }
                
			}
			//endif
		}
	}
    //fin de l'action
	public int onEnd() {
		return next;
	}
    private void remove_current_node_from_open_list_and_add_to_closedNodes(String myPosition) {
    	monAgent.closedNodes.add(myPosition);
    	monAgent.openNodes.remove(myPosition);
    	monAgent.myMap.addNode(myPosition,MapAttribute.closed);
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
    
    private String choixPosDirectAccessibleAlea(List<Couple<String,List<Couple<Observation,Integer>>>> lobs,String myPosition){
    	Random r = new Random();
    	int nbAlea = r.nextInt(lobs.size()-1)+1; // +1 car on ne veux pas que la position soit la position actuelle
    	String nextNode = lobs.get(nbAlea).getLeft();
    	return nextNode;
    }
    
    private void moveToDirectlyAccessibleOpenNodeOrChosenNode(String nextNode,String myPosition,List<String> openNodes) {
		if (nextNode==null){
			//no directly accessible openNode
			//chose one, compute the path and take the first step.
			nextNode=monAgent.myMap.getShortestPath(myPosition, openNodes.get(0)).get(0);
		}
		((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
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
}