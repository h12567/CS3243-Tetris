import java.util.Random;

public class Gene implements Comparable<Gene> {
    public double[] gene;
    public int score;
    public int mx;
    public static int GENE_LENGTH = Config.NO_OF_FEATURES;

    public Gene(double[] gene) {
        this.gene = gene;
        reset();
    }

    public int compareTo(Gene o) {
        return this.score - o.score;
    }

    public String toString() {
        String res = "" + gene[0];
        for(int i = 1 ; i < GENE_LENGTH ; i ++)
            res = res + ", " + gene[i];
        return res;
    }

    public void reset() {
        score = 0;
        mx = 0;
    }

    public void update(int x) {
        score += x;
        mx = Math.max(mx, x);
    }

    public Gene normalize() {
        double sum = 0;
        for(int i = 0 ; i < GENE_LENGTH ; i ++) {
            sum += gene[i] * gene[i];
        }

        sum = Math.sqrt(sum);

        for(int i = 0 ; i < GENE_LENGTH ; i ++) {
            gene[i] = gene[i] / sum;
        }
        return this;
    }

    public static Gene getRandomGene() {
        double[] res = new double[GENE_LENGTH];
        for(int i = 0 ; i < GENE_LENGTH ; i ++) {
            res[i] = new Random().nextDouble() * 2 - 1.0;
        }
        return new Gene(res).normalize();
    }
}
