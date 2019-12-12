package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player{
    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentLegals) {
        final List<Move> kingCastles=new ArrayList<>();
        //If the player white king is not in check and the king is the first move, then proceed
        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            //If the 62 and 63 tile is not occupied (this is for the white king side castle move), then proceed
            if(!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()){
                //the rook should be on tile 63
                final Tile rookTile= this.board.getTile(63);
                //if it is rook's first move as well, then proceed
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    //if there are no opponent legal attacks on these tiles, then proceed
                    if(Player.calculateAttacksOnTile(61, opponentLegals).isEmpty()&&
                       Player.calculateAttacksOnTile(62, opponentLegals).isEmpty()&&
                       rookTile.getPiece().getPieceType().isRook()){
                        //make the castling move by moving the king to tile 62 and rook to tile 61
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing,
                                                                   62,
                                                                   (Rook)rookTile.getPiece(),
                                                                    rookTile.getTileCoordinate(),
                                                                   61));
                    }
                }
            }
            //this is for the queen side castling move
            if(!this.board.getTile(59).isTileOccupied() &&
               !this.board.getTile(58).isTileOccupied() &&
               !this.board.getTile(57).isTileOccupied()){
                final Tile rookTile=this.board.getTile(56);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                   if(Player.calculateAttacksOnTile(58, opponentLegals).isEmpty()&&
                       Player.calculateAttacksOnTile(59, opponentLegals).isEmpty()&&
                      rookTile.getPiece().getPieceType().isRook()){
                       kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing,
                               58,
                               (Rook)rookTile.getPiece(),
                               rookTile.getTileCoordinate(),
                               59));

                    }
//&&
//                       Player.calculateAttacksOnTile(57, opponentLegals).isEmpty()
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
}
