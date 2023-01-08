import java.io.*;
import java.lang.*;
import java.sql.Time;
import java.util.*;

class Game{
    static int[] dx = {0, 0, 1, -1, 1, -1, 1, -1};
    static int[] dy = {1, -1, 0, 0, 1, -1, -1, 1};
    static Cell[][] parentOf = new Cell[10][10];

    static char[][] grid = new char[9][9];
    static Cell[] figures;
    static Cell Goal;
    static double time;
    static boolean win;

    static String whoIs(Cell cell){
        if(grid[cell.r][cell.c]== 'X') return "cannot be placed inside danger zone";
        else if(grid[cell.r][cell.c] == 'D') return "cannot be placed inside the position of the Davy Jones";
        else if(grid[cell.r][cell.c] == 'J') return "cannot be placed inside the position of the Jack Sparrow";
        else if(grid[cell.r][cell.c] == 'T') return "cannot be placed inside the Tortuga";
        else if(grid[cell.r][cell.c] == 'C') return "cannot be placed inside the Dead Man's Chest isles";
        else return "cannot be placed inside the position of the Rock";
    }

    static boolean Error(String s){
        // System.out.println(s);
        return false;
    }

    static boolean Create(Cell[] fig){
        figures = fig;
        Goal = fig[4];
        for (int i = 0; i < 9; i ++){
            for (int j = 0; j < 9; j ++){
                grid[i][j] = '-';
            }
        }
        Cell davy = fig[1], kraken = fig[2], rock = fig[3], chest = fig[4], tortuga = fig[5];

        grid[0][0] = 'J';                                                                                       // placing the Jack

        if(grid[davy.r][davy.c] == '-') grid[davy.r][davy.c] = 'D';                                         // placing the Davy Jhonson and his danger zones           
        else return Error("Davy Jones cannot be placed inside " + whoIs(davy));     
        for (int i = 0; i < 8; i ++){
            int newRow = fig[1].r + dx[i];
            int newCol = fig[1].c + dy[i];
            if(isValid(newRow, newCol)){
                grid[newRow][newCol] = 'X';
            }
        }
        if(grid[0][0] != 'J') return Error("Davy Jones " + whoIs(davy));

        if(grid[kraken.r][kraken.c] == '-' || grid[kraken.r][kraken.c] == 'X') grid[kraken.r][kraken.c] = 'K';  // placing the Kraken and its danger zones
        else return Error("Kraken " + whoIs(kraken));
        for (int i = 0; i < 4; i ++){
            int newRow = fig[2].r + dx[i];
            int newCol = fig[2].c + dy[i];
            if(isValid(newRow, newCol) && grid[newRow][newCol] != 'D'){
                grid[newRow][newCol] = 'X';
                if(newRow == 0 && newCol == 0) return Error("Jack Sparrow " + whoIs(new Cell(newRow, newCol)));
            }
        }

        if(grid[rock.r][rock.c] == '-' || grid[rock.r][rock.c] == 'X' || grid[rock.r][rock.c] == 'K') grid[rock.r][rock.c] = 'R'; // placing the Rock
        else return Error("The Rock " + whoIs(rock));

        if(grid[chest.r][chest.c] == '-') grid[chest.r][chest.c] = 'C';                                           // placing the Chest
        else return Error("the Dead Man's Chest isles " + whoIs(chest));

        if(grid[tortuga.r][tortuga.c] == '-' || grid[tortuga.r][tortuga.c]  == 'J') grid[tortuga.r][tortuga.c]  = 'T'; // placing the Tortuga
        else return Error("the Tortuga " + whoIs(chest));
        return true;
    }

    static void initParents(){
        Cell[][] tmp = new Cell[10][10];
        parentOf = tmp;
    }

    static void initMaps(char[][] map){
        for (int i = 0; i < 9; i ++){
            for (int j = 0; j < 9; j ++){
                map[i][j] = '-';
            }
        }
    }

