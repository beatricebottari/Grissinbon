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
    private double capture;

    // Weights
    private double BLACK_WEIGHT = 6.0;
    private double WHITE_WEIGHT = 12.0;
    private double FREE_WAY_FOR_KING = 20.0;    // -free_way_for_king --> +rhombus ratio or similar
    private double KING_BONUS = 7.0 ;
    private double ENCIRCLE_WEIGHT = 900;
    private double CAPTURE_WEIGHT = 2.0;


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
        result += (8-this.pawnsWHITE)*this.WHITE_WEIGHT;
        result -= this.freeWayForKing*this.FREE_WAY_FOR_KING;

        double encircleRatio = this.ENCIRCLE_WEIGHT/this.totalDistanceFromBlackOrSimilar;
        result += encircleRatio;

        result += (1.0/this.blackNeedToEatKing)*(encircleRatio);
        result += capture;

        return result;
    }

    private void resetFields() {
        this.kingPosition = null;
        this.pawnsBLACK = 0;        // val max = 16
        this.pawnsWHITE = 0;        // val max = 8
        this.blackAdjacentKing = 0;     // val max = 4
        blackNeedToEatKing = 0;         // val max = 4
        this.freeWayForKing = 0;    // val max = 4
        this.capture = 0;
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
                    // Controlla se questa pedina può catturare una pedina nera avversaria
                    capture = capture + evaluateCapturingMoves(i, j);
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
        if(escapes.contains(state.getBox(this.kingPosition[0], this.kingPosition[1])))
                return true;
        return false;
    }
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
        int consecutiveWhite = 0;
        boolean foundOpponent = false;
        boolean foundKing = false;
        int[] whereKing = null;

        while (isValidPosition(currentX, currentY)) {

                //se non sono in un campo e trovo un campo non posso entrarci
                if (camps.contains(state.getBox(currentX, currentY)) && !camps.contains(state.getBox(x, y))) {
                    break;
                } // La casella è vuota e non ho ancora trovato una pedina bianca, quindi continuo la ricerca nella stessa direzione
                else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.EMPTY.toString()) && !foundOpponent) {
                    currentX += dx;
                    currentY += dy;
                    continue;
                } // la casella è bianca ed è il primo bianco che incontro
                else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.WHITE.toString()) && consecutiveWhite == 0) {
                    foundOpponent = true;
                    consecutiveWhite++;
                } // la casella è il king ed è il primo bianco che incontro
                else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.KING.toString()) && consecutiveWhite == 0) {
                    foundOpponent = true;
                    foundKing = true;
                    whereKing = new int[]{currentX,currentY};
                    consecutiveWhite++;
                } // La direzione è occupata da un altro nero, quindi non è possibile catturare
                else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.BLACK.toString()) && !foundOpponent) {
                    break;
                } //chi e' l'avversario?
                else if (foundOpponent) {
                    if(foundKing) {
                        if(camps.contains(state.getBox(currentX, currentY))){  // e c'è un campo, catturo
                            directionScore = CAPTURE_WEIGHT;
                            break;
                        } else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.BLACK.toString()) ||
                                state.getPawn(currentX, currentY).equalsPawn(State.Pawn.THRONE.toString())) {
                            // controllo che nella direzione opposta ho altre due pedine nere o una nera e il trono per catturare
                            int oppositeDirection = checkOppositeDirection(whereKing[0], whereKing[1], dx, dy);
                            if (oppositeDirection == 2) {
                                directionScore = CAPTURE_WEIGHT;
                                break;
                            }
                        }
                    } // è un bianco, quindi basta che trovo un altro nero o un campo o il trono
                    else if (state.getPawn(currentX, currentY).equalsPawn(State.Pawn.BLACK.toString()) ||
                            state.getPawn(currentX, currentY).equalsPawn(State.Pawn.THRONE.toString()) ||
                            camps.contains(state.getBox(currentX, currentY))) {
                        // Se trova una pedina nera o simili dopo aver trovato la pedina bianca avversaria, la mossa può catturare
                        directionScore = CAPTURE_WEIGHT;
                        break;
                    }
                } else if (foundOpponent && consecutiveWhite > 0) {
                    // Non è presente una pedina nera opposta oppure ci sono più pedine bianche consecutive, quindi non è possibile catturare
                    break;
                }

                //mi muovo di un'altra casella nella direzione indicata
                currentX += dx;
                currentY += dy;
            }

        return directionScore;
    }

    private int checkOppositeDirection(int whereKingX, int whereKingY, int dx, int dy) {
        int res = 0;
        if(dx != 0) { // controllo dy
            if(state.getPawn(whereKingX, whereKingY+1).equalsPawn(State.Pawn.BLACK.toString()) ||
                    state.getPawn(whereKingX, whereKingY+1).equalsPawn(State.Pawn.THRONE.toString())) {
                res++;
            }
            if (state.getPawn(whereKingX, whereKingY-1).equalsPawn(State.Pawn.BLACK.toString()) ||
                    state.getPawn(whereKingX, whereKingY-1).equalsPawn(State.Pawn.THRONE.toString())) {
                res++;
            }
        } else { // controllo dx
            if(state.getPawn(whereKingX+1, whereKingY).equalsPawn(State.Pawn.BLACK.toString()) ||
                    state.getPawn(whereKingX+1, whereKingY).equalsPawn(State.Pawn.THRONE.toString())) {
                res++;
            }
            if (state.getPawn(whereKingX-1, whereKingY).equalsPawn(State.Pawn.BLACK.toString()) ||
                    state.getPawn(whereKingX-1, whereKingY).equalsPawn(State.Pawn.THRONE.toString())) {
                res++;
            }
        }
        return res;
    }

    private boolean isValidPosition(int x, int y) {
        // Verifica se la posizione è all'interno del tabellone
        return x >= 0 && x < state.getBoard().length-1 && y >= 0 && y < state.getBoard().length-1;
    }
}

