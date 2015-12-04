
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class ChessMate {
	
	static int DEPTH = 3;
	
	//Global variables
	public static PieceType[][] board = new PieceType[8][8];
	public static long startTime = 0;
	public static int moves = 0;
	
	static boolean whiteTurn = true;
	static int whiteKingPos = 60;
	static int blackKingPos = 4;
	
	public static void main(String[] args){
		Random r = new Random();
		initBoard();
		printBoard();
		
		Scanner scanner = new Scanner(System.in);
		
		while(true){
			
			
			if(legalMoves(true,true).size() == 0){
				if(kingInCheck(true)){
					System.out.println("CHECKMATE - BLACK WINS");
				}else{
					System.out.println("STALEMATE");
				}
				return;
			}
		
			
			for(Move move : legalMoves(true,true)){
				System.out.print(move.startPos + "->" + move.endPos + "|");
			}
			
			System.out.println("Make a move!!!");
			int startPos = scanner.nextInt();
			int endPos = scanner.nextInt();
			
			if(board[endPos/8][endPos%8] == PieceType.EMPTY){
				makeMove(new Move(startPos, endPos, MoveType.NORMAL, board[endPos/8][endPos%8]));
			}else{
				makeMove(new Move(startPos, endPos, MoveType.CAPTURE, board[endPos/8][endPos%8]));
			}

			moves++;
			printBoard();

			
			whiteTurn = !whiteTurn;
			
			startTime = System.currentTimeMillis();
			Move bestMove = null;
			int bestRating = -1000001;
			List<Move> legalMoves = legalMoves(false,true);
			for(Move move : legalMoves){
				System.out.print(move.startPos + "->" + move.endPos + "|");
			}
			System.out.println("");
			sortMoves(legalMoves);
			for(Move move : legalMoves){
				System.out.print(move.startPos + "->" + move.endPos + "|");
			}
			
			System.out.println("Number of moves:" + legalMoves.size());
			if(legalMoves.size() == 0){
				if(kingInCheck(false)){
					System.out.println("CHECKMATE - WHITE WINS");
				}else{
					System.out.println("STALEMATE");
				}
				return;
			}
			
			for(Move move : legalMoves){
				makeMove(move);
				int rating = (DEPTH%2 == 0) ? -alphaBetaMin(-1000000,1000000, DEPTH) : alphaBetaMin(-1000000,1000000, DEPTH);  //Changed from max to min and added negative
				//rating += Rating.captureRating();
				
				System.out.println("Rating: " + rating);
				if(rating > bestRating){
					bestMove = move;
					bestRating = rating;
				}
				undoMove(move);
			}
			System.out.println("BEST Rating: " + bestRating);
			System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms");
			System.out.println("Moves done: " + moves);
			makeMove(bestMove);
			
			moves++;
			
			moves = 0;
			
			
			printBoard();
			
			whiteTurn = !whiteTurn;
			
			
		}
		
		 
	}
	
	public static void initBoard(){
		//Empty squares
		for(int i = 0; i < 64; i++){
			board[i/8][i%8] = PieceType.EMPTY;
		}
		
		//Black Pieces
		for(int col = 0; col < 8; col++){ 
			board[1][col] = PieceType.BLACK_PAWN;
		}
		board[0][0] = PieceType.BLACK_ROOK;
		board[0][1] = PieceType.BLACK_KNIGHT;
		board[0][2] = PieceType.BLACK_BISHOP;
		board[0][3] = PieceType.BLACK_QUEEN;
		board[0][4] = PieceType.BLACK_KING;
		board[0][5] = PieceType.BLACK_BISHOP;
		board[0][6] = PieceType.BLACK_KNIGHT;
		board[0][7] = PieceType.BLACK_ROOK;
		
		//White Pieces
		for(int col = 0; col < 8; col++){ 
			board[6][col] = PieceType.WHITE_PAWN;
		}
		board[7][0] = PieceType.WHITE_ROOK;
		board[7][1] = PieceType.WHITE_KNIGHT;
		board[7][2] = PieceType.WHITE_BISHOP;
		board[7][3] = PieceType.WHITE_QUEEN;
		board[7][4] = PieceType.WHITE_KING; 
		board[7][5] = PieceType.WHITE_BISHOP;
		board[7][6] = PieceType.WHITE_KNIGHT;
		board[7][7] = PieceType.WHITE_ROOK;
		
	}
	
	public static int alphaBetaMax(int alpha, int beta, int depthleft ) {
		moves++;
		List<Move> moves = legalMoves(false, true);
		
	   if ( depthleft == 0 || moves.size() == 0) return Rating.rating(moves, depthleft);
	   
	   for (Move move : moves) {
		   makeMove(move);
		   int rating = alphaBetaMin( alpha, beta, depthleft - 1 );
		   undoMove(move);
		   if( rating >= beta )
	         return beta;   // fail hard beta-cutoff
		   if( rating > alpha )
			 alpha = rating; // alpha acts like max in MiniMax
	   }
	   return alpha;
	}
		 
	public static int alphaBetaMin( int alpha, int beta, int depthleft ) {
		List<Move> moves = legalMoves(true, true);
		
	   if ( depthleft == 0 || moves.size() == 0) return -Rating.rating(moves, depthleft);
	   
	   for (Move move : moves) { //Move move : moves
		   makeMove(move);
		   int rating = alphaBetaMax( alpha, beta, depthleft - 1 ); 
		   undoMove(move);
		   if( rating <= alpha )
			   return alpha; // fail hard alpha-cutoff
		  if( rating < beta )
		     beta = rating; // beta acts like min in MiniMax
	   }
	   return beta;
	}
	
	public static void sortMoves(List<Move> moves){
		
	    Collections.sort(moves, new Comparator<Move>() {

			@Override
			public int compare(Move m1, Move m2) {
				int rating1 = 0,rating2 = 0;
				
				makeMove(m1);
				rating1 = Rating.rating(null, 1);
				if(m1.moveType != MoveType.NORMAL) rating1 += 100000;
				undoMove(m1);
				makeMove(m2);
				rating2 = Rating.rating(null, 1);
				if(m2.moveType != MoveType.NORMAL) rating2 += 100000;
				undoMove(m2);
				
				return rating2-rating1; 
			}

	    });
	}
	
	public static List<Move> legalMoves(boolean white, boolean checkcheck){
		List<Move> moves = new ArrayList<>();
		for(int i = 0; i < 64; i++){
			if(white){
				switch(board[i/8][i%8]){
				case WHITE_KING:
					moves.addAll(kingMoves(i, checkcheck));
					break;
				case WHITE_QUEEN:
					moves.addAll(queenMoves(i, checkcheck));
					break;
				case WHITE_ROOK:
					moves.addAll(rookMoves(i, checkcheck));
					break;
				case WHITE_KNIGHT:
					moves.addAll(knightMoves(i, checkcheck));;
					break;
				case WHITE_BISHOP:
					moves.addAll(bishopMoves(i, checkcheck));
					break;
				case WHITE_PAWN:
					moves.addAll(pawnMoves(i, checkcheck));;
					break;
				}
			}else{
				switch(board[i/8][i%8]){
				case BLACK_KING:
					moves.addAll(kingMoves(i, checkcheck));
					break;
				case BLACK_QUEEN:
					moves.addAll(queenMoves(i, checkcheck));
					break;
				case BLACK_ROOK:
					moves.addAll(rookMoves(i, checkcheck));
					break;
				case BLACK_KNIGHT:
					moves.addAll(knightMoves(i, checkcheck));
					break;
				case BLACK_BISHOP:
					moves.addAll(bishopMoves(i, checkcheck));
					break;
				case BLACK_PAWN:
					moves.addAll(pawnMoves(i, checkcheck));
					break;
				}
			}
		}
		return moves;
	}
	
	private static List<Move> kingMoves(int pos, boolean checkcheck){
		List<Move> moves = new ArrayList<>();
		boolean white = isWhite(board[pos/8][pos%8]);
		
		for(int row = -1; row <= 1; row++){
			for(int col = -1; col <= 1; col++){
				if(row == 0 && col == 0) continue; //SKIP over current position
				int newRow = (pos/8) + row;
				int newCol = (pos%8) + col;
				if(newRow > 7 || newRow < 0 || newCol > 7 || newCol < 0) continue;  //Skip over moves outside of board.
				
				if(isWhite(board[newRow][newCol]) == white && board[newRow][newCol] != PieceType.EMPTY) continue; //Can't move to squares occupied by own player's piece

				if(board[newRow][newCol] == PieceType.EMPTY){ //No Capture
					Move move = new Move(pos, (newRow*8) + newCol, MoveType.NORMAL, PieceType.EMPTY);
					if(checkcheck){
						makeMove(move);
						if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
						undoMove(move);
					}else{
						moves.add(move);
					}
				}else if(isWhite(board[newRow][newCol]) != white){ //Capture
					Move move = new Move(pos, (newRow*8) + newCol, MoveType.CAPTURE, board[newRow][newCol]);
					if(checkcheck){
						makeMove(move);
						if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
						undoMove(move);
					}else{
						moves.add(move);
					}
				}
				
				
			}
		}
		return moves;
	}
	
	private static List<Move> rookMoves(int pos, boolean checkcheck){
		List<Move> moves = new ArrayList<>();
		boolean white = isWhite(board[pos/8][pos%8]);
		int startRow = pos/8;
		int startCol = pos%8;
		
		for(int i = -1; i < 2; i+=2){ //+1 or -1	
			int m = 1; //Multiplier
			
			//LEFT - RIGHT
			while(startCol + (i*m) >= 0 && startCol + (i*m) < 8){ //While on board
				int newPos = pos + (i*m);
				if(isWhite(board[newPos/8][newPos%8]) == white && board[newPos/8][newPos%8] != PieceType.EMPTY)break; //Can't run into own piece
				
				if(board[newPos/8][newPos%8] == PieceType.EMPTY){ //No Capture
					Move move = new Move(pos, newPos, MoveType.NORMAL, PieceType.EMPTY);
					if(checkcheck){
						makeMove(move);
						if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
						undoMove(move);
					}else{
						moves.add(move);
					}
				}else if(isWhite(board[newPos/8][newPos%8]) != white){ //Capture
					Move move = new Move(pos, newPos, MoveType.CAPTURE, board[newPos/8][newPos%8]);
					if(checkcheck){
						makeMove(move);
						if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
						undoMove(move);
					}else{
						moves.add(move);
					}
				}
				
				if(isWhite(board[newPos/8][newPos%8]) != white && board[newPos/8][newPos%8] != PieceType.EMPTY) break; //Last move was capture - STOP!
				m++;
			}
			m=1;
			//UP - DOWN
			while(startRow + (i*m) >= 0 && startRow + (i*m) < 8){ //While on board
				int newPos = pos + (8*i*m);
				if(isWhite(board[newPos/8][newPos%8]) == white && board[newPos/8][newPos%8] != PieceType.EMPTY)break; //Can't run into own piece
				
				if(board[newPos/8][newPos%8] == PieceType.EMPTY){ //No Capture
					Move move = new Move(pos, newPos, MoveType.NORMAL, PieceType.EMPTY);
					if(checkcheck){
						makeMove(move);
						if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
						undoMove(move);
					}else{
						moves.add(move);
					}
				}else if(isWhite(board[newPos/8][newPos%8]) != white){ //Capture
					Move move = new Move(pos, newPos, MoveType.CAPTURE, board[newPos/8][newPos%8]);
					if(checkcheck){
						makeMove(move);
						if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
						undoMove(move);
					}else{
						moves.add(move);
					}
				}
				
				if(isWhite(board[newPos/8][newPos%8]) != white && board[newPos/8][newPos%8] != PieceType.EMPTY) break; //Last move was capture - STOP!
				m++;
			}
		}
		return moves;
	}
	
	private static List<Move> bishopMoves(int pos, boolean checkcheck){
		List<Move> moves = new ArrayList<>();
		boolean white = isWhite(board[pos/8][pos%8]);
		int startRow = pos/8;
		int startCol = pos%8;
		
		for(int r = -1; r < 2; r+=2){ //+1 or -1
			for(int c = -1; c < 2; c+=2){ //+1 or -1
				int m = 1; //Multiplier
				while(true){ //While inside board //(startRow + (r*m) >= 0 && startRow + (r*m) < 8) && (startRow + (c*m) >= 0 && startRow + (c*m) < 8)
					int newPos = pos + (8*r*m) + (c*m);
					int newRow = startRow + (r*m);
					int newCol = startCol + (c*m);
					
					if(newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) break; //Prevent teleportation
					if(newPos > 63 || newPos < 0) break; //Can't be outside board
					try{
						if(isWhite(board[newPos/8][newPos%8]) == white && board[newPos/8][newPos%8] != PieceType.EMPTY)break; //Can't run into own piece
						
						if(board[newPos/8][newPos%8] == PieceType.EMPTY){ //No Capture
							Move move = new Move(pos, newPos, MoveType.NORMAL, PieceType.EMPTY);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							}
						}else if(isWhite(board[newPos/8][newPos%8]) != white){ //Capture
							Move move = new Move(pos, newPos, MoveType.CAPTURE, board[newPos/8][newPos%8]);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							}
						}
						
						if(isWhite(board[newPos/8][newPos%8]) != white && board[newPos/8][newPos%8] != PieceType.EMPTY) break; //Last move was capture - STOP!
						m++;
					}catch(Exception e){};
				
				}
			}	
		}
		return moves;
	}

	private static List<Move> queenMoves(int pos, boolean checkcheck){
		List<Move> moves = new ArrayList<>();
		
		//Bishop and rook combined
		moves.addAll(bishopMoves(pos, checkcheck));
		moves.addAll(rookMoves(pos, checkcheck));
		
		return moves;
	}
	
	private static List<Move> knightMoves(int pos, boolean checkcheck){
		List<Move> moves = new ArrayList<>();
		boolean white = isWhite(board[pos/8][pos%8]);
		int startRow = pos/8;
		int startCol = pos%8;
		
		for(int r = -1; r <= 1; r+=2){
			for(int c = -1; c <= 1; c+=2){
				try{ //In case move is off board
					if(board[startRow + r][startCol + (2*c)] == PieceType.EMPTY){
						Move move = new Move(pos, pos + (r*8) + (2*c), MoveType.NORMAL, PieceType.EMPTY);
						if(checkcheck){
							makeMove(move);
							if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
							undoMove(move);
						}else{
							moves.add(move);
						}
					}else if(isWhite(board[startRow + r][startCol + (2*c)]) != white){
						Move move = new Move(pos, pos + (r*8) + (2*c), MoveType.CAPTURE, board[startRow + r][startCol + (2*c)]);
						if(checkcheck){
							makeMove(move);
							if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
							undoMove(move);
						}else{
							moves.add(move);
						}
					}
				}catch(Exception e){}
				try{ //In case move is off board
					if(board[startRow + (r*2)][startCol + c] == PieceType.EMPTY){
						Move move = new Move(pos, pos + (r*8*2)+ c, MoveType.NORMAL, PieceType.EMPTY);
						if(checkcheck){
							makeMove(move);
							if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
							undoMove(move);
						}else{
							moves.add(move);
						}
					}else if(isWhite(board[startRow + (r*2)][startCol + c]) != white){
						Move move = new Move(pos, pos + (r*8*2) + c, MoveType.CAPTURE, board[startRow + (2*r)][startCol + c]);
						makeMove(move);
						if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
						undoMove(move);
					}
				}catch(Exception e){}
			}
		}
		return moves;
	}
	
	private static List<Move> pawnMoves(int pos, boolean checkcheck){
		List<Move> moves = new ArrayList<>();
		boolean white = isWhite(board[pos/8][pos%8]);
		int startRow = pos/8;
		int startCol = pos%8;
		
		int direction = white ? -1 : 1;
		
		if(white){
			if(pos > 15){ //No promotion + direction
				try{
					if(board[startRow + direction][startCol] == PieceType.EMPTY){ //Move one forward;
						Move move = new Move(pos, pos+(8*direction), MoveType.NORMAL, PieceType.EMPTY);
						if(checkcheck){
							makeMove(move);
							if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
							undoMove(move);
						}else{
							moves.add(move);
						}
					}
				}catch(Exception e){}
				try{
					if(startRow == 6){ //Move two forward
						if(board[startRow + direction][startCol] == PieceType.EMPTY && board[startRow + direction*2][startCol] == PieceType.EMPTY){
							Move move = new Move(pos, pos+(16*direction), MoveType.NORMAL, PieceType.EMPTY);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							}
						}
					} //Capture
				}catch(Exception e){}
				for(int c = -1; c <= 1; c+=2){
					try{
						if(board[startRow + direction][startCol + c] != PieceType.EMPTY && isWhite(board[startRow + direction][startCol + c]) != white){ //Enemy Piece
							Move move = new Move(pos, pos+(8*direction) + c, MoveType.CAPTURE, board[startRow + direction][startCol + c]);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							};
						}
					}catch(Exception e){}
				}
			}else{ //PROMOTION
				for(int c = -1; c <= 1; c+=2){
					try{
						if(board[startRow + direction][startCol + c] != PieceType.EMPTY && isWhite(board[startRow + direction][startCol + c]) != white){ //Enemy Piece
							Move move = new Move(pos, pos+(8*direction) + c, MoveType.CAPTURE_PROMOTE, board[startRow + direction][startCol + c]);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							};
						}
					}catch(Exception e){}
				}
				try{
					if(board[startRow + direction][startCol] == PieceType.EMPTY){ //Move one forward;
						Move move = new Move(pos, pos+(8*direction), MoveType.PROMOTE, PieceType.EMPTY);
						if(checkcheck){
							makeMove(move);
							if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
							undoMove(move);
						}else{
							moves.add(move);
						}
					}
				}catch(Exception e){}
			}
		}else{
			if(pos < 48){ //No promotion + direction
				try{
					if(board[startRow + direction][startCol] == PieceType.EMPTY){ //Move one forward;
						Move move = new Move(pos, pos+(8*direction), MoveType.NORMAL, PieceType.EMPTY);
						if(checkcheck){
							makeMove(move);
							if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
							undoMove(move);
						}else{
							moves.add(move);
						}
					}
				}catch(Exception e){}
				try{
					if(startRow == 1){ //Move two forward
						if(board[startRow + direction][startCol] == PieceType.EMPTY && board[startRow + direction*2][startCol] == PieceType.EMPTY){
							Move move = new Move(pos, pos+(16*direction), MoveType.NORMAL, PieceType.EMPTY);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							}
						}
					} //Capture
				}catch(Exception e){}
				for(int c = -1; c <= 1; c+=2){
					try{
						if(board[startRow + direction][startCol + c] != PieceType.EMPTY && isWhite(board[startRow + direction][startCol + c]) != white){ //Enemy Piece
							Move move = new Move(pos, pos+(8*direction) + c, MoveType.CAPTURE, board[startRow + direction][startCol + c]);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							};
						}
					}catch(Exception e){}
				}
			}else{ //Promotion
				for(int c = -1; c <= 1; c+=2){
					try{
						if(board[startRow + direction][startCol + c] != PieceType.EMPTY && isWhite(board[startRow + direction][startCol + c]) != white){ //Enemy Piece
							Move move = new Move(pos, pos+(8*direction) + c, MoveType.CAPTURE_PROMOTE, board[startRow + direction][startCol + c]);
							if(checkcheck){
								makeMove(move);
								if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
								undoMove(move);
							}else{
								moves.add(move);
							};
						}
					}catch(Exception e){}
				}
				try{
					if(board[startRow + direction][startCol] == PieceType.EMPTY){ //Move one forward;
						Move move = new Move(pos, pos+(8*direction), MoveType.PROMOTE, PieceType.EMPTY);
						if(checkcheck){
							makeMove(move);
							if(!kingInCheck(white))moves.add(move); //Safe to move to location -> add to possible moves
							undoMove(move);
						}else{
							moves.add(move);
						}
					}
				}catch(Exception e){}
				
			}	
		}	
		return moves;
	}
	
	
	public static void makeMove(Move move){
		//KING POS
		if(board[move.startPos/8][move.startPos%8].toString().contains("KING")){
			if(isWhite(board[move.startPos/8][move.startPos%8])){
				whiteKingPos = move.endPos;
			}else{
				blackKingPos = move.endPos;
			}
		}
		
		if(move.moveType == MoveType.NORMAL || move.moveType == MoveType.CAPTURE){
			board[move.endPos/8][move.endPos%8] = board[move.startPos/8][move.startPos%8];
		}
		if(move.moveType == MoveType.PROMOTE || move.moveType == MoveType.CAPTURE_PROMOTE){
			board[move.endPos/8][move.endPos%8] = isWhite(board[move.startPos/8][move.startPos%8]) ? PieceType.WHITE_QUEEN : PieceType.BLACK_QUEEN;
		}
		
		board[move.startPos/8][move.startPos%8] = PieceType.EMPTY;

	}
	
	public static void undoMove(Move move){
		//KING POS
		if(board[move.endPos/8][move.endPos%8].toString().contains("KING")){
			if(isWhite(board[move.endPos/8][move.endPos%8])){
				whiteKingPos = move.startPos;
			}else{
				blackKingPos = move.startPos;
			}
		}
		board[move.startPos/8][move.startPos%8] = board[move.endPos/8][move.endPos%8];
		
		board[move.endPos/8][move.endPos%8] = PieceType.EMPTY;
		
		if(move.moveType == MoveType.PROMOTE){
			if(move.endPos < 8){
				board[move.startPos/8][move.startPos%8] = PieceType.WHITE_PAWN;
			}else{
				board[move.startPos/8][move.startPos%8] = PieceType.BLACK_PAWN;
			}
		}else if(move.moveType == MoveType.CAPTURE_PROMOTE){
			board[move.endPos/8][move.endPos%8] = move.capture;
			if(move.endPos < 8){
				board[move.startPos/8][move.startPos%8] = PieceType.WHITE_PAWN;
			}else{
				board[move.startPos/8][move.startPos%8] = PieceType.BLACK_PAWN;
			}
		}else if(move.moveType == MoveType.CAPTURE){
			board[move.endPos/8][move.endPos%8] = move.capture;
		} //TODO add other move types

	}
	
	public static boolean kingInCheck(boolean white){
		List<Move> moves = legalMoves(!white, false);
		
		for(Move move : moves){
			if(white){
				if(move.endPos == whiteKingPos) return true;
			}else{
				if(move.endPos == blackKingPos)return true;
			}
		}
		
		return false;
	}
	
	
	public static boolean isWhite(PieceType piece){
		if(piece.toString().contains("WHITE")) return true;
		return false;
	}
	
	public static void printBoard(){
		for(int row = 0; row < 8; row++){
			System.out.println("");
			System.out.print("|");
			for(int col = 0; col < 8; col++){
				switch(board[row][col]){
				case WHITE_KING:
					System.out.print("A|");
					break;
				case WHITE_QUEEN:
					System.out.print("Q|");
					break;
				case WHITE_ROOK:
					System.out.print("R|");
					break;
				case WHITE_KNIGHT:
					System.out.print("K|");
					break;
				case WHITE_BISHOP:
					System.out.print("B|");
					break;
				case WHITE_PAWN:
					System.out.print("P|");
					break;
				case BLACK_KING:
					System.out.print("a|");
					break;
				case BLACK_QUEEN:
					System.out.print("q|");
					break;
				case BLACK_ROOK:
					System.out.print("r|");
					break;
				case BLACK_KNIGHT:
					System.out.print("k|");
					break;
				case BLACK_BISHOP:
					System.out.print("b|");
					break;
				case BLACK_PAWN:
					System.out.print("p|");
					break;
				case EMPTY:
					System.out.print(" |");
				}
				
			}
		}
		System.out.println("");
	}
	
	
}
