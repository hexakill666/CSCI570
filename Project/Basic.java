import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Basic{

    public static int gapPenalty = 30;
    public static Map<String, Integer> mismatchPenaltyMap = new HashMap<>();

    static{
        mismatchPenaltyMap.put("AC", 110);
        mismatchPenaltyMap.put("AG", 48);
        mismatchPenaltyMap.put("AT", 94);
        mismatchPenaltyMap.put("CG", 118);
        mismatchPenaltyMap.put("CT", 48);
        mismatchPenaltyMap.put("GT", 110);
    }

    public static int getMismatchPenalty(char str1, char str2){
        if(str2 < str1){
            return mismatchPenaltyMap.get("" + str2 + str1);
        }
        return mismatchPenaltyMap.get("" + str1 + str2);
    }

    public static String createSequence(Scanner sc, String baseStr){
        StringBuilder sb = new StringBuilder(baseStr);
        while(sc.hasNextInt()){
            sb.insert(sc.nextInt() + 1, sb.toString());
        }
        return sb.toString();
    }

    public static Node[][] createBoard(String seq1, String seq2){
        int length = seq1.length() + 1;
        int width = seq2.length() + 1;
        Node[][] board = new Node[length][width];
        board[0][0] = new Node(null, 0, '#', '#');
        for(int a = 1; a < width; a++){
            board[0][a] = new Node(board[0][a - 1], board[0][a - 1].penalty + gapPenalty, '_', seq2.charAt(a - 1));
        }
        for(int a = 1; a < length; a++){
            board[a][0] = new Node(board[a - 1][0], board[a - 1][0].penalty + gapPenalty, seq1.charAt(a - 1), '_');
        }
        for(int row = 1; row < length; row++){
            for(int col = 1; col < width; col++){
                if(seq1.charAt(row - 1) == seq2.charAt(col - 1)){
                    board[row][col] = new Node(board[row - 1][col - 1], board[row - 1][col - 1].penalty, seq1.charAt(row - 1), seq2.charAt(col - 1));
                }
                else{
                    int minPenalty = Integer.MAX_VALUE;
                    if(board[row - 1][col - 1].penalty + getMismatchPenalty(seq1.charAt(row - 1), seq2.charAt(col - 1)) < minPenalty){
                        minPenalty = board[row - 1][col - 1].penalty + getMismatchPenalty(seq1.charAt(row - 1), seq2.charAt(col - 1));
                        board[row][col] = new Node(board[row - 1][col - 1], minPenalty, seq1.charAt(row - 1), seq2.charAt(col - 1));
                    }
                    if(board[row][col - 1].penalty + gapPenalty < minPenalty){
                        minPenalty = board[row][col - 1].penalty + gapPenalty;
                        board[row][col] = new Node(board[row][col - 1], minPenalty, '_', seq2.charAt(col - 1));
                    }
                    if(board[row - 1][col].penalty + gapPenalty < minPenalty){
                        minPenalty = board[row - 1][col].penalty + gapPenalty;
                        board[row][col] = new Node(board[row - 1][col], minPenalty, seq1.charAt(row - 1), '_');
                    }
                }
            }
        }
        return board;
    }

    public static String[] createAlignSequence(Node[][] board){
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        int length = board.length;
        int width = board[length - 1].length;
        Node tempNode = board[length - 1][width - 1];
        while(tempNode.seq1 != '#' && tempNode.seq2 != '#'){
            sb1.append(tempNode.seq1);
            sb2.append(tempNode.seq2);
            tempNode = tempNode.pre;
        }
        return new String[]{sb1.reverse().toString(), sb2.reverse().toString()};
    }

    public static double getMemoryInKB() {
        double total = Runtime.getRuntime().totalMemory();
        return (total - Runtime.getRuntime().freeMemory()) / 10e3;
    }

    public static double getTimeInMilliseconds() {
        return System.nanoTime() / 10e6;
    }

    public static void main(String[] args) {
        String inputFileName = args[0];
        String outputFileName = args[1];
        Scanner sc = null;
        FileWriter out = null;

        try{
            sc = new Scanner(new File(inputFileName));
            out = new FileWriter(new File(outputFileName));
        }
        catch(Exception e){

        }

        double memoryBefore = getMemoryInKB();
        double startTime = getTimeInMilliseconds();

        String seq1 = createSequence(sc, sc.next());
        String seq2 = createSequence(sc, sc.next());
        Node[][] board = createBoard(seq1, seq2);
        String[] alignSeq = createAlignSequence(board);

        double memoryAfter = getMemoryInKB();
        double endTime = getTimeInMilliseconds();

        try {
            out.append(board[seq1.length()][seq2.length()].penalty + "\n")
                .append(alignSeq[0] + "\n")
                .append(alignSeq[1] + "\n")
                .append(endTime - startTime + "\n")
                .append(memoryAfter - memoryBefore + "");
            out.flush();
        }
        catch (Exception e) {

        }
    }
}

class Node{
    Node pre;
    int penalty;
    char seq1;
    char seq2;

    public Node(Node pre, int penalty, char seq1, char seq2){
        this.pre = pre;
        this.penalty = penalty;
        this.seq1 = seq1;
        this.seq2 = seq2;
    }
}