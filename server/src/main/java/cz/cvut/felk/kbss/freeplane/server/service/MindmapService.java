package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapContentDto;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapInfoDto;

import java.util.List;

public interface MindmapService {

      Mindmap findMindmapById(long id);

      void save(Mindmap mindmap);

      void update(Mindmap mindmap);

      void delete(Mindmap mindmap);

      List<Mindmap> getAllMindmaps();

      boolean hasPermission(Mindmap mindmap, User user);

      List<Mindmap> getOwnMindmapsByCollaborator(long collaboratorId);

      List<Mindmap> getSharedMindmapsByCollaborator(long collaboratorId);

      MindmapInfoDto convertToDto(Mindmap mindmap);

      MindmapContentDto convertToContentDto(Mindmap mindmap);

      Mindmap convertToEntity(MindmapInfoDto mindmapDto);

      boolean isCurrentUserMindmapCreator(long mindmapId);

      void updateIsPublic(Mindmap mindmap);
}
