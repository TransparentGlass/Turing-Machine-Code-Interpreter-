package com.tmci;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TuringMachineTest {

    @Test
    void defaultMachineStartsWithBlankHead() {
        Turing_Machine machine = new Turing_Machine();

        assertEquals('_', machine.head.read());
    }

    @Test
    void stringConstructorBuildsTapeInOrder() {
        Turing_Machine machine = new Turing_Machine("AB");

        assertEquals('A', machine.head.read());
        assertEquals('B', machine.head.right.read());
    }

    @Test 
    void printInputTuringMachine(){
        Turing_Machine machine = new Turing_Machine("ab12345___");
        machine.print();
    }
}
