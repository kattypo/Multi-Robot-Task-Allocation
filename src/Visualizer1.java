import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;

public abstract class Visualizer1 implements ActionListener {
    static ArrayList<Robot> robots = new ArrayList<>();
    static ArrayList<Task> tasks = new ArrayList<>();
    static JFrame grid = new JFrame();
    static JPanel bottom = new JPanel();//displays grid
    static JPanel top = new JPanel();//displays button
    static int idAssigner = 0; //gives each task a unique id

    public static void demo(){

        AStarPathGenerator path = new AStarPathGenerator();
        int[] initialPositions = generateInitialEnvironment();
        char[][] initialGrid = generateGrid(initialPositions);
        displayGrid(initialGrid, grid);

        for(int j = 0; j < tasks.size(); j++) {
            tasks.get(j).setId(idAssigner);
            idAssigner += 1;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run(){
                boolean duplicate = false;
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
                if(tasks.size() == 0){
                    timer.cancel();
                    return;
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
                                    //do not do anything - robot must wait for other robot to move
                                }
                            }
                            if (!duplicate) {
                                robots.get(i).setCol(shortest.get(1).col); //sets the new row for the robot
                                robots.get(i).setRow(shortest.get(1).row); //sets the new col for the robot

                            }
                        }
                        else if((robots.get(i).calculateBid(robots.get(i).row, robots.get(i).col, tasks.get(findTask(robots.get(i).task)).row, tasks.get(findTask(robots.get(i).task)).col) == 1)){
                            //start doing task or keep doing task

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
                updateGrid();       
            }
        }, 0, 1000);
    }

    public static int[] generateInitialEnvironment(){ //generates random positions
        //positions should be between 0-2499 inclusive
        //first 20 numbers are the robots' initial positions and the rest are garbage positions

        final int robots = 20;
        final int garbage = 40;
        final int maxValue = 2499;
        final int totalPositions = robots + garbage;
        Set<Integer> uniquePositions = new HashSet<>();
        Random random = new Random();

        while (uniquePositions.size() < totalPositions) {
            int testValue = random.nextInt(maxValue);
            uniquePositions.add(testValue);
        }
        int iterator = 0;
        int[] positions = new int[totalPositions];
        
        for(Integer position: uniquePositions){
            positions[iterator] = position.intValue();
            iterator += 1;
        }
        return positions;
    }

    public static char[][] generateGrid(int[] positions) //creates grid
    {
        char[][] result = new char[50][50]; //creates environment for display
        for(int i = 0; i < 50; i++) {
            Arrays.fill(result[i], '-');
        }

        for(int i = 0; i < 20; i++) { //fills the robots positions
            int row = positions[i] / 50;
            int column = positions[i] % 50;
            result[row][column] = 'R';
            Robot robot = new Robot();
            robot.setRow(row);
            robot.setCol(column);
            robot.setID(i);
            robots.add(robot);
        }

        for(int i = 20; i < positions.length; i++) { //fills the garbage/rubble positions
            int row = positions[i] / 50;
            int column = positions[i] % 50;
            result[row][column] = 'G';
            Task task = new Task();
            task.setRow(row);
            task.setCol(column);
            tasks.add(task);
        }
        return result;
    }

    public static void displayGrid(char[][] initialGrid, JFrame grid) {
        top.setBackground(Color.gray);
        bottom.setLayout(new GridLayout(50,50));
        bottom.setSize(800,800);
        JLabel[][] cells = new JLabel[50][50];

        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new JLabel(" " + initialGrid[i][j] + " ", SwingConstants.CENTER);
                cells[i][j].setOpaque(true);
                if ((cells[i][j].getText().compareTo(" R ")) == 0) {
                    cells[i][j].setBackground(Color.darkGray);
                } 
                else if ((cells[i][j].getText().compareTo(" G ")) == 0) {
                    cells[i][j].setBackground(Color.green);
                } 
                else {
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

    public static void updateGrid() {
        JLabel[][] cells;
        cells = new JLabel[50][50];
        char[][] result = new char[50][50]; //creates environment for display
        bottom.removeAll();

        for(int i = 0; i < 50; i++) {
            Arrays.fill(result[i], '-');
        }

        for(int i = 0; i < tasks.size(); i++) {
            result[tasks.get(i).row][tasks.get(i).col] = 'G';
        }

        for(int j = 0; j < robots.size(); j++) {
            result[robots.get(j).row][robots.get(j).col] = 'R';
        }

        for (int i = 0; i < 50; i++) {
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

    static int findTask(int value) {
        int goalTask = -1;

        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i).id == value){
                goalTask= i;
            }
        }

        if(goalTask == -1)  {
            System.out.println("Error: id not found");
        }
        return goalTask;
    }
}
