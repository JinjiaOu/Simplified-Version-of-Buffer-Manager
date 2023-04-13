import java.io.*;
import java.util.Scanner;

public class BufferPool {
    private Frame[] buffers;
    private int removeIndex;
    private final int numRecordsPerBlock;
    private final String filepath = "Project1/F";

    public BufferPool(int numBuffers) {
        this.buffers = new Frame[numBuffers];
        // initialize each new frame
        for (int i = 0; i < numBuffers; i++) {
            buffers[i] = new Frame();
        }
        this.removeIndex = 0;
        this.numRecordsPerBlock = 100;
    }

    // gets a record from a block in the pool if it exists, or from disk
    public void get(int recordID) {
        int blockID = ((recordID - 1) / this.numRecordsPerBlock) + 1;
        int blockFrame = inPool(blockID);

        if (blockFrame == -1) {
            blockFrame = getBlockFromDisk(blockID);
            if (blockFrame != -1) {
                System.out.println("Brought file " + blockID + " from disk. I/O action performed.");
                System.out.println("Placed in Frame " + (blockFrame + 1));
            } else {
                System.out.println("The corresponding block #" + blockID + " not accessible from disk" +
                        "buffers are full");
                return;
            }
        } else {
            System.out.println("File " + blockID + " already in memory. No I/O necessary.");
            System.out.println("Located in Frame " + (blockFrame + 1));
        }

        System.out.println(getRecordFromPool(blockFrame, (recordID % 100) - 1));
    }

    // sets a recordID to a record.
    public void set(int recordID, char[] record) {
        int blockID = ((recordID - 1) / this.numRecordsPerBlock) + 1;
        int blockFrame = inPool(blockID);

        if (blockFrame == -1) {
            blockFrame = getBlockFromDisk(blockID);
            if (blockFrame == -1) {
                System.out.println("The corresponding block #" + blockID + " not accessible from disk " +
                        "buffers are full");
                System.out.println("Write fail");
                return;
            } else {
                System.out.println("Brought File " + blockID + " from disk. I/O action performed.");
                System.out.println("Placed in Frame " + (blockFrame + 1));
            }
        } else {
            System.out.println("File " + blockID + " already in memory. No I/O necessary.");
            System.out.println("Located in Frame " + (blockFrame + 1));
        }

        this.buffers[blockFrame].setRecord((recordID % this.numRecordsPerBlock) - 1, record);
        System.out.println("Write success");
    }


    // pins a block with given blockID in buffer pool
    public void pin(int blockID) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockID() == blockID) {
                // block is already in memory
                System.out.println("File " + blockID + " pinned in Frame " + (i + 1));
                if (buffers[i].isPinned()) {
                    System.out.println("Already pinned");
                } else {
                    // set pin
                    buffers[i].setPinned(true);
                    System.out.println("Frame " + (i + 1) + " was not already pinned");
                }
                return;
            }
        }
        // block is not in memory
        int blockFrame = getBlockFromDisk(blockID);
        if (blockFrame != -1) {
            buffers[blockFrame].setPinned(true);
            System.out.println("File " + blockID + " pinned in Frame " + (blockFrame + 1));
            System.out.println("Frame " + (blockFrame + 1) + " was not already pinned");
        } else {
            System.out.println("The corresponding block #" + blockID + " cannot be pinned " +
                    "buffers are full");
        }
    }

    // unpins block with given blockID in buffer pool
    public void unpin(int blockID){
        int blockFrame = inPool(blockID);
        if(blockFrame != -1){
            System.out.println("File " + blockID + " is unpinned in Frame " + (blockFrame + 1));
            this.buffers[blockFrame].setPinned(false);
        } else {
            // block is not in memory
            System.out.println("The corresponding block " + blockID + " cannot be unpinned because it is not in memory.");
        }
    }

    // get a record from given frame
    private String getRecordFromPool(int frameNum, int recordNum){
        return this.buffers[frameNum].getRecord(recordNum);
    }

    // gets block from disk
    private int getBlockFromDisk(int blockID) {
        int emptyFrame = findEmptyFrame();

        if (emptyFrame == -1) {
            emptyFrame = findRemovableFrame();
        }

        if (emptyFrame != -1 && this.buffers[emptyFrame].isDirty()) {
            writeToDisk(this.buffers[emptyFrame].getContent(), this.buffers[emptyFrame].getBlockID());
        }

        if (emptyFrame != -1) {
            readFromDisk(emptyFrame, blockID);
        }

        return emptyFrame;
    }

    private int inPool(int blockID) {
        for (int i = 0; i < this.buffers.length; i++) {
            if (buffers[i].getBlockID() == blockID) {
                return i;
            }
        }
        return -1;
    }

    private int findEmptyFrame() {
        for (int i = 0; i < this.buffers.length; i++) {
            if (buffers[i].getBlockID() == -1) {
                return i;
            }
        }
        return -1;
    }

    private int findRemovableFrame() {
        for (int i = this.removeIndex; i < this.buffers.length + this.removeIndex; i++) {
            int index = i % this.buffers.length;
            if (!this.buffers[index].isPinned()) {
                this.removeIndex = index + 1;
                System.out.println("Evicted File " + this.buffers[index].getBlockID() + " from Frame " + (index + 1));
                return index;
            }
        }
        return -1;
    }

    private void writeToDisk(char[] content, int blockID) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Project1/F" + blockID + ".txt"));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromDisk(int targetFrame, int blockID) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("Project1/F" + blockID + ".txt")).useDelimiter("\\Z");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder content = new StringBuilder(4000);
        while (scanner.hasNext()) {
            content.append(scanner.next());
        }
        scanner.close();

        this.buffers[targetFrame].setContent(content.toString().toCharArray());
        this.buffers[targetFrame].setBlockID(blockID);
        this.buffers[targetFrame].setDirty(false);
    }


}