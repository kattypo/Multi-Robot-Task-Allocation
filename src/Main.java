import java.util.*;
public abstract class Main  {
    //design specs:
    // robots - full commitment (finish task once started)
    // coordinated strategy (two robots on one task max)
    // option 1 has no obstacles
    // option 2 has obstacles

    public static void main(String[] args) {

        System.out.print("Choose a option\n Option 1: demo with no obstacles\n Option 2: demo with obstacles\n");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        if (option == 1) {
            Visualizer1.demo();
        }
        else if (option == 2) {
            Visualizer2.demo();
        }
        else {
            System.out.println("Invalid input. Try again.");
        }
    }
}
