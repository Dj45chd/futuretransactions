package com.abnambro.futuretransactions;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FutureTransaction {

    private String clientInformation;

    private String productInformation;

    private Integer totalTransactionAmount;

    private String transactionDate;

    public FutureTransaction(String clientInformation, String productInformation, String transactionDate) {
        this.clientInformation = clientInformation;
        this.productInformation = productInformation;
        this.transactionDate = transactionDate;
    }
}
