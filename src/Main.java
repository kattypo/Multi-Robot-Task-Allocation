import java.util.*;
import javax.swing.*;
import java.awt.*;

//design specs:
//robots - full commitment (finish task once started)
//       - coordinated strategy (two robots on one task max)
//a demo mode and input mode (custom values)

public class Main {
    static ArrayList<Robot> robots = new ArrayList<>();
    static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {

        //taking user input is placeholder
        //would like to ask for cycles through JPanel input
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the amount of cycles to display: ");
        int cycles = scan.nextInt(); //amount of cycles to demo for
        int cyclesCounter = 0;
        boolean check = true;

        int[] initialPositions = generateInitialEnvironment();
        char[][] initialGrid = generateGrid(initialPositions);
        displayGrid(initialGrid);

        while(cyclesCounter < cycles){ //will iterate for determined amount of cycles
            for(int i = 0; i < robots.size(); i++){//robots should view list of tasks and pick one
                if((robots.get(i).hasTask == false) && (tasks.size() > 0)){//verifies robot is not assigned task and there are tasks to assign
                    for(int j = 0; j < tasks.size(); j++){
                        if(tasks.get(j).assigned() < 2){
                            int robotBid = robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(j).row, tasks.get(j).col);
                            Bid bid = new Bid(robotBid, i);
                            System.out.printf("Current Robot location y- %d x- %d and current task location y- %d x- %d\n", robots.get(i).row, robots.get(i).col, tasks.get(j).row, tasks.get(j).col);
                            System.out.printf("Robot %d is placing a bid for task %d: %d\n", i, j, robotBid); //test
                            tasks.get(j).addBid(bid);
                        }
                    }
                }
            }

            for(int i = 0; i < tasks.size(); i++){//sorts the bids in each tasks
                tasks.get(i).sortBids();
            }

            Collections.sort(tasks);//sorts the tasks by best preferred match for assignment

            for(int place = 0; place < robots.size(); place++){ //will go through each task's preferred robot 1st,2nd, etc.
                for(int i = 0; i < tasks.size(); i++){ //assigns robots their tasks
                    if(robots.get(tasks.get(i).bidWinner(place)).hasTask == false && tasks.get(i).assigned() < 2){ //checks that bidwinner doesn't already have task
                        robots.get(tasks.get(i).bidWinner(place)).assignTask(i);//assigns task to robot
                        tasks.get(i).addRobot();//assigns robot to task
                        System.out.printf("Task %d's has been assigned to: %d\n", i, tasks.get(i).bidWinner(place));
                        System.out.printf("Task location: y-%d x-%d Robot location: y-%d x-%d\n", tasks.get(i).row, tasks.get(i).col, robots.get(tasks.get(i).bidWinner(0)).row, robots.get(tasks.get(i).bidWinner(0)).col);
                    }
                }
            }
            cyclesCounter += 1;
            //prioritize each task having a robot
            //if there are more robots than tasks, allow teamwork
            //for each robot go through each available task (priortize 0 robot tasks first)
            //for each robot go through each available task (not maxed out tasks next)
            //generate bid for that task
            //function will go through bids and best bid(robot) is selected
        }

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
            robot.setID(i);
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
