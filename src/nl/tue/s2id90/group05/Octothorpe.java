package nl.tue.s2id90.group05;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class Octothorpe  extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;
    
    // Custom variables for our draughts player
    // Boolean variable for storing whether our player is white or not
    // i.e: isAIWhitePlayer = true (program plays with white pieces)
    private boolean isAIWhitePlayer;
    
    private int startDepth;
    
    private int kingValue = 10;
    private int singleCaptureValue = 1;
    private int doubleCaptureValue = 5;
    private int loseValue = -10;
    private int[] POSITION_MATRIX = {0, 
        9, 9, 9, 9, 9, 
        9, 8, 8, 8, 8, 
        7, 7, 7, 7, 8, 
        7, 6, 6, 6, 6, 
        5, 5, 5, 5, 6, 
        5, 4, 4, 4, 4, 
        3, 3, 3, 3, 4, 
        3, 2, 2, 2, 2, 
        1, 1, 1, 1, 2, 
        3, 3, 3, 3, 3};

    public Octothorpe(int maxSearchDepth) {
        super("octothorpe.png"); // ToDo: replace with your own icon
        this.startDepth = maxSearchDepth;
    }
    
    @Override public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        
        // Determine the color of the player before the game starts
        if (!gameStarted(s)) {
            isAIWhitePlayer = s.isWhiteToMove();
        }
        
        // Reset the depth to the initial starting depth (iterative deepening)
        maxSearchDepth = this.startDepth;
        
        try {
            while (true) {
                // compute bestMove and bestValue in a call to alphabeta
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, 1);
                
                // store the bestMove found uptill now
                // NB this is not done in case of an AIStoppedException in alphaBeat()
                bestMove  = node.getBestMove();

                // print the results for debugging reasons
                System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n", 
                    this.getClass().getSimpleName(),maxSearchDepth, bestMove, bestValue
                );
                
                maxSearchDepth += 1;
            }
        } catch (AIStoppedException ex) {  /* nothing to do */  }
        
        if (bestMove == null) {
            System.err.println("no valid move found!");
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    } 

    /** This method's return value is displayed in the AICompetition GUI.
     * 
     * @return the value for the draughts state s as it is computed in a call to getMove(s). 
     */
    @Override public Integer getValue() { 
       return bestValue;
    }

    /** Tries to make alphabeta search stop. Search should be implemented such that it
     * throws an AIStoppedException when boolean stopped is set to true;
    **/
    @Override public void stop() {
       stopped = true; 
    }
    
    /** returns random valid move in state s, or null if no moves exist. */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        // Collections.shuffle(moves);
        // return moves.isEmpty()? null : moves.get(0);
        return moves.get(0);
    }
    
    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        
        // If a leaf node is reached and there is not a valid capture as a next move
        // program returns the evaluation for the node
        if (leafNodeReached(node, depth) && !hasValidCapture(state.getMoves())) {
            return evaluate(state);
        }
        
        if (this.isAIWhitePlayer == node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha, beta, depth);
        } else  {
            return alphaBetaMin(node, alpha, beta, depth);
        }
    }
    
    /** Does an alphabeta computation with the given alpha and beta
     * where the player that is to move in node is the minimizing player.
     * 
     * <p>Typical pieces of code used in this method are:
     *     <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     *          <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     *          <li><code>node.setBestMove(bestMove);</code></li>
     *          <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     *     </ul>
     * </p>
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth  maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been set to true.
     */
     int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        
        Move bestMove = state.getMoves().get(0);
        int value = 0;
        List<Move> moves = state.getMoves();
        for (Move m : moves) {
            state.doMove(m);
            
            DraughtsNode newNode = new DraughtsNode(state);
            //beta = Math.min(beta, alphaBeta(newNode, alpha, beta, depth - 1));
            value = alphaBeta(newNode, alpha, beta, depth + 1);
            if (beta > value) {
                beta = value;
                bestMove = m;
            }
            if (beta <= alpha) {
                state.undoMove(m);
                return alpha;
            }
            beta = Math.min(beta, value);
            
            state.undoMove(m);
        }
        node.setBestMove(bestMove);
        return beta;
     }
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        
        Move bestMove = state.getMoves().get(0);
        int value = 0;
        List<Move> moves = state.getMoves();
        for (Move m : moves) {
            state.doMove(m);
            
            DraughtsNode newNode = new DraughtsNode(state);
            //alpha = Math.max(alpha, alphaBeta(newNode, alpha, beta, depth - 1));
            value = alphaBeta(newNode, alpha, beta, depth + 1);
            if (alpha < value) {
                alpha = value;
                bestMove = m;
            }
            if (alpha >= beta) {
                state.undoMove(m);
                return beta;
            }
            alpha = Math.max(alpha, value);
            
            state.undoMove(m);
        }
        
        node.setBestMove(bestMove);
        return alpha;
    }

    /** A method that evaluates the given state. */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) {
        // Variables for storing the cost of the evaluation function
        int evaluate = 0;
        
        // Evaluation function for the number of pieces on the board
        // Get the value for the number of pieces the AI has minus the number
        // of pieces for the other player
        evaluate += getNumPieces(state);
        
        // Evaluation function for the number of draughts that can be captured
        // minus number of draughts that will be lost
        evaluate += getNumCaptures(state);
        
        return evaluate;
    }
    
    public int getNumPieces(DraughtsState state) {
        // Variables for storing the cost of the evaluation function
        int evaluate = 0;
        // Obtain pieces array
        int[] pieces = state.getPieces();
        
        // Compute correct sign depending on player's color
        int sign = this.isAIWhitePlayer ? 1 : -1;
        
        // Compute a value for this state by
        // comparing p[i] with WHITEPIECE (1), WHITEKING (3)
        // or BLACKPIECE (2), BLACKKING (4)
        for (int i = 1; i <= 50; i ++) {
            switch (pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    evaluate += POSITION_MATRIX[i] * sign;
                    break;
                case DraughtsState.BLACKPIECE:
                    evaluate -= POSITION_MATRIX[51 - i] * sign;
                    break;
                case DraughtsState.WHITEKING:
                    evaluate += kingValue * sign;
                    break;
                case DraughtsState.BLACKKING:
                    evaluate -= kingValue * sign;
                    break;
            }
        }
        
        // Return evaluation for white or black (depending on who needs to move)
        return evaluate;
    }
    
    public int getNumOpponentPieces(DraughtsState state) {
        // Variables for storing white/black pieces
        int whitePieces = 0;
        int blackPieces = 0;
        // Obtain pieces array
        int[] pieces = state.getPieces();
        
        for (int p : pieces) {
            if (p == DraughtsState.WHITEPIECE || p == DraughtsState.WHITEKING) {
                whitePieces += 1;
            }
            if (p == DraughtsState.BLACKPIECE || p == DraughtsState.BLACKKING) {
                blackPieces += 1;
            }
        }
        
        // Return number of pieces of OPPONENT
        if (this.isAIWhitePlayer) {
            return blackPieces;
        } else {
            return whitePieces;
        }
    }
    
    // Function that returns number of captured draughts minus
    // number of lost draughts for the player that needs to move
    public int getNumCaptures(DraughtsState state) {
        // Variables for storing the cost of the evaluation function
        int evaluate = 0;
        // Obtain pieces array
        int[] pieces = state.getPieces();
        // Variables for storing players pieces values;
        int playerPiece, playerKing;
        int enemyPiece, enemyKing;
        int sign;
        
        if (this.isAIWhitePlayer) {
            playerPiece = DraughtsState.WHITEPIECE;
            playerKing = DraughtsState.WHITEKING;
            enemyPiece = DraughtsState.BLACKPIECE;
            enemyKing = DraughtsState.BLACKKING;
            sign = 1;
        } else {
            playerPiece = DraughtsState.BLACKPIECE;
            playerKing = DraughtsState.BLACKKING;
            enemyPiece = DraughtsState.WHITEPIECE;
            enemyKing = DraughtsState.WHITEKING;
            sign = -1;
        }
        
        for (int i = 1; i <= 50; i++) {
            // Assume player piece is at position piece[i]. Now we check:
            // piece[i-6]: top-left
            // piece[i-5]: top-right
            // piece[i+4]: bottom-left
            // piece[i+5]: bottom-right
            if (pieces[i] == playerPiece || pieces[i] == playerKing) {
                // player is white/black and we check for attacking pieces
                int[] emptyPosition = {5, 4, -6, -5};
                int[] enemyPosition = {-6, -5, 5, 4};
                evaluate -= getSingleCaptures(i, pieces, sign, emptyPosition, enemyPosition, enemyPiece, enemyKing);
                
                // player is white/black and we check for pieces that can be captured
                int[] newEmptyPosition = {-11, -9, 11, 9};
                int[] newEnemyPosition = {-5, -4, 6, 5};
                evaluate += getSingleCaptures(i, pieces, sign, newEmptyPosition, newEnemyPosition, enemyPiece, enemyKing);
                
                // player is white/black and we check for pieces that can be captured
                // double capture move (weights more) x-pattern
                int[] emptyPosition01 = {9, 11, -9, -11};
                int[] enemyPosition01 = {5, 6, -4, -5};
                int[] emptyPosition02 = {-2, 2, -3, -2};
                int[] enemyPosition02 = {4, 7, -3, -6};
                
                // player is white/black and we check for pieces that can be captured
                // double capture move (weights more) /-pattern
                int[] emptyPosition03 = {-9, -11, 11, 9};
                int[] enemyPosition03 = {-4, -5, 6, 5};
                int[] emptyPosition04 = {-18, -22, 22, 18};
                int[] enemyPosition04 = {-13, -16, 17, 14};
            }
        }
        
        return evaluate;
    }
    
    // Function for calculating captures based on input arrays
    public int getSingleCaptures(int i, int[] pieces, int sign, int[] emptyPosition, int[] enemyPosition, int enemyPiece, int enemyKing) {
        // Variables for storing the cost of the evaluation function
        int evaluate = 0;
        for (int j = 0; j < 4; j ++) {
            if (validPosition(i, emptyPosition[j]) && validPosition(i, enemyPosition[j])) {
                if (pieces[i + emptyPosition[j]] == DraughtsState.EMPTY) {
                    if (pieces[i + enemyPosition[j]] == enemyPiece || pieces[i + enemyPosition[j]] == enemyKing) {
                        evaluate += singleCaptureValue * sign;
                    }
                }
            }
        }
        return evaluate;
    }
    
    public int getDoubleCaptures(int i, int[] pieces, int sign, int[] emptyPosition1, int[] enemyPosition1, int[] emptyPosition2, int[] enemyPosition2, int enemyPiece, int enemyKing) {
        // Variables for storing the cost of the evaluation function
        int evaluate = 0;
        for (int j = 0; j < 4; j ++) {
            if (validPosition(i, emptyPosition1[j]) && validPosition(i, enemyPosition1[j]) &&
                validPosition(i, emptyPosition2[j]) && validPosition(i, enemyPosition2[j])) {
                if (pieces[i + emptyPosition1[j]] == DraughtsState.EMPTY && pieces[i + emptyPosition2[j]] == DraughtsState.EMPTY) {
                    if ((pieces[i + enemyPosition1[j]] == enemyPiece || pieces[i + enemyPosition1[j]] == enemyKing) && 
                        (pieces[i + enemyPosition2[j]] == enemyPiece || pieces[i + enemyPosition2[j]] == enemyKing)) {
                        evaluate += doubleCaptureValue * sign;
                    }
                }
            }
        }
        return evaluate;
    }
    
    // Function for checking if a position is a valid 10x10 draughts position
    public boolean validPosition(int i, int j) {
        return 1 <= i+j && i+j <= 50;
    }
    
    // Function for determining whether the game has started or not
    public boolean gameStarted(DraughtsState state) {
        // Obtain pieces array
        int[] pieces = state.getPieces();
        
        // The board is in the initial state if the game did not start
        // hence we can get the correct color for the isAIWhitePlayer variable
        for(int i = 1; i <= 20; i++) {
            if (pieces[i] != DraughtsState.BLACKPIECE) {
                // YES, game did start
                return true;
            }
        }
        
        // NO, game did NOT start
        return false;
    }
    
    // Function for determining whether a leaf node has been reached
    // or whether the maximum search depth has been reached
    // important: depth starts from max search depth and decreases in alphaBeta
    public boolean leafNodeReached(DraughtsNode node, int depth) {
        return depth >= this.maxSearchDepth || node.getState().isEndState();
    }
    
    // Function for determining whether the player can capture a piece
    // important: all possible moves are stored in the moves list and passed as a parameter
    public boolean hasValidCapture(List<Move> moves) {
        // Iterate over all the possible moves
        for (Move move : moves) {
            if (move.isCapture()) {
                return true;
            }
        }
        return false;
    }
}
//