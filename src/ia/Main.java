package ia;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {
    public static int[][][] templates = {
            { //TEMPLATE
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0}
            },
            { //1
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,0,0,0,0},
                    {0,0,1,1,1,1,0,0,0,0},
                    {0,0,1,0,0,0,1,0,0,0},
                    {0,0,0,0,0,0,1,0,0,0},
                    {0,0,1,1,1,1,1,0,0,0},
                    {0,0,0,0,0,0,1,0,0,0},
                    {0,0,0,0,0,0,1,0,0,0}
            },
            { //2
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,1,1,1,1,1,0,0,0,0},
                    {0,0,0,0,0,1,1,0,0,0},
                    {0,0,0,0,0,1,1,0,0,0},
                    {0,0,0,0,0,1,1,0,0,0},
                    {0,0,0,1,1,1,1,1,0,0},
                    {0,0,0,0,0,0,1,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0}
            },
            { //3
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,1,1,0,0,1,0,0,0},
                    {0,0,0,1,0,1,0,0,0,0},
                    {0,0,0,1,1,0,0,0,0,0},
                    {0,0,0,1,0,1,0,0,0,0},
                    {0,0,1,1,0,0,1,0,0,0},
                    {1,1,1,1,1,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0}
            },
            { //4
                    {0,0,1,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,1,0,0,0},
                    {0,0,1,0,0,0,1,0,0,0},
                    {0,0,1,1,0,0,1,0,0,0},
                    {0,0,1,1,0,0,1,0,0,0},
                    {0,0,0,1,0,0,1,0,0,0},
                    {0,0,0,1,0,0,0,0,0,0},
                    {0,0,1,1,1,1,1,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0}
            },
            { //4
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,0,0,0,0,0},
                    {0,0,1,0,1,0,0,1,0,0},
                    {0,0,1,1,1,1,1,1,0,0},
                    {0,0,0,0,0,1,0,0,0,0},
                    {0,0,1,1,1,1,1,1,0,0},
                    {0,0,0,0,1,0,0,0,0,0},
                    {0,0,1,1,1,1,1,1,0,0},
                    {0,0,1,0,0,1,0,1,0,0},
                    {0,0,0,0,0,0,0,0,0,0}
            },
    };

    public static MapObject[][] mapa;

    public static void main(String[] args) {
        long seed = Instant.now().getEpochSecond();
        System.out.println("seed: "+seed);
        Random rand = new Random(1538813152);

//        //teste
//        int[] teste = {210, 140, 100, 150, 100, 50, 100, 70, 40, 40};
//        Genetico g = new Genetico(teste, rand);
//        System.out.println("Melhor aptidao:");
//        System.out.println(g.calcularAptidao(new int[]{0, 1, 2, 2, 3, 3, 3, 1, 0, 1}, teste));
//
//        //int count = 5;
//        int[] res;
//
//        do{
//            g = new Genetico(teste, rand);
//            //count--;
//            res = g.realizar();
//        }while(res == null);
//
//        System.out.println("Final");
////        System.out.println(5-count);
//        System.out.println(Arrays.toString(res));

        int[][] template = templates[rand.nextInt(templates.length)];
        System.out.println(template[9][9]);

        //Fazer mapa
        mapa = new MapObject[10][10];


        for(int i = 0; i < mapa.length; i++){
            for(int j = 0; j < mapa[0].length; j++){
                if(template[i][j] == 0){
                    mapa[i][j] = new Chao(i, j);
                } else {
                    mapa[i][j] = new Muro(i, j);
                }
            }
        }

        //printMapa(mapa);

        //GERAR PAREDAO, PORTA E BAUS
        int lado = rand.nextInt(4), portaX, portaY, baus = 4;
        System.out.println(lado);
        switch (lado){
            case 0: //CIMA

                //PAREDAO
                for (int i = 0; i < mapa[0].length; i++){
                    mapa[0][i] = new Muro(0, i);
                }

                //PORTA
                portaY = rand.nextInt(10);
                mapa[0][portaY] = new Porta(0, portaY);

                //BAUS
                while(baus != 0){
                    int local = rand.nextInt(10);
                    if(mapa[1][local] instanceof Bau == false) {
                        mapa[1][local] = new Bau(1, local);
                        baus--;
                    }
                }
                break;

            case 1: //ESQUERDA

                //PAREDAO
                for (int i = 0; i < mapa[0].length; i++){
                    mapa[i][0] = new Muro(i, 0);
                }

                //PORTA
                portaX = rand.nextInt(10);
                mapa[portaX][0] = new Porta(portaX, 0);

                //BAUS
                while(baus != 0){
                    int local = rand.nextInt(10);
                    if(mapa[local][1] instanceof Bau == false) {
                        mapa[local][1] = new Bau(local, 1);
                        baus--;
                    }
                }
                break;

            case 2: //BAIXO

                //PAREDAO
                for (int i = 0; i < mapa[0].length; i++){
                    mapa[9][i] = new Muro(9, i);
                }

                //PORTA
                portaY = rand.nextInt(10);
                mapa[9][portaY] = new Porta(9, portaY);

                //BAUS
                while(baus != 0){
                    int local = rand.nextInt(10);
                    if(mapa[8][local] instanceof Bau == false) {
                        mapa[8][local] = new Bau(8, local);
                        baus--;
                    }
                }
                break;

            case 3: //DIREITA

                //PAREDAO
                for (int i = 0; i < mapa[0].length; i++){
                    mapa[i][9] = new Muro(i, 9);
                }

                //PORTA
                portaX = rand.nextInt(10);
                mapa[portaX][9] = new Porta(portaX, 9);

                //BAUS
                while(baus != 0){
                    int local = rand.nextInt(10);
                    if(mapa[local][8] instanceof Bau == false) {
                        mapa[local][8] = new Bau(local, 8);
                        baus--;
                    }
                }
                break;
        }
        //printMapa(mapa);

        //GERAR SACOS
        int preenchidos;
        int[] valores = new int[10];

        int totais = Math.abs(rand.nextInt(5000)+100);
        System.out.println("Totais: "+totais);
        valores[0] = valores[1] = valores[2] = valores[3] = totais;
        preenchidos = 4;

        while(preenchidos != 10){
            int takeFrom = rand.nextInt(preenchidos);
            int amount = rand.nextInt(valores[takeFrom]);
            valores[takeFrom] -= amount;
            valores[preenchidos] = amount;
            preenchidos++;
        }

        int sacos = 10;
        while(sacos != 0){
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);

            if(mapa[x][y] instanceof Chao){
                mapa[x][y] = new Saco(x, y, valores[sacos-1]);
                sacos--;
            }
        }
        //printMapa();

        //GERAR BURACOS
        int buracos = 5;
        while(buracos != 0){
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);

            if(!checkHoles(x, y) && mapa[x][y] instanceof Chao){
                mapa[x][y] = new Buraco(x, y);
                buracos--;
            }
        }
        //printMapa();

        //GERAR AGENTE
        boolean agenteGerado = false;
        Agente ag = null;
        while(agenteGerado == false){
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);

            if(mapa[x][y] instanceof Chao){
                System.out.println("agente x: "+x+"y: "+y);
                mapa[x][y] = new Agente(x, y, (MapObject) mapa[x][y], rand);
                ag = (Agente)mapa[x][y];
                agenteGerado = true;
            }
        }
        printMapa();
        printMoedas();

        ag.start();
        //printMoedas();

        System.out.println("seed: "+seed);
        System.out.println("FIM");
    }

    public static boolean checkHoles(int x, int y){
        for(int i = Math.max(x-1, 0); i <= Math.min(x+1, 9); i++) {
            for(int j = Math.max(y-1, 0); j <= Math.min(y+1, 9); j++){
                if(mapa[i][j] instanceof Buraco){
                    return true;
                }
            }
        }

        return false;
    }

    public static void printMapa(){
        System.out.println("-------------");
        for(int i = 0; i < mapa.length; i++){
            System.out.print("|");
            for (int j = 0; j < mapa[0].length; j++){
                System.out.print(mapa[i][j]);
            }
            System.out.println("|");
        }
        System.out.println("------------");
    }

    public static void printMoedas(){
        int acc = 0;
        for(int i = 0; i < mapa.length; i++){
            for (int j = 0; j < mapa[0].length; j++){
                if(mapa[i][j] instanceof Saco){
                    System.out.print(((Saco)mapa[i][j]).getQuantidade()+" ");
                    acc += ((Saco)mapa[i][j]).getQuantidade();
                }
            }
        }
        System.out.println();
        System.out.println("Total: "+acc);
    }

    public static void removerSaco(Saco s){
        mapa[s.getX()][s.getY()] = new Chao(s.getX(), s.getY());
    }
}
