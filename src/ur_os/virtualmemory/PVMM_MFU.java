/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import ur_os.memory.paging.PageTable;
import ur_os.memory.paging.PageTableEntry;

/**
 *
 * @author user
 * ojo, para que nos acordemos
 * MFU: La página que se usó MÁS VECES es la víctima.
 */
public class PVMM_MFU extends ProcessVirtualMemoryManager {

    public PVMM_MFU() {
        type = ProcessVirtualMemoryManagerType.MFU;
    }

    @Override
    public int getVictim(LinkedList<Integer> accesosMemoria, PageTable tablaPaginas) {

        System.out.println("\nMirando a ver cuál es la victim...");

        LinkedList<Integer> paginasValidas = new LinkedList<>();
        int indice = 0;
        for (PageTableEntry entrada : tablaPaginas.getList()) {
            if (entrada.isValid()) {
                paginasValidas.add(indice);
            }
            indice++;
        }
        System.out.println("Páginas: " + paginasValidas);

        HashMap<Integer, Integer> frecuencias = new HashMap<>();
        for (int pagina : paginasValidas) {
            frecuencias.put(pagina, 0);
        }
        for (int acceso : accesosMemoria) {
            if (frecuencias.containsKey(acceso)) {
                frecuencias.put(acceso, frecuencias.get(acceso) + 1);
            }
        }

        System.out.println("Frecuencias:");
        for (Map.Entry<Integer, Integer> e : frecuencias.entrySet()) {
            System.out.println("      Página " + e.getKey() + ": " + e.getValue() + " acceso(s)");
        }

        // la de MAYOR frecuencia (opuesto a LFU)
        int victima = -1;
        int frecuenciaMaxima = -1;
        for (Map.Entry<Integer, Integer> e : frecuencias.entrySet()) {
            if (e.getValue() > frecuenciaMaxima) {
                frecuenciaMaxima = e.getValue();
                victima = e.getKey();
            }
        }

        System.out.println("Víctima: página " + victima
                + " (usada " + frecuenciaMaxima + " vez/veces, o sea la mayor)");
        return victima;
    }
}