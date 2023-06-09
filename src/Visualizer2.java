import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public abstract class Visualizer2 implements ActionListener {
    static ArrayList<Robot> robots = new ArrayList<>();
    static ArrayList<Task> tasks = new ArrayList<>();

    static ArrayList<Obstacle> obstacles = new ArrayList<>();

    static int cyclesCounter = 1;//starts at one so the program waits for user to press button before updating
    static JFrame grid = new JFrame();
    static JPanel bottom = new JPanel();//displays grid
    static JPanel top = new JPanel();//displays button
    static int idAssigner = 0; //gives each task a unique id
    static char[][] initialGrid;

    public static void demo(){

        int cycles = 1; //amount of cycles to demo for
        AStarPathGenerator path = new AStarPathGenerator();//added
        JButton button = new JButton("Progress 1 step");
        int[] initialPositions = generateInitialEnvironment();
        initialGrid = generateGrid(initialPositions);
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
                        if((!robots.get(i).hasTask) && (tasks.size() > 0)){//verifies robot is not assigned task and there are tasks to assign
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
                            if(!robots.get(tasks.get(i).bidWinner(place)).hasTask && tasks.get(i).assigned() < 2){ //checks that bidwinner doesn't already have task
                                robots.get(tasks.get(i).bidWinner(place)).assignTask(tasks.get(i).id);//assigns task to robot
                                tasks.get(i).addRobot();//assigns robot to task
                            }
                        }
                    }

                    for(int i = 0; i < robots.size(); i++){ //goes through each robot and calculates next point if not at the task
                        if((robots.get(i).hasTask)) {
                            if((robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(findTask(robots.get(i).task)).row, tasks.get(findTask(robots.get(i).task)).col) > 1)){
                                int[] start = {robots.get(i).col, robots.get(i).row};
                                int[] end = {tasks.get(findTask(robots.get(i).task)).col, tasks.get(findTask(robots.get(i).task)).row};
                                LinkedList<AStarPathGenerator.Cell> shortest = new LinkedList<>(); //will store the shortest path
                                shortest = path.aStarSearch(initialGrid, start, end); //returns the shortest path to the goal
                                duplicate = false;
                                for (int j = 0; j < robots.size(); j++) {
                                    if (((shortest.get(1).col) == (robots.get(j).col)) && ((shortest.get(1).row) == (robots.get(j).row)) && (i != j)) {
                                        duplicate = true;
                                        //do not do anything - robot must wait for robot to move
                                        //may be a robot just passing or a robot may already be at the task blocking the robot's way
                                    }
                                }
                                if (!duplicate) {
                                    robots.get(i).setCol(shortest.get(1).col); //sets the new row for the robot
                                    robots.get(i).setRow(shortest.get(1).row); //sets the new col for the robot

                                }
                            }
                            else if((robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(findTask(robots.get(i).task)).row, tasks.get(findTask(robots.get(i).task)).col) == 1)){
                                //start doing task or keep doing task 1 robot

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
                initialGrid = updateGrid();
            }
        });
    }
    public static int[] generateInitialEnvironment(){//generates random positions
        //don't do number repeats and positions should be between 0-2500
        //return positions of those members for calculations later
        //the first 20 numbers are the robots, the next 40 are garbage, and the rest are obstacles
        int robots = 20;
        int garbage = 40;
        int obstacles = 20;
        int[] positions = new int[robots + garbage + obstacles];
        int testValue;
        Random random = new Random();
        for(int i = 0; i < positions.length; i++) { //generates random positions
            boolean duplicate = false;
            do {
                testValue = random.nextInt(((2500) - 1));
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
        //first 20 positions are robots, 40 are garbage, and the rest are obstacles
        char[][] result = new char[50][50]; //creates environment for display
        for(int i = 0; i < 50; i++)
        {
            Arrays.fill(result[i], '-');
        }
        for(int i = 0; i < 20; i++) //fills the robots positions
        {
            int row = positions[i] / 50;
            int column = positions[i] % 50;

            result[row][column] = 'R';
            Robot robot = new Robot();
            robot.setRow(row);
            robot.setCol(column);
            robot.setID(i);
            robots.add(robot);
        }
        for(int i = 20; i < 60; i++) //fills the garbage positions
        {
            int row = positions[i] / 50;
            int column = positions[i] % 50;

            result[row][column] = 'G';
            Task task = new Task();
            task.setRow(row);
            task.setCol(column);
            tasks.add(task);
        }
        for(int i = 60; i < positions.length; i++) //fills the obstacle positions
        {
            int row = positions[i] / 50;
            int column = positions[i] % 50;

            result[row][column] = '.';
            Obstacle obstacle = new Obstacle();
            obstacle.setRow(row);
            obstacle.setCol(column);
            obstacles.add(obstacle);
        }
        return result;
    }

    public static void displayGrid(char[][] initialGrid, JFrame grid, JButton button){
        top.setBackground(Color.gray);
        bottom.setLayout(new GridLayout(50,50));
        bottom.setSize(800,800);
        top.add(button);

        JLabel[][] cells;
        cells = new JLabel[50][50];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new JLabel(" " + initialGrid[i][j] + " ", SwingConstants.CENTER);
                cells[i][j].setOpaque(true);
                if ((cells[i][j].getText().compareTo(" R ")) == 0) {
                    cells[i][j].setBackground(Color.darkGray);
                } else if ((cells[i][j].getText().compareTo(" G ")) == 0) {
                    cells[i][j].setBackground(Color.green);
                } else if ((cells[i][j].getText().compareTo(" . ")) == 0) {
                    cells[i][j].setBackground(Color.pink);
                }else {
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
        grid.setLayout(new GridLayout(2,50));
        grid.setSize(900,900);
        grid.setVisible(true);
    }

    public static char[][] updateGrid(){
        JLabel[][] cells;
        cells = new JLabel[50][50];
        char[][] result = new char[50][50]; //creates environment for display
        bottom.removeAll();
        for(int i = 0; i < 50; i++)
        {
            Arrays.fill(result[i], '-');
        }

        for(int i = 0; i < tasks.size(); i++){
            result[tasks.get(i).row][tasks.get(i).col] = 'G';

        }
        for(int j = 0; j < robots.size(); j++){
            result[robots.get(j).row][robots.get(j).col] = 'R';

        }
        for(int i = 0; i < obstacles.size(); i++){
            result[obstacles.get(i).row][obstacles.get(i).col] = '.';

        }
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new JLabel(" " + result[i][j] + " ", SwingConstants.CENTER);
                cells[i][j].setOpaque(true);
                if((cells[i][j].getText().compareTo(" R ")) == 0){
                    cells[i][j].setBackground(Color.darkGray);
                } else if((cells[i][j].getText().compareTo(" G ")) == 0) {
                    cells[i][j].setBackground(Color.green);
                } else if((cells[i][j].getText().compareTo(" . ")) == 0) {
                    cells[i][j].setBackground(Color.pink);
                }else{
                    cells[i][j].setBackground(Color.white);
                }
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 1));
                bottom.add(cells[i][j]);
            }
        }

        bottom.revalidate();
        bottom.repaint();
        return result;
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
