package ia;

import java.util.*;

public class Agente implements MapObject{

    //CLASSE PRIVADA
    private class RouteObject {
        MapObject conteudo;
        RouteObject cameFrom;
        int deslocamento;
        double heurist;

        private RouteObject(MapObject conteudo, RouteObject from, int desl, double heur){
            this.conteudo = conteudo;
            cameFrom = from;
            deslocamento = desl;
            heurist = heur;
        }

        private double getCost(){ return deslocamento + heurist;}
        private MapObject getConteudo(){return conteudo;}
    }

    //ATRIBUTOS
    private int x;
    private int y;
    private int pontos = 0;

    private MapObject under;
    private Porta portaLocal;
    private Random rand;
    private ArrayList<Bau> bausLocal = new ArrayList<>();
    private ArrayList<Saco> inventario = new ArrayList<>();
    private Object[][] vision;

    //CONSTRUTORES
    public Agente(int x, int y, MapObject under) {
        this.x = x;
        this.y = y;
        this.under = under;
        rand = new Random();
    }

    public Agente(int x, int y, MapObject under, Random rand) {
        this.x = x;
        this.y = y;
        this.under = under;
        this.rand = rand;
    }

    //GETTERS
    public int                      getX() { return x; }
    public int                      getY() { return y; }
    public int                 getPontos() { return pontos; }
    public ArrayList<Saco> getInventario() { return inventario; }

    //SETTERS
    public void          setX(int x)                      { this.x = x; }
    public void          setY(int y)                      { this.y = y; }
    public void     setPontos(int pontos)                 { this.pontos = pontos; }
    public void setInventario(ArrayList<Saco> inventario) { this.inventario = inventario; }

    //TOSTRING
    @Override
    public String toString() { return "@"; }

    //MÉTODOS
    public int getMoedas() {
        int acc = 0;
        for(int i = 0; i < inventario.size(); i++){
            acc += inventario.get(i).getQuantidade();
        }
        return acc;
    }

    public void setVisible(){
        Object[][] mapa = Main.mapa;

        int xSize = Math.min(x+2, 9) - Math.max(x-2, 0) + 1;
        int ySize = Math.min(y+2, 9) - Math.max(y-2, 0) + 1;
        Object[][] res = new Object[xSize][ySize];

        //System.out.println(res.length);
        //System.out.println(res[1].length);

        for(int i = Math.max(x-2, 0); i <= Math.min(x+2, 9); i++){
            //System.out.println("i: "+i);
            for(int j = Math.max(y-2, 0); j <= Math.min(y+2, 9); j++){
            //System.out.println("j: "+j);
                res[i - Math.max(x-2, 0)][j - Math.max(y-2, 0)] = mapa[i][j];
            }
        }
        vision = res;
    }

    public void start(){
//        System.out.println("---------------Start---------------");
//        printVision();
        while(inventario.size() < 10) {
            setVisible();
            findDoor();
            getChestsInVision();

            System.out.println("Estado: Procurando Sacos de moedas.");
            Saco sack = findSack();
            if (sack != null) {
                System.out.println("Achei um!");
                moveTo(sack);
                grabBag();
            } else {
                System.out.println("Não achei nenhum. Vou andar aleatóriamente");
                moveTo(randomDirection());
            }
        }

        System.out.println("Lista de sacolas:");
        System.out.println(Arrays.toString(inventario.stream().map(Saco::getQuantidade).toArray()));

        System.out.println("Achei todas as sacolas, vou procurar os baús");
        findAllChests();

        System.out.println("Achei todos os baús. Agora vou fazer a distribuição.");
        int[] arr = new int[10], distrib;
        for(int i = 0; i < 10; i++){
            arr[i] = inventario.get(i).getQuantidade();
        }
        do{
            Genetico g = new Genetico(arr, rand);
            distrib = g.realizar();
        }while(distrib == null);
        System.out.println("Achei uma distribuição:");
        System.out.println(Arrays.toString(distrib));
        System.out.println(Arrays.toString(inventario.stream().map(Saco::getQuantidade).toArray()));

        for(int i = 0; i < distrib.length; i++){
            System.out.printf("Indo até o %dº baú\n", distrib[i]+1);
            Bau currChest = bausLocal.get(distrib[i]);
            moveTo(currChest);
            placeInChest(currChest);
        }
        System.out.println("Coloquei todas as sacolas nos baús:");
        for(Bau b : bausLocal){
            System.out.printf("%d: %s\n", bausLocal.indexOf(b),
                    Arrays.toString(b.getConteudo().stream().map(Saco::getQuantidade).toArray()));
        }

        System.out.println("Agora vou procurar a porta de saída");
        while(portaLocal == null){
            moveTo(randomDirection());
        }
        moveTo(portaLocal);
        System.out.println("Estou na porta final, acabei tudo");
        Main.printMapa();
    }