    static boolean isValid(int row, int col){
        return (row >= 0) && (row < 9) && (col >= 0)
            && (col < 9);
    }
    static boolean isSafe(int row, int col){
        if (grid[row][col] != 'X' && grid[row][col] != 'K' && grid[row][col] != 'R') return true;
        return false;
    }

    static void reconstructPath(char[][] chart, ArrayList<Cell> ls, Cell from){
        int r = from.r;
        int c = from.c;
        while(parentOf[r][c] != null){
            chart[r][c] = '*';
            ls.add(new Cell(r, c));
            int newRow = parentOf[r][c].r;
            int newCol = parentOf[r][c].c;
            r = newRow; c = newCol;
        }
        ls.add(new Cell(r, c));
        Collections.reverse(ls);
        chart[r][c] = '*';
    }

    static void printResults(char[][] chart, ArrayList<Cell> ls, int res, double timeDuration, String fileName){
        try {
            FileWriter myWriter = new FileWriter(fileName);

                myWriter.write("Win\n" + res +  "\n");
                for (int i = 0; i < ls.size(); i ++){
                    myWriter.write("[" + ls.get(i).r + "," + ls.get(i).c + "] ");
                }
                myWriter.write('\n');
        
                for (int i = 0; i < 20; i ++)myWriter.write("-");
                myWriter.write('\n');
        
                myWriter.write("  ");
                for(int i = 0; i < 9; i ++) myWriter.write(i + " ");
                myWriter.write('\n');

                for (int i = 0; i < 9; i ++){
                    myWriter.write(i + " ");
                    for (int j = 0; j < 9; j ++){
                        myWriter.write(chart[i][j] + " ");
                    }
                    myWriter.write('\n');

                }
                for (int i = 0; i < 20; i ++)myWriter.write("-");
                myWriter.write("\n" + 
                    String.format("%.2f ms", timeDuration) + "\n");
                
                time = timeDuration;
            myWriter.close();
            // System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static void Lose(String fileName){
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write("Lose\n");
            myWriter.close();
            // System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static void killKraken(){
        grid[figures[2].r][figures[2].c] = '-';
        for (int i = 0; i < 4; i ++){
            int newRow = figures[2].r + dx[i];
            int newCol = figures[2].c + dy[i];
            if(isValid(newRow, newCol)){
                grid[newRow][newCol] = '-';
            }
        }
        otherEnemies();
    }
    static void resurrectKraken(){
        grid[figures[2].r][figures[2].c] = 'K';
        for (int i = 0; i < 4; i ++){
            int newRow = figures[2].r + dx[i];
            int newCol = figures[2].c + dy[i];
            if(isValid(newRow, newCol)){
                grid[newRow][newCol] = 'X';
            }
        }
        otherEnemies();
    }
    
    static void otherEnemies(){
        grid[figures[3].r][figures[3].c] = 'R';
        grid[figures[1].r][figures[1].c] = 'D';
        for (int i = 0; i < 8; i ++){
            int newRow = figures[1].r + dx[i];
            int newCol = figures[1].c + dy[i];
            if(isValid(newRow, newCol)){
                grid[newRow][newCol] = 'X';
            }
        }
    }
    
}

class BackTracking extends Game{
    static char[][] map = new char[9][9];
    static char[][] map2 = new char[9][9];

    static ArrayList<Cell> list ;
    static ArrayList<Cell> list2 ;

    static int directBacktracking(){
        initParents();
        int dst = backTrack(new Cell(0, 0), 'C', map, list);
        return dst;
    }

    static int indirectBacktracking(){
        initParents();
        ArrayList<Cell> l = new ArrayList<Cell>();
        int dst;
        dst = backTrack(new Cell(0, 0), 'T', map2, list2);
        if(dst != Integer.MAX_VALUE){
            killKraken();
            dst += backTrack(new Cell(figures[5].r, figures[5].c), 'C', map2, l);
            if(dst < 0) dst = Integer.MAX_VALUE;  
            if(!l.isEmpty())l.remove(0);
            list2.addAll(l);
        }
        resurrectKraken();
        return dst;
    }

