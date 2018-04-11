import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Breeder {

    private static int NUMBER_TO_DO_CROSSOVER = (int) (Config.CROSSOVER_AMOUNT * Config.POPULATION);
    private static int NUMBER_TO_DO_MUTATION = (int) (Config.MUTATION_AMOUNT * Config.POPULATION);

    private Gene[] genesArray;
    private double[][] oldGenes;
    private int[] fitnessScores;

    private double[][] newGenes;
    private ArrayList< Gene > newPopulation = new ArrayList< Gene > ();
    Random rand = new Random();

    Breeder(Gene[] genesArray) {
        initialize(genesArray);
        expandByCrossOver();
        expandByMutation();
        reducePopulation();
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
            this.newPopulation.add(this.genesArray[i]);
        }
    }

    /**
     * Select randomly a fraction of the Config.POPULATION, get the two best genes, cross over until we have the required number of children
     */
    private void expandByCrossOver() {

        for (int i = 0; i < NUMBER_TO_DO_CROSSOVER; i++) {

            int firstIndex = rand.nextInt(Config.POPULATION);
            int secondIndex = rand.nextInt(Config.POPULATION);

            if(firstIndex != secondIndex) {
                newPopulation.add(crossOver(genesArray[firstIndex], genesArray[secondIndex]));
            }
        }
    }


    private void expandByMutation() {
        for (int i = 0; i < NUMBER_TO_DO_MUTATION; i++) {
            int index = rand.nextInt(newPopulation.size());
            int mutateLocation = rand.nextInt(Config.NO_OF_FEATURES);

            Collections.shuffle(newPopulation);
            newPopulation.add(mutate(newPopulation.get(index), mutateLocation));
        }
    }

    private void reducePopulation() {
        Collections.sort(newPopulation);
        while(newPopulation.size() > Config.POPULATION) {
            newPopulation.remove(newPopulation.size() - 1);
        }
    }
    /**
     * Cross two genses. Take the indices from the oldGenes array and write the child to the newGenes array.
     * Cross genes by getting the weighted average of each feature with the weights being the fitness score of each gene.
     *
     * @param k index of child gene
     */
    private Gene crossOver(Gene first, Gene second) {
        double[] firstGene = first.dna;
        double[] secondGene = second.dna;
        double[] childGene = new double[Config.NO_OF_FEATURES];
        double next;

        for (int c = 0; c < Config.NO_OF_FEATURES; c++) {
            next = rand.nextDouble();

            if(next <= Config.CROSSOVER_RATE) {
                childGene[c] = firstGene[c];
            } else {
                childGene[c] = secondGene[c];
            }


            if (Double.isNaN(childGene[c])) {
                System.out.println("FUCK");
            }
        }


        Simulator simulator = new Simulator(childGene);
        simulator.run();
        return new Gene(childGene, simulator.getScore());
    
    }



    private boolean flipCoin() {
        return rand.nextInt(2) == 0;
    }


    /**
     * Allowing mutation with a given probability. Mutate by adding/substracting randomly an amount in the given range
     */
    private Gene mutate(Gene gene, int mutateLocation) {
        double[] parentGene = gene.dna;
        double[] childGene = new double[Config.NO_OF_FEATURES];
        System.arraycopy( parentGene, 0, childGene, 0, parentGene.length );

        double next = rand.nextDouble();
        if(next < Config.MUTATION_RATE) {
            if(flipCoin()) {
                childGene[mutateLocation] += rand.nextDouble() * 2;
            } else {
                childGene[mutateLocation] -= rand.nextDouble() * 2;
            }
        }

        Simulator simulator = new Simulator(childGene);
        simulator.run();
        return new Gene(childGene, simulator.getScore());
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

        printWriter.print("Score,Blockage,Height,Bumpiness,ClearedLines,Tower,Hole\n");

        double average = 0.0;
        for (int i = 0; i < Config.POPULATION; i++) {
            printWriter.print(fitnessScores[i]);
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                printWriter.print("," + oldGenes[i][j]);
                genePrintWriter.print(newGenes[i][j] + " ");
            }
            printWriter.print("\n");
            genePrintWriter.print("\n");
            average += fitnessScores[i];
        }

        average /= Config.POPULATION;

        if (generation == 0) {
            resultPrintWriter.print("Generation,Mean,Best,ThirdQuartile,Median,FirstQuartile,Worst\n");
        }

        System.out.println("AVG SCORE " + average);
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
        Gene currentGene;
        for(int i = 0; i < Config.POPULATION; i++) {
            currentGene = newPopulation.get(i);
            newGenes[i] = currentGene.dna;
            fitnessScores[i] = currentGene.fitness;
        }
        return newGenes;
    }
}
