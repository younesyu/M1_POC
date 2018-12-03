// -*- coding: utf-8 -*-

import java.util.ArrayList;
import java.util.Date;
import java.text.*;

public class SeptNains {
    public static void main(String[] args) {
        int nbNains = 7;
        String nom [] = {"Simplet", "Dormeur",  "Atchoum", "Joyeux", "Grincheux", "Prof", "Timide"};
        Nain nain [] = new Nain [nbNains];
        for(int i = 0; i < nbNains; i++) nain[i] = new Nain(nom[i]);
        for(int i = 0; i < nbNains; i++) nain[i].start();

        /* Attendre 5s. avant d'interrompre chaque nain */
        try { Thread.sleep(5_000); } catch (InterruptedException e) {e.printStackTrace();}	

        /* Interrompre chaque nain, un à un */
        for(int i = 0; i < nbNains; i++) {
            nain[i].interrupt();
        }

        
        /* Attendre la terminaison de chaque nain, l'un après l'autre */
        for(int i = 0; i < nbNains; i++){
            try { nain[i].join(); } catch (InterruptedException e) {e.printStackTrace();}	
        }

        /* Afficher le message final */
        System.out.println("Tous les nains ont terminé.");        
    }
}    

class BlancheNeige {
    private final boolean verbeux = true;  // Pour afficher éventuellement le contenu de la liste
    public ArrayList<Thread> liste = new ArrayList<Thread>();

    public synchronized void requerir() {   // C'est simplement s'inscrire à la fin de la liste
        liste.add(Thread.currentThread());
        System.out.println("\t\t" + Thread.currentThread().getName()
                           + " requiert un accès exclusif à la ressource");
        if (verbeux) System.out.println("\t\t\t\t" + liste.toString());
    }

    public synchronized void acceder() throws InterruptedException {
        // Le droit d'accéder à la ressource correspond  au fait d'être le premier dans la liste
        while( liste.get(0) != Thread.currentThread()) {
            // Le nain s'endort sur le moniteur Blanche-Neige

            long startTime = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = new Date(startTime);
            
            System.out.println("[" + sdf.format(date) + "] " + Thread.currentThread().getName() + ": \"Et alors?\"");
            
            wait(1_000);

            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - startTime;

            while (timeDifference < 1000) {
                timeDifference = currentTime - startTime;
                wait(1_000 - timeDifference);
            }
        }	
        System.out.println("\t\t" + Thread.currentThread().getName()
                           + " obtient le privilège d'accès exclusif à la ressource.");
        if (verbeux) System.out.println("\t\t\t\t" + liste.toString());
    }

    public synchronized void relacher(){
        notifyAll();
        liste.remove(0);
        // Le nain s'efface de la liste: il cède ainsi son privilège au suivant.
        System.out.println("\t\t" + Thread.currentThread().getName()
                           + " relâche son privilège.");
        if (verbeux) System.out.println("\t\t\t\t" + liste.toString());
    }
}

class Nain extends Thread {
    public static BlancheNeige bn = new BlancheNeige();

    public Nain(String nom) {
        this.setName(nom);
    }

    public void run() {
        while(true) {
            try {
                bn.requerir();
                bn.acceder();
                System.out.println(getName() + " accède à Blanche-Neige.");
                sleep(2_000);
                System.out.println(getName() + " quitte Blanche-Neige.");
                bn.relacher();
            } catch (InterruptedException e) { break ;}	
        }
        System.out.println(getName() + " s'en va!");        
    }
    
    public String toString(){
        // Permet un affichage simple de la liste d'attente lors de l'appel à liste.toString()
        return getName();
    }
}

/*
  $ make
  javac *.java
  $ java SeptNains
  ........Simplet requiert un accès exclusif à la ressource
  ........Simplet obtient le privilège d'accès exclusif à la ressource.
  Simplet accède à Blanche-Neige.
  ........Timide requiert un accès exclusif à la ressource
  ........Prof requiert un accès exclusif à la ressource
  ........Grincheux requiert un accès exclusif à la ressource
  ........Joyeux requiert un accès exclusif à la ressource
  ........Atchoum requiert un accès exclusif à la ressource
  ........Dormeur requiert un accès exclusif à la ressource
  Simplet quitte Blanche-Neige.
  ........Simplet relâche son privilège.
  ........Simplet requiert un accès exclusif à la ressource
  ........Timide obtient le privilège d'accès exclusif à la ressource.
  Timide accède à Blanche-Neige.
  Timide quitte Blanche-Neige.
  ........Timide relâche son privilège.
  ........Timide requiert un accès exclusif à la ressource
  ........Prof obtient le privilège d'accès exclusif à la ressource.
  Prof accède à Blanche-Neige.
  Prof quitte Blanche-Neige.
  ........Prof relâche son privilège.
  ........Prof requiert un accès exclusif à la ressource
  ........Grincheux obtient le privilège d'accès exclusif à la ressource.
  Grincheux accède à Blanche-Neige.
  Grincheux quitte Blanche-Neige.
  ........Grincheux relâche son privilège.
  ........Grincheux requiert un accès exclusif à la ressource
  ........Joyeux obtient le privilège d'accès exclusif à la ressource.
  Joyeux accède à Blanche-Neige.
  Simplet s'en va!
  Prof s'en va!
  Dormeur s'en va!
  Joyeux s'en va!
  Timide s'en va!
  Atchoum s'en va!
  Grincheux s'en va!
  Tous les nains ont terminé.
  $
*/
