package ia;

import java.util.ArrayList;

public class Bau implements MapObject{
    private int x, y;
    private ArrayList<Saco> conteudo = new ArrayList<>();

    public Bau(int x, int y) {
        this.x = x;
        this.y = y;
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

    public ArrayList<Saco> getConteudo() {
        return conteudo;
    }

    public void setConteudo(ArrayList<Saco> conteudo) {
        this.conteudo = conteudo;
    }

    public void addSaco(Saco saco) {
        this.conteudo.add(saco);
    }

    public int getTotal(){
        int acc = 0;
        for(int i = 0; i < conteudo.size(); i++){
            acc += conteudo.get(i).getQuantidade();
        }
        return acc;
    }

    @Override
    public String toString() {
        return "B";
    }
}
