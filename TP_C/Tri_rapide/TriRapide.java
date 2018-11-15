// -*- coding: utf-8 -*-

import java.util.Arrays;
import java.util.Random ;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TriRapide {
    static final int taille = 1_000_000 ;                   // Longueur du tableau à trier
    static final int [] tableau = new int[taille] ;         // Le tableau d'entiers à trier 
    static final int borne = 10 * taille ;                  // Valeur maximale dans le tableau
    static final int nbThreads = 4;
    static final ExecutorService executeur = Executors.newFixedThreadPool(nbThreads);
    static final CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executeur);

    static class Trieur implements Callable<Boolean> {
        private int[] tableauATrier;
        private int début, fin;
    
        public Trieur(int[] tableau, int début, int fin) {
            tableauATrier = tableau;
            this.début = début;
            this.fin = fin;
        }
    
        public Boolean call() {
            trierRapidementParallele(tableauATrier, début, fin);
            return true;
        }
    }

    private static void echangerElements(int[] t, int m, int n) {
        int temp = t[m] ;
        t[m] = t[n] ;
        t[n] = temp ;
    }

    private static int partitionner(int[] t, int début, int fin) {
        int v = t[fin] ;                               // Choix (arbitraire) du pivot : t[fin]
        int place = début ;                            // Place du pivot, à droite des éléments déplacés
        for (int i = début; i < fin; i++) {            // Parcours du *reste* du tableau
            if (t[i] < v) {                            // Cette valeur t[i] doit être à droite du pivot
                echangerElements(t, i, place) ;        // On le place à sa place
                place++ ;                              // On met à jour la place du pivot
            }
        }
        echangerElements(t, place, fin) ;              // Placement définitif du pivot
        return place ;
    }

    private static void trierRapidement(int[] t, int début, int fin) {
        if (début < fin) {                             // S'il y a un seul élément, il n'y a rien à faire!
            int p = partitionner(t, début, fin) ;      
            trierRapidement(t, début, p-1) ;
            trierRapidement(t, p+1, fin) ;
        }
    }

    private static void trierRapidementParallele(int[] t, int début, int fin) {

        if (début < fin) {                             // S'il y a un seul élément, il n'y a rien à faire!
            int p = partitionner(t, début, fin);      
            
            if(t.length > 1000 && t.length > taille / 100) {
                ecs.submit(new Trieur(t, début, p-1));
                ecs.submit(new Trieur(t, p+1, fin));
            }
            
            else {
                trierRapidement(t, début, p-1);
                trierRapidement(t, p+1, fin);
            }
        }
    }

    private static void afficher(int[] t, int début, int fin) {
        for (int i = début ; i <= début+3 ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.print("...") ;
        for (int i = fin-3 ; i <= fin ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.print("\n") ;
    }

    public static void executionSequentielle(int[] tableau) {
        System.out.println("Démarrage du tri rapide séquentiel.") ;
        long débutDuTri = System.nanoTime();

        trierRapidement(tableau, 0, taille-1);                   // Tri du tableau

        long finDuTri = System.nanoTime();
        long duréeDuTri = (finDuTri - débutDuTri) / 1_000_000 ;

        System.out.println("Exécution séquentielle : Tri obtenu en " + duréeDuTri + " millisecondes.") ;

    }

    public static void executionParallele(int[] tableau) {
        System.out.println("Démarrage du tri rapide parallèle.") ;
        
        long débutDuTri = System.nanoTime();

        trierRapidementParallele(tableau, 0, taille-1);                   // Tri du tableau

        while(!executeur.awaitTermination(1, TimeUnit.SECONDS)) {
            int[] tableauTrié = new int[taille];
            long finDuTri = System.nanoTime();
            long duréeDuTri = (finDuTri - débutDuTri) / 1_000_000 ;
            
            System.out.print("Tableau trié : ") ; 
            afficher(tableau, 0, taille -1) ;                         // Affiche le tableau obtenu
            System.out.println("Exécution parallèle : Obtenu en " + duréeDuTri + " millisecondes.") ;
        }

        executeur.shutdown();                                     // Fermeture de l'exécuteur
    }
    
    public void compareTabs(int[] t1, int[] t2) {
        if (Arrays.compare(t1, t2) != 0) {
            System.out.println("Les tableaux sont différents.");
            return;
        }
        
        System.out.println("Les tableaux sont identiques.");
        return;
    }

    public static void main(String[] args) {
        Random alea = new Random() ;
        for (int i=0 ; i<taille ; i++) {                          // Remplissage aléatoire du tableau
            tableau[i] = alea.nextInt(2*borne) - borne ;            
        }
        System.out.print("Tableau initial : ") ;
        afficher(tableau, 0, taille -1) ;                         // Affiche le tableau à trier

        executionSequentielle(tableau);
        executionParallele(tableau);

        

    }
}


/*
  $ make
  javac *.java
  $ java TriRapide
  Tableau initial :  4967518 -8221265 -951337 4043143... -4807623 -1976577 -2776352 -6800164
  Démarrage du tri rapide.
  Tableau trié :  -9999981 -9999967 -9999957 -9999910... 9999903 9999914 9999947 9999964
  obtenu en 85 millisecondes.
*/
