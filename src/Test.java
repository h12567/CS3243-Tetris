import java.util.Random;
import java.util.Arrays;

public class Test {
    public static double GENE_MAX_VALUE = 1;
    public static int GENE_LENGTH = Config.NO_OF_FEATURES;

    public static final int POPULATION = Config.POPULATION;
    public static final int TRIES = 100;
    public static final int NEW_POP = 20;
    public static final int CROSSOVER = 20;
    public static final int MUTATION = 30;

    public static Gene getRandomGene() {
        double[] res = new double[GENE_LENGTH];
        for(int i = 0 ; i < GENE_LENGTH ; i ++) {
            res[i] = new Random().nextDouble() * GENE_MAX_VALUE * 2 - GENE_MAX_VALUE;
        }
        return new Gene(res).normalize();
    }

    public static Gene getChild(Gene a, Gene b) {
        double[] res = new double[GENE_LENGTH];

        double aw = a.score / (a.score + b.score);
        double bw = b.score / (a.score + b.score);

        for(int i = 0 ; i < GENE_LENGTH ; i ++ ) {
            res[i] = (aw * a.gene[i] + bw * b.gene[i]) / 2;
        }

        return new Gene(res).normalize();
    }

    public static Gene[] crossOver(Gene[] genes) {
        int sum = 0;
        for(int i = 0 ; i < POPULATION ; i ++)
            sum += genes[i].score;

        Gene mar[] = new Gene[CROSSOVER];

        for(int i = 0 ; i < CROSSOVER ; i ++) {
            int a = 0, b = 0;

            while(a == b) {
                int tmpSum = 0;
                a = new Random().nextInt(sum);
                for(int j = 0 ; j < POPULATION ; j ++) {
                    tmpSum += genes[j].score * genes[j].score;
                    if(tmpSum >= a) {
                        a = j;
                        break;
                    }
                }

                tmpSum = 0;
                b = new Random().nextInt(sum);
                for(int j = 0 ; j < POPULATION ; j ++) {
                    tmpSum += genes[j].score * genes[j].score;
                    if(tmpSum >= b) {
                        b = j;
                        break;
                    }
                }
            }

            mar[i] = getChild(genes[a], genes[b]);
        }

        return mar;
    }

    public static Gene[] mutation(Gene[] genes) {
        Gene[] res = new Gene[MUTATION];
        Random random = new Random();
        int cntr = 0;
        for(int i = POPULATION - 1; i >= POPULATION - MUTATION ; i --) {
            double[] cur = new double[GENE_LENGTH];
            for(int j = 0 ; j < GENE_LENGTH ; j ++) {
                cur[j] = genes[i].gene[j];
            }
            for (int j = 0; j < GENE_LENGTH; j++) {
                if (random.nextDouble() < Config.MUTATTION_RATE) {
                    cur[j] += -Config.MUTATION_AMOUNT + 2 * Config.MUTATION_AMOUNT * random
                            .nextDouble();
                }
            }
            res[cntr ++] = new Gene(cur).normalize();
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        Gene[] genes = new Gene[POPULATION];

        for(int i = 0 ; i < POPULATION ; i ++)
            genes[i] = getRandomGene();

        while(true) {
            System.out.print("STATUS");
            for(int id = 0 ; id < POPULATION ; id ++) {
                System.out.print("\rGENE id: " + id);
                genes[id].reset();
                 
                Simulator[] sim = new Simulator[TRIES];
                Thread[] threads = new Thread[TRIES];
                for(int i = 0 ; i < TRIES ; i ++) {
                    sim[i] = new Simulator(genes[id].gene);
                    threads[i] = new Thread(sim[i]);
                    threads[i].start();
                }

                for(int i = 0 ; i < TRIES ; i ++) {
                    threads[i].join();
                    genes[id].update(sim[i].getScore());
                }
            }
            System.out.println();

            Arrays.sort(genes);

            //Print top 10;
            System.out.println("===========TOP10============");
            for(int i = 0 ; i < 10 ; i ++) {
                Gene x = genes[POPULATION - 1 - i];
                System.out.println(x.score + " (" + x.mx + ") => " + x);
            }
            System.out.println("============================");

            Gene[] newPop = new Gene[POPULATION];
            int cur = 0;

            //cross over
            Gene[] crossOverResult = crossOver(genes);
            for(int i = 0 ; i < CROSSOVER ; i ++) {
                newPop[cur++] = crossOverResult[i];
            }

            //cross over
            Gene[] mutationResult = mutation(genes);
            for(int i = 0 ; i < CROSSOVER ; i ++) {
                newPop[cur++] = mutationResult[i];
            }

            //new population
            for(int i = 0 ; i < NEW_POP ; i ++) {
                newPop[cur++] = getRandomGene();
            }

            //insert
            for(int i = 0 ; i < cur ; i ++) {
                genes[i] = newPop[i];
            }
        }
    }
}
