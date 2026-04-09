/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ur_os.memory.freememorymagament;
import java.util.Iterator;
import java.util.LinkedList;
import ur_os.process.Process;

/**
 *
 * @author User
 */
public class QuickFitMemorySlotManager extends FreeMemorySlotManager {

    private static final int PEQUENO = 200000;
    private static final int MEDIANO = 400000;
    private static final int GRANDE  = 600000;

    private LinkedList<MemorySlot> grupoPequeno;
    private LinkedList<MemorySlot> grupoMediano;
    private LinkedList<MemorySlot> grupoGrande;

    public QuickFitMemorySlotManager(int tamanoMemoria) {
        super(tamanoMemoria);
        grupoPequeno = new LinkedList<>();
        grupoMediano = new LinkedList<>();
        grupoGrande  = new LinkedList<>();
    }

    private LinkedList<MemorySlot> obtenerGrupo(int tamano) {
        if (tamano <= PEQUENO) return grupoPequeno;
        if (tamano <= MEDIANO) return grupoMediano;
        if (tamano <= GRANDE)  return grupoGrande;
        return null;
    }

    private void agregarAGrupo(MemorySlot espacio) {
        LinkedList<MemorySlot> grupo = obtenerGrupo(espacio.getSize());
        if (grupo != null && !grupo.contains(espacio)) {
            grupo.add(espacio);
        }
    }

    private void eliminarDeGrupos(MemorySlot espacio) {
        grupoPequeno.remove(espacio);
        grupoMediano.remove(espacio);
        grupoGrande.remove(espacio);
    }

    @Override
    public MemorySlot getSlot(int tamano) {

        LinkedList<MemorySlot> grupo = obtenerGrupo(tamano);
        if (grupo != null && !grupo.isEmpty()) {
            Iterator<MemorySlot> it = grupo.iterator();
            while (it.hasNext()) {
                MemorySlot espacio = it.next();
                if (espacio.canContain(tamano)) {
                    it.remove(); // seguro: no lanza ConcurrentModificationException

                    if (espacio.getSize() == tamano) {
                        list.remove(espacio);
                        return espacio;
                    } else {
                        return espacio.assignMemory(tamano);
                    }
                }
            }
        }

        for (MemorySlot espacio : list) {
            if (espacio.canContain(tamano)) {
                if (espacio.getSize() == tamano) {
                    list.remove(espacio);
                    eliminarDeGrupos(espacio);
                    return espacio;
                } else {
                    MemorySlot espacioAsignado = espacio.assignMemory(tamano);
                    agregarAGrupo(espacio);
                    return espacioAsignado;
                }
            }
        }

        System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
        return null;
    }

    @Override
    public void reclaimMemory(Process p) {
        super.reclaimMemory(p); // devuelve a list y hace fuseSlots
        sincronizarGrupos();   // resincroniza grupos con el estado actual de list
    }

    private void sincronizarGrupos() {
        grupoPequeno.clear();
        grupoMediano.clear();
        grupoGrande.clear();
        for (MemorySlot espacio : list) {
            agregarAGrupo(espacio);
        }
    }


}