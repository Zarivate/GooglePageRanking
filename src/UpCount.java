public class UpCount {
    private long calcNonRecursive(int depth) {
        int result = 1;
        for (int i = 1; i <= depth; i++) {
            result = result + (i % 7) + ((((result ^ i) % 4) == 0) ? 1 : 0);
        }
        return result;
    }

    public static void main(String[] args) {
        UpCount uc = new UpCount();
        System.out.println(uc.calcNonRecursive(11589));
    }
}