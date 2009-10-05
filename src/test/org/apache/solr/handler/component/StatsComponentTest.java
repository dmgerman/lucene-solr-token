begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|MapSolrParams
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
name|params
operator|.
name|StatsParams
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
name|SolrInputDocument
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Statistics Component Test  */
end_comment

begin_class
DECL|class|StatsComponentTest
specifier|public
class|class
name|StatsComponentTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema11.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
DECL|method|testStats
specifier|public
name|void
name|testStats
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|f
range|:
operator|new
name|String
index|[]
block|{
literal|"stats_i"
block|}
control|)
block|{
name|doTestFieldStatisticsResult
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|doTestFieldStatisticsMissingResult
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|doTestFacetStatisticsResult
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|doTestFacetStatisticsMissingResult
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|f
range|:
operator|new
name|String
index|[]
block|{
literal|"stats_ii"
block|}
control|)
block|{
name|doTestMVFieldStatisticsResult
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestFieldStatisticsResult
specifier|public
name|void
name|doTestFieldStatisticsResult
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|f
argument_list|,
literal|"-10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|f
argument_list|,
literal|"-20"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|f
argument_list|,
literal|"-30"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|f
argument_list|,
literal|"-40"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test statistics values"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|)
argument_list|,
literal|"//double[@name='min'][.='-40.0']"
argument_list|,
literal|"//double[@name='max'][.='-10.0']"
argument_list|,
literal|"//double[@name='sum'][.='-100.0']"
argument_list|,
literal|"//long[@name='count'][.='4']"
argument_list|,
literal|"//long[@name='missing'][.='0']"
argument_list|,
literal|"//double[@name='sumOfSquares'][.='3000.0']"
argument_list|,
literal|"//double[@name='mean'][.='-25.0']"
argument_list|,
literal|"//double[@name='stddev'][.='12.909944487358056']"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestMVFieldStatisticsResult
specifier|public
name|void
name|doTestMVFieldStatisticsResult
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|f
argument_list|,
literal|"-10"
argument_list|,
name|f
argument_list|,
literal|"-100"
argument_list|,
literal|"active_s"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|f
argument_list|,
literal|"-20"
argument_list|,
name|f
argument_list|,
literal|"200"
argument_list|,
literal|"active_s"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|f
argument_list|,
literal|"-30"
argument_list|,
name|f
argument_list|,
literal|"-1"
argument_list|,
literal|"active_s"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|f
argument_list|,
literal|"-40"
argument_list|,
name|f
argument_list|,
literal|"10"
argument_list|,
literal|"active_s"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"active_s"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test statistics values"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|)
argument_list|,
literal|"//double[@name='min'][.='-100.0']"
argument_list|,
literal|"//double[@name='max'][.='200.0']"
argument_list|,
literal|"//double[@name='sum'][.='9.0']"
argument_list|,
literal|"//long[@name='count'][.='8']"
argument_list|,
literal|"//long[@name='missing'][.='1']"
argument_list|,
literal|"//double[@name='sumOfSquares'][.='53101.0']"
argument_list|,
literal|"//double[@name='mean'][.='1.125']"
argument_list|,
literal|"//double[@name='stddev'][.='87.08852228787508']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test statistics values"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|,
literal|"stats.facet"
argument_list|,
literal|"active_s"
argument_list|)
argument_list|,
literal|"//double[@name='min'][.='-100.0']"
argument_list|,
literal|"//double[@name='max'][.='200.0']"
argument_list|,
literal|"//double[@name='sum'][.='9.0']"
argument_list|,
literal|"//long[@name='count'][.='8']"
argument_list|,
literal|"//long[@name='missing'][.='1']"
argument_list|,
literal|"//double[@name='sumOfSquares'][.='53101.0']"
argument_list|,
literal|"//double[@name='mean'][.='1.125']"
argument_list|,
literal|"//double[@name='stddev'][.='87.08852228787508']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test value for active_s=true"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|,
literal|"stats.facet"
argument_list|,
literal|"active_s"
argument_list|)
argument_list|,
literal|"//lst[@name='true']/double[@name='min'][.='-100.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='max'][.='200.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='sum'][.='70.0']"
argument_list|,
literal|"//lst[@name='true']/long[@name='count'][.='4']"
argument_list|,
literal|"//lst[@name='true']/long[@name='missing'][.='0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='sumOfSquares'][.='50500.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='mean'][.='17.5']"
argument_list|,
literal|"//lst[@name='true']/double[@name='stddev'][.='128.16005617976296']"
argument_list|)
expr_stmt|;
comment|//Test for fixing multivalued missing
comment|/*assertQ("test value for active_s=false", req             , "//lst[@name='false']/double[@name='min'][.='-40.0']"             , "//lst[@name='false']/double[@name='max'][.='10.0']"             , "//lst[@name='false']/double[@name='sum'][.='-61.0']"             , "//lst[@name='false']/long[@name='count'][.='4']"             , "//lst[@name='false']/long[@name='missing'][.='1']"             , "//lst[@name='false']/double[@name='sumOfSquares'][.='2601.0']"             , "//lst[@name='false']/double[@name='mean'][.='-15.22']"             , "//lst[@name='false']/double[@name='stddev'][.='23.59908190304586']"     );*/
block|}
DECL|method|doTestFieldStatisticsMissingResult
specifier|public
name|void
name|doTestFieldStatisticsMissingResult
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|f
argument_list|,
literal|"-10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|f
argument_list|,
literal|"-20"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|f
argument_list|,
literal|"-40"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test statistics values"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|)
argument_list|,
literal|"//double[@name='min'][.='-40.0']"
argument_list|,
literal|"//double[@name='max'][.='-10.0']"
argument_list|,
literal|"//double[@name='sum'][.='-70.0']"
argument_list|,
literal|"//long[@name='count'][.='3']"
argument_list|,
literal|"//long[@name='missing'][.='1']"
argument_list|,
literal|"//double[@name='sumOfSquares'][.='2100.0']"
argument_list|,
literal|"//double[@name='mean'][.='-23.333333333333332']"
argument_list|,
literal|"//double[@name='stddev'][.='15.275252316519467']"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestFacetStatisticsResult
specifier|public
name|void
name|doTestFacetStatisticsResult
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|f
argument_list|,
literal|"10"
argument_list|,
literal|"active_s"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|f
argument_list|,
literal|"20"
argument_list|,
literal|"active_s"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|f
argument_list|,
literal|"30"
argument_list|,
literal|"active_s"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|f
argument_list|,
literal|"40"
argument_list|,
literal|"active_s"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test value for active_s=true"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|,
literal|"stats.facet"
argument_list|,
literal|"active_s"
argument_list|)
argument_list|,
literal|"//lst[@name='true']/double[@name='min'][.='10.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='max'][.='20.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='sum'][.='30.0']"
argument_list|,
literal|"//lst[@name='true']/long[@name='count'][.='2']"
argument_list|,
literal|"//lst[@name='true']/long[@name='missing'][.='0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='sumOfSquares'][.='500.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='mean'][.='15.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='stddev'][.='7.0710678118654755']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test value for active_s=false"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|,
literal|"stats.facet"
argument_list|,
literal|"active_s"
argument_list|)
argument_list|,
literal|"//lst[@name='false']/double[@name='min'][.='30.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='max'][.='40.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='sum'][.='70.0']"
argument_list|,
literal|"//lst[@name='false']/long[@name='count'][.='2']"
argument_list|,
literal|"//lst[@name='false']/long[@name='missing'][.='0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='sumOfSquares'][.='2500.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='mean'][.='35.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='stddev'][.='7.0710678118654755']"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestFacetStatisticsMissingResult
specifier|public
name|void
name|doTestFacetStatisticsMissingResult
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|f
argument_list|,
literal|"10"
argument_list|,
literal|"active_s"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|f
argument_list|,
literal|"20"
argument_list|,
literal|"active_s"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"active_s"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|f
argument_list|,
literal|"40"
argument_list|,
literal|"active_s"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test value for active_s=true"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|,
literal|"stats.facet"
argument_list|,
literal|"active_s"
argument_list|)
argument_list|,
literal|"//lst[@name='true']/double[@name='min'][.='10.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='max'][.='20.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='sum'][.='30.0']"
argument_list|,
literal|"//lst[@name='true']/long[@name='count'][.='2']"
argument_list|,
literal|"//lst[@name='true']/long[@name='missing'][.='0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='sumOfSquares'][.='500.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='mean'][.='15.0']"
argument_list|,
literal|"//lst[@name='true']/double[@name='stddev'][.='7.0710678118654755']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test value for active_s=false"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|f
argument_list|,
literal|"stats.facet"
argument_list|,
literal|"active_s"
argument_list|)
argument_list|,
literal|"//lst[@name='false']/double[@name='min'][.='40.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='max'][.='40.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='sum'][.='40.0']"
argument_list|,
literal|"//lst[@name='false']/long[@name='count'][.='1']"
argument_list|,
literal|"//lst[@name='false']/long[@name='missing'][.='1']"
argument_list|,
literal|"//lst[@name='false']/double[@name='sumOfSquares'][.='1600.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='mean'][.='40.0']"
argument_list|,
literal|"//lst[@name='false']/double[@name='stddev'][.='0.0']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

