package org.trs.therepairsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trs.therepairsystem.dto.request.announcement.AnnouncementCreateRequest;
import org.trs.therepairsystem.dto.request.announcement.AnnouncementUpdateRequest;
import org.trs.therepairsystem.dto.response.AnnouncementDTO;
import org.trs.therepairsystem.entity.Announcement;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.repository.AnnouncementRepository;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.web.converter.AnnouncementConverter;
import org.trs.therepairsystem.common.enums.AnnouncementStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    
    @Override
    public Announcement createAnnouncement(AnnouncementCreateRequest request, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在: " + adminId));
        
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setAdmin(admin);
        announcement.setStatus(request.getStatus() != null ? request.getStatus() : AnnouncementStatus.DRAFT);
        announcement.setIsTop(request.getIsTop() != null ? request.getIsTop() : false);
        
        return announcementRepository.save(announcement);
    }
    
    @Override
    public Announcement updateAnnouncement(Long id, AnnouncementUpdateRequest request, Long adminId) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + id));
        
        // 验证是否是原发布者或管理员
        if (!announcement.getAdmin().getId().equals(adminId)) {
            User currentAdmin = userRepository.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("管理员不存在: " + adminId));
            // 这里可以加入更细的权限验证
        }
        
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setIsTop(request.getIsTop() != null ? request.getIsTop() : false);
        
        return announcementRepository.save(announcement);
    }
    
    @Override
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new IllegalArgumentException("公告不存在: " + id);
        }
        announcementRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncements(Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findAllOrderByIsTopAndCreateTime(pageable);
        return announcements.map(AnnouncementConverter::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AnnouncementDTO getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + id));
        return AnnouncementConverter.toDTO(announcement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getTopAnnouncements() {
        List<Announcement> announcements = announcementRepository.findByIsTopTrueOrderByCreateTimeDesc();
        return announcements.stream()
                .map(AnnouncementConverter::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getLatestAnnouncements(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Announcement> announcements = announcementRepository.findTopAnnouncements(pageable);
        return announcements.stream()
                .map(AnnouncementConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementRepository.findByStatusOrderByIsTopDescCreateTimeDesc(AnnouncementStatus.PUBLISHED);
        return announcements.stream()
                .map(AnnouncementConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementDTO publishAnnouncement(Long id, Long adminId) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + id));
        
        announcement.setStatus(AnnouncementStatus.PUBLISHED);
        
        Announcement saved = announcementRepository.save(announcement);
        return AnnouncementConverter.toDTO(saved);
    }

    @Override
    public AnnouncementDTO unpublishAnnouncement(Long id, Long adminId) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + id));
        
        announcement.setStatus(AnnouncementStatus.UNPUBLISHED);
        
        Announcement saved = announcementRepository.save(announcement);
        return AnnouncementConverter.toDTO(saved);
    }
}