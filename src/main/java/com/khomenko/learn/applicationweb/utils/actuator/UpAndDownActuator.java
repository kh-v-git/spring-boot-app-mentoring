/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.utils.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("upAndDown")
public class UpAndDownActuator implements HealthIndicator {

    private boolean isUp = true;

    @Override
    public Health health() {
        if (isUp) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }

    public void setUp(boolean up) {
        isUp = up;
    }
}
