package backtracking;

import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Sudoku {
	private static final int N = 9; // board size
	private static final int Nsqrt = 3; // block size
	char[][] board = new char[N][N]; // Sudoku board
	/*
	 * level: 
	 * 1 (EASY) - mostly filled board
	 * 2 (MEDIUM) - halfly filled board
	 * 3 (HARD) - mostly empty board 
	 */
	private static final double EMPTY_RATIO_EASY = 0.25, 
			EMPTY_RATIO_MEDIUM = 0.5, 
			EMPTY_RATIO_HARD = 0.75;
	
	/**
	 * construct a partially filled board randomly. Difficulty level is set to default, i.e., 1
	 */
	public Sudoku() {
		int level = 1;
		createRandomSudoku(level);
	}
	
	/**
	 * construct a partially filled board randomly
	 * @param level - difficulty level
	 */
	public Sudoku(int level) {
		createRandomSudoku(level);
	}
	
	/**
	 * construct a partially filled board with input
	 * @param board - specified input
	 */
	public Sudoku(String[] board) {
		for(int i=0; i<N; i++) {
			String[] str = board[i].split(",");
	    	for(int j=0; j<N; j++) {
	    		this.board[i][j] = str[j].charAt(0);
	    		if(this.board[i][j]=='0') {
	    			this.board[i][j] = 0;
	    		}
	    	}
		}
		System.out.println("Original board:");
		printSudoku();
	}
	
	/**
	 * create a partially filled Sudoku board
	 * @param level - difficulty level
	 */
	public void createRandomSudoku(int level) {
		int[] nums = new int[N];
		for(int i=0; i<N; i++) {
			nums[i] = i+1;
		}
		shuffle(nums);
		
		for(int j=0; j<N; j++) { // fill in the first row of the Sudoku
			board[0][j] = (char)(nums[j] + '0');
		}		

		Random rm = new Random();
		
		int starti = rm.nextInt(N), startj = rm.nextInt(N);
		Stack<Integer> emptyLocationList = getEmptySpots(starti, startj);
		solve(emptyLocationList); // solve the sudoku
		
		int totalLocations = N*N, remove = 0;
		switch(level) {
			case 2:
				remove = (int)(totalLocations * EMPTY_RATIO_MEDIUM);
				break;
			case 3:
				remove = (int)(totalLocations * EMPTY_RATIO_HARD);
				break;
			case 1:
			default:
				remove = (int)(totalLocations * EMPTY_RATIO_EASY);
		}

		boolean[] used = new boolean[totalLocations];
		while(remove-->0) {
			int position = rm.nextInt(totalLocations);
			while(used[position]) {
				position = rm.nextInt(totalLocations);
			}
			used[position] = true;
			board[position/N][position%N] = 0;
		}

		System.out.println("Original board:");
		printSudoku();
	}
	
	/**
	 * shuffle an int array
	 * @param nums - int array
	 */
	private void shuffle(int[] nums) {
		Random rm = new Random();
		for(int i=1; i<N; i++) {
			int pos = rm.nextInt(i+1);
			swap(nums, pos, i);
		}
	}
	
	/**
	 * swap two elements in an int array
	 * @param nums - int array
	 * @param i - index of first element
	 * @param j - index of second element
	 */
	private void swap(int[] nums, int i, int j) {
		int temp = nums[i];
		nums[i] = nums[j];
		nums[j] = temp;
	}
	
	/**
	 * solve Sudoku
	 */
	public void solveSudoku() {
		Stack<Integer> emptyLocationList = getEmptySpots(0, 0);
        if(!solve(emptyLocationList)) {
        	System.out.println("No solution!");
        }
        else {
        	System.out.println("Solution: ");
        	printSudoku();
        }
    } 
	
	/**
	 * solve the Sudoku until all empty spots are filled
	 * @param emptyLocationList - list of empty spots
	 * @return true if all empty spots are filled
	 */
	private boolean solve(Stack<Integer> emptyLocationList) { 
		if(emptyLocationList.size()==0) return true;
		int firstValue = emptyLocationList.peek();
		int row = firstValue/N, col = firstValue%N;
        for(int k=1; k<=N; k++) {
        	if(isSafe(board, row, col, (char)(k+'0'))) {
        		board[row][col] = (char)(k+'0');
        		emptyLocationList.pop();
        		if(solve(emptyLocationList)) return true;
        		board[row][col] = ' ';
        		emptyLocationList.push(firstValue);
        	}
        }
        return false;
	}
	
	/**
	 * check if placing 'ch' at location (i, j) is safe or not
	 * @param board - partially filled Sudoku board
	 * @param i - row index
	 * @param j - column index
	 * @param ch - number to be placed at location (i, j)
	 * @return true if placing 'ch' at location (i, j) is allowed, otherwise return false
	 */
    private boolean isSafe(char[][] board, int i, int j, char ch) {
		for(int k=0; k<N; k++) {
    		if(board[k][j]==ch) return false;
    		if(board[i][k]==ch) return false;
    	}
    	int starti = Nsqrt * (i/Nsqrt), startj = Nsqrt * (j/Nsqrt);
    	for(int k=starti; k<starti+Nsqrt; k++) {
    		for(int l=startj; l<startj+Nsqrt; l++) {
    			if(board[k][l]==ch) return false;
    		}
    	}
        return true;
    }
	
    /**
     * add all empty spots of the board into a stack
     * @param starti - start row index
     * @param startj - start column index
     * @return stack of all empty spots
     */
	private Stack<Integer> getEmptySpots(int starti, int startj) {
		Stack<Integer> emptyLocationList = new Stack<>();
		for(int i=starti; i<N+starti; i++) {
	    	for(int j=startj; j<N+startj; j++) {
	    		int row = i%N, col = j%N;
	    		if(board[row][col]==0) {
	    			emptyLocationList.push(N*row+col);
	    		}
	    	}
		}
		return emptyLocationList;
	}
    
	/**
	 * print Sudoku board
	 */
    private void printSudoku() {
		for(int i=0; i<N; i++) {
			if(i%3==0) {
				if(i==0 || i==N-1) 
					System.out.println("╔═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╗");
				else
					System.out.println("╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣");
			}
			else {
					System.out.println("╠───┼───┼───╬───┼───┼───╬───┼───┼───╣");
			}
			
			for(int j=0; j<N; j++) {
				if(j%3==0) System.out.print("║ ");
				else System.out.print("│ ");
				System.out.print(board[i][j] + " ");
			}
			System.out.println("║");
		}
		System.out.println("╚═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╝");
		System.out.println();
    }
	
	public static void main(String[] args) {
		/*
		// get the solution of a given board:
		String[] board = {
				"0,0,9,7,4,8,0,0,0",
				"7,0,0,0,0,0,0,0,0",
				"0,2,0,1,0,9,0,0,0",
				"0,0,7,0,0,0,2,4,0",
				"0,6,4,0,1,0,5,9,0",
				"0,9,8,0,0,0,3,0,0",
				"0,0,0,8,0,3,0,2,0",
				"0,0,0,0,0,0,0,0,6",
				"0,0,0,2,7,5,9,0,0",
		};
		Sudoku q = new Sudoku(board);
		q.solveSudoku();
		*/
		
		// generate a random board and get the solution:
		Scanner sc = new Scanner(System.in);
		System.out.print("1 - easy\n2 - medium\n3 - hard\nSelect level: ");
		int level = sc.nextInt();
		Sudoku q = new Sudoku(level);

		System.out.print("Type anything and enter to get the solution: ");
		if(sc.hasNext())
			q.solveSudoku();
		sc.close();
		
	}    
}
