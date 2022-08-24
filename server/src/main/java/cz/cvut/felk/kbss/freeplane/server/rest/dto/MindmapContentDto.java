package cz.cvut.felk.kbss.freeplane.server.rest.dto;

public class MindmapContentDto extends MindmapInfoDto {

    private byte[] xml;
    private boolean isReadOnly;

    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }
}
