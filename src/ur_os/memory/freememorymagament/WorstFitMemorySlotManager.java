/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class WorstFitMemorySlotManager extends FreeMemorySlotManager {

    public WorstFitMemorySlotManager(int memSize) {
        super(memSize);
    }

    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot worstFitSlot = null;

        for (int i = 0; i < list.size(); i++) {
            MemorySlot currentSlot = list.get(i);
            if (currentSlot.canContain(size)) {

                if (worstFitSlot == null || currentSlot.getSize() > worstFitSlot.getSize()) {
                    worstFitSlot = currentSlot;
                }
            }
        }

        // Con lista revisada, vamos con el más grande 
        if (worstFitSlot != null) {
            if (worstFitSlot.getSize() == size) {
                list.remove(worstFitSlot);
                return worstFitSlot;
            } else {
                // Se divide y se devuelve lo solicitado
                return worstFitSlot.assignMemory(size);
            }
        }

        // Si no se encontró donde cupiera
        System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
        return null;
    }
}

