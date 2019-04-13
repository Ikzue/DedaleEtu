package eu.su.mas.dedaleEtu.mas.behavioursCommon;
import eu.su.mas.dedale.env.Observation;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class InterBlocagePrioriteEtAlea extends OneShotBehaviour{
	private static final long serialVersionUID = 8567677999496787661L;
	private BasicAgent monagent;
	public InterBlocagePrioriteEtAlea(final AbstractDedaleAgent myagent) {
		super(myagent);
	    monagent = (BasicAgent)myagent;;
	}
	public void action() {
		
		System.out.println("*****************Resolution Interblocage Priorite "+this.myAgent.getLocalName()+"****************************************************");
		//create message
		String[] receivers = {"Collect1","Collect2","Tanker1","Explo1","Explo2"}; //list of localname
		AID sender = this.myAgent.getAID();
        String content = createContent(monagent);
	    ACLMessage msg = createMessage(receivers,sender,content);   
	    //send message
	    ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
	    // attendre T millis seconds message et retenir le nombre maxmum
	    int T = 100;
	    int nb = Integer.valueOf(content);
	    int max = getMaximum(T,nb,receivers);
	    // 5) si mon nombre n'est pas le max, alors je changer ma position aleatoirement
	    if (max>nb)
	    	moveAleatoire();
	    else {
	    	essayerAccederDestination();
	    };
	}
	private void essayerAccederDestination() {
    	long s = System.currentTimeMillis();
		while (!((AbstractDedaleAgent)this.myAgent).getCurrentPosition().equals(monagent.destination) && System.currentTimeMillis()-s <100){
			try{((AbstractDedaleAgent)this.myAgent).moveTo(monagent.destination);}catch(Exception e){break;}   // TO Do: trouver l'erreur 
		}
	}
	private void moveAleatoire() {
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
	}
	private int getMaximum(int T,int monNbAlea,String[] receivers) {
		// 2) attendre 100ms et retenu le max resu
		int tempAttente = T;
		long start = System.currentTimeMillis(); 
		int max = monNbAlea;
		int tonNbAlea;
		String[] content;
        do{
        	ACLMessage msg = this.myAgent.receive();
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
        return max;  
	}
	private String createContent(BasicAgent monagent) {
		Observation myTreasureType = ((AbstractDedaleAgent)this.myAgent).getMyTreasureType();
		Random randInt = new Random();
		int n = randInt.nextInt() % 1000;  // nb alea entre 0 et 999
		int priorite = 0;
		if (this.myAgent.getLocalName().contains("ollect"))            // collect sont prioritaire p/p explorateur  et Diamon > gold
			priorite += (myTreasureType == Observation.GOLD)?10000:20000;
		String content = Integer.toString(priorite+n);
		return content;
	}
	private ACLMessage createMessage(String[] receivers,AID id,String content) {
		// 1.1) Create the message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		for (String receiver: receivers) {
			if (!receiver.equals(this.getAgent().getLocalName())) 
				msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
		}
		msg.setContent(content);
		return msg;
	}
}
