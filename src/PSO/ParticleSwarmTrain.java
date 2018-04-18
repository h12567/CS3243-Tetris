import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ParticleSwarmTrain {
	public static Particle[] swarm = new Particle[Config.POPULATION]; 
	public static double[] globalBest;
	public static double globalBestScore;
	public static double[][] genes;

	public static void initializeSwarm() {
		try {
	     	genes = new double[Config.POPULATION][Config.NO_OF_FEATURES];
			// If is first generation, randomly generate genes
			if (Config.NEW_TRAINING_SESSION) {
				Random rand = new Random();
				for (int i = 0; i < Config.POPULATION; i++) {
					for (int j = 0; j < Config.NO_OF_FEATURES; j++) {																																						
						genes[i][j] = Math.abs(rand.nextDouble()) * 10 * Config.FEATURE_TYPE[j];
					}
					swarm[i] = new Particle(genes[i]);
				}
			} else {
				// If not first generation, get data from "genes.txt"
				Scanner scan;
				File file = new File("swarm.txt");
				scan = new Scanner(file);
				for (int i = 0; i < Config.POPULATION; i++) {
					for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
						genes[i][j] = scan.nextDouble();
					}
					swarm[i] = new Particle(genes[i]);
				}
				scan.close();
			}

			globalBest = genes[0];
			globalBestScore = 0.0;

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void run() {
		ArrayList<Future<Pair>> futureScores = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(100);

        // Run particle simulations concurrently
        for(int i = 0; i < Config.POPULATION; i++) {
            Particle particle = swarm[i];
            Future<Pair> futureScore = executor.submit(particle.playGame(globalBest));
            futureScores.add(futureScore);
        }

        // Get scores and update global bests
        for(int i = 0; i < Config.POPULATION; i++) {
            try {
                Future<Pair> future = futureScores.get(i);
                Pair result = future.get();
                if(result.second > globalBestScore) {
                    globalBestScore = result.second;
                    System.arraycopy(result.first, 0, globalBest, 0, result.first.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	public static void write() throws IOException{
		FileWriter swarmWriter = new FileWriter("swarm.txt", true);
        PrintWriter swarmPrintWriter = new PrintWriter(swarmWriter);

        for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
        	System.out.println(globalBest[j]);
            swarmPrintWriter.print(globalBest[j] + " ");
        }
        swarmPrintWriter.print("\n");

        System.out.println("THE BEST SCORE SO FAR: " + globalBestScore);

        swarmPrintWriter.flush();
        swarmPrintWriter.close();
	}

	public static void main(String[] args) {
		try {
			initializeSwarm();
			for(int i = 0; i < Config.NUMBER_OF_GENERATIONS; i++) {
				run();
				write();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}