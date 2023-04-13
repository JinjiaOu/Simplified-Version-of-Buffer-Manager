public class Frame {
    private static final int CONTENT_SIZE = 4000;
    private static final int RECORD_SIZE = 40;

    private char[] content = new char[CONTENT_SIZE];
    private boolean dirty;
    private boolean pinned;
    private int blockID;

    public Frame() {
        this.dirty = false;
        this.pinned = false;
        this.blockID = -1;
    }

    public String getRecord(int recordNum) {
        StringBuilder record = new StringBuilder(RECORD_SIZE);
        int startIndex = recordNum * RECORD_SIZE;

        for (int i = 0; i < RECORD_SIZE; i++) {
            record.append(content[startIndex + i]);
        }

        return record.toString();
    }

    public void setRecord(int recordNum, char[] record) {
        int startIndex = recordNum * RECORD_SIZE;

        for (int i = 0; i < RECORD_SIZE; i++) {
            content[startIndex + i] = record[i];
        }

        this.dirty = true;
    }

    // Getters
    public boolean isDirty() {
        return dirty;
    }

    public boolean isPinned() {
        return pinned;
    }

    public int getBlockID() {
        return blockID;
    }

    public char[] getContent() {
        return content;
    }

    // Setters
    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public void setContent(char[] content) {
        this.content = content;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}