import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Efficient{

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

    public static String createSeq(Scanner sc, String baseStr){
        StringBuilder sb = new StringBuilder(baseStr);
        while(sc.hasNextInt()){
            sb.insert(sc.nextInt() + 1, sb.toString());
        }
        return sb.toString();
    }

    public static int hirschberg(String seq1, String seq2, StringBuilder align1, StringBuilder align2){
        if(seq1.length() <= 2 || seq2.length() <= 2){
            Node[][] board = createBoard(seq1, seq2);
            String[] alignSeq = createAlignSeq(board);
            align1.append(alignSeq[0]);
            align2.append(alignSeq[1]);
            return board[board.length - 1][board[board.length - 1].length - 1].penalty;
        }
        else{
            int seq1Mid = seq1.length() / 2;
            String seq1Left = seq1.substring(0, seq1Mid);
            String seq1Right = seq1.substring(seq1Mid);
            int[] penalty1 = getAlignPenaltyArray(seq1Left, seq2);
            int[] penalty2 = getAlignPenaltyArray(new StringBuilder(seq1Right).reverse().toString(), new StringBuilder(seq2).reverse().toString());
            int[] sumPenalty = new int[penalty1.length];
            int seq2MinIndex = -1, seq2MinPenalty = Integer.MAX_VALUE, left = 0, right = sumPenalty.length - 1;
            for(int a = 0; a < sumPenalty.length; a++){
                sumPenalty[a] = penalty1[left++] + penalty2[right--];
                if(sumPenalty[a] < seq2MinPenalty){
                    seq2MinIndex = a;
                    seq2MinPenalty = sumPenalty[a];
                }
            }
            String seq2Left = seq2.substring(0, seq2MinIndex);
            String seq2Right = seq2.substring(seq2MinIndex);
            return hirschberg(seq1Left, seq2Left, align1, align2) + hirschberg(seq1Right, seq2Right, align1, align2);
        }
    }

    public static int[] getAlignPenaltyArray(String seq1, String seq2){
        int length = seq1.length() + 1;
        int width = seq2.length() + 1;
        int[][] board = new int[2][width];
        board[0][0] = 0;
        board[1][0] = gapPenalty;
        for(int col = 1; col < width; col++){
            board[0][col] = board[0][col - 1] + gapPenalty;
        }
        for(int row = 1; row < length; row++){
            for(int col = 1; col < width; col++){
                if(seq1.charAt(row - 1) == seq2.charAt(col - 1)){
                    board[1][col] = board[0][col - 1];
                }
                else{
                    int minPenalty = Integer.MAX_VALUE;
                    if(board[0][col - 1] + getMismatchPenalty(seq1.charAt(row - 1), seq2.charAt(col - 1)) < minPenalty){
                        minPenalty = board[0][col - 1] + getMismatchPenalty(seq1.charAt(row - 1), seq2.charAt(col - 1));
                        board[1][col] = minPenalty;
                    }
                    if(board[1][col - 1] + gapPenalty < minPenalty){
                        minPenalty = board[1][col - 1] + gapPenalty;
                        board[1][col] = minPenalty;
                    }
                    if(board[0][col] + gapPenalty < minPenalty){
                        minPenalty = board[0][col] + gapPenalty;
                        board[1][col] = minPenalty;
                    }
                }
            }
            System.arraycopy(board[1], 0, board[0], 0, width);
            board[1][0] = board[0][0] + gapPenalty;
        }
        return board[1];
    }

    public static Node[][] createBoard(String seq1, String seq2){
        int length = seq1.length() + 1;
        int width = seq2.length() + 1;
        Node[][] board = new Node[length][width];
        board[0][0] = new Node(null, 0, '#', '#');
        for(int col = 1; col < width; col++){
            board[0][col] = new Node(board[0][col - 1], board[0][col - 1].penalty + gapPenalty, '_', seq2.charAt(col - 1));
        }
        for(int row = 1; row < length; row++){
            board[row][0] = new Node(board[row - 1][0], board[row - 1][0].penalty + gapPenalty, seq1.charAt(row - 1), '_');
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

    public static String[] createAlignSeq(Node[][] board){
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

        String seq1 = createSeq(sc, sc.next());
        String seq2 = createSeq(sc, sc.next());
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        int penalty = hirschberg(seq1, seq2, align1, align2);

        double memoryAfter = getMemoryInKB();
        double endTime = getTimeInMilliseconds();

        try {
            out.append(penalty + "\n")
                .append(align1.toString() + "\n")
                .append(align2.toString() + "\n")
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