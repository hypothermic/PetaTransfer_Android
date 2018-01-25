package nl.hypothermic.android.petatransfer;

public class AnonymousException {

    public static void main(String args) {
        newRunnable(42).run();
    }

    private static Runnable newRunnable(final int x) {
        if(x < 0) throw new IllegalArgumentException();
        return new Runnable() {
            @Override
            public void run() {
                System.out.println("x=" + x);
            }
        };
    }
}