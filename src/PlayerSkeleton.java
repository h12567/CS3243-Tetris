
public class PlayerSkeleton {
	private static double[] gene = {0.011261695180865523, -0.0124337310467185, 0.21228879863276026, -0.43041071100319944, 0.3714456436209095, 1.0813223602111466
	};

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		return Logic.getBestMove(s, legalMoves, gene);
	}
	
	public static void main(String[] args) {
		int t = 1000;

		if(args.length == 0) {
			Simulator sim = new Simulator(gene);
			sim.setLimit(1000000000);
			sim.run();

			System.out.println("You have completed "+sim.getScore()+" rows.");
			return;
		}

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
