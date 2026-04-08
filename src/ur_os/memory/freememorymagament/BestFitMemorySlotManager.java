/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class BestFitMemorySlotManager extends FreeMemorySlotManager {

    public BestFitMemorySlotManager(int memSize) {
        super(memSize);
    }

    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot bestFitSlot = null;

        // se recorre y se valida si se ha encontrado uno "perfecto"
        for (int i = 0; i < list.size() && (bestFitSlot == null || bestFitSlot.getSize() != size); i++) {
            MemorySlot currentSlot = list.get(i);

            if (currentSlot.canContain(size)) {
                if (bestFitSlot == null || currentSlot.getSize() < bestFitSlot.getSize()) {
                    bestFitSlot = currentSlot;
                }
            }
        }

        if (bestFitSlot != null) {
            if (bestFitSlot.getSize() == size) {
                list.remove(bestFitSlot);
                return bestFitSlot;
            } else {
                //  assignMemory actualiza el slot original y devuelve el nuevo
                return bestFitSlot.assignMemory(size);
            }
        }

        System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
        return null;
    }
}
