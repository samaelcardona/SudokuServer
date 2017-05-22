/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokuserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SAMAEL
 */
public class SudokuServer_ implements Runnable {

    private String theName = "";
    private int thePort = 0;
    private LinkedList<SocketController> clients;
    private String username = "";
    LinkedList[][] matrizCandidatos = new LinkedList[9][9];
    int[][] matrizUsuario = new int[9][9];
    String tecnica;

    public SudokuServer_(int aPort, String aName) {
        this.thePort = aPort;
        this.theName = aName;
        this.clients = new LinkedList();
    }

    public String recibirMatrizSudokuUsuario(String[] matrizUs, String tecnica) {

        LinkedList<LinkedList<Integer>> listaDeListas = new LinkedList();
        int contadorParaCadaFila = 0;

        LinkedList<Integer> listaTemporal = new LinkedList<>();

        for (int i = 0; i < matrizUs.length; i++) {

            listaTemporal.add(Integer.parseInt(matrizUs[i]));

            if (contadorParaCadaFila == 8) {
                listaDeListas.add(listaTemporal);
                listaTemporal = new LinkedList<>();
                contadorParaCadaFila = -1;
            }

            contadorParaCadaFila++;
        }

        for (int f = 0; f < 9; f++) {
            for (int c = 0; c < 9; c++) {
                matrizUsuario[f][c] = listaDeListas.get(f).get(c);
            }
        }

        this.tecnica = tecnica;
        matrizCandidatos = this.generarMatrizCandidatos(matrizUsuario);

        String cadena;
        for (int f = 0; f < listaDeListas.size(); f++) {
            cadena = "";
            for (int c = 0; c < listaDeListas.get(f).size(); c++) {
                cadena = cadena + "" + listaDeListas.get(f).get(c);
            }
            System.out.println("" + cadena);
        }

        if (tecnica.equalsIgnoreCase("T1")) {
            return this.metodo1();
        }
        if (tecnica.equalsIgnoreCase("T2")) {
            return this.metodo2();
        }
        if (tecnica.equalsIgnoreCase("T3")) {
            return this.metodo3();
        }

        return "";

    }

    public void resolverPasoApaso() {

    }

    public void resolverUnSoloPaso() {

    }

    public String metodo1() {
        // metodo 1

        ////mostrar los candidatos
        for (int i = 0; i < matrizCandidatos.length; i++) {
            for (int j = 0; j < matrizCandidatos.length; j++) {
                String cadena = " ";
                for (int k = 0; k < matrizCandidatos[i][j].size(); k++) {
                    cadena = cadena + " " + matrizCandidatos[i][j].get(k);
                }
                System.out.println("Candidatos en " + (i + 1) + " " + (j + 1) + " " + cadena);
            }
        }

        Object candidato = -1;
        int paraFila = -1;
        int paraColumna = -1;

        for (int i = 0; i < matrizCandidatos.length; i++) {
            for (int j = 0; j < matrizCandidatos.length; j++) {
                if (matrizCandidatos[i][j].size() == 1) {
                    System.out.println(" " + (i + 1) + " " + (j + 1) + " Colocar " + matrizCandidatos[i][j].get(0));
                    candidato = matrizCandidatos[i][j].get(0);
                    paraFila = i;
                    paraColumna = j;
                    this.eliminarEnFila(candidato, i);
                    this.eliminarEnColumna(candidato, j);
                    j = matrizCandidatos.length;
                    i = matrizCandidatos.length;
                }
            }
        }
        ///colocarlo en la matriz 
        if (!candidato.equals(-1)) {
            matrizUsuario[paraFila][paraColumna] = (int) candidato;

            return ("<OK>" + paraFila + "," + paraColumna + "," + candidato);
        }

        return "<NOT FOUND>";

//       
    }

