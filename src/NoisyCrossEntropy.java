import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import org.apache.commons.math3.distribution.NormalDistribution;

public class NoisyCrossEntropy {

    private Gene[] genesArray;
    private int[] fitness;
    private double[][] dnas;
    private int generation;

    private double[] means;
    private double[] deviations;

    private double[][] newGenes;

    private double SELECTION = Config.SELECTION_RATE * (double) Config.POPULATION;

    /**
     * Constructor. Takes in Gene[], return new genes
     */
    NoisyCrossEntropy(Gene[] genesArray, int generation) throws Exception {
        if (generation > 0) {
            Arrays.sort(genesArray, null);
        }
        initialize(genesArray, generation);
        calculateMeans();
        calculateDeviations();
        generateGenes();
        normalize();
    }

    private double getNoise() {
        return Math.exp(- (double) generation / (10.0 * Math.PI));
    }

    private void initialize(Gene[] genesArray, int generation) {
        this.generation = generation;
        this.genesArray = genesArray;
        this.fitness = new int[Config.POPULATION];
        this.dnas = new double[Config.POPULATION][Config.NO_OF_FEATURES];
        if (generation > 0) {
            for (int i = 0; i < Config.POPULATION; i++) {
                fitness[i] = genesArray[i].fitness;
                dnas[i] = genesArray[i].dna;
            }
        }
        this.means = new double[Config.NO_OF_FEATURES];
        this.deviations = new double[Config.NO_OF_FEATURES];
        this.newGenes = new double[Config.POPULATION][Config.NO_OF_FEATURES];
    }

    private void calculateMeans() {
        for (int i = 0; i < SELECTION; i++) {
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                means[j] += dnas[i][j];
            }
        }
        for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
            means[j] /= SELECTION;
        }
    }

    private void calculateDeviations() {
        for (int i = 0; i < SELECTION; i++) {
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                double diff = dnas[i][j] - means[j];
                deviations[j] += diff * diff;
            }
        }
        for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
            deviations[j] /= SELECTION;
            deviations[j] =+ getNoise();
        }
    }

    private void generateGenes() {
        NormalDistribution[] normalDistributions = new NormalDistribution[Config.NO_OF_FEATURES];
        for (int i = 0; i < Config.NO_OF_FEATURES; i++) {
            normalDistributions[i] = new NormalDistribution(means[i], deviations[i]);
        }
        for (int i = 0; i < Config.POPULATION; i++) {
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                newGenes[i][j] = normalDistributions[j].sample();
            }
        }
    }

    private void normalize() {
        for (int i = 0; i < Config.POPULATION; i++) {
            double sumOfSquares = 0.0;
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                sumOfSquares += newGenes[i][j] * newGenes[i][j];
            }
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                newGenes[i][j] /= Math.sqrt(sumOfSquares);
            }
        }
    }

    public double[][] getGenes() {
        return newGenes;
    }

    public void writeLah() throws Exception {
        // Log writer
        FileWriter fileWriter = new FileWriter(
                "log/log" + String.format("%03d", generation) + ".txt", false);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        // Result writer
        FileWriter resultWriter = new FileWriter("log/log.txt", true);
        PrintWriter resultPrintWriter = new PrintWriter(resultWriter);

        // Gene writer
        FileWriter geneWriter = new FileWriter("genes.txt", false);
        PrintWriter genePrintWriter = new PrintWriter(geneWriter);

        printWriter.print("Score,F1,F2,F3,F4,F5,F6,F7,F8\n");

        double average = 0.0;
        for (int i = 0; i < Config.POPULATION; i++) {
            printWriter.print(fitness[i]);
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                printWriter.print("," + dnas[i][j]);
                genePrintWriter.print(newGenes[i][j] + " ");
            }
            printWriter.print("\n");
            genePrintWriter.print("\n");
            average += fitness[i];
        }

        average /= Config.POPULATION;

        if (generation == 0) {
            resultPrintWriter.print("Generation,Mean,Best,ThirdQuartile,Median,FirstQuartile,Worst\n");
        }

        resultPrintWriter
                .print(generation + "," + average + "," + fitness[0] + "," + fitness[
                        Config.POPULATION / 4] + "," + fitness[Config.POPULATION / 2] + ","
                        + fitness[3 * Config.POPULATION / 4] + "," + fitness[
                        Config.POPULATION - 1] + "\n");

        printWriter.flush();
        printWriter.close();

        resultPrintWriter.flush();
        resultPrintWriter.close();

        genePrintWriter.flush();
        genePrintWriter.close();
    }
}
