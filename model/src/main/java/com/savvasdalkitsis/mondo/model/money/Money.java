package com.savvasdalkitsis.mondo.model.money;

import lombok.Getter;
import lombok.experimental.Builder;

@Builder
@Getter
public class Money {

    int wholeValue;
    String currency;
}