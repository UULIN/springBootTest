package com.qunhe.instdeco.counterslow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author tumei
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TetrisSearchRangeDTO implements Serializable {
    private static final long serialVersionUID = 4910220674887920900L;
    private Long total;
    private Long cost;
    private List<TetrisSearchSingleRecord> data;
}
