// -*- coding: utf-8 -*-
import java.lang.String;
import java.util.ArrayList;

import javax.print.event.PrintJobListener;

public class SeptNains {
    public static void main(String[] args) {
        int nbNains = 7;
        String nom [] = {"Simplet", "Dormeur",  "Atchoum", "Joyeux", "Grincheux", "Prof", "Timide"};
        Nain nain [] = new Nain [nbNains];
        for(int i = 0; i < nbNains; i++) nain[i] = new Nain(nom[i]);
        for(int i = 0; i < nbNains; i++) nain[i].start();

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        for(int i = 0; i < nbNains; i++) {	
            nain[i].interrupt();
        }
        for(int i = 0; i < nbNains; i++) {
            try { nain[i].join(); } catch (InterruptedException e) {}
        }
        System.out.println("C'est fini.");        
    }
}    

class BlancheNeige {
    private volatile boolean libre = true;        // Initialement, Blanche-Neige est libre.
    private volatile ArrayList<String> fileDAttente = new ArrayList<>();

    public synchronized void requerir() {
        System.out.println("\t" + Thread.currentThread().getName()
                           + " veut un accès exclusif à la ressource");
        fileDAttente.add(Thread.currentThread().getName());
    }

    public synchronized void acceder() {
        while(Thread.currentThread().getName().compareTo(fileDAttente.get(0)) != 0 || !libre) { // Le nain s'endort sur l'objet bn
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        libre = false;
        System.out.println("\t\t" + Thread.currentThread().getName()
                           + " accède à la ressource.");
    }

    public synchronized void relacher() {
        System.out.println("\t\t\t" + Thread.currentThread().getName()
                           + " relâche la ressource.");
        libre = true;
        fileDAttente.remove(0);
        notifyAll();
    }
}

class Nain extends Thread {
    private static final BlancheNeige bn = new BlancheNeige();
    public Nain(String nom) {
        this.setName(nom);
    }
    public void run() {
        while(!this.isInterrupted()) {
            bn.requerir();
            bn.acceder();
            System.out.println("\t\t" + getName() + " a un accès exclusif à Blanche-Neige.");
            try {sleep(1000);} catch (InterruptedException e) { this.interrupt(); }
            bn.relacher();
        }
        System.out.println(getName() + " a terminé!");
    }	
}

/*
  $ make
  $ java SeptNains
  Simplet veut un accès exclusif à la ressource
  Grincheux veut un accès exclusif à la ressource
      Grincheux accède à la ressource.
  Prof veut un accès exclusif à la ressource
          Grincheux a un accès exclusif à Blanche-Neige.
  Joyeux veut un accès exclusif à la ressource
  Atchoum veut un accès exclusif à la ressource
  Dormeur veut un accès exclusif à la ressource
  Timide veut un accès exclusif à la ressource
               Grincheux relâche la ressource.
  Grincheux veut un accès exclusif à la ressource
       Grincheux accède à la ressource.
           Grincheux a un accès exclusif à Blanche-Neige.
		       Grincheux relâche la ressource.
  Grincheux veut un accès exclusif à la ressource
      Grincheux accède à la ressource.
          Grincheux a un accès exclusif à Blanche-Neige.
              Grincheux relâche la ressource.
  Grincheux veut un accès exclusif à la ressource
       Grincheux accède à la ressource.
           Grincheux a un accès exclusif à Blanche-Neige.
		       Grincheux relâche la ressource.
  Grincheux veut un accès exclusif à la ressource
      Grincheux accède à la ressource.
          Grincheux a un accès exclusif à Blanche-Neige.
              Grincheux relâche la ressource.
  Grincheux veut un accès exclusif à la ressource
       Grincheux accède à la ressource.
           Grincheux a un accès exclusif à Blanche-Neige.
		       Grincheux relâche la ressource.
  Grincheux veut un accès exclusif à la ressource
      Grincheux accède à la ressource.
          Grincheux a un accès exclusif à Blanche-Neige.
              Grincheux relâche la ressource.
*/
