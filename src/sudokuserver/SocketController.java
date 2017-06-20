/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokuserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SAMAEL
 */
public class SocketController implements Runnable {

    private SudokuServer_ theserver = null;
    private String theName = "";
    private Thread theThread = null;
    private Socket theSocket = null;
    private PrintWriter theOut = null;
    private BufferedReader theIn = null;

    private String theUserName = "";
    String[] matrizTecnica;
    String[] matriz;
    String tecnica;

    public SocketController(Socket aSocket, String aUserName, SudokuServer_ aServer) {
        this.theserver = aServer;
        this.theUserName = aUserName;
        this.theSocket = aSocket;
        this.matriz=new String[81];

        /*inicio el socket*/
        try {

            this.theOut = new PrintWriter(theSocket.getOutputStream(), true);
            this.theIn = new BufferedReader(new InputStreamReader(theSocket.getInputStream(), "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(SudokuServer_.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*inicio el hilo*/
        this.theThread = new Thread(this);
        this.theThread.start();
    }

    /*cerrar el socket*/
    public void close() {
        try {
            this.theOut.close();
            this.theIn.close();
            this.theSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(SudokuServer_.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*salidas del servidor hacia el cliente*/
    public void writeMessage(String message) {
        this.theOut.println(message);
    }

    @Override
    public void run() {
        String readLine = "";

        writeMessage("Bienvenido");

        System.out.println("started");

        while (!readLine.trim().equalsIgnoreCase("QUIT")) {
            try {
                readLine = this.theIn.readLine();
            } catch (IOException ex) {
                Logger.getLogger(SudokuServer_.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (readLine != null) {

                readLine = readLine.trim();

                if (readLine.startsWith("<board>")||readLine.startsWith("<BOARD>")) {

                    matrizTecnica = readLine.substring(7).split(";");
                   
                    for (int i = 0; i < matrizTecnica[0].length(); i++) {
                        matriz[i] =""+ matrizTecnica[0].charAt(i);
                    }
                    tecnica = matrizTecnica[1];

                    this.writeMessage(theserver.recibirMatrizSudokuUsuario(matriz, tecnica));
                }

            }

        }
        theThread.stop();
        close();
        System.out.println(getTheUserName() + "endend");
    }

    public String getTheUserName() {
        return this.theUserName;
    }

    public void setTheUserName(String theUserName) {
        this.theUserName = theUserName;
    }
}
