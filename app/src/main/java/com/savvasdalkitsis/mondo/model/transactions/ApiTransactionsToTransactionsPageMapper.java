/**
 * Copyright (C) 2016 Savvas Dalkitsis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.savvasdalkitsis.mondo.model.transactions;

import com.savvasdalkitsis.mondo.infra.date.DateParser;
import com.savvasdalkitsis.mondo.model.money.Money;
import com.savvasdalkitsis.mondo.repository.model.ApiMerchant;
import com.savvasdalkitsis.mondo.repository.model.ApiTransaction;
import com.savvasdalkitsis.mondo.repository.model.ApiTransactions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.functions.Func1;

import static com.savvasdalkitsis.mondo.util.StringUtils.isNotEmptyNorNull;

public class ApiTransactionsToTransactionsPageMapper implements Func1<ApiTransactions, TransactionsPage> {

    private DateParser dateParser;

    public ApiTransactionsToTransactionsPageMapper(DateParser dateParser) {
        this.dateParser = dateParser;
    }

    @Override
    public TransactionsPage call(ApiTransactions apiTransactions) {
        List<Transaction> transactions = new ArrayList<>();
        for (ApiTransaction apiTransaction : apiTransactions.getTransactions()) {
            ApiMerchant merchant = nullSafe(apiTransaction.getMerchant());
            String merchantName = merchant.getName();
            transactions.add(Transaction.builder()
                    .created(dateParser.parse(apiTransaction.getCreated()))
                    .amount(Money.builder()
                            .wholeValue(Math.abs(apiTransaction.getAmount()))
                            .expense(apiTransaction.getAmount() < 0)
                            .currency(apiTransaction.getCurrency())
                            .build())
                    .amountInLocalCurrency(Money.builder()
                            .wholeValue(Math.abs(apiTransaction.getLocalAmount()))
                            .expense(apiTransaction.getLocalAmount() < 0)
                            .currency(apiTransaction.getLocalCurrency())
                            .build())
                    .description(isNotEmptyNorNull(merchantName) ? merchantName : apiTransaction.getDescription())
                    .logoUrl(merchant.getLogo())
                    .build());
        }
        // in a real app, this would be sorted by a comparator using the transaction date
        Collections.reverse(transactions);
        return TransactionsPage.builder()
                .transactions(transactions)
                .build();
    }

    private ApiMerchant nullSafe(ApiMerchant merchant) {
        return merchant != null ? merchant : ApiMerchant.builder().build();
    }
}