    public String metodo2() {
        ///metodo 2
        Object candidato = -1;
        int paraFila = -1;
        int paraColumna = -1;

        //este seria para las cajas 
        for (int fila = 1; fila <= 9; fila = fila + 3) {
            for (int columna = 1; columna <= 9; columna = columna + 3) {

                int filIni = (fila / 3) * 3;
                int colIni = (columna / 3) * 3;

                for (int f = filIni; f < filIni + 3; f++) {
                    for (int c = colIni; c < colIni + 3; c++) {
                        if (matrizCandidatos[f][c].size() > 0 && candidato.equals(-1)) {
                            for (int i = 0; i < matrizCandidatos[f][c].size(); i++) {
                                if (this.esUnicoEnLaCaja(matrizCandidatos[f][c].get(i), f, c) == true) {
                                    System.out.println(" en caja " + (f + 1) + " " + (c + 1) + " Colocar " + matrizCandidatos[f][c].get(i));
                                    candidato = matrizCandidatos[f][c].get(i);
                                    paraFila = f;
                                    paraColumna = c;
                                    i = matrizCandidatos[f][c].size();
                                    matrizCandidatos[paraFila][paraColumna].clear();
                                    this.eliminarEnColumna(candidato, c);
                                    this.eliminarEnFila(candidato, f);
                                    columna = 10;
                                    fila = 10;
                                }
                            }
                        }
                    }
                }
            }
        }

        ///revisar para filas 
        if (candidato.equals(-1)) {
            for (int fila = 0; fila < 9; fila++) {
                for (int columna = 0; columna < 9; columna++) {
                    if (matrizCandidatos[fila][columna].size() > 0 && candidato.equals(-1)) {
                        for (int i = 0; i < matrizCandidatos[fila][columna].size(); i++) {
                            if (this.esUnicoEnLaFila(matrizCandidatos[fila][columna].get(i), fila, columna) == true) {
                                System.out.println(" en fila " + (fila + 1) + " " + (columna + 1) + " Colocar " + matrizCandidatos[fila][columna].get(i));
                                candidato = matrizCandidatos[fila][columna].get(i);
                                paraFila = fila;
                                paraColumna = columna;
                                i = matrizCandidatos[fila][columna].size();
                                matrizCandidatos[fila][columna].clear();
                                this.eliminarEnColumna(candidato, columna);
                                this.eliminarEnCaja(candidato, fila, columna);
                            }
                        }
                    }
                }
            }
        }

        //revisar para columnas
        if (candidato.equals(-1)) {
            for (int columna = 0; columna < 9; columna++) {
                for (int fila = 0; fila < 9; fila++) {
                    if (matrizCandidatos[fila][columna].size() > 0 && candidato.equals(-1)) {
                        for (int i = 0; i < matrizCandidatos[fila][columna].size(); i++) {
                            if (this.esUnicoEnLaColumna(matrizCandidatos[fila][columna].get(i), fila, columna) == true) {
                                System.out.println(" en  columna " + (fila + 1) + " " + (columna + 1) + " Colocar " + matrizCandidatos[fila][columna].get(i));
                                candidato = matrizCandidatos[fila][columna].get(i);
                                paraFila = fila;
                                paraColumna = columna;
                                i = matrizCandidatos[fila][columna].size();
                                matrizCandidatos[fila][columna].clear();
                                this.eliminarEnFila(candidato, fila);
                                this.eliminarEnCaja(candidato, fila, columna);
                            }
                        }

                    }
                }
            }
        }

        //colocarlo en la matriz
        if (!candidato.equals(-1)) {
            matrizUsuario[paraFila][paraColumna] = (int) candidato;

            return ("<OK>" + paraFila + "," + paraColumna + "," + candidato);
        }

        return "<NOT FOUND>";

    }

