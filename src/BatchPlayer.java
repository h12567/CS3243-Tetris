import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class BatchPlayer {

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

    public static void main(String[] args) throws IOException {
        double[][] genes = readGenesFromFile(Config.POPULATION);

        int[] fitnessScores = new int[Config.POPULATION];

        final int NO_OF_THREADS = Config.POPULATION * Config.TESTS;

        // Create the threads
        Simulator[] myRunnables = new Simulator[NO_OF_THREADS];
        Thread threads[] = new Thread[NO_OF_THREADS];

        // Thread 0 1 2 3 ... TESTS - 1 is gene 0, and so on
        for (int i = 0; i < NO_OF_THREADS; i++) {
            myRunnables[i] = new Simulator(genes[i / Config.TESTS]);
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
                fitnessScores[i / Config.TESTS] += myRunnables[i].getScore();
            }
        } catch (InterruptedException e) {
            System.out.println("FUCK THIS SHIT IM OUT");
        }

        System.out.println("Run done, writing log...");

        Gene[] genesArray = new Gene[Config.POPULATION];

        for (int i = 0; i < Config.POPULATION; i++) {
            genesArray[i] = new Gene(genes[i], fitnessScores[i]);
        }

        Arrays.sort(genesArray, null);

        // Result writer
        FileWriter resultWriter = new FileWriter("batch_result.txt", false);
        PrintWriter resultPrintWriter = new PrintWriter(resultWriter);

        for (int i = 0; i < Config.POPULATION; i++) {
            resultPrintWriter.print(genesArray[i].fitness);
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                resultPrintWriter.print("," + genesArray[i].dna[j]);
            }
            resultPrintWriter.print("\n");
        }

        resultPrintWriter.flush();
        resultPrintWriter.close();

        System.out.println("Writing log done.");
    }

}
