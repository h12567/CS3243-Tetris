import java.util.Random;
import java.util.Arrays;

public class Test {
    public static double GENE_MAX_VALUE = 1000;
    public static int GENE_LENGTH = 6;

    public static class Gene implements Comparable<Gene> {
        public double[] gene;
        public int score;

        public Gene(double[] gene) {
            this.gene = gene;
        }

        public int compareTo(Gene o) {
            return this.score - o.score;
        }

        public String toString() {
            String res = "" + gene[0];
            for(int i = 1 ; i < GENE_LENGTH ; i ++)
                res = res + " " + gene[i];
            return res;
        }
    }

    public static Gene getRandomGene() {
        double[] res = new double[GENE_LENGTH];
        for(int i = 0 ; i < GENE_LENGTH ; i ++) {
            res[i] = new Random().nextDouble() * GENE_MAX_VALUE * 2 - GENE_MAX_VALUE;
        }
        // double sum = 0;
        // for(int i = 0 ; i < GENE_LENGTH ; i ++) {
        //     sum += res[i];
        // }

        // for(int i = 0 ; i < GENE_LENGTH ; i ++) {
        //     res[i] = res[i] / sum * 100;
        // }
        return new Gene(res);
    }

    public static Gene getChild(Gene a, Gene b) {
        double[] res = new double[GENE_LENGTH];

        double aw = a.score / (a.score + b.score);
        double bw = b.score / (a.score + b.score);

        for(int i = 0 ; i < GENE_LENGTH ; i ++ ) {
            res[i] = (aw * a.gene[i] + bw * b.gene[i]) / 2;
        }

        return new Gene(res);
    }

    public static void mutate(Gene a, double chance) {
        for(int i = 0 ; i < GENE_LENGTH ; i ++ ) {
            double d = GENE_MAX_VALUE * 2 * chance;
            double lb = Math.max(-GENE_MAX_VALUE, a.gene[i] - d);
            double ub = Math.min(GENE_MAX_VALUE, a.gene[i] + d);
            a.gene[i] = lb + new Random().nextDouble() * (ub - lb);
        }
    }

    public static double chance = 0.05;
    public static int GENE_NUMBER = 100;
    public static int NEW_RANDOM_GENE = 10;
    public static int NEW_CROSSOVER_GENE = 75;
    public static int LIMIT = -1;


    public static void main(String[] args) throws Exception {
        Gene genes[] = new Gene[GENE_NUMBER];

        for(int i = 0 ; i < GENE_NUMBER; i ++) {
            genes[i] = getRandomGene();
        }

        int tries = 1;
        int generation = 0;
        while(true) {
            Thread threads[] = new Thread[GENE_NUMBER * tries];
            Simulator sim[] = new Simulator[GENE_NUMBER * tries];
            for(int i = 0 ; i < GENE_NUMBER * tries ; i++ ) {
                sim[i] = new Simulator(genes[i / tries].gene);
                threads[i] = new Thread(sim[i]);
                threads[i].run();
            }

            for(int i = 0 ; i < GENE_NUMBER ; i++ ) {
                genes[i].score = -1;
            }

            for(int i = 0 ; i < GENE_NUMBER * tries ; i++ ) {
                threads[i].join();
                if(genes[i/tries].score < sim[i].getScore())
                    genes[i/tries].score = sim[i].getScore();
            }

            Arrays.sort(genes);

            System.out.println("TOP 10");
            for(int i = GENE_NUMBER - 1 ; i >= GENE_NUMBER - 10 ; i --) {
                System.out.printf("%d => %s\n", genes[i].score, genes[i].toString());
            }

            System.out.println(genes[0].score + " " + genes[GENE_NUMBER - 1].score);

            int cntr = 0;

            //Marriage
            int sum = 0;
            for(int i = 0 ; i < GENE_NUMBER ; i ++)
                sum += genes[i].score * genes[i].score;

            Gene mar[] = new Gene[NEW_CROSSOVER_GENE];

            for(int i = 0 ; i < NEW_CROSSOVER_GENE ; i ++) {
                int a = 0, b = 0;

                while(a == b) {
                    int tmpSum = 0;
                    a = new Random().nextInt(sum) + 1;
                    for(int j = 0 ; j < GENE_NUMBER ; j ++) {
                        tmpSum += genes[j].score * genes[j].score;
                        if(tmpSum >= a) {
                            a = j;
                            break;
                        }
                    }

                    tmpSum = 0;
                    b = new Random().nextInt(sum) + 1;
                    for(int j = 0 ; j < GENE_NUMBER ; j ++) {
                        tmpSum += genes[j].score * genes[j].score;
                        if(tmpSum >= b) {
                            b = j;
                            break;
                        }
                    }
                }

                mar[i] = getChild(genes[a], genes[b]);
            }

            for(int i = 0 ; i < NEW_CROSSOVER_GENE ; i ++) {
                genes[cntr++] = mar[i];
            }

            for(int i = 0 ; i < NEW_RANDOM_GENE ; i ++) {
                genes[cntr++] = getRandomGene();
            }

            while(cntr < genes.length) {
                if(genes[cntr].score <= LIMIT) {
                    genes[cntr++] = getRandomGene();
                } else {
                    break;
                }
            }

            for(int i = 0 ; i < GENE_NUMBER - 5 ; i ++)
                mutate(genes[i], chance);

            System.out.println("Generation best: " + genes[GENE_NUMBER - 1].score);
        }
    }
}
