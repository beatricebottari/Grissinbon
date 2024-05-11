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

    //migliori posizioni per i bianchi
    private int[][] bestPositions = {
            {2,3},  {3,5},
            {5,3},  {6,5}
    };

    //Threshold usata per decidere se usare il peso delle bestPositions
    private static int THRESHOLD_BEST = 2;

    private int pawnsBlack; // pedine NERE attuali
    private int pawnsWhite; // pedine BIANCHE attuali
    private int winningWaysForKing; // vie libere per il RE
    private Coordinates kingCoordinate;
    private int blackNearKing; // pedine NERE vicine al RE
    private double positions_sum; // pesi delle posizioni dei bianchi
    private double whiteBestPositions; //bianchi nelle posizioni migliori

    private double capture;    //possibilità di catturare una pedina nera
    private int threats;    //pedine nere che minacciano pedine bianche
    //private double CRStartegicheFree;

    private double WHITE_REMAINING_WEIGHT = 24.0;
    private double BLACK_EATEN_WEIGHT = 12.0;
    private double WINNING_WAYS_KING_WEIGHT = 52.0;
    private double BLACK_NEAR_KING_WEIGHT = 6.0;
    private double POSITION_WEIGHT = 1.0;
    private double KING_POSITION_WEIGHT = 2.0;
    private double WHITE_BEST_POSITION = 1.0;
    private double THREATENING_PAWNS_WEIGHT = 0.3;
    private double CAPTURE_WEIGHT = 2.0;

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
        this.whiteBestPositions=0;

        this.threats=0;
        this.capture=0;
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
        result -= blackNearKing*BLACK_NEAR_KING_WEIGHT;
        result += this.positions_sum*POSITION_WEIGHT;
        result += whiteBestPositions*WHITE_BEST_POSITION;

        result -= threats*THREATENING_PAWNS_WEIGHT;
        result += capture;
        //result += CRStartegicheFree;

        return result;
    }

    private int calculateValue(){

        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j <state.getBoard()[i].length; j++) {
                if(state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
                    pawnsWhite++;
                    // Controlla se questa pedina può catturare una pedina nera avversaria
                    capture = capture + evaluateCapturingMoves(i, j);
                }
                else if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())){
                    pawnsWhite++;
                    kingCoordinate = new Coordinates(i, j);
                    //diamo un peso alla posizione attuale del re
                    this.positions_sum += this.pesi_posizione_re[i][j]*KING_POSITION_WEIGHT;
                } else if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())){
                    pawnsBlack++;
                    if (isThreateningWhitePieces(i, j)) {
                        this.threats++;
                    }
                }
            }
        }

        whiteBestPositions = (double) (getNumberOnBestPositions() / this.bestPositions.length);
        if (this.kingCoordinate == null){
            return LOOSE;
        } else if (escapes.contains(state.getBox(kingCoordinate.getX(), kingCoordinate.getY()))) {
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

    private int getNumberOnBestPositions(){

        int num = 0;

        if (state.getNumberOf(State.Pawn.WHITE) >= 8 - THRESHOLD_BEST){
            for(int[] pos: bestPositions){
                if(state.getPawn(pos[0],pos[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    num++;
                }
            }
        }

        return num;
    }

    //TODO: da guardare ultima funzione righeColonneStrategicheLibere se ci serve

    /*
    *
    * Scansiono la board in cerca di pedine nere "minacciose"
    *
    * minacciosa = pedina nera situata di fianco ad una pedina bianca o al re
    *
    * scopo: fare in modo che l'IA tenga in considerazione la situazione di tutti i pezzi bianchi in modo tale che,
    * nel caso in cui non ci siano mosse migliori, muova le pedine bianche in posizioni più sicure (riducendo il
    * numero di threateningPawns) per evitare che vengano mangiate a caso
    *
    */

    private boolean isThreateningWhitePieces(int x, int y) {
        boolean threatensWhite = false; //a default, una pedina nera non è mai minacciosa

        // Controlla se ci sono pedine bianche nelle posizioni adiacenti
        if (x > 0 && state.getPawn(x - 1, y).equalsPawn(State.Pawn.WHITE.toString())) {
            threatensWhite = true;
        } else if (x < state.getBoard().length - 1 && state.getPawn(x + 1, y).equalsPawn(State.Pawn.WHITE.toString())) {
            threatensWhite = true;
        } else if (y > 0 && state.getPawn(x, y - 1).equalsPawn(State.Pawn.WHITE.toString())) {
            threatensWhite = true;
        } else if (y < state.getBoard().length - 1 && state.getPawn(x, y + 1).equalsPawn(State.Pawn.WHITE.toString())) {
            threatensWhite = true;
        }

        return threatensWhite;
    }

    /*
    * Per ogni pedina bianca guardo se può effettuare una cattura. Questo si verifica se la pedina bianca può muoversi
    * in una direzione in cui c'è una pedina nera e adiacente a tale pedine nera ce n'è un'altra bianca. In tutti gli
    * altri casi non può catturare.
    *
     */

    private double evaluateCapturingMoves(int x, int y) {
        double capturingMovesScore = 0;

        // Valuta la possibilità di cattura in tutte e quattro le direzioni, ma appena trovo una cattura possibile mi
        // fermo. Non mi interessa sapere se ce ne sono altre in altre direzioni, in quanto ne ho già trovata una.
        capturingMovesScore = evaluateCaptureInDirection(x, y, 0, -1); // Sopra
        if (capturingMovesScore == CAPTURE_WEIGHT) {
            return capturingMovesScore;
        }

        capturingMovesScore = evaluateCaptureInDirection(x, y, 0, 1);  // Sotto
        if (capturingMovesScore == CAPTURE_WEIGHT) {
            return capturingMovesScore;
        }

        capturingMovesScore = evaluateCaptureInDirection(x, y, -1, 0); // Sinistra
        if (capturingMovesScore == CAPTURE_WEIGHT) {
            return capturingMovesScore;
        }

        capturingMovesScore = evaluateCaptureInDirection(x, y, 1, 0);  // Destra
        if (capturingMovesScore == CAPTURE_WEIGHT) {
            return capturingMovesScore;
        }

        return capturingMovesScore;
    }

    private double evaluateCaptureInDirection(int x, int y, int dx, int dy) {
        double directionScore = 0;

        // mi muovo di 1 casella nella direzione indicata
        int currentX = x + dx;
        int currentY = y + dy;
        int consecutiveBlacks = 0;
        boolean foundOpponent = false;
        while (isValidPosition(currentX, currentY)) {
            if(camps.contains(state.getBox(x, y))) {
                break;
            } else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.EMPTY.toString()) && !foundOpponent) {
                // La casella è vuota e non ho ancora trovato una pedina nera, quindi continuo la ricerca nella stessa direzione
                currentX += dx;
                currentY += dy;
                continue;
            } else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.BLACK.toString()) && consecutiveBlacks==0) {
                foundOpponent = true;
                consecutiveBlacks++;
            } else if(state.getPawn(currentX, currentY).equalsPawn(State.Pawn.WHITE.toString()) && !foundOpponent){
                // La direzione è occupata da un altro bianco, quindi non è possibile catturare
                break;
            } else if (foundOpponent && state.getPawn(currentX, currentY).equalsPawn(State.Pawn.WHITE.toString())) {
                // Se trova una pedina bianca dopo aver trovato la pedina nera avversaria, la mossa può catturare
                directionScore = CAPTURE_WEIGHT;
                break;
            } else if (foundOpponent && consecutiveBlacks > 0) {
                // Non è presente una pedina bianca opposta oppure ci sono più pedine nere consecutive, quindi non è possibile catturare
                break;
            }
            //mi muovo di un'altra casella nella direzione indicata
            currentX += dx;
            currentY += dy;
        }

        return directionScore;
    }


    private boolean isValidPosition(int x, int y) {
        // Verifica se la posizione è all'interno del tabellone
        return x >= 0 && x < state.getBoard().length-1 && y >= 0 && y < state.getBoard().length-1;
    }

}