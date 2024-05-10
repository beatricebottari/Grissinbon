package it.unibo.ai.didattica.competition.tablut.grissinbon.heuristic;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Heuristic {



    public State state;
    public List<String> camps;
    public List<String> escape;
    public List<String> nearsThrone;
    public String throne;


    public Heuristic(State state) {
        this.state = state;
        this.camps = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9",
                "e9", "f9", "e8");
        this.escape = Arrays.asList("a2", "a3", "a7", "a8", "b1", "b9", "c1", "c9", "g1", "g9", "h1", "h9", "i2", "i3",
                "i7", "i8");
        this.nearsThrone = Arrays.asList("e4", "e6", "d5", "f5");
        this.throne = "e5";
    }

    public double evaluateState(){
        return 0;
    }

    //controlliamo se non ci sono pedine o campi in tutte le direzioni, il re così vince
    //TODO: da controllare le direzioni
    public boolean checkLeft(int row,int column) {
        for( int i=row; i>= 0; i--) {
            if(this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString()) ||
                    camps.contains(state.getBox(i, column)))
                return false;
        }
        return true;
    }

    public boolean checkRight(int row,int column) {
        for( int i=row; i< 9; i++) {
            if(this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString()) ||
                    camps.contains(state.getBox(i, column)))
                return false;
        }
        return true;
    }

    public boolean checkUp(int row,int column) {
        for(int i=column; i>= 0; i--) {
            if(this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(row, i).equalsPawn(State.Pawn.WHITE.toString()) ||
                    camps.contains(state.getBox(row, i)))
                return false;
        }
        return true;
    }

    public boolean checkDown(int row,int column) {
        for( int i=column; i < 9; i++) {
            if(this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(row, i).equalsPawn(State.Pawn.WHITE.toString()) ||
                    camps.contains(state.getBox(row, i)))
                return false;
        }
        return true;
    }



}
