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
public class MyDraughtsPlayer  extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;
    
    // Custom variables for our draughts player
    private int kingValue = 10;
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

    public MyDraughtsPlayer(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
    }
    
    @Override public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);
            
            // store the bestMove found uptill now
            // NB this is not done in case of an AIStoppedException in alphaBeat()
            bestMove  = node.getBestMove();
            
            // print the results for debugging reasons
            System.err.format(
                "%s: depth= %2d, best move = %5s, value=%d\n", 
                this.getClass().getSimpleName(),maxSearchDepth, bestMove, bestValue
            );
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
        Collections.shuffle(moves);
        return moves.isEmpty()? null : moves.get(0);
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
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
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
        
        // Checking whether depth limit is reached
        if (depth <= 0 || depth - numOpponentPieces(state) <= 0) {
            return evaluate(state);
        }
        
        Move bestMove = state.getMoves().get(0);
        int value = 0;
        List<Move> moves = state.getMoves();
        for (Move m : moves) {
            state.doMove(m);
            
            DraughtsNode newNode = new DraughtsNode(state);
            //beta = Math.min(beta, alphaBeta(newNode, alpha, beta, depth - 1));
            value = alphaBeta(newNode, alpha, beta, depth - 1);
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
        
        // Checking whether depth limit is reached
        if (depth <= 0) {
            return evaluate(state);
        }
        
        Move bestMove = state.getMoves().get(0);
        int value = 0;
        List<Move> moves = state.getMoves();
        for (Move m : moves) {
            state.doMove(m);
            
            DraughtsNode newNode = new DraughtsNode(state);
            //alpha = Math.max(alpha, alphaBeta(newNode, alpha, beta, depth - 1));
            value = alphaBeta(newNode, alpha, beta, depth - 1);
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
        // Obtain pieces array
        int[] pieces = state.getPieces();
        
        // Compute correct sign depending on player's color
        int sign = state.isWhiteToMove() ? -1 : 1;
        
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
    
    int numOpponentPieces(DraughtsState state) {
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
        if (state.isWhiteToMove()) {
            return blackPieces;
        } else {
            return whitePieces;
        }
    }
}
