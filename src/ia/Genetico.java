package ia;

import java.util.Arrays;
import java.util.Random;

public class Genetico {
    private static final int tamPop = 7;
    private boolean[] mascara = new boolean[]{true, true, true, true, false, false, false, false, false, false};
    private int[] valores;
    private Membro[] populacao;
    private Random rand;

    public double calcularAptidao(int[] conteudo, int[] valores){
        Membro res = new Membro(conteudo, valores);
        return res.aptidao;
    }

    private class Membro{
        private int[] conteudo, valores;
        private double aptidao;

        public Membro(int[] conteudo, int[] valores){
            this.valores = valores;
            this.conteudo = conteudo;
            calcularAptidao();
        }

        private void calcularAptidao(){
            int bau0 = 0, bau1 = 0, bau2 = 0, bau3 = 0, total = 0;
            for(int i = 0; i < conteudo.length; i++){
                total += valores[i];
                switch(conteudo[i]){
                    case 0:
                        bau0 += valores[i];
                        break;
                    case 1:
                        bau1 += valores[i];
                        break;
                    case 2:
                        bau2 += valores[i];
                        break;
                    case 3:
                        bau3 += valores[i];
                        break;
                }
            }
            int porBau = total/4;
            bau0 = Math.abs(porBau-bau0);
            bau1 = Math.abs(porBau-bau1);
            bau2 = Math.abs(porBau-bau2);
            bau3 = Math.abs(porBau-bau3);

            aptidao = (bau0 + bau1 + bau2 + bau3)/4.0;
        }
    }

    public Genetico(int[] valores, Random rand){
        this.valores = valores;
        this.rand = rand;
        gerarPopulacao();
    }

    public int[] realizar(){
        System.out.println("realizar");
        printPopulacao();
        System.out.println("---------------------");
        int count = 500, chance = 70;
        do{
            cruzamento();
            //elitizar();
            if(rand.nextInt(100) < chance) {
                mutacao();
                elitizar();
            }
            //printPopulacao();
            count--;
        }while(populacao[0].aptidao > 0 && count > 0);

        if(populacao[0].aptidao == 0){
            System.out.println(500-count);
            return populacao[0].conteudo;
        }
        return null;
    }

