package org.trs.therepairsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trs.therepairsystem.entity.Announcement;
import org.trs.therepairsystem.common.enums.AnnouncementStatus;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    /**
     * 分页查询公告，按置顶和创建时间排序
     */
    @Query("SELECT a FROM Announcement a ORDER BY a.isTop DESC, a.createTime DESC")
    Page<Announcement> findAllOrderByIsTopAndCreateTime(Pageable pageable);
    
    /**
     * 查询置顶公告
     */
    List<Announcement> findByIsTopTrueOrderByCreateTimeDesc();
    
    /**
     * 查询最新的几条公告
     */
    @Query("SELECT a FROM Announcement a ORDER BY a.isTop DESC, a.createTime DESC")
    List<Announcement> findTopAnnouncements(Pageable pageable);

    /**
     * 根据状态查询公告，按置顶和创建时间排序
     */
    List<Announcement> findByStatusOrderByIsTopDescCreateTimeDesc(AnnouncementStatus status);
}