import java.util.ArrayList;

public class Task {
    int robots = 0; //current robots assigned to the task
    int row;
    int col;

    ArrayList<Integer> bids = new ArrayList<Integer>(); //will be used to choose best bid

    int assigned(){ //returns # of assigned robots
        return robots;
    }

    void addRobot(){ //updates assigned robots
        if(robots < 2){
            robots += 1;
        }
        else{
            System.out.println("Error: Task already assigned max robots.");
        }
    }
    void setRow(int y){
        row = y;
    }
    void setCol(int x){
        col = x;
    }
}
