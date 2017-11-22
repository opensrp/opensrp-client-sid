package org.smartregister.bidan_cloudant.repository;

import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

/**
 * Created by sid-tech on 11/22/17.
 */

public class UniqueIdRepository extends BaseRepository {

    public UniqueIdRepository(BidanRepository bidanRepository) {
        super(bidanRepository);
    }
}
