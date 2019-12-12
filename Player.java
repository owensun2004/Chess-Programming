package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {


    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;
    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves){
        this.board = board;
        this.playerKing=establishKing();
        this.legalMoves=ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck=!Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();

    }
    public King getPlayerKing(){
        return this.playerKing;
    }
    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }
    //if the opponent player makes a move, then these codes are gonna check if the piece's next move can overlap with the king's tile
    //if it does overlap, then it's a check
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves=new ArrayList<>();
        for(final Move move : moves){
            if(piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private King establishKing() {
        for(final Piece piece : getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board!!");
    }
    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }
    //if the king is in check and has no escape routes, then it's a check mate
    public boolean isInCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }
    public boolean isInStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }
//In order to calculate if the king can escape, the computer will go through every player's legal moves and make them in an imaginary board
    protected boolean hasEscapeMoves() {
        for(final Move move: this.legalMoves){
            final MoveTransition transition=makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }


    public boolean isCastled(){
        return false;
    }
    //if the player's move is illegal, then it will not make a transition, and will return the same board
    //if the player's move is legal, then it will execute the move and create a new board
    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
           return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard=move.execute();
        //You can't make a move with your king into a check position. This is going to check this scenario and make sure that it doesn't happen
        //by returning the same board if the opponent can attack your king after you make a king move
        final Collection<Move> kingAttacks= Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves());
        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
