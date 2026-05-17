/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import java.util.ArrayList;
import ur_os.memory.paging.PageTable;
import ur_os.memory.paging.PageTableEntry;
import ur_os.process.Instruction;
import ur_os.process.ProcessInstructionList;
import ur_os.memory.MemoryInstruction;
import ur_os.system.OS;

/**
 *
 * @author User
 * El BELADY: Saca la página que NO se va a usar por MÁS TIEMPO en el futuro.
 */
public class PVMM_OPTIMAL extends ProcessVirtualMemoryManager {


    private ArrayList<Integer> todasLasPaginas;

    public PVMM_OPTIMAL(ProcessInstructionList pil) {
        type = ProcessVirtualMemoryManagerType.OPTIMAL;

        todasLasPaginas = new ArrayList<>();
        for (Instruction inst : pil.getFutureInstructions()) {
            if (inst instanceof MemoryInstruction) {
                MemoryInstruction mi = (MemoryInstruction) inst;
                todasLasPaginas.add(mi.getLogicalAddress() / OS.PAGE_SIZE);
            }
        }
    }

    @Override
    public int getVictim(LinkedList<Integer> accesosMemoria, PageTable tablaPaginas) {

        System.out.println("\\nMirando a ver cuál es la victim...");

        LinkedList<Integer> paginasValidas = new LinkedList<>();
        int indice = 0;
        for (PageTableEntry entrada : tablaPaginas.getList()) {
            if (entrada.isValid()) {
                paginasValidas.add(indice);
            }
            indice++;
        }
        System.out.println("Páginas: " + paginasValidas);

        int yaEjecutadas = accesosMemoria.size();

        int victima = -1;
        int mayorDistancia = -1;

        System.out.println("uso futuro de cada página:");
        for (int pagina : paginasValidas) {
            int distancia = Integer.MAX_VALUE;
            for (int i = yaEjecutadas; i < todasLasPaginas.size(); i++) {
                if (todasLasPaginas.get(i) == pagina) {
                    distancia = i - yaEjecutadas;
                    break;
                }
            }

            if (distancia == Integer.MAX_VALUE) {
                System.out.println("       Página " + pagina + ": Nunca se usará de nuevo (víctima)");
            } else {
                System.out.println("       Página " + pagina + ": próximo uso en " + distancia + " instrucciones");
            }

            if (distancia > mayorDistancia) {
                mayorDistancia = distancia;
                victima = pagina;
            }
        }

        if (mayorDistancia == Integer.MAX_VALUE) {
            System.out.println("Víctima: página " + victima + " (nunca se volverá a usar)");
        } else {
            System.out.println("Víctima: página " + victima + " (la que más tarda en usarse de nuevo)");
        }

        return victima;
    }

    private int buscarProximoUso(int numeroPagina, ArrayList<Instruction> instruccionesFuturas) {
        int posicion = 0;
        for (Instruction instruccion : instruccionesFuturas) {
            if (instruccion instanceof MemoryInstruction) {
                MemoryInstruction mi = (MemoryInstruction) instruccion;
                int paginaDeLaInstruccion = mi.getLogicalAddress() / OS.PAGE_SIZE;
                if (paginaDeLaInstruccion == numeroPagina) {
                    return posicion;
                }
            }
            posicion++;
        }
        return Integer.MAX_VALUE;
    }
}