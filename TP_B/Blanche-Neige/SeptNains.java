// -*- coding: utf-8 -*-
import java.lang.String;
import java.util.ArrayList;

import javax.print.event.PrintJobListener;

public class SeptNains {
    public static void main(String[] args) {
        final int nbNains = 7;
        final String nom [] = { "Simplet", "Dormeur",  "Atchoum", 
                                "Joyeux", "Grincheux", "Prof", "Timide"};
        Nain nain [] = new Nain [nbNains];
        for(int i = 0; i < nbNains; i++) nain[i] = new Nain(nom[i]);
        for(int i = 0; i < nbNains; i++) nain[i].start();

        // Interrompre les nains au bout de 5 secondes
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
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
    private volatile boolean libre = true; // Initialement, Blanche-Neige est libre.
    private volatile ArrayList<String> fileDAttente = new ArrayList<>();

    public synchronized void requerir() {
        System.out.println("\t" + Thread.currentThread().getName()
                           + " veut un accès exclusif à la ressource");
        fileDAttente.add(Thread.currentThread().getName());
        
        // System.out.print("Contenu de la file d'attente : - ");
        // for (String nain : fileDAttente) {
        //     System.out.print(nain + "-");
        // }
        // System.out.println();
    }

    public synchronized boolean acceder() {
        String threadName = Thread.currentThread().getName();
        
        while(!libre || threadName.compareTo(fileDAttente.get(0)) != 0) {
            try { wait(); } // Le nain s'endort sur l'objet bn
            catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
                return false; 
            }
        }

        libre = false;
        System.out.println("\t\t" + threadName
                           + " accède à la ressource.");
        return true;
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
            if (bn.acceder() == true) {
                System.out.println("\t\t" + getName() 
                        + " a un accès exclusif à Blanche-Neige.");
                try { sleep(1000); }
                catch (InterruptedException e) { this.interrupt(); }
                finally { bn.relacher(); }
            }
        }
        System.out.println(getName() + " a terminé!");
    }	
}