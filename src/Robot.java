import java.lang.Math;

public class Robot {
    int bid = -1; //stores current bid for task
    boolean hasTask = false; //if robot is currently assigned task
    int row;
    int col;
    int id; //to keep track of which robot is which
    int task;

    int calculateBid(int row, int col, int goalRow, int goalCol){ //takes in task location & returns bid for task
        bid = (int) Math.round(Math.sqrt(Math.pow((goalCol - col), 2) + Math.pow((goalRow - row), 2)));
        return bid;
    }

    void assignTask(int taskId){ //robot has task

        hasTask = true;
        task = taskId;
    }
    void unassignTask(){ //robot has no task
        if(hasTask){
            hasTask = false;
        }
    }
    void setRow(int y){

        row = y;
    }
    void setCol(int x){

        col = x;
    }
    void setID(int ID){

        id = ID;
    }
}