    public void cruzamento(){
        System.out.println("Cruzamento");
        System.out.println("Antes");
        printPopulacao();

        int r1 = rand.nextInt(populacao.length), r2 = rand.nextInt(populacao.length);
//        while(r2 == r1){ r2 = rand.nextInt(populacao.length); }
        Membro pai1 = torneio(populacao[r1], populacao[r2]);

        r1 = rand.nextInt(populacao.length);
        r2 = rand.nextInt(populacao.length);
//        while(r2 == r1){ r2 = rand.nextInt(populacao.length); }
        Membro mae1 = torneio(populacao[r1], populacao[r2]);

        r1 = rand.nextInt(populacao.length);
        r2 = rand.nextInt(populacao.length);
//        while(r2 == r1){ r2 = rand.nextInt(populacao.length); }
        Membro pai2 = torneio(populacao[r1], populacao[r2]);

        r1 = rand.nextInt(populacao.length);
        r2 = rand.nextInt(populacao.length);
//        while(r2 == r1){ r2 = rand.nextInt(populacao.length); }
        Membro mae2 = torneio(populacao[r1], populacao[r2]);

        r1 = rand.nextInt(populacao.length);
        r2 = rand.nextInt(populacao.length);
//        while(r2 == r1){ r2 = rand.nextInt(populacao.length); }
        Membro pai3 = torneio(populacao[r1], populacao[r2]);

        r1 = rand.nextInt(populacao.length);
        r2 = rand.nextInt(populacao.length);
//        while(r2 == r1){ r2 = rand.nextInt(populacao.length); }
        Membro mae3 = torneio(populacao[r1], populacao[r2]);

        int[] filho1 = new int[10], filho2 = new int[10], filho3 = new int[10];
        int[] filho4 = new int[10], filho5 = new int[10], filho6 = new int[10];
        for(int i = 0; i < 10; i++){
            filho1[i] = mascara[i]? pai1.conteudo[i] : mae1.conteudo[i];
            filho2[i] = mascara[i]? mae1.conteudo[i] : pai1.conteudo[i];
            filho3[i] = mascara[i]? pai2.conteudo[i] : mae2.conteudo[i];
            filho4[i] = mascara[i]? mae2.conteudo[i] : pai2.conteudo[i];
            filho5[i] = mascara[i]? pai3.conteudo[i] : mae3.conteudo[i];
            filho6[i] = mascara[i]? mae3.conteudo[i] : pai3.conteudo[i];
        }

        //PRINTS
        System.out.println("Máscara: "+Arrays.toString(mascara));
        System.out.println("Pai e Mãe 1:");
        System.out.println(Arrays.toString(pai1.conteudo));
        System.out.println(Arrays.toString(mae1.conteudo));
        System.out.println();
        System.out.println(Arrays.toString(filho1));
        System.out.println(Arrays.toString(filho2));
        System.out.println();
        System.out.println("Pai e Mãe 2:");
        System.out.println(Arrays.toString(pai2.conteudo));
        System.out.println(Arrays.toString(mae2.conteudo));
        System.out.println();
        System.out.println(Arrays.toString(filho3));
        System.out.println(Arrays.toString(filho4));
        System.out.println();
        System.out.println("Pai e Mãe 3:");
        System.out.println(Arrays.toString(pai3.conteudo));
        System.out.println(Arrays.toString(mae3.conteudo));
        System.out.println();
        System.out.println(Arrays.toString(filho5));
        System.out.println(Arrays.toString(filho6));

        populacao[1] = new Membro(filho1, valores);
        populacao[2] = new Membro(filho2, valores);
        populacao[3] = new Membro(filho3, valores);
        populacao[4] = new Membro(filho4, valores);
        populacao[5] = new Membro(filho5, valores);
        populacao[6] = new Membro(filho6, valores);

        System.out.println("Depois");
        printPopulacao();
        System.out.println("-------------------------");
    }

    public void mutacao(){
        System.out.println("Mutação");
        System.out.println("Antes");
        printPopulacao();
        int lugar = rand.nextInt((populacao.length-1))+1, pos;
        populacao[lugar].conteudo[pos = rand.nextInt(10)] = (populacao[lugar].conteudo[pos] + 1) % 4;
        populacao[lugar].conteudo[pos = rand.nextInt(10)] = (populacao[lugar].conteudo[pos] + 1) % 4;
        populacao[lugar].conteudo[pos = rand.nextInt(10)] = (populacao[lugar].conteudo[pos] + 1) % 4;
        populacao[lugar].calcularAptidao();
        System.out.println("Depois");
        printPopulacao();
        System.out.println("-------------------------");
    }

    public void gerarPopulacao(){
        populacao = new Membro[tamPop];
        for(int i = 0; i < tamPop; i++){
            int[] carga = new int[10];
            for(int j = 0; j < 10; j++){
                carga[j] = rand.nextInt(4);
            }
            populacao[i] = new Membro(carga, valores);
        }
        elitizar();
    }

    public void elitizar(){
        int melhor = 0;
        double valor = Double.MAX_VALUE;

        for(int i = 0; i < populacao.length; i++){
            if(populacao[i].aptidao < valor){
                valor = populacao[i].aptidao;
                melhor = i;
            }
        }

        Membro temp = populacao[0];
        populacao[0] = populacao[melhor];
        populacao[melhor] = temp;

        System.out.println("Elitizar");
        printPopulacao();
        System.out.println("-------------------------");
    }

    public void printPopulacao() {
        System.out.println("População:");
        for(int i = 0; i < populacao.length; i++){
            for(int j = 0; j < populacao[i].conteudo.length; j++){
                System.out.print(populacao[i].conteudo[j]+" ");
            }
            System.out.println("|Aptidão: "+populacao[i].aptidao);
        }
    }

    public Membro torneio(Membro a, Membro b){
//        System.out.println("torneio");
//        System.out.println(a.aptidao > b.aptidao? a : b);
//        System.out.println("-------------------------");
        return a.aptidao < b.aptidao? a : b;
    }
}
