
public class Move {
	
	public byte startPos;
	public byte endPos;
	public MoveType moveType;
	public PieceType capture;
	
	public Move(byte startPos, byte endPos, MoveType moveType, PieceType capture){
		this.startPos = startPos;
		this.endPos = endPos;
		this.moveType = moveType;
		this.capture = capture;
	}
}
