import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

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
        System.out.println("OK");
        initialize(genesArray);
        expandByCrossOver();
        expandByMutation();
        reducePopulation();
        System.out.println("CURRENT MAX: " + newPopulation.get(0).fitness);
        System.out.println("CURRENT MIN " + newPopulation.get(newPopulation.size() -1).fitness);
    }

    private void initialize(Gene[] genesArray) {
        this.genesArray = genesArray;
        this.fitnessScores = new int[Config.POPULATION];
        this.oldGenes = new double[Config.POPULATION][Config.NO_OF_FEATURES];
        this.newGenes = new double[Config.POPULATION][Config.NO_OF_FEATURES];
        
        double[][] weightsSet = {
            // {0.14859893753929043*20,-0.3988580287056608*20,-0.05147732402369354*20, -0.30161953479781256*20f, -0.2543786543434735*20},
            // {  3.4181268101392694, -3.2178882868487753, -9.348695305445199, -7.899265427351652, -3.3855972247263626},
            // { 1.0157382356213152, -4.592208044263306, -7.360837710922974, -7.798123378421997, -6.793547344753633},
            // { 1.9400759197493433, -4.061509929466729, -8.334616452961026, -6.015572468226395, -6.853156643203179},
            // {0.07102716633358042, -2.805173095479242, -5.30189828551295, -4.705874277946131, -4.066370242557882},
            // {9.832837125985247, -7.0662690787529, -9.481180098603112, -3.038489536922142, -9.341267476130502},
            // {0.07102716633358042, -2.805173095479242, -5.30189828551295, -3.943463672770103, -4.066370242557882},
            // {3.93719917810158, -5.053304009912225, -2.615659954395013, -2.3932594151809408, -8.702962904595529},
            // {7.247935059854502, -4.980881668207032, -8.465654162209866, -1.418733019146855, -7.947687995019514},
            // {5.404565296917712, -5.053304009912225, -2.615659954395013, -2.3932594151809408, -8.702962904595529}
        };
   
        System.out.println("LAST VERSION"); 
        Arrays.sort(this.genesArray, null);

        // for (double[] weights : weightsSet) {
        //     Simulator simulator = new Simulator(weights);
        //     simulator.run();
        //     System.out.println("sCORe oF it IS " + simulator.getScore());
        //     newPopulation.add(new Gene(weights, simulator.getScore()));
        // }

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

        rand = new Random();

        for (int i = 0; i < NUMBER_TO_DO_CROSSOVER; i++) {

            int firstIndex = rand.nextInt(Config.POPULATION);
            int secondIndex = rand.nextInt(Config.POPULATION);

            if(firstIndex != secondIndex) {
                newPopulation.add(crossOver(genesArray[firstIndex], genesArray[secondIndex]));
            }
        }
    }


    private void expandByMutation() {

        rand = new Random();

        Vector subjects = new Vector<Integer>();    
        while (subjects.size() < NUMBER_TO_DO_MUTATION) {
            int subject = rand.nextInt(newPopulation.size());
            if (!subjects.contains(subject)) {
                subjects.add(subject);
            }
        }

        for (int i = 0;  i < subjects.size();  i++) {
            int subject = rand.nextInt(subjects.size());
            int featureIndex = rand.nextInt(Config.NO_OF_FEATURES);

            newPopulation.add(mutate(newPopulation.get(subject), featureIndex));
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

        rand = new Random();
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
        rand = new Random();
        return rand.nextInt(2) == 0;
    }


    /**
     * Allowing mutation with a given probability. Mutate by adding/substracting randomly an amount in the given range
     */
    private Gene mutate(Gene gene, int mutateLocation) {

        rand = new Random();
        double[] parentGene = gene.dna;
        double[] childGene = new double[Config.NO_OF_FEATURES];
        System.arraycopy( parentGene, 0, childGene, 0, parentGene.length );

            if(flipCoin()) {
                childGene[mutateLocation] += rand.nextDouble() * 2;
            } else {
                childGene[mutateLocation] -= rand.nextDouble() * 2;
            }

            if(mutateLocation == 5) {
                childGene[mutateLocation] = Math.abs(childGene[mutateLocation]);
            } else {
                childGene[mutateLocation] = -Math.abs(childGene[mutateLocation]);
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
