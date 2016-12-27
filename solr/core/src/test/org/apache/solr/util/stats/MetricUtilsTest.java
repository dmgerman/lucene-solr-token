begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Snapshot
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|MetricUtilsTest
specifier|public
class|class
name|MetricUtilsTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testSolrTimerGetSnapshot
specifier|public
name|void
name|testSolrTimerGetSnapshot
parameter_list|()
block|{
comment|// create a timer with up to 100 data points
specifier|final
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|timer
operator|.
name|update
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
comment|// obtain timer metrics
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|MetricUtils
operator|.
name|addMetrics
argument_list|(
name|lst
argument_list|,
name|timer
argument_list|)
expr_stmt|;
comment|// check that expected metrics were obtained
name|assertEquals
argument_list|(
name|lst
operator|.
name|size
argument_list|()
argument_list|,
literal|9
argument_list|)
expr_stmt|;
specifier|final
name|Snapshot
name|snapshot
init|=
name|timer
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
comment|// cannot test avgRequestsPerMinute directly because mean rate changes as time increases!
comment|// assertEquals(lst.get("avgRequestsPerSecond"), timer.getMeanRate());
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"5minRateRequestsPerSecond"
argument_list|)
argument_list|,
name|timer
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"15minRateRequestsPerSecond"
argument_list|)
argument_list|,
name|timer
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"avgTimePerRequest"
argument_list|)
argument_list|,
name|MetricUtils
operator|.
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|getMean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"medianRequestTime"
argument_list|)
argument_list|,
name|MetricUtils
operator|.
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|getMedian
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"75thPcRequestTime"
argument_list|)
argument_list|,
name|MetricUtils
operator|.
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get75thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"95thPcRequestTime"
argument_list|)
argument_list|,
name|MetricUtils
operator|.
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get95thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"99thPcRequestTime"
argument_list|)
argument_list|,
name|MetricUtils
operator|.
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get99thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|get
argument_list|(
literal|"999thPcRequestTime"
argument_list|)
argument_list|,
name|MetricUtils
operator|.
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get999thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
