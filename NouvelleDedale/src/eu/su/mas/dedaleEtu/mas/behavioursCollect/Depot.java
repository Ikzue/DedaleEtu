package eu.su.mas.dedaleEtu.mas.behavioursCollect;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.behaviours.OneShotBehaviour;

public class Depot extends OneShotBehaviour{
	private static final long serialVersionUID = 85676897314967861L;
	private BasicAgent monAgent;
	private boolean bloquage;
	
	public Depot(final AbstractDedaleAgent myagent) {
	    this.myAgent = myagent;
	    monAgent = (BasicAgent) myagent;
	    bloquage = false;
	}
    public void action() {
    	System.out.println(this.myAgent.getLocalName()+ " execute le comportement Depot.");
    	monAgent.chemin.clear();
    	monAgent.cheminTresor.clear();
    	moveAndGive();
    	monAgent.chemin.clear();
    }
    
    private void moveAndGive() {
    	while(!bloquage) {
    		agent_wait(500);
	    	String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
			String nextNode=getCheminDepot(myPosition).get(0);
			if(nextNode.equals(monAgent.tankerPosition)) {
				((AbstractDedaleAgent)this.myAgent).emptyMyBackPack("Tanker1");
				break;
			}
			else {
				if (!((AbstractDedaleAgent)this.myAgent).moveTo(nextNode))
					monAgent.chemin.clear();
				else 
					monAgent.chemin.remove(nextNode);
			}
    	}
    }
    
	
    private List<String> getCheminDepot(String myPosition){
		return monAgent.myMap.getShortestPath(myPosition, monAgent.tankerPosition);
	}
//	     TO  Do
//    private List<String> getCheminDepot(String myPosition){
//		if (monAgent.chemin.isEmpty()) {
//			monAgent.chemin = monAgent.myMap.getShortestPath(myPosition, monAgent.tankerPosition); 
//		}
//		return monAgent.chemin;
//	}
    private void agent_wait(int T) {
		try {
			this.myAgent.doWait(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
