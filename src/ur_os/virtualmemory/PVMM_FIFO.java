/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import ur_os.memory.paging.PageTable;
import ur_os.memory.paging.PageTableEntry;

/**
 *
 * @author user
 * 
 * ojo, para que nos acordemos
 * FIFO: La página que llegó PRIMERO a memoria es la víctima.
 */
public class PVMM_FIFO extends ProcessVirtualMemoryManager {

    public PVMM_FIFO() {
        type = ProcessVirtualMemoryManagerType.FIFO;
    }

    @Override
    public int getVictim(LinkedList<Integer> accesosMemoria, PageTable tablaPaginas) {

        System.out.println("\nMirando a ver cuál es la victim...");

        // recolectar qué páginas están actualmente
        LinkedList<Integer> paginasValidas = new LinkedList<>();
        int indice = 0;
        for (PageTableEntry entrada : tablaPaginas.getList()) {
            if (entrada.isValid()) {
                paginasValidas.add(indice);
            }
            indice++;
        }
        System.out.println("Páginas: " + paginasValidas);
        System.out.println("Accesos del más viejito al más nuevo: " + accesosMemoria);

        // La primera página válida que aparezca es la víctima.
        for (int pagina : accesosMemoria) {
            if (paginasValidas.contains(pagina)) {
                System.out.println("Víctima: página " + pagina + " (entró primero)" );
                return pagina;
            }
        }

        // si el historial no coincide con ninguna página válida,
        // se saca simplemente la primera de la lista.
        if (!paginasValidas.isEmpty()) {
            int victima = paginasValidas.getFirst();
            System.out.println("víctima: página " + victima);
            return victima;
        }
        return -1;
    }
}