
public class PlayerSkeleton {
	private double paramLinesCleared = 100;
	private double paramBlockage = 45;
	private double paramAggregateHeight = 15;
	private double paramBumpiness = 50;

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		return Logic.getBestMove(s, legalMoves, paramBlockage, paramAggregateHeight,
			paramBumpiness, paramLinesCleared);
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
