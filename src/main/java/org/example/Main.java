package org.example;

public class Main {
    // 'public' aur 'String[] args' add kiya gaya hai
    public static void main(String[] args) {

        // IO.println ki jagah System.out.println use kiya gaya hai
        System.out.println(String.format("Hello and welcome!"));

        for (int i = 1; i <= 5; i++) {
            // Yahan bhi System.out.println use karna hai
            System.out.println("i = " + i);
        }
    }
}