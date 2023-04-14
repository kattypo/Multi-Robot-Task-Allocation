import java.lang.Math;

public class Robot {
    int bid = 0; //stores current bid for task
    boolean task = false; //if robot is currently assigned task
    int row;
    int col;

    int calculateBid(int row, int col, int goalRow, int goalCol){ //takes in task location & returns bid for task
        bid = (int) Math.round(Math.sqrt( (goalCol - col)^2 - (goalRow - row)^2));
        return bid;
    }

    void assignTask(){ //robot has task

        task = true;
    }
    void unassignTask(){ //robot has no task
        if(task){
            task = false;
        }
    }
    void setRow(int y){
        row = y;
    }
    void setCol(int x){
        col = x;
    }
}
