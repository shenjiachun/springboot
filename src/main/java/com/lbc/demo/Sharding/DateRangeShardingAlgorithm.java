package com.lbc.demo.Sharding;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.lbc.demo.util.DateCtrlUtil;
import io.shardingsphere.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.RangeShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;

public class DateRangeShardingAlgorithm implements RangeShardingAlgorithm<Date> {
    private static final Logger LOG = LoggerFactory.getLogger(DateRangeShardingAlgorithm.class);

    private String logicTableName;

    public DateRangeShardingAlgorithm(String logicTableName) {
        this.logicTableName = logicTableName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Date> shardingValue) {
        Collection<String> result = Lists.newLinkedList();
        Range range = shardingValue.getValueRange();

        Date start = (Date) range.lowerEndpoint();
        result.add(this.logicTableName + "_" + DateCtrlUtil.getYear(start));
        return result;
    }
}
