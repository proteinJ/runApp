package com.running.runapp.domain.running.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long memberId;
    private Double lat;
    private Double lng;
    private Long groupId;
}