    public void moveTo(MapObject o){
        System.out.println("Moveto");
        if(x == o.getX() && y == o.getY()){
            return;
        }

        ArrayList<MapObject> path = aStar(x, y, o.getX(), o.getY());

        System.out.println("------------------AFTER--------------------");
        System.out.println(Arrays.toString(path.toArray()));

        Main.printMapa();
        for(int i = 0; i < path.size(); i++){
            try {
                Thread.sleep(1000);
            } catch(Exception e){}

            //Move agente
            Main.mapa[x][y] = under;
            under = path.get(i);
            x = path.get(i).getX();
            y = path.get(i).getY();
            Main.mapa[x][y] = this;
            setVisible();
            getChestsInVision();
            findDoor();

            //Visualização
            Main.printMapa();

        }
    }

    public MapObject findNearest(MapObject obj){
        if(obj instanceof Muro == false && obj instanceof Buraco == false){
            return obj;
        }
        double distance = Double.MAX_VALUE;
        MapObject res = null;

        MapObject[][] mapa = Main.mapa;
        for(int i = 0; i < mapa.length; i++){
            for(int j = 0; j < mapa[i].length; j++){
                if(mapa[i][j] instanceof Muro == false && mapa[i][j] instanceof Buraco == false){
                    double currDist = distance(mapa[i][j], obj);
                    if(currDist < distance){
                        distance = currDist;
                        res = mapa[i][j];
                    }
                }
            }
        }
        return res;
    }

    public MapObject findNearest(int x, int y){
        return findNearest((MapObject)Main.mapa[x][y]);
    }

    public ArrayList<MapObject> aStar(int x1, int y1, int x2, int y2){
//        System.out.println("------aStar----------");
//        System.out.printf("%d,%d\n%d,%d\n", x1, y1, x2, y2);
        MapObject[][]mapa = Main.mapa;
        ArrayList<RouteObject> visited = new ArrayList<>();
        ArrayList<RouteObject> toVisit = new ArrayList<>();

        MapObject atual = (MapObject)mapa[x1][y1];
//        System.out.println("atual: "+atual);
//        System.out.printf("x: %d, y: %d\n", atual.getX(), atual.getY());

        MapObject objetivo = (MapObject)mapa[x2][y2];
//        System.out.println("objetivo: "+objetivo);

        RouteObject curr = new RouteObject(atual, null, 0, distance(atual,objetivo));

        //Add first
        visited.add(curr);
//        System.out.println("curr1: "+curr.conteudo);
//        System.out.printf("---------estado curr 1---------: %d %d\n", curr.conteudo.hashCode(), objetivo.hashCode());
        if(curr.conteudo == objetivo){
            return new ArrayList<>();
        }
        do{
//            try{
//                Thread.sleep(1000);
//            }catch(Exception e){}
            ArrayList<MapObject> v = getNeighbors(mapa, curr);
//            System.out.println("neighbors: "+Arrays.toString(v.toArray()));
            ArrayList<RouteObject> vizinhos = new ArrayList<>();
            for(MapObject viz : v){
//                System.out.printf("viz: %s %d,%d\n", viz, viz.getX(), viz.getY());
                boolean previous = false;
                for(int i = 0; i < visited.size(); i++){
                    if(visited.get(i).conteudo == viz){
//                        System.out.println("Already Visited: "+viz);
                        previous = true;
                        break;
                    }
                }
                if(previous){continue;}

                if(viz instanceof Buraco){
                    visited.add(new RouteObject(viz, curr, curr.deslocamento + 1, distance(viz, objetivo)));
                    MapObject jump = tratarBuraco(mapa, curr, viz);
//                    System.out.printf("jump: %s %d,%d\n", jump, jump.getX(), jump.getY());
                    if(jump instanceof Muro == false) {
                        vizinhos.add(new RouteObject(jump, curr, curr.deslocamento + 1, distance(jump, objetivo)));
                    }
                } else {
                    vizinhos.add(new RouteObject(viz, curr, curr.deslocamento + 1, distance(viz, objetivo)));
                }
            }
//            System.out.println("Vizinhos: "+Arrays.toString((vizinhos.stream().map(RouteObject::getConteudo)).toArray()));
            insertIntoArray(toVisit, vizinhos);
//            Object[] print = (toVisit.stream().map(RouteObject::getConteudo)).toArray();
//            System.out.println("toVisit: "+Arrays.toString(print));
            curr = toVisit.remove(0);
            visited.add(curr);

//            System.out.printf("curr: %s %d,%d\n", curr.conteudo, curr.conteudo.getX(), curr.conteudo.getY());
//            System.out.printf("---------estado curr---------: %d %d\n", curr.conteudo.hashCode(), objetivo.hashCode());
        }while(curr.conteudo != objetivo && toVisit.size() != 0);

        if(curr.conteudo != objetivo){
            return new ArrayList<>();
        }
        ArrayList<MapObject> res = new ArrayList<>();

        while(curr != null){
            res.add(curr.conteudo);
            curr = curr.cameFrom;
        }

        Collections.reverse(res);
        res.remove(0);

        return res;
    }

