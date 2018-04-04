
public class PlayerSkeleton {
	private double[] gene = {0.10337164626348756,0.9419915999495674,0.2658363436957589,-0.13996961788673246,0.1081927576704793};

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		return Logic.getBestMove(s, legalMoves, gene);
	}
	
	public static void main(String[] args) {
		int t = 1000;

		if(args.length == 1) {
			t = Integer.parseInt(args[0]);
		}

		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
