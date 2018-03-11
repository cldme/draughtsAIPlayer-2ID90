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
    
    private int kingValue = 100;
    private int loseValue = 50;
    private int formationValue = 20;
    private int riskyValue = 50;
    private final int[] POSITION_MATRIX = {0,
        60, 50, 50, 60, 60,
        60, 50, 40, 50, 60,
        50, 40, 40, 50, 60,
        50, 40, 30, 40, 50,
        40, 30, 30, 40, 50,
        40, 30, 20, 30, 40,
        30, 20, 20, 30, 40,
        30, 20, 20, 20, 30,
        20, 10, 10, 20, 30,
        20, 10, 10, 10, 20};

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
        // Variable for storing neighbour value
        int neighbours = 0;
        // Array for storing board pieces
        int[] pieces = state.getPieces();
        
        for (int p = 1; p <= 50; p ++) {
            int pieceRow = getPieceRow(p);
            int pieceCol = getPieceColumn(p);
            
            neighbours = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 && j != 0) {
                        int neighbourIndex = getNeighbourIndex(p, i, j);
                        if (validPosition(neighbourIndex)) {
                            if (pieces[p] == DraughtsState.BLACKPIECE || pieces[p] == DraughtsState.BLACKKING) {
                                if (pieces[neighbourIndex] == DraughtsState.BLACKPIECE || pieces[neighbourIndex] == DraughtsState.BLACKKING) {
                                    neighbours += 1;
                                }
                            } else if (pieces[neighbourIndex] == DraughtsState.WHITEPIECE || pieces[neighbourIndex] == DraughtsState.WHITEKING) {
                                neighbours += 1;
                            }
                        }
                    }
                }
            }
            
            if (pieces[p] == DraughtsState.WHITEPIECE) {
                evaluate += 10;
                evaluate += getKingDistance(p, true);
                evaluate += getPieceRowColScore(pieceRow, pieceCol);
                evaluate += neighbours * 3;
            }
            
            if (pieces[p] == DraughtsState.BLACKPIECE) {
                evaluate -= 10;
                evaluate -= getKingDistance(p, false);
                evaluate -= getPieceRowColScore(pieceRow, pieceCol);
                evaluate -= neighbours * 3;
            }
            
            if (pieces[p] == DraughtsState.WHITEKING) {
                evaluate += 30;
                evaluate += neighbours * 3;
            }
            
            if (pieces[p] == DraughtsState.BLACKKING) {
                evaluate -= 30;
                evaluate -= neighbours * 3;
            }
        }
        
        if (this.isAIWhitePlayer) {
            return evaluate;
        } else {
            return evaluate * (-1);
        }
    }
    
    // Function for checking if a position is a valid 10x10 draughts position
    public boolean validPosition(int i) {
        return 1 <= i && i <= 50;
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
    
    // Function returns the row on which the piece is placed
    // i.e: by checking division modulo 5
    public int getPieceRow(int n) {
        return (n / 5) + 1;
    }
    
    // Function returns the column on which the piece is placed
    // i.e: by checking division modulo 10
    public int getPieceColumn(int n) {
        int[] column = {6, 1, 7, 2, 8, 3, 9, 4, 0, 5};
        int digit = n % 10;
        for(int i = 0; i < column.length; i ++) {
            if (column[i] == digit) {
                return i + 1;
            }
        }
        return 0;
    }
    
    // Function returns score for piece position on the board
    public int getPieceRowColScore(int row, int col) {
        int evaluate = 0;
        if (row == 1 || row == 10) {
            evaluate += 5;
        }
        if (col != 1 && col != 2 && col !=9 && col != 10) {
            evaluate += 1;
        }
        return evaluate;
    }
    
    // Return index of the neighbour of the piece
    public int getNeighbourIndex(int index, int i, int j) {
        // Variable for storing number of neighbour friends
        int evaluate = 0;
        int row = getPieceRow(index);
        
        if (row % 2 == 0) {
            if (i == 1) {
                if (j == 1) {
                    return index + 6;
                } else {
                    return index - 4;
                }
            } else {
                if (j == 1) {
                    return index + 5;
                } else {
                    return index - 5;
                }
            }
        } else {
            if (i == 1) {
                if (j == 1) {
                    return index + 5;
                } else {
                    return index - 5;
                }
            } else {
                if (j == 1) {
                    return index + 4;
                } else {
                    return index - 6;
                }
            }
        }
    }
    
    public int getKingDistance(int index, boolean white) {
        // Variable to store distance to king piece
        int distance;
        
        if (white) {
            distance = ((50 - index) / 5) - 6;
        } else {
            distance = ((index - 1) / 5) - 6;
        }
        
        return (distance > 0) ? distance : 0;
    }
}
//