    public MapObject tratarBuraco(MapObject[][] mapa, RouteObject curr, MapObject buraco){
//        System.out.println("tratar buraco");

//        System.out.printf("local x: %d local y: %d\n", curr.conteudo.getX(), curr.getConteudo().getY());
//        System.out.printf("buraco x: %d buraco y: %d\n", buraco.getX(), buraco.getY());
        int deltaX = curr.conteudo.getX() - buraco.getX();
        int deltaY = curr.conteudo.getY() - buraco.getY();

//        System.out.printf("delta x: %d delta y: %d\n", deltaX, deltaY);
//        System.out.printf("x: %d, y: %d\n", buraco.getX()+deltaX, buraco.getY()+deltaY);
        int xFinal = Math.min(9, Math.max(0, buraco.getX()-deltaX));
        int yFinal = Math.min(9, Math.max(0, buraco.getY()-deltaY));

//        System.out.printf("xFinal: %d, yFinal: %d\n", xFinal, yFinal);
        return (MapObject)mapa[xFinal][yFinal];
    }

    public void insertIntoArray(ArrayList<RouteObject> arr, ArrayList<RouteObject> toInsert){
//        System.out.println("Insert into Array");
        for(RouteObject v : toInsert){
            for(int i = 0; i < arr.size(); i++){
                if(arr.get(i).getCost() > v.getCost()){
                    arr.add(i, v);
                    break;
                }
            }
            arr.add(v);
        }
    }

    public ArrayList<MapObject> getNeighbors(MapObject[][] mapa, int x1, int y1){
        //System.out.println("get neighbors");
        ArrayList<MapObject> res = new ArrayList<>();

        for(int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                int xCenter = ((MapObject)mapa[i][j]).getX();
                int yCenter = ((MapObject)mapa[i][j]).getY();
                //System.out.printf("x: %d, y: %d\n", xCenter, yCenter);
                if(distance(x1, y1, xCenter, yCenter) <= 1.5 &&
                   distance(x1, y1, xCenter, yCenter) > 0 &&
                   mapa[i][j] instanceof Muro == false){
                    res.add((MapObject) mapa[i][j]);
                }
            }
        }

