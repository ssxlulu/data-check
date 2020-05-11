package com.ssxlulu.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Job configuration.
 *
 * @author ssxlulu
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobConfiguration {
    private int concurrency = 30;
}