    static void search(int var, boolean isValidMap){
        list = new ArrayList<Cell>();
        list2 = new ArrayList<Cell>();
        win = true;
        if(!isValidMap){
            win = false;
            Lose("outputBacktracking.txt");
            return;
        }
        initParents();
        initMaps(map);
        initMaps(map2);
        int mn1, mn2;

        long initialTime = System.nanoTime();
        mn1 = directBacktracking();
        double takenTimeDr = ((double)System.nanoTime() - (double)initialTime)/1e6;

        initialTime = System.nanoTime();
        mn2 = indirectBacktracking();
        double takenTimeIndr = ((double)System.nanoTime() - (double)initialTime)/1e6;
        
        if(mn1 == mn2 && mn1 == Integer.MAX_VALUE){
            win = false;
            Lose("outputBacktracking.txt");
        }
        else{
            if(mn1 <= mn2){
                printResults(map, list, mn1, takenTimeDr, "outputBacktracking.txt");
            }else{
                printResults(map2, list2, mn2, takenTimeIndr, "outputBacktracking.txt");
            }
        }

    }

    static int backTrack(Cell start, char goal, char[][] chart, ArrayList<Cell> ls){
        Queue<Cell> queue = new LinkedList<Cell>();
        boolean[][] used = new boolean[9][9];
        int dst = 0;

        for (int i = 0; i < 9; i ++){
            for (int j = 0; j < 9; j ++){
                used[i][j] = false;
            }
        }

        queue.add(start);
        used[start.r][start.c] = true;
        while(!queue.isEmpty()){
            int sz = queue.size();
            while(sz != 0){
                sz --;
                Cell cur = queue.remove();
                if(grid[cur.r][cur.c] == goal){
                    reconstructPath(chart, ls, cur);
                    initParents();
                    return dst;
                }
                for (int i = 0; i < 8; i ++){
                    int newRow = cur.r + dx[i];
                    int newCol = cur.c + dy[i];
                    if(isValid(newRow, newCol) && isSafe(newRow, newCol) && !used[newRow][newCol]){
                        used[newRow][newCol] = true;
                        parentOf[newRow][newCol] = cur;
                        queue.add(new Cell(newRow, newCol));
                    }
                }
            } 
            dst ++;
        }
        return Integer.MAX_VALUE;
    }
}

class AStar extends Game{
    static char[][] map = new char[9][9];
    static char[][] map2 = new char[9][9];

    static ArrayList<Cell> list;
    static ArrayList<Cell> list2;

    static int directAStar(){   
        initParents();
        int dst = aStar(new Cell(0, 0), figures[4], map, list);
        return dst;
    }

    static int indirectAStar(){
        initParents();
        ArrayList<Cell> l = new ArrayList<Cell>();
        int dst;
        dst = aStar(new Cell(0, 0), figures[5], map2, list2);
        if(dst != Integer.MAX_VALUE){
            killKraken();       
            dst += aStar(new Cell(figures[5].r, figures[5].c), figures[4], map2, l);
            if(dst < 0) dst = Integer.MAX_VALUE;  
            if(!l.isEmpty())l.remove(0);
            list2.addAll(l);
        }
        
        resurrectKraken();      
        return dst;
    }

    static void search(int var, boolean isValidMap){
        list = new ArrayList<Cell>();
        list2 = new ArrayList<Cell>();
        win = true;
        if(!isValidMap){
            win = false;
            Lose("outputAStar.txt");
            return;
        }
        initParents();
        initMaps(map);
        initMaps(map2);
        int mn1, mn2;

        long initialTime = System.nanoTime();
        mn1 = directAStar();
        double takenTimeDr = ((double)System.nanoTime() - (double)initialTime)/1e6;

        initialTime = System.nanoTime();
        mn2 = indirectAStar();
        double takenTimeIndr = ((double)System.nanoTime() - (double)initialTime)/1e6;
        
        if(mn1 == mn2 && mn1 == Integer.MAX_VALUE){
            win = false;
            Lose("outputAStar.txt");
        }
        else{
            if(mn1 <= mn2){
                printResults(map, list, mn1, takenTimeDr, "outputAStar.txt");
            }else{
                printResults(map2, list2, mn2, takenTimeIndr, "outputAStar.txt");
            }
        }
        
    }

