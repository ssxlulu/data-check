package com.ssxlulu.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Column.
 *
 * @author ssxlulu
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Column {

    private final String name;

    private final Object value;

    private final boolean primaryKey;
}
