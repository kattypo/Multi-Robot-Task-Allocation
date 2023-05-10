import java.util.ArrayList;
import java.util.Collections;

public class Task implements Comparable<Task>{
    int robots = 0; //current robots assigned to the task
    int row;
    int col;

    ArrayList<Bid> bids = new ArrayList<>(); //will be used to store bids
    int bestMatch;
    int bestMatchValue;

    int time = 2;

    int id;


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

    void addBid(Bid bid){ //adds bid to list
        bids.add(bid);
    }

    void sortBids()
    {
        Collections.sort(bids); //sorts the bids by value increasing
        setBestMatch(bids.get(0).id); //assigns best match for sorting later
        setBestMatchValue(bids.get(0).number); //assigns best match value for sorting later
    }
    int bidWinner(int place){ //takes in the place value wanted
        int winner;
        if(bids.size() == 0){
            System.out.println("Error: there are no bids stored.");
            winner = -1;
        }
        else{
            winner = bids.get(place).id;
        }
        return winner;
    }
    void setRow(int y){

        row = y;
    }
    void setCol(int x){

        col = x;
    }
    void setTime(int value){
        time = value;

    }
    void setBestMatch(int value){
        bestMatch = value;
    }
    void setBestMatchValue(int value){
        bestMatchValue = value;
    }

    @Override public int compareTo(Task compareWinner) { //do not touch handles sorting tasks by their best bid
        int compareNum = compareWinner.bestMatchValue;
        return this.bestMatchValue - compareNum;
    }

    void setId(int value)
    {
        id = value;
    }

}
