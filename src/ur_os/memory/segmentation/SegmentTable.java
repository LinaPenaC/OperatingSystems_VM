/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.segmentation;

import java.util.ArrayList;
import ur_os.system.SystemOS;
import java.util.Random;
import ur_os.memory.MemoryAddress;

/**
 *
 * @author super
 */
public class SegmentTable {
    
    ArrayList<SegmentTableEntry> segmentTable;
    public static final int SAMPLE_PROGRAM_SIZE = 100;
    public static final int SAMPLE_SEGMENT_NUMBER = 5;
    int programSize; //Size of the program in bytes
    int segmentNumber; //Size of the program in bytes
    Random r;
    
    public SegmentTable(){
        this(SAMPLE_PROGRAM_SIZE, SAMPLE_SEGMENT_NUMBER);
    }
    
    public SegmentTable(int programSize){
        this(programSize, SAMPLE_SEGMENT_NUMBER);
    }
       
    public SegmentTable(int programSize, int segmentNumber){
        this(programSize, segmentNumber, true);
    }
    
    public SegmentTable(int programSize, int segmentNumber, boolean auto){
        this.programSize = programSize;
        this.segmentNumber = segmentNumber;
        segmentTable = new ArrayList(segmentNumber); 
        //r = new Random(SystemOS.SEED_SEGMENTS);
        r = new Random();
        if(auto)
            createSegments();
    }
    
    public void createSegments(){
        int[] vals = new int[segmentNumber];
        float total = 0;
        float total2 = 0;
        int base = 0;
        //Generate random numbers from 1 to 99
        for (int i = 0; i < segmentNumber; i++) {
            do{
                vals[i] = r.nextInt(100);
            }while(vals[i] == 0);
            total += vals[i];
        }
        
        for (int i = 0; i < segmentNumber; i++) {
            //The segment size is the percentage of the random value agains the total sum times de program size
            vals[i] = java.lang.Math.round((vals[i]/total)*this.programSize);
            total2 += vals[i];
        }
        
        //Any difference produced by the rounding will be addedd to the final segment
        vals[segmentNumber-1] += this.programSize - total2;
        
        //All bases are 0 because they will be set when the memory slot is assigned to the segment
        for (int i = 0; i < segmentNumber; i++) {
            this.addSegment(base, vals[i]);
        }
        
    }
    
    public SegmentTable(SegmentTable pt){
        this(pt.getProgramSize(), pt.getSize());
        segmentTable = new ArrayList(pt.getTable());
    }
    
    public ArrayList<SegmentTableEntry> getTable(){
        return segmentTable;
    }
    
    public MemoryAddress getSegmentMemoryAddressFromLocalAddress(int locAdd, boolean store){
        int segment = -1;
        int offset = -1;
        
        int accumulated = 0;
        for(int i = 0; i < segmentTable.size(); i++){
            int limit = segmentTable.get(i).getLimit();
            if(locAdd < accumulated + limit){
                segment = i;
                offset  = locAdd - accumulated;
                break;
            }
            accumulated += limit;
        }
 
        if(segment == -1){
            // Dirección fuera del espacio del proceso
            System.out.println("Error - Local address " + locAdd + " out of process bounds");
            return new MemoryAddress(-1, -1);
        }
        
        //For Virtual Memory: marcar el segmento como modificado si es STORE
        if(store){
            this.segmentTable.get(segment).setDirty();
        }
              
        System.out.println("Accessing Segment "+segment+" and offset "+offset);
        return new MemoryAddress(segment, offset);
    }
    
    //Convierte un locgiAddres en direción física con la tabla de segemntos
    public MemoryAddress getPhysicalMemoryAddressFromLogicalMemoryAddress(MemoryAddress m){
        
        int seg = m.getDivision();
        int offset = m.getOffset();
 
        if(seg < 0 || seg >= segmentTable.size()){
            System.out.println("Error - Invalid segment number: " + seg);
            return new MemoryAddress(-1, -1);
        }
 
        SegmentTableEntry entry = segmentTable.get(seg);
 
        if(!entry.isValid()){
            System.out.println("Segment fault - Segment " + seg + " not loaded in memory");
            return new MemoryAddress(-1, -1);
        }
 
        // Ver que offset no supere el límite del segmento
        if(offset >= entry.getLimit() || offset < 0){
            System.out.println("Segment fault - Offset " + offset +
                               " out of bounds for segment " + seg +
                               " (limit=" + entry.getLimit() + ")");
            return new MemoryAddress(-1, -1);
        }
 
        int physicalAddress = entry.getBase() + offset;
 
        // division = seg 
        // getAddress() = seg + physicalAddress = dirección física
        return new MemoryAddress(seg, physicalAddress);
    }
    
    public SegmentTableEntry getSegment(int i){
        return segmentTable.get(i);
    }
    
    public void addSegment(int base, int limit){
        segmentTable.add(new SegmentTableEntry(base, limit));
    }
    
    public void setFrameID(int segment, int base, int limit){
        if(segment == segmentTable.size()){
            segmentTable.add(new SegmentTableEntry(base, limit)); //If it is a new segment
        }else if(segment < segmentTable.size()){
            segmentTable.get(segment).setSegment(base, limit); //Update base and limit for an existing segment
        }else{
            System.out.println("Error - Including erroneous segment number");
        }
        
    }
    
    public ArrayList<Integer> getValidList(){
        ArrayList<Integer> validSegments= new ArrayList();
        int i=0;
        for(SegmentTableEntry p: segmentTable){
            if(p.isValid()){
                validSegments.add(i);
            }
            i++;
        }
        return validSegments;
    }

    public int getSize() {
        return segmentNumber;
    }

    public int getProgramSize() {
        return programSize;
    }
    
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (SegmentTableEntry segmentTableEntry : segmentTable) {
            sb.append("Segment: ");
            sb.append(count++);
            sb.append(" ");
            sb.append(segmentTableEntry.toString());
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
