package com.ttech.specifications;

import com.ttech.model.Transportation;
import com.ttech.model.Location;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Expression;

import java.time.DayOfWeek;

public class TransportationSpecifications {

    public static Specification<Transportation> hasOrigin(Location origin) {
        return (root, query, cb) -> origin != null ? cb.equal(root.get("originLocation"), origin) : null;
    }

    public static Specification<Transportation> hasDestination(Location destination) {
        return (root, query, cb) -> destination != null ? cb.equal(root.get("destinationLocation"), destination) : null;
    }

    public static Specification<Transportation> operatesOnDay(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null)
            return null;

        return (root, query, cb) -> {
            Expression<Integer> arrayPos = cb.function(
                    "ARRAY_POSITION", Integer.class,
                    root.get("operatingDays"),
                    cb.literal(dayOfWeek.getValue()));

            return cb.gt(arrayPos, 0); // ARRAY_POSITION(...) > 0
        };
    }
}
