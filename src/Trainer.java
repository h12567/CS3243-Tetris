import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Trainer {

    private static double[][] readGenesFromFile(int population)
            throws FileNotFoundException {

        double[][] genes = new double[population][Config.NO_OF_FEATURES];

        Scanner scan;
        File file = new File("genes.txt");
        scan = new Scanner(file);
        for (int i = 0; i < population; i++) {
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                genes[i][j] = scan.nextDouble();
            }
        }
        scan.close();
        return genes;
    }

    public static void main(String[] args) throws Exception {
        Gene[] genesArray = new Gene[Config.POPULATION];
        NoisyCrossEntropy nce = new NoisyCrossEntropy(genesArray, 0);
        double[][] genes;
        if (Config.TRAIN_FROM_FILE) {
            genes = readGenesFromFile(Config.POPULATION);
        } else {
            genes = nce.getGenes();
            nce.writeLah();
        }

        final int NO_OF_THREADS = 10;

        // Create the threads
        Simulator[] myRunnables = new Simulator[NO_OF_THREADS];
        Thread threads[] = new Thread[NO_OF_THREADS];

        for (int g = Config.STARTING_GENERATION; g <= Config.GENERATIONS; g++) {
			int[] fitnessScores = new int[Config.POPULATION];
            for (int z = 0; z < Config.POPULATION * Config.TESTS / NO_OF_THREADS; z++) {
                // Thread 0 1 2 3 ... TESTS - 1 is gene 0, and so on
                for (int i = 0; i < NO_OF_THREADS; i++) {
                    myRunnables[i] = new Simulator(genes[(z * NO_OF_THREADS + i) / Config.TESTS]);
                    threads[i] = new Thread(myRunnables[i]);
                    threads[i].start();
                }

                try {
                    // Wair for all threads to finish
                    for (int i = 0; i < NO_OF_THREADS; i++) {
                        threads[i].join();
                    }

                    // Update the fitness array
                    for (int i = 0; i < NO_OF_THREADS; i++) {
                        fitnessScores[(z * NO_OF_THREADS + i) / Config.TESTS] += myRunnables[i]
                                .getScore();
                    }
                } catch (InterruptedException e) {
                    System.out.println("FUCK THIS SHIT IM OUT");
                }
            }
            for (int i = 0; i < Config.POPULATION; i++) {
                genesArray[i] = new Gene(genes[i], fitnessScores[i]);
            }
            nce = new NoisyCrossEntropy(genesArray, g);
            genes = nce.getGenes();
            nce.writeLah();
            System.out.println("Done generation " + g);
        }
    }

}
