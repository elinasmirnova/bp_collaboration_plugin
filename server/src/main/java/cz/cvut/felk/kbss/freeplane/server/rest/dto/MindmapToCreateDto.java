package cz.cvut.felk.kbss.freeplane.server.rest.dto;

public class MindmapToCreateDto {

    private String title;
    private boolean isPublic;
    private String xml;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
