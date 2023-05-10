public class Bid implements Comparable<Bid> {
    int number;
    int id;

    Bid(int bidNumber, int bidId){
        number = bidNumber;
        id = bidId;
    }

    @Override public int compareTo(Bid compareBid) { //do not touch handles sorting bid values
        int compareNum = compareBid.number;
        return this.number - compareNum;
    }
}
