package com.yaro.product.utils;

import com.yaro.product.dto.DtoProductRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductUtils {

    public static boolean isValidRequest(DtoProductRequest productRequest) {
        if(productRequest != null) {
            return productRequest.getName() != null && productRequest.getName().length() > 0
                    && productRequest.getDescription() != null && productRequest.getDescription().length() > 0
                    && productRequest.getPrice() != null && productRequest.getPrice().intValue() > 0 && productRequest.getPrice().longValue() < Long.MAX_VALUE;
        }
        return false;
    }
}
