package ia;

public class Saco implements MapObject{
    private int x, y, quantidade;

    public Saco(int x, int y, int quantidade) {
        this.x = x;
        this.y = y;
        this.quantidade = quantidade;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "$";
    }
}
