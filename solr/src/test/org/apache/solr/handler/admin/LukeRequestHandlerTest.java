begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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

begin_comment
comment|/**  * :TODO: currently only tests some of the utilities in the LukeRequestHandler  */
end_comment

begin_class
DECL|class|LukeRequestHandlerTest
specifier|public
class|class
name|LukeRequestHandlerTest
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
literal|"schema12.xml"
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
comment|/** tests some simple edge cases */
DECL|method|doTestHistogramPowerOfTwoBucket
specifier|public
name|void
name|doTestHistogramPowerOfTwoBucket
parameter_list|()
block|{
name|assertHistoBucket
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|4
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|4
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|8
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|8
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|8
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|16
argument_list|,
literal|9
argument_list|)
expr_stmt|;
specifier|final
name|int
name|MAX_VALID
init|=
operator|(
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|2
operator|)
operator|+
literal|1
operator|)
operator|/
literal|2
decl_stmt|;
name|assertHistoBucket
argument_list|(
name|MAX_VALID
argument_list|,
name|MAX_VALID
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
name|MAX_VALID
argument_list|,
name|MAX_VALID
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
name|MAX_VALID
operator|*
literal|2
argument_list|,
name|MAX_VALID
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|assertHistoBucket
specifier|private
name|void
name|assertHistoBucket
parameter_list|(
name|int
name|expected
parameter_list|,
name|int
name|in
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"histobucket: "
operator|+
name|in
argument_list|,
name|expected
argument_list|,
name|LukeRequestHandler
operator|.
name|TermHistogram
operator|.
name|getPowerOfTwoBucket
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLuke
specifier|public
name|void
name|testLuke
parameter_list|()
block|{
name|doTestHistogramPowerOfTwoBucket
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"SOLR1000"
argument_list|,
literal|"name"
argument_list|,
literal|"Apache Solr"
argument_list|,
literal|"solr_si"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sl"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sf"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sd"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_s"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sI"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sS"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_t"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_tt"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_b"
argument_list|,
literal|"true"
argument_list|,
literal|"solr_i"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_l"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_f"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_d"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_ti"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_tl"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_tf"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_td"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pi"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pl"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pf"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pd"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_dt"
argument_list|,
literal|"2000-01-01T01:01:01Z"
argument_list|,
literal|"solr_tdt"
argument_list|,
literal|"2000-01-01T01:01:01Z"
argument_list|,
literal|"solr_pdt"
argument_list|,
literal|"2000-01-01T01:01:01Z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// test that Luke can handle all of the field types
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"id"
argument_list|,
literal|"SOLR1000"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

