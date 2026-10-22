package com.tmci;

public class Turing_Machine {
    cell head;

    public Turing_Machine() {
        this.head = new cell();
    }

    Turing_Machine(String input) {
        if (input == null || input.isEmpty()) {
            this.head = new cell();
            return;
        }

        this.head = new cell(input.charAt(0));
        cell current = this.head;

        for (int i = 1; i < input.length(); i++) {
            cell next = new cell(input.charAt(i));
            current.right = next;
            next.left = current;
            current = next;
        }
    }

    public void print(){
        cell current = this.head;
        while (current != null){
            System.out.print(current.read());
            current = current.right;
        } 
    }
}

class cell {
    public cell left;
    public cell right;
    private char data = '_';

    cell(char c) {
        data = c;
        left = null;
        right = null;
    }

    cell() {
        left = null;
        right = null;
    }

    public void write(char c) {
        this.data = c;
    }

    public char read() {
        return this.data;
    }
}