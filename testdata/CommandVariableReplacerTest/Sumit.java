// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
public class Sumit {

    public void doSum(String filename) {
        try {
            RandomAccessFile file = new RandomAccessFile(filename, "r");
            String line;
            int sum = 0;
            int rv = 0;
            while ((line = file.readLine()) != null) {
                rv = new Integer(line.trim()).intValue();
                if (rv > 0) {
                    sum = sum + rv;
                    // System.out.println(line);
                }
            }
            System.out.print("The sum of the integers is " + sum);
        } catch (Exception e) {
            System.out.println("Possible trouble reading Sumit.dat");
        }
    }

    public static void main(String[] args) {

       new Sumit().doSum("+inputfilename+"); 

    }
}

