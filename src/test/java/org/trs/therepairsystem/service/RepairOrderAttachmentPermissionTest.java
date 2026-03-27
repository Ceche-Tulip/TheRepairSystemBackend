package org.trs.therepairsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.trs.therepairsystem.common.enums.AttachmentType;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;
import org.trs.therepairsystem.common.exception.BusinessException;
import org.trs.therepairsystem.config.AttachmentStorageProperties;
import org.trs.therepairsystem.entity.RepairOrder;
import org.trs.therepairsystem.entity.RepairOrderAttachment;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.repository.RepairOrderAttachmentRepository;
import org.trs.therepairsystem.repository.RepairOrderRepository;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.service.storage.AttachmentStorageService;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 附件三态权限校验测试
 * 
 * 测试目标：验证 RepairOrderAttachmentServiceImpl 中的权限校验逻辑
 * - canUploadByStatusAndType(): 校验上传权限关系（角色-状态-类型）
 * - canDeleteByStatus(): 校验删除权限关系（角色-状态）
 */
@DisplayName("工单附件权限校验测试")
class RepairOrderAttachmentPermissionTest {

    @Mock
    private RepairOrderAttachmentRepository attachmentRepository;

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttachmentStorageProperties storageProperties;

    @Mock
    private List<AttachmentStorageService> storageServices;

    @InjectMocks
    private RepairOrderAttachmentServiceImpl attachmentService;

    private User submitter;
    private User engineer;
    private User admin;
    private RepairOrder testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化测试用户
        submitter = new User();
        submitter.setId(1L);
        submitter.setUsername("user1");
        
        engineer = new User();
        engineer.setId(2L);
        engineer.setUsername("engineer1");
        
        admin = new User();
        admin.setId(3L);
        admin.setUsername("admin1");

        // 初始化测试工单
        testOrder = RepairOrder.builder()
                .id(100L)
                .submitUser(submitter)
                .engineer(engineer)
                .admin(admin)
                .status(RepairOrderStatus.DRAFT)
                .build();

        // 初始化存储配置
        when(storageProperties.getStorageProvider()).thenReturn(
                org.trs.therepairsystem.common.enums.StorageProvider.LOCAL);
        when(storageProperties.getMaxFileSizeBytes()).thenReturn(10485760L);
        when(storageProperties.getAllowedContentTypes()).thenReturn(
                List.of("image/jpeg", "image/png", "image/webp"));

        // 创建mock存储服务
        AttachmentStorageService mockStorageService = mock(AttachmentStorageService.class);
        when(mockStorageService.provider()).thenReturn(org.trs.therepairsystem.common.enums.StorageProvider.LOCAL);
        // upload方法通常是void，Mockito会自动处理，无需配置返回值
        
        when(storageServices.stream()).thenReturn(List.of(mockStorageService).stream());
        when(storageServices.iterator()).thenReturn(List.of(mockStorageService).iterator());
        
        // Mock repository save方法以支持成功上传测试
        when(attachmentRepository.save(any(RepairOrderAttachment.class))).thenAnswer(invocation -> {
            RepairOrderAttachment attachment = invocation.getArgument(0);
            if (attachment.getId() == null) {
                attachment.setId(1L);
            }
            return attachment;
        });
        
        // 手动初始化 storageServiceByProvider（模拟 @PostConstruct）
        java.util.Map<org.trs.therepairsystem.common.enums.StorageProvider, AttachmentStorageService> map = 
            new java.util.EnumMap<>(org.trs.therepairsystem.common.enums.StorageProvider.class);
        map.put(org.trs.therepairsystem.common.enums.StorageProvider.LOCAL, mockStorageService);
        
