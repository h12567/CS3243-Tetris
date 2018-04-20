import java.util.stream.IntStream;
import java.util.Arrays;

public class ExtendedState extends State {

    public ExtendedState() {
        super();
    }

    public ExtendedState(State s) {
        super(s);
    }

    public void setNextPiece(int nextPiece) {
        this.nextPiece = nextPiece;
    }

}