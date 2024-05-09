package it.unibo.ai.didattica.competition.tablut.grissinbon.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.List;

public class GrissinbonBlackHeuristic {

    private State state;

    //private final int[][] kingCoordinate = {{4,4}};
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
    private int pawnsBLACK; // numero di pedine NERE attuali
    private int pawnsWHITE; // numero di pedine BIANCHE attuali
    private int blackNearKing; // numero di pedine NERE vicine al RE

    //private int freeWayForKing; // vie libere per il re
    //private int totalDistanceFromBlackThroneCamps; // per stabilire se stiamo accerchiando i bianchi

    //Pesi
    private double BLACK_WEIGHT = 5.0;
    private double WHITE_WEIGHT = 5.0;
    private double FREE_WAY_FOR_KING = 5.0;
    //private double KING_BONUS = 5.0 ;
    private double ACCERCHIAMENTO_WEIGHT = 5;


    private static int LOOSE = -1;
    private static int WIN = 1;

    private void resetFields() {
        //this.kingCoordinate=null;
        this.pawnsBLACK=0;
        this.pawnsWHITE=0;
        this.blackNearKing=0;
        //this.freeWayForKing = 0;
    }



    private boolean checkLeft(int row,int column) {
        for( int i=row; i>= 0; i--) {
            if(this.state.getPawn(i, column).equalsPawn(State.Pawn.BLACK.toString()) ||
                    this.state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString())
            )
                return false;
        }
        return true;
    }



}

