import java.io.*;
import java.util.*;

public class PCMonitor {
    static int N = 50;
    static Producer p = new Producer();
    static Consumer c = new Consumer();
    static MyMonitor m = new MyMonitor();
    
    static class Producer extends Thread{
        private volatile boolean flag = true;
        
        public void stopProducing() {
            flag = false;
        }
        public void run() {
            System.out.println("Producer started producing");
            int item;
            while(flag) {
                item = produce_item();
                m.put(item);
            }
        }
        
        private int produce_item() {
            Random rand = new Random();
            int num = rand.nextInt(N) +1;
            System.out.println("Producer produced item: " + num);
            return num;
        }
    }
    
    static class Consumer extends Thread{
        private volatile boolean flag2 = true;
        
        public void stopConsuming() {
            flag2 = false;
        }
        
        public void run() {
            System.out.println("Consumer started consuming");
            int item;
            while(flag2) {
                item = m.take();
                consume_item(item);
            }
        }
        
        private void consume_item(int item) {
            System.out.println("Consumer took item: " + item );
        }
    }
    
    //Class monitor to make sure producer and consumer don't access shared buffer at the same time
    static class MyMonitor extends Thread{
        private int buffer[] = new int[N];
        private int count = 0;
        private int beg, end = 0;
        
        //Producer sleeps when buffer is full otherwise puts in buffer 
        public synchronized void put(int a) {
            if(count == N) { goToSleep(); }
            buffer[end] = a;
            end = (end+1)%N;
            count++;
            if(count == 1) { notify(); }
        }
        //Consumer sleeps when buffer is empty otherwise takes from buffer
        
        public synchronized int take() {
            int a, i;
            if(count == 0) { goToSleep(); }
            a = buffer[beg];
            i = beg;
            beg = (beg+1)%N;
            count--;
            if(count == N-1) { notify(); }
            return a;
        }
        
        //Function to make them sleep. 
        private void goToSleep() {
            try {
                wait();
            }
            catch(InterruptedException exc) {
                System.out.println("Interrupt occured");
            }
        }
    }
    
    public static void main(String args[]) {
            p.start();
            c.start();
            //So that loop doesn't run forever
            try {
                p.sleep(2);
                c.sleep(2);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
            
            p.stopProducing();
            c.stopConsuming();
    }

}
