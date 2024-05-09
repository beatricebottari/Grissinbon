package it.unibo.ai.didattica.competition.tablut.grissinbon.heuristic;
import java.util.Arrays;
import java.util.List;
import it.unibo.ai.didattica.competition.tablut.domain.State;
public class GrissinbonWhiteHeuristic {

    private State state;
    private List<String> camps;
    private List<String> escape;
    private List<String> nearsThrone;
    private String throne;

    private double[][] pesi_posizione_re=new double[][]
                    {{0, 20, 20,-6, -6, -6,20, 20, 0},
                    {20, 1, 1, -5, -6, -5, 1,  1, 20},
                    {20, 1, 4,  1, -2,  1, 4,  1, 20},
                    {-6,-5, 1,  1,  1,  1, 1, -5, -6},
                    {-6,-6,-2,  1,  2,  1,-2, -6, -6},
                    {-6,-5, 1,  1,  1,  1, 1, -5, -6},
                    {20, 1, 4,  1, -2,  1, 4,  1, 20},
                    {20, 1, 1, -5, -6, -5, 1,  1, 20},
                    {0, 20, 20,-6, -6, -6,20, 20, 0}};


    private double WHITE_REMAINING_WEIGHT = 22.0;
    private double BLACK_REMAINING_WEIGHT = 12.0;
    private double WINNING_WAYS_KING_WEIGHT = 50.0;
    private double BLACK_NEAR_KING_WEIGHT = 6.0;
    private double POSITION_WEIGHT = 0.4;
    private double KING_POSITION_WEIGHT = 2;








}
