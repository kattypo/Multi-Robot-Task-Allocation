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

        int cycles = 10; //amount of cycles to demo for
        int cyclesCounter = 0;
        boolean check = true;
        JFrame grid = new JFrame();
        PathGenerator path = new PathGenerator();

        int[] initialPositions = generateInitialEnvironment();
        char[][] initialGrid = generateGrid(initialPositions);
        displayGrid(initialGrid, grid);

        while(cyclesCounter < cycles){ //will iterate for determined amount of cycles
            for(int i = 0; i < robots.size(); i++){//robots should view list of tasks and pick one
                if((robots.get(i).hasTask == false) && (tasks.size() > 0)){//verifies robot is not assigned task and there are tasks to assign
                    for(int j = 0; j < tasks.size(); j++){
                        if(tasks.get(j).assigned() < 2 && (tasks.get(j).needsRemoval == false)){
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
            //code above here works properly
            //now that robots are assigned their tasks
            //handle the robots setting the path to their tasks and then handling the task once they get there
            //JPanel should update to show movement
            //10 moves can be made in a cycle
            //a full cycle to clean up for 1 robot
            //half the amount of time left if another robot shows up
            //Every 10 cycles more trash will added

            for(int i = 0; i < tasks.size(); i++){ //will go through each task to make sure it does not need to be removed (added)
                if(tasks.get(i).needsRemoval){
                    tasks.remove(i);
                }
            }

            for(int i = 0; i < robots.size(); i++){ //goes thru eat robot and calculates next point if not at the task
                if(robots.get(i).hasTask && robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(robots.get(i).task).row, tasks.get(robots.get(i).task).col) > 1){
                    int[] start = {robots.get(i).row, robots.get(i).col};
                    int[] end = {tasks.get(robots.get(i).task).row, tasks.get(robots.get(i).task).col};
                    LinkedList<PathGenerator.Cell> shortest = new LinkedList<>(); //will store the shortest path
                    shortest = path.shortestPath(initialGrid, start, end ); //returns the shortest path to the goal
                    robots.get(i).setRow(shortest.get(1).x); //sets the new row for the robot
                    robots.get(i).setCol(shortest.get(1).y); //sets the new col for the robot

                }
                if(robots.get(i).hasTask && robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(robots.get(i).task).row, tasks.get(robots.get(i).task).col) == 1 &&  tasks.get(robots.get(i).task).assigned() == 1){
                    //start doing task or keep doing task 1 robot
                }
                if(robots.get(i).hasTask && robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(robots.get(i).task).row, tasks.get(robots.get(i).task).col) == 1 &&  tasks.get(robots.get(i).task).assigned() == 2){
                    //start doing task or keep doing task 2 robots
                }
            }
           cyclesCounter += 1;
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

    public static void displayGrid(char[][] initialGrid, JFrame grid){
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

//    public static void updateGrid(JFrame grid, ArrayList<Robot> robots, ArrayList<Task> tasks){
//        JLabel[][] cells;
//        cells = new JLabel[10][10];
//        char[][] result = new char[10][10]; //creates environment for display
//        for(int i = 0; i < 10; i++)
//        {
//            Arrays.fill(result[i], '-');
//        }
//
//        for(int i = 0; i < robots.size(); i++){
//            result[robots.get(i).col][robots.get(i).row] = 'R';
//        }
//        for(int i = 0; i < tasks.size(); i++){
//            result[tasks.get(i).col][tasks.get(i).row] = 'G';
//        }
//
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < cells.length; j++) {
//                cells[i][j] = new JLabel(" " + result[i][j] + " ", SwingConstants.CENTER);
//                cells[i][j].setOpaque(true);
//                if((cells[i][j].getText().compareTo(" R ")) == 0){
//                    cells[i][j].setBackground(Color.pink);
//                } else if((cells[i][j].getText().compareTo(" G ")) == 0) {
//                    cells[i][j].setBackground(Color.green);
//                } else{
//                    cells[i][j].setBackground(Color.white);
//                }
//                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 1));
//                //grid.add(cells[i][j]);
//            }
//        }
//        grid.revalidate();
//        grid.repaint();
//    }
}
