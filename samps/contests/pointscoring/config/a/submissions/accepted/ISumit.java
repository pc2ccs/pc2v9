
// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
import java.io.*;

//
// File:    ISumit.java
// Purpose: to print the sum of the positive integers from stdin

public class ISumit {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 1);

            String line;
            int sum = 0;
            int rv = 0;
            while ((line = br.readLine()) != null) {
                rv = new Integer(line.trim()).intValue();
                if (rv > 0)
                    sum = sum + rv;
            }
            System.out.print("The sum of the integers is ");
            System.out.println(sum);
        } catch (Exception e) {
            System.out.println("Possible trouble reading stdin");
            System.out.println("Message: " + e.getMessage());
        }
    }
}