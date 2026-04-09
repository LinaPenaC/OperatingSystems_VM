/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author User
 */
// Es como First Fit pero en vez de empezar siempre desde el inicio,
// mira en donde quedo la ultima vez y sigue buscando desde ahi
public class NextFitMemorySlotManager extends FreeMemorySlotManager {

    private int ultimoIndice;
    public NextFitMemorySlotManager(int tamanoMemoria) {
        super(tamanoMemoria);
        ultimoIndice = 0; 
    }
    
    @Override
    public MemorySlot getSlot(int tamano) {
        if (list.isEmpty()) {
            System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
            return null;
        }
        int total = list.size();
        for (int i=0; i<total; i++) {
            int indice=(ultimoIndice+i)%total;
            MemorySlot espacioActual = list.get(indice);

            if (espacioActual.canContain(tamano)) {
                ultimoIndice = indice; 
                if (espacioActual.getSize() == tamano) {
                    list.remove(indice);
                    if (ultimoIndice >= list.size()) ultimoIndice = 0;
                    return espacioActual;
                } else {
                    return espacioActual.assignMemory(tamano);
                }
            }
        }
        System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
        return null;
    }
}
