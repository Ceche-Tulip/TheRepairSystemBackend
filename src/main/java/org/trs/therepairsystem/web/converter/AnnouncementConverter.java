package org.trs.therepairsystem.web.converter;

import org.trs.therepairsystem.dto.response.AnnouncementDTO;
import org.trs.therepairsystem.entity.Announcement;

public class AnnouncementConverter {
    
    public static AnnouncementDTO toDTO(Announcement announcement) {
        if (announcement == null) {
            return null;
        }
        
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setAdminId(announcement.getAdmin().getId());
        dto.setAdminName(announcement.getAdmin().getRealName());
        dto.setIsTop(announcement.getIsTop());
        dto.setStatus(announcement.getStatus());
        dto.setStatusDescription(announcement.getStatus().getDescription());
        dto.setCreateTime(announcement.getCreateTime());
        dto.setUpdateTime(announcement.getUpdateTime());
        
        return dto;
    }
}