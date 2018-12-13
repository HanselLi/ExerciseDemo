package com.ebensz.appmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by liyang3323 on 2018/12/4.
 */

public class FileInputDemo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(" next input : ");
        if (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            System.out.println(" next line is : " + nextLine);
            new FileInputDemo().readFile(nextLine);
        }
        scanner.close();
    }


    public int readFile(String filePath) {
        File file = new File(filePath);
        boolean canExecute = file.canExecute();
        if (!canExecute) {
            System.out.println(" File can not execute return");
            return -1;
        }
        InputStream fis = null;
        if (file.exists() && file.length() != 0) {
            try {
                fis = new FileInputStream(file);
                int read = fis.read();
                int size = fis.available();
                for (int i = 0; i <= size; i++) {
                    System.out.println((char) fis.read() + "");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return -1;
    }

}
