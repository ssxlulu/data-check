package com.ssxlulu.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Column meta data.
 *
 * @author ssxlulu
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ColumnMetaData {

    private final String name;

    private final int dataType;

    private final String dataTypeName;

    private final boolean primaryKey;

}
