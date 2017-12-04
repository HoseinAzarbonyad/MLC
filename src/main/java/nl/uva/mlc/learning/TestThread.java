/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.learning;

/**
 *
 * @author mosi
 */
class L2RThread extends Thread {
   private Thread t;
   private String threadName;
   private String path;
   
   L2RThread(String name, String path){
       this.threadName = name;
       this.path = path;
       System.out.println("Creating " +  threadName );
   }
   @Override
   public void run() {
      System.out.println("Running " +  threadName );
      try {
         for(int i = 4000; i > 0; i--) {
            System.out.println("Thread: " + threadName + ", " + i);
//             Let the thread sleep for a while.
            Thread.sleep(0);
         }
     } catch (InterruptedException e) {
         System.out.println("Thread " +  threadName + " interrupted.");
     }
     System.out.println("Thread " +  threadName + " exiting.");
   }
   
   public void start ()
   {
      System.out.println("Starting " +  threadName );
      if (t == null)
      {
         t = new Thread (this, threadName);
         t.start ();
      }
   }

}

public class TestThread {
   public static void main(String args[]) {
   
      L2RThread R1 = new L2RThread("..", "Fold1");
      R1.start();
      
      L2RThread R2 = new L2RThread("..", "Fold2");
      R2.start();
   }   
}
