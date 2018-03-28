import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class PokemonTrainer {

    private static double[][] generateGenes(boolean firstGen, int population)
            throws FileNotFoundException {

        double[][] genes = new double[population][Config.NO_OF_FEATURES];

        // If is first generation, randomly generate genes
        if (firstGen) {
            Random random = new Random();
            for (int i = 0; i < population; i++) {
                // Generate a random vector with elements in the range [-1;1]
                for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                    genes[i][j] = -1 + 2 * random.nextDouble();
                }
                // Normalize the vector
                double temp = 0.0;
                for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                    temp += genes[i][j] * genes[i][j];
                }
                for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                    genes[i][j] /= temp;
                }
            }
        } else {
            // If not first generation, get data from "genes.txt"
            Scanner scan;
            File file = new File("genes.txt");
            scan = new Scanner(file);
            for (int i = 0; i < population; i++) {
                for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                    genes[i][j] = scan.nextDouble();
                }
            }
            scan.close();
        }
        return genes;
    }

    public static void main(String[] args) throws IOException {
        // Generate genes
        double[][] genes = generateGenes(Config.NEW_TRAINING_SESSION, Config.POPULATION);
        int[] fitnessScores = new int[Config.POPULATION];

        // For each generation
        for (int g = Config.STARTING_GENERATION; g < Config.NUMBER_OF_GENERATIONS; g++) {

            /*
            We are running a lot of threads concurrently, so the number of iterations we run
            all the threads together is the population divided by the number of threads.
             */
            for (int z = 0; z < Config.POPULATION / Config.NO_OF_THREADS; z++) {

                // Create the threads
//                PlayerSkeleton[] myRunnables = new PlayerSkeleton[Config.NO_OF_THREADS];
                Simulator[] myRunnables = new Simulator[Config.NO_OF_THREADS];
                Thread threads[] = new Thread[Config.NO_OF_THREADS];

                // For each thread, create a simulator then run the simulator
                // Can change to PlayerSkeleton for visualization
                // Remember to set the number low or disable JFrame visibility unless you want to kill your pc
                for (int i = 0; i < Config.NO_OF_THREADS; i++) {
//                    myRunnables[i] = new PlayerSkeleton(genes[Config.NO_OF_THREADS * z + i]);
                    myRunnables[i] = new Simulator(genes[Config.NO_OF_THREADS * z + i]);
                    threads[i] = new Thread(myRunnables[i]);
                    threads[i].start();
                }

                try {
                    // Wair for all threads to finish
                    for (int i = 0; i < Config.NO_OF_THREADS; i++) {
                        threads[i].join();
                    }

                    // Update the fitness array
                    for (int i = 0; i < Config.NO_OF_THREADS; i++) {
                        fitnessScores[Config.NO_OF_THREADS * z + i] = myRunnables[i].getScore();
                    }
                } catch (InterruptedException e) {
                    System.out.println("WTF");
                }
            }

            Gene[] genesArray = new Gene[Config.POPULATION];

            for (int i = 0; i < Config.POPULATION; i++) {
                genesArray[i] = new Gene(genes[i], fitnessScores[i]);
            }

            // After we finish all tests, it's time for the breeder
            Breeder breeder = new Breeder(genesArray);
            genes = breeder.getGenes();

            // Write log and new genes
            breeder.writeLah(g);
        }
    }
}
