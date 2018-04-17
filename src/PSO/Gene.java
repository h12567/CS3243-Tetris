public class Gene implements Comparable<Gene> {

    public double[] dna;
    public int fitness;

    Gene(double[] dna) {
        this.dna = dna;
    }

    Gene(double[] dna, int fitness) {
        this.dna = dna;
        this.fitness = fitness;
    }

    @Override
    /**
     * The most left gene is the strongest gene
     */
    public int compareTo(Gene o) {
        if (this.fitness > o.fitness) {
            return -1;
        } else if (this.fitness == o.fitness) {
            return 0;
        } else {
            return 1;
        }
    }
}
