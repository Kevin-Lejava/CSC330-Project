package Chess;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import javax.swing.*;

//TODO:
//Implement Checking logic
//Implement checkmate/stalemate logic: how do we end a game?
//Implement descriptive error output to tell users why their move was rejected
//Draw button?

public class ChessGUI extends JFrame implements MouseListener {
	private static final long serialVersionUID = 1L;

	private JLayeredPane pane;
	private JPanel board;
	private JButton space[][] = new JButton[8][8];
	private GridBagConstraints c = new GridBagConstraints();

	private ImageIcon WPawn = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\White Pawn.png", "White Pawn");
	private ImageIcon BPawn = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\Black Pawn.png", "Black Pawn");
	private ImageIcon WRook = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\White Rook.png", "White Rook");
	private ImageIcon BRook = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\Black Rook.png", "Black Rook");
	private ImageIcon WKnight = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\White Knight.png", "White Knight");
	private ImageIcon BKnight = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\Black Knight.png", "Black Knight");
	private ImageIcon WBishop = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\White Bishop.png", "White Bishop");
	private ImageIcon BBishop = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\Black Bishop.png", "Black Bishop");
	private ImageIcon WQueen = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\White Queen.png", "White Queen");
	private ImageIcon BQueen = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\Black Queen.png", "Black Queen");
	private ImageIcon WKing = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\White King.png", "White King");
	private ImageIcon BKing = new ImageIcon(
			"C:\\Users\\Kevin\\Programming\\Java Eclipse\\Chess src\\src\\Chess\\PNG\\Black King.png", "Black King");

	private Icon pickedPiece;
	private boolean Press = false;
	private int originalX;
	private int originalY;
	private PieceColor currentTurn = PieceColor.WHITE; //determines which player is currently moving
	//holds the current x- and y-coordinates of each king
	private int whiteKingX;
	private int whiteKingY;
	private int blackKingX;
	private int blackKingY;
	//determines whether the kings and rooks have been moved, relevant for checking the legality of a move
	private boolean blackQSideRookMoved;
	private boolean blackKSideRookMoved;
	private boolean whiteQSideRookMoved;
	private boolean whiteKSideRookMoved;
	private boolean blackKingMoved;
	private boolean whiteKingMoved;
	
	private boolean castled; //determines whether the current player castled this turn, so the game knows to also move the rook
	private int enPassantWindow; //set to 2 when a pawn is double-moved, and decremented every turn until it returns to 0
	private int enPassantX, enPassantY; //stores the coordinates of the square to be targeted by a pawn using en-passant
	
	private GameState currentState;

	// Constructor creates a general layout of the board then calls setBoard Method
	public ChessGUI() {
		Dimension boardSize = new Dimension(592, 592);

		pane = new JLayeredPane();
		getContentPane().add(pane);
		pane.setPreferredSize(boardSize);

		board = new JPanel();
		board.setPreferredSize(boardSize);
		board.setBounds(0, 0, boardSize.width, boardSize.height);

		pane.add(board, JLayeredPane.DEFAULT_LAYER);
		board.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;

		setBoard();
		
		//initializes the gamestate to NEUTRAL
		currentState = GameState.newGame();

		//initializes the variables that hold the king's position
		whiteKingX = 4;
		whiteKingY = 7;
		blackKingX = 4;
		blackKingY = 0;
		
		//sets the flags that determine whether the king and rooks have moved yet, needed for castling
		blackQSideRookMoved = false;
		blackKSideRookMoved = false;
		whiteQSideRookMoved = false;
		whiteKSideRookMoved = false;
		blackKingMoved = false;
		whiteKingMoved = false;
		
		enPassantWindow = 0;
	}

	// setBoard method creates the patterned squares
	// Each square has a button set on it.
	// Each piece is set on a square as an ImageIcon
	private void setBoard() {
		for (c.gridy = 0; c.gridy < 8; c.gridy++)
			for (c.gridx = 0; c.gridx < 8; c.gridx++) {
				JPanel square = new JPanel();
				space[c.gridx][c.gridy] = new JButton();
				space[c.gridx][c.gridy].setBorderPainted(false);
				space[c.gridx][c.gridy].setContentAreaFilled(false);
				space[c.gridx][c.gridy].setOpaque(false);
				space[c.gridx][c.gridy].setPreferredSize(new Dimension(64, 64));
				space[c.gridx][c.gridy].addMouseListener(this);

				square.add(space[c.gridx][c.gridy]);

				board.add(square, c);

				if (c.gridx % 2 == 0) {
					if (c.gridy % 2 == 0)
						square.setBackground(Color.black);
					else
						square.setBackground(new Color(199, 116, 28));
				}

				else {
					if (c.gridy % 2 == 1)
						square.setBackground(Color.black);
					else
						square.setBackground(new Color(199, 116, 28));
				}

				if (c.gridy == 0) {
					if (c.gridx == 0 || c.gridx == 7)
						space[c.gridx][c.gridy].setIcon(BRook);
					else if (c.gridx == 1 || c.gridx == 6)
						space[c.gridx][c.gridy].setIcon(BKnight);
					else if (c.gridx == 2 || c.gridx == 5)
						space[c.gridx][c.gridy].setIcon(BBishop);
					else if (c.gridx == 3)
						space[c.gridx][c.gridy].setIcon(BQueen);
					else
						space[c.gridx][c.gridy].setIcon(BKing);
				}

				else if (c.gridy == 1)
					space[c.gridx][c.gridy].setIcon(BPawn);

				else if (c.gridy == 6)
					space[c.gridx][c.gridy].setIcon(WPawn);

				else if (c.gridy == 7) {
					if (c.gridx == 0 || c.gridx == 7)
						space[c.gridx][c.gridy].setIcon(WRook);
					else if (c.gridx == 1 || c.gridx == 6)
						space[c.gridx][c.gridy].setIcon(WKnight);
					else if (c.gridx == 2 || c.gridx == 5)
						space[c.gridx][c.gridy].setIcon(WBishop);
					else if (c.gridx == 3)
						space[c.gridx][c.gridy].setIcon(WQueen);
					else
						space[c.gridx][c.gridy].setIcon(WKing);
				}
			}
	}

	// Performs actions based on which button the mouse has been clicked
	public void mouseClicked(MouseEvent click) {

		for (c.gridy = 0; c.gridy < 8; c.gridy++)
			for (c.gridx = 0; c.gridx < 8; c.gridx++)
				if (click.getSource() == space[c.gridx][c.gridy]) {

					if (Press == false) {
						pickedPiece = space[c.gridx][c.gridy].getIcon();
						originalX = c.gridx;
						originalY = c.gridy;
						space[c.gridx][c.gridy].setIcon(null);
						Press = true;
					}

					else if (Press) {
						if (GameState.gameContinuing(currentState))
						{
							if (checkLegalMove(pickedPiece)) {
								space[c.gridx][c.gridy].setIcon(pickedPiece);
								Press = false;
								if (space[c.gridx][0].getIcon() == WPawn)
									space[c.gridx][c.gridy].setIcon(WQueen);
								if (space[c.gridx][7].getIcon() == BPawn)
									space[c.gridx][c.gridy].setIcon(BQueen);
							
								//if a successful king move was made, update the variables that track the king's position
								if (pickedPiece == WKing)
								{
									whiteKingX = c.gridx;
									whiteKingY = c.gridy;
									whiteKingMoved = true;
								}
								if (pickedPiece == BKing)
								{
									blackKingX = c.gridx;
									blackKingY = c.gridy;
									blackKingMoved = true;
								}
							
								//if you're moving a piece from a rook starting square, or moving a piece onto it, set the flag indicating that castling with that rook is no longer possible
								if ((originalX == 0 && originalY == 0) || (c.gridx == 0 && c.gridy == 0))
									blackQSideRookMoved = true;
								if((originalX == 7 && originalY == 0) || (c.gridx == 7 && c.gridy == 0))
									blackKSideRookMoved = true;
								if((originalX == 0 && originalY == 7) || (c.gridx == 0 && c.gridy == 7))
									whiteQSideRookMoved = true;
								if((originalX == 7 && originalY == 7) || (c.gridx == 7 && c.gridy == 7))
									whiteKSideRookMoved = true;
							
								//if the player has just successfully castled, this tells the game where to move the rook
								if(castled)
								{
									Icon castledRook;
									if(c.gridy == 0)
									{
										if(c.gridx == 6)
										{
											castledRook = space[7][0].getIcon();
											space[7][0].setIcon(null);
											space[5][0].setIcon(castledRook);
											blackKSideRookMoved = true;
										}
										if(c.gridx == 2)
										{
											castledRook = space[0][0].getIcon();
											space[0][0].setIcon(null);
											space[3][0].setIcon(castledRook);
											blackQSideRookMoved = true;
										}
									}
									if(c.gridy == 7)
									{
										if(c.gridx == 6)
										{
											castledRook = space[7][7].getIcon();
											space[7][7].setIcon(null);
											space[5][7].setIcon(castledRook);
											whiteKSideRookMoved = true;
										}
										if(c.gridx == 2)
										{
											castledRook = space[0][7].getIcon();
											space[0][7].setIcon(null);
											space[3][7].setIcon(castledRook);
											whiteQSideRookMoved = true;
										}
									}
									castled = false;
								}
							
								//if the player has just successfully performed en passant, this deletes the appropriate pawn
								if(c.gridx == enPassantX && c.gridy == enPassantY && enPassantWindow > 0)
								{
									if(pickedPiece == WPawn)
										space[c.gridx][c.gridy + 1].setIcon(null);
									if(pickedPiece == BPawn)
										space[c.gridx][c.gridy - 1].setIcon(null);
								}
								
								
								if(enPassantWindow > 0)
									enPassantWindow--;
								
								currentState = evaluateGameStateDriver(currentTurn); //updates the game's current state--is anybody in check, and if so, can they escape?
								if(currentState != GameState.NEUTRAL)
									System.out.println(GameState.displayGameState(currentState));
								
								currentTurn = PieceColor.flipColor(currentTurn); //if the current player made a successful move, it is now the other player's turn
							}

							else {
								space[originalX][originalY].setIcon(pickedPiece);
								Press = false;
								break;
							}
						}
						else //if you're here, the game is over! print the appropriate message
						{
							System.out.println(GameState.displayGameState(currentState));
							space[originalX][originalY].setIcon(pickedPiece);
							Press = false;
							break;
						}
					}

				}

		repaint();
	}
	
