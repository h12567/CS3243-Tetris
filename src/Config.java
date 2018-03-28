public class Config {
    // GAME SETTINGS
    public static final int POPULATION = 1000;
    public static final int NUMBER_OF_GENERATIONS = 100;
    public static final int TURN_NUMBER_LIMIT = 3000;    // Set to -1 for unlimited
    public static final int NO_OF_THREADS = 250; // MUST DIVIDE POPULATION!
    public static final boolean NEW_TRAINING_SESSION = true;
    public static final int STARTING_GENERATION = 0;

    // BREEDER SETTINGS
    public static final double SELECTION_RATE = 0.1;  // Two best genes from SELECTION_RATE * population will be chosen to cross over
    public static final double KILLOFF_RATE = 0.3;    // KILLOFF_RATE * POP = no. of genes killed off
    public static final double MUTATTION_RATE = 0.05;    // The probability of a gene getting a mutation
    public static final double MUTATION_AMOUNT = 0.2; // The max amount of mutation allowed

    // NOT A SETTING
    public static final int NO_OF_FEATURES = 4;
}
