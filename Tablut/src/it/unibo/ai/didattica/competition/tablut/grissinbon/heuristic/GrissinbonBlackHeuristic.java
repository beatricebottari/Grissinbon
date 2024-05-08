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


}

