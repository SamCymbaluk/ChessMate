
public class Move {
	
	public int startPos;
	public int endPos;
	public MoveType moveType;
	public PieceType capture;
	
	public Move(int startPos, int endPos, MoveType moveType, PieceType capture){
		this.startPos = startPos;
		this.endPos = endPos;
		this.moveType = moveType;
		this.capture = capture;
	}
}
