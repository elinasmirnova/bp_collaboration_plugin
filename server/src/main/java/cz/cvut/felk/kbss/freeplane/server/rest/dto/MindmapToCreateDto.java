package cz.cvut.felk.kbss.freeplane.server.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MindmapToCreateDto {

    private String title;

    @JsonProperty("isPublic")
    private Boolean isPublic;

    private String map;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
