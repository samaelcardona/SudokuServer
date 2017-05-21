/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokuserver;

/**
 *
 * @author SAMAEL
 */
public class SudokuServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SudokuServer_ server = new SudokuServer_(25000, "Sudoku server");
        server.run();
    }
    
}