        return res;
    }

    public ArrayList<MapObject> getNeighbors(MapObject[][] mapa, RouteObject pos){
        return getNeighbors(mapa, pos.conteudo.getX(), pos.conteudo.getY());
    }

    public double distance(int x1, int y1, int x2, int y2){
        int x = Math.abs(x1 - x2);
        int y = Math.abs(y1 - y2);
        return Math.sqrt((x*x)+(y*y));
    }

    public double distance (MapObject a, MapObject b){
        return distance(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public void printVision(){
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                System.out.print(vision[i][j]);
            }
            System.out.println();
        }
    }

    public Porta findDoor(){
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                if(vision[i][j] instanceof Porta){
                    portaLocal = (Porta)vision[i][j];
                    return portaLocal;
                }
            }
        }

        return null;
    }

    public void getChestsInVision(){
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                if(vision[i][j] instanceof Bau){
                    System.out.println("Bau em vista");
                    if (!bausLocal.contains(vision[i][j])) {
                        System.out.println("Bau novo");
                        bausLocal.add((Bau)vision[i][j]);
                        System.out.println(Arrays.toString(bausLocal.toArray()));
                    }
                }
            }
        }
    }

    public Saco findSack(){
//        System.out.println("--------------findsack---------");
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                if(vision[i][j] instanceof Saco){
                    Saco res = (Saco)vision[i][j];
//                    System.out.println("saco: "+res+" "+res.getX()+", "+res.getY());
                    return res;
                }
            }
        }
//        System.out.println("saco: "+null);
        return null;
    }

    public void grabBag(){
        if(under instanceof Saco){
            inventario.add((Saco)under);
            under = new Chao(under.getX(), under.getY());
            System.out.println("Peguei uma Sacola. Conteúdo: "+inventario.get(inventario.size()-1).getQuantidade()+" Moedas");
        }
    }

    public MapObject randomDirection(){
        boolean a = rand.nextBoolean();
        boolean b = rand.nextBoolean();

        System.out.println("Tentando os cantos");
        for(int i = 0; i < 4; i++) {
            a = rand.nextBoolean();
            b = rand.nextBoolean();
            System.out.println("a: "+a);
            System.out.println("b: "+a);

            int x = a ? vision.length-1 : 0;
            int y = b ? vision[x].length-1 : 0;
            MapObject local = (MapObject)vision[x][y];
            if(local instanceof Muro == false && local instanceof Buraco == false && local instanceof Agente == false){
                System.out.printf("Achei local: %d %d\n", local.getX(), local.getY());
                setVisible();
                return local;
            }
        }

        System.out.println("Nao consegui os cantos, tentando os outros");
        for(int i = a? vision.length-1 : 0; i != (a? -1 : vision.length); i += a? -1 : 1){
            for(int j = b? vision[i].length-1 : 0; j != (b? -1 : vision[i].length); j += b? -1 : 1){
                MapObject local = (MapObject)vision[i][j];
                if(local instanceof Chao || local instanceof Bau || local instanceof Porta || local instanceof Saco){
                    System.out.printf("Achei local: %d %d\n", local.getX(), local.getY());
                    setVisible();
                    return local;
                }
            }
        }

        System.out.println("Não achei");
        setVisible();
        return this;
    }

    public void findAChest(){
        System.out.println("Procurando por algum bau");
        while(under instanceof Bau == false){
            setVisible();
            getChestsInVision();
            System.out.println("Baus: "+Arrays.toString(bausLocal.toArray()));
            if(bausLocal.size() > 0) {
                moveTo(bausLocal.get(rand.nextInt(bausLocal.size())));
                return;
            }
            moveTo(randomDirection());

        }
    }

    public void findAllChests(){
        System.out.println("Baus: "+Arrays.toString(bausLocal.toArray()));
        if(bausLocal.size() == 4){
            return;
        }

        if(under instanceof Bau == false){
            findAChest();
        }

        boolean moveX = false, moveY = false;
        while(!moveX && !moveY) {
            switch (under.getX()) {
                case 1: case 8:
                    switch (under.getY()) {
                        case 1: case 8:
                            int localX = x, localY = y;
                            moveTo(findNearest(localX, (localY+6) % 12));
                            getChestsInVision();
                            moveTo(findNearest((localX+6) % 12, localY));
                            getChestsInVision();
                            findAChest();
                            break;

                        default:
                            moveX = true;
                    }
                    break;

                default:
                    moveY = true;

            }
        }

        while(bausLocal.size() < 4){
            moveTo(findNearest(x, rand.nextInt(10)));
            getChestsInVision();
        }
    }

    public void placeInChest(Bau chest){
        System.out.printf("Largando a sacola de %d moedas no baú\n", inventario.get(0).getQuantidade());
        chest.addSaco(inventario.remove(0));
    }
}
