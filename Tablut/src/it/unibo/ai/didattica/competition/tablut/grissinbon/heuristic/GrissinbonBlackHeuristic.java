package it.unibo.ai.didattica.competition.tablut.grissinbon.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

// IDEA DI BASE: piu' il valore "result" e' alto, piu' e' conveniente una mossa per i neri,
// percui aggiungere tutte le variabili che indicano una posizione di vantaggio per i neri
// e sottrarre tutte quelle che indicano una posizione di vantaggio per i bianchi

// Ogni variabile va poi moltiplicata per un peso, ovvero la rilevanza dell'informazione da essa individuata.
// Non tutte le variabili (che siano da aggiungere o sottrarre) hanno la stessa importanza,
// percui i pesi corrispettivi avranno valori diversi.

// OBIETTIVO NERI: mangiare il re, quindi
// - più lo si circonda meglio è
// - meno pedine bianche ci sono a proteggerlo meglio è.
// E evitare che il re vada in salvo
// -> coprire sempre almeno parzialmente il rombo

public class GrissinbonBlackHeuristic extends Heuristic{
    private State state;

    //private final int[] throne = {4,4};
    private final int[][] positionsNearThrone = {{4,3}, {4,5}, {3,4}, {5,4}};
    private int[] kingPosition;

    // values to calculate every move
    private int pawnsWHITE;
    private int pawnsBLACK;
    private int blackAdjacentKing;
    private int freeWayForKing;
    private int blackNeedToEatKing;
    private int totalDistanceFromBlackOrSimilar;  // by whites, the more is little the more black are

    // Weights
    private double BLACK_WEIGHT = 6.0;
    private double WHITE_WEIGHT = 11.0;
    private double FREE_WAY_FOR_KING = 20.0;    // -free_way_for_king --> +rhombus ratio or similar
    private double KING_BONUS = 7.0 ;
    private double ENCIRCLE_WEIGHT = 900;


    public GrissinbonBlackHeuristic (State state)
    {
        super(state);
    }

    public double evaluate() {
        resetFields();
        double result = 0.0;

        int matchState = calculateState();

        if (matchState == LOOSE) {
            return Double.MIN_VALUE;
        }
        else if (matchState == WIN){
            return Double.MAX_VALUE;
        }

        result += this.pawnsBLACK*this.BLACK_WEIGHT;
        result += this.blackAdjacentKing*this.BLACK_WEIGHT; // black adjacent king worth double
        result -= this.pawnsWHITE*this.WHITE_WEIGHT;
        result -= this.freeWayForKing*this.FREE_WAY_FOR_KING;

        double encircleRatio = this.ENCIRCLE_WEIGHT/this.totalDistanceFromBlackOrSimilar;
        result += encircleRatio;

        result += (1.0/this.blackNeedToEatKing)*(encircleRatio);

        return result;
    }

    private void resetFields() {
        this.kingPosition = null;
        this.pawnsBLACK = 0;        // val max = 8
        this.pawnsWHITE = 0;        // val max = 16
        this.blackAdjacentKing = 0;     // val max = 4
        blackNeedToEatKing = 0;         // val max = 4
        this.freeWayForKing = 0;    // val max = 4
    }

