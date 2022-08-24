package cz.cvut.felk.kbss.freeplane.server.service.impl;

import cz.cvut.felk.kbss.freeplane.server.dao.MindmapRepository;
import cz.cvut.felk.kbss.freeplane.server.model.Collaboration;
import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapContentDto;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapInfoDto;
import cz.cvut.felk.kbss.freeplane.server.security.utils.SecurityUtils;
import cz.cvut.felk.kbss.freeplane.server.service.CollaborationService;
import cz.cvut.felk.kbss.freeplane.server.service.MindmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mind map service implementation
 */
@Service
@Transactional
public class MindmapServiceImpl implements MindmapService {

    private final MindmapRepository mindmapRepository;

    private final CollaborationService collaborationService;

    @Autowired
    public MindmapServiceImpl(MindmapRepository mindmapRepository, CollaborationService collaborationService) {
        this.mindmapRepository = mindmapRepository;
        this.collaborationService = collaborationService;
    }

    @Override
    public Mindmap findMindmapById(long id) {
        return this.mindmapRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Long save(Mindmap mindmap) {
        return mindmapRepository.save(mindmap).getMindmapId();
    }

    @Override
    @Transactional
    public void update(Mindmap mindmap) {
        mindmapRepository.save(mindmap);
    }

    @Override
    @Transactional
    public void delete(Mindmap mindmap) {
        mindmapRepository.delete(mindmap);
    }

    @Override
    @Transactional
    public List<Mindmap> getAllMindmaps() {
        return mindmapRepository.findAll();
    }

    @Override
    public List<Mindmap> getOwnMindmapsByCollaborator(long collaboratorId) {
        return mindmapRepository.getOwnMindmapsByCollaboratorId(collaboratorId);
    }

    @Override
    public List<Mindmap> getSharedMindmapsByCollaborator(long collaboratorId) {
        return mindmapRepository.getSharedMindmapsByCollaboratorId(collaboratorId);
    }

    @Override
    public MindmapInfoDto convertToDto(Mindmap map) {
        MindmapInfoDto dto = new MindmapInfoDto();
        dto.setMindmapId(map.getMindmapId());
        dto.setTitle(map.getTitle());
        dto.setCreationDate(map.getCreationDate());
        dto.setCreatorId(map.getCreator().getCollaboratorId());
        dto.setEditionDate(map.getEditionDate());
        dto.setLastEditorId(map.getLastEditorId());
        dto.setPublic(String.valueOf(map.isPublic()));
        dto.setCollaborations(map.getCollaborations().stream().map(this.collaborationService::convertToDto).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public MindmapContentDto convertToContentDto(Mindmap map) {
        MindmapContentDto dto = new MindmapContentDto();
        dto.setMindmapId(map.getMindmapId());
        dto.setTitle(map.getTitle());
        dto.setCreationDate(map.getCreationDate());
        dto.setCreatorId(map.getCreator().getCollaboratorId());
        dto.setEditionDate(map.getEditionDate());
        dto.setLastEditorId(map.getLastEditorId());
        dto.setPublic(String.valueOf(map.isPublic()));
        dto.setCollaborations(map.getCollaborations().stream().map(this.collaborationService::convertToDto).collect(Collectors.toList()));
        dto.setXml(map.getXml());
        return dto;
    }

    @Override
    @Transactional
    public boolean isCurrentUserMindmapCreator(long id) {
        return SecurityUtils.getCurrentUserDetails().getUser().getMindmaps()
                .stream()
                .map(Mindmap::getMindmapId)
                .anyMatch(mindmapId -> mindmapId.equals(id));
    }

    @Override
    @Transactional
    public boolean isCurrentUserCollaborator(long mindmapId) {
        return SecurityUtils.getCurrentUserDetails().getUser().getCollaborations()
                .stream()
                .map(Collaboration::getMindmap)
                .map(Mindmap::getMindmapId)
                .anyMatch(collaborationMindmapId -> collaborationMindmapId.equals(mindmapId));
    }

    @Override
    @Transactional
    public String getCurrentUserRoleForMindmap(long mindmapId) {
        return SecurityUtils.getCurrentUserDetails().getUser().getCollaborations()
                .stream()
                .filter(collaboration -> collaboration.getMindmap().getMindmapId().equals(mindmapId))
                .findFirst()
                .get()
                .getRole();
    }


    @Override
    @Transactional
    public boolean mindMapTitleExists(String inputTitle) {
        return SecurityUtils.getCurrentUserDetails().getUser().getMindmaps()
                .stream()
                .map(Mindmap::getTitle)
                .anyMatch(inputTitle::equals);
    }
}
