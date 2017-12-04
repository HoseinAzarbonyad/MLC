/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 *
 **     ------- هو اللطیف -------     ** 
 * @author Mostafa Dehghani
 *
 **/
public class Runner extends Thread{
    final InputStream is;
    final String type;
    final StringBuilder sb;

    Runner(final InputStream is, String type)
    {
        this.is = is;
        this.type = type;
        this.sb = new StringBuilder();
    }

    public void run()
    {
        try
        {
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
            {
                this.sb.append(line).append("\n");
            }
        }
        catch (final IOException ioe)
        {
            System.err.println(ioe.getMessage());
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public String toString()
    {
        return this.sb.toString();
    }

    public static void runCommand(String Command) throws IOException{
        try
        {
            final Process p = Runtime.getRuntime().exec(Command); 
            final Runner stderr = new Runner(p.getErrorStream(), "STDERR");
            final Runner stdout = new Runner(p.getInputStream(), "STDOUT");
            stderr.start();
            stdout.start();
            final int exitValue = p.waitFor();
           
            if (exitValue == 0)
            {
                System.out.println("--------------------------------------");
                System.out.print(stdout.toString());
                System.out.println("--------------------------------------");
            }
            else
            {
                System.err.println("\n\n\n\n"+Command+"\n\n\n");
                System.err.print(stderr.toString());
            }
        }
        catch (final InterruptedException e)
        {
            throw new RuntimeException(e);
        }
     }
}
