import java.util.Random;
import java.util.Arrays;

public class SimulatedAnnealing {
    public static int GENE_LENGTH = Config.NO_OF_FEATURES;

    public static class Temperature {

        private int n;
        private int cur;
        private double t0, tn;
    

        public Temperature(int n, double t0, double tn) {
            this.n = n;
            this.cur = 0;
            this.t0 = t0;
            this.tn = tn;
        }

        public int nextStep() {
            cur += 1;
            return cur;
        }

        public double getProb() {
            return this.getProb(cur);
        }

        public double getProb(double k) {
            double exp = 2.0 * Math.log(t0 - tn) * (k - n/2.0) / n;
            // System.out.println(exp);
            return tn + (t0 - tn) / (1 + Math.pow(Math.E, exp));
        }

        public int getCur() {
            return cur;
        }

        public int getN() {
            return n;
        }
    }

    public static final double DIST_MILTIPLIER = 0.20;

    public static Gene mutation(Gene gene, Temperature temp) {
        Gene newGene = new Gene(Arrays.copyOf(gene.gene, gene.gene.length));
        Gene dist = Gene.getRandomGene();

        double x = new Random().nextDouble();

        for(int i = 0 ; i < GENE_LENGTH ; i ++) {
            newGene.gene[i] += dist.gene[i] * x * temp.getProb() * DIST_MILTIPLIER;
        }

        return newGene;
    }

    public static final int TRIES = 100;

    public static void proc(Gene gene) throws Exception {
        Simulator[] sim = new Simulator[TRIES];
        Thread[] threads = new Thread[TRIES];
        for(int i = 0 ; i < TRIES ; i ++) {
            sim[i] = new Simulator(gene.gene);
            sim[i].setLimit(1000000000);
            threads[i] = new Thread(sim[i]);
            threads[i].start();
        }

        for(int i = 0 ; i < TRIES ; i ++) {
            threads[i].join();
            gene.update(sim[i].getScore());
        }
    }

    public static void main(String[] args) throws Exception {
        Temperature temp = new Temperature(100000, 5, 0);

        double[] x = {0.014484298586960149, 0.031294521160020156, 0.1980366661548338, -0.20512670163387006, 0.005229229383325969, 0.9578559538547294};
        // Gene init = Gene.getRandomGene();
        Gene init = new Gene(x);
        proc(init);
        System.out.println(init.score + " (" + init.mx + ") => " + init);

        while(temp.getCur() <= temp.getN()){
            System.err.println("GEN: " + temp.getCur() + " | " + (temp.getProb() * 0.20));
            Gene nextGen = mutation(init, temp);
            
            System.out.println("Checking: " + nextGen);
            proc(nextGen);

            System.out.println(nextGen.compareTo(init));

            if(nextGen.compareTo(init) > 0) {
                init = nextGen;
            } 
            // else if(new Random().nextDouble() <= temp.getProb() / 100.0) {
            //     init = nextGen;
            // }

            System.out.println(init.score + " (" + init.mx + ") => " + init);

            temp.nextStep();
        }
    }
}