    private int calculateState() {

        // calculate pawns on board, for white pawns also the distance from camps
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j <state.getBoard()[i].length; j++) {

                if(state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
                    pawnsWHITE++;
                    this.totalDistanceFromBlackOrSimilar += getDistanceFromBlackOrSimilar(i, j);
                }
                else if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())){
                    pawnsWHITE++;
                    kingPosition = new int[]{i,j};
                    this.totalDistanceFromBlackOrSimilar += (int) (getDistanceFromBlackOrSimilar(i, j)*this.KING_BONUS);
                }
                else if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                    pawnsBLACK++;
                }
            }
        }

        if(kingPosition==null) {
            return WIN;
        }
        else if (isEscape(this.kingPosition[0], this.kingPosition[1])) {
            return LOOSE;
        }

        int x = this.kingPosition[0];
        int y = this.kingPosition[1];

        // free ways for king
        if(x < 3 || x > 5) {
            if(checkLeft(x,y)) freeWayForKing++;
            if(checkRight(x,y)) freeWayForKing++;
        }
        if(y < 3 || y > 5) {
            if(checkUp(x,y)) freeWayForKing++;
            if(checkDown(x,y)) freeWayForKing++;
        }

        // black adjacent king
        if(x > 0)
            if (this.state.getPawn(x-1, y).equalsPawn(State.Pawn.BLACK.toString())) blackAdjacentKing++;
        if(x < state.getBoard().length-1)
            if (this.state.getPawn(x+1, y).equalsPawn(State.Pawn.BLACK.toString())) blackAdjacentKing++;
        if(y > 0)
            if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.BLACK.toString())) blackAdjacentKing++;
        if(y < state.getBoard().length-1)
            if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.BLACK.toString())) blackAdjacentKing++;

        blackNeedToEatKing = getNumBlackNeedToEat();

        return 0;
    }


    private boolean isEscape(int x, int y) {
        if(escape.contains(state.getBox(this.kingPosition[0], this.kingPosition[1])))
                return true;
        return false;
    }

    /*
    private boolean isCamp(int x, int y) {
        for (int[] camp : this.camps){
            if(camp[0] == x && camp[1] == y) return true;
        }
        return false;
    }


    private boolean isFree(int x, int y) {
        if(this.state.getPawn(x, y).equalsPawn(State.Pawn.BLACK.toString()) ||
                this.state.getPawn(x, y).equalsPawn(State.Pawn.WHITE.toString()) ||
                //this.isCamp(x,y))
            return false;
        else
            return true;
    }

    private boolean checkFreeLeft(int row,int column) {
        for( int i=row; i>= 0; i--) {
            if(isFree(i, column)) return false;
        }
        return true;
    }

    private boolean checkFreeRight(int row,int column) {
        for( int i=row; i< 9; i++) {
            if(isFree(i, column)) return false;
        }
        return true;
    }

    private boolean checkFreeUp(int row,int column) {
        for( int i=column; i>= 0; i--) {
            if(isFree(row, i)) return false;
        }
        return true;
    }

    private boolean checkFreeDown(int row,int column) {
        for( int i=column; i < 9; i++) {
            if(isFree(row, i)) return false;
        }
        return true;
    }
*/
    private boolean isBlackOrSimilar(int x, int y) {
        if(this.state.getPawn(x, y).equalsPawn(State.Pawn.BLACK.toString())
                || this.state.getPawn(x, y).equalsPawn(State.Pawn.THRONE.toString())
                || camps.contains(state.getBox(x, y)))
            return false;
        else
            return true;
    }

    public int getDistanceFromBlackOrSimilar(int row, int column) {
        int distanceResult = 0 ;

        distanceResult += getDistanceUp(row,column);
        distanceResult += getDistanceDown(row,column);
        distanceResult += getDistanceRight(row,column);
        distanceResult += getDistanceLeft(row,column);

        return distanceResult;
    }

    public int getDistanceUp(int row, int column) {
        int distance = 0; boolean end = false;

        for (int i = row-1 ; i>=0 && !end; i--) {
            if (isBlackOrSimilar(i, column))
                end = true;
            else
                distance++;
        }
        return distance;
    }

    public int getDistanceDown(int row, int column) {
        int distance = 0; boolean end = false;

        for (int i = row+1 ; i<this.state.getBoard().length && !end; i++) {
            if (isBlackOrSimilar(i, column))
                end = true;
            else
                distance++;
        }
        return distance;
    }

    public int getDistanceRight(int row, int column) {
        int distance = 0; boolean end = false;

        for (int i = column+1 ; i<state.getBoard().length && !end; i++) {
            if (isBlackOrSimilar(row, i))
                end = true;
            else
                distance++;
        }
        return distance;
    }

    public int getDistanceLeft(int row, int column) {
        int distance = 0; boolean end = false;

        for (int i = column-1 ; i>=0 && !end; i--) {
            if (isBlackOrSimilar(row, i))
                end = true;
            else
                distance++;
        }

        return distance;
    }

    private boolean isNearThrone(int x, int y) {
        for (int[] nr : this.positionsNearThrone){
            if(nr[0] == x && nr[1] == y) return true;
        }
        return false;
    }

    public int getNumBlackNeedToEat() {
        if(throne.equals(state.getBox(kingPosition[0], kingPosition[1])))
            return 4;
        else if(isNearThrone(kingPosition[0], kingPosition[1]))
            return 3;
        else
            return 2;
    }
}