	private PieceColor getPieceColor(Icon image) //gets the color of a piece given its image
	{
		switch(image.toString())
		{
		case "White Pawn":
		case "White Knight":
		case "White Bishop":
		case "White Rook":
		case "White Queen":
		case "White King":
			return PieceColor.WHITE;
		case "Black Pawn":
		case "Black Knight":
		case "Black Bishop":
		case "Black Rook":
		case "Black Queen":
		case "Black King":
			return PieceColor.BLACK;
		default:
			return PieceColor.NONE; //if you're here, you've somehow selected a piece that isn't black or white. impressive!
		}
	}

	// Determines where pieces can move along the board and checks if player is
	// moving correctly.
	// If not then the piece goes back to where it came from.
	private boolean checkLegalMove(Icon image) {
		if (pickedPiece == null) //you can't move an empty square
			return false; 
		if (c.gridx == originalX && c.gridy == originalY) //you can't move in place either
			return false;
		
		PieceColor color = getPieceColor(image);
		
		if (pieceMismatch(color)) //you can't move the other player's pieces
		{
			System.err.println("That's not your piece, you can't move it!");
			return false;
		}
		
		//if the current player is in check, simulate their current move and see if it takes them out of check
		//if it doesn't, send them back to the drawing board
		if((color == PieceColor.WHITE && currentState == GameState.WHITE_CHECKED) || (color == PieceColor.BLACK && currentState == GameState.BLACK_CHECKED))
		{
			//copies relevant data that might be changed during the simulation, so it can be replaced
			Icon destinationPiece = space[c.gridx][c.gridy].getIcon();
			int whiteKingXCopy = whiteKingX;
			int whiteKingYCopy = whiteKingY;
			int blackKingXCopy = blackKingX;
			int blackKingYCopy = blackKingY;
			
			space[originalX][originalY].setIcon(null);
			space[c.gridx][c.gridy].setIcon(pickedPiece);
			
			if(pickedPiece == WKing)
			{
				whiteKingX = c.gridx;
				whiteKingY = c.gridy;
			}
			if(pickedPiece == BKing)
			{
				blackKingX = c.gridx;
				blackKingY = c.gridy;
			}
			
			boolean stopsCheck;
			if(PieceColor.equalsWhite(color))
				stopsCheck = !attacked(color, whiteKingX, whiteKingY, true, false, false);
			else
				stopsCheck = !attacked(color, blackKingX, blackKingY, true, false, false);
			
			space[c.gridx][c.gridy].setIcon(destinationPiece);
			whiteKingX = whiteKingXCopy;
			whiteKingY = whiteKingYCopy;
			blackKingX = blackKingXCopy;
			blackKingY = blackKingYCopy;
			
			if(stopsCheck == false) { System.err.println("Your King is under attack and your move would not get him to safety!"); }
			return stopsCheck;
		}
		
		switch(image.toString()) //figure out what piece you're moving and go to the appropriate function to check if that move is legal
		{
		case "White Rook":
		case "Black Rook":
			return checkRookMove(image, color, originalX, originalY, c.gridx, c.gridy);
		case "White Bishop":
		case "Black Bishop":
			return checkBishopMove(image, color, originalX, originalY, c.gridx, c.gridy);
		case "White Queen":
		case "Black Queen":
			return checkQueenMove(image, color, originalX, originalY, c.gridx, c.gridy);
		case "White Knight":
		case "Black Knight":
			return checkKnightMove(image, color, originalX, originalY, c.gridx, c.gridy);
		case "White King":
		case "Black King":
			return checkKingMove(image, color, originalX, originalY, c.gridx, c.gridy, true);
		case "White Pawn":
			return checkWPawnMove(image, originalX, originalY, c.gridx, c.gridy);
		case "Black Pawn":
			return checkBPawnMove(image, originalX, originalY, c.gridx, c.gridy);
		default:
			return false;
		}
	}
	
