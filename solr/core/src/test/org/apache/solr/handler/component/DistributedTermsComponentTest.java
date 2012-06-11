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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|BaseDistributedSearchTestCase
import|;
end_import

begin_comment
comment|/**  * Test for TermsComponent distributed querying  *  *  * @since solr 1.5  */
end_comment

begin_class
DECL|class|DistributedTermsComponentTest
specifier|public
class|class
name|DistributedTermsComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|18
argument_list|,
literal|"b_t"
argument_list|,
literal|"snake spider shark snail slug seal"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|19
argument_list|,
literal|"b_t"
argument_list|,
literal|"snake spider shark snail slug"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|20
argument_list|,
literal|"b_t"
argument_list|,
literal|"snake spider shark snail"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|21
argument_list|,
literal|"b_t"
argument_list|,
literal|"snake spider shark"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|22
argument_list|,
literal|"b_t"
argument_list|,
literal|"snake spider"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|23
argument_list|,
literal|"b_t"
argument_list|,
literal|"snake"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|24
argument_list|,
literal|"b_t"
argument_list|,
literal|"ant zebra"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|25
argument_list|,
literal|"b_t"
argument_list|,
literal|"zebra"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"b_t"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|5
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"b_t"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"s"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|5
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"b_t"
argument_list|,
literal|"terms.prefix"
argument_list|,
literal|"sn"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"sn"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|5
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"b_t"
argument_list|,
literal|"terms.prefix"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"sn"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|5
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"b_t"
argument_list|,
literal|"terms.prefix"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.sort"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|5
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"b_t"
argument_list|,
literal|"terms.prefix"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"sn"
argument_list|,
literal|"terms.sort"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"b_t"
argument_list|,
literal|"terms.sort"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

