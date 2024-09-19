import java.util.*;
public abstract class Main  {
    //Design specs:
    // Robots - full commitment (finish task once started)
    // Coordinated strategy (two robots on one task max)
    // Option 1 has no obstacles
    // Option 2 has obstacles

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose a option\n Option 1: Demo with no obstacles\n Option 2: Demo with obstacles\n");
        
        try {
            int option = scanner.nextInt();

            switch(option) {
                case 1:
                    Visualizer1.demo();
                    break;
                case 2:
                    Visualizer2.demo();
                    break;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        } 
        catch(Exception e) {
            System.out.println("Invalid input. Try again.");
        } 
        finally {
            scanner.close();
        }
    }
}
