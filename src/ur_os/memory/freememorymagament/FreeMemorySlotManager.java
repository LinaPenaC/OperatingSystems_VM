/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

import ur_os.memory.segmentation.SegmentTableEntry;
import ur_os.memory.segmentation.PMM_Segmentation;
import ur_os.memory.contiguous.SMM_Contiguous;
import java.util.ArrayList;
import java.util.LinkedList;
import ur_os.process.Process;
import ur_os.memory.ProcessMemoryManager;
import static ur_os.memory.MemoryManagerType.CONTIGUOUS;
import static ur_os.memory.MemoryManagerType.SEGMENTATION;
import ur_os.memory.contiguous.PMM_Contiguous;

/**
 *
 * @author super
 */
public abstract class FreeMemorySlotManager extends FreeMemoryManager{
    
    LinkedList<MemorySlot> list;
    
    public FreeMemorySlotManager(){
        this(ur_os.system.SystemOS.MEMORY_SIZE);
    }
    
    public FreeMemorySlotManager(int memSize){
        list = new LinkedList();
        list.add(new MemorySlot(0,memSize));
    }
    
    public abstract MemorySlot getSlot(int size);
    
    public void fuseSlots(){
        int tam = list.size();
        for (int i = 0; i < tam-1; i++) {
            if(list.get(i).getEnd()+1 == list.get(i+1).getBase()){
                list.get(i).addSlot(list.get(i+1));
                list.remove(list.get(i+1));
                tam--;
                i--;
            }
        }
        for (int i = 0; i < tam; i++) {
            if(list.get(i).getSize() == 0){
                list.remove(i);
                tam--;
                i--;
            }
        }
        
    }
    
    private void returnMemorySlot(MemorySlot m){
        
        
        int i = 0;
        //Find the slot with a higher base address than the one inserted
        while(i<list.size() && list.get(i).getBase() < m.getBase()){
            i++;
        }
        
        if(i > 0){
            i--;
        }
        
        if(i == 0 && list.get(i).getBase() > m.getBase()){//If the slot is the highest one
            list.addFirst(m);
        }else if (i == list.size()-1){//If the slot is the first
            list.getLast().addSlot(m);
        }else{
            list.add(i+1, m);
        }
        
        fuseSlots();
        
    }

    @Override
    public void reclaimMemory(Process p){
        ProcessMemoryManager pmm = p.getPMM();
        switch (pmm.getType()) {
            case SEGMENTATION:
                PMM_Segmentation pmms = (PMM_Segmentation)p.getPMM();
                ArrayList<SegmentTableEntry> list = pmms.getSt().getTable();
                for (SegmentTableEntry ste : list) {
                    this.returnMemorySlot(ste.getMemorySlot());
                }
                
                break;
            default:
            case CONTIGUOUS:
                PMM_Contiguous pmmc = (PMM_Contiguous)p.getPMM();
                this.returnMemorySlot(pmmc.getMemorySlot());
                pmmc.setValid(false);
                break;
        }
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (MemorySlot memorySlot : list) {
            sb.append(memorySlot.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getSize() {
        return this.list.size();
    }
    
    //CÁLCULO DE FRAGMENTACION: CON LOS INDICADORES QUE NOS DIJO EL PROFE
    // Indicador 1: porcentaje de memoria que no se esta usando en este momento
    public double calcularPorcentajeNoUtilizado(int tamanoTotalMemoria) {
        double memoriaLibre = obtenerMemoriaLibreTotal();
        return (memoriaLibre / tamanoTotalMemoria) * 100;
    }

    // Indicador 2: tamano promedio de los slots libres
    public double calcularTamanoPromedioSlots() {
        if (list.size() == 0) return 0; // evitamos dividir entre cero
        return (double) obtenerMemoriaLibreTotal() / list.size();
    }

    // Indicador 3: fragmentacion externa como porcentaje
    // Mide cuanta memoria libre esta atrapada en huecos pequenos
    // Formula estandar: (1 - slotMasGrande / memoriaLibreTotal) * 100
    public double calcularFragmentacionExterna() {
        int memoriaLibre = obtenerMemoriaLibreTotal();
        if (memoriaLibre == 0) return 0.0;
        if (list.size() <= 1) return 0.0; // con un solo hueco no hay fragmentacion

        int slotMasGrande = 0;
        for (MemorySlot espacio : list) {
            if (espacio.getSize() > slotMasGrande) {
                slotMasGrande = espacio.getSize();
            }
        }

        return (1.0 - ((double) slotMasGrande / memoriaLibre)) * 100;
    }

    public int obtenerMemoriaLibreTotal() {
        int total = 0;
        for (MemorySlot espacio : list) {
            total += espacio.getSize();
        }
        return total;
    }
}
