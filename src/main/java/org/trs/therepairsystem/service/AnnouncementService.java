package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.trs.therepairsystem.dto.request.announcement.AnnouncementCreateRequest;
import org.trs.therepairsystem.dto.request.announcement.AnnouncementUpdateRequest;
import org.trs.therepairsystem.dto.response.AnnouncementDTO;
import org.trs.therepairsystem.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    
    /**
     * 创建公告
     */
    Announcement createAnnouncement(AnnouncementCreateRequest request, Long adminId);
    
    /**
     * 更新公告
     */
    Announcement updateAnnouncement(Long id, AnnouncementUpdateRequest request, Long adminId);
    
    /**
     * 删除公告
     */
    void deleteAnnouncement(Long id);
    
    /**
     * 分页查询公告
     */
    Page<AnnouncementDTO> getAnnouncements(Pageable pageable);
    
    /**
     * 获取单个公告详情
     */
    AnnouncementDTO getAnnouncementById(Long id);
    
    /**
     * 获取置顶公告
     */
    List<AnnouncementDTO> getTopAnnouncements();
    
    /**
     * 获取最新公告（限制数量）
     */
    List<AnnouncementDTO> getLatestAnnouncements(int limit);
    /**
     * 获取已发布公告列表
     */
    List<AnnouncementDTO> getPublishedAnnouncements();
    
    /**
     * 发布公告
     */
    AnnouncementDTO publishAnnouncement(Long id, Long adminId);
    
    /**
     * 下架公告
     */
    AnnouncementDTO unpublishAnnouncement(Long id, Long adminId);
}