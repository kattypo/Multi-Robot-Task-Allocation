import java.util.LinkedList;

public class PathGenerator {
    public static class Cell  {
        int x;
        int y;
        int dist;  	//distance
        Cell prev;  //parent cell in the path
        Cell(int x, int y, int dist, Cell prev) {
            this.x = x;
            this.y = y;
            this.dist = dist;
            this.prev = prev;
        }

        @Override
        public String toString(){
            return "(" + x + "," + y + ")";
        }
    }
    //BFS, Time O(n^2), Space O(n^2)
    public static LinkedList<Cell> shortestPath(char[][] matrix, int[] start, int[] end) {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];
        //initialize the cells
        int m = matrix.length;
        int n = matrix[0].length;
        Cell[][] cells = new Cell[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == '-') {
                    cells[i][j] = new Cell(i, j, Integer.MAX_VALUE, null);
                }
                if (((i == sx && j == sy)|| (i == dx && j == dy)) && (matrix[i][j] == 'R') || ((i == sx && j == sy)|| (i == dx && j == dy)) && (matrix[i][j] == 'G')) {
                    cells[i][j] = new Cell(i, j, Integer.MAX_VALUE, null);
                }
            }
        }
        //breadth first search
        LinkedList<Cell> queue = new LinkedList<>();
        Cell src = cells[sx][sy];
        src.dist = 0;
        queue.add(src);
        Cell dest = null;
        Cell p;
        while ((p = queue.poll()) != null) {
            //find destination
            if (p.x == dx && p.y == dy) {
                dest = p;
                break;
            }
            // moving up
            visit(cells, queue, p.x - 1, p.y, p);
            // moving down
            visit(cells, queue, p.x + 1, p.y, p);
            // moving left
            visit(cells, queue, p.x, p.y - 1, p);
            //moving right
            visit(cells, queue, p.x, p.y + 1, p);
        }

        //compose the path if path exists
//        if (dest == null) {
//            System.out.println("there is no path.");
//            return;
//        } else {
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
            } while ((p = p.prev) != null);
//            System.out.println(path);
//            for(int i = 0; i < path.size(); i++){
//                System.out.println(path.get(i));
//            }
            return path;
//        }
    }

    //function to update cell visiting status, Time O(1), Space O(1)
    private static void visit(Cell[][] cells, LinkedList<Cell> queue, int x, int y, Cell parent) {
        //out of boundary
        if (x < 0 || x >= cells.length || y < 0 || y >= cells[0].length || cells[x][y] == null) {
            return;
        }
        //update distance, and previous node
        int dist = parent.dist + 1;
        Cell p = cells[x][y];
        if (dist < p.dist) {
            p.dist = dist;
            p.prev = parent;
            queue.add(p);
        }
    }
}
