package eu.su.mas.dedaleEtu.mas.behavioursTanker;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.behaviours.OneShotBehaviour;

public class Retour extends OneShotBehaviour {
	private static final long serialVersionUID = 8567689731496787331L;
    private BasicAgent monAgent;
    private int next = 0;
	public Retour(final AbstractDedaleAgent myagent) {
		super(myagent);
		monAgent = (BasicAgent)myagent;
	}
	public void action() {
		System.out.println(monAgent.getLocalName()+" execute Retour");
		String myPosition = monAgent.getCurrentPosition();
		if (monAgent.tankerPosition.equals(myPosition)) {
			next = 7;
			return;
		}
		String nextNode = monAgent.myMap.getShortestPath(myPosition,monAgent.tankerPosition).get(0);
		monAgent.moveTo(nextNode);
		Boolean succes = testMovementSucces(((AbstractDedaleAgent)this.myAgent).getCurrentPosition(),myPosition);
        if (!succes)
        	if (testInterBlocage(nextNode,myPosition)) {
        		next=8;
        		return;
        	}
        next = 1;
	}

	@Override
	public int onEnd() {
		return next;
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
