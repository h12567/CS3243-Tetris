import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Breeder {

    private static int NUMBER_NEED_TO_KILL_OFF = (int) (Config.KILLOFF_RATE * Config.POPULATION);

    private Gene[] genesArray;
    private double[][] oldGenes;
    private int[] fitnessScores;

    private double[][] newGenes;

    Breeder(Gene[] genesArray) {
        initialize(genesArray);
        crossOver();
        killOff();
        mutate();
        normalize();
    }

    private void initialize(Gene[] genesArray) {
        this.genesArray = genesArray;
        this.fitnessScores = new int[Config.POPULATION];
        this.oldGenes = new double[Config.POPULATION][Config.NO_OF_FEATURES];
        this.newGenes = new double[Config.POPULATION][Config.NO_OF_FEATURES];

        Arrays.sort(this.genesArray, null);

        for (int i = 0; i < Config.POPULATION; i++) {
            this.oldGenes[i] = this.genesArray[i].dna;
            this.fitnessScores[i] = this.genesArray[i].fitness;
        }
    }

    /**
     * Select randomly a fraction of the Config.POPULATION, get the two best genes, cross over until we have the required number of children
     */
    private void crossOver() {

        for (int i = 0; i < NUMBER_NEED_TO_KILL_OFF; i++) {
            LinkedList<Gene> selectedGenes = new LinkedList<Gene>();

            // Shuffle the old genes set to get the most random results
            LinkedList<Gene> oldGenesSet = new LinkedList<Gene>(Arrays.asList(genesArray));
            Collections.shuffle(oldGenesSet);

            // Get the number of selections required by popping out the old genes set
            while (selectedGenes.size() <= Config.SELECTION_RATE * Config.POPULATION) {
                selectedGenes.add(oldGenesSet.pop());
            }

            // Sort it to get the best two genes
            selectedGenes.sort(null);

            // Cross over, write to newGenes[POPUPATION - 1 - i]
            crossOver(selectedGenes.pop(), selectedGenes.pop(), Config.POPULATION - 1 - i);
        }
    }


    /**
     * Cross two genses. Take the indices from the oldGenes array and write the child to the newGenes array.
     * Cross genes by getting the weighted average of each feature with the weights being the fitness score of each gene.
     *
     * @param k index of child gene
     */
    private void crossOver(Gene first, Gene second, int k) {
        double[] firstGene = first.dna;
        double[] secondGene = second.dna;
        int firstFitnessScore = first.fitness;
        int secondFitnessScore = second.fitness;

        // If the parents are too damn weak, just select one
        if (first.fitness == 0 && second.fitness == 0) {
            newGenes[k] = firstGene;
        } else {
            for (int c = 0; c < Config.NO_OF_FEATURES; c++) {
                newGenes[k][c] =
                        (firstGene[c] * firstFitnessScore + secondGene[c] * secondFitnessScore)
                                / (firstFitnessScore + secondFitnessScore);
                if (Double.isNaN(newGenes[k][c])) {
                    System.out.println("FUCK");
                }
            }
        }
    }


    /**
     * Kill off the worst performed genes
     */
    private void killOff() {
        // Can just do a simple loop because oldGenes is sorted
        for (int i = 0; i < Config.POPULATION - NUMBER_NEED_TO_KILL_OFF; i++) {
            newGenes[i] = oldGenes[i];
        }
    }

    /**
     * Allowing mutation with a given probability. Mutate by adding/substracting randomly an amount in the given range
     */
    private void mutate() {
        Random random = new Random();
        for (int i = 0; i < Config.POPULATION; i++) {
            if (random.nextDouble() < Config.MUTATTION_RATE) {
                for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                    newGenes[i][j] += -Config.MUTATION_AMOUNT + 2 * Config.MUTATION_AMOUNT * random
                            .nextDouble();
                    if (Double.isNaN(newGenes[i][j])) {
                        System.out.println("FUCK");
                    }
                }
            }
        }
    }

    /**
     * Normalize all vectors
     */
    private void normalize() {
        for (int i = 0; i < Config.POPULATION; i++) {
            double temp = 0.0;
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                temp += newGenes[i][j] * newGenes[i][j];
            }
            temp = Math.sqrt(temp);
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                newGenes[i][j] = newGenes[i][j] / temp;
            }
        }
    }

    /**
     * Write logs and genes
     */
    public void writeLah(int generation) throws IOException {
        // Log writer
        FileWriter fileWriter = new FileWriter(
                "log\\log" + String.format("%03d", generation) + ".txt", false);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        // Result writer
        FileWriter resultWriter = new FileWriter("log\\log.txt", true);
        PrintWriter resultPrintWriter = new PrintWriter(resultWriter);

        // Gene writer
        FileWriter geneWriter = new FileWriter("genes.txt", false);
        PrintWriter genePrintWriter = new PrintWriter(geneWriter);

        printWriter.print("Score,Blockage,Height,Bumpiness,ClearedLines\n");

        double average = 0.0;
        for (int i = 0; i < Config.POPULATION; i++) {
            printWriter.print(fitnessScores[i] + "," + oldGenes[i][0] + ","
                    + oldGenes[i][1] + "," + oldGenes[i][2] + "," + oldGenes[i][3] + "\n");
            genePrintWriter.print(newGenes[i][0] + " " + newGenes[i][1] + " " + newGenes[i][2] + " "
                    + newGenes[i][3] + "\n");
            average += fitnessScores[i];
        }

        average /= Config.POPULATION;

        if (generation == 0) {
            resultPrintWriter.print("Generation,Mean,Best,ThirdQuartile,Median,FirstQuartile,Worst\n");
        }

        resultPrintWriter
                .print(generation + "," + average + "," + fitnessScores[0] + "," + fitnessScores[
                        Config.POPULATION / 4] + "," + fitnessScores[Config.POPULATION / 2] + ","
                        + fitnessScores[3 * Config.POPULATION / 4] + "," + fitnessScores[
                        Config.POPULATION - 1] + "\n");

        printWriter.flush();
        printWriter.close();

        resultPrintWriter.flush();
        resultPrintWriter.close();

        genePrintWriter.flush();
        genePrintWriter.close();
    }

    public double[][] getGenes() {
        return newGenes;
    }
}
