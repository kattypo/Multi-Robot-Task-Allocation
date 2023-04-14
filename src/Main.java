import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

//design specs:
//robots - full commitment (finish task once started)
//       - coordinated strategy (two robots on one task max)
//a demo mode and input mode (custom values)

public class Main {
    static ArrayList<Robot> robots = new ArrayList<>();
    static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {

        int[] initialPositions = generateInitialEnvironment();
        char[][] initialGrid = generateGrid(initialPositions);
        displayGrid(initialGrid);

    }
    public static int[] generateInitialEnvironment(){//generates random positions
        //don't do number repeats and positions should be between 0-99
        //return positions of those members for calculations later
        //the first 5 numbers are the robots and the last four are the garbage
        int robots = 5;
        int garbage = 4;
        int[] positions = new int[robots + garbage];
        int testValue;
        Random random = new Random();
        for(int i = 0; i < positions.length; i++) { //generates random positions
            boolean duplicate = false;
            do {
                testValue = random.nextInt(((100) - 1) - 0) + 0;
                duplicate = false;
                for (int j = 0; j < i; j++) {
                    if (positions[j] == testValue) {
                        duplicate = true;
                        break;
                    }
                }
            }
            while (duplicate);
            positions[i] = testValue;
        }
        return positions;
    }
    public static char[][] generateGrid(int[] positions) //creates grid
    {
        //first 5 positions are robots, the rest is garbage piles
        char[][] result = new char[10][10]; //creates environment for display
        for(int i = 0; i < 10; i++)
        {
            Arrays.fill(result[i], '-');
        }
        for(int i = 0; i < 5; i++) //fills the robots positions
        {
            int row = positions[i] / 10;
            int column = positions[i] % 10;

            result[row][column] = 'R';
            Robot robot = new Robot();
            robot.setRow(row);
            robot.setCol(column);
            robots.add(robot);
        }
        for(int i = 5; i < positions.length; i++) //fills the garbage/rubble positions
        {
            int row = positions[i] / 10;
            int column = positions[i] % 10;

            result[row][column] = 'G';
            Task task = new Task();
            task.setRow(row);
            task.setCol(column);
            tasks.add(task);
        }
        return result;
    }
    public static int calculateBids(){ //stores robot bids and chooses
        int result = 0;

        return result;
    }

    public static void displayGrid(char[][] initialGrid){
        JFrame grid = new JFrame();
        grid.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        grid.setLayout(new GridLayout(10,10));
        grid.setSize(700,700);
        grid.setVisible(true);

        JLabel[][] cells;
        cells = new JLabel[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new JLabel(" " + initialGrid[i][j] + " ", SwingConstants.CENTER);
                cells[i][j].setOpaque(true);
                if((cells[i][j].getText().compareTo(" R ")) == 0){
                    cells[i][j].setBackground(Color.darkGray);
                } else if((cells[i][j].getText().compareTo(" G ")) == 0) {
                    cells[i][j].setBackground(Color.green);
                } else{
                    cells[i][j].setBackground(Color.white);
                }
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                grid.add(cells[i][j]);
            }
        }
    }
}