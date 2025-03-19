package com.litemq.tests;

import com.litemq.core.LiteMQ;
import java.io.IOException;
import java.util.Scanner;

public class LiteMQTestApp {
    public static void main(String[] args) {
        try {
            LiteMQ liteMQ = LiteMQ.getInstance();
            System.out.println("LiteMQ Server Started...");
            
            // Keep the server alive with a blocking loop
            Scanner scanner = new Scanner(System.in);
            System.out.println("Press ENTER to shut down LiteMQ...");
            scanner.nextLine();  // Blocks until user presses Enter

            System.out.println("[LiteMQ] Graceful shutdown initiated....");
            liteMQ.shutdown();
            System.out.println("[LiteMQ] Shutdown completed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
