package graph;

public class Coordination {
    private Graph G;
    private int nCol;
    private int nRow;

    public Coordination(Graph G) {
        this.G = G;

        init();
    }

    private void init() {
        int nSwitch = G.switches().size();
        nCol = (int) Math.sqrt(nSwitch);
        nRow = nSwitch % nCol == 0 ? nCol : nCol + 1;
    }

    public double distanceBetween(int u, int v) {
        if (!G.isSwitchVertex(u) || !G.isSwitchVertex(v)) {
            throw new RuntimeException("Node must be switch");
        }

        return manhattanDistance(u, v);
    }

    public int manhattanDistance(int u, int v) {
        int ux = u % nCol;
        int uy = u / nCol;
        int vx = v % nCol;
        int vy = v / nCol;
        return Math.abs(ux - vx) + Math.abs(uy - vy);
    }

    public String getCoordOfSwitch(int u)
    {
        int ux = u % nCol;
        int uy = u / nCol;
        return ux + "\t" + uy;
    }

    public String getCoordOfHost(int s, double bias)
    {
        int hx = s % nCol;
        int sy = s / nCol;
        double hy = sy + bias;
        return hx + "\t" + hy;
    }

    public double totalCableLength() {
        double totalLength = 0;
        for (int sw1: G.switches()) {
            for (int sw2: G.adj(sw1)) {
                if (G.isSwitchVertex(sw2)) {
                    totalLength += distanceBetween(sw1, sw2);
                }
            }
        }

        return totalLength / 2;
    }
}
