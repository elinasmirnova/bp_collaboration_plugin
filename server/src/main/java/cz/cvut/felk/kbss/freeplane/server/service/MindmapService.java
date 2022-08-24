package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapContentDto;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapInfoDto;

import java.util.List;

/**
 * Mind map service interface
 */
public interface MindmapService {

    Mindmap findMindmapById(long id);

    Long save(Mindmap mindmap);

    void update(Mindmap mindmap);

    void delete(Mindmap mindmap);

    List<Mindmap> getAllMindmaps();

    List<Mindmap> getOwnMindmapsByCollaborator(long collaboratorId);

    List<Mindmap> getSharedMindmapsByCollaborator(long collaboratorId);

    MindmapInfoDto convertToDto(Mindmap mindmap);

    MindmapContentDto convertToContentDto(Mindmap mindmap);

    boolean isCurrentUserMindmapCreator(long mindmapId);

    boolean isCurrentUserCollaborator(long mindmapId);

    String getCurrentUserRoleForMindmap(long mindmapId);

    boolean mindMapTitleExists(String inputTitle);
}
