package game1024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * NumberGame implements NumberSlider to create the game engine behind the 1024 game
 * @author Justin Dowty
 *
 */
public class NumberGame implements NumberSlider{
	private int[][] gameBoard;
	private int winningValue;
	private int currentScore;
	private Stack<ArrayList<Cell>> stack = new Stack<ArrayList<Cell>>();
	private Stack<Integer> scoreStack = new Stack<Integer>();
	Random rand = new Random();
	
	/**
	 * @param height int height of board, or number of rows
	 * @param width int width of board, or number of columns
	 * @param winningValue int for desired winning value for board
	 * @throws IllegalArgumentException if winningValue is not a power of 2
	 */
	public void resizeBoard(int height, int width, int winningValue){
		gameBoard = new int[height][width];
		currentScore = 0;
		double testVal = (double) winningValue;
		while(testVal > 1){
			testVal = testVal / 2;
		}
		this.winningValue = winningValue;
		if(winningValue < 0 || testVal != 1){
			throw new IllegalArgumentException();			
		}		
	}

	/**
	 * resets the gameBoard to initial state, which has two random values placed on the board
	 */
	public void reset(){
		stack.clear();
		scoreStack.clear();
		currentScore = 0;
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				gameBoard[i][j] = 0;
			}
		}
		
		int rand1 = rand.nextInt(gameBoard.length);
		int rand2 = rand.nextInt(gameBoard[0].length);
		int rand3 = rand.nextInt(gameBoard.length);
		int rand4 = rand.nextInt(gameBoard[0].length);
		while(rand1 == rand3 && rand2 == rand4){
			rand3 = rand.nextInt(gameBoard.length);
			rand4 = rand.nextInt(gameBoard[0].length);
		}	
		
		// randValues are determined by a random int from 0 to 9
		// if randValue is 0 (10% chance) then randValue will be 2
		// otherwise (90% chance) randValue will be 1
		// this seems to be similar chances to the original 1024 game
		int randValue1 = rand.nextInt(10);
		int randValue2 = rand.nextInt(10);
		if(randValue1 == 0){
			randValue1 = 2;
		}
		else{
			randValue1 = 1;
		}
		if(randValue2 == 0){
			randValue2 = 2;
		}
		else{
			randValue2 = 1;
		}
		
		gameBoard[rand1][rand2] = randValue1;
		gameBoard[rand3][rand4] = randValue2;
		
		stack.push(getNonEmptyTiles());
	}
	
	/**
	 * @param ref an integer array that gets read into the gameBoard
	 */
	public void setValues(final int[][] ref){
		gameBoard = new int[ref.length][ref[0].length];
		for(int i = 0; i < ref.length; i++){
			for(int j = 0; j < ref[0].length; j++){
				gameBoard[i][j] = ref[i][j];
			}
		}
		stack.push(getNonEmptyTiles()); // push state to top of stack if values are set with this method
	}
	
	/**
	 * places a random value at an available location on the gameBoard
	 * @return Cell value that is placed
	 * @throws IllegalStateException if the board is full
	 */
	public Cell placeRandomValue(){
		int boardArea = gameBoard.length * gameBoard[0].length;
		int count = 0;
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				if(gameBoard[i][j] != 0){
					count++;					
				}
			}
		}
		if(count == boardArea){
			throw new IllegalStateException();
		}
		int rand1 = rand.nextInt(gameBoard.length);
		int rand2 = rand.nextInt(gameBoard[0].length);
		while(gameBoard[rand1][rand2] != 0){
			rand1 = rand.nextInt(gameBoard.length);
			rand2 = rand.nextInt(gameBoard[0].length);
		}
		// randValue is determined by a random int from 0 to 9
		// if randValue is 0 (10% chance) then randValue will be 2
		// otherwise (90% chance) randValue will be 1
		// this seems to be similar chances to the original 1024 game
		int randValue = rand.nextInt(10);
		if(randValue == 0){
			randValue = 2;
		}
		else{
			randValue = 1;
		}
		gameBoard[rand1][rand2] = randValue;
		Cell value = new Cell(rand1, rand2, 1);
		return value;
	}
	
	/**
	 * slide performs the calculations of a slide and populates the gameBoard with correct new values
	 * this method uses the slideLogicForRow method found below it to perform these calculations
	 * @param dir takes a parameter of type SlideDirection to know which way the slide is to calculated
	 * @return boolean value indicating whether or not the board change when slide was invoked
	 */
	public boolean slide(SlideDirection dir){
		/**
		 *  previousBoard holds information of the board before the slide
		 *  previousBoard is loaded with a for loop
		 */
		int[][] previousBoard = new int[gameBoard.length][gameBoard[0].length];
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				previousBoard[i][j] = gameBoard[i][j];
			}
		}
		/**
		 * The current state gets pushed to the top of the stack, if the board remains unchanged for this slide that 
		 * save will be popped to avoid having unchanged boards pile on the stack
		 */
		stack.push(getNonEmptyTiles());
		scoreStack.push(currentScore);
		int[] tempRow = new int[0]; // tempRow is used to hold the information of the current row being evaluated before
									// the slide
		/**
		 * The necessity for the if(right or left) and if(up or down) is that for right and left evaluations the
		 * length of tempRow needs to be set to the length of a row on the gameBoard, while the up and down
		 * evaluations need to set tempRow to the length of a column
		 */
		if(dir == SlideDirection.RIGHT || dir == SlideDirection.LEFT){
			tempRow = new int[gameBoard[0].length];
			for(int i = 0; i < gameBoard.length; i++){ // looping over each row
				/**
				 * For the right slide direction the row needs to be read in backwards so that the slideLogicFor Row
				 * function can operate on that row properly, tempRow then gets flipped as its loaded back into the board
				 * so the tiles will be in the proper position. The slideLogicForRow function is evaluating the row to
				 * slide left
				 */
				if(dir == SlideDirection.RIGHT){
					for(int j = 0; j < gameBoard[0].length; j++){ // Row gets read in backwards so slideLogicForRow will work correctly
						tempRow[j] = gameBoard[i][gameBoard[0].length - 1 - j];				
					}				
					tempRow = slideLogicForRow(tempRow);
					tempRow = flipArray(tempRow); // Row gets flipped to be read back into the board correctly
				}
				/**
				 * The left direction does not need to be flipped because of how the slideLogicForRow function
				 * always reads the information left to right, performing that slide logic correctly to the left
				 */
				if(dir == SlideDirection.LEFT){
					for(int j = 0; j < gameBoard[0].length; j++){
						tempRow[j] = gameBoard[i][j];				
					}
					tempRow = slideLogicForRow(tempRow);
				}

				/**
				 * This loop loads each element of the post-slide tempRow back into the gameBoard
				 */
				for(int k = 0; k < gameBoard[0].length; k++){
					gameBoard[i][k] = tempRow[k];
				}
			}			
		}
		if(dir == SlideDirection.UP || dir == SlideDirection.DOWN){
			tempRow = new int[gameBoard.length];
			for(int i = 0; i < gameBoard[0].length; i++){ // looping over each column
				if(dir == SlideDirection.UP){
					for(int j = 0; j < gameBoard.length; j++){ //Reading column in from top
						tempRow[j] = gameBoard[j][i];
					}
					tempRow = slideLogicForRow(tempRow);					
				}
				if(dir == SlideDirection.DOWN){
					for(int j = 0; j < gameBoard.length; j++){ //Reading column in from bottom
						tempRow[j] = gameBoard[gameBoard.length - 1 - j][i];
					}
					tempRow = slideLogicForRow(tempRow);	
					tempRow = flipArray(tempRow); // Row gets flipped to be read back into the board correctly
				}
				/**
				 * This loop loads each element of the post-slide tempRow back into the gameBoard
				 */
				for(int k = 0; k < gameBoard.length; k++){
					gameBoard[k][i] = tempRow[k];
				}
			}
			
		}
		/**
		 * checks to see if the gameBoard has changed after slide was invoked
		 */
		if(Arrays.deepEquals(previousBoard, gameBoard)){
			stack.pop(); // pops the top value that was set above in slide method 
						 // so that an unchanged board doesn't stay in stack
			scoreStack.pop();
			return false;
		}
		placeRandomValue(); // if board has changed random value is placed to set up next board
		return true;
	}
		
	/**
	 * slideLogicForRow takes in a row, calculates the row after the slide, then returns that row
	 * @param row takes in an int array row to be processed for the slide
	 * @return returns the row after the slide has taken place
	 */
	private int[] slideLogicForRow(int[] row){
		int count;
		for(int i = 0; i < row.length - 1; i++){
			count = 0;
			do{
				count++;
				if(i+count == row.length){
					break;
				}
				if(row[i] == 0){
					break;
				}
				if(row[i] == row[i+count]){
					row[i] *= 2;
					currentScore += row[i];
					row[i+count] = 0;
					break;
				}
			}while(row[i+count] == 0);			
		}
		for(int p = 0; p < row.length; p ++){      // outter loop makes sure that enough attempts 
												   // at sliding all of the numbers happen
			for(int i = row.length-1; i > 0; i--){
				if(row[i-1] == 0){
					row[i-1] = row[i];
					row[i] = 0;
				}
			}
		}
		return row;
	}
	
	/**
	 * flips an array
	 * @param array int array to be flipped
	 * @return flipped array
	 */
	private int[] flipArray(int[] array){
		for(int i = 0; i < array.length/2; i++){
			int temp = array[i];
			array[i] = array[array.length - 1 - i];	
			array[array.length - 1 - i] = temp;
		}
		return array;
	}
	
	/**
	 * @return an ArrayList of Cell values that represent all spots on gameBoard with a nonzero value
	 */
	public ArrayList<Cell> getNonEmptyTiles(){
		ArrayList<Cell> list = new ArrayList<Cell>();
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				if(gameBoard[i][j] != 0){
					list.add(new Cell(i,j,gameBoard[i][j]));
				}
			}
		}
		return list;
	}
	
	/**
	 * @return the current status of the game
	 */
	public GameStatus getStatus(){
		// boolean value lost is used to determine if the player has lost the game
		// initialized to true to start, and is set to false within for loop if another move is available
		// if there is a winning value on the board that means the user won, so USER_WON is returned 
		boolean lost = true;
		for(int i = 0; i < gameBoard.length-1; i++){
			for(int j = 0; j < gameBoard[0].length-1; j++){
				if(gameBoard[i][j] == gameBoard[i][j+1] || gameBoard[i][j] == gameBoard[i+1][j] || gameBoard[i][j] == 0 || gameBoard[i+1][j] == 0 || gameBoard[i][j+1] == 0){
					lost = false;
				}
				// checking the bottom right square
				if(gameBoard[gameBoard.length-1][gameBoard[0].length-1] == gameBoard[gameBoard.length-2][gameBoard[0].length-1] || gameBoard[gameBoard.length-1][gameBoard[0].length-1] == gameBoard[gameBoard.length-1][gameBoard[0].length-2] || gameBoard[gameBoard.length-1][gameBoard[0].length-1] == 0){
					lost = false;
				}
				if(gameBoard[gameBoard.length-1][j] == gameBoard[gameBoard.length-1][j+1] || gameBoard[i][gameBoard[0].length-1] == gameBoard[i+1][gameBoard[0].length-1]){
					lost = false;
				}
				if(gameBoard[i][j] == winningValue || gameBoard[i+1][j] == winningValue || gameBoard[i][j+1] == winningValue || gameBoard[i+1][j+1] == winningValue){
					return GameStatus.USER_WON;
				}
			}
		}
		if(lost){
			return GameStatus.USER_LOST;
		}
		return GameStatus.IN_PROGRESS;
	}
	
	/**
	 * @return the gameBoard to its previous state, does so by initializing board to 0
	 * then reads from the top stack state to repopulate the board, then pops that state
	 */
	public void undo(){
		if(stack.isEmpty() || scoreStack.isEmpty()){
			throw new IllegalStateException();
		}
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				gameBoard[i][j] = 0;
			}
		}
		for(Cell c: stack.peek()){
			gameBoard[c.row][c.column] = c.value;
		}
		currentScore = scoreStack.pop();
		stack.pop();
		
	}		
	
	/**
	 * prints board to screen
	 */
	public void printBoard(){
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				System.out.print(" " + gameBoard[i][j] + " ");
			}
			System.out.println("\n");
		}
	}
	
	public int getCurrentScore(){
		return this.currentScore;
	}
	
	
	
	
}
