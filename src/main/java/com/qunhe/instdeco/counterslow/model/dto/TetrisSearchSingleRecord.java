package com.qunhe.instdeco.counterslow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author tumei
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TetrisSearchSingleRecord implements Serializable {
    private static final long serialVersionUID = 4239991337021069183L;
    private String ps;
    private String timeRange;
}
