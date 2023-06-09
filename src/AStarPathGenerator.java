import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class AStarPathGenerator {
    public class Cell implements Comparable<Cell>{
        int row;
        int col;
        int parent_i;
        int parent_j;
        double g;
        double h;
        double f;

        public Cell(int[] position) {
            col = position[0];
            row = position[1];
        }

        @Override public int compareTo(Cell compareCell) { //do not touch handles sorting cells by their lowest f
            int compareNum = (int) Math.round(compareCell.f);
            return (int) Math.round(this.f) - compareNum;
        }
    }
    boolean isValid(char[][] grid, int row, int col, int[] dest) //checks if our cell (col, row) is valid
    {
        int rows = grid.length;
        int cols = grid[0].length;

        if (row >= 0 && col >= 0 && row < rows && col < cols){
            if(grid[row][col] == '-' || (row == dest[1] && col == dest[0] && grid[row][col] == 'G')){
                return true;
            }
        }
        return false;
    }
    boolean isUnBlocked(char[][] grid, int[] current) //checks if goal is accessible
    {
        int rows = grid.length;
        int cols = grid[0].length;

            if(current[0] > 0){ //checks left
                if(grid[current[0] - 1][current[1]] == '-'){
                    return true;
                }
            }
            if(current[0] < cols - 1){ //checks right
                if(grid[current[0] + 1][current[1]] == '-'){
                    return true;
                }
            }
            if(current[1] > 0){ //checks up
                if(grid[current[0]][current[1] - 1] == '-'){
                    return true;
                }
            }
            if(current[1] < rows - 1){ //checks down
                if(grid[current[0]][current[1] + 1] == '-'){
                    return true;
                }
            }
            if(current[0] > 0 && current[1] > 0){ //checks diagonal up and to the left
                if(grid[current[0] - 1][current[1] - 1] == '-'){
                    return true;
                }
            }
            if(current[0] < cols - 1 && current[1] > 0){ //checks diagonal up and to the right
                if(grid[current[0] + 1][current[1] - 1] == '-'){
                    return true;
                }
            }
            if(current[0] > 0 && current[1] < rows - 1){ //checks diagonal down and to the left
                if(grid[current[0] - 1][current[1] + 1] == '-'){
                    return true;
                }
            }
            if(current[0] < cols - 1 && current[1] < rows - 1){ //checks diagonal down and to the right
                if(grid[current[0] + 1][current[1] + 1] == '-'){
                    return true;
                }
            }
            return false;
    }
    boolean isDestination(int[] position, int[] dest){ //checks if we are at the destination
        if((position[0] == dest[0]) && (position[1] == dest[1])){
            return true;
        }
           return false;
    }

    double calculateHValue(int[] current, int[] dest){ //calculates heuristic function
        return Math.sqrt(Math.pow((current[0] - dest[0]), 2.0) + Math.pow((current[1] - dest[1]), 2.0));
    }
    double calculateGValue(int[] current, int[] start){ //calculates heuristic function
        return Math.sqrt(Math.pow((current[0] - start[0]), 2.0) + Math.pow((current[1] - start[1]), 2.0));
    }
    LinkedList<Cell> sortCells(LinkedList<Cell> openList){ //sorts the cells by f value increasing
        Collections.sort(openList);
        return openList;
    }
    LinkedList<Cell> tracePath(Cell[][] cellDetails, int[] dest){
        int row = dest[1];
        int col = dest[0];
        Stack <Cell> cellStack = new Stack<>();
        LinkedList<Cell> path = new LinkedList<>();

        while (!(cellDetails[row][col].parent_i == row && cellDetails[row][col].parent_j == col)) {
            cellStack.push(cellDetails[row][col]);
            int temp_row = cellDetails[row][col].parent_i;
            int temp_col = cellDetails[row][col].parent_j;
            row = temp_row;
            col = temp_col;
        }

        cellStack.push(cellDetails[row][col]);
        while (!cellStack.empty()) {
            Cell current = cellStack.peek();
            cellStack.pop();
            path.add(current);
        }

        return path;
    }
    LinkedList<Cell> aStarSearch(char[][] grid, int[] start, int[] dest)//A* Search algorithm to find the shortest path
    {
        int i, j;
        int rows = grid.length;
        int cols = grid[0].length;
        LinkedList<Cell> finalPath = new LinkedList<>();
        boolean[][] closedList =  new boolean[rows][cols] ; //cells that have been closed
        Cell[][] cellDetails = new Cell[rows][cols]; //cells and their information
        LinkedList <Cell> openList = new LinkedList<>(); //cells that are open

        for (i = 0; i < rows; i++) { //generates all the cells
            for (j = 0; j < cols; j++) {
                int[] position = new int[2];
                position[0] = j;
                position[1] = i;
                Cell cell = new Cell(position);
                cellDetails[i][j] = cell;
                cellDetails[i][j].f = 0.0; //f = g + h
                cellDetails[i][j].g = 0.0; //how far we are from the starting cell
                cellDetails[i][j].h = 0.0; //how far we are from the goal cell
                cellDetails[i][j].parent_i = -1;
                cellDetails[i][j].parent_j = -1;
            }
        }

        //sets up the first cell in the path
        i = start[1];
        j = start[0];
        cellDetails[i][j].parent_i = i;
        cellDetails[i][j].parent_j = j;
        openList.add(cellDetails[i][j]);
        boolean foundDest = false;

        while (!openList.isEmpty()) {
            openList = sortCells(openList);
            Cell currentCell = openList.peek();
            openList.remove(openList.peek());
            i = currentCell.row;
            j = currentCell.col;
            closedList[i][j] = true;
            double gNew, hNew, fNew;
            int[] successor = new int[2];

            ///// North/////

            successor[1] = i - 1;
            successor[0] = j;

            if (isValid(grid, i - 1, j, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i - 1][j].parent_i = i;
                    cellDetails[i - 1][j].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i - 1][j] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j].f == 0 || cellDetails[i - 1][j].f > fNew) {
                        openList.add(cellDetails[i-1][j]);
                        cellDetails[i - 1][j].f = fNew;
                        cellDetails[i - 1][j].g = gNew;
                        cellDetails[i - 1][j].h = hNew;
                        cellDetails[i - 1][j].parent_i = i;
                        cellDetails[i - 1][j].parent_j = j;
                    }
                }
            }

            ///// South /////

            successor[1] = i + 1;
            successor[0] = j;

            if (isValid(grid, i + 1, j, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i + 1][j].parent_i = i;
                    cellDetails[i + 1][j].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i + 1][j] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j].f == 0 || cellDetails[i + 1][j].f > fNew) {
                        openList.add(cellDetails[i + 1][j]);
                        cellDetails[i + 1][j].f = fNew;
                        cellDetails[i + 1][j].g = gNew;
                        cellDetails[i + 1][j].h = hNew;
                        cellDetails[i + 1][j].parent_i = i;
                        cellDetails[i + 1][j].parent_j = j;
                    }
                }
            }

            ///// East /////

            successor[1] = i;
            successor[0] = j + 1;

            if (isValid(grid, i, j + 1, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i][j + 1].parent_i = i;
                    cellDetails[i][j + 1].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i][j + 1] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i][j + 1].f == 0 || cellDetails[i][j + 1].f > fNew) {
                        openList.add(cellDetails[i][j + 1]);
                        cellDetails[i][j + 1].f = fNew;
                        cellDetails[i][j + 1].g = gNew;
                        cellDetails[i][j + 1].h = hNew;
                        cellDetails[i][j + 1].parent_i = i;
                        cellDetails[i][j + 1].parent_j = j;
                    }
                }
            }

            ///// West /////

            successor[1] = i;
            successor[0] = j - 1;

            if (isValid(grid, i, j - 1, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i][j - 1].parent_i = i;
                    cellDetails[i][j - 1].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i][j - 1] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i][j - 1].f == 0 || cellDetails[i][j - 1].f > fNew) {
                        openList.add(cellDetails[i][j - 1]);
                        cellDetails[i][j - 1].f = fNew;
                        cellDetails[i][j - 1].g = gNew;
                        cellDetails[i][j - 1].h = hNew;
                        cellDetails[i][j - 1].parent_i = i;
                        cellDetails[i][j - 1].parent_j = j;
                    }
                }
            }

            ///// North-East /////

            successor[1] = i - 1;
            successor[0] = j + 1;

            if (isValid(grid, i - 1, j + 1, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i - 1][j + 1].parent_i = i;
                    cellDetails[i - 1][j + 1].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i - 1][j + 1] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j + 1].f == 0 || cellDetails[i - 1][j + 1].f > fNew) {
                        openList.add(cellDetails[i - 1][j + 1]);
                        cellDetails[i - 1][j + 1].f = fNew;
                        cellDetails[i - 1][j + 1].g = gNew;
                        cellDetails[i - 1][j + 1].h = hNew;
                        cellDetails[i - 1][j + 1].parent_i = i;
                        cellDetails[i - 1][j + 1].parent_j = j;
                    }
                }
            }

            ///// North-West /////

            successor[1] = i - 1;
            successor[0] = j - 1;

            if (isValid(grid, i - 1, j - 1, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i - 1][j - 1].parent_i = i;
                    cellDetails[i - 1][j - 1].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i - 1][j - 1] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j - 1].f == 0 || cellDetails[i - 1][j - 1].f > fNew) {
                        openList.add(cellDetails[i - 1][j - 1]);
                        cellDetails[i - 1][j - 1].f = fNew;
                        cellDetails[i - 1][j - 1].g = gNew;
                        cellDetails[i - 1][j - 1].h = hNew;
                        cellDetails[i - 1][j - 1].parent_i = i;
                        cellDetails[i - 1][j - 1].parent_j = j;
                    }
                }
            }

            ///// South-East /////

            successor[1] = i + 1;
            successor[0] = j + 1;

            if (isValid(grid, i + 1, j + 1, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i + 1][j + 1].parent_i = i;
                    cellDetails[i + 1][j + 1].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i + 1][j + 1] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j + 1].f == 0 || cellDetails[i + 1][j + 1].f > fNew) {
                        openList.add(cellDetails[i + 1][j + 1]);
                        cellDetails[i + 1][j + 1].f = fNew;
                        cellDetails[i + 1][j + 1].g = gNew;
                        cellDetails[i + 1][j + 1].h = hNew;
                        cellDetails[i + 1][j + 1].parent_i = i;
                        cellDetails[i + 1][j + 1].parent_j = j;
                    }
                }
            }

            ///// South-West /////
            successor[1] = i + 1;
            successor[0] = j - 1;

            if (isValid(grid, i + 1, j - 1, dest)) {
                if (isDestination(successor, dest)) {
                    cellDetails[i + 1][j - 1].parent_i = i;
                    cellDetails[i + 1][j - 1].parent_j = j;
                    finalPath = tracePath(cellDetails, dest);
                    foundDest = true;
                    return finalPath;
                }

                // If the successor is already on the closed list then ignore it, otherwise do the following

                else if (!closedList[i + 1][j - 1] && isUnBlocked(grid, successor)) {
                    gNew = calculateGValue(successor, start);
                    hNew = calculateHValue(successor, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j - 1].f == 0 || cellDetails[i + 1][j - 1].f > fNew) {
                        openList.add(cellDetails[i + 1][j - 1]);
                        cellDetails[i + 1][j - 1].f = fNew;
                        cellDetails[i + 1][j - 1].g = gNew;
                        cellDetails[i + 1][j - 1].h = hNew;
                        cellDetails[i + 1][j - 1].parent_i = i;
                        cellDetails[i + 1][j - 1].parent_j = j;
                    }
                }
            }
        }

        if (!foundDest)
            System.out.println("Failed to find the Destination Cell");

        return finalPath;
    }
}
