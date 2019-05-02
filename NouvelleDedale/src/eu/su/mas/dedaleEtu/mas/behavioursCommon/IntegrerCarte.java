package eu.su.mas.dedaleEtu.mas.behavioursCommon;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.BasicAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;

public class IntegrerCarte extends OneShotBehaviour{
	private static final long serialVersionUID = 2019022314407795289L;
	private BasicAgent monAgent;
	/**
	 * 
	 * @param myagent the Agent this behaviour is linked to
	 * @param receiverName The local name of the receiver agent
	 */
	public IntegrerCarte(final AbstractDedaleAgent myagent) {
		super(myagent);
	    monAgent = (BasicAgent) myagent;
	}
	public void action() {
		System.out.println(this.myAgent.getLocalName()+ " execute le comportement IntegrerCarte.");
		int i = 0;
		i++;
		System.out.println("test "+i);
		if (!monAgent.yourCarte.equals("")) {
			//System.out.println(this.myAgent.getLocalName()+" integrer une carte ");
			//System.out.println("Carte avant:");
			//System.out.print("closedNodes :");
			
			i++;
			//System.out.println("test "+i);
			/*
			for (String nc:monAgent.closedNodes) {
				System.out.print(nc);
			}
			System.out.println();
			System.out.print("openNodes :");
			for (String no:monAgent.openNodes) {
				System.out.print(no);
			}
			System.out.println();
			*/
			i++;
			//System.out.println("test "+i);
			
			String[] ss = monAgent.yourCarte.split("\\|");
		    try {
				String[] ncs = ss[0].split("\\*");
	            //traitement de noeds fermes
				for (String nc:ncs) {
					//System.out.print("nc :"+nc);
					if (nc.equals(""))
						continue;
					if (!monAgent.closedNodes.contains(nc)) {
						monAgent.closedNodes.add(nc);
						monAgent.openNodes.remove(nc);
						monAgent.myMap.addNode(nc, MapAttribute.closed);
				    }    
				}
		    }catch(Exception e) {};
				
			try {
				String[] nos = ss[1].split("\\*");
				//traitement de noeuds ouverts
				for (String no:nos) {
					System.out.print("no :"+no);
					if (no.equals(""))
						continue;
					if (monAgent.closedNodes.contains(no))
						continue;
					if (monAgent.openNodes.contains(no))
						continue;				
					monAgent.openNodes.add(no);
					monAgent.myMap.addNode(no, MapAttribute.open);    
				}
			}catch(Exception e) {};
			
			try {
				String[] aretes = ss[2].split("\\*");
				//traitement des aretes
				for (String e:aretes) {
					if (e.equals(""))
						continue;
					String[] n = e.split("-");
					monAgent.myMap.addEdge(n[0], n[1]);
				}
			}catch(Exception e) {}
			
			if (monAgent.tankerPosition == null)
				try {
					monAgent.tankerPosition = ss[3];
				}catch(Exception e) {};
		}
		
	}
}
