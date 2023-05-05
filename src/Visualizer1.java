import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public abstract class Visualizer1 implements ActionListener {
    static ArrayList<Robot> robots = new ArrayList<>();
    static ArrayList<Task> tasks = new ArrayList<>();

    static int cyclesCounter = 1;//starts at one so the program waits for user to press button before updating
    static JFrame grid = new JFrame();
    static JPanel bottom = new JPanel();//displays grid
    static JPanel top = new JPanel();//displays button
    static int idAssigner = 0; //gives each task a unique id

    public static void demo(){

        int cycles = 1; //amount of cycles to demo for
        PathGenerator path = new PathGenerator();
        JButton button = new JButton("Progress 1 step");
        int[] initialPositions = generateInitialEnvironment();
        char[][] initialGrid = generateGrid(initialPositions);
        displayGrid(initialGrid, grid, button);
        for(int j = 0; j < tasks.size(); j++) {
            tasks.get(j).setId(idAssigner);
            idAssigner += 1;
        }

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {//responds to button press
                resetCounter();//sets counter to zero so it updates
                boolean duplicate = false;
                while(cyclesCounter < cycles){ //will iterate for determined amount of cycles
                    for(int i = 0; i < robots.size(); i++){//robots should view list of tasks and pick one
                        if((robots.get(i).hasTask == false) && (tasks.size() > 0)){//verifies robot is not assigned task and there are tasks to assign
                            for(int j = 0; j < tasks.size(); j++){
                                if(tasks.get(j).assigned() < 2){
                                    int robotBid = robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(j).row, tasks.get(j).col);
                                    Bid bid = new Bid(robotBid, i);
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
                                robots.get(tasks.get(i).bidWinner(place)).assignTask(tasks.get(i).id);//assigns task to robot
                                tasks.get(i).addRobot();//assigns robot to task
                            }
                        }
                    }

                    for(int i = 0; i < robots.size(); i++){ //goes through eat robot and calculates next point if not at the task
                        System.out.printf("Robot %d - cycle %d\n", i, cyclesCounter);
                        if((robots.get(i).hasTask)) {
                            if((robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(findTask(robots.get(i).task)).row, tasks.get(findTask(robots.get(i).task)).col) > 1)){
                                System.out.printf("Robot %d - cycle %d - option 1\n", i, cyclesCounter);
                                int[] start = {robots.get(i).col, robots.get(i).row};
                                int[] end = {tasks.get(findTask(robots.get(i).task)).col, tasks.get(findTask(robots.get(i).task)).row};
                                LinkedList<PathGenerator.Cell> shortest = new LinkedList<>(); //will store the shortest path
                                shortest = path.shortestPath(initialGrid, start, end); //returns the shortest path to the goal
                                System.out.println(shortest);
                                duplicate = false;
                                for (int j = 0; j < robots.size(); j++) {
                                    if (((shortest.get(1).x) == (robots.get(j).col)) && ((shortest.get(1).y) == (robots.get(j).row)) && (i != j)) {
                                        duplicate = true;
                                        //do not do anything - robot must wait for robot to move
                                    }
                                }
                                if (!duplicate) {
                                    robots.get(i).setCol(shortest.get(1).x); //sets the new row for the robot
                                    robots.get(i).setRow(shortest.get(1).y); //sets the new col for the robot

                                }
                            }
                            else if((robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(findTask(robots.get(i).task)).row, tasks.get(findTask(robots.get(i).task)).col) == 1)){
                                //start doing task or keep doing task 1 robot
                                System.out.printf("Robot %d - cycle %d - option 2\n", i, cyclesCounter);
                                System.out.println(tasks.get(findTask(robots.get(i).task)).time);

                                if(tasks.get(findTask(robots.get(i).task)).time > 0){
                                    int value = tasks.get(findTask(robots.get(i).task)).time - 1;
                                    tasks.get(findTask(robots.get(i).task)).setTime(value);
                                }
                                else if(tasks.get(findTask(robots.get(i).task)).time <= 0){
                                    for(int j = 0; j < robots.size(); j++){
                                        if((robots.get(j).hasTask)){
                                            if(tasks.get(findTask(robots.get(j).task)) == (tasks.get(findTask(robots.get(i).task))) && (i != j)){
                                                robots.get(j).unassignTask();
                                            }
                                        }
                                    }
                                    tasks.remove(findTask(robots.get(i).task));
                                    robots.get(i).unassignTask();
                                }
                                else{
                                    System.out.println("Error: Unhandled exception.");
                                }
                            }
                        }
                    }
                    cyclesCounter += 1;
                }
                updateGrid();
            }
        });
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

    public static void displayGrid(char[][] initialGrid, JFrame grid, JButton button){
        top.setBackground(Color.gray);
        bottom.setLayout(new GridLayout(10,10));
        bottom.setSize(800,800);
        top.add(button);

        JLabel[][] cells;
        cells = new JLabel[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new JLabel(" " + initialGrid[i][j] + " ", SwingConstants.CENTER);
                cells[i][j].setOpaque(true);
                if ((cells[i][j].getText().compareTo(" R ")) == 0) {
                    cells[i][j].setBackground(Color.darkGray);
                } else if ((cells[i][j].getText().compareTo(" G ")) == 0) {
                    cells[i][j].setBackground(Color.green);
                } else {
                    cells[i][j].setBackground(Color.white);
                }
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                bottom.add(cells[i][j]);
            }
        }

        bottom.setBackground(Color.blue);

        grid.getContentPane() .add(BorderLayout.NORTH, top);
        grid.getContentPane() .add(BorderLayout.SOUTH, bottom);

        grid.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        grid.setLayout(new GridLayout(2,10));
        grid.setSize(900,900);
        grid.setVisible(true);
    }

    public static void updateGrid(){
        JLabel[][] cells;
        cells = new JLabel[10][10];
        char[][] result = new char[10][10]; //creates environment for display
        bottom.removeAll();
        for(int i = 0; i < 10; i++)
        {
            Arrays.fill(result[i], '-');
        }

        for(int i = 0; i < tasks.size(); i++){
            result[tasks.get(i).row][tasks.get(i).col] = 'G';

        }
        for(int j = 0; j < robots.size(); j++){
            result[robots.get(j).row][robots.get(j).col] = 'R';
            System.out.printf("Row-%d Col-%d\n", robots.get(j).row, robots.get(j).col);

        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new JLabel(" " + result[i][j] + " ", SwingConstants.CENTER);
                cells[i][j].setOpaque(true);
                if((cells[i][j].getText().compareTo(" R ")) == 0){
                    cells[i][j].setBackground(Color.darkGray);
                } else if((cells[i][j].getText().compareTo(" G ")) == 0) {
                    cells[i][j].setBackground(Color.green);
                } else{
                    cells[i][j].setBackground(Color.white);
                }
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                bottom.add(cells[i][j]);
            }
        }

        bottom.revalidate();
        bottom.repaint();
    }
    static void resetCounter(){//for use with button, selection will reset the counter
        cyclesCounter = 0;
    }

    static int findTask(int value){
        int goalTask = -1;

        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i).id == value){
                goalTask= i;
            }
        }

        if(goalTask == -1){
            System.out.println("Error: id not found");
        }
        return goalTask;

    }
}
