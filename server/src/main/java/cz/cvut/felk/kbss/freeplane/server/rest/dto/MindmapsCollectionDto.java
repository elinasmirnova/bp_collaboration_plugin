package cz.cvut.felk.kbss.freeplane.server.rest.dto;

import java.util.List;

public class MindmapsCollectionDto {

    List<MindmapInfoDto> yourMindmaps;
    List<MindmapInfoDto> sharedMindmaps;

    public List<MindmapInfoDto> getYourMindmaps() {
        return yourMindmaps;
    }

    public void setYourMindmaps(List<MindmapInfoDto> yourMindmaps) {
        this.yourMindmaps = yourMindmaps;
    }

    public List<MindmapInfoDto> getSharedMindmaps() {
        return sharedMindmaps;
    }

    public void setSharedMindmaps(List<MindmapInfoDto> sharedMindmaps) {
        this.sharedMindmaps = sharedMindmaps;
    }
}
