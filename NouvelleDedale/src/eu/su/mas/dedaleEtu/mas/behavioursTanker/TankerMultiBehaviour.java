package eu.su.mas.dedaleEtu.mas.behavioursTanker;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.ProtocoleInterBlocage;
import jade.core.behaviours.FSMBehaviour;
import eu.su.mas.dedaleEtu.mas.behavioursTanker.TankerRecevoirMessage;
import eu.su.mas.dedaleEtu.mas.behavioursTanker.TankerEnvoieCarte;
import eu.su.mas.dedaleEtu.mas.behavioursCommon.IntegrerCarte;

/**
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.</br>
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs.</br> 
 * This (non optimal) behaviour is done until all nodes are explored. </br> 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.</br> 
 * Warning, this behaviour is a solo exploration and does not take into account the presence of other agents (or well) and indefinitely tries to reach its target node
 * @author hc
 *
 */
public class TankerMultiBehaviour extends FSMBehaviour {
	private static final long serialVersionUID = 8567689731496787661L;
	/**
	 * Current knowledge of the agent regarding the environment
	 */
	public TankerMultiBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
		//states
		this.registerFirstState(new Initialization(myagent),"init");
		this.registerState(new TankerRecevoirMessage(myagent),"recevoirMessage");
		this.registerState(new TankerEnvoieCarte(myagent),"repondre");
		this.registerState(new IntegrerCarte(myagent), "integrerCarte");
		this.registerState(new ProtocoleInterBlocage(myagent), "interBlocage");
		this.registerState(new Retour(myagent), "retour");
		this.registerState(new IntegrerCarte(myagent), "integrerCarte");				
		//transitions
		this.registerDefaultTransition("init","recevoirMessage"); 
		this.registerDefaultTransition("recevoirMessage","recevoirMessage");
		this.registerTransition("recevoirMessage","repondre",4);
		this.registerTransition("recevoirMessage","interBlocage",5);
		this.registerTransition("recevoirMessage","integrerCarte",1);
		this.registerDefaultTransition("interBlocage","retour");
		this.registerDefaultTransition("retour","retour");
		this.registerTransition("retour","recevoirMessage",7);
		this.registerTransition("retour","interBlocage",8);
		this.registerDefaultTransition("integrerCarte","recevoirMessage");
		this.registerDefaultTransition("repondre","recevoirMessage");
	}

	@Override
	public int onEnd(){
		System.out.println("FSM behaviour terminé");
		myAgent.doDelete();
		return super.onEnd();
	}
    //fin du comportement TankerMessageBehaviour    
}