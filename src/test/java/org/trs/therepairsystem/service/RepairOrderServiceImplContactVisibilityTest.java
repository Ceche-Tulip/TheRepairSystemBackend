package org.trs.therepairsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;
import org.trs.therepairsystem.dto.response.RepairOrderResponse;
import org.trs.therepairsystem.entity.Building;
import org.trs.therepairsystem.entity.FaultType;
import org.trs.therepairsystem.entity.Floor;
import org.trs.therepairsystem.entity.RepairOrder;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.repository.BuildingRepository;
import org.trs.therepairsystem.repository.EngineerAreaRelRepository;
import org.trs.therepairsystem.repository.EngineerFaultRelRepository;
import org.trs.therepairsystem.repository.FaultTypeRepository;
import org.trs.therepairsystem.repository.FloorRepository;
import org.trs.therepairsystem.repository.RepairOrderRepository;
import org.trs.therepairsystem.repository.RepairRatingRepository;
import org.trs.therepairsystem.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceImplContactVisibilityTest {

    @Mock
    private RepairOrderRepository repairOrderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private FloorRepository floorRepository;
    @Mock
    private FaultTypeRepository faultTypeRepository;
    @Mock
    private EngineerAreaRelRepository engineerAreaRelRepository;
    @Mock
    private EngineerFaultRelRepository engineerFaultRelRepository;
    @Mock
    private RepairRatingRepository repairRatingRepository;

    @InjectMocks
    private RepairOrderServiceImpl repairOrderService;

    private User submitter;
    private User engineer;
    private User admin;
    private RepairOrder assignedOrder;
    private RepairOrder unassignedOrder;

    @BeforeEach
    void setUp() {
        submitter = new User();
        submitter.setId(1L);
        submitter.setUsername("submitter");
        submitter.setPhone("13800138000");

        engineer = new User();
        engineer.setId(2L);
        engineer.setUsername("engineer");
        engineer.setPhone("13900139000");

        admin = new User();
        admin.setId(3L);
        admin.setUsername("admin");
        admin.setPhone("13600136000");

        Building building = new Building(10L, "A栋");
        Floor floor = new Floor(20L, building, 3, "3F");
        FaultType faultType = new FaultType(30L, "空调", "#FFFFFF", "desc");

        assignedOrder = RepairOrder.builder()
                .id(100L)
                .submitUser(submitter)
                .engineer(engineer)
                .admin(admin)
                .building(building)
                .floor(floor)
                .faultType(faultType)
                .status(RepairOrderStatus.PENDING)
                .description("test")
                .createTime(LocalDateTime.now())
                .build();

        unassignedOrder = RepairOrder.builder()
                .id(101L)
                .submitUser(submitter)
                .engineer(null)
                .admin(admin)
                .building(building)
                .floor(floor)
                .faultType(faultType)
                .status(RepairOrderStatus.SUBMITTED)
                .description("test")
                .createTime(LocalDateTime.now())
                .build();
    }

    @Test
    void getOrderById_submitterViewAssignedOrder_shouldExposeBothPhones() {
        when(repairOrderRepository.findById(100L)).thenReturn(Optional.of(assignedOrder));

        RepairOrderResponse response = repairOrderService.getOrderById(1L, false, 100L);

        assertEquals("13800138000", response.getSubmitUserPhone());
        assertEquals("13900139000", response.getEngineerPhone());
    }

    @Test
    void getOrderById_engineerViewAssignedOrder_shouldExposeBothPhones() {
        when(repairOrderRepository.findById(100L)).thenReturn(Optional.of(assignedOrder));

        RepairOrderResponse response = repairOrderService.getOrderById(2L, false, 100L);

        assertEquals("13800138000", response.getSubmitUserPhone());
        assertEquals("13900139000", response.getEngineerPhone());
    }

    @Test
    void getOrderById_adminViewAssignedOrder_shouldExposeBothPhones() {
        when(repairOrderRepository.findById(100L)).thenReturn(Optional.of(assignedOrder));

        RepairOrderResponse response = repairOrderService.getOrderById(3L, true, 100L);

        assertEquals("13800138000", response.getSubmitUserPhone());
        assertEquals("13900139000", response.getEngineerPhone());
    }

    @Test
    void getOrderById_submitterViewUnassignedOrder_shouldExposeSubmitterPhoneOnly() {
        when(repairOrderRepository.findById(101L)).thenReturn(Optional.of(unassignedOrder));

        RepairOrderResponse response = repairOrderService.getOrderById(1L, false, 101L);

        assertEquals("13800138000", response.getSubmitUserPhone());
        assertNull(response.getEngineerPhone());
    }
}
