package Chess;

public enum GameState {
	NEUTRAL, WHITE_CHECKED, BLACK_CHECKED, WHITE_WIN, BLACK_WIN, DRAW;
	
	static public boolean gameContinuing(GameState current)
	{
		if(current == NEUTRAL || current == WHITE_CHECKED || current == BLACK_CHECKED)
			return true;
		return false;
	}
	
	static public String displayGameState(GameState current)
	{
		if(current == DRAW)
			return "The game is over, the players have drawn.";
		if(current == WHITE_WIN)
			return "The game is over, White has won!";
		if(current == BLACK_WIN)
			return "The game is over, Black has won!";
		if(current == WHITE_CHECKED)
			return "White's King is in check!";
		if(current == BLACK_CHECKED)
			return "Black's King is in check!";
			
		return "Neither player is in check, and the game is ongoing.";
	}
	
	static public GameState checkWhite() { return WHITE_CHECKED; }
	static public GameState checkBlack() { return BLACK_CHECKED; }
	static public GameState newGame() { return NEUTRAL; }
	static public GameState noChecks() { return NEUTRAL; }
	static public GameState whiteCheckmate() { return WHITE_WIN; }
	static public GameState blackCheckmate() { return BLACK_WIN; }
	static public GameState draw() { return DRAW; }
	static public GameState stalemate() { return DRAW; }
}
