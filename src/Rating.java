
import java.util.List;


public class Rating {
	
	//Position boards
	static short[][] pawnBoard = {
		{  0,  0,  0,  0,  0,  0,  0,  0},
		{  0,  0,  0,  0,  0,  0,  0,  0},
		{  0, 10, 15, 20, 20, 15, 10,  0},
		{  5,  5,  5, 25, 25,  5,  5,  5},
		{ 15, 15, 15, 15, 15, 15, 15, 15},
		{ 25, 25, 25, 25, 25, 25, 25, 25},
		{ 50, 50, 50, 50, 50, 50, 50, 50},
		{100,100,100,100,100,100,100,100}};
	
	static short[][] knightBoard = {
		{-50,-40,-30,-30,-30,-30,-40,-50},
        {-40,-20,  0,  0,  0,  0,-20,-40},
        {-40,  0, 15, 15, 15, 15,  0,-40},
        {-30,  5, 15, 20, 20, 15,  5,-30},
        {-30,  0, 15, 20, 20, 15,  0,-30},
        {-30,  5, 10, 15, 15, 10,  5,-30},
        {-40,-20,  0,  5,  5,  0,-20,-40},
        {-50,-40,-30,-30,-30,-30,-40,-50}};
	
    static short rookBoard[][]={
        {-50,-30,-20, 10, 10,-20,-30,-50},
        {  5, 10, 10, 10, 10, 10, 10,  5},
        { -5,  0,  0,  0,  0,  0,  0, -5},
        { -5,  0,  0,  0,  0,  0,  0, -5},
        { -5,  0,  0,  0,  0,  0,  0, -5},
        { -5,  0,  0,  0,  0,  0,  0, -5},
        { -5,  0,  0,  0,  0,  0,  0, -5},
        {  0,  0,  0,  5,  5,  0,  0,  0}};
    
    static short bishopBoard[][]={
        {-20,-10,-10,-10,-10,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5, 10, 10,  5,  0,-10},
        {-10,  5,  5, 10, 10,  5,  5,-10},
        {-10,  0, 10, 10, 10, 10,  0,-10},
        {-10, 10, 10, 10, 10, 10, 10,-10},
        {-10,  5,  0,  0,  0,  0,  5,-10},
        {-20,-10,-10,-10,-10,-10,-10,-20}};
    
    static short queenBoard[][]={
        {-20,-10,-10, -5, -5,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5,  5,  5,  5,  0,-10},
        { -5,  0,  5,  5,  5,  5,  0, -5},
        {  0,  0,  5,  5,  5,  5,  0, -5},
        {-10,  5,  5,  5,  5,  5,  0,-10},
        {-10,  0,  5,  0,  0,  0,  0,-10},
        {-20,-10,-10, -5, -5,-10,-10,-20}};
			
	
	public static int rating(List<Move> moves, int depth){
	
		int rating = 0;
			
		rating += rateMovability();	
		rating += ratePosition();
		rating += rateMaterial();
		
		rating = rating * (depth + 1);
		
		return rating;
		
	}
	
	public static int captureRating(){
		int counter = 0;
		for(Move move : ChessMate.legalMoves(true, true)){
			switch(move.capture){
			case BLACK_PAWN: counter -= 100;
			break;
			case BLACK_BISHOP: counter -= 300;
			break;
			case BLACK_KNIGHT: counter -= 300;
			break;
			case BLACK_ROOK: counter -= 500;
			break;
			case BLACK_QUEEN: counter -= 900;
			default:
			}
		}
		System.out.println("Capture rating: " + counter);
		
		return (counter/2);
	}
	
	private static int ratePosition(){
		int counter = 0;
		for(byte r = 0; r < 8; r++){
			for(byte c = 0; c < 8; c++){
				switch(ChessMate.board[r][c]){
				case BLACK_PAWN:
					counter += pawnBoard[r][c];
					break;
				case BLACK_KNIGHT:
					counter += knightBoard[r][c];
					break;
				case BLACK_BISHOP:
					counter += bishopBoard[r][c];
					break;
				case BLACK_ROOK:
					counter += rookBoard[r][c];
					break;
				case BLACK_QUEEN:
					counter += queenBoard[r][c];
					break;
				default:
				}
			}
		}
		return counter;
	}
	
	public static int rateAttack(boolean white) {
        int counter=0;
        if(!white){
        	byte tempPosition = ChessMate.blackKingPos;
	        for (byte i=0;i<64;i++) {
	            switch (ChessMate.board[i/8][i%8]) {
	                case BLACK_PAWN: {ChessMate.blackKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=64;}}
	                    break;
	                case BLACK_ROOK: {ChessMate.blackKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=500;}}
	                    break;
	                case BLACK_KNIGHT: {ChessMate.blackKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=300;}}
	                    break;
	                case BLACK_BISHOP: {ChessMate.blackKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=300;}}
	                    break;
	                case BLACK_QUEEN: {ChessMate.blackKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=900;}}
	        			//System.out.println(i);
	                    break;
	                default:
	            }
	        }
	        ChessMate.blackKingPos = tempPosition;
        }else{
        	byte tempPosition = ChessMate.whiteKingPos;
 	        for (byte i=0;i<64;i++) {
 	            switch (ChessMate.board[i/8][i%8]) {
 	                case WHITE_PAWN: {ChessMate.whiteKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=64;}}
 	                    break;
 	                case WHITE_ROOK: {ChessMate.whiteKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=500;}}
 	                    break;
 	                case WHITE_KNIGHT: {ChessMate.whiteKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=300;}}
 	                    break;
 	                case WHITE_BISHOP: {ChessMate.whiteKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=300;}}
 	                    break;
 	                case WHITE_QUEEN: {ChessMate.whiteKingPos=i; if (ChessMate.kingInCheck(false)) {counter-=900;}}
 	        			//System.out.println(i);
 	                    break;
 	               default:
 	            }
 	        }
 	        ChessMate.whiteKingPos = tempPosition;
        }
        return counter/2;
    }
	private static int rateMaterial(){
		int counter = 0;
		for(byte i = 0; i < 64; i++){
			if(ChessMate.board[i/8][i%8] == PieceType.EMPTY) continue;
			switch(ChessMate.board[i/8][i%8]){
			case WHITE_QUEEN:
				counter -= 900;
				break;
			case WHITE_ROOK:
				counter -= 500;
				break;
			case WHITE_KNIGHT:
				counter -= 300;
				break;
			case WHITE_BISHOP:
				counter -= 300;
				break;
			case WHITE_PAWN:
				counter -= 100;
				break;
			case BLACK_QUEEN:
				counter += 900;
				break;
			case BLACK_ROOK:
				counter += 500;
				break;
			case BLACK_KNIGHT:
				counter += 300;
				break;
			case BLACK_BISHOP:
				counter += 300;
				break;
			case BLACK_PAWN:
				counter += 100;
				break;
			default:
			}
		}
		
		return counter;
	}
	
	private static int rateMovability(){
		int counter = 0;
		
		//Not sure why this works, I would expect the signs to be switched
		
		List<Move> movesBlack = ChessMate.legalMoves(false, true);
		if(movesBlack.size() == 0){
			if(ChessMate.kingInCheck(false)){
				counter += 200000;
			}else{
				counter += 150000;
			}
		}
		
		List<Move> movesWhite = ChessMate.legalMoves(true, true);
		if(movesWhite.size() == 0){
			if(ChessMate.kingInCheck(true)){
				counter -= 200000;
			}else{
				counter += 150000;
			}
		}
		
		return counter;
	}
}