    public String metodo3() {

        boolean yaEjecutado = false;
        //este seria para las cajas 
        for (int fila = 1; fila <= 9; fila = fila + 3) {
            for (int columna = 1; columna <= 9; columna = columna + 3) {

                int filIni = (fila / 3) * 3;
                int colIni = (columna / 3) * 3;

                for (int f = filIni; f < filIni + 3; f++) {
                    for (int c = colIni; c < colIni + 3; c++) {
                        if (matrizCandidatos[f][c].size() == 2) {

                            int filIni2 = (filIni / 3) * 3;
                            int colIni2 = (colIni / 3) * 3;

                            for (int f2 = filIni2; f2 < filIni2 + 3; f2++) {
                                for (int c2 = colIni2; c2 < colIni2 + 3; c2++) {
                                    if (matrizCandidatos[f2][c2].size() == 2 && f != f2 && c != c2) {
                                        if (matrizCandidatos[f][c].get(0) == matrizCandidatos[f2][c2].get(0)
                                                && matrizCandidatos[f][c].get(1) == matrizCandidatos[f2][c2].get(1)) {
                                            this.eliminarEnCaja(matrizCandidatos[f][c].get(0), f, c);
                                            this.eliminarEnCaja(matrizCandidatos[f][c].get(1), f, c);
                                            yaEjecutado = true;
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        //filas 
        if (yaEjecutado == false) {
            for (int f = 0; f < 9; f++) {
                for (int c = 0; c < 9; c++) {
                    if (matrizCandidatos[f][c].size() == 2) {
                        for (int f2 = 0; f2 < 9; f2++) {
                            for (int c2 = 0; c2 < 9; c2++) {
                                if (matrizCandidatos[f2][c2].size() == 2) {
                                    if (matrizCandidatos[f2][c2].size() == 2 && f != f2 && c != c2) {
                                        if (matrizCandidatos[f][c].get(0) == matrizCandidatos[f2][c2].get(0)
                                                && matrizCandidatos[f][c].get(1) == matrizCandidatos[f2][c2].get(1)) {
                                            this.eliminarEnFila(matrizCandidatos[f][c].get(0), f);
                                            this.eliminarEnFila(matrizCandidatos[f][c].get(1), f);
                                            ///toca que crear otro eliminar teniendo en cuenta  los que no se pueden eliminar 
                                            yaEjecutado = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        System.out.println(""+yaEjecutado);
        ////mostrar los candidatos
        for (int i = 0; i < matrizCandidatos.length; i++) {
            for (int j = 0; j < matrizCandidatos.length; j++) {
                String cadena = " ";
                for (int k = 0; k < matrizCandidatos[i][j].size(); k++) {
                    cadena = cadena + " " + matrizCandidatos[i][j].get(k);
                }
                System.out.println("Candidatos en " + (i + 1) + " " + (j + 1) + " " + cadena);
            }
        }

        return "";
    }

    private LinkedList[][] generarMatrizCandidatos(int[][] matrizUsuario) {
        LinkedList[][] matrizM1 = new LinkedList[9][9];

        ///iniciar las listas internas de la matriz 
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                matrizM1[i][j] = new LinkedList();
            }
        }
        //recoorre la matriz en verificando numeros posibles 
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (matrizUsuario[fila][columna] == 0) {
                    for (int k = 1; k <= 9; k++) {
                        if (puedeirColumna(k, matrizUsuario, columna) == true && puedeirFila(k, matrizUsuario, fila) == true && puedeirCaja(k, matrizUsuario, fila, columna) == true) {
                            matrizM1[fila][columna].add(k);
                        }
                    }
                }

            }
        }
        //retorna la matriz con numeros agregados.
        return matrizM1;
    }

    //verifica que el numero pueda  ir en esa columna 
    private boolean puedeirColumna(int k, int[][] matrizUsuario1, int columna) {
        boolean puedeIr = true;

        for (int fila = 0; fila < matrizUsuario1.length; fila++) {
            if (matrizUsuario1[fila][columna] == k) {
                puedeIr = false;
            }
        }
        return puedeIr;
    }

    //verifica que un numero pueda ir en esa columna
    private boolean puedeirFila(int k, int[][] matrizUsuario1, int fila) {

        boolean puedeIr = true;

        for (int columna = 0; columna < matrizUsuario1.length; columna++) {
            if (matrizUsuario1[fila][columna] == k) {
                puedeIr = false;
            }
        }
        return puedeIr;
    }

    //verifica que el numero pueda ir en esa caja 
    private boolean puedeirCaja(int k, int[][] matrizUsuario1, int fila, int columna) {
        boolean puedeIr = true;
        int filIni = (fila / 3) * 3;
        int colIni = (columna / 3) * 3;

        for (int f = filIni; f < filIni + 3; f++) {
            for (int c = colIni; c < colIni + 3; c++) {
                if (matrizUsuario1[f][c] == k) {
                    puedeIr = false;
                }
            }
        }

        return puedeIr;
    }

    private void eliminarEnFila(Object candidato, int i) {
        for (int j = 0; j < 9; j++) {
            if (matrizCandidatos[i][j].contains(candidato)) {
                matrizCandidatos[i][j].remove(candidato);
            }
        }
    }

    private void eliminarEnColumna(Object candidato, int j) {
        for (int i = 0; i < 9; i++) {
            if (matrizCandidatos[i][j].contains(candidato)) {
                matrizCandidatos[i][j].remove(candidato);
            }
        }
    }

    private void eliminarEnCaja(Object candidato, int fila, int columna) {
        int filIni = (fila / 3) * 3;
        int colIni = (columna / 3) * 3;

        for (int f = filIni; f < filIni + 3; f++) {
            for (int c = colIni; c < colIni + 3; c++) {
                if (matrizCandidatos[f][c].contains(candidato)) {
                    matrizCandidatos[f][c].remove(candidato);
                }
            }
        }
    }

    private boolean esUnicoEnLaCaja(Object candidato, int fila, int columna) {
        boolean puedeIr = true;
        int filIni = (fila / 3) * 3;
        int colIni = (columna / 3) * 3;

        for (int f = filIni; f < filIni + 3; f++) {
            for (int c = colIni; c < colIni + 3; c++) {
                for (int i = 0; i < matrizCandidatos[f][c].size(); i++) {
                    if (matrizCandidatos[f][c].get(i) == candidato && (f != fila || c != columna)) {
                        puedeIr = false;
                    }
                }

            }
        }

        return puedeIr;
    }

    private boolean esUnicoEnLaFila(Object get, int fila, int c) {
        boolean puedeIr = true;

        for (int columna = 0; columna < matrizCandidatos.length; columna++) {
            for (int i = 0; i < matrizCandidatos[fila][columna].size(); i++) {
                if (matrizCandidatos[fila][columna].get(i).equals(get) && c != columna) {
                    puedeIr = false;
                }
            }
        }
        return puedeIr;
    }

    private boolean esUnicoEnLaColumna(Object get, int f, int columna) {
        boolean puedeIr = true;

        for (int fila = 0; fila < matrizCandidatos.length; fila++) {
            for (int i = 0; i < matrizCandidatos[fila][columna].size(); i++) {
                if (matrizCandidatos[fila][columna].get(i).equals(get) && f != fila) {
                    puedeIr = false;
                }
            }
        }
        return puedeIr;
    }

    /*metodo que me verifica que el nombre de usuario exista o no*/
    public boolean VerificarUser(String user) {
        boolean flag = false;
        for (int i = 0; i < getClients().size(); i++) {
            if (getClients().get(i).getTheUserName().equals(user)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /*metodo para ver todos los usuario */
    public String UsuariosT() {
        String usuTotal = "user:";
        for (int i = 0; i < getClients().size(); i++) {
            usuTotal += getClients().get(i).getTheUserName() + ",";
        }
        return usuTotal;
    }

    /* metodo para enviar un mensje a todos*/
    public void SendAll(String message) {
        for (int i = 0; i < this.getClients().size(); i++) {
            this.getClients().get(i).writeMessage(message);
        }
    }

    /*metodo para enviar a un solo usuario*/
    public void Send(String username, String message) {
        System.out.println("cliente Messenger Server" + username);

        for (int i = 0; i < getClients().size(); i++) {

            System.out.println("cliente for Messenger Server" + getClients().get(i).getTheUserName() + "el username" + username);
            if (getClients().get(i).getTheUserName().equals(username)) {
                System.out.println("cliente messenger server 3" + getClients().get(i).getTheUserName() + " " + message);
                getClients().get(i).writeMessage(message);
            }
        }
    }

    public void run() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        SocketController client = null;
        boolean quit = false;

        try {
            serverSocket = new ServerSocket(thePort);
            while (!quit) {
                try {
                    socket = serverSocket.accept();
                    client = new SocketController(socket, username, this);
                    this.clients.add(client);//esta estaba comentada

                } catch (IOException ex) {
                    Logger.getLogger(SudokuServer_.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(SudokuServer_.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the clients
     */
    public LinkedList<SocketController> getClients() {
        return this.clients;
    }

    /**
     * @param clients the clients to set
     */
    public void setClients(LinkedList<SocketController> clients) {
        this.clients = clients;
    }

}
