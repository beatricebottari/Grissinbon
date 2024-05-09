package it.unibo.ai.didattica.competition.tablut.grissinbon.heuristic;
import java.util.Arrays;
import java.util.List;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.grissinbon.utility.Coordinates;

public class GrissinbonWhiteHeuristic extends Heuristic {

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

    private int pawnsBlack; // pedine NERE attuali
    private int pawnsWhite; // pedine BIANCHE attuali
    private int winningWaysForKing; // vie libere per il RE
    private Coordinates kingCoordinate;
    private int blackNearKing; // pedine NERE vicine al RE
    private double positions_sum; // pesi delle posizioni dei bianchi
    //private double CRStartegicheFree;

    private double WHITE_REMAINING_WEIGHT = 22.0;
    private double BLACK_EATEN_WEIGHT = 12.0;
    private double WINNING_WAYS_KING_WEIGHT = 50.0;
    private double BLACK_NEAR_KING_WEIGHT = 6.0;
    private double POSITION_WEIGHT = 0.4;
    private double KING_POSITION_WEIGHT = 2;

    private static int LOOSE = -1;
    private static int WIN = 1;

    public GrissinbonWhiteHeuristic(State state) {
        super(state);
    }

    private void resetFields() {
        this.pawnsBlack=0;
        this.pawnsWhite=0;
        this.winningWaysForKing=0;
        this.kingCoordinate=null;
        this.blackNearKing=0;
        this.positions_sum=0;
        //this.CRStartegicheFree = 0;
    }

    public double evaluate(){
        double result = 0;
        resetFields();
        int state = calculateValue();

        //se lo state è -1 ho perso
        if(state == LOOSE){
            //TODO: si può tornare anche qualcosa di meglio
            return Double.MIN_VALUE;
        } else if(state == WIN){
            return Double.MAX_VALUE;
        }
        result += (16 - pawnsBlack)*BLACK_EATEN_WEIGHT;
        result += pawnsWhite*WHITE_REMAINING_WEIGHT;
        result += winningWaysForKing*WINNING_WAYS_KING_WEIGHT;
        result += blackNearKing*BLACK_NEAR_KING_WEIGHT;
        result += this.positions_sum*POSITION_WEIGHT;
        //result += CRStartegicheFree;

        return result;
    }

    private int calculateValue(){

        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j <state.getBoard()[i].length; j++) {
                if(state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
                    pawnsWhite++;
                }
                else if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())){
                    pawnsWhite++;
                    kingCoordinate = new Coordinates(i, j);
                    //diamo un peso alla posizione attuale del re
                    this.positions_sum += this.pesi_posizione_re[i][j]*KING_POSITION_WEIGHT;
                } else if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())){
                    pawnsBlack++;
                }
            }
        }

        if (this.kingCoordinate == null){
            return LOOSE;
        } else if (escape.contains(state.getBox(kingCoordinate.getX(), kingCoordinate.getY()))) {
            return WIN;
        }

        //contiamo le pedine nere vicine al re
        int x = kingCoordinate.getX();
        int y = kingCoordinate.getY();
        if(x>0) {
            //guardo se re ha pedina nera sotto
            if (this.state.getPawn(x - 1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
        }
         if(x<state.getBoard().length-1){
            //guardo se re ha pedina nera sopra
            if(this.state.getPawn(x+1, y).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
        }
         if(y>0) {
             //guardo se re ha pedina nera a sinistra
             if (this.state.getPawn(x, y-1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
         }
        if(y < state.getBoard().length-1) {
            //guardo se re ha pedina nera a destra
            if (this.state.getPawn(x, y+1).equalsPawn(State.Pawn.BLACK.toString())) blackNearKing++;
        }

        if(x < 3 || x > 5) {
            if(checkLeft(x,y)) winningWaysForKing++;
            if(checkRight(x,y)) winningWaysForKing++;
        }

        if(y < 3 || y > 5) {
            if(checkUp(x,y)) winningWaysForKing++;
            if(checkDown(x,y)) winningWaysForKing++;
        }
        return 0;

    }

    //TODO: da guardare ultima funzione righeColonneStrategicheLibere se ci serve

}













