package com.fusiontech.repository;

import com.fusiontech.entity.AddressData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AddressDataElasticsearchRepository extends ElasticsearchRepository<AddressData, String> {
//  Page<AddressData> findAll(Pageable pageable);

//  @Query("""
//    {
//        "bool": {
//            "should": [
//                {
//                    "multi_match": {
//                        "query": "?0"
//                    }
//                },
//                {
//                    "match_phrase": {
//                        "address": {
//                            "query": "?0"
//                        }
//                    }
//                }
//            ]
//        }
//    }
//    """)
  Page<AddressData> getAddressDataByAddress(String address, Pageable pageable);

}
