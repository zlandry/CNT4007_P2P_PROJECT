public class MultithreadTest extends Thread{
    public void run() {
        try {
            System.out.println("Thread " + Thread.currentThread().getId() + " is running!");
        }
        catch (Exception e) {
            System.out.println("Exception is caught: " + e.getMessage());
        }
    }
}

class Multithread {
    public static void main(String[] args) {
        int n = 8;
        for (int i = 0; i < n; i++) {
            MultithreadTest object = new MultithreadTest();
            object.start();
        }
    }
}
