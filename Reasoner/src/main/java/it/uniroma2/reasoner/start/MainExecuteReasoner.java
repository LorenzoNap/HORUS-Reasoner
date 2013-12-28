package it.uniroma2.reasoner.start;



public class MainExecuteReasoner {

	public static void main(String[] args) {
		
		StartReasonerFacade startReasoner = new StartReasonerFacade();
		
		
		new ReasonerGui(startReasoner);
			

	}

}
