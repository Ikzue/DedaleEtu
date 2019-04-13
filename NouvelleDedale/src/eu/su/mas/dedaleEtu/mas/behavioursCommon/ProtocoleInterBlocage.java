package eu.su.mas.dedaleEtu.mas.behavioursCommon;
import java.lang.Math;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

//protocoleInterBlocage
public class ProtocoleInterBlocage extends OneShotBehaviour {

	private static final long serialVersionUID = 8567677777496787661L;
	private BasicAgent monAgent;
	public ProtocoleInterBlocage(final AbstractDedaleAgent myagent) {
		super(myagent);
	    monAgent = (BasicAgent)myagent;;
	}

	@Override
	public void action() {
		System.out.println();
		System.out.println("*****************Resolution Interblocage "+this.myAgent.getLocalName()+"****************************************************");
		System.out.println();
			
		// 1) envoie de message: "InterBlocage"+n where n est un nombre aleatoire
		String[] receivers = {"Collect1","Collect2","Tanker1","Explo1","Explo2"}; //list of localname
		// 1.1) Create the message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))     
				continue;                   //pas de message pour lui meme
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
		}
		// 1.2) set message content	
		Random n = new Random();
		int monNbAlea = n.nextInt(999);
		monNbAlea = Math.abs(monNbAlea);
		//ajouter la priorite de l'agent
		if (this.myAgent.getLocalName().contains("ollect"))            // collect sont prioritaire p/p explorateur  et Diamon > gold
			monNbAlea += (monAgent.getMyTreasureType() == Observation.GOLD)?10000:20000;
		msg.setContent("InterBlocage:"+monNbAlea);
		// 1.3) envoyer
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))   //afficher tous les messages envoyes
				continue;                   
			System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}

		// 2) attendre 100ms et retenu le max resu
		int tempAttente = 100;
		long start = System.currentTimeMillis(); 
		int max = monNbAlea;
		int tonNbAlea;
		String[] content;
        do{
        	msg = this.myAgent.receive();
        	if (msg == null) {
        		block(50);
        		continue;
        	}
        	
			System.out.println(this.myAgent.getLocalName()+"<----Result received from "+
				    msg.getSender().getLocalName()+" ,content= "+msg.getContent());
        	       	
        	if (!msg.getContent().contains("InterBlocage"))
        		continue;
        	
        	content = msg.getContent().split("\\:");
        	
        	if (content.length == 1)
        		continue;
        	try {
        		tonNbAlea = Integer.parseInt(content[1]);
        		if (tonNbAlea>max) {
        			max = tonNbAlea;
        			// 3.1) set message content				
        			msg.setContent("InterBlocage:"+max);
        			// 3.2) envoyer
        			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
        			for (String receiver: receivers) {
        				if (receiver.equals(this.getAgent().getLocalName()) || receiver.equals(msg.getSender().getLocalName()))   //afficher tous les messages envoyes
        					continue;                   
        				System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
        			}
        		}
        	}catch(Exception e) {};
        	
        }while(System.currentTimeMillis()- start < tempAttente);
		// 5) si mon nombre n'est pas le max, alors je changer ma position aleatoirement
        if (max > monNbAlea) {
			System.out.println(this.myAgent.getLocalName()+" perd");
			String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
			String myNewPosition;
			if (myPosition!=null){
				//1) List of observable from the agent's current position
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				//2) choisir par hazard une position autour de la posiiton courante
				Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
				while (iter.hasNext()) {
					((AbstractDedaleAgent)this.myAgent).moveTo(iter.next().getLeft());
					myNewPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
					if (!myPosition.equals(myNewPosition)){
						System.out.println(this.myAgent.getLocalName()+ " move from "+ myPosition + " to "+myNewPosition);				
						break;
					}
				}
			}
			this.myAgent.doWait(100); // laisser passer
        }else {
        	long s = System.currentTimeMillis();
    		while (!((AbstractDedaleAgent)this.myAgent).getCurrentPosition().equals(monAgent.destination) && System.currentTimeMillis()-s <100){
    			try{((AbstractDedaleAgent)this.myAgent).moveTo(monAgent.destination);}catch(Exception e){break;}   // TO Do: trouver l'erreur 
    		}
        }
	}
}
//endProtocoleInterBlocage




//  avant 
/*    
package eu.su.mas.dedaleEtu.mas.behavioursCommon;


import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

//protocoleInterBlocage
public class ProtocoleInterBlocage extends OneShotBehaviour {

	private static final long serialVersionUID = 8567677777496787661L;
    
	private int next;
	
	public ProtocoleInterBlocage(final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		System.out.println();
		System.out.println("*****************Resolution Interblocage "+this.myAgent.getLocalName()+"****************************************************");
		System.out.println();
			
		// 1) envoie de message: "InterBlocage"+n where n est un nombre aleatoire
		String[] receivers = {"Collect1","Collect2","Tanker1"}; //list of localname
		// 1.1) Create the message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))     
				continue;                   //pas de message pour lui meme
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
		}
		// 1.2) set message content	
		Random n = new Random();
		int monNbAlea = n.nextInt(200);
		msg.setContent("InterBlocage:"+monNbAlea);
		// 1.3) envoyer
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))   //afficher tous les messages envoyes
				continue;                   
			System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}

		// 2) attendre 500ms et retenu le max resu
		int tempAttente = 500;
		long start = System.currentTimeMillis(); 
		int max = monNbAlea;
		int tonNbAlea;
		String[] content;
        do{
        	msg = this.myAgent.receive();
        	if (msg == null) {
        		block(50);
        		continue;
        	}
        	
			System.out.println(this.myAgent.getLocalName()+"<----Result received from "+
				    msg.getSender().getLocalName()+" ,content= "+msg.getContent());
        	       	
        	if (!msg.getContent().contains("InterBlocage"))
        		continue;
        	
        	content = msg.getContent().split("\\:");
        	
        	if (content.length == 1)
        		continue;
        	try {
        		tonNbAlea = Integer.parseInt(content[1]);
        		if (tonNbAlea>max) {
        			max = tonNbAlea;
        			// 3.1) set message content				
        			msg.setContent("InterBlocage:"+max);
        			// 3.2) envoyer
        			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
        			for (String receiver: receivers) {
        				if (receiver.equals(this.getAgent().getLocalName()) || receiver.equals(msg.getSender().getLocalName()))   //afficher tous les messages envoyes
        					continue;                   
        				System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
        			}
        		}
        	}catch(Exception e) {};
        	
        }while(System.currentTimeMillis()- start < tempAttente);
		next = 0;
		// 5) si mon nombre n'est pas le max, alors je changer ma position aleatoirement
        if (max > monNbAlea) {
			System.out.println(this.myAgent.getLocalName()+" perd");
			String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
			String myNewPosition;
			if (myPosition!=null){
				//1) List of observable from the agent's current position
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				//2) choisir par hazard une position autour de la posiiton courante
				Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
				while (iter.hasNext()) {
					((AbstractDedaleAgent)this.myAgent).moveTo(iter.next().getLeft());
					myNewPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
					if (!myPosition.equals(myNewPosition)){
						System.out.println(this.myAgent.getLocalName()+ " move from "+ myPosition + " to "+myNewPosition);				
						break;
					}
				}
			}
			block(1000); // laisser passer
        }else {
			System.out.println(this.myAgent.getLocalName()+"gagne");
			next = 1;	
        }
	}
    public int onEnd() {
    	System.out.println("protocole end ," +this.myAgent.getLocalName()+" return "+next);
    	return next;
    }
}
//endProtocoleInterBlocage 
*/