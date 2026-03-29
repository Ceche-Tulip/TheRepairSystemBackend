package org.trs.therepairsystem.common.util;

import org.trs.therepairsystem.entity.RepairOrder;
import org.trs.therepairsystem.entity.User;

public final class ContactVisibilityPolicy {

    private ContactVisibilityPolicy() {
    }

    public static String resolveOrderPhone(User targetUser, RepairOrder order, Long viewerUserId, boolean viewerIsAdmin) {
        if (targetUser == null) {
            return null;
        }
        String phone = targetUser.getPhone();
        if (phone == null || phone.isBlank()) {
            return phone;
        }
        if (viewerIsAdmin) {
            return phone;
        }
        if (viewerUserId == null) {
            return DataMaskingUtils.maskPhone(phone);
        }
        if (viewerUserId.equals(targetUser.getId())) {
            return phone;
        }

        boolean viewerIsSubmitter = order.getSubmitUser() != null && viewerUserId.equals(order.getSubmitUser().getId());
        boolean viewerIsEngineer = order.getEngineer() != null && viewerUserId.equals(order.getEngineer().getId());
        boolean targetIsSubmitter = order.getSubmitUser() != null && targetUser.getId().equals(order.getSubmitUser().getId());
        boolean targetIsEngineer = order.getEngineer() != null && targetUser.getId().equals(order.getEngineer().getId());

        boolean collaborators = (viewerIsSubmitter && targetIsEngineer) || (viewerIsEngineer && targetIsSubmitter);
        if (collaborators) {
            return phone;
        }

        return DataMaskingUtils.maskPhone(phone);
    }
}
