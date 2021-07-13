package Chess;

public enum PieceColor {
	NONE, WHITE, BLACK;
	
	static public PieceColor flipColor(PieceColor col) //if white was passed, return black. if black was passed, return white
	{
		if(col == WHITE)
			return BLACK;
		return WHITE;
	}
	
	static public boolean equalsWhite(PieceColor col) { return col == WHITE; }
	static public boolean equalsBlack(PieceColor col) { return col == BLACK; }
}
