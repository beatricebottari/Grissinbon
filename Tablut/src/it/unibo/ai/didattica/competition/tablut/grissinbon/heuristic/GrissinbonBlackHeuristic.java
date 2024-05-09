package it.unibo.ai.didattica.competition.tablut.grissinbon.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.List;

public class GrissinbonBlackHeuristic {

    private State state;

    private final int[][] camps =           // gray citadels
            {{0, 3}, {0, 4}, {0, 5}, {1, 4},
            {3, 0}, {4, 0}, {5, 0}, {4, 1},
            {8, 3}, {8, 4}, {8, 5}, {7, 4},
            {3, 8}, {4, 8}, {5, 8}, {4, 7}};

    private final int[][] escapes =       // stars
            {{0,1}, {0,2}, {1,0}, {2,0},
            {8,1}, {8,2}, {6,0}, {7,0},
            {0,6}, {0,7}, {1,8}, {2,8},
            {6,8}, {7,8}, {8,6}, {8,7}};

    private final int[][] escapes_block = {  {1,2},       {1,6},
                                        {2,1},                 {2,7},

                                        {6,1},                  {6,7},
                                              {7,2},      {7,6} };
    private final int[] castle = {4,4};
    private final int[][] positionsNearCastle = {{4,3}, {4,5}, {3,4}, {5,4}};

    // valori da aggiornare
    private int[] kingCoordinate;
    private int pawnsBLACK; // numero di pedine NERE attuali
    private int pawnsWHITE; // numero di pedine BIANCHE attuali
    private int blackNearKing; // numero di pedine NERE vicine al RE

    private int freeWayForKing; // vie libere per il re
    private int totalDistanceFromBlackThroneCamps; // per stabilire se stiamo accerchiando i bianchi

    //Pesi
    private double BLACK_WEIGHT = 5.0;
    private double WHITE_WEIGHT = 5.0;
    private double FREE_WAY_FOR_KING = 5.0;
    //private double KING_BONUS = 5.0 ;
    private double ACCERCHIAMENTO_WEIGHT = 5;


    private static int LOOSE = -1;
    private static int WIN = 1;

    private void resetFields() {
        this.kingCoordinate= new int[]{4, 4};
        this.pawnsBLACK=0;
        this.pawnsWHITE=0;
        this.blackNearKing=0;
        this.freeWayForKing = 0;
    }

    public double evaluate(){
        resetFields();
        double result = 0.0;

        int value = calculateValue();

        if (value == LOOSE) {
            return Double.MIN_VALUE;
        }
        else if (value == WIN){
            return Double.MAX_VALUE;
        }

        //calcolo result
        //da MODIFICAREEEEEEEEEEEEEE ????????????????
        result+=this.pawnsBLACK*this.BLACK_WEIGHT;
        result-=this.pawnsWHITE*this.WHITE_WEIGHT;
        result+=this.blackNearKing*this.BLACK_WEIGHT;
        result-=this.freeWayForKing*this.FREE_WAY_FOR_KING;
        result+=this.ACCERCHIAMENTO_WEIGHT/this.totalDistanceFromBlackThroneCamps;


        return result;
    }


    private int calculateValue() {
        int value =0;

        if(kingCoordinate==null)
        {
            return WIN;
        }
        else if (isEscape(this.kingCoordinate[0], this.kingCoordinate[1])) {
            return LOOSE;
        }

        int x = this.kingCoordinate[0];
        int y = this.kingCoordinate[1];

        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j <state.getBoard()[i].length; j++) {

                //calcolo pedine white
                if(state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
                    pawnsWHITE++;
                    //this.totalDistanceFromBlackThroneCamps+=getDistanceFromBlackThroneCamps(i, j);
                }
                else if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())){
                    pawnsWHITE++;
                    kingCoordinate = new int[]{i,j};
                    //this.totalDistanceFromBlackThroneCamps+=getDistanceFromBlackThroneCamps(i, j)*this.KING_BONUS;
                }
                else if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                    pawnsBLACK++;
                }


                // check pedine BLACK immediatamente vicine al re (di fianco a lato, sopra, sotto)
                if(x > 0) {
                    if (this.state.getPawn(x-1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
                }
                if(x < state.getBoard().length-1) {
                    if (this.state.getPawn(x+1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
                }
                if(y > 0) {
                    if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
                }
                if(y < state.getBoard().length-1) {
                    if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
                }

                // VIE LIBERE PER IL RE
                if(x < 3 || x > 5) {
                    if(checkLeft(x,y)) freeWayForKing++;
                    if(checkRight(x,y)) freeWayForKing++;
                }

                if(y < 3 || y > 5) {
                    if(checkUp(x,y)) freeWayForKing++;
                    if(checkDown(x,y)) freeWayForKing++;
                }

                return 0;


            }
        }
        return value;
    }



    //check a sx generico di BLACK, WHITE CAMP
    private boolean checkLeft(int row,int column) {
        for( int i=row; i>= 0; i--) {
            if(this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString()) ||
                    this.isCamp(i,column))
                return false;
        }
        return true;
    }

    //check a dx generico di BLACK, WHITE CAMP
    private boolean checkRight(int row,int column) {
        for( int i=row; i< 9; i++) {
            if(this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString()) ||
                    this.isCamp(i,column))
                return false;
        }
        return true;
    }

    private boolean checkUp(int row,int column) {
        for( int i=column; i>= 0; i--) {
            if(this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(row, i).equalsPawn(State.Pawn.WHITE.toString()) ||
                    this.isCamp(row,i))
                return false;
        }
        return true;
    }


    private boolean checkDown(int row,int column) {
        for( int i=column; i < 9; i++) {
            if(this.state.getPawn(row, i).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(row, i).equalsPawn(State.Pawn.WHITE.toString()) ||
                    this.isCamp(row,i))
                return false;
        }
        return true;
    }


    private boolean isCamp(int x, int y) {
        for (int[] camp : this.camps){
            if(camp[0] == x && camp[1] == y) return true;
        }
        return false;
    }

    private boolean isEscape(int x, int y) {
        for (int[] escape : this.escapes) {
            if (escape[0] == x && escape[1] == y) {
                return true;
            }
        }
        return false;
    }


}

