package it.unibo.ai.didattica.competition.tablut.grissinbon.heuristic;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Heuristic {

    public static int LOOSE = -1;
    public static int WIN = 1;

    public State state;
    public List<String> camps;
    public List<String> escapes;
    public String throne;
    public List<String> nearsThrone;

    public Heuristic(State state) {
        this.state = state;
        this.camps = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1",
                "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
        this.escapes = Arrays.asList("a2", "a3", "a7", "a8", "b1", "b9",
                "c1", "c9", "g1", "g9", "h1", "h9", "i2", "i3", "i7", "i8");
        this.throne = "e5";
        this.nearsThrone = Arrays.asList("e4", "e6", "d5", "f5");

    }

    public abstract double evaluate();

    private boolean isFree(int x, int y) {
        if(this.state.getPawn(x, y).equalsPawn(State.Pawn.BLACK.toString()) ||
                this.state.getPawn(x, y).equalsPawn(State.Pawn.WHITE.toString()) ||
                this.camps.contains(state.getBox(x, y)))
            return false;
        else
            return true;
    }

    public boolean checkLeft(int row,int column) {
        for( int i=row; i>= 0; i--) {
            if(isFree(row,column))
                return false;
        }
        return true;
    }

    public boolean checkRight(int row,int column) {
        for( int i=row; i< 9; i++) {
            if(isFree(row,column))
                return false;
        }
        return true;
    }

    public boolean checkUp(int row,int column) {
        for(int i=column; i>= 0; i--) {
            if(isFree(row,column))
                return false;
        }
        return true;
    }

    public boolean checkDown(int row,int column) {
        for( int i=column; i < 9; i++) {
            if(isFree(row,column))
                return false;
        }
        return true;
    }

}