	//This method determines the current state of the game--Is someone in check? If so, can their King escape check?
	//Algorithm:
	//1. Split off into one of two code blocks depending on the current player's color
	//2. Check if the opposite player's King is under attack. If not, return NEUTRAL
	//3. If they are under attack, figure out how many attacks are currently targeting them. If there are more than one, skip to 5
	//4. If there's one attacker, figure out if the attacker can be captured next turn, or if the attack can be blocked. If so, they are checked but not mated
	//5. Figure out if the King has any adjacent squares that are not attacked or blocked by an ally. If so, they are checked but not mated
	//6. If 4 and 5 did not reveal any way out of check, return checkmate--the game is over!
	//NOTE: Should not be called directly. Use evaluateGameStateDriver() instead to temporarily remove the chosen King from the board, preventing bugs with checkmate detection
	private GameState evaluateGameState(PieceColor color)
	{
		GameState returnState = GameState.noChecks();
		if(PieceColor.equalsWhite(color))
		{
			if(attacked(PieceColor.BLACK, blackKingX, blackKingY, true, false, false))
			{
				returnState = GameState.checkBlack();
				LinkedHashMap<String, Icon> attacks = countAttacks(PieceColor.BLACK, blackKingX, blackKingY);
				if(attacks.size() == 1)
				{
					String coords = (String)attacks.keySet().toArray()[0];
					int x = Integer.parseInt(coords.substring(0, 1));
					int y = Integer.parseInt(coords.substring(2));
					
					if(attacked(PieceColor.WHITE, x, y, false, false, false))
						return returnState;
					
					if(attacks.containsValue(WPawn) || attacks.containsValue(WKnight) || attacks.containsValue(WKing));
					else if(x == blackKingX)
					{
						if(y > blackKingY)
						{
							for(int i = blackKingY + 1; i < y; i++)
								if(attacked(PieceColor.WHITE, x, i, false, false, true) || (validPawnTarget(PieceColor.BLACK, x, i - 1, x, i) || validPawnTarget(PieceColor.BLACK, x, i - 2, x, i)))
									return returnState;
						}
						else
						{
							for(int i = y + 1; i < blackKingY; i++)
								if(attacked(PieceColor.WHITE, x, i, false, false, true) || (validPawnTarget(PieceColor.BLACK, x, i - 1, x, i) || validPawnTarget(PieceColor.BLACK, x, i - 2, x, i)))
									return returnState;
						}
					}
					else if(y == blackKingY)
					{
						if(x > blackKingX)
						{
							for(int i = blackKingX + 1; i < x; i++)
								if(attacked(PieceColor.WHITE, i, y, false, false, true) || (validPawnTarget(PieceColor.BLACK, i, y - 1, i, y) || validPawnTarget(PieceColor.BLACK, i, y - 2, i, y)))
									return returnState;
						}
						else
						{
							for(int i = x + 1; i < blackKingX; i++)
								if(attacked(PieceColor.WHITE, x, i, false, false, true) || (validPawnTarget(PieceColor.BLACK, i, y - 1, i, y) || validPawnTarget(PieceColor.BLACK, i, y - 2, i, y)))
									return returnState;
						}
					}
					else if(x - blackKingX == y - blackKingY)
					{
						if(x > blackKingX)
						{
							for(int i = 1; (blackKingX + i < x && blackKingY + i < y); i++)
								if(attacked(PieceColor.WHITE, blackKingX + i, blackKingY + i, false, false, true) || 
								(validPawnTarget(PieceColor.BLACK, blackKingX + i, blackKingY + i - 1, blackKingX + i, blackKingY + i) || 
								validPawnTarget(PieceColor.BLACK, blackKingX + i, blackKingY + i - 2, blackKingX + i, blackKingY + i)))
									return returnState;
						}
						else
						{
							for(int i = 1; (x + i < blackKingX && y + i < blackKingY); i++)
								if(attacked(PieceColor.WHITE, x+i, y+i, false, false, true) || (validPawnTarget(PieceColor.BLACK, x+i, y+i - 1, x+i, y+i) || validPawnTarget(PieceColor.BLACK, x+i, y+i - 2, x+i, y+i)))
									return returnState;
						}
					}
					else
					{
						if(x > blackKingX)
						{
							for(int i = 1; (blackKingX + i < x && blackKingY - i > y); i++)
								if(attacked(PieceColor.WHITE, blackKingX + i, blackKingY - i, false, false, true) || 
								(validPawnTarget(PieceColor.BLACK, blackKingX + i, blackKingY - i - 1, blackKingX + i, blackKingY - i) || 
								validPawnTarget(PieceColor.BLACK, blackKingX + i, blackKingY - i - 2, blackKingX + i, blackKingY - i)))
									return returnState;
						}
						else
						{
							for(int i = 1; (x + i < blackKingX && y - i > blackKingY); i++)
								if(attacked(PieceColor.WHITE, x + i, y - i, false, false, true) || (validPawnTarget(PieceColor.BLACK, x+ i, y-i - 1, x+ i, y-i) || validPawnTarget(PieceColor.BLACK, x+ i, y-i - 2, x+ i, y-i)))
									return returnState;
						}
					}
				}
				{
					if(blackKingX > 0)
					{
						if(blackKingY > 0)
							if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX - 1, blackKingY - 1, false))
								return returnState;
						if(blackKingY < 7)
							if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX - 1, blackKingY + 1, false))
								return returnState;
						if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX - 1, blackKingY, false))
							return returnState;
					}
					if(blackKingX < 7)
					{
						if(blackKingY > 0)
							if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX + 1, blackKingY - 1, false))
								return returnState;
						if(blackKingY < 7)
							if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX + 1, blackKingY + 1, false))
								return returnState;
						if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX + 1, blackKingY, false))
							return returnState;
					}
					if(blackKingY > 0)
						if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX, blackKingY - 1, false))
							return returnState;
					if(blackKingY < 7)
						if(checkKingMove(BKing, PieceColor.BLACK, blackKingX, blackKingY, blackKingX, blackKingY + 1, false))
							return returnState;
				}
				returnState = GameState.whiteCheckmate();
			}
		}
		
		else //black logic
		{
			if(attacked(PieceColor.WHITE, whiteKingX, whiteKingY, true, false, false))
			{
				returnState = GameState.checkWhite();
				LinkedHashMap<String, Icon> attacks = countAttacks(PieceColor.WHITE, whiteKingX, whiteKingY);
				if(attacks.size() == 1)
				{
					String coords = (String)attacks.keySet().toArray()[0];
					int x = Integer.parseInt(coords.substring(0, 1));
					int y = Integer.parseInt(coords.substring(2));
					
					if(attacked(PieceColor.BLACK, x, y, false, false, false))
						return returnState;
					
					if(attacks.containsValue(BPawn) || attacks.containsValue(BKnight) || attacks.containsValue(BKing));
					else if(x == whiteKingX)
					{
						if(y > whiteKingY)
						{
							for(int i = whiteKingY + 1; i < y; i++)
								if(attacked(PieceColor.BLACK, x, i, false, false, true) || (validPawnTarget(PieceColor.WHITE, x, i + 1, x, i) || validPawnTarget(PieceColor.WHITE, x, i + 2, x, i)))
									return returnState;
						}
						else
						{
							for(int i = y + 1; i < whiteKingY; i++)
								if(attacked(PieceColor.BLACK, x, i, false, false, true) || (validPawnTarget(PieceColor.WHITE, x, i + 1, x, i) || validPawnTarget(PieceColor.WHITE, x, i + 2, x, i)))
									return returnState;
						}
					}
					else if(y == whiteKingY)
					{
						if(x > whiteKingX)
						{
							for(int i = whiteKingX + 1; i < x; i++)
								if(attacked(PieceColor.BLACK, i, y, false, false, true) || (validPawnTarget(PieceColor.WHITE, i, y + 1, i, y) || validPawnTarget(PieceColor.WHITE, i, y + 2, i, y)))
									return returnState;
						}
						else
						{
							for(int i = x + 1; i < whiteKingX; i++)
								if(attacked(PieceColor.BLACK, x, i, false, false, true) || (validPawnTarget(PieceColor.WHITE, x, i + 1, x, i) || validPawnTarget(PieceColor.WHITE, x, i + 2, x, i)))
									return returnState;
						}
					}
					else if(x - whiteKingX == y - whiteKingY)
					{
						if(x > whiteKingX)
						{
							for(int i = 1; (whiteKingX + i < x && whiteKingY + i < y); i++)
								if(attacked(PieceColor.BLACK, whiteKingX + i, whiteKingY + i, false, false, true) || 
								(validPawnTarget(PieceColor.WHITE, whiteKingX + i, whiteKingY + i + 1, whiteKingX + i, whiteKingY + i) || 
								validPawnTarget(PieceColor.WHITE, whiteKingX + i, whiteKingY + i + 2, whiteKingX + i, whiteKingY + i)))
									return returnState;
						}
						else
						{
							for(int i = 1; (x + i < whiteKingX && y + i < whiteKingY); i++)
								if(attacked(PieceColor.BLACK, x+i, y+i, false, false, true) || (validPawnTarget(PieceColor.WHITE, x+i, y+i + 1, x+i, y+i) || validPawnTarget(PieceColor.WHITE, x+i, y+i + 2, x+i, y+i)))
									return returnState;
						}
					}
					else
					{
						if(x > whiteKingX)
						{
							for(int i = 1; (whiteKingX + i < x && whiteKingY - i > y); i++)
								if(attacked(PieceColor.BLACK, whiteKingX + i, whiteKingY - i, false, false, true) || 
								(validPawnTarget(PieceColor.WHITE, whiteKingX + i, whiteKingY - i + 1, whiteKingX + i, whiteKingY - i) || 
								validPawnTarget(PieceColor.WHITE, whiteKingX + i, whiteKingY - i + 2, whiteKingX + i, whiteKingY - i)))
									return returnState;
						}
						else
						{
							for(int i = 1; (x + i < whiteKingX && y - i > whiteKingY); i++)
								if(attacked(PieceColor.BLACK, x + i, y - i, false, false, true) || (validPawnTarget(PieceColor.WHITE, x + i, y - i + 1, x + i, y - i) || validPawnTarget(PieceColor.WHITE, x + i, y - i + 2, x + i, y - i)))
									return returnState;
						}
					}
				}
				{
					if(whiteKingX > 0)
					{
						if(whiteKingY > 0)
							if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX - 1, whiteKingY - 1, false))
								return returnState;
						if(whiteKingY < 7)
							if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX - 1, whiteKingY + 1, false))
								return returnState;
						if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX - 1, whiteKingY, false))
							return returnState;
					}
					if(whiteKingX < 7)
					{
						if(whiteKingY > 0)
							if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX + 1, whiteKingY - 1, false))
								return returnState;
						if(whiteKingY < 7)
							if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX + 1, whiteKingY + 1, false))
								return returnState;
						if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX + 1, whiteKingY, false))
							return returnState;
					}
					if(whiteKingY > 0)
						if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX, whiteKingY - 1, false))
							return returnState;
					if(whiteKingY < 7)
						if(checkKingMove(WKing, PieceColor.WHITE, whiteKingX, whiteKingY, whiteKingX, whiteKingY + 1, false))
							return returnState;
				}
				returnState = GameState.blackCheckmate();
			}
		}
		return returnState;
	}
	//Removes the appropriate King from the board, calls evaluateGameState(), replaces the King and passes the result up the call stack
	//If you don't first remove the King, evaluateGameState() will sometimes get confused and think the King can hide behind itself to avoid check from a rook or such
	private GameState evaluateGameStateDriver(PieceColor color)
	{
		if(color == PieceColor.WHITE)
			space[blackKingX][blackKingY].setIcon(null);
		else
			space[whiteKingX][whiteKingY].setIcon(null);
		
		GameState returnState = evaluateGameState(color);
		
		if(color == PieceColor.WHITE)
			space[blackKingX][blackKingY].setIcon(BKing);
		else
			space[whiteKingX][whiteKingY].setIcon(WKing);
		
		return returnState;
	}
	
	//used in evaluateGameState() to determine if a check can be blocked by pushing a pawn
	//1. Make sure you're not about to check for an out-of-bounds pawn
	//2. Check that the spot in question has a pawn
	//3. If you're trying to double-move the pawn, make sure there's nothing in the way and it's on the proper file to double-move
	//4. Test if the pawn is pinned to the King
	//If you have a pawn in the proper place, making a valid move and not revealing another attack on the King, then return true! Otherwise return false.
	private boolean validPawnTarget(PieceColor color, int pawnX, int pawnY, int targetX, int targetY)
	{
		if(PieceColor.equalsWhite(color))
		{
			if(pawnY > 7)
				return false;
			if(space[pawnX][pawnY].getIcon() != WPawn)
				return false;
			if(pawnY - 2 == targetY)
				if(space[pawnX][pawnY - 1].getIcon() != null || pawnY != 6)
					return false;
			if(willExposeKing(WPawn, color, pawnX, pawnY, targetX, targetY))
				return false;
		}
		else
		{
			if(pawnY < 0)
				return false;
			if(space[pawnX][pawnY].getIcon() != BPawn)
				return false;
			if(pawnY + 2 == targetY)
				if(space[pawnX][pawnY + 1].getIcon() != null || pawnY != 1)
					return false;
			if(willExposeKing(BPawn, color, pawnX, pawnY, targetX, targetY))
				return false;
		}
		return true;
	}
	
	//checks that the piece is being moved by the correct player; white shouldn't be able to move black pieces, for example
	//returns true if the player is indeed trying to move the wrong color piece
	private boolean pieceMismatch(PieceColor color) 
	{
		return color != currentTurn;
	}
	
	private boolean checkRookMove(Icon image, PieceColor color, int startX, int startY, int destX, int destY)
	{
		if (destX != startX && destY != startY) //make sure your rook is moving in a pure horizontal or vertical line
		{
			System.err.println("Invalid move! Rooks move in straight lines, vertically or horizontally.");
			return false;
		}
		if (destX == startX)
			if(!checkHorizontalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		if (destY == startY)
			if(!checkVerticalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		if (willExposeKing(image, color, startX, startY, destX, destY))
		{
			System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
			return false;
		}
		return true;
	}
	private boolean checkBishopMove(Icon image, PieceColor color, int startX, int startY, int destX, int destY)
	{
		if(Math.abs(destX - startX) != Math.abs(destY - startY)) //make sure your bishop is moving in a proper diagonal line
		{
			System.err.println("Invalid move! Bishops move in straight, diagonal lines.");
			return false;
		}
		if (destX - startX == destY - startY)
		{
			if(!checkLDiagonalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		}
		else
		{
			if(!checkRDiagonalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		}
		if (willExposeKing(image, color, startX, startY, destX, destY))
		{
			System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
			return false;
		}
		return true;
	}
	private boolean checkQueenMove(Icon image, PieceColor color, int startX, int startY, int destX, int destY)
	{
		if((Math.abs(destX - startX) != Math.abs(destY - startY)) && (destX != startX && destY != startY))
		{
			System.err.println("Invalid move! Queens move in straight lines in any of the eight directions.");
			return false;
		}
		
		if (destX == startX)
			if(!checkHorizontalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		if (destY == startY)
			if(!checkVerticalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		if (destX - startX == destY - startY)
		{
			if(!checkLDiagonalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		}
		if(destX - startX == 0 - (destY - startY))
		{
			if(!checkRDiagonalCollision(color, startX, startY, destX, destY))
			{
				System.err.println("Invalid move! This might be because there are pieces between the starting point and destination, or because you are trying to capture a friendly piece.");
				return false;
			}
		}
		
		if (willExposeKing(image, color, startX, startY, destX, destY))
		{
			System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
			return false;
		}
		return true;
	}
	private boolean checkKnightMove(Icon image, PieceColor color, int startX, int startY, int destX, int destY)
	{
		if(Math.abs(destX - startX) + Math.abs(destY - startY) != 3) //first make sure the knight has moved precisely 3 squares away from the start, in any direction
		{
			System.err.println("Invalid move! Knights move in an 'L'-shape, moving two squares horizontally or vertically, then one square vertically or horizontally.");
			return false;
		}
		if(destX == startX || destY == startY) //then make sure the knight didn't just move three tiles in a single direction
		{
			System.err.println("Invalid move! Knights move in an 'L'-shape, moving two squares horizontally or vertically, then one square vertically or horizontally.");
			return false;
		}
		
		Icon destinationSquare = space[destX][destY].getIcon();
		if(PieceColor.equalsWhite(color))
		{
			if(destinationSquare == WPawn || destinationSquare == WKnight || destinationSquare == WBishop || destinationSquare == WRook || destinationSquare == WQueen || destinationSquare == WKing)
			{
				System.err.println("Invalid move! You are attempting to capture a friendly piece.");
				return false;
			}
		}
		else
		{
			if(destinationSquare == BPawn || destinationSquare == BKnight || destinationSquare == BBishop || destinationSquare == BRook || destinationSquare == BQueen || destinationSquare == BKing)
			{
				System.err.println("Invalid move! You are attempting to capture a friendly piece.");
				return false;
			}
		}
			
		if (willExposeKing(image, color, startX, startY, destX, destY))
		{
			System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
			return false;
		}
		return true;
	}
	//displayErrorOutput should be set to true when checking a user's move, and false when checking a hypothetical move (i.e. the computer trying to figure out if the king can escape check)
	private boolean checkKingMove(Icon image, PieceColor color, int startX, int startY, int destX, int destY, boolean displayErrorOutput)
	{
		if(Math.abs(destX - startX) == 2 && destY == startY) //if the king is trying to castle, let the castling method handle that
			return checkCastle(image, color, startX, startY, destX, destY);
		if(Math.abs(destX - startX) > 1 || Math.abs(destY - startY) > 1) //the king can move one block horizontally and/or vertically, but no more
		{
			if(displayErrorOutput) {System.err.println("Invalid move! The King can move a single square in any direction.");}
			return false;
		}
		
		Icon destinationSquare = space[destX][destY].getIcon(); 
		if(PieceColor.equalsWhite(color))
		{
			if(destinationSquare == WPawn || destinationSquare == WKnight || destinationSquare == WBishop || destinationSquare == WRook || destinationSquare == WQueen)
			{
				if(displayErrorOutput) {System.err.println("Invalid move! You are attempting to capture a friendly piece.");}
				return false;
			}
		}
		else
		{
			if(destinationSquare == BPawn || destinationSquare == BKnight || destinationSquare == BBishop || destinationSquare == BRook || destinationSquare == BQueen)
			{
				if(displayErrorOutput) {System.err.println("Invalid move! You are attempting to capture a friendly piece.");}
				return false;
			}
		}
		
		if (attacked(color, destX, destY, true, true, false)) //the king cannot put itself under attack
		{
			if(displayErrorOutput) {System.err.println("Invalid move! The King cannot put itself on a square that an enemy is attacking.");}
			return false;
		}
		return true;
	}
	//Castling may only be performed if:
	//1. The King hasn't moved
	//2. The rook involved hasn't moved
	//3. The King is not under attack
	//4. The King will not have to move over any square that is under attack
	//5. There are no pieces between the King and rook
	private boolean checkCastle(Icon image, PieceColor color, int startX, int startY, int destX, int destY)
	{
		if(startX != 4)
		{
			System.err.println("You cannot castle if you are not on the starting square!");
			return false;
		}
		if((image == BKing && blackKingMoved) || (image == WKing && whiteKingMoved))
		{
			System.err.println("You cannot castle if you have already moved your King!");
			return false;
		}
		if(attacked(color, startX, startY, true, true, false))
		{
			System.err.println("Your King is under attack, you cannot castle right now!");
			return false;
		}
		
		if(PieceColor.equalsWhite(color))
		{
			if(destX == 6)
			{
				if(whiteKSideRookMoved)
				{
					System.err.println("This rook has already moved, you cannot castle with it!");
					return false;
				}
				if(space[5][7].getIcon() != null || space[6][7].getIcon() != null)
				{
					System.err.println("You cannot castle if there are pieces between your King and rook!");
					return false;
				}
				if(attacked(color, 5, 7, true, true, false) || attacked(color, 6, 7, true, true, false))
				{
					System.err.println("You cannot castle your King through squares that are under attack!");
					return false;
				}
			}
			else if(destX == 2)
			{
				if(whiteQSideRookMoved)
				{
					System.err.println("This rook has already moved, you cannot castle with it!");
					return false;
				}
				if(space[3][7].getIcon() != null || space[2][7].getIcon() != null || space[1][7].getIcon() != null)
				{
					System.err.println("You cannot castle if there are pieces between your King and rook!");
					return false;
				}
				if(attacked(color, 3, 7, true, true, false) || attacked(color, 2, 7, true, true, false))
				{
					System.err.println("You cannot castle your King through squares that are under attack!");
					return false;
				}
			}
			else
			{
				System.err.println("Invalid move! The King can move a single square in any direction."); //realistically you shouldn't be able to get to this spot in the code anyways, call this a sanity check
				return false;
			}
		}
		else
		{
			if(destX == 6)
			{
				if(blackKSideRookMoved)
				{
					System.err.println("This rook has already moved, you cannot castle with it!");
					return false;
				}
				if(space[5][0].getIcon() != null || space[6][0].getIcon() != null)
				{
					System.err.println("You cannot castle if there are pieces between your King and rook!");
					return false;
				}
				if(attacked(color, 5, 0, true, true, false) || attacked(color, 6, 0, true, true, false))
				{
					System.err.println("You cannot castle your King through squares that are under attack!");
					return false;
				}
			}
			else if(destX == 2)
			{
				if(blackQSideRookMoved)
				{
					System.err.println("This rook has already moved, you cannot castle with it!");
					return false;
				}
				if(space[3][0].getIcon() != null || space[2][0].getIcon() != null || space[1][0].getIcon() != null)
				{
					System.err.println("You cannot castle if there are pieces between your King and rook!");
					return false;
				}
				if(attacked(color, 3, 0, true, true, false) || attacked(color, 2, 0, true, true, false))
				{
					System.err.println("You cannot castle your King through squares that are under attack!");
					return false;
				}
			}
			else
			{
				System.err.println("Invalid move! The King can move a single square in any direction.");
				return false;
			}
		}
		castled = true;
		return true;
	}
	//Pawn movement is complicated
	//A pawn that has not moved yet can move forward either one or two squares. A moved pawn can only move forward one at a time.
	//Pawns can not move through or into other pieces if they go straight.
	//They can, however, capture pieces that are one square ahead and one to the left or right
	//A pawn that has just moved two squares becomes vulnerable to en passant on the opponent's next turn:
	//In en passant, an opposing pawn captures a double-moving pawn by diagonally attacking the square behind the double-mover
	//"Forward" is defined differently for black and white pawns, as they can only move towards the enemy's side
	private boolean checkWPawnMove(Icon image, int startX, int startY, int destX, int destY)
	{
		if(startY == 6 && destX == startX)
		{
			if(destY != 5 && destY != 4)
			{
				System.err.println("Invalid move! Pawns move one square forward, or two on their first move.");
				return false;
			}
			
			Icon firstSquare = space[destX][5].getIcon();
			Icon secondSquare = space[destX][4].getIcon();
			if(firstSquare != null)
			{
				System.err.println("Pawns cannot capture pieces by moving straight, nor can they move through pieces!");
				return false;
			}
			if(destY == 4)
				if(secondSquare != null)
				{
					System.err.println("Pawns cannot capture pieces by moving straight, nor can they move through pieces!");
					return false;
				}
			
			if(willExposeKing(image, PieceColor.WHITE, startX, startY, destX, destY))
			{
				System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
				return false;
			}
			
			if(destY == 4) //if you've double-moved, open the en passant window; your opponent now has a turn to perform en passant
			{
				enPassantWindow = 2;
				enPassantX = destX;
				enPassantY = 5;
			}
			return true;
		}
		if(destX == startX)
		{
			if(destY + 1 != startY)
			{
				System.err.println("Invalid move! Pawns move one square forward, or two on their first move.");
				return false;
			}
			
			Icon destinationSquare = space[destX][destY].getIcon();
			if(destinationSquare != null)
			{
				System.err.println("Pawns cannot capture pieces by moving straight, nor can they move through pieces!");
				return false;
			}
			
			if(willExposeKing(image, PieceColor.WHITE, startX, startY, destX, destY))
			{
				System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
				return false;
			}
			return true;
		}
		else
		{
			if(Math.abs(destX - startX) != 1 || destY != startY - 1)
			{
				System.err.println("Invalid move! Pawns can only move diagonally by going one forward and one to the right or left, and only if they capture an enemy in the process!");
				return false;
			}
			
			Icon destinationSquare = space[destX][destY].getIcon();
			if(enPassantWindow > 0 && destX == enPassantX && destY == enPassantY); //note the semi-colon
				//if the conditions are right to en passant, don't do anything here but don't execute the next check either
			else if(destinationSquare != BPawn && destinationSquare != BKnight && destinationSquare != BBishop && destinationSquare != BRook && destinationSquare != BQueen && destinationSquare != BKing)
			{
				System.err.println("Pawns can only diagonally move if they capture an enemy in the process! This includes en passant.");
				return false;
			}
			
			if(willExposeKing(image, PieceColor.WHITE, startX, startY, destX, destY))
			{
				System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
				return false;
			}
			return true;
		}
	}
	private boolean checkBPawnMove(Icon image, int startX, int startY, int destX, int destY)
	{
		if(startY == 1 && destX == startX)
		{
			if(destY != 2 && destY != 3)
			{
				System.err.println("Invalid move! Pawns move one square forward, or two on their first move.");
				return false;
			}
			
			Icon firstSquare = space[destX][2].getIcon();
			Icon secondSquare = space[destX][3].getIcon();
			if(firstSquare != null)
			{
				System.err.println("Pawns cannot capture pieces by moving straight, nor can they move through pieces!");
				return false;
			}
			if(destY == 3)
				if(secondSquare != null)
				{
					System.err.println("Pawns cannot capture pieces by moving straight, nor can they move through pieces!");
					return false;
				}
			
			if(willExposeKing(image, PieceColor.BLACK, startX, startY, destX, destY))
			{
				System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
				return false;
			}
			
			if(destY == 3)
			{
				enPassantWindow = 2;
				enPassantX = destX;
				enPassantY = 2;
			}
			return true;
		}
		if(destX == startX)
		{
			if(destY - 1 != startY)
			{
				System.err.println("Invalid move! Pawns move one square forward, or two on their first move.");
				return false;
			}
			
			Icon destinationSquare = space[destX][destY].getIcon();
			if(destinationSquare != null)
			{
				System.err.println("Pawns cannot capture pieces by moving straight, nor can they move through pieces!");
				return false;
			}
			
			if(willExposeKing(image, PieceColor.BLACK, startX, startY, destX, destY))
			{
				System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
				return false;
			}
			return true;
		}
		else
		{
			if(Math.abs(destX - startX) != 1 || destY != startY + 1)
			{
				System.err.println("Invalid move! Pawns can only move diagonally by going one forward and one to the right or left, and only if they capture an enemy in the process!");
				return false;
			}
			
			Icon destinationSquare = space[destX][destY].getIcon();
			if(enPassantWindow > 0 && destX == enPassantX && destY == enPassantY); //note the semi-colon
				//if the conditions are right to en passant, don't do anything here but don't execute the next check either
			else if(destinationSquare != WPawn && destinationSquare != WKnight && destinationSquare != WBishop && destinationSquare != WRook && destinationSquare != WQueen && destinationSquare != WKing)
			{
				System.err.println("Pawns can only diagonally move if they capture an enemy in the process! This includes en passant.");
				return false;
			}
			
			if(willExposeKing(image, PieceColor.BLACK, startX, startY, destX, destY))
			{
				System.err.println("You cannot move a piece in a way that exposes your King to an enemy!");
				return false;
			}
			return true;
		}
	}
	
	//these methods if a piece has a clear path to the destination, and if it is allowed to capture the piece on the destination
	private boolean checkHorizontalCollision(PieceColor color, int startX, int startY, int destX, int destY)
	{
		if(destY > startY)
		{
			for (int i = startY + 1; i < destY; i++)
			{
				Icon thisTile = space[startX][i].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		else
		{
			for (int i = destY + 1; i < startY; i++)
			{
				Icon thisTile = space[destX][i].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		Icon destinationImage = space[destX][destY].getIcon();
		if (destinationImage == null)
			return true;
		else
			return getPieceColor(destinationImage) != color;
	}
	private boolean checkVerticalCollision(PieceColor color, int startX, int startY, int destX, int destY)
	{
		if(destX > startX)
		{
			for (int i = startX + 1; i < destX; i++)
			{
				Icon thisTile = space[i][destY].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		else
		{
			for (int i = destX + 1; i < startX; i++)
			{
				Icon thisTile = space[i][destY].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		Icon destinationImage = space[destX][destY].getIcon();
		if (destinationImage == null)
			return true;
		else
			return getPieceColor(destinationImage) != color;
	}
	private boolean checkRDiagonalCollision(PieceColor color, int startX, int startY, int destX, int destY)
	{
		if(startX > destX)
		{
			for(int i = 1; (destX + i < startX); i++)
			{
				Icon thisTile = space[destX + i][destY - i].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		else
		{
			for(int i = 1; (startX + i < destX); i++)
			{
				Icon thisTile = space[startX + i][startY - i].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		Icon destinationImage = space[destX][destY].getIcon();
		if (destinationImage == null)
			return true;
		else
			return getPieceColor(destinationImage) != color;
	}
	private boolean checkLDiagonalCollision(PieceColor color, int startX, int startY, int destX, int destY)
	{
		if(startX > destX)
		{
			for(int i = 1; (destX + i < startX); i++)
			{
				Icon thisTile = space[destX + i][destY + i].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		else
		{
			for(int i = 1; (startX + i < destX); i++)
			{
				Icon thisTile = space[startX + i][startY + i].getIcon();
				if (thisTile != null)
					return false;
			}
		}
		Icon destinationImage = space[destX][destY].getIcon();
		if (destinationImage == null)
			return true;
		else
			return getPieceColor(destinationImage) != color;
	}
	
	//returns true if the given piece is between the friendly king and an enemy bishop, rook, or queen
	//does NOT determine if the desired move would leave the king vulnerable, that's the caller's job
	private boolean pinned(Icon image, PieceColor color, int currentX, int currentY)
	{
		if(PieceColor.equalsWhite(color)) //handles the logic for white pieces
		{
			//you can't be pinned if you aren't on the same row, column, or diagonal as the king
			if((currentX != whiteKingX && currentY != whiteKingY) && (Math.abs(whiteKingX - currentX) != Math.abs(whiteKingY - currentY)))
				return false;
			if(currentX == whiteKingX) //if you are on the same x-coordinate as the king, check for a pin by a rook or queen
			{
				if(currentY > whiteKingY)
				{
					for(int i = currentY - 1; i > whiteKingY; i--)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentY + 1; i < 8; i++)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece == BRook || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = currentY + 1; i < whiteKingY; i++)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentY - 1; i > -1; i--)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece == BRook || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
			if(currentY == whiteKingY) //if you are on the same y-coordinate as the king, check for a pin by a rook or queen
			{
				if(currentX > whiteKingX)
				{
					for(int i = currentX - 1; i > whiteKingX; i--)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentX + 1; i < 8; i++)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece == BRook || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = currentX + 1; i < whiteKingX; i++)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentX - 1; i > -1; i--)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece == BRook || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
			if(currentX - whiteKingX == currentY - whiteKingY) //if you are on the same left-diagonal as the king, check for a pin by a bishop or queen
			{
				if(currentX > whiteKingX)
				{
					for(int i = (currentX - whiteKingX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[whiteKingX + i][whiteKingY + i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = (currentX - whiteKingX) + 1; (currentX + i < 8 && currentY + i < 8); i++)
					{
						Icon thisPiece = space[whiteKingX + i][whiteKingY + i].getIcon();
						if(thisPiece == BBishop || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = (whiteKingX - currentX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[currentX + i][currentY + i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = 1; (currentX - i > -1 && currentY - i > -1); i++)
					{
						Icon thisPiece = space[currentX - i][currentY - i].getIcon();
						if(thisPiece == BBishop || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
			else //if you are on the same right-diagonal as the king, check for a pin by a bishop or queen
			{
				if (currentX > whiteKingX)
				{
					for(int i = (currentX - whiteKingX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[whiteKingX + i][whiteKingY - i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = (currentX - whiteKingX) + 1; (currentX + i < 8 && currentY - i > 0); i++)
					{
						Icon thisPiece = space[whiteKingX + i][whiteKingY - i].getIcon();
						if(thisPiece == BBishop || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = (whiteKingX - currentX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[whiteKingX - i][whiteKingY + i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = 1; (currentX - i > -1 && currentY - i < 8); i++)
					{
						Icon thisPiece = space[currentX - i][currentY + i].getIcon();
						if(thisPiece == BBishop || thisPiece == BQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
		}
		else //handles the logic for black pieces
		{
			//you can't be pinned if you aren't on the same row, column, or diagonal as the king
			if((currentX != blackKingX && currentY != blackKingY) && (Math.abs(blackKingX - currentX) != Math.abs(blackKingY - currentY)))
				return false;
			if(currentX == blackKingX) //if you are on the same x-coordinate as the king, check for a pin by a rook or queen
			{
				if(currentY > blackKingY)
				{
					for(int i = currentY - 1; i > blackKingY; i--)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentY + 1; i < 8; i++)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece == WRook || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = currentY + 1; i < blackKingY; i++)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentY - 1; i > -1; i--)
					{
						Icon thisPiece = space[currentX][i].getIcon();
						if(thisPiece == WRook || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
			if(currentY == blackKingY) //if you are on the same y-coordinate as the king, check for a pin by a rook or queen
			{
				if(currentX > blackKingX)
				{
					for(int i = currentX - 1; i > blackKingX; i--)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentX + 1; i < 8; i++)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece == WRook || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = currentX + 1; i < blackKingX; i++)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = currentX - 1; i > -1; i--)
					{
						Icon thisPiece = space[i][currentY].getIcon();
						if(thisPiece == WRook || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
			if(currentX - blackKingX == currentY - blackKingY) //if you are on the same left-diagonal as the king, check for a pin by a bishop or queen
			{
				if(currentX > blackKingX)
				{
					for(int i = (currentX - blackKingX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[blackKingX + i][blackKingY + i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = (currentX - blackKingX) + 1; (currentX + i < 8 && currentY + i < 8); i++)
					{
						Icon thisPiece = space[blackKingX + i][blackKingY + i].getIcon();
						if(thisPiece == WBishop || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = (blackKingX - currentX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[currentX + i][currentY + i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = 1; (currentX - i > -1 && currentY - i > -1); i++)
					{
						Icon thisPiece = space[currentX - i][currentY - i].getIcon();
						if(thisPiece == WBishop || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
			else //if you are on the same right-diagonal as the king, check for a pin by a bishop or queen
			{
				if (currentX > blackKingX)
				{
					for(int i = (currentX - blackKingX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[blackKingX + i][blackKingY - i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = (currentX - blackKingX) + 1; (currentX + i < 8 && currentY - i > 0); i++)
					{
						Icon thisPiece = space[blackKingX + i][blackKingY - i].getIcon();
						if(thisPiece == WBishop || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
				else
				{
					for(int i = (blackKingX - currentX) - 1; i > 0; i--)
					{
						Icon thisPiece = space[blackKingX - i][blackKingY + i].getIcon();
						if(thisPiece != null)
							return false;
					}
					for(int i = 1; (currentX - i > -1 && currentY - i < 8); i++)
					{
						Icon thisPiece = space[currentX - i][currentY + i].getIcon();
						if(thisPiece == WBishop || thisPiece == WQueen)
							return true;
						if(thisPiece != null)
							return false;
						else
							continue;
					}
					return false;
				}
			}
		}
	}
	//if the piece is pinned, determine whether performing the desired move would leave the king under attack
	//if the piece isn't pinned in the first place, return false
	private boolean willExposeKing(Icon image, PieceColor color, int startX, int startY, int destX, int destY)
	{
		int yourKingX = PieceColor.equalsWhite(color) ? whiteKingX : blackKingX;
		int yourKingY = PieceColor.equalsWhite(color) ? whiteKingY : blackKingY;
		if(pinned(image, color, startX, startY))
		{
			if(startX == yourKingX)
			{
				if(destX != startX)
					return true;
				if(destY >= yourKingY && startY < yourKingY)
					return true;
				if(destY <= yourKingY && startY > yourKingY)
					return true;
			}
			if(startY == yourKingY)
			{
				if(destY != startY)
					return true;
				if(destX >= yourKingX && startX < yourKingX)
					return true;
				if(destX <= yourKingX && startX > yourKingX)
					return true;
			}
			if(startX - yourKingX == startY - yourKingY)
			{
				if(destX - yourKingX != destY - yourKingY)
					return true;
				if(destX >= yourKingX && startX < yourKingX)
					return true;
				if(destX <= yourKingX && startX > yourKingX)
					return true;
			}
			if(startX - yourKingX == 0 - (startY - yourKingY))
			{
				if(destX - yourKingX != 0 - (destY - yourKingY))
					return true;
				if(destX >= yourKingX && startX < yourKingX)
					return true;
				if(destX <= yourKingX && startX > yourKingX)
					return true;
			}
		}
		return false;
	}
	
	//determines if the given square is under attack by the color opposite the 'color' parameter
	//takes three flag arguments that modify how it determines if the square is under attack
	//for example, a king absolutely cannot move to a square under attack by anything hostile, so countPinnedPieces and countKing should be set
	//on the other hand, if you want to know if there are any pieces able to block a check, there's no point counting a pawn that can't actually diagonally-move to get in the way
	private boolean attacked(PieceColor color, int x, int y, boolean countPinnedPieces, boolean countKing, boolean ignorePawns)
	{
		if(countKing && ignorePawns)
		{
			return (diagonalAttacked(color, x, y, countPinnedPieces) || horizontalAttacked(color, x, y, countPinnedPieces) 
					|| knightAttacked(color, x, y, countPinnedPieces) || kingAttacked(color, x , y));
		}
		if(countKing)
		{
			return (pawnAttacked(color, x, y, countPinnedPieces) || diagonalAttacked(color, x, y, countPinnedPieces) 
					|| horizontalAttacked(color, x, y, countPinnedPieces) || knightAttacked(color, x, y, countPinnedPieces) || kingAttacked(color, x , y));
		}
		if(ignorePawns)
		{
			return (diagonalAttacked(color, x, y, countPinnedPieces) || horizontalAttacked(color, x, y, countPinnedPieces) 
					|| knightAttacked(color, x, y, countPinnedPieces));
		}
		return (pawnAttacked(color, x, y, countPinnedPieces) || diagonalAttacked(color, x, y, countPinnedPieces) 
					|| horizontalAttacked(color, x, y, countPinnedPieces) || knightAttacked(color, x, y, countPinnedPieces));
	}
	private boolean pawnAttacked(PieceColor color, int x, int y, boolean countPinnedPieces)
	{
		if(color == PieceColor.WHITE && y > 0)
		{
			if(x > 0)
			{
				if(space[x-1][y-1].getIcon() == BPawn)
					if(countPinnedPieces || !pinned(BPawn, PieceColor.BLACK, x-1, y-1))
						return true;
			}
			if(x < 7)
			{
				if(space[x+1][y-1].getIcon() == BPawn)
					if(countPinnedPieces || !pinned(BPawn, PieceColor.BLACK, x+1, y-1))
						return true;
			}
		}
		else if (y < 7)
		{
			if(x > 0)
			{
				if(space[x-1][y+1].getIcon() == WPawn)
					if(countPinnedPieces || !pinned(WPawn, PieceColor.WHITE, x-1, y+1))
						return true;
			}
			if(x < 7)
			{
				if(space[x+1][y+1].getIcon() == WPawn)
					if(countPinnedPieces || !pinned(WPawn, PieceColor.WHITE, x+1, y+1))
						return true;
			}
		}
		return false;
	}
	private boolean diagonalAttacked(PieceColor color, int x, int y, boolean countPinnedPieces)
	{
		Icon opposingBishop = (PieceColor.equalsWhite(color)) ? BBishop : WBishop;
		Icon opposingQueen = (PieceColor.equalsWhite(color)) ? BQueen : WQueen;
		for(int i = 1; (x+i <= 7 && y+i <= 7); i++)
		{
			if(space[x+i][y+i].getIcon() == opposingQueen || space[x+i][y+i].getIcon() == opposingBishop)
				if(countPinnedPieces || !pinned(space[x+i][y+i].getIcon(), PieceColor.flipColor(color), x+i, y+i))
					return true;
			if(space[x+i][y+i].getIcon() != null)
				break;
		}
		for(int i = 1; (x+i <= 7 && y-i >= 0); i++)
		{
			if(space[x+i][y-i].getIcon() == opposingQueen || space[x+i][y-i].getIcon() == opposingBishop)
				if(countPinnedPieces || !pinned(space[x+i][y-i].getIcon(), PieceColor.flipColor(color), x+i, y+i))
					return true;
			if(space[x+i][y-i].getIcon() != null)
				break;
		}
		for(int i = 1; (x-i >= 0 && y+i <= 7); i++)
		{
			if(space[x-i][y+i].getIcon() == opposingQueen || space[x-i][y+i].getIcon() == opposingBishop)
				if(countPinnedPieces || !pinned(space[x-i][y+i].getIcon(), PieceColor.flipColor(color), x-i, y+i))
					return true;
			if(space[x-i][y+i].getIcon() != null)
				break;
		}
		for(int i = 1; (x-i >= 0 && y-i >= 0); i++)
		{
			if(space[x-i][y-i].getIcon() == opposingQueen || space[x-i][y-i].getIcon() == opposingBishop)
				if(countPinnedPieces || !pinned(space[x-i][y-i].getIcon(), PieceColor.flipColor(color), x-i, y-i))
					return true;
			if(space[x-i][y-i].getIcon() != null)
				break;
		}
		return false;
	}
	private boolean horizontalAttacked(PieceColor color, int x, int y, boolean countPinnedPieces)
	{
		Icon opposingRook = (PieceColor.equalsWhite(color)) ? BRook : WRook;
		Icon opposingQueen = (PieceColor.equalsWhite(color)) ? BQueen : WQueen;
		for(int i = x + 1; i <= 7; i++)
		{
			if(space[i][y].getIcon() == opposingQueen || space[i][y].getIcon() == opposingRook)
				if(countPinnedPieces || !pinned(space[i][y].getIcon(), PieceColor.flipColor(color), i, y))
					return true;
			if(space[i][y].getIcon() != null)
				break;
		}
		for(int i = x - 1; i >= 0; i--)
		{
			if(space[i][y].getIcon() == opposingQueen || space[i][y].getIcon() == opposingRook)
				if(countPinnedPieces || !pinned(space[i][y].getIcon(), PieceColor.flipColor(color), i, y))
					return true;
			if(space[i][y].getIcon() != null)
				break;
		}
		for(int i = y + 1; i <= 7; i++)
		{
			if(space[x][i].getIcon() == opposingQueen || space[x][i].getIcon() == opposingRook)
				if(countPinnedPieces || !pinned(space[x][i].getIcon(), PieceColor.flipColor(color), x, i))
					return true;
			if(space[x][i].getIcon() != null)
				break;
		}
		for(int i = y - 1; i >= 0; i--)
		{
			if(space[x][i].getIcon() == opposingQueen || space[x][i].getIcon() == opposingRook)
				if(countPinnedPieces || !pinned(space[x][i].getIcon(), PieceColor.flipColor(color), x, i))
					return true;
			if(space[x][i].getIcon() != null)
				break;
		}
		return false;
	}
	private boolean knightAttacked(PieceColor color, int x, int y, boolean countPinnedPieces)
	{
		Icon opposingKnight = (PieceColor.equalsWhite(color)) ? BKnight : WKnight;
		if(x > 0)
		{
			if(x > 1)
			{
				if(y > 0)
					if(space[x-2][y-1].getIcon() == opposingKnight)
						if(countPinnedPieces || !pinned(space[x-2][y-1].getIcon(), PieceColor.flipColor(color), x-2, y-1))
							return true;
				if(y < 7)
					if(space[x-2][y+1].getIcon() == opposingKnight)
						if(countPinnedPieces || !pinned(space[x-2][y+1].getIcon(), PieceColor.flipColor(color), x-2, y+1))
							return true;
			}
			if(y > 1)
				if(space[x-1][y-2].getIcon() == opposingKnight)
					if(countPinnedPieces || !pinned(space[x-1][y-2].getIcon(), PieceColor.flipColor(color), x-1, y-2))
						return true;
			if(y < 6)
				if(space[x-1][y+2].getIcon() == opposingKnight)
					if(countPinnedPieces || !pinned(space[x-1][y+2].getIcon(), PieceColor.flipColor(color), x-1, y+2))
						return true;
		}
		if(x < 7)
		{
			if (x < 6)
			{
				if(y > 0)
					if(space[x+2][y-1].getIcon() == opposingKnight)
						if(countPinnedPieces || !pinned(space[x+2][y-1].getIcon(), PieceColor.flipColor(color), x+2, y-1))
							return true;
				if(y < 7)
					if(space[x+2][y+1].getIcon() == opposingKnight)
						if(countPinnedPieces || !pinned(space[x+2][y+1].getIcon(), PieceColor.flipColor(color), x+2, y+1))
							return true;
			}
			if(y > 1)
				if(space[x+1][y-2].getIcon() == opposingKnight)
					if(countPinnedPieces || !pinned(space[x+1][y-2].getIcon(), PieceColor.flipColor(color), x+1, y-2))
						return true;
			if(y < 6)
				if(space[x+1][y+2].getIcon() == opposingKnight)
					if(countPinnedPieces || !pinned(space[x+1][y+2].getIcon(), PieceColor.flipColor(color), x+1, y+2))
						return true;
		}
		return false;
	}
	private boolean kingAttacked(PieceColor color, int x, int y)
	{
		Icon opposingKing = (PieceColor.equalsWhite(color)) ? BKing : WKing;
		if(x > 0)
		{
			if(space[x-1][y].getIcon() == opposingKing)
				return true;
			if(y > 0)
				if(space[x-1][y-1].getIcon() == opposingKing)
					return true;
			if(y < 7)
				if(space[x-1][y+1].getIcon() == opposingKing)
					return true;
		}
		if(x < 7)
		{
			if(space[x+1][y].getIcon() == opposingKing)
				return true;
			if(y > 0)
				if(space[x+1][y-1].getIcon() == opposingKing)
					return true;
			if(y < 7)
				if(space[x+1][y+1].getIcon() == opposingKing)
					return true;
		}
		if(y > 0)
			if(space[x][y-1].getIcon() == opposingKing)
				return true;
		if(y < 7)
			if(space[x][y+1].getIcon() == opposingKing)
				return true;
		
		return false;
	}
	
	//returns a mapping of every square containing a piece attacking the target square to each piece on those squares
	private LinkedHashMap<String, Icon> countAttacks(PieceColor color, int x, int y)
	{
		LinkedHashMap<String, Icon> attacks = new LinkedHashMap<String, Icon>();
		
		if(pawnAttacked(color, x, y, true))
		{
			if(PieceColor.equalsWhite(color))
			{
				if(space[x-1][y-1].getIcon() == BPawn)
					attacks.put(String.format("%d %d", x-1, y-1), BPawn);
				if(space[x+1][y-1].getIcon() == BPawn)
					attacks.put(String.format("%d %d", x+1, y-1), BPawn);
			}
			else
			{
				if(space[x-1][y+1].getIcon() == WPawn)
					attacks.put(String.format("%d %d", x-1, y+1), WPawn);
				if(space[x+1][y+1].getIcon() == WPawn)
					attacks.put(String.format("%d %d", x+1, y+1), WPawn);
			}
		}
		
		{
			Icon opposingBishop = (PieceColor.equalsWhite(color)) ? BBishop : WBishop;
			Icon opposingQueen = (PieceColor.equalsWhite(color)) ? BQueen : WQueen;
				for(int i = 1; (x+i <= 7 && y+i <= 7); i++)
				{
					if(space[x+i][y+i].getIcon() == opposingQueen || space[x+i][y+i].getIcon() == opposingBishop)
					{
						attacks.put(String.format("%d %d", x+i, y+i), (space[x+i][y+i].getIcon () == opposingBishop) ? opposingBishop : opposingQueen);
						break;
					}
					if(space[x+i][y+i].getIcon() != null)
						break;
				}
				for(int i = 1; (x+i <= 7 && y-i >= 0); i++)
				{
					if(space[x+i][y-i].getIcon() == opposingQueen || space[x+i][y-i].getIcon() == opposingBishop)
					{
						attacks.put(String.format("%d %d", x+i, y-i), (space[x+i][y-i].getIcon () == opposingBishop) ? opposingBishop : opposingQueen);
						break;
					}
					if(space[x+i][y-i].getIcon() != null)
						break;
				}
				for(int i = 1; (x-i >= 0 && y+i <= 7); i++)
				{
					if(space[x-i][y+i].getIcon() == opposingQueen || space[x-i][y+i].getIcon() == opposingBishop)
					{
						attacks.put(String.format("%d %d", x-i, y+i), (space[x-i][y+i].getIcon () == opposingBishop) ? opposingBishop : opposingQueen);
						break;
					}
					if(space[x-i][y+i].getIcon() != null)
						break;
				}
				for(int i = 1; (x-i >= 0 && y-i >= 0); i++)
				{
					if(space[x-i][y-i].getIcon() == opposingQueen || space[x-i][y-i].getIcon() == opposingBishop)
					{
						attacks.put(String.format("%d %d", x-i, y-i), (space[x-i][y-i].getIcon () == opposingBishop) ? opposingBishop : opposingQueen);
						break;
					}
					if(space[x-i][y-i].getIcon() != null)
						break;
				}
		}
		
		{
			Icon opposingRook = (PieceColor.equalsWhite(color)) ? BRook : WRook;
			Icon opposingQueen = (PieceColor.equalsWhite(color)) ? BQueen : WQueen;
			for(int i = x + 1; i <= 7; i++)
			{
				if(space[i][y].getIcon() == opposingQueen || space[i][y].getIcon() == opposingRook)
				{
					attacks.put(String.format("%d %d", i, y), (space[i][y].getIcon() == opposingRook) ? opposingRook : opposingQueen);
					break;
				}
				if(space[i][y].getIcon() != null)
					break;
			}
			for(int i = x - 1; i >= 0; i--)
			{
				if(space[i][y].getIcon() == opposingQueen || space[i][y].getIcon() == opposingRook)
				{
					attacks.put(String.format("%d %d", i, y), (space[i][y].getIcon() == opposingRook) ? opposingRook : opposingQueen);
					break;
				}
				if(space[i][y].getIcon() != null)
					break;
			}
			for(int i = y + 1; i <= 7; i++)
			{
				if(space[x][i].getIcon() == opposingQueen || space[x][i].getIcon() == opposingRook)
				{
					attacks.put(String.format("%d %d", x, i), (space[x][i].getIcon() == opposingRook) ? opposingRook : opposingQueen);
					break;
				}
				if(space[x][i].getIcon() != null)
					break;
			}
			for(int i = y - 1; i >= 0; i--)
			{
				if(space[x][i].getIcon() == opposingQueen || space[x][i].getIcon() == opposingRook)
				{
					attacks.put(String.format("%d %d", x, i), (space[x][i].getIcon() == opposingRook) ? opposingRook : opposingQueen);
					break;
				}
				if(space[x][i].getIcon() != null)
					break;
			}
		}
		
		{
			Icon opposingKnight = (PieceColor.equalsWhite(color)) ? BKnight : WKnight;
			if(x > 0)
			{
				if(x > 1)
				{
					if(y > 0)
						if(space[x-2][y-1].getIcon() == opposingKnight)
							attacks.put(String.format("%d %d", x-2, y-1), opposingKnight);
					if(y < 7)
						if(space[x-2][y+1].getIcon() == opposingKnight)
							attacks.put(String.format("%d %d", x-2, y+1), opposingKnight);
				}
				if(y > 1)
					if(space[x-1][y-2].getIcon() == opposingKnight)
						attacks.put(String.format("%d %d", x-1, y-2), opposingKnight);
				if(y < 6)
					if(space[x-1][y+2].getIcon() == opposingKnight)
						attacks.put(String.format("%d %d", x-1, y+2), opposingKnight);
			}
			if(x < 7)
			{
				if (x < 6)
				{
					if(y > 0)
						if(space[x+2][y-1].getIcon() == opposingKnight)
							attacks.put(String.format("%d %d", x+2, y-1), opposingKnight);
					if(y < 7)
						if(space[x+2][y+1].getIcon() == opposingKnight)
							attacks.put(String.format("%d %d", x+2, y+1), opposingKnight);
				}
				if(y > 1)
					if(space[x+1][y-2].getIcon() == opposingKnight)
						attacks.put(String.format("%d %d", x+1, y-2), opposingKnight);
				if(y < 6)
					if(space[x+1][y+2].getIcon() == opposingKnight)
						attacks.put(String.format("%d %d", x+1, y+2), opposingKnight);
			}
		}
		
		if(kingAttacked(color, x, y))
		{
			if(PieceColor.equalsWhite(color))
				attacks.put(String.format("%d %d", blackKingX, blackKingY), BKing);
			else
				attacks.put(String.format("%d %d", whiteKingX, whiteKingY), WKing);
		}
		
		return attacks;
	}

	// main sets up the general java window or "Frame"
	public static void main(String[] args) {
		JFrame frame = new ChessGUI();
		frame.setTitle("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// Unimplemented methods that don't do anything for the code, but we can't
	// implement mouseListener without them.
	// Think of it as a little code grave yard.
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}