package org.trs.therepairsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trs.therepairsystem.entity.RepairOrderAttachment;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairOrderAttachmentRepository extends JpaRepository<RepairOrderAttachment, Long> {

    List<RepairOrderAttachment> findByOrderIdOrderByUploadTimeDesc(Long orderId);

    Optional<RepairOrderAttachment> findByIdAndOrderId(Long attachmentId, Long orderId);
}
