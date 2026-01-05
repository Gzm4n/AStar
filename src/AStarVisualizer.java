import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class AStarVisualizer extends JPanel {
    // 1. CONFIGURACIONES DE LOS DATOS
    private final int TILE_SIZE = 20;
    private final int ROWS = 20;
    private final int COLS = 20;

    // 2. ESTRUCTURAS DE DATOS
    private Nodo[][] grid;
    private PriorityQueue<Nodo> openSet;
    private HashSet<Nodo> closedSet;
    private Nodo startNode, endNode;
    private boolean solved = false;

    public AStarVisualizer() {
        grid = new Nodo[ROWS][COLS];
        openSet = new PriorityQueue<>(); // Para organizar los nodos por fCost
        closedSet = new HashSet<>(); // Para evitar revisar nodos repetidos

        // Inicializar el grid con nodos
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = new Nodo(c, r);
            }
        }

        // Nodos de inicio y fin
        startNode = grid[3][3];
        endNode = grid[15][15];

        // Mouse Listener para dibujar paredes
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / TILE_SIZE; //Se divide para normalizar las coordenadas en pixeles
                int row = e.getY() / TILE_SIZE;
                if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
                    grid[row][col].isWall = !grid[row][col].isWall; // Convierte a pared si no lo es y viceversa
                    repaint(); // Actualiza la pantalla
                }
            }
        });
    }

    // Este metodo es automáticamente llamado por Swing para dibujar el panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpia la pantalla

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Nodo n = grid[r][c];

                // Determina el color de cada celda basado en su estado
                if (n == startNode) g.setColor(Color.GREEN);
                else if (n == endNode) g.setColor(Color.RED);
                else if (n.isWall) g.setColor(Color.BLACK);
                else if (n.isClosed) g.setColor(new Color(150, 150, 255)); // Explorado
                else if (n.isOpen) g.setColor(new Color(200, 200, 255));   // A explorar
                else g.setColor(Color.WHITE);

                // Dibujar los cuadrados de los nodos
                g.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE); // Multiplicacion para convertir tile_size a pixeles y width/height
                // dibujar las lineas entre nodos
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE); //Nótese que pone draw y no fill
            }
        }

        // Resalta la ruta encontrada
        drawPath(g);
    }

    private void drawPath(Graphics g) {
        g.setColor(Color.YELLOW);
        Nodo current = endNode.parent;
        while (current != null && current != startNode) {
            g.fillRect(current.x * TILE_SIZE, current.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            current = current.parent;
        }
    }

    // LA LÓGICA DEL ALGORITMO A*
    public void runAStar() {
        openSet.add(startNode);

        // Se utiliza Swing Timer para animar (50ms de delay)
        javax.swing.Timer timer = new javax.swing.Timer(50, e -> {
            if (!openSet.isEmpty()) {
                Nodo current = openSet.poll();

                if (current == endNode) {
                    ((javax.swing.Timer)e.getSource()).stop(); // Detener el timer
                    System.out.println("Camino encontrado");
                }

                current.isClosed = true;

                // Verificar vecinos
                checkNeighbors(current);

                repaint(); // Para llamar a paintComponent
            }
        });
        timer.start();
    }

    private void checkNeighbors(Nodo current) {
        int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};
        for (int[] d : dirs) {
            int newR = current.y + d[0];
            int newC = current.x + d[1];

            if (newR >= 0 && newR < ROWS && newC >= 0 && newC < COLS) {
                Nodo neighbor = grid[newR][newC];
                if (neighbor.isWall || neighbor.isClosed) continue; //Para ignorar los siguientes pasos si es una pared o está cerrado

                int tentativeG = current.gCost + 1;
                if (tentativeG < neighbor.gCost || !openSet.contains(neighbor)) {
                    neighbor.parent = current;
                    neighbor.setCosts(endNode, tentativeG);
                    if (!openSet.contains(neighbor)) {
                        neighbor.isOpen = true;
                        openSet.add(neighbor);
                    }
                }
            }
        }
    }

    public void reset() {
        openSet.clear();
        closedSet.clear();
        solved = false; // Reiniciar la bandera de terminado

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Nodo n = grid[r][c];
                n.isOpen = false;
                n.isClosed = false;
                n.parent = null;
                n.gCost = 0;
                n.hCost = 0;
                n.fCost = 0;
            }
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("A estrella");
        AStarVisualizer visualizer = new AStarVisualizer();

        // Crear un panel con botones
        JPanel controlPanel = new JPanel();

        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(e -> visualizer.runAStar());

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> visualizer.reset());

        controlPanel.add(startBtn);
        controlPanel.add(resetBtn);

        frame.setLayout(new BorderLayout());
        frame.add(visualizer, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH); // Botones al final

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}