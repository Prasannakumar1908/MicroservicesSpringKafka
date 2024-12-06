package com.prodify.apigateway.Model;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserModel {
    private String userId;
    private String paymentDetails;
}