    static int aStar(Cell start, Cell goal, char[][] chart, ArrayList<Cell> ls){
        Goal = goal;
        PriorityQueue<Cell> pQueue = new PriorityQueue<Cell>(new QSort());
        boolean[][] used = new boolean[9][9];

        for (int i = 0; i < 9; i ++){
            for (int j = 0; j < 9; j ++){
                used[i][j] = false;
            }
        }

        pQueue.add(start);
        used[start.r][start.c] = true;
        while (!pQueue.isEmpty()){
            Cell cur = pQueue.remove();
            int row = cur.r, col = cur.c;
            
            if(row == goal.r && col == goal.c){
                reconstructPath(chart, ls, cur);
                initParents();
                return ls.size() - 1;
            }
            for (int i = 0; i < 8; i ++){
                int newRow = row + dx[i];
                int newCol = col + dy[i];
                if(isValid(newRow, newCol) && isSafe(newRow, newCol) && !used[newRow][newCol]){
                    used[newRow][newCol] = true;
                    parentOf[newRow][newCol] = cur;
                    pQueue.add(new Cell(newRow, newCol));
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}

class Cell{
    int r;
    int c;

    int f, g, h;

    public Cell(int r, int c){
        this.r = r;
        this.c = c;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = r * 9 + (prime * result + c);
        return result;
    }
 
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cell other = (Cell) obj;
        if (r != other.r || c != other.c)
            return false;
        return true;
    } 
}

class QSort implements Comparator<Cell>{
    public int compare(Cell a, Cell b){
        int ah = Math.abs(Game.Goal.r - a.r) + Math.abs(Game.Goal.c - a.c);
        int bh = Math.abs(Game.Goal.r - b.r) + Math.abs(Game.Goal.c - b.c);
        
        if(ah > bh) return 1;
        else if(ah < bh) return -1;
        return 0;
    }
}

class Tester extends Game{
    static boolean generateMap(){
        Random rand = new Random();
        Cell[] actors = new Cell[6];
        actors[0] = new Cell(0, 0);

        do{
            for (int i = 1; i < 6; i ++){
                actors[i] = new Cell(rand.nextInt(9), rand.nextInt(9));
            }
        }while(!Create(actors));
        return true;
    } 

    static void StatisticalAnalysis(){
        int numOfWinsB = 0, numOfWinsA = 0;

        double sumOfBacktrack = 0, sumOfAStar = 0;

        double[] backtrackTime = new double[1000];
        double[] aStarTime = new double[1000];

        int[] cntOfBack = new int[100000];
        int[] cntOfAStar = new int[100000];

        for (int i = 0; i < 1000; i ++){
            generateMap();

            BackTracking.search(1, true);
            backtrackTime[i] = Game.time;
            sumOfBacktrack += Game.time;
            cntOfBack[(int)(Game.time * 100)] ++;
            if(Game.win) numOfWinsB ++;

            AStar.search(1, true);
            aStarTime[i] = Game.time;
            sumOfAStar += Game.time;
            cntOfAStar[(int)(Game.time * 100)] ++;

            if(Game.win) numOfWinsA ++;
        }

        double meanOfBacktrack = sumOfBacktrack/1000;
        double meanOfAStar = sumOfAStar/1000;

        Arrays.sort(aStarTime);
        Arrays.sort(backtrackTime);

        double medianOfBacktrack = (aStarTime[499] + aStarTime[500])/2;
        double medianOfAStar =  (backtrackTime[499] + backtrackTime[500])/2;

        double modeOfBacktrack = 0;
        double modeOfAStar = 0;
        for (int i = 0; i < 1000; i ++){
            modeOfAStar = Math.max(modeOfAStar, cntOfAStar[i]);
            modeOfBacktrack = Math.max(modeOfBacktrack, cntOfBack[i]);
        }
        modeOfBacktrack /= 100; modeOfAStar /= 100;

        sumOfBacktrack = 0; sumOfAStar = 0;
        for (int i = 0; i < 1000; i ++){
            sumOfAStar = (aStarTime[i] - meanOfAStar) * (aStarTime[i] - meanOfAStar);
            sumOfBacktrack = (backtrackTime[i] - meanOfBacktrack) * (backtrackTime[i] - meanOfBacktrack);
        }
        double sdForBacktrack = Math.sqrt(sumOfBacktrack/1000);
        double sdForAStar = Math.sqrt(sumOfAStar/1000);

        for (int i = 0; i < 110; i ++) System.out.print("-");
        System.out.println("\nBacktraking:");
        System.out.println("mean\t\tmode\t\tmedian\t\tstandart_deviation\tnumber_of_wins\t\tnumber_of_loses");
        System.out.printf("%.4f\t\t%.4f\t\t%.4f\t\t%.4f\t\t\t%d\t\t\t%d\n", 
            meanOfBacktrack, modeOfBacktrack, medianOfBacktrack, sdForBacktrack, numOfWinsB, (1000-numOfWinsB));
        System.out.printf("percentage of wins: %.1f", (double)numOfWinsB/10);
        System.out.println("%");
        System.out.printf("percentage of loses: %.1f", (1000-(double)numOfWinsB)/10);
        System.out.println("%");

        System.out.println("\nAStar:");
        System.out.println("mean\t\tmode\t\tmedian\t\tstandart_deviation\tnumber_of_wins\t\tnumber_of_loses");
        System.out.printf("%.4f\t\t%.4f\t\t%.4f\t\t%.4f\t\t\t%d\t\t\t%d\n", 
            meanOfAStar, modeOfAStar, medianOfAStar, sdForAStar, numOfWinsA, (1000-numOfWinsA));
        System.out.printf("percentage of wins: %.1f", (double)numOfWinsA/10);
        System.out.println("%");
        System.out.printf("percentage of loses: %.1f", (1000-(double)numOfWinsA)/10);
        System.out.println("%");
    }
}

public class Main{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int cnt = 0, var = 0;
        boolean isValidMap = false;

        Tester.StatisticalAnalysis();

        System.out.print("\nWhat kind of input do you want?\n1 - from 'input.txt'\n2 - from generator\n");
        System.out.println("Choose var!:    (1, 2)");
        int inputType = sc.nextInt();
        if(inputType == 1){
            try {
                Cell figures[] = new Cell[6];
                File inputFile = new File("input.txt");
                try{
                    if (inputFile.createNewFile()) {
                        System.out.println("File created: " + inputFile.getName());
                    } else {
                        System.out.println("File already exists.");
                    }
                }catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                Scanner scanFromTxt = new Scanner(inputFile);
                Scanner line = new Scanner(scanFromTxt.nextLine());
                while (line.hasNext()){
                    String s = line.next();
                    int num = 0, Cell[] = new int[2];
                    for (int i = 0; i < s.length(); i ++){
                        if(s.charAt(i) >= '0' && s.charAt(i) <= '9'){
                            Cell[num] = (s.charAt(i) - '0');
                            num ++;
                        }
                    }
                    figures[cnt] = new Cell(Cell[0], Cell[1]);
                    cnt ++;
                }
                var = scanFromTxt.nextInt();
                scanFromTxt.close();

                isValidMap = Game.Create(figures);
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }else{
            var = sc.nextInt();
            isValidMap = Tester.generateMap();
            sc.close();
        }

        System.out.println();
        BackTracking.search(var, isValidMap);

        System.out.println();
        AStar.search(var, isValidMap);
        sc.close();

    } 
}

// [0,0] [4,7] [3,2] [6,4] [8,7] [0,6]
// [0,0] [5,6] [5,3] [7,1] [8,5] [3,0] 
// [0,0] [1,6] [7,7] [5,1] [8,8] [4,1]