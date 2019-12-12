package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentLegals) {
        final List<Move> kingCastles=new ArrayList<>();
        //If the player black king is not in check and the king is the first move, then proceed
        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            //If the 62 and 63 tile is not occupied (this is for the white king side castle move), then proceed
            if(!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()){
                //the rook should be on tile 63
                final Tile rookTile= this.board.getTile(7);
                //if it is rook's first move as well, then proceed
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    //if there are no opponent legal attacks on these tiles, then proceed
                    if(Player.calculateAttacksOnTile(5, opponentLegals).isEmpty()&&
                            Player.calculateAttacksOnTile(6, opponentLegals).isEmpty()&&
                            rookTile.getPiece().getPieceType().isRook()){
                        //make the castling move
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing,
                                                                    6,
                                                                    (Rook)rookTile.getPiece(),
                                                                    rookTile.getTileCoordinate(),
                                                                    5));
                    }

                }
            }
            //this is for the queen side castling move
            if(!this.board.getTile(1).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()){
                final Tile rookTile=this.board.getTile(0);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(2, opponentLegals).isEmpty()&&
                            Player.calculateAttacksOnTile(3, opponentLegals).isEmpty()&&
                            rookTile.getPiece().getPieceType().isRook()){
                        kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing,
                                2,
                                (Rook)rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                3));

                    }
//&&
//                            Player.calculateAttacksOnTile(57, opponentLegals).isEmpty()
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
}
