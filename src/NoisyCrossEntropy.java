import java.util.Arrays;

public class NoisyCrossEntropy {

    private Gene[] genesArray;
    private int[] fitness;
    private double[][] dnas;
    private int generation;

    private double[] means;
    private double[] normalizedMeans;
    private double[] deviations;
    private double[] normalizedDeviations;

    private double[][] newGenes;

    private double SELECTION = Config.SELECTION_RATE * (double) Config.POPULATION;

    /**
     * Constructor. Takes in Gene[], return new genes
     */
    NoisyCrossEntropy(Gene[] genesArray, int generation) {
        Arrays.sort(genesArray, null);
        initialize(genesArray, generation);
        calculateMeans();
        calculateDeviations();
        normalize();
    }

    private double getNoise() {
        return Math.exp(- (double) generation / Math.PI);
    }

    private void initialize(Gene[] genesArray, int generation) {
        this.generation = generation;
        this.genesArray = genesArray;
        this.fitness = new int[Config.POPULATION];
        this.dnas = new double[Config.POPULATION][Config.NO_OF_FEATURES];
        for (int i = 0; i < Config.POPULATION; i++) {
            fitness[i] = genesArray[i].fitness;
            dnas[i] = genesArray[i].dna;
        }
        this.means = new double[Config.NO_OF_FEATURES];
        this.normalizedMeans = new double[Config.NO_OF_FEATURES];
        this.deviations = new double[Config.NO_OF_FEATURES];
        this.normalizedDeviations = new double[Config.NO_OF_FEATURES];
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

    private void normalize() {
        double sumOfSquares = 0.0;
        for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
            sumOfSquares += means[j];
        }
        if (sumOfSquares != 0.0) {
            for (int j = 0; j < Config.NO_OF_FEATURES; j++) {
                normalizedMeans[j] = means[j] / sumOfSquares;
                normalizedDeviations[j] = deviations[j] / sumOfSquares;
            }
        }
    }

    private void generateGenes() {

    }
}
