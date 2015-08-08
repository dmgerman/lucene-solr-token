begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|IntervalFacet
operator|.
name|Count
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|DistributedIntervalFacetingTest
specifier|public
class|class
name|DistributedIntervalFacetingTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
throws|throws
name|Exception
block|{
name|schemaString
operator|=
literal|"schema-distrib-interval-faceting.xml"
expr_stmt|;
name|configString
operator|=
literal|"solrconfig-basic.xml"
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|testRandom
argument_list|()
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|testSolrJ
argument_list|()
expr_stmt|;
block|}
DECL|method|testSolrJ
specifier|private
name|void
name|testSolrJ
parameter_list|()
throws|throws
name|Exception
block|{
name|indexr
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"test_i_dv"
argument_list|,
literal|"0"
argument_list|,
literal|"test_s_dv"
argument_list|,
literal|"AAA"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"test_i_dv"
argument_list|,
literal|"1"
argument_list|,
literal|"test_s_dv"
argument_list|,
literal|"BBB"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"test_i_dv"
argument_list|,
literal|"2"
argument_list|,
literal|"test_s_dv"
argument_list|,
literal|"AAA"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"test_i_dv"
argument_list|,
literal|"3"
argument_list|,
literal|"test_s_dv"
argument_list|,
literal|"CCC"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|response
init|=
name|controlClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|String
index|[]
name|intervals
init|=
operator|new
name|String
index|[]
block|{
literal|"[0,1)"
block|,
literal|"[1,2)"
block|,
literal|"[2,3)"
block|,
literal|"[3,*)"
block|}
decl_stmt|;
name|q
operator|.
name|addIntervalFacets
argument_list|(
literal|"test_i_dv"
argument_list|,
name|intervals
argument_list|)
expr_stmt|;
name|response
operator|=
name|controlClient
operator|.
name|query
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test_i_dv"
argument_list|,
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIntervals
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIntervals
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Count
name|count
init|=
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIntervals
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|intervals
index|[
name|i
index|]
argument_list|,
name|count
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|q
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|q
operator|.
name|addIntervalFacets
argument_list|(
literal|"test_i_dv"
argument_list|,
name|intervals
argument_list|)
expr_stmt|;
name|q
operator|.
name|addIntervalFacets
argument_list|(
literal|"test_s_dv"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"{!key='AAA'}[AAA,AAA]"
block|,
literal|"{!key='BBB'}[BBB,BBB]"
block|,
literal|"{!key='CCC'}[CCC,CCC]"
block|}
argument_list|)
expr_stmt|;
name|response
operator|=
name|controlClient
operator|.
name|query
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|stringIntervalIndex
init|=
literal|"test_s_dv"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getField
argument_list|()
argument_list|)
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test_i_dv"
argument_list|,
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
operator|-
name|stringIntervalIndex
argument_list|)
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test_s_dv"
argument_list|,
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
name|stringIntervalIndex
argument_list|)
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
operator|-
name|stringIntervalIndex
argument_list|)
operator|.
name|getIntervals
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Count
name|count
init|=
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
operator|-
name|stringIntervalIndex
argument_list|)
operator|.
name|getIntervals
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|intervals
index|[
name|i
index|]
argument_list|,
name|count
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Count
argument_list|>
name|stringIntervals
init|=
name|response
operator|.
name|getIntervalFacets
argument_list|()
operator|.
name|get
argument_list|(
name|stringIntervalIndex
argument_list|)
operator|.
name|getIntervals
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stringIntervals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AAA"
argument_list|,
name|stringIntervals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stringIntervals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BBB"
argument_list|,
name|stringIntervals
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stringIntervals
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"CCC"
argument_list|,
name|stringIntervals
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stringIntervals
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|private
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
comment|// All field values will be a number between 0 and cardinality
name|int
name|cardinality
init|=
literal|1000000
decl_stmt|;
comment|// Fields to use for interval faceting
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[]
block|{
literal|"test_s_dv"
block|,
literal|"test_i_dv"
block|,
literal|"test_l_dv"
block|,
literal|"test_f_dv"
block|,
literal|"test_d_dv"
block|,
literal|"test_ss_dv"
block|,
literal|"test_is_dv"
block|,
literal|"test_fs_dv"
block|,
literal|"test_ls_dv"
block|,
literal|"test_ds_dv"
block|}
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
name|atLeast
argument_list|(
literal|500
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|//have some empty docs
name|indexr
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
operator|&&
name|i
operator|>
literal|0
condition|)
block|{
comment|//delete some docs
name|del
argument_list|(
literal|"id:"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Object
index|[]
name|docFields
init|=
operator|new
name|Object
index|[
operator|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|)
operator|*
literal|10
operator|+
literal|12
index|]
decl_stmt|;
name|docFields
index|[
literal|0
index|]
operator|=
literal|"id"
expr_stmt|;
name|docFields
index|[
literal|1
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|docFields
index|[
literal|2
index|]
operator|=
literal|"test_s_dv"
expr_stmt|;
name|docFields
index|[
literal|3
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cardinality
argument_list|)
argument_list|)
expr_stmt|;
name|docFields
index|[
literal|4
index|]
operator|=
literal|"test_i_dv"
expr_stmt|;
name|docFields
index|[
literal|5
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cardinality
argument_list|)
argument_list|)
expr_stmt|;
name|docFields
index|[
literal|6
index|]
operator|=
literal|"test_l_dv"
expr_stmt|;
name|docFields
index|[
literal|7
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cardinality
argument_list|)
argument_list|)
expr_stmt|;
name|docFields
index|[
literal|8
index|]
operator|=
literal|"test_f_dv"
expr_stmt|;
name|docFields
index|[
literal|9
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
name|cardinality
argument_list|)
expr_stmt|;
name|docFields
index|[
literal|10
index|]
operator|=
literal|"test_d_dv"
expr_stmt|;
name|docFields
index|[
literal|11
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|cardinality
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|12
init|;
name|j
operator|<
name|docFields
operator|.
name|length
condition|;
control|)
block|{
name|docFields
index|[
name|j
operator|++
index|]
operator|=
literal|"test_ss_dv"
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cardinality
argument_list|)
argument_list|)
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
literal|"test_is_dv"
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cardinality
argument_list|)
argument_list|)
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
literal|"test_ls_dv"
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cardinality
argument_list|)
argument_list|)
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
literal|"test_fs_dv"
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
name|cardinality
argument_list|)
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
literal|"test_ds_dv"
expr_stmt|;
name|docFields
index|[
name|j
operator|++
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|cardinality
argument_list|)
expr_stmt|;
block|}
name|indexr
argument_list|(
name|docFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|==
literal|0
condition|)
block|{
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
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
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atLeast
argument_list|(
literal|100
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|doTestQuery
argument_list|(
name|cardinality
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Executes one query using interval faceting and compares with the same query using    * facet query with the same range    */
DECL|method|doTestQuery
specifier|private
name|void
name|doTestQuery
parameter_list|(
name|int
name|cardinality
parameter_list|,
name|String
index|[]
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|startOptions
init|=
operator|new
name|String
index|[]
block|{
literal|"("
block|,
literal|"["
block|}
decl_stmt|;
name|String
index|[]
name|endOptions
init|=
operator|new
name|String
index|[]
block|{
literal|")"
block|,
literal|"]"
block|}
decl_stmt|;
comment|// the query should match some documents in most cases
name|Integer
index|[]
name|qRange
init|=
name|getRandomRange
argument_list|(
name|cardinality
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"q"
argument_list|,
literal|"id:["
operator|+
name|qRange
index|[
literal|0
index|]
operator|+
literal|" TO "
operator|+
name|qRange
index|[
literal|1
index|]
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|String
name|field
init|=
name|fields
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|fields
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
comment|//choose from any of the fields
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
literal|"facet.interval"
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|set
argument_list|(
literal|"facet.interval"
argument_list|,
name|getFieldWithKey
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// number of intervals
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|Integer
index|[]
name|interval
init|=
name|getRandomRange
argument_list|(
name|cardinality
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|String
name|open
init|=
name|startOptions
index|[
name|interval
index|[
literal|0
index|]
operator|%
literal|2
index|]
decl_stmt|;
name|String
name|close
init|=
name|endOptions
index|[
name|interval
index|[
literal|1
index|]
operator|%
literal|2
index|]
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"f."
operator|+
name|field
operator|+
literal|".facet.interval.set"
argument_list|,
name|open
operator|+
name|interval
index|[
literal|0
index|]
operator|+
literal|","
operator|+
name|interval
index|[
literal|1
index|]
operator|+
name|close
argument_list|)
expr_stmt|;
block|}
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldWithKey
specifier|private
name|String
name|getFieldWithKey
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"{!key='_some_key_for_"
operator|+
name|field
operator|+
literal|"_"
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|+
literal|"'}"
operator|+
name|field
return|;
block|}
comment|/**    * Returns a random range. It's guaranteed that the first    * number will be lower than the second, and both of them    * between 0 (inclusive) and<code>max</code> (exclusive).    * If the fieldName is "test_s_dv" or "test_ss_dv" (the    * two fields used for Strings), the comparison will be done    * alphabetically    */
DECL|method|getRandomRange
specifier|private
name|Integer
index|[]
name|getRandomRange
parameter_list|(
name|int
name|max
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|Integer
index|[]
name|values
init|=
operator|new
name|Integer
index|[
literal|2
index|]
decl_stmt|;
name|values
index|[
literal|0
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|values
index|[
literal|1
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"test_s_dv"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"test_ss_dv"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Integer
name|o1
parameter_list|,
name|Integer
name|o2
parameter_list|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|o1
argument_list|)
operator|.
name|compareTo
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o2
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
block|}
end_class

end_unit

