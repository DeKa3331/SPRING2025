package com.umcsuser.carrent.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {
    private String id;
    private String vehicleId;
    private String userId;
    private String rentDateTime;
    private String returnDateTime;
}