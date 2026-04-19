package com.bank.ooad.patterns.strategy;

import com.bank.ooad.models.system.NotificationMessage;

public interface INotificationGateway {
    boolean dispatch(NotificationMessage message);
    String captureDeliveryStatus();
}
