package com.ssxlulu.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Index meta data.
 *
 * @author ssxlulu
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class IndexMetaData {
    private final String name;
}