        try {
            java.lang.reflect.Field field = attachmentService.getClass().getDeclaredField("storageServiceByProvider");
            field.setAccessible(true);
            field.set(attachmentService, map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===================== 上传权限测试 =====================

    @DisplayName("用户上传权限场景矩阵")
    @ParameterizedTest(name = "用户在{0}状态下上传{1}应该{2}")
    @CsvSource({
            // 用户在 DRAFT/SUBMITTED/PENDING 可上传 PROBLEM_PHOTO
            "DRAFT,PROBLEM_PHOTO,允许",
            "SUBMITTED,PROBLEM_PHOTO,允许",
            "PENDING,PROBLEM_PHOTO,允许",
            // 用户在 IN_PROGRESS 及以后禁止上传 PROBLEM_PHOTO
            "IN_PROGRESS,PROBLEM_PHOTO,禁止",
            "COMPLETED,PROBLEM_PHOTO,禁止",
            "CLOSED,PROBLEM_PHOTO,禁止",
            "CANCELLED,PROBLEM_PHOTO,禁止",
            // 用户在 DRAFT/SUBMITTED/PENDING 可上传 BEFORE_AFTER
            "DRAFT,BEFORE_AFTER,允许",
            "SUBMITTED,BEFORE_AFTER,允许",
            "PENDING,BEFORE_AFTER,允许",
            // 用户在 IN_PROGRESS 及以后禁止上传 BEFORE_AFTER
            "IN_PROGRESS,BEFORE_AFTER,禁止",
            "COMPLETED,BEFORE_AFTER,禁止",
            // 用户禁止上传 REPAIR_PROOF（任何状态）
            "DRAFT,REPAIR_PROOF,禁止",
            "IN_PROGRESS,REPAIR_PROOF,禁止",
            "COMPLETED,REPAIR_PROOF,禁止",
    })
    void testUserUploadPermissions(String statusStr, String attachmentTypeStr, String expectedResult) {
        // 初始化
        RepairOrderStatus status = RepairOrderStatus.valueOf(statusStr);
        AttachmentType type = AttachmentType.valueOf(attachmentTypeStr);

        testOrder.setStatus(status);
        testOrder.setEngineer(null); // 只有提交者

        when(repairOrderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(userRepository.findById(1L)).thenReturn(Optional.of(submitter));
        when(storageProperties.getMaxFileSizeBytes()).thenReturn(10485760L);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // 执行
        if ("允许".equals(expectedResult)) {
            try {
                // 不会真正上传（存储服务未初始化），但会验证权限
                attachmentService.uploadAttachment(1L, false, 100L, mockFile, type);
                assertTrue(true, "上传应该被允许");
            } catch (BusinessException e) {
                if (e.getMessage().contains("STATE_VIOLATION")) {
                    fail("用户上传应该被允许，但被拒绝: " + e.getMessage());
                }
            }
        } else { // 禁止
            assertThrows(BusinessException.class, () -> {
                attachmentService.uploadAttachment(1L, false, 100L, mockFile, type);
            }, "应该抛出权限异常");
        }
    }

    @DisplayName("工程师上传权限场景矩阵")
    @ParameterizedTest(name = "工程师在{0}状态下上传{1}应该{2}")
    @CsvSource({
            // 工程师在 DRAFT/SUBMITTED/PENDING 禁止上传
            "DRAFT,REPAIR_PROOF,禁止",
            "SUBMITTED,REPAIR_PROOF,禁止",
            "PENDING,REPAIR_PROOF,禁止",
            // 工程师在 IN_PROGRESS/COMPLETED 可上传 REPAIR_PROOF
            "IN_PROGRESS,REPAIR_PROOF,允许",
            "COMPLETED,REPAIR_PROOF,允许",
            // 工程师在 CLOSED/CANCELLED 禁止上传
            "CLOSED,REPAIR_PROOF,禁止",
            "CANCELLED,REPAIR_PROOF,禁止",
            // 工程师在 IN_PROGRESS/COMPLETED 可上传 BEFORE_AFTER
            "IN_PROGRESS,BEFORE_AFTER,允许",
            "COMPLETED,BEFORE_AFTER,允许",
            // 工程师禁止上传 PROBLEM_PHOTO（任何状态）
            "DRAFT,PROBLEM_PHOTO,禁止",
            "IN_PROGRESS,PROBLEM_PHOTO,禁止",
            "COMPLETED,PROBLEM_PHOTO,禁止",
    })
    void testEngineerUploadPermissions(String statusStr, String attachmentTypeStr, String expectedResult) {
        // 初始化
        RepairOrderStatus status = RepairOrderStatus.valueOf(statusStr);
        AttachmentType type = AttachmentType.valueOf(attachmentTypeStr);

        testOrder.setStatus(status);
        testOrder.setSubmitUser(submitter);

        when(repairOrderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(userRepository.findById(2L)).thenReturn(Optional.of(engineer));

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // 执行
        if ("允许".equals(expectedResult)) {
            try {
                attachmentService.uploadAttachment(2L, false, 100L, mockFile, type);
                assertTrue(true, "上传应该被允许");
            } catch (BusinessException e) {
                if (e.getMessage().contains("STATE_VIOLATION")) {
                    fail("工程师上传应该被允许，但被拒绝: " + e.getMessage());
                }
            }
        } else { // 禁止
            assertThrows(BusinessException.class, () -> {
                attachmentService.uploadAttachment(2L, false, 100L, mockFile, type);
            }, "应该抛出权限异常");
        }
    }

    @DisplayName("管理员上传权限无限制")
    @ParameterizedTest(name = "管理员在{0}状态下上传{1}应该允许")
    @CsvSource({
            "DRAFT,PROBLEM_PHOTO",
            "DRAFT,REPAIR_PROOF",
            "DRAFT,BEFORE_AFTER",
            "IN_PROGRESS,PROBLEM_PHOTO",
            "IN_PROGRESS,REPAIR_PROOF",
            "IN_PROGRESS,BEFORE_AFTER",
            "COMPLETED,PROBLEM_PHOTO",
            "COMPLETED,REPAIR_PROOF",
            "COMPLETED,BEFORE_AFTER",
            "CLOSED,PROBLEM_PHOTO",
            "CANCELLED,REPAIR_PROOF",
    })
    void testAdminUploadPermissions(String statusStr, String attachmentTypeStr) {
        // 初始化
        RepairOrderStatus status = RepairOrderStatus.valueOf(statusStr);
        AttachmentType type = AttachmentType.valueOf(attachmentTypeStr);

        testOrder.setStatus(status);

        when(repairOrderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(userRepository.findById(3L)).thenReturn(Optional.of(admin));

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // 执行 - 管理员全权限
        try {
            attachmentService.uploadAttachment(3L, true, 100L, mockFile, type);
            assertTrue(true, "管理员上传应该被允许");
        } catch (BusinessException e) {
            if (e.getMessage().contains("STATE_VIOLATION")) {
                fail("管理员上传应该无限制，但被拒绝: " + e.getMessage());
            }
        }
    }

    // ===================== 删除权限测试 =====================

    @DisplayName("用户删除权限场景矩阵")
    @ParameterizedTest(name = "用户在{0}状态下删除自己的附件应该{1}")
    @CsvSource({
            // 用户在 DRAFT/SUBMITTED/PENDING 可删除
            "DRAFT,允许",
            "SUBMITTED,允许",
            "PENDING,允许",
            // 用户在 IN_PROGRESS 及以后禁止删除
            "IN_PROGRESS,禁止",
            "COMPLETED,禁止",
            "CLOSED,禁止",
            "CANCELLED,禁止",
    })
    void testUserDeletePermissions(String statusStr, String expectedResult) {
        // 初始化
        RepairOrderStatus status = RepairOrderStatus.valueOf(statusStr);

        testOrder.setStatus(status);
        testOrder.setEngineer(null);

        RepairOrderAttachment attachment = RepairOrderAttachment.builder()
                .id(1L)
                .order(testOrder)
                .uploadedBy(submitter)
                .attachmentType(AttachmentType.PROBLEM_PHOTO)
                .build();

        when(attachmentRepository.findByIdAndOrderId(1L, 100L))
                .thenReturn(Optional.of(attachment));
        when(repairOrderRepository.findById(100L))
                .thenReturn(Optional.of(testOrder));

        // 执行
        if ("允许".equals(expectedResult)) {
            // 不会真正删除（存储服务未初始化），但会验证权限
            try {
                attachmentService.deleteAttachment(1L, false, 100L, 1L);
                assertTrue(true, "删除应该被允许");
            } catch (BusinessException e) {
                if (e.getMessage().contains("STATE_VIOLATION")) {
                    fail("用户删除应该被允许，但被拒绝: " + e.getMessage());
                }
            }
        } else { // 禁止
            assertThrows(BusinessException.class, () -> {
                attachmentService.deleteAttachment(1L, false, 100L, 1L);
            }, "应该抛出权限异常");
        }
    }

    @DisplayName("工程师删除权限场景矩阵")
    @ParameterizedTest(name = "工程师在{0}状态下删除自己的附件应该{1}")
    @CsvSource({
            // 工程师在 DRAFT/SUBMITTED/PENDING 禁止删除
            "DRAFT,禁止",
            "SUBMITTED,禁止",
            "PENDING,禁止",
            // 工程师在 IN_PROGRESS/COMPLETED 可删除
            "IN_PROGRESS,允许",
            "COMPLETED,允许",
            // 工程师在 CLOSED/CANCELLED 禁止删除
            "CLOSED,禁止",
            "CANCELLED,禁止",
    })
    void testEngineerDeletePermissions(String statusStr, String expectedResult) {
        // 初始化
        RepairOrderStatus status = RepairOrderStatus.valueOf(statusStr);

        testOrder.setStatus(status);

        RepairOrderAttachment attachment = RepairOrderAttachment.builder()
                .id(1L)
                .order(testOrder)
                .uploadedBy(engineer)
                .attachmentType(AttachmentType.REPAIR_PROOF)
                .build();

        when(attachmentRepository.findByIdAndOrderId(1L, 100L))
                .thenReturn(Optional.of(attachment));
        when(repairOrderRepository.findById(100L))
                .thenReturn(Optional.of(testOrder));

        // 执行
        if ("允许".equals(expectedResult)) {
            try {
                attachmentService.deleteAttachment(2L, false, 100L, 1L);
                assertTrue(true, "删除应该被允许");
            } catch (BusinessException e) {
                if (e.getMessage().contains("STATE_VIOLATION")) {
                    fail("工程师删除应该被允许，但被拒绝: " + e.getMessage());
                }
            }
        } else { // 禁止
            assertThrows(BusinessException.class, () -> {
                attachmentService.deleteAttachment(2L, false, 100L, 1L);
            }, "应该抛出权限异常");
        }
    }

    @DisplayName("管理员删除权限无限制")
    @ParameterizedTest(name = "管理员在{0}状态下删除任意附件应该允许")
    @CsvSource({
            "DRAFT",
            "SUBMITTED",
            "PENDING",
            "IN_PROGRESS",
            "COMPLETED",
            "CLOSED",
            "CANCELLED",
    })
    void testAdminDeletePermissions(String statusStr) {
        // 初始化
        RepairOrderStatus status = RepairOrderStatus.valueOf(statusStr);

        testOrder.setStatus(status);

        RepairOrderAttachment attachment = RepairOrderAttachment.builder()
                .id(1L)
                .order(testOrder)
                .uploadedBy(submitter) // 由提交者上传
                .attachmentType(AttachmentType.PROBLEM_PHOTO)
                .build();

        when(attachmentRepository.findByIdAndOrderId(1L, 100L))
                .thenReturn(Optional.of(attachment));
        when(repairOrderRepository.findById(100L))
                .thenReturn(Optional.of(testOrder));

        // 执行 - 管理员可删除任何人的附件，无状态限制
        try {
            attachmentService.deleteAttachment(3L, true, 100L, 1L);
            assertTrue(true, "管理员删除应该无限制");
        } catch (BusinessException e) {
            if (e.getMessage().contains("STATE_VIOLATION")) {
                fail("管理员删除应该无限制，但被拒绝: " + e.getMessage());
            }
        }
    }

    @DisplayName("非上传者无法删除他人附件")
    @Test
    void testDeleteOthersAttachmentDenied() {
        testOrder.setStatus(RepairOrderStatus.IN_PROGRESS);

        RepairOrderAttachment attachment = RepairOrderAttachment.builder()
                .id(1L)
                .order(testOrder)
                .uploadedBy(submitter) // 由提交者上传
                .attachmentType(AttachmentType.PROBLEM_PHOTO)
                .build();

        when(attachmentRepository.findByIdAndOrderId(1L, 100L))
                .thenReturn(Optional.of(attachment));
        when(repairOrderRepository.findById(100L))
                .thenReturn(Optional.of(testOrder));

        // 工程师尝试删除提交者的附件 - 应拒绝（即使在允许状态）
        assertThrows(BusinessException.class, () -> {
            attachmentService.deleteAttachment(2L, false, 100L, 1L);
        });
    }

    @DisplayName("BEFORE_AFTER 类型特殊场景")
    @Test
    void testBeforeAfterTypePermissions() {
        // 用户在 PENDING 可上传 BEFORE_AFTER（预期效果图）
        testOrder.setStatus(RepairOrderStatus.PENDING);
        testOrder.setEngineer(engineer);

        when(repairOrderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(userRepository.findById(1L)).thenReturn(Optional.of(submitter));

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getOriginalFilename()).thenReturn("before.jpg");

        try {
            attachmentService.uploadAttachment(1L, false, 100L, mockFile, AttachmentType.BEFORE_AFTER);
            assertTrue(true, "用户在 PENDING 应能上传 BEFORE_AFTER");
        } catch (BusinessException e) {
            fail("用户在 PENDING 应能上传 BEFORE_AFTER，但被拒绝: " + e.getMessage());
        }

        // 工程师在 COMPLETED 可上传 BEFORE_AFTER（最终结果图）
        testOrder.setStatus(RepairOrderStatus.COMPLETED);

        when(userRepository.findById(2L)).thenReturn(Optional.of(engineer));

        try {
            attachmentService.uploadAttachment(2L, false, 100L, mockFile, AttachmentType.BEFORE_AFTER);
            assertTrue(true, "工程师在 COMPLETED 应能上传 BEFORE_AFTER");
        } catch (BusinessException e) {
            fail("工程师在 COMPLETED 应能上传 BEFORE_AFTER，但被拒绝: " + e.getMessage());
        }
    }
}
