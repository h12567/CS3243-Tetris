import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Particle {
	Random random;
	double[] currentWeights = new double[Config.NO_OF_FEATURES];
	double[] bestWeights = new double[Config.NO_OF_FEATURES];
	double bestScore;
	double[] v = new double[Config.NO_OF_FEATURES];
	double INERTIA = 0.7;
	double C1 = 2.0;
	double C2 = 2.0;

	public Particle(double[] weights) {
		System.arraycopy(weights, 0, this.currentWeights, 0, weights.length);
		System.arraycopy(weights, 0, this.bestWeights, 0, weights.length);
		bestScore = 0.0;

		random = new Random();
		for(int i = 0; i < v.length; i++) {
			v[i] = random.nextDouble() * 2.0 - 1.0;
		}
	}

	public Callable<Pair> playGame(double[] globalBestWeights) {
		random = new Random();
		double constant_ind = random.nextDouble();
        double constant_global = random.nextDouble();

        for(int i = 0; i < v.length; i++) {
            v[i] = INERTIA * v[i]
                   + constant_ind * C1 * (bestWeights[i] - currentWeights[i])
                   + constant_global * C2 * (globalBestWeights[i] - currentWeights[i]);
        }
        for(int i = 0; i < v.length; i++) {
            currentWeights[i] = v[i] + currentWeights[i];
        }


        return new PsoSimulator();
	}

	private class PsoSimulator implements Callable<Pair> {
		public Pair call() {
			double score = 0.0;
			double[] tempWeights = new double[Config.NO_OF_FEATURES];
			for(int i = 0; i < currentWeights.length; i++ ) {
				tempWeights[i] = (double) currentWeights[i];
			}
			for(int i = 0; i < Config.TESTS_PER_GENERATION; i++) {
				State s = new State();
	        	while (!s.hasLost()) {
	            	if (s.getTurnNumber() == -1
	                	    || (s.getTurnNumber() != -1 && s.getTurnNumber() < Config.TURN_NUMBER_LIMIT)) {

	                	s.makeMove(Logic.getBestMove(s, s.legalMoves(), tempWeights));
		            } else {
		                break;
		            }
	        		score += s.getRowsCleared();
	        	}
	        }

	        score /= Config.TESTS_PER_GENERATION;
	        if(score > bestScore) {
	        	System.arraycopy(currentWeights, 0, bestWeights, 0, currentWeights.length);
	        	bestScore = score;
	        }
	        return new Pair(currentWeights, score);
		}
	}

}