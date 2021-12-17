package cz.cvut.felk.kbss.freeplane.server.service.impl;

import cz.cvut.felk.kbss.freeplane.server.config.SocketSessionRegistry;
import cz.cvut.felk.kbss.freeplane.server.dao.MindmapRepository;
import cz.cvut.felk.kbss.freeplane.server.model.Collaboration;
import cz.cvut.felk.kbss.freeplane.server.model.Mindmap;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapContentDto;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.MindmapInfoDto;
import cz.cvut.felk.kbss.freeplane.server.security.utils.SecurityUtils;
import cz.cvut.felk.kbss.freeplane.server.service.CollaborationService;
import cz.cvut.felk.kbss.freeplane.server.service.MindmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MindmapServiceImpl implements MindmapService {

    @Autowired
    private SocketSessionRegistry socketSessionRegistry;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MindmapRepository mindmapRepository;

    private final CollaborationService collaborationService;

    @Autowired
    public MindmapServiceImpl(SimpMessagingTemplate simpMessagingTemplate, MindmapRepository mindmapRepository, CollaborationService collaborationService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mindmapRepository = mindmapRepository;
        this.collaborationService = collaborationService;
    }

    @Override
    public Mindmap findMindmapById(long id) {
        return this.mindmapRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Mindmap mindmap) {
        mindmapRepository.save(mindmap);
    }

    @Override
    @Transactional
    public void update(Mindmap mindmap) {
        mindmapRepository.save(mindmap);
    }

    @Override
    public void delete(Mindmap mindmap) {

    }

    @Override
    public List<Mindmap> getAllMindmaps() {
        return mindmapRepository.findAll();
    }

    @Override
    public boolean hasPermission(Mindmap mindmap, User user) {
        return false;
    }


    public void startCollaboration(Mindmap mindmap) {
        for (Collaboration collaboration : mindmap.getCollaborations()) {
            socketSessionRegistry.getSessionInfo(collaboration.getCollaborator().getCollaboratorId().toString());
        }
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
        dto.setPublic(map.isPublic());
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
        dto.setPublic(map.isPublic());
        dto.setCollaborations(map.getCollaborations().stream().map(this.collaborationService::convertToDto).collect(Collectors.toList()));
        dto.setXml(map.getXml());
        return dto;
    }

    @Override
    public Mindmap convertToEntity(MindmapInfoDto mindmapDto) {
        Mindmap entity = new Mindmap();
        entity.setMindmapId(mindmapDto.getMindmapId());
        entity.setTitle(mindmapDto.getTitle());
        entity.setCreationDate(mindmapDto.getCreationDate());
       // entity.setCreatorId(mindmapDto.getCreatorId());
        entity.setEditionDate(mindmapDto.getEditionDate());
        entity.setLastEditorId(mindmapDto.getLastEditorId());
        entity.setPublic(mindmapDto.isPublic());
        if (mindmapDto instanceof MindmapContentDto) {
            entity.setXml(((MindmapContentDto) mindmapDto).getXml());
        }
        return entity;
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
    public void updateIsPublic(Mindmap mindmap) {
    }

}
