package com.psychiatryclinic.core.utilities.sequence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class SequenceService {
    private final MongoOperations mongoOperations;

    public String getNextInvoiceNumber() {
        DatabaseSequence counter = mongoOperations.findAndModify(
            query(where("_id").is("invoice_sequence")),
            new Update().inc("seq", 1),
            options().returnNew(true).upsert(true),
            DatabaseSequence.class
        );

        int sequence = counter != null ? counter.getSeq() : 1;
        return String.format("FTR-%d%02d-%04d",
            java.time.Year.now().getValue(),
            java.time.MonthDay.now().getMonthValue(),
            sequence
        );
    }
} 