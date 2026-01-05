import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Nodo implements Comparable<Nodo> {
    int x, y; // Coordenadas
    int gCost, hCost, fCost; //Costos
    Nodo parent; //Nodo padre
    boolean isWall = false; //Es una pared?
    boolean isOpen = false; //Está abierto?
    boolean isClosed = false; //Está cerrado?

    public Nodo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Calcula los costos de un nodo a otro
    public void setCosts(Nodo target, int g) {
        this.gCost = g;
        // Distancia de Manhattan
        this.hCost = Math.abs(x - target.x) + Math.abs(y - target.y);
        this.fCost = gCost + hCost;
    }

    @Override
    public int compareTo(Nodo other) {
        //Activar tiebreaker para caminos más directos
//        if (this.fCost == other.fCost) {
//            //Si F es el mismo, escoge el más cercano al objetivo (menor H)
//            return Integer.compare(this.hCost, other.hCost);
//        }
        return Integer.compare(this.fCost, other.fCost);
    }
}