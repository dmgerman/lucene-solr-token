begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|Collections
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ParametersFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|tdunning
operator|.
name|math
operator|.
name|stats
operator|.
name|AVLTreeDigest
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
name|SolrClient
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
name|SolrException
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
name|hll
operator|.
name|HLL
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
name|JSONTestUtil
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
name|SolrTestCaseHS
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
name|SolrTestCaseJ4
operator|.
name|SuppressPointFields
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|macro
operator|.
name|MacroExpander
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Lucene45"
block|,
literal|"Appending"
block|}
argument_list|)
annotation|@
name|SuppressPointFields
DECL|class|TestJsonFacets
specifier|public
class|class
name|TestJsonFacets
extends|extends
name|SolrTestCaseHS
block|{
DECL|field|servers
specifier|private
specifier|static
name|SolrInstances
name|servers
decl_stmt|;
comment|// for distributed testing
DECL|field|origTableSize
specifier|private
specifier|static
name|int
name|origTableSize
decl_stmt|;
DECL|field|origDefaultFacetMethod
specifier|private
specifier|static
name|FacetField
operator|.
name|FacetMethod
name|origDefaultFacetMethod
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|true
expr_stmt|;
name|origTableSize
operator|=
name|FacetFieldProcessorByHashDV
operator|.
name|MAXIMUM_STARTING_TABLE_SIZE
expr_stmt|;
name|FacetFieldProcessorByHashDV
operator|.
name|MAXIMUM_STARTING_TABLE_SIZE
operator|=
literal|2
expr_stmt|;
comment|// stress test resizing
name|origDefaultFacetMethod
operator|=
name|FacetField
operator|.
name|FacetMethod
operator|.
name|DEFAULT_METHOD
expr_stmt|;
comment|// instead of the following, see the constructor
comment|//FacetField.FacetMethod.DEFAULT_METHOD = rand(FacetField.FacetMethod.values());
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|initServers
specifier|public
specifier|static
name|void
name|initServers
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|servers
operator|==
literal|null
condition|)
block|{
name|servers
operator|=
operator|new
name|SolrInstances
argument_list|(
literal|3
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterTests
specifier|public
specifier|static
name|void
name|afterTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|false
expr_stmt|;
name|FacetFieldProcessorByHashDV
operator|.
name|MAXIMUM_STARTING_TABLE_SIZE
operator|=
name|origTableSize
expr_stmt|;
name|FacetField
operator|.
name|FacetMethod
operator|.
name|DEFAULT_METHOD
operator|=
name|origDefaultFacetMethod
expr_stmt|;
if|if
condition|(
name|servers
operator|!=
literal|null
condition|)
block|{
name|servers
operator|.
name|stop
argument_list|()
expr_stmt|;
name|servers
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// tip: when debugging a test, comment out the @ParametersFactory and edit the constructor to be no-arg
annotation|@
name|ParametersFactory
DECL|method|parameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
block|{
comment|// wrap each enum val in an Object[] and return as Iterable
return|return
parameter_list|()
lambda|->
name|Arrays
operator|.
name|stream
argument_list|(
name|FacetField
operator|.
name|FacetMethod
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|it
lambda|->
operator|new
name|Object
index|[]
block|{
name|it
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|TestJsonFacets
specifier|public
name|TestJsonFacets
parameter_list|(
name|FacetField
operator|.
name|FacetMethod
name|defMethod
parameter_list|)
block|{
name|FacetField
operator|.
name|FacetMethod
operator|.
name|DEFAULT_METHOD
operator|=
name|defMethod
expr_stmt|;
comment|// note: the real default is restored in afterTests
block|}
comment|// attempt to reproduce https://github.com/Heliosearch/heliosearch/issues/33
annotation|@
name|Test
DECL|method|testComplex
specifier|public
name|void
name|testComplex
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|Client
name|client
init|=
name|Client
operator|.
name|localClient
decl_stmt|;
name|double
name|price_low
init|=
literal|11000
decl_stmt|;
name|double
name|price_high
init|=
literal|100000
decl_stmt|;
name|ModifiableSolrParams
name|p
init|=
name|params
argument_list|(
literal|"make_s"
argument_list|,
literal|"make_s"
argument_list|,
literal|"model_s"
argument_list|,
literal|"model_s"
argument_list|,
literal|"price_low"
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|price_low
argument_list|)
argument_list|,
literal|"price_high"
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|price_high
argument_list|)
argument_list|)
decl_stmt|;
name|MacroExpander
name|m
init|=
operator|new
name|MacroExpander
argument_list|(
name|p
operator|.
name|getMap
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|make_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${make_s}"
argument_list|)
decl_stmt|;
name|String
name|model_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${model_s}"
argument_list|)
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|int
name|nDocs
init|=
literal|99
decl_stmt|;
name|String
index|[]
name|makes
init|=
block|{
literal|"honda"
block|,
literal|"toyota"
block|,
literal|"ford"
block|,
literal|null
block|}
decl_stmt|;
name|Double
index|[]
name|prices
init|=
block|{
literal|10000.0
block|,
literal|30000.0
block|,
literal|50000.0
block|,
literal|0.0
block|,
literal|null
block|}
decl_stmt|;
name|String
index|[]
name|honda_models
init|=
block|{
literal|"accord"
block|,
literal|"civic"
block|,
literal|"fit"
block|,
literal|"pilot"
block|,
literal|null
block|}
decl_stmt|;
comment|// make sure this is alphabetized to match tiebreaks in index
name|String
index|[]
name|other_models
init|=
block|{
literal|"z1"
block|,
literal|"z2"
block|,
literal|"z3"
block|,
literal|"z4"
block|,
literal|"z5"
block|,
literal|"z6"
block|,
literal|null
block|}
decl_stmt|;
name|int
name|nHonda
init|=
literal|0
decl_stmt|;
specifier|final
name|int
index|[]
name|honda_model_counts
init|=
operator|new
name|int
index|[
name|honda_models
operator|.
name|length
index|]
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|Double
name|price
init|=
name|rand
argument_list|(
name|prices
argument_list|)
decl_stmt|;
if|if
condition|(
name|price
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
literal|"cost_f"
argument_list|,
name|price
argument_list|)
expr_stmt|;
block|}
name|boolean
name|matches_price
init|=
name|price
operator|!=
literal|null
operator|&&
name|price
operator|>=
name|price_low
operator|&&
name|price
operator|<=
name|price_high
decl_stmt|;
name|String
name|make
init|=
name|rand
argument_list|(
name|makes
argument_list|)
decl_stmt|;
if|if
condition|(
name|make
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|make_s
argument_list|,
name|make
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"honda"
operator|.
name|equals
argument_list|(
name|make
argument_list|)
condition|)
block|{
name|int
name|modelNum
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|honda_models
operator|.
name|length
argument_list|)
decl_stmt|;
name|String
name|model
init|=
name|honda_models
index|[
name|modelNum
index|]
decl_stmt|;
if|if
condition|(
name|model
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|model_s
argument_list|,
name|model
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|matches_price
condition|)
block|{
name|nHonda
operator|++
expr_stmt|;
name|honda_model_counts
index|[
name|modelNum
index|]
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|make
operator|==
literal|null
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|model_s
argument_list|,
name|rand
argument_list|(
name|honda_models
argument_list|)
argument_list|)
expr_stmt|;
comment|// add some docs w/ model but w/o make
block|}
else|else
block|{
comment|// other makes
name|doc
operator|.
name|addField
argument_list|(
name|model_s
argument_list|,
name|rand
argument_list|(
name|other_models
argument_list|)
argument_list|)
expr_stmt|;
comment|// add some docs w/ model but w/o make
block|}
name|client
operator|.
name|add
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
name|client
operator|.
name|add
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// dup, causing a delete
block|}
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|0
condition|)
block|{
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// force new seg
block|}
block|}
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// now figure out top counts
name|List
argument_list|<
name|Integer
argument_list|>
name|idx
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|honda_model_counts
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|idx
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|idx
argument_list|,
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
block|{
name|int
name|cmp
init|=
name|honda_model_counts
index|[
name|o2
index|]
operator|-
name|honda_model_counts
index|[
name|o1
index|]
decl_stmt|;
return|return
name|cmp
operator|==
literal|0
condition|?
name|o1
operator|-
name|o2
else|:
name|cmp
return|;
block|}
argument_list|)
expr_stmt|;
comment|// straight query facets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"fq"
argument_list|,
literal|"+${make_s}:honda +cost_f:[${price_low} TO ${price_high}]"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{makes:{terms:{field:${make_s}, facet:{models:{terms:{field:${model_s}, limit:2, mincount:0}}}}}}}"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"make_s,model_s"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"facets=={count:"
operator|+
name|nHonda
operator|+
literal|", makes:{buckets:[{val:honda, count:"
operator|+
name|nHonda
operator|+
literal|", models:{buckets:["
operator|+
literal|"{val:"
operator|+
name|honda_models
index|[
name|idx
operator|.
name|get
argument_list|(
literal|0
argument_list|)
index|]
operator|+
literal|", count:"
operator|+
name|honda_model_counts
index|[
name|idx
operator|.
name|get
argument_list|(
literal|0
argument_list|)
index|]
operator|+
literal|"},"
operator|+
literal|"{val:"
operator|+
name|honda_models
index|[
name|idx
operator|.
name|get
argument_list|(
literal|1
argument_list|)
index|]
operator|+
literal|", count:"
operator|+
name|honda_model_counts
index|[
name|idx
operator|.
name|get
argument_list|(
literal|1
argument_list|)
index|]
operator|+
literal|"}]}"
operator|+
literal|"}]}}"
argument_list|)
expr_stmt|;
block|}
DECL|method|indexSimple
specifier|public
name|void
name|indexSimple
parameter_list|(
name|Client
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"A"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NY"
argument_list|,
literal|"num_d"
argument_list|,
literal|"4"
argument_list|,
literal|"num_i"
argument_list|,
literal|"2"
argument_list|,
literal|"val_b"
argument_list|,
literal|"true"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"one"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"B"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NJ"
argument_list|,
literal|"num_d"
argument_list|,
literal|"-9"
argument_list|,
literal|"num_i"
argument_list|,
literal|"-5"
argument_list|,
literal|"val_b"
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"A"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NJ"
argument_list|,
literal|"num_d"
argument_list|,
literal|"2"
argument_list|,
literal|"num_i"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"B"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NJ"
argument_list|,
literal|"num_d"
argument_list|,
literal|"11"
argument_list|,
literal|"num_i"
argument_list|,
literal|"7"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"two"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"B"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NY"
argument_list|,
literal|"num_d"
argument_list|,
literal|"-5"
argument_list|,
literal|"num_i"
argument_list|,
literal|"-5"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|testDomainJoinSelf
specifier|public
name|void
name|testDomainJoinSelf
parameter_list|()
throws|throws
name|Exception
block|{
name|Client
name|client
init|=
name|Client
operator|.
name|localClient
argument_list|()
decl_stmt|;
name|indexSimple
argument_list|(
name|client
argument_list|)
expr_stmt|;
comment|// self join domain switch at the second level of faceting
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"json.facet"
argument_list|,
literal|""
operator|+
literal|"{x: { type: terms, field: 'num_i', "
operator|+
literal|"      facet: { y: { domain: { join: { from: 'cat_s', to: 'cat_s' } }, "
operator|+
literal|"                    type: terms, field: 'where_s' "
operator|+
literal|"                  } } } }"
argument_list|)
argument_list|,
literal|"facets=={count:6, x:{ buckets:["
operator|+
literal|"   { val:-5, count:2, "
operator|+
literal|"     y : { buckets:[{ val:'NJ', count:2 }, { val:'NY', count:1 } ] } }, "
operator|+
literal|"   { val:2, count:1, "
operator|+
literal|"     y : { buckets:[{ val:'NJ', count:1 }, { val:'NY', count:1 } ] } }, "
operator|+
literal|"   { val:3, count:1, "
operator|+
literal|"     y : { buckets:[{ val:'NJ', count:1 }, { val:'NY', count:1 } ] } }, "
operator|+
literal|"   { val:7, count:1, "
operator|+
literal|"     y : { buckets:[{ val:'NJ', count:2 }, { val:'NY', count:1 } ] } } ] } }"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedJoinDomain
specifier|public
name|void
name|testNestedJoinDomain
parameter_list|()
throws|throws
name|Exception
block|{
name|Client
name|client
init|=
name|Client
operator|.
name|localClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"1_s"
argument_list|,
literal|"A"
argument_list|,
literal|"2_s"
argument_list|,
literal|"A"
argument_list|,
literal|"3_s"
argument_list|,
literal|"C"
argument_list|,
literal|"y_s"
argument_list|,
literal|"B"
argument_list|,
literal|"x_t"
argument_list|,
literal|"x   z"
argument_list|,
literal|"z_t"
argument_list|,
literal|"  2 3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"1_s"
argument_list|,
literal|"B"
argument_list|,
literal|"2_s"
argument_list|,
literal|"A"
argument_list|,
literal|"3_s"
argument_list|,
literal|"B"
argument_list|,
literal|"y_s"
argument_list|,
literal|"B"
argument_list|,
literal|"x_t"
argument_list|,
literal|"x y  "
argument_list|,
literal|"z_t"
argument_list|,
literal|"1   3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"1_s"
argument_list|,
literal|"C"
argument_list|,
literal|"2_s"
argument_list|,
literal|"A"
argument_list|,
literal|"3_s"
argument_list|,
literal|"#"
argument_list|,
literal|"y_s"
argument_list|,
literal|"A"
argument_list|,
literal|"x_t"
argument_list|,
literal|"  y z"
argument_list|,
literal|"z_t"
argument_list|,
literal|"1 2  "
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"1_s"
argument_list|,
literal|"A"
argument_list|,
literal|"2_s"
argument_list|,
literal|"B"
argument_list|,
literal|"3_s"
argument_list|,
literal|"C"
argument_list|,
literal|"y_s"
argument_list|,
literal|"A"
argument_list|,
literal|"x_t"
argument_list|,
literal|"    z"
argument_list|,
literal|"z_t"
argument_list|,
literal|"    3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"1_s"
argument_list|,
literal|"B"
argument_list|,
literal|"2_s"
argument_list|,
literal|"_"
argument_list|,
literal|"3_s"
argument_list|,
literal|"B"
argument_list|,
literal|"y_s"
argument_list|,
literal|"C"
argument_list|,
literal|"x_t"
argument_list|,
literal|"x    "
argument_list|,
literal|"z_t"
argument_list|,
literal|"1   3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"1_s"
argument_list|,
literal|"C"
argument_list|,
literal|"2_s"
argument_list|,
literal|"B"
argument_list|,
literal|"3_s"
argument_list|,
literal|"A"
argument_list|,
literal|"y_s"
argument_list|,
literal|"C"
argument_list|,
literal|"x_t"
argument_list|,
literal|"x y z"
argument_list|,
literal|"z_t"
argument_list|,
literal|"1    "
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"x_t:x"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
comment|// NOTE q - only x=x in base set (1,2,5,6)
literal|"json.facet"
argument_list|,
literal|""
operator|+
literal|"{x: { type: terms, field: 'x_t', "
operator|+
literal|"      domain: { join: { from:'1_s', to:'2_s' } },"
comment|//                y1& y2 are the same facet, with *similar* child facet z1/z2 ...
operator|+
literal|"      facet: { y1: { type: terms, field: 'y_s', "
comment|//                               z1& z2 are same field, diff join...
operator|+
literal|"                     facet: { z1: { type: terms, field: 'z_t', "
operator|+
literal|"                                    domain: { join: { from:'2_s', to:'3_s' } } } } },"
operator|+
literal|"               y2: { type: terms, field: 'y_s', "
comment|//                               z1& z2 are same field, diff join...
operator|+
literal|"                     facet: { z2: { type: terms, field: 'z_t', "
operator|+
literal|"                                    domain: { join: { from:'3_s', to:'1_s' } } } } } } } }"
argument_list|)
argument_list|,
literal|"facets=={count:4, "
operator|+
literal|"x:{ buckets:["
comment|// joined 1->2: doc5 drops out, counts: z=4, x=3, y=3
operator|+
literal|"   { val:z, count:4, "
comment|// x=z (docs 1,3,4,6) y terms: A=2, B=1, C=1
operator|+
literal|"     y1 : { buckets:[ "
comment|// z1 joins 2->3...
operator|+
literal|"             { val:A, count:2, "
comment|// A in docs(3,4), joins (A,B) -> docs(2,5,6)
operator|+
literal|"               z1: { buckets:[{ val:'1', count:3 }, { val:'3', count:2 }] } }, "
operator|+
literal|"             { val:B, count:1, "
comment|// B in doc1, joins A -> doc6
operator|+
literal|"               z1: { buckets:[{ val:'1', count:1 }] } }, "
operator|+
literal|"             { val:C, count:1, "
comment|// C in doc6, joins B -> docs(2,5)
operator|+
literal|"               z1: { buckets:[{ val:'1', count:2 }, { val:'3', count:2 }] } } "
operator|+
literal|"          ] }, "
operator|+
literal|"     y2 : { buckets:[ "
comment|// z2 joins 3->1...
operator|+
literal|"             { val:A, count:2, "
comment|// A in docs(3,4), joins C -> docs(3,6)
operator|+
literal|"               z2: { buckets:[{ val:'1', count:2 }, { val:'2', count:1 }] } }, "
operator|+
literal|"             { val:B, count:1, "
comment|// B in doc1, joins C -> docs(3,6)
operator|+
literal|"               z2: { buckets:[{ val:'1', count:2 }, { val:'2', count:1 }] } }, "
operator|+
literal|"             { val:C, count:1, "
comment|// C in doc6, joins A -> docs(1,4)
operator|+
literal|"               z2: { buckets:[{ val:'3', count:2 }, { val:'2', count:1 }] } } "
operator|+
literal|"          ] } }, "
operator|+
literal|"   { val:x, count:3, "
comment|// x=x (docs 1,2,!5,6) y terms: B=2, C=1
operator|+
literal|"     y1 : { buckets:[ "
comment|// z1 joins 2->3...
operator|+
literal|"             { val:B, count:2, "
comment|// B in docs(1,2), joins A -> doc6
operator|+
literal|"               z1: { buckets:[{ val:'1', count:1 }] } }, "
operator|+
literal|"             { val:C, count:1, "
comment|// C in doc6, joins B -> docs(2,5)
operator|+
literal|"               z1: { buckets:[{ val:'1', count:2 }, { val:'3', count:2 }] } } "
operator|+
literal|"          ] }, "
operator|+
literal|"     y2 : { buckets:[ "
comment|// z2 joins 3->1...
operator|+
literal|"             { val:B, count:2, "
comment|// B in docs(1,2), joins C,B -> docs(2,3,5,6)
operator|+
literal|"               z2: { buckets:[{ val:'1', count:4 }, { val:'3', count:2 }, { val:'2', count:1 }] } }, "
operator|+
literal|"             { val:C, count:1, "
comment|// C in doc6, joins A -> docs(1,4)
operator|+
literal|"               z2: { buckets:[{ val:'3', count:2 }, { val:'2', count:1 }] } } "
operator|+
literal|"          ] } }, "
operator|+
literal|"   { val:y, count:3, "
comment|// x=y (docs 2,3,6) y terms: A=1, B=1, C=1
operator|+
literal|"     y1 : { buckets:[ "
comment|// z1 joins 2->3...
operator|+
literal|"             { val:A, count:1, "
comment|// A in doc3, joins A -> doc6
operator|+
literal|"               z1: { buckets:[{ val:'1', count:1 }] } }, "
operator|+
literal|"             { val:B, count:1, "
comment|// B in doc2, joins A -> doc6
operator|+
literal|"               z1: { buckets:[{ val:'1', count:1 }] } }, "
operator|+
literal|"             { val:C, count:1, "
comment|// C in doc6, joins B -> docs(2,5)
operator|+
literal|"               z1: { buckets:[{ val:'1', count:2 }, { val:'3', count:2 }] } } "
operator|+
literal|"          ] }, "
operator|+
literal|"     y2 : { buckets:[ "
comment|// z2 joins 3->1...
operator|+
literal|"             { val:A, count:1, "
comment|// A in doc3, joins # -> empty set
operator|+
literal|"               z2: { buckets:[ ] } }, "
operator|+
literal|"             { val:B, count:1, "
comment|// B in doc2, joins B -> docs(2,5)
operator|+
literal|"               z2: { buckets:[{ val:'1', count:2 }, { val:'3', count:2 }] } }, "
operator|+
literal|"             { val:C, count:1, "
comment|// C in doc6, joins A -> docs(1,4)
operator|+
literal|"               z2: { buckets:[{ val:'3', count:2 }, { val:'2', count:1 }] } } "
operator|+
literal|"          ]}  }"
operator|+
literal|"   ]}}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMethodStream
specifier|public
name|void
name|testMethodStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Client
name|client
init|=
name|Client
operator|.
name|localClient
argument_list|()
decl_stmt|;
name|indexSimple
argument_list|(
name|client
argument_list|)
expr_stmt|;
comment|// test multiple json.facet commands
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{x:'sum(num_d)'}"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{y:'min(num_d)'}"
argument_list|)
argument_list|,
literal|"facets=={count:6 , x:3.0, y:-9.0 }"
argument_list|)
expr_stmt|;
comment|// test streaming
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{   cat:{terms:{field:'cat_s', method:stream }}"
operator|+
comment|// won't stream; need sort:index asc
literal|", cat2:{terms:{field:'cat_s', method:stream, sort:'index asc' }}"
operator|+
literal|", cat3:{terms:{field:'cat_s', method:stream, sort:'index asc', mincount:3 }}"
operator|+
comment|// mincount
literal|", cat4:{terms:{field:'cat_s', method:stream, sort:'index asc', prefix:B }}"
operator|+
comment|// prefix
literal|", cat5:{terms:{field:'cat_s', method:stream, sort:'index asc', offset:1 }}"
operator|+
comment|// offset
literal|" }"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|", cat :{buckets:[{val:B, count:3},{val:A, count:2}]}"
operator|+
literal|", cat2:{buckets:[{val:A, count:2},{val:B, count:3}]}"
operator|+
literal|", cat3:{buckets:[{val:B, count:3}]}"
operator|+
literal|", cat4:{buckets:[{val:B, count:3}]}"
operator|+
literal|", cat5:{buckets:[{val:B, count:3}]}"
operator|+
literal|" }"
argument_list|)
expr_stmt|;
comment|// test nested streaming under non-streaming
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{   cat:{terms:{field:'cat_s', sort:'index asc', facet:{where:{terms:{field:where_s,method:stream,sort:'index asc'}}}   }}}"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|", cat :{buckets:[{val:A, count:2, where:{buckets:[{val:NJ,count:1},{val:NY,count:1}]}   },{val:B, count:3, where:{buckets:[{val:NJ,count:2},{val:NY,count:1}]}    }]}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test nested streaming under streaming
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{   cat:{terms:{field:'cat_s', method:stream,sort:'index asc', facet:{where:{terms:{field:where_s,method:stream,sort:'index asc'}}}   }}}"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|", cat :{buckets:[{val:A, count:2, where:{buckets:[{val:NJ,count:1},{val:NY,count:1}]}   },{val:B, count:3, where:{buckets:[{val:NJ,count:2},{val:NY,count:1}]}    }]}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test nested streaming with stats under streaming
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{   cat:{terms:{field:'cat_s', method:stream,sort:'index asc', facet:{  where:{terms:{field:where_s,method:stream,sort:'index asc',sort:'index asc', facet:{x:'max(num_d)'}     }}}   }}}"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|", cat :{buckets:[{val:A, count:2, where:{buckets:[{val:NJ,count:1,x:2.0},{val:NY,count:1,x:4.0}]}   },{val:B, count:3, where:{buckets:[{val:NJ,count:2,x:11.0},{val:NY,count:1,x:-5.0}]}    }]}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test nested streaming with stats under streaming with stats
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{   cat:{terms:{field:'cat_s', method:stream,sort:'index asc', facet:{ y:'min(num_d)',  where:{terms:{field:where_s,method:stream,sort:'index asc', facet:{x:'max(num_d)'}     }}}   }}}"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|", cat :{buckets:[{val:A, count:2, y:2.0, where:{buckets:[{val:NJ,count:1,x:2.0},{val:NY,count:1,x:4.0}]}   },{val:B, count:3, y:-9.0, where:{buckets:[{val:NJ,count:2,x:11.0},{val:NY,count:1,x:-5.0}]}    }]}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"cat_s:A"
argument_list|)
argument_list|,
literal|"response/numFound==2"
argument_list|)
expr_stmt|;
block|}
DECL|field|suffixMap
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|suffixMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
block|{
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_s"
block|,
literal|"_ss"
block|,
literal|"_sd"
block|,
literal|"_sds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_ss"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_ss"
block|,
literal|"_sds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_l"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_l"
block|,
literal|"_ls"
block|,
literal|"_ld"
block|,
literal|"_lds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_ls"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_ls"
block|,
literal|"_lds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_i"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_i"
block|,
literal|"_is"
block|,
literal|"_id"
block|,
literal|"_ids"
block|,
literal|"_l"
block|,
literal|"_ls"
block|,
literal|"_ld"
block|,
literal|"_lds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_is"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_is"
block|,
literal|"_ids"
block|,
literal|"_ls"
block|,
literal|"_lds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_d"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_d"
block|,
literal|"_ds"
block|,
literal|"_dd"
block|,
literal|"_dds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_ds"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_ds"
block|,
literal|"_dds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_f"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_f"
block|,
literal|"_fs"
block|,
literal|"_fd"
block|,
literal|"_fds"
block|,
literal|"_d"
block|,
literal|"_ds"
block|,
literal|"_dd"
block|,
literal|"_dds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_fs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_fs"
block|,
literal|"_fds"
block|,
literal|"_ds"
block|,
literal|"_dds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_dt"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_dt"
block|,
literal|"_dts"
block|,
literal|"_dtd"
block|,
literal|"_dtds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_dts"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_dts"
block|,
literal|"_dtds"
block|}
argument_list|)
expr_stmt|;
name|suffixMap
operator|.
name|put
argument_list|(
literal|"_b"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"_b"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getAlternatives
name|List
argument_list|<
name|String
argument_list|>
name|getAlternatives
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|int
name|idx
init|=
name|field
operator|.
name|lastIndexOf
argument_list|(
literal|"_"
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<=
literal|0
operator|||
name|idx
operator|>=
name|field
operator|.
name|length
argument_list|()
condition|)
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|field
argument_list|)
return|;
name|String
name|suffix
init|=
name|field
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|String
index|[]
name|alternativeSuffixes
init|=
name|suffixMap
operator|.
name|get
argument_list|(
name|suffix
argument_list|)
decl_stmt|;
if|if
condition|(
name|alternativeSuffixes
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|field
argument_list|)
return|;
name|String
name|base
init|=
name|field
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|out
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|alternativeSuffixes
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|altS
range|:
name|alternativeSuffixes
control|)
block|{
name|out
operator|.
name|add
argument_list|(
name|base
operator|+
name|altS
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|out
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Test
DECL|method|testStats
specifier|public
name|void
name|testStats
parameter_list|()
throws|throws
name|Exception
block|{
name|doStats
argument_list|(
name|Client
operator|.
name|localClient
argument_list|,
name|params
argument_list|(
literal|"debugQuery"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatsDistrib
specifier|public
name|void
name|testStatsDistrib
parameter_list|()
throws|throws
name|Exception
block|{
name|initServers
argument_list|()
expr_stmt|;
name|Client
name|client
init|=
name|servers
operator|.
name|getClient
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|queryDefaults
argument_list|()
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|servers
operator|.
name|getShards
argument_list|()
argument_list|,
literal|"debugQuery"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doStats
argument_list|(
name|client
argument_list|,
name|params
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doStats
specifier|public
name|void
name|doStats
parameter_list|(
name|Client
name|client
parameter_list|,
name|ModifiableSolrParams
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldLists
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"noexist"
argument_list|,
name|getAlternatives
argument_list|(
literal|"noexist_s"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"cat_s"
argument_list|,
name|getAlternatives
argument_list|(
literal|"cat_s"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"where_s"
argument_list|,
name|getAlternatives
argument_list|(
literal|"where_s"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"num_d"
argument_list|,
name|getAlternatives
argument_list|(
literal|"num_f"
argument_list|)
argument_list|)
expr_stmt|;
comment|// num_d name is historical, which is why we map it to num_f alternatives so we can include floats as well
name|fieldLists
operator|.
name|put
argument_list|(
literal|"num_i"
argument_list|,
name|getAlternatives
argument_list|(
literal|"num_i"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"super_s"
argument_list|,
name|getAlternatives
argument_list|(
literal|"super_s"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"val_b"
argument_list|,
name|getAlternatives
argument_list|(
literal|"val_b"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"date"
argument_list|,
name|getAlternatives
argument_list|(
literal|"date_dt"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"sparse_s"
argument_list|,
name|getAlternatives
argument_list|(
literal|"sparse_s"
argument_list|)
argument_list|)
expr_stmt|;
name|fieldLists
operator|.
name|put
argument_list|(
literal|"multi_ss"
argument_list|,
name|getAlternatives
argument_list|(
literal|"multi_ss"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: if a field will be used as a function source, we can't use multi-valued types for it (currently)
name|int
name|maxAlt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|fieldList
range|:
name|fieldLists
operator|.
name|values
argument_list|()
control|)
block|{
name|maxAlt
operator|=
name|Math
operator|.
name|max
argument_list|(
name|fieldList
operator|.
name|size
argument_list|()
argument_list|,
name|maxAlt
argument_list|)
expr_stmt|;
block|}
comment|// take the field with the maximum number of alternative types and loop through our variants that many times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxAlt
condition|;
name|i
operator|++
control|)
block|{
name|ModifiableSolrParams
name|args
init|=
name|params
argument_list|(
name|p
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fieldLists
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|alts
init|=
name|fieldLists
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|String
name|alt
init|=
name|alts
operator|.
name|get
argument_list|(
name|i
operator|%
name|alts
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|alt
argument_list|)
expr_stmt|;
block|}
name|args
operator|.
name|set
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
comment|// doStatsTemplated(client, args);
block|}
comment|// single valued strings
name|doStatsTemplated
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|p
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"noexist"
argument_list|,
literal|"noexist_s"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"where_s"
argument_list|,
literal|"where_s"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_i"
argument_list|,
literal|"super_s"
argument_list|,
literal|"super_s"
argument_list|,
literal|"val_b"
argument_list|,
literal|"val_b"
argument_list|,
literal|"date"
argument_list|,
literal|"date_dt"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"multi_ss"
argument_list|,
literal|"multi_ss"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multi-valued strings, long/float substitute for int/double
name|doStatsTemplated
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|p
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"noexist"
argument_list|,
literal|"noexist_ss"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"cat_ss"
argument_list|,
literal|"where_s"
argument_list|,
literal|"where_ss"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_f"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_l"
argument_list|,
literal|"num_is"
argument_list|,
literal|"num_ls"
argument_list|,
literal|"num_fs"
argument_list|,
literal|"num_ds"
argument_list|,
literal|"super_s"
argument_list|,
literal|"super_ss"
argument_list|,
literal|"val_b"
argument_list|,
literal|"val_b"
argument_list|,
literal|"date"
argument_list|,
literal|"date_dt"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"sparse_ss"
argument_list|,
literal|"multi_ss"
argument_list|,
literal|"multi_ss"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multi-valued strings, method=dv for terms facets
name|doStatsTemplated
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|p
argument_list|,
literal|"terms"
argument_list|,
literal|"method:dv,"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"noexist"
argument_list|,
literal|"noexist_ss"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"cat_ss"
argument_list|,
literal|"where_s"
argument_list|,
literal|"where_ss"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_f"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_l"
argument_list|,
literal|"super_s"
argument_list|,
literal|"super_ss"
argument_list|,
literal|"val_b"
argument_list|,
literal|"val_b"
argument_list|,
literal|"date"
argument_list|,
literal|"date_dt"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"sparse_ss"
argument_list|,
literal|"multi_ss"
argument_list|,
literal|"multi_ss"
argument_list|)
argument_list|)
expr_stmt|;
comment|// single valued docvalues for strings, and single valued numeric doc values for numeric fields
name|doStatsTemplated
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|p
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"noexist"
argument_list|,
literal|"noexist_sd"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"cat_sd"
argument_list|,
literal|"where_s"
argument_list|,
literal|"where_sd"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_dd"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_id"
argument_list|,
literal|"num_is"
argument_list|,
literal|"num_lds"
argument_list|,
literal|"num_fs"
argument_list|,
literal|"num_dds"
argument_list|,
literal|"super_s"
argument_list|,
literal|"super_sd"
argument_list|,
literal|"val_b"
argument_list|,
literal|"val_b"
argument_list|,
literal|"date"
argument_list|,
literal|"date_dtd"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"sparse_sd"
argument_list|,
literal|"multi_ss"
argument_list|,
literal|"multi_sds"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multi-valued docvalues
name|FacetFieldProcessorByArrayDV
operator|.
name|unwrap_singleValued_multiDv
operator|=
literal|false
expr_stmt|;
comment|// better multi-valued coverage
name|doStatsTemplated
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|p
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"noexist"
argument_list|,
literal|"noexist_sds"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"cat_sds"
argument_list|,
literal|"where_s"
argument_list|,
literal|"where_sds"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_is"
argument_list|,
literal|"num_ids"
argument_list|,
literal|"num_fs"
argument_list|,
literal|"num_fds"
argument_list|,
literal|"super_s"
argument_list|,
literal|"super_sds"
argument_list|,
literal|"val_b"
argument_list|,
literal|"val_b"
argument_list|,
literal|"date"
argument_list|,
literal|"date_dtds"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"sparse_sds"
argument_list|,
literal|"multi_ss"
argument_list|,
literal|"multi_sds"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multi-valued docvalues
name|FacetFieldProcessorByArrayDV
operator|.
name|unwrap_singleValued_multiDv
operator|=
literal|true
expr_stmt|;
name|doStatsTemplated
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|p
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"noexist"
argument_list|,
literal|"noexist_sds"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"cat_sds"
argument_list|,
literal|"where_s"
argument_list|,
literal|"where_sds"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_i"
argument_list|,
literal|"num_is"
argument_list|,
literal|"num_ids"
argument_list|,
literal|"num_fs"
argument_list|,
literal|"num_fds"
argument_list|,
literal|"super_s"
argument_list|,
literal|"super_sds"
argument_list|,
literal|"val_b"
argument_list|,
literal|"val_b"
argument_list|,
literal|"date"
argument_list|,
literal|"date_dtds"
argument_list|,
literal|"sparse_s"
argument_list|,
literal|"sparse_sds"
argument_list|,
literal|"multi_ss"
argument_list|,
literal|"multi_sds"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doStatsTemplated
specifier|public
specifier|static
name|void
name|doStatsTemplated
parameter_list|(
name|Client
name|client
parameter_list|,
name|ModifiableSolrParams
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|p
operator|.
name|set
argument_list|(
literal|"Z_num_i"
argument_list|,
literal|"Z_"
operator|+
name|p
operator|.
name|get
argument_list|(
literal|"num_i"
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|set
argument_list|(
literal|"sparse_num_d"
argument_list|,
literal|"sparse_"
operator|+
name|p
operator|.
name|get
argument_list|(
literal|"num_d"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|get
argument_list|(
literal|"num_is"
argument_list|)
operator|==
literal|null
condition|)
name|p
operator|.
name|add
argument_list|(
literal|"num_is"
argument_list|,
literal|"num_is"
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|get
argument_list|(
literal|"num_fs"
argument_list|)
operator|==
literal|null
condition|)
name|p
operator|.
name|add
argument_list|(
literal|"num_fs"
argument_list|,
literal|"num_fs"
argument_list|)
expr_stmt|;
name|MacroExpander
name|m
init|=
operator|new
name|MacroExpander
argument_list|(
name|p
operator|.
name|getMap
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|cat_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${cat_s}"
argument_list|)
decl_stmt|;
name|String
name|where_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${where_s}"
argument_list|)
decl_stmt|;
name|String
name|num_d
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${num_d}"
argument_list|)
decl_stmt|;
name|String
name|num_i
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${num_i}"
argument_list|)
decl_stmt|;
name|String
name|num_is
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${num_is}"
argument_list|)
decl_stmt|;
name|String
name|num_fs
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${num_fs}"
argument_list|)
decl_stmt|;
name|String
name|Z_num_i
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${Z_num_i}"
argument_list|)
decl_stmt|;
name|String
name|val_b
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${val_b}"
argument_list|)
decl_stmt|;
name|String
name|date
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${date}"
argument_list|)
decl_stmt|;
name|String
name|super_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${super_s}"
argument_list|)
decl_stmt|;
name|String
name|sparse_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${sparse_s}"
argument_list|)
decl_stmt|;
name|String
name|multi_ss
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${multi_ss}"
argument_list|)
decl_stmt|;
name|String
name|sparse_num_d
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${sparse_num_d}"
argument_list|)
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Client
name|iclient
init|=
name|client
decl_stmt|;
comment|/*** This code was not needed yet, but may be needed if we want to force empty shard results more often.     // create a new indexing client that doesn't use one shard to better test for empty or non-existent results     if (!client.local()) {       List<SolrClient> shards = client.getClientProvider().all();       iclient = new Client(shards.subList(0, shards.size()-1), client.getClientProvider().getSeed());      }      ***/
name|SolrInputDocument
name|doc
init|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|,
name|where_s
argument_list|,
literal|"NY"
argument_list|,
name|num_d
argument_list|,
literal|"4"
argument_list|,
name|sparse_num_d
argument_list|,
literal|"6"
argument_list|,
name|num_i
argument_list|,
literal|"2"
argument_list|,
name|num_is
argument_list|,
literal|"2"
argument_list|,
name|num_is
argument_list|,
literal|"-5"
argument_list|,
name|num_fs
argument_list|,
literal|"2"
argument_list|,
name|num_fs
argument_list|,
literal|"-5"
argument_list|,
name|super_s
argument_list|,
literal|"zodiac"
argument_list|,
name|date
argument_list|,
literal|"2001-01-01T01:01:01Z"
argument_list|,
name|val_b
argument_list|,
literal|"true"
argument_list|,
name|sparse_s
argument_list|,
literal|"one"
argument_list|)
decl_stmt|;
name|iclient
operator|.
name|add
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iclient
operator|.
name|add
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iclient
operator|.
name|add
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// a couple of deleted docs
name|iclient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|,
name|where_s
argument_list|,
literal|"NJ"
argument_list|,
name|num_d
argument_list|,
literal|"-9"
argument_list|,
name|num_i
argument_list|,
literal|"-5"
argument_list|,
name|num_is
argument_list|,
literal|"3"
argument_list|,
name|num_is
argument_list|,
literal|"-1"
argument_list|,
name|num_fs
argument_list|,
literal|"3"
argument_list|,
name|num_fs
argument_list|,
literal|"-1.5"
argument_list|,
name|super_s
argument_list|,
literal|"superman"
argument_list|,
name|date
argument_list|,
literal|"2002-02-02T02:02:02Z"
argument_list|,
name|val_b
argument_list|,
literal|"false"
argument_list|,
name|multi_ss
argument_list|,
literal|"a"
argument_list|,
name|multi_ss
argument_list|,
literal|"b"
argument_list|,
name|Z_num_i
argument_list|,
literal|"0"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iclient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iclient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iclient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|,
name|where_s
argument_list|,
literal|"NJ"
argument_list|,
name|num_d
argument_list|,
literal|"2"
argument_list|,
name|sparse_num_d
argument_list|,
literal|"-4"
argument_list|,
name|num_i
argument_list|,
literal|"3"
argument_list|,
name|num_is
argument_list|,
literal|"0"
argument_list|,
name|num_is
argument_list|,
literal|"3"
argument_list|,
name|num_fs
argument_list|,
literal|"0"
argument_list|,
name|num_fs
argument_list|,
literal|"3"
argument_list|,
name|super_s
argument_list|,
literal|"spiderman"
argument_list|,
name|date
argument_list|,
literal|"2003-03-03T03:03:03Z"
argument_list|,
name|multi_ss
argument_list|,
literal|"b"
argument_list|,
name|Z_num_i
argument_list|,
literal|""
operator|+
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iclient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|,
name|where_s
argument_list|,
literal|"NJ"
argument_list|,
name|num_d
argument_list|,
literal|"11"
argument_list|,
name|num_i
argument_list|,
literal|"7"
argument_list|,
name|num_is
argument_list|,
literal|"0"
argument_list|,
name|num_fs
argument_list|,
literal|"0"
argument_list|,
name|super_s
argument_list|,
literal|"batman"
argument_list|,
name|date
argument_list|,
literal|"2001-02-03T01:02:03Z"
argument_list|,
name|sparse_s
argument_list|,
literal|"two"
argument_list|,
name|multi_ss
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iclient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iclient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|,
name|where_s
argument_list|,
literal|"NY"
argument_list|,
name|num_d
argument_list|,
literal|"-5"
argument_list|,
name|num_i
argument_list|,
literal|"-5"
argument_list|,
name|num_is
argument_list|,
literal|"-1"
argument_list|,
name|num_fs
argument_list|,
literal|"-1.5"
argument_list|,
name|super_s
argument_list|,
literal|"hulk"
argument_list|,
name|date
argument_list|,
literal|"2002-03-01T03:02:01Z"
argument_list|,
name|multi_ss
argument_list|,
literal|"b"
argument_list|,
name|multi_ss
argument_list|,
literal|"a"
argument_list|,
name|Z_num_i
argument_list|,
literal|""
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iclient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// test for presence of debugging info
name|ModifiableSolrParams
name|debugP
init|=
name|params
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|debugP
operator|.
name|set
argument_list|(
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|debugP
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{catA:{query:{q:'${cat_s}:A'}},  catA2:{query:{query:'${cat_s}:A'}},  catA3:{query:'${cat_s}:A'}    }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, 'catA':{ 'count':2}, 'catA2':{ 'count':2}, 'catA3':{ 'count':2}}"
argument_list|,
literal|"debug/facet-trace=="
comment|// just test for presence, not exact structure / values
argument_list|)
expr_stmt|;
comment|// straight query facets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{catA:{query:{q:'${cat_s}:A'}},  catA2:{query:{query:'${cat_s}:A'}},  catA3:{query:'${cat_s}:A'}    }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, 'catA':{ 'count':2}, 'catA2':{ 'count':2}, 'catA3':{ 'count':2}}"
argument_list|)
expr_stmt|;
comment|// nested query facets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ catB:{type:query, q:'${cat_s}:B', facet:{nj:{query:'${where_s}:NJ'}, ny:{query:'${where_s}:NY'}} }}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, 'catB':{'count':3, 'nj':{'count':2}, 'ny':{'count':1}}}"
argument_list|)
expr_stmt|;
comment|// nested query facets on subset
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:(2 3)"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ catB:{query:{q:'${cat_s}:B', facet:{nj:{query:'${where_s}:NJ'}, ny:{query:'${where_s}:NY'}} }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':2, 'catB':{'count':1, 'nj':{'count':1}, 'ny':{'count':0}}}"
argument_list|)
expr_stmt|;
comment|// nested query facets with stats
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ catB:{query:{q:'${cat_s}:B', facet:{nj:{query:{q:'${where_s}:NJ'}}, ny:{query:'${where_s}:NY'}} }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, 'catB':{'count':3, 'nj':{'count':2}, 'ny':{'count':1}}}"
argument_list|)
expr_stmt|;
comment|// field/terms facet
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{c1:{field:'${cat_s}'}, c2:{field:{field:'${cat_s}'}}, c3:{${terms} type:terms, field:'${cat_s}'}  }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'c1':{ 'buckets':[{ 'val':'B', 'count':3}, { 'val':'A', 'count':2}]}, "
operator|+
literal|"'c2':{ 'buckets':[{ 'val':'B', 'count':3}, { 'val':'A', 'count':2}]}, "
operator|+
literal|"'c3':{ 'buckets':[{ 'val':'B', 'count':3}, { 'val':'A', 'count':2}]}} "
argument_list|)
expr_stmt|;
comment|// test mincount
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:'${cat_s}', mincount:3}}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{  'buckets':[{ 'val':'B', 'count':3}]} } "
argument_list|)
expr_stmt|;
comment|// test default mincount of 1
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:'${cat_s}'}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':1, "
operator|+
literal|"'f1':{  'buckets':[{ 'val':'A', 'count':1}]} } "
argument_list|)
expr_stmt|;
comment|// test  mincount of 0 - need processEmpty for distrib to match up
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{processEmpty:true, f1:{terms:{${terms} field:'${cat_s}', mincount:0}}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':1, "
operator|+
literal|"'f1':{  'buckets':[{ 'val':'A', 'count':1}, { 'val':'B', 'count':0}]} } "
argument_list|)
expr_stmt|;
comment|// test  mincount of 0 with stats, need processEmpty for distrib to match up
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{processEmpty:true, f1:{terms:{${terms} field:'${cat_s}', mincount:0, allBuckets:true, facet:{n1:'sum(${num_d})'}  }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':1, "
operator|+
literal|"'f1':{ allBuckets:{ 'count':1, n1:4.0}, 'buckets':[{ 'val':'A', 'count':1, n1:4.0}, { 'val':'B', 'count':0 /*, n1:0.0 */ }]} } "
argument_list|)
expr_stmt|;
comment|// test sorting by other stats
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:'${cat_s}', sort:'n1 desc', facet:{n1:'sum(${num_d})'}  }}"
operator|+
literal|" , f2:{terms:{${terms} field:'${cat_s}', sort:'n1 asc', facet:{n1:'sum(${num_d})'}  }} }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"  f1:{  'buckets':[{ val:'A', count:2, n1:6.0 }, { val:'B', count:3, n1:-3.0}]}"
operator|+
literal|", f2:{  'buckets':[{ val:'B', count:3, n1:-3.0}, { val:'A', count:2, n1:6.0 }]} }"
argument_list|)
expr_stmt|;
comment|// test sorting by other stats
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{${terms} type:terms, field:'${cat_s}', sort:'x desc', facet:{x:'min(${num_d})'}  }"
operator|+
literal|" , f2:{${terms} type:terms, field:'${cat_s}', sort:'x desc', facet:{x:'max(${num_d})'}  } "
operator|+
literal|" , f3:{${terms} type:terms, field:'${cat_s}', sort:'x desc', facet:{x:'unique(${where_s})'}  } "
operator|+
literal|" , f4:{${terms} type:terms, field:'${cat_s}', sort:'x desc', facet:{x:'hll(${where_s})'}  } "
operator|+
literal|" , f5:{${terms} type:terms, field:'${cat_s}', sort:'x desc', facet:{x:'variance(${num_d})'}  } "
operator|+
literal|" , f6:{type:terms, field:${num_d}, limit:1, sort:'x desc', facet:{x:'hll(${num_i})'}  } "
operator|+
comment|// facet on a field that will cause hashing and exercise hll.resize on numeric field
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"  f1:{  'buckets':[{ val:'A', count:2, x:2.0 },  { val:'B', count:3, x:-9.0}]}"
operator|+
literal|", f2:{  'buckets':[{ val:'B', count:3, x:11.0 }, { val:'A', count:2, x:4.0 }]} "
operator|+
literal|", f3:{  'buckets':[{ val:'A', count:2, x:2 },    { val:'B', count:3, x:2 }]} "
operator|+
literal|", f4:{  'buckets':[{ val:'A', count:2, x:2 },    { val:'B', count:3, x:2 }]} "
operator|+
literal|", f5:{  'buckets':[{ val:'B', count:3, x:74.6666666666666 },    { val:'A', count:2, x:1.0 }]} "
operator|+
literal|", f6:{  buckets:[{ val:-9.0, count:1, x:1 }]} "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test sorting by stat with function
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:'${cat_s}', sort:'n1 desc', facet:{n1:'avg(add(${num_d},${num_d}))'}  }}"
operator|+
literal|" , f2:{terms:{${terms} field:'${cat_s}', sort:'n1 asc', facet:{n1:'avg(add(${num_d},${num_d}))'}  }} }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"  f1:{  'buckets':[{ val:'A', count:2, n1:6.0 }, { val:'B', count:3, n1:-2.0}]}"
operator|+
literal|", f2:{  'buckets':[{ val:'B', count:3, n1:-2.0}, { val:'A', count:2, n1:6.0 }]} }"
argument_list|)
expr_stmt|;
comment|// percentiles 0,10,50,90,100
comment|// catA: 2.0 2.2 3.0 3.8 4.0
comment|// catB: -9.0 -8.2 -5.0 7.800000000000001 11.0
comment|// all: -9.0 -7.3999999999999995 2.0 8.200000000000001 11.0
comment|// test sorting by single percentile
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:'${cat_s}', sort:'n1 desc', facet:{n1:'percentile(${num_d},50)'}  }}"
operator|+
literal|" , f2:{terms:{${terms} field:'${cat_s}', sort:'n1 asc', facet:{n1:'percentile(${num_d},50)'}  }} "
operator|+
literal|" , f3:{terms:{${terms} field:'${cat_s}', sort:'n1 desc', facet:{n1:'percentile(${sparse_num_d},50)'}  }} "
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"  f1:{  'buckets':[{ val:'A', count:2, n1:3.0 }, { val:'B', count:3, n1:-5.0}]}"
operator|+
literal|", f2:{  'buckets':[{ val:'B', count:3, n1:-5.0}, { val:'A', count:2, n1:3.0 }]}"
operator|+
literal|", f3:{  'buckets':[{ val:'A', count:2, n1:1.0}, { val:'B', count:3}]}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test sorting by multiple percentiles (sort is by first)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${cat_s}, sort:'n1 desc', facet:{n1:'percentile(${num_d},50,0,100)'}  }}"
operator|+
literal|" , f2:{terms:{${terms} field:${cat_s}, sort:'n1 asc', facet:{n1:'percentile(${num_d},50,0,100)'}  }} }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"  f1:{  'buckets':[{ val:'A', count:2, n1:[3.0,2.0,4.0] }, { val:'B', count:3, n1:[-5.0,-9.0,11.0] }]}"
operator|+
literal|", f2:{  'buckets':[{ val:'B', count:3, n1:[-5.0,-9.0,11.0]}, { val:'A', count:2, n1:[3.0,2.0,4.0] }]} }"
argument_list|)
expr_stmt|;
comment|// test sorting by count/index order
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:'${cat_s}', sort:'count desc' }  }"
operator|+
literal|"           , f2:{terms:{${terms} field:'${cat_s}', sort:'count asc'  }  }"
operator|+
literal|"           , f3:{terms:{${terms} field:'${cat_s}', sort:'index asc'  }  }"
operator|+
literal|"           , f4:{terms:{${terms} field:'${cat_s}', sort:'index desc' }  }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:6 "
operator|+
literal|" ,f1:{buckets:[ {val:B,count:3}, {val:A,count:2} ] }"
operator|+
literal|" ,f2:{buckets:[ {val:A,count:2}, {val:B,count:3} ] }"
operator|+
literal|" ,f3:{buckets:[ {val:A,count:2}, {val:B,count:3} ] }"
operator|+
literal|" ,f4:{buckets:[ {val:B,count:3}, {val:A,count:2} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test sorting by default count/index order
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:'${cat_s}', sort:'count' }  }"
operator|+
literal|"           , f2:{terms:{${terms} field:'${cat_s}', sort:'count asc'  }  }"
operator|+
literal|"           , f3:{terms:{${terms} field:'${cat_s}', sort:'index'  }  }"
operator|+
literal|"           , f4:{terms:{${terms} field:'${cat_s}', sort:'index desc' }  }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:6 "
operator|+
literal|" ,f1:{buckets:[ {val:B,count:3}, {val:A,count:2} ] }"
operator|+
literal|" ,f2:{buckets:[ {val:A,count:2}, {val:B,count:3} ] }"
operator|+
literal|" ,f3:{buckets:[ {val:A,count:2}, {val:B,count:3} ] }"
operator|+
literal|" ,f4:{buckets:[ {val:B,count:3}, {val:A,count:2} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test tiebreaks when sorting by count
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:1 id:6"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:'${cat_s}', sort:'count desc' }  }"
operator|+
literal|"           , f2:{terms:{${terms} field:'${cat_s}', sort:'count asc'  }  }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:2 "
operator|+
literal|" ,f1:{buckets:[ {val:A,count:1}, {val:B,count:1} ] }"
operator|+
literal|" ,f2:{buckets:[ {val:A,count:1}, {val:B,count:1} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// terms facet with nested query facet
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{cat:{terms:{${terms} field:'${cat_s}', facet:{nj:{query:'${where_s}:NJ'}}    }   }} }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'cat':{ 'buckets':[{ 'val':'B', 'count':3, 'nj':{ 'count':2}}, { 'val':'A', 'count':2, 'nj':{ 'count':1}}]} }"
argument_list|)
expr_stmt|;
comment|// terms facet with nested query facet on subset
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:(2 5 4)"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{cat:{terms:{${terms} field:'${cat_s}', facet:{nj:{query:'${where_s}:NJ'}}    }   }} }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':3, "
operator|+
literal|"'cat':{ 'buckets':[{ 'val':'B', 'count':2, 'nj':{ 'count':2}}, { 'val':'A', 'count':1, 'nj':{ 'count':1}}]} }"
argument_list|)
expr_stmt|;
comment|// test prefix
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${super_s}, prefix:s, mincount:0 }}}"
comment|// even with mincount=0, we should only see buckets with the prefix
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[{val:spiderman, count:1}, {val:superman, count:1}]} } "
argument_list|)
expr_stmt|;
comment|// test prefix that doesn't exist
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${super_s}, prefix:ttt, mincount:0 }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[]} } "
argument_list|)
expr_stmt|;
comment|// test prefix that doesn't exist at start
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${super_s}, prefix:aaaaaa, mincount:0 }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[]} } "
argument_list|)
expr_stmt|;
comment|// test prefix that doesn't exist at end
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${super_s}, prefix:zzzzzz, mincount:0 }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[]} } "
argument_list|)
expr_stmt|;
comment|// test prefix on where field
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" f1:{${terms} type:terms, field:${where_s}, prefix:N  }"
operator|+
literal|",f2:{${terms} type:terms, field:${where_s}, prefix:NY }"
operator|+
literal|",f3:{${terms} type:terms, field:${where_s}, prefix:NJ }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6 "
operator|+
literal|",f1:{ 'buckets':[ {val:NJ,count:3}, {val:NY,count:2} ]}"
operator|+
literal|",f2:{ 'buckets':[ {val:NY,count:2} ]}"
operator|+
literal|",f3:{ 'buckets':[ {val:NJ,count:3} ]}"
operator|+
literal|" } "
argument_list|)
expr_stmt|;
comment|// test prefix on real multi-valued field
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" f1:{${terms} type:terms, field:${multi_ss}, prefix:A  }"
operator|+
literal|",f2:{${terms} type:terms, field:${multi_ss}, prefix:z }"
operator|+
literal|",f3:{${terms} type:terms, field:${multi_ss}, prefix:aa }"
operator|+
literal|",f4:{${terms} type:terms, field:${multi_ss}, prefix:bb }"
operator|+
literal|",f5:{${terms} type:terms, field:${multi_ss}, prefix:a }"
operator|+
literal|",f6:{${terms} type:terms, field:${multi_ss}, prefix:b }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6 "
operator|+
literal|",f1:{buckets:[]}"
operator|+
literal|",f2:{buckets:[]}"
operator|+
literal|",f3:{buckets:[]}"
operator|+
literal|",f4:{buckets:[]}"
operator|+
literal|",f5:{buckets:[ {val:a,count:3} ]}"
operator|+
literal|",f6:{buckets:[ {val:b,count:3} ]}"
operator|+
literal|" } "
argument_list|)
expr_stmt|;
comment|//
comment|// missing
comment|//
comment|// test missing w/ non-existent field
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${noexist}, missing:true}}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[], missing:{count:6} } } "
argument_list|)
expr_stmt|;
comment|// test missing
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${sparse_s}, missing:true }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[{val:one, count:1}, {val:two, count:1}], missing:{count:4} } } "
argument_list|)
expr_stmt|;
comment|// test missing with stats
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${sparse_s}, missing:true, facet:{x:'sum(${num_d})'}   }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[{val:one, count:1, x:4.0}, {val:two, count:1, x:11.0}], missing:{count:4, x:-12.0}   } } "
argument_list|)
expr_stmt|;
comment|// test that the missing bucket is not affected by any prefix
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${sparse_s}, missing:true, prefix:on, facet:{x:'sum(${num_d})'}   }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[{val:one, count:1, x:4.0}], missing:{count:4, x:-12.0}   } } "
argument_list|)
expr_stmt|;
comment|// test missing with prefix that doesn't exist
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${sparse_s}, missing:true, prefix:ppp, facet:{x:'sum(${num_d})'}   }}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[], missing:{count:4, x:-12.0}   } } "
argument_list|)
expr_stmt|;
comment|// test numBuckets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${cat_s}, numBuckets:true, limit:1}}}"
comment|// TODO: limit:0 produced an error
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ numBuckets:2, buckets:[{val:B, count:3}]} } "
argument_list|)
expr_stmt|;
comment|// prefix should lower numBuckets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${cat_s}, numBuckets:true, prefix:B}}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ numBuckets:1, buckets:[{val:B, count:3}]} } "
argument_list|)
expr_stmt|;
comment|// mincount should not lower numBuckets (since SOLR-10552)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{terms:{${terms} field:${cat_s}, numBuckets:true, mincount:3}}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ numBuckets:2, buckets:[{val:B, count:3}]} } "
argument_list|)
expr_stmt|;
comment|// basic range facet
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{type:range, field:${num_d}, start:-5, end:10, gap:5}}"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[ {val:-5.0,count:1}, {val:0.0,count:2}, {val:5.0,count:0} ] } }"
argument_list|)
expr_stmt|;
comment|// basic range facet on dates
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{type:range, field:${date}, start:'2001-01-01T00:00:00Z', end:'2003-01-01T00:00:00Z', gap:'+1YEAR'}}"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[ {val:'2001-01-01T00:00:00Z',count:2}, {val:'2002-01-01T00:00:00Z',count:2}] } }"
argument_list|)
expr_stmt|;
comment|// range facet on dates w/ stats
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{type:range, field:${date}, start:'2002-01-01T00:00:00Z', end:'2005-01-01T00:00:00Z', gap:'+1YEAR',   other:all, facet:{ x:'avg(${num_d})' } } }"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[ {val:'2002-01-01T00:00:00Z',count:2,x:-7.0}, {val:'2003-01-01T00:00:00Z',count:1,x:2.0}, {val:'2004-01-01T00:00:00Z',count:0}], before:{count:2,x:7.5}, after:{count:0}, between:{count:3,x:-4.0}  } }"
argument_list|)
expr_stmt|;
comment|// basic range facet with "include" params
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{range:{field:${num_d}, start:-5, end:10, gap:5, include:upper}}}"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[ {val:-5.0,count:0}, {val:0.0,count:2}, {val:5.0,count:0} ] } }"
argument_list|)
expr_stmt|;
comment|// range facet with sub facets and stats
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{range:{field:${num_d}, start:-5, end:10, gap:5,   facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}}   }}}"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[ {val:-5.0,count:1,x:-5.0,ny:{count:1}}, {val:0.0,count:2,x:5.0,ny:{count:1}}, {val:5.0,count:0 /* ,x:0.0,ny:{count:0} */ } ] } }"
argument_list|)
expr_stmt|;
comment|// range facet with sub facets and stats, with "other:all"
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{range:{field:${num_d}, start:-5, end:10, gap:5, other:all,   facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}}   }}}"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[ {val:-5.0,count:1,x:-5.0,ny:{count:1}}, {val:0.0,count:2,x:5.0,ny:{count:1}}, {val:5.0,count:0 /* ,x:0.0,ny:{count:0} */} ]"
operator|+
literal|",before: {count:1,x:-5.0,ny:{count:0}}"
operator|+
literal|",after:  {count:1,x:7.0, ny:{count:0}}"
operator|+
literal|",between:{count:3,x:0.0, ny:{count:2}}"
operator|+
literal|" } }"
argument_list|)
expr_stmt|;
comment|// range facet with mincount
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{type:range, field:${num_d}, start:-5, end:10, gap:5, other:all, mincount:2,    facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}}   }}"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[  {val:0.0,count:2,x:5.0,ny:{count:1}} ]"
operator|+
literal|",before: {count:1,x:-5.0,ny:{count:0}}"
operator|+
literal|",after:  {count:1,x:7.0, ny:{count:0}}"
operator|+
literal|",between:{count:3,x:0.0, ny:{count:2}}"
operator|+
literal|" } }"
argument_list|)
expr_stmt|;
comment|// range facet with sub facets and stats, with "other:all", on subset
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:(3 4 6)"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{range:{field:${num_d}, start:-5, end:10, gap:5, other:all,   facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}}   }}}"
argument_list|)
argument_list|,
literal|"facets=={count:3, f:{buckets:[ {val:-5.0,count:1,x:-5.0,ny:{count:1}}, {val:0.0,count:1,x:3.0,ny:{count:0}}, {val:5.0,count:0 /* ,x:0.0,ny:{count:0} */} ]"
operator|+
literal|",before: {count:0 /* ,x:0.0,ny:{count:0} */ }"
operator|+
literal|",after:  {count:0 /* ,x:0.0,ny:{count:0} */}"
operator|+
literal|",between:{count:2,x:-2.0, ny:{count:1}}"
operator|+
literal|" } }"
argument_list|)
expr_stmt|;
comment|// stats at top level
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ sum1:'sum(${num_d})', sumsq1:'sumsq(${num_d})', avg1:'avg(${num_d})', avg2:'avg(def(${num_d},0))', min1:'min(${num_d})', max1:'max(${num_d})'"
operator|+
literal|", numwhere:'unique(${where_s})', unique_num_i:'unique(${num_i})', unique_num_d:'unique(${num_d})', unique_date:'unique(${date})'"
operator|+
literal|", where_hll:'hll(${where_s})', hll_num_i:'hll(${num_i})', hll_num_d:'hll(${num_d})', hll_date:'hll(${date})'"
operator|+
literal|", med:'percentile(${num_d},50)', perc:'percentile(${num_d},0,50.0,100)', variance:'variance(${num_d})', stddev:'stddev(${num_d})' }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"sum1:3.0, sumsq1:247.0, avg1:0.6, avg2:0.5, min1:-9.0, max1:11.0"
operator|+
literal|", numwhere:2, unique_num_i:4, unique_num_d:5, unique_date:5"
operator|+
literal|", where_hll:2, hll_num_i:4, hll_num_d:5, hll_date:5"
operator|+
literal|", med:2.0, perc:[-9.0,2.0,11.0], variance:49.04, stddev:7.002856560004639}"
argument_list|)
expr_stmt|;
comment|// stats at top level, no matches
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:DOESNOTEXIST"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ sum1:'sum(${num_d})', sumsq1:'sumsq(${num_d})', avg1:'avg(${num_d})', min1:'min(${num_d})', max1:'max(${num_d})'"
operator|+
literal|", numwhere:'unique(${where_s})', unique_num_i:'unique(${num_i})', unique_num_d:'unique(${num_d})', unique_date:'unique(${date})'"
operator|+
literal|", where_hll:'hll(${where_s})', hll_num_i:'hll(${num_i})', hll_num_d:'hll(${num_d})', hll_date:'hll(${date})'"
operator|+
literal|", med:'percentile(${num_d},50)', perc:'percentile(${num_d},0,50.0,100)', variance:'variance(${num_d})', stddev:'stddev(${num_d})' }"
argument_list|)
argument_list|,
literal|"facets=={count:0 "
operator|+
literal|"\n//  ,sum1:0.0, sumsq1:0.0, avg1:0.0, min1:'NaN', max1:'NaN', numwhere:0 \n"
operator|+
literal|" }"
argument_list|)
expr_stmt|;
comment|// stats at top level, matching documents, but no values in the field
comment|// NOTE: this represents the current state of what is returned, not the ultimate desired state.
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:3"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ sum1:'sum(${num_d})', sumsq1:'sumsq(${num_d})', avg1:'avg(${num_d})', min1:'min(${num_d})', max1:'max(${num_d})'"
operator|+
literal|", numwhere:'unique(${where_s})', unique_num_i:'unique(${num_i})', unique_num_d:'unique(${num_d})', unique_date:'unique(${date})'"
operator|+
literal|", where_hll:'hll(${where_s})', hll_num_i:'hll(${num_i})', hll_num_d:'hll(${num_d})', hll_date:'hll(${date})'"
operator|+
literal|", med:'percentile(${num_d},50)', perc:'percentile(${num_d},0,50.0,100)', variance:'variance(${num_d})', stddev:'stddev(${num_d})' }"
argument_list|)
argument_list|,
literal|"facets=={count:1 "
operator|+
literal|",sum1:0.0,"
operator|+
literal|" sumsq1:0.0,"
operator|+
literal|" avg1:0.0,"
operator|+
comment|// TODO: undesirable. omit?
literal|" min1:'NaN',"
operator|+
comment|// TODO: undesirable. omit?
literal|" max1:'NaN',"
operator|+
literal|" numwhere:0,"
operator|+
literal|" unique_num_i:0,"
operator|+
literal|" unique_num_d:0,"
operator|+
literal|" unique_date:0,"
operator|+
literal|" where_hll:0,"
operator|+
literal|" hll_num_i:0,"
operator|+
literal|" hll_num_d:0,"
operator|+
literal|" hll_date:0,"
operator|+
literal|" variance:0.0,"
operator|+
literal|" stddev:0.0"
operator|+
literal|" }"
argument_list|)
expr_stmt|;
comment|//
comment|// tests on a multi-valued field with actual multiple values, just to ensure that we are
comment|// using a multi-valued method for the rest of the tests when appropriate.
comment|//
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{cat:{terms:{${terms} field:'${multi_ss}', facet:{nj:{query:'${where_s}:NJ'}}    }   }} }"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'cat':{ 'buckets':[{ 'val':'a', 'count':3, 'nj':{ 'count':2}}, { 'val':'b', 'count':3, 'nj':{ 'count':2}}]} }"
argument_list|)
expr_stmt|;
comment|// test unique on multi-valued field
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"x:'unique(${multi_ss})'"
operator|+
literal|",y:{query:{q:'id:2', facet:{x:'unique(${multi_ss})'} }}  "
operator|+
literal|",x2:'hll(${multi_ss})'"
operator|+
literal|",y2:{query:{q:'id:2', facet:{x:'hll(${multi_ss})'} }}  "
operator|+
literal|" }"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|",x:2"
operator|+
literal|",y:{count:1, x:2}"
operator|+
comment|// single document should yield 2 unique values
literal|",x2:2"
operator|+
literal|",y2:{count:1, x:2}"
operator|+
comment|// single document should yield 2 unique values
literal|" }"
argument_list|)
expr_stmt|;
comment|// test allBucket multi-valued
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{x:{terms:{${terms} field:'${multi_ss}',allBuckets:true}}}"
argument_list|)
argument_list|,
literal|"facets=={ count:6, "
operator|+
literal|"x:{ buckets:[{val:a, count:3}, {val:b, count:3}] , allBuckets:{count:6} } }"
argument_list|)
expr_stmt|;
comment|// allBuckets for multi-valued field with stats.  This can sometimes take a different path of adding complete DocSets to the Acc
comment|// also test limit:0
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" f0:{${terms} type:terms, field:${multi_ss}, allBuckets:true, limit:0} "
operator|+
literal|",f1:{${terms} type:terms, field:${multi_ss}, allBuckets:true, limit:0, offset:1} "
operator|+
comment|// offset with 0 limit
literal|",f2:{${terms} type:terms, field:${multi_ss}, allBuckets:true, limit:0, facet:{x:'sum(${num_d})'}, sort:'x desc' } "
operator|+
literal|",f3:{${terms} type:terms, field:${multi_ss}, allBuckets:true, limit:0, missing:true, facet:{x:'sum(${num_d})', y:'avg(${num_d})'}, sort:'x desc' } "
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|" f0:{allBuckets:{count:6}, buckets:[]}"
operator|+
literal|",f1:{allBuckets:{count:6}, buckets:[]}"
operator|+
literal|",f2:{allBuckets:{count:6, x:-15.0}, buckets:[]} "
operator|+
literal|",f3:{allBuckets:{count:6, x:-15.0, y:-2.5}, buckets:[], missing:{count:2, x:4.0, y:4.0} }} "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// allBuckets with numeric field with stats.
comment|// also test limit:0
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" f0:{${terms} type:terms, field:${num_i}, allBuckets:true, limit:0} "
operator|+
literal|",f1:{${terms} type:terms, field:${num_i}, allBuckets:true, limit:0, offset:1} "
operator|+
comment|// offset with 0 limit
literal|",f2:{${terms} type:terms, field:${num_i}, allBuckets:true, limit:0, facet:{x:'sum(${num_d})'}, sort:'x desc' } "
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|" f0:{allBuckets:{count:5}, buckets:[]}"
operator|+
literal|",f1:{allBuckets:{count:5}, buckets:[]}"
operator|+
literal|",f2:{allBuckets:{count:5, x:3.0}, buckets:[]} "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|//////////////////////////////////////////////////////////////////////////////////////////////////////////
comment|// test converting legacy facets
comment|// test mincount
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
comment|// , "json.facet", "{f1:{terms:{field:'${cat_s}', mincount:3}}}"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.version"
argument_list|,
literal|"2"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!key=f1}${cat_s}"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{  'buckets':[{ 'val':'B', 'count':3}]} } "
argument_list|)
expr_stmt|;
comment|// test prefix
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
comment|// , "json.facet", "{f1:{terms:{field:${super_s}, prefix:s, mincount:0 }}}"  // even with mincount=0, we should only see buckets with the prefix
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.version"
argument_list|,
literal|"2"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!key=f1}${super_s}"
argument_list|,
literal|"facet.prefix"
argument_list|,
literal|"s"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"0"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{ 'buckets':[{val:spiderman, count:1}, {val:superman, count:1}]} } "
argument_list|)
expr_stmt|;
comment|// range facet with sub facets and stats
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
comment|// , "json.facet", "{f:{range:{field:${num_d}, start:-5, end:10, gap:5,   facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}}   }}}"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.version"
argument_list|,
literal|"2"
argument_list|,
literal|"facet.range"
argument_list|,
literal|"{!key=f}${num_d}"
argument_list|,
literal|"facet.range.start"
argument_list|,
literal|"-5"
argument_list|,
literal|"facet.range.end"
argument_list|,
literal|"10"
argument_list|,
literal|"facet.range.gap"
argument_list|,
literal|"5"
argument_list|,
literal|"f.f.facet.stat"
argument_list|,
literal|"x:sum(${num_i})"
argument_list|,
literal|"subfacet.f.query"
argument_list|,
literal|"{!key=ny}${where_s}:NY"
argument_list|)
argument_list|,
literal|"facets=={count:6, f:{buckets:[ {val:-5.0,count:1,x:-5.0,ny:{count:1}}, {val:0.0,count:2,x:5.0,ny:{count:1}}, {val:5.0,count:0 /* ,x:0.0,ny:{count:0} */ } ] } }"
argument_list|)
expr_stmt|;
comment|// test sorting by stat
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
comment|// , "json.facet", "{f1:{terms:{field:'${cat_s}', sort:'n1 desc', facet:{n1:'sum(${num_d})'}  }}" +
comment|//    " , f2:{terms:{field:'${cat_s}', sort:'n1 asc', facet:{n1:'sum(${num_d})'}  }} }"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.version"
argument_list|,
literal|"2"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!key=f1}${cat_s}"
argument_list|,
literal|"f.f1.facet.sort"
argument_list|,
literal|"n1 desc"
argument_list|,
literal|"facet.stat"
argument_list|,
literal|"n1:sum(${num_d})"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!key=f2}${cat_s}"
argument_list|,
literal|"f.f1.facet.sort"
argument_list|,
literal|"n1 asc"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"  f1:{  'buckets':[{ val:'A', count:2, n1:6.0 }, { val:'B', count:3, n1:-3.0}]}"
operator|+
literal|", f2:{  'buckets':[{ val:'B', count:3, n1:-3.0}, { val:'A', count:2, n1:6.0 }]} }"
argument_list|)
expr_stmt|;
comment|// range facet with sub facets and stats, with "other:all", on subset
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"id:(3 4 6)"
comment|//, "json.facet", "{f:{range:{field:${num_d}, start:-5, end:10, gap:5, other:all,   facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}}   }}}"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.version"
argument_list|,
literal|"2"
argument_list|,
literal|"facet.range"
argument_list|,
literal|"{!key=f}${num_d}"
argument_list|,
literal|"facet.range.start"
argument_list|,
literal|"-5"
argument_list|,
literal|"facet.range.end"
argument_list|,
literal|"10"
argument_list|,
literal|"facet.range.gap"
argument_list|,
literal|"5"
argument_list|,
literal|"f.f.facet.stat"
argument_list|,
literal|"x:sum(${num_i})"
argument_list|,
literal|"subfacet.f.query"
argument_list|,
literal|"{!key=ny}${where_s}:NY"
argument_list|,
literal|"facet.range.other"
argument_list|,
literal|"all"
argument_list|)
argument_list|,
literal|"facets=={count:3, f:{buckets:[ {val:-5.0,count:1,x:-5.0,ny:{count:1}}, {val:0.0,count:1,x:3.0,ny:{count:0}}, {val:5.0,count:0 /* ,x:0.0,ny:{count:0} */} ]"
operator|+
literal|",before: {count:0 /* ,x:0.0,ny:{count:0} */ }"
operator|+
literal|",after:  {count:0 /* ,x:0.0,ny:{count:0} */}"
operator|+
literal|",between:{count:2,x:-2.0, ny:{count:1}}"
operator|+
literal|" } }"
argument_list|)
expr_stmt|;
comment|////////////////////////////////////////////////////////////////////////////////////////////
comment|// multi-select / exclude tagged filters via excludeTags
comment|////////////////////////////////////////////////////////////////////////////////////////////
comment|// test uncached multi-select (see SOLR-8496)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"{!cache=false}*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=doc3,allfilt}-id:3"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"f1:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:doc3} }  "
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:5, "
operator|+
literal|" f1:{ buckets:[ {val:B, count:3}, {val:A, count:2} ]  }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test sub-facets of  empty buckets with domain filter exclusions (canProduceFromEmpty) (see SOLR-9519)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=doc3}id:non-exist"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=CATA}${cat_s}:A"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"f1:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:doc3} }  "
operator|+
literal|",q1 :{type:query, q:'*:*', facet:{ f1:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:doc3} } }  }  "
operator|+
comment|// nested under query
literal|",q1a:{type:query, q:'id:4', facet:{ f1:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:doc3} } }  }  "
operator|+
comment|// nested under query, make sure id:4 filter still applies
literal|",r1 :{type:range, field:${num_d}, start:0, gap:3, end:5,  facet:{ f1:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:doc3} } }  }  "
operator|+
comment|// nested under range, make sure range constraints still apply
literal|",f2:{${terms} type:terms, field:${cat_s}, domain:{filter:'*:*'} }  "
operator|+
comment|// domain filter doesn't widen, so f2 should not appear.
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:0, "
operator|+
literal|" f1:{ buckets:[ {val:A, count:2} ]  }"
operator|+
literal|",q1:{ count:0, f1:{buckets:[{val:A, count:2}]} }"
operator|+
literal|",q1a:{ count:0, f1:{buckets:[{val:A, count:1}]} }"
operator|+
literal|",r1:{ buckets:[ {val:0.0,count:0,f1:{buckets:[{val:A, count:1}]}}, {val:3.0,count:0,f1:{buckets:[{val:A, count:1}]}} ]  }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// nested query facets on subset (with excludeTags)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=abc}id:(2 3)"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ processEmpty:true,"
operator|+
literal|" f1:{query:{q:'${cat_s}:B', facet:{nj:{query:'${where_s}:NJ'}, ny:{query:'${where_s}:NY'}} , excludeTags:[xyz,qaz]}}"
operator|+
literal|",f2:{query:{q:'${cat_s}:B', facet:{nj:{query:'${where_s}:NJ'}, ny:{query:'${where_s}:NY'}} , excludeTags:abc }}"
operator|+
literal|",f3:{query:{q:'${cat_s}:B', facet:{nj:{query:'${where_s}:NJ'}, ny:{query:'${where_s}:NY'}} , excludeTags:'xyz,abc,qaz' }}"
operator|+
literal|",f4:{query:{q:'${cat_s}:B', facet:{nj:{query:'${where_s}:NJ'}, ny:{query:'${where_s}:NY'}} , excludeTags:[xyz , abc , qaz] }}"
operator|+
literal|",f5:{query:{q:'${cat_s}:B', facet:{nj:{query:'${where_s}:NJ'}, ny:{query:'${where_s}:NY'}} , excludeTags:[xyz,qaz]}}"
operator|+
comment|// this is repeated, but it did fail when a single context was shared among sub-facets
literal|",f6:{query:{q:'${cat_s}:B', facet:{processEmpty:true, nj:{query:'${where_s}:NJ'}, ny:{ type:query, q:'${where_s}:NY', excludeTags:abc}}  }}"
operator|+
comment|// exclude in a sub-facet
literal|",f7:{query:{q:'${cat_s}:B', facet:{processEmpty:true, nj:{query:'${where_s}:NJ'}, ny:{ type:query, q:'${where_s}:NY', excludeTags:xyz}}  }}"
operator|+
comment|// exclude in a sub-facet that doesn't match
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':2, "
operator|+
literal|" 'f1':{'count':1, 'nj':{'count':1}, 'ny':{'count':0}}"
operator|+
literal|",'f2':{'count':3, 'nj':{'count':2}, 'ny':{'count':1}}"
operator|+
literal|",'f3':{'count':3, 'nj':{'count':2}, 'ny':{'count':1}}"
operator|+
literal|",'f4':{'count':3, 'nj':{'count':2}, 'ny':{'count':1}}"
operator|+
literal|",'f5':{'count':1, 'nj':{'count':1}, 'ny':{'count':0}}"
operator|+
literal|",'f6':{'count':1, 'nj':{'count':1}, 'ny':{'count':1}}"
operator|+
literal|",'f7':{'count':1, 'nj':{'count':1}, 'ny':{'count':0}}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// terms facet with nested query facet (with excludeTags, using new format inside domain:{})
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"{!cache=false}*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=doc6,allfilt}-id:6"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=doc3,allfilt}-id:3"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{processEmpty:true, "
operator|+
literal|" f0:{${terms} type:terms, field:${cat_s},                                    facet:{nj:{query:'${where_s}:NJ'}} }  "
operator|+
literal|",f1:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:doc3},   missing:true,  facet:{nj:{query:'${where_s}:NJ'}} }  "
operator|+
literal|",f2:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:allfilt},missing:true,  facet:{nj:{query:'${where_s}:NJ'}} }  "
operator|+
literal|",f3:{${terms} type:terms, field:${cat_s}, domain:{excludeTags:doc6},   missing:true,  facet:{nj:{query:'${where_s}:NJ'}} }  "
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:4, "
operator|+
literal|" f0:{ buckets:[ {val:A, count:2, nj:{ count:1}}, {val:B, count:2, nj:{count:2}} ] }"
operator|+
literal|",f1:{ buckets:[ {val:A, count:2, nj:{ count:1}}, {val:B, count:2, nj:{count:2}} ] , missing:{count:1,nj:{count:0}} }"
operator|+
literal|",f2:{ buckets:[ {val:B, count:3, nj:{ count:2}}, {val:A, count:2, nj:{count:1}} ] , missing:{count:1,nj:{count:0}} }"
operator|+
literal|",f3:{ buckets:[ {val:B, count:3, nj:{ count:2}}, {val:A, count:2, nj:{count:1}} ] , missing:{count:0} }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// range facet with sub facets and stats, with "other:all" (with excludeTags)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=doc6,allfilt}-id:6"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=doc3,allfilt}-id:3"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{processEmpty:true "
operator|+
literal|", f1:{type:range, field:${num_d}, start:-5, end:10, gap:5, other:all,   facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}} , domain:{excludeTags:allfilt} }"
operator|+
literal|", f2:{type:range, field:${num_d}, start:-5, end:10, gap:5, other:all,   facet:{ x:'sum(${num_i})', ny:{query:'${where_s}:NY'}}  }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={count:4"
operator|+
literal|",f1:{buckets:[ {val:-5.0,count:1,x:-5.0,ny:{count:1}}, {val:0.0,count:2,x:5.0,ny:{count:1}}, {val:5.0,count:0} ]"
operator|+
literal|",before: {count:1,x:-5.0,ny:{count:0}}"
operator|+
literal|",after:  {count:1,x:7.0, ny:{count:0}}"
operator|+
literal|",between:{count:3,x:0.0, ny:{count:2}} }"
operator|+
literal|",f2:{buckets:[ {val:-5.0,count:0}, {val:0.0,count:2,x:5.0,ny:{count:1}}, {val:5.0,count:0} ]"
operator|+
literal|",before: {count:1,x:-5.0,ny:{count:0}}"
operator|+
literal|",after:  {count:1,x:7.0, ny:{count:0}}"
operator|+
literal|",between:{count:2,x:5.0, ny:{count:1}} }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|//
comment|// facet on numbers
comment|//
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" f1:{${terms}  type:field, field:${num_i} }"
operator|+
literal|",f2:{${terms}  type:field, field:${num_i}, sort:'count asc' }"
operator|+
literal|",f3:{${terms}  type:field, field:${num_i}, sort:'index asc' }"
operator|+
literal|",f4:{${terms}  type:field, field:${num_i}, sort:'index desc' }"
operator|+
literal|",f5:{${terms}  type:field, field:${num_i}, sort:'index desc', limit:1, missing:true, allBuckets:true, numBuckets:true }"
operator|+
literal|",f6:{${terms}  type:field, field:${num_i}, sort:'index desc', mincount:2, numBuckets:true }"
operator|+
comment|// mincount should not lower numbuckets (since SOLR-10552)
literal|",f7:{${terms}  type:field, field:${num_i}, sort:'index desc', offset:2, numBuckets:true }"
operator|+
comment|// test offset
literal|",f8:{${terms}  type:field, field:${num_i}, sort:'index desc', offset:100, numBuckets:true }"
operator|+
comment|// test high offset
literal|",f9:{${terms}  type:field, field:${num_i}, sort:'x desc', facet:{x:'avg(${num_d})'}, missing:true, allBuckets:true, numBuckets:true }"
operator|+
comment|// test stats
literal|",f10:{${terms}  type:field, field:${num_i}, facet:{a:{query:'${cat_s}:A'}}, missing:true, allBuckets:true, numBuckets:true }"
operator|+
comment|// test subfacets
literal|",f11:{${terms}  type:field, field:${num_i}, facet:{a:'unique(${num_d})'} ,missing:true, allBuckets:true, sort:'a desc' }"
operator|+
comment|// test subfacet using unique on numeric field (this previously triggered a resizing bug)
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|",f1:{ buckets:[{val:-5,count:2},{val:2,count:1},{val:3,count:1},{val:7,count:1} ] } "
operator|+
literal|",f2:{ buckets:[{val:2,count:1},{val:3,count:1},{val:7,count:1},{val:-5,count:2} ] } "
operator|+
literal|",f3:{ buckets:[{val:-5,count:2},{val:2,count:1},{val:3,count:1},{val:7,count:1} ] } "
operator|+
literal|",f4:{ buckets:[{val:7,count:1},{val:3,count:1},{val:2,count:1},{val:-5,count:2} ] } "
operator|+
literal|",f5:{ buckets:[{val:7,count:1}]   , numBuckets:4, allBuckets:{count:5}, missing:{count:1}  } "
operator|+
literal|",f6:{ buckets:[{val:-5,count:2}]  , numBuckets:4  } "
operator|+
literal|",f7:{ buckets:[{val:2,count:1},{val:-5,count:2}] , numBuckets:4 } "
operator|+
literal|",f8:{ buckets:[] , numBuckets:4 } "
operator|+
literal|",f9:{ buckets:[{val:7,count:1,x:11.0},{val:2,count:1,x:4.0},{val:3,count:1,x:2.0},{val:-5,count:2,x:-7.0} ],  numBuckets:4, allBuckets:{count:5,x:0.6},missing:{count:1,x:0.0} } "
operator|+
comment|// TODO: should missing exclude "x" because no values were collected?
literal|",f10:{ buckets:[{val:-5,count:2,a:{count:0}},{val:2,count:1,a:{count:1}},{val:3,count:1,a:{count:1}},{val:7,count:1,a:{count:0}} ],  numBuckets:4, allBuckets:{count:5},missing:{count:1,a:{count:0}} } "
operator|+
literal|",f11:{ buckets:[{val:-5,count:2,a:2},{val:2,count:1,a:1},{val:3,count:1,a:1},{val:7,count:1,a:1} ] , missing:{count:1,a:0} , allBuckets:{count:5,a:5}  } "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// facet on a float field - shares same code with integers/longs currently, so we only need to test labels/sorting
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" f1:{${terms}  type:field, field:${num_d} }"
operator|+
literal|",f2:{${terms}  type:field, field:${num_d}, sort:'index desc' }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|",f1:{ buckets:[{val:-9.0,count:1},{val:-5.0,count:1},{val:2.0,count:1},{val:4.0,count:1},{val:11.0,count:1} ] } "
operator|+
literal|",f2:{ buckets:[{val:11.0,count:1},{val:4.0,count:1},{val:2.0,count:1},{val:-5.0,count:1},{val:-9.0,count:1} ] } "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test 0, min/max int
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" u : 'unique(${Z_num_i})'"
operator|+
literal|", f1:{${terms}  type:field, field:${Z_num_i} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={count:6 "
operator|+
literal|",u:3"
operator|+
literal|",f1:{ buckets:[{val:"
operator|+
name|Integer
operator|.
name|MIN_VALUE
operator|+
literal|",count:1},{val:0,count:1},{val:"
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|",count:1}]} "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// multi-valued integer
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|" c1:'unique(${num_is})', c2:'hll(${num_is})'"
operator|+
literal|",f1:{${terms} type:terms, field:${num_is} }  "
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:6 "
operator|+
literal|", c1:5, c2:5"
operator|+
literal|", f1:{ buckets:[ {val:-1,count:2},{val:0,count:2},{val:3,count:2},{val:-5,count:1},{val:2,count:1}  ] } "
operator|+
literal|"} "
argument_list|)
expr_stmt|;
comment|// multi-valued float
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|" c1:'unique(${num_fs})', c2:'hll(${num_fs})'"
operator|+
literal|",f1:{${terms} type:terms, field:${num_fs} }  "
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:6 "
operator|+
literal|", c1:5, c2:5"
operator|+
literal|", f1:{ buckets:[ {val:-1.5,count:2},{val:0.0,count:2},{val:3.0,count:2},{val:-5.0,count:1},{val:2.0,count:1}  ] } "
operator|+
literal|"} "
argument_list|)
expr_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
comment|// "cat0:{type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:0}" +  // overrequest=0 test needs predictable layout
literal|"cat1:{type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:1}"
operator|+
literal|",catDef:{type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:-1}"
operator|+
comment|// -1 is default overrequest
literal|",catBig:{type:terms, field:${cat_s}, sort:'count desc', offset:1, limit:2147483647, overrequest:2147483647}"
operator|+
comment|// make sure overflows don't mess us up
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:6"
operator|+
comment|// ", cat0:{ buckets:[ {val:B,count:3} ] }"
literal|", cat1:{ buckets:[ {val:B,count:3} ] }"
operator|+
literal|", catDef:{ buckets:[ {val:B,count:3} ] }"
operator|+
literal|", catBig:{ buckets:[ {val:A,count:2} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test filter
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"myfilt"
argument_list|,
literal|"${cat_s}:A"
argument_list|,
literal|"ff"
argument_list|,
literal|"-id:1"
argument_list|,
literal|"ff"
argument_list|,
literal|"-id:2"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"t:{${terms} type:terms, field:${cat_s}, domain:{filter:[]} }"
operator|+
comment|// empty filter list
literal|",t_filt:{${terms} type:terms, field:${cat_s}, domain:{filter:'${cat_s}:B'} }"
operator|+
literal|",t_filt2 :{${terms} type:terms, field:${cat_s}, domain:{filter:'{!query v=$myfilt}'} }"
operator|+
comment|// test access to qparser and other query parameters
literal|",t_filt2a:{${terms} type:terms, field:${cat_s}, domain:{filter:{param:myfilt} } }"
operator|+
comment|// test filter via "param" type
literal|",t_filt3: {${terms} type:terms, field:${cat_s}, domain:{filter:['-id:1','-id:2']} }"
operator|+
literal|",t_filt3a:{${terms} type:terms, field:${cat_s}, domain:{filter:{param:ff}} }"
operator|+
comment|// test multi-valued query parameter
literal|",q:{type:query, q:'${cat_s}:B', domain:{filter:['-id:5']} }"
operator|+
comment|// also tests a top-level negative filter
literal|",r:{type:range, field:${num_d}, start:-5, end:10, gap:5, domain:{filter:'-id:4'} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:6, "
operator|+
literal|"t        :{ buckets:[ {val:B, count:3}, {val:A, count:2} ] }"
operator|+
literal|",t_filt  :{ buckets:[ {val:B, count:3}] } "
operator|+
literal|",t_filt2 :{ buckets:[ {val:A, count:2}] } "
operator|+
literal|",t_filt2a:{ buckets:[ {val:A, count:2}] } "
operator|+
literal|",t_filt3 :{ buckets:[ {val:B, count:2}, {val:A, count:1}] } "
operator|+
literal|",t_filt3a:{ buckets:[ {val:B, count:2}, {val:A, count:1}] } "
operator|+
literal|",q:{count:2}"
operator|+
literal|",r:{buckets:[ {val:-5.0,count:1}, {val:0.0,count:1}, {val:5.0,count:0} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test acc reuse (i.e. reset() method).  This is normally used for stats that are not calculated in the first phase,
comment|// currently non-sorting stats.
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{type:terms, field:'${cat_s}', facet:{h:'hll(${where_s})' , u:'unique(${where_s})', mind:'min(${num_d})', maxd:'max(${num_d})', sumd:'sum(${num_d})', avgd:'avg(${num_d})', variance:'variance(${num_d})', stddev:'stddev(${num_d})'         }   }}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':6, "
operator|+
literal|"'f1':{  buckets:[{val:B, count:3, h:2, u:2, mind:-9.0, maxd:11.0, sumd:-3.0, avgd:-1.0, variance:74.66666666666667, stddev:8.640987597877148},"
operator|+
literal|"                 {val:A, count:2, h:2, u:2, mind:2.0, maxd:4.0, sumd:6.0, avgd:3.0, variance:1.0, stddev:1.0}] } } "
argument_list|)
expr_stmt|;
comment|// test min/max of string field
if|if
condition|(
name|where_s
operator|.
name|equals
argument_list|(
literal|"where_s"
argument_list|)
operator|||
name|where_s
operator|.
name|equals
argument_list|(
literal|"where_sd"
argument_list|)
condition|)
block|{
comment|// supports only single valued currently...
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:* -(+${cat_s}:A +${where_s}:NJ)"
comment|// make NY the only value in bucket A
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"  f1:{type:terms, field:'${cat_s}', facet:{min:'min(${where_s})', max:'max(${where_s})'}   }"
operator|+
literal|", f2:{type:terms, field:'${cat_s}', facet:{min:'min(${where_s})', max:'max(${where_s})'} , sort:'min desc'}"
operator|+
literal|", f3:{type:terms, field:'${cat_s}', facet:{min:'min(${where_s})', max:'max(${where_s})'} , sort:'min asc'}"
operator|+
literal|", f4:{type:terms, field:'${cat_s}', facet:{min:'min(${super_s})', max:'max(${super_s})'} , sort:'max asc'}"
operator|+
literal|", f5:{type:terms, field:'${cat_s}', facet:{min:'min(${super_s})', max:'max(${super_s})'} , sort:'max desc'}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:5, "
operator|+
literal|" f1:{ buckets:[{val:B, count:3, min:NJ, max:NY}, {val:A, count:1, min:NY, max:NY}]}"
operator|+
literal|",f2:{ buckets:[{val:A, count:1, min:NY, max:NY}, {val:B, count:3, min:NJ, max:NY}]}"
operator|+
literal|",f3:{ buckets:[{val:B, count:3, min:NJ, max:NY}, {val:A, count:1, min:NY, max:NY}]}"
operator|+
literal|",f4:{ buckets:[{val:B, count:3, min:batman, max:superman}, {val:A, count:1, min:zodiac, max:zodiac}]}"
operator|+
literal|",f5:{ buckets:[{val:A, count:1, min:zodiac, max:zodiac}, {val:B, count:3, min:batman, max:superman}]}"
operator|+
literal|" } "
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOverrequest
specifier|public
name|void
name|testOverrequest
parameter_list|()
throws|throws
name|Exception
block|{
name|initServers
argument_list|()
expr_stmt|;
name|Client
name|client
init|=
name|servers
operator|.
name|getClient
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|queryDefaults
argument_list|()
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|servers
operator|.
name|getShards
argument_list|()
argument_list|,
literal|"debugQuery"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SolrClient
argument_list|>
name|clients
init|=
name|client
operator|.
name|getClientProvider
argument_list|()
operator|.
name|all
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|clients
operator|.
name|size
argument_list|()
operator|>=
literal|3
argument_list|)
expr_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|p
init|=
name|params
argument_list|(
literal|"cat_s"
argument_list|,
literal|"cat_s"
argument_list|)
decl_stmt|;
name|String
name|cat_s
init|=
name|p
operator|.
name|get
argument_list|(
literal|"cat_s"
argument_list|)
decl_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
comment|// A will win tiebreak
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
comment|// A will win tiebreak
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Shard responses should be A=1, A=1, B=2, merged should be "A=2, B=2" hence A wins tiebreak
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"cat0:{type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:0}"
operator|+
literal|",cat1:{type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:1}"
operator|+
literal|",catDef:{type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:-1}"
operator|+
comment|// -1 is default overrequest
literal|",catBig:{type:terms, field:${cat_s}, sort:'count desc', offset:1, limit:2147483647, overrequest:2147483647}"
operator|+
comment|// make sure overflows don't mess us up
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:6"
operator|+
literal|", cat0:{ buckets:[ {val:A,count:2} ] }"
operator|+
comment|// with no overrequest, we incorrectly conclude that A is the top bucket
literal|", cat1:{ buckets:[ {val:B,count:4} ] }"
operator|+
literal|", catDef:{ buckets:[ {val:B,count:4} ] }"
operator|+
literal|", catBig:{ buckets:[ {val:A,count:2} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBigger
specifier|public
name|void
name|testBigger
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|p
init|=
name|params
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"cat_ss"
argument_list|,
literal|"where_s"
argument_list|,
literal|"where_ss"
argument_list|)
decl_stmt|;
comment|//    doBigger(Client.localClient, p);
name|initServers
argument_list|()
expr_stmt|;
name|Client
name|client
init|=
name|servers
operator|.
name|getClient
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|queryDefaults
argument_list|()
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|servers
operator|.
name|getShards
argument_list|()
argument_list|)
expr_stmt|;
name|doBigger
argument_list|(
name|client
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|getId
specifier|private
name|String
name|getId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|US
argument_list|,
literal|"%05d"
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|doBigger
specifier|public
name|void
name|doBigger
parameter_list|(
name|Client
name|client
parameter_list|,
name|ModifiableSolrParams
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|MacroExpander
name|m
init|=
operator|new
name|MacroExpander
argument_list|(
name|p
operator|.
name|getMap
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|cat_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${cat_s}"
argument_list|)
decl_stmt|;
name|String
name|where_s
init|=
name|m
operator|.
name|expand
argument_list|(
literal|"${where_s}"
argument_list|)
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// make deterministic
name|int
name|numCat
init|=
literal|1
decl_stmt|;
name|int
name|numWhere
init|=
literal|2000000000
decl_stmt|;
name|int
name|commitPercent
init|=
literal|10
decl_stmt|;
name|int
name|ndocs
init|=
literal|1000
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|>
name|model
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|// cat->where->list<ids>
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|Integer
name|cat
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|numCat
argument_list|)
decl_stmt|;
name|Integer
name|where
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|numWhere
argument_list|)
decl_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getId
argument_list|(
name|i
argument_list|)
argument_list|,
name|cat_s
argument_list|,
name|cat
argument_list|,
name|where_s
argument_list|,
name|where
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|sub
init|=
name|model
operator|.
name|get
argument_list|(
name|cat
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
name|sub
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|cat
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|ids
init|=
name|sub
operator|.
name|get
argument_list|(
name|where
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|sub
operator|.
name|put
argument_list|(
name|where
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|commitPercent
condition|)
block|{
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|model
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{type:terms, field:${cat_s}, limit:2, facet:{x:'unique($where_s)'}  }}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':"
operator|+
name|ndocs
operator|+
literal|","
operator|+
literal|"'f1':{  'buckets':[{ 'val':'0', 'count':"
operator|+
name|ndocs
operator|+
literal|", x:"
operator|+
name|sz
operator|+
literal|" }]} } "
argument_list|)
expr_stmt|;
if|if
condition|(
name|client
operator|.
name|local
argument_list|()
condition|)
block|{
comment|// distrib estimation prob won't match
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{type:terms, field:${cat_s}, limit:2, facet:{x:'hll($where_s)'}  }}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':"
operator|+
name|ndocs
operator|+
literal|","
operator|+
literal|"'f1':{  'buckets':[{ 'val':'0', 'count':"
operator|+
name|ndocs
operator|+
literal|", x:"
operator|+
name|sz
operator|+
literal|" }]} } "
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{type:terms, field:id, limit:1, offset:990}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':"
operator|+
name|ndocs
operator|+
literal|","
operator|+
literal|"'f1':{buckets:[{val:'00990',count:1}]}} "
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|int
name|off
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
decl_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"off"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|off
argument_list|)
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f1:{type:terms, field:id, limit:1, offset:${off}}}"
argument_list|)
argument_list|,
literal|"facets=={ 'count':"
operator|+
name|ndocs
operator|+
literal|","
operator|+
literal|"'f1':{buckets:[{val:'"
operator|+
name|getId
argument_list|(
name|off
argument_list|)
operator|+
literal|"',count:1}]}} "
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTolerant
specifier|public
name|void
name|testTolerant
parameter_list|()
throws|throws
name|Exception
block|{
name|initServers
argument_list|()
expr_stmt|;
name|Client
name|client
init|=
name|servers
operator|.
name|getClient
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|queryDefaults
argument_list|()
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|servers
operator|.
name|getShards
argument_list|()
operator|+
literal|",[ff01::114]:33332:/ignore_exception"
argument_list|)
expr_stmt|;
name|indexSimple
argument_list|(
name|client
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"ignore_exception"
argument_list|,
literal|"true"
argument_list|,
literal|"shards.tolerant"
argument_list|,
literal|"false"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{type:terms, field:cat_s}}"
argument_list|)
argument_list|,
literal|"facets=={ count:6,"
operator|+
literal|"f:{ buckets:[{val:B,count:3},{val:A,count:2}] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"we should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"ignore_exception"
argument_list|,
literal|"true"
argument_list|,
literal|"shards.tolerant"
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{type:terms, field:cat_s}}"
argument_list|)
argument_list|,
literal|"facets=={ count:6,"
operator|+
literal|"f:{ buckets:[{val:B,count:3},{val:A,count:2}] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockJoin
specifier|public
name|void
name|testBlockJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|doBlockJoin
argument_list|(
name|Client
operator|.
name|localClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doBlockJoin
specifier|public
name|void
name|doBlockJoin
parameter_list|(
name|Client
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|p
init|=
name|params
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|parent
decl_stmt|;
name|parent
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"A"
argument_list|,
literal|"v_t"
argument_list|,
literal|"q"
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|parent
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|parent
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"B"
argument_list|,
literal|"v_t"
argument_list|,
literal|"q w"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChildDocument
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2.1"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"a"
argument_list|,
literal|"v_t"
argument_list|,
literal|"x y z"
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChildDocument
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2.2"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"b"
argument_list|,
literal|"v_t"
argument_list|,
literal|"x y  "
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChildDocument
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2.3"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"c"
argument_list|,
literal|"v_t"
argument_list|,
literal|"  y z"
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|parent
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|parent
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"C"
argument_list|,
literal|"v_t"
argument_list|,
literal|"q w e"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChildDocument
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3.1"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"d"
argument_list|,
literal|"v_t"
argument_list|,
literal|"x    "
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChildDocument
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3.2"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"e"
argument_list|,
literal|"v_t"
argument_list|,
literal|"  y  "
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChildDocument
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3.3"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"f"
argument_list|,
literal|"v_t"
argument_list|,
literal|"    z"
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|parent
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|parent
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"D"
argument_list|,
literal|"v_t"
argument_list|,
literal|"e"
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|parent
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|"pages:{ type:query, domain:{blockChildren:'type_s:book'} , facet:{ x:{field:v_t} } }"
operator|+
literal|",pages2:{type:terms, field:v_t, domain:{blockChildren:'type_s:book'} }"
operator|+
literal|",books:{ type:query, domain:{blockParent:'type_s:book'}  , facet:{ x:{field:v_t} } }"
operator|+
literal|",books2:{type:terms, field:v_t, domain:{blockParent:'type_s:book'} }"
operator|+
literal|",pageof3:{ type:query, q:'id:3', facet : { x : { type:terms, field:page_s, domain:{blockChildren:'type_s:book'}}} }"
operator|+
literal|",bookof22:{ type:query, q:'id:2.2', facet : { x : { type:terms, field:book_s, domain:{blockParent:'type_s:book'}}} }"
operator|+
literal|",missing_blockParent:{ type:query, domain:{blockParent:'type_s:does_not_exist'} }"
operator|+
literal|",missing_blockChildren:{ type:query, domain:{blockChildren:'type_s:does_not_exist'} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", pages:{count:6 , x:{buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ]}  }"
operator|+
literal|", pages2:{ buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ] }"
operator|+
literal|", books:{count:4 , x:{buckets:[ {val:q,count:3},{val:e,count:2},{val:w,count:2} ]}  }"
operator|+
literal|", books2:{ buckets:[ {val:q,count:3},{val:e,count:2},{val:w,count:2} ] }"
operator|+
literal|", pageof3:{count:1 , x:{buckets:[ {val:d,count:1},{val:e,count:1},{val:f,count:1} ]}  }"
operator|+
literal|", bookof22:{count:1 , x:{buckets:[ {val:B,count:1} ]}  }"
operator|+
literal|", missing_blockParent:{count:0}"
operator|+
literal|", missing_blockChildren:{count:0}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// no matches in base query
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"no_match_s:NO_MATCHES"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ processEmpty:true,"
operator|+
literal|"pages:{ type:query, domain:{blockChildren:'type_s:book'} }"
operator|+
literal|",books:{ type:query, domain:{blockParent:'type_s:book'} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:0"
operator|+
literal|", pages:{count:0}"
operator|+
literal|", books:{count:0}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test facet on children nested under terms facet on parents
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"books:{ type:terms, field:book_s, facet:{ pages:{type:terms, field:v_t, domain:{blockChildren:'type_s:book'}} } }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", books:{buckets:[{val:A,count:1,pages:{buckets:[]}}"
operator|+
literal|"                 ,{val:B,count:1,pages:{buckets:[{val:y,count:3},{val:x,count:2},{val:z,count:2}]}}"
operator|+
literal|"                 ,{val:C,count:1,pages:{buckets:[{val:x,count:1},{val:y,count:1},{val:z,count:1}]}}"
operator|+
literal|"                 ,{val:D,count:1,pages:{buckets:[]}}"
operator|+
literal|"] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test filter after block join
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|"pages1:{type:terms, field:v_t, domain:{blockChildren:'type_s:book', filter:'*:*'} }"
operator|+
literal|",pages2:{type:terms, field:v_t, domain:{blockChildren:'type_s:book', filter:'-id:3.1'} }"
operator|+
literal|",books:{type:terms, field:v_t, domain:{blockParent:'type_s:book', filter:'*:*'} }"
operator|+
literal|",books2:{type:terms, field:v_t, domain:{blockParent:'type_s:book', filter:'id:1'} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", pages1:{ buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ] }"
operator|+
literal|", pages2:{ buckets:[ {val:y,count:4},{val:z,count:3},{val:x,count:2} ] }"
operator|+
literal|", books:{ buckets:[ {val:q,count:3},{val:e,count:2},{val:w,count:2} ] }"
operator|+
literal|", books2:{ buckets:[ {val:q,count:1} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test other various ways to get filters
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"f1"
argument_list|,
literal|"-id:3.1"
argument_list|,
literal|"f2"
argument_list|,
literal|"id:1"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|"pages1:{type:terms, field:v_t, domain:{blockChildren:'type_s:book', filter:[]} }"
operator|+
literal|",pages2:{type:terms, field:v_t, domain:{blockChildren:'type_s:book', filter:{param:f1} } }"
operator|+
literal|",books:{type:terms, field:v_t, domain:{blockParent:'type_s:book', filter:[{param:q},{param:missing_param}]} }"
operator|+
literal|",books2:{type:terms, field:v_t, domain:{blockParent:'type_s:book', filter:[{param:f2}] } }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", pages1:{ buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ] }"
operator|+
literal|", pages2:{ buckets:[ {val:y,count:4},{val:z,count:3},{val:x,count:2} ] }"
operator|+
literal|", books:{ buckets:[ {val:q,count:3},{val:e,count:2},{val:w,count:2} ] }"
operator|+
literal|", books2:{ buckets:[ {val:q,count:1} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Similar to {@link #testBlockJoin} but uses query time joining.    *<p>    * (asserts are slightly diff because if a query matches multiple types of documents, blockJoin domain switches    * to parent/child domains preserve any existing parent/children from the original domain - eg: when q=*:*)    *</p>    */
DECL|method|testQureyJoinBooksAndPages
specifier|public
name|void
name|testQureyJoinBooksAndPages
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Client
name|client
init|=
name|Client
operator|.
name|localClient
argument_list|()
decl_stmt|;
specifier|final
name|SolrParams
name|p
init|=
name|params
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// build up a list of the docs we want to test with
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docsToAdd
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"A"
argument_list|,
literal|"v_t"
argument_list|,
literal|"q"
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"B"
argument_list|,
literal|"v_t"
argument_list|,
literal|"q w"
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"book_id_s"
argument_list|,
literal|"2"
argument_list|,
literal|"id"
argument_list|,
literal|"2.1"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"a"
argument_list|,
literal|"v_t"
argument_list|,
literal|"x y z"
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"book_id_s"
argument_list|,
literal|"2"
argument_list|,
literal|"id"
argument_list|,
literal|"2.2"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"b"
argument_list|,
literal|"v_t"
argument_list|,
literal|"x y  "
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"book_id_s"
argument_list|,
literal|"2"
argument_list|,
literal|"id"
argument_list|,
literal|"2.3"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"c"
argument_list|,
literal|"v_t"
argument_list|,
literal|"  y z"
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"C"
argument_list|,
literal|"v_t"
argument_list|,
literal|"q w e"
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"book_id_s"
argument_list|,
literal|"3"
argument_list|,
literal|"id"
argument_list|,
literal|"3.1"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"d"
argument_list|,
literal|"v_t"
argument_list|,
literal|"x    "
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"book_id_s"
argument_list|,
literal|"3"
argument_list|,
literal|"id"
argument_list|,
literal|"3.2"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"e"
argument_list|,
literal|"v_t"
argument_list|,
literal|"  y  "
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"book_id_s"
argument_list|,
literal|"3"
argument_list|,
literal|"id"
argument_list|,
literal|"3.3"
argument_list|,
literal|"type_s"
argument_list|,
literal|"page"
argument_list|,
literal|"page_s"
argument_list|,
literal|"f"
argument_list|,
literal|"v_t"
argument_list|,
literal|"    z"
argument_list|)
argument_list|)
expr_stmt|;
name|docsToAdd
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"type_s"
argument_list|,
literal|"book"
argument_list|,
literal|"book_s"
argument_list|,
literal|"D"
argument_list|,
literal|"v_t"
argument_list|,
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
comment|// shuffle the docs since order shouldn't matter
name|Collections
operator|.
name|shuffle
argument_list|(
name|docsToAdd
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrInputDocument
name|doc
range|:
name|docsToAdd
control|)
block|{
name|client
operator|.
name|add
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// the domains we'll be testing, initially setup for block join
specifier|final
name|String
name|toChildren
init|=
literal|"join: { from:'id', to:'book_id_s' }"
decl_stmt|;
specifier|final
name|String
name|toParents
init|=
literal|"join: { from:'book_id_s', to:'id' }"
decl_stmt|;
specifier|final
name|String
name|toBogusChildren
init|=
literal|"join: { from:'id', to:'does_not_exist' }"
decl_stmt|;
specifier|final
name|String
name|toBogusParents
init|=
literal|"join: { from:'book_id_s', to:'does_not_exist' }"
decl_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|"pages:{ type:query, domain:{"
operator|+
name|toChildren
operator|+
literal|"} , facet:{ x:{field:v_t} } }"
operator|+
literal|",pages2:{type:terms, field:v_t, domain:{"
operator|+
name|toChildren
operator|+
literal|"} }"
operator|+
literal|",books:{ type:query, domain:{"
operator|+
name|toParents
operator|+
literal|"}  , facet:{ x:{field:v_t} } }"
operator|+
literal|",books2:{type:terms, field:v_t, domain:{"
operator|+
name|toParents
operator|+
literal|"} }"
operator|+
literal|",pageof3:{ type:query, q:'id:3', facet : { x : { type:terms, field:page_s, domain:{"
operator|+
name|toChildren
operator|+
literal|"}}} }"
operator|+
literal|",bookof22:{ type:query, q:'id:2.2', facet : { x : { type:terms, field:book_s, domain:{"
operator|+
name|toParents
operator|+
literal|"}}} }"
operator|+
literal|",missing_Parents:{ type:query, domain:{"
operator|+
name|toBogusParents
operator|+
literal|"} }"
operator|+
literal|",missing_Children:{ type:query, domain:{"
operator|+
name|toBogusChildren
operator|+
literal|"} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", pages:{count:6 , x:{buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ]}  }"
operator|+
literal|", pages2:{ buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ] }"
operator|+
literal|", books:{count:2 , x:{buckets:[ {val:q,count:2},{val:w,count:2},{val:e,count:1} ]}  }"
operator|+
literal|", books2:{ buckets:[ {val:q,count:2},{val:w,count:2},{val:e,count:1} ] }"
operator|+
literal|", pageof3:{count:1 , x:{buckets:[ {val:d,count:1},{val:e,count:1},{val:f,count:1} ]}  }"
operator|+
literal|", bookof22:{count:1 , x:{buckets:[ {val:B,count:1} ]}  }"
operator|+
literal|", missing_Parents:{count:0}"
operator|+
literal|", missing_Children:{count:0}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// no matches in base query
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"no_match_s:NO_MATCHES"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ processEmpty:true,"
operator|+
literal|"pages:{ type:query, domain:{"
operator|+
name|toChildren
operator|+
literal|"} }"
operator|+
literal|",books:{ type:query, domain:{"
operator|+
name|toParents
operator|+
literal|"} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:0"
operator|+
literal|", pages:{count:0}"
operator|+
literal|", books:{count:0}"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test facet on children nested under terms facet on parents
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"books:{ type:terms, field:book_s, facet:{ pages:{type:terms, field:v_t, domain:{"
operator|+
name|toChildren
operator|+
literal|"}} } }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", books:{buckets:[{val:A,count:1,pages:{buckets:[]}}"
operator|+
literal|"                 ,{val:B,count:1,pages:{buckets:[{val:y,count:3},{val:x,count:2},{val:z,count:2}]}}"
operator|+
literal|"                 ,{val:C,count:1,pages:{buckets:[{val:x,count:1},{val:y,count:1},{val:z,count:1}]}}"
operator|+
literal|"                 ,{val:D,count:1,pages:{buckets:[]}}"
operator|+
literal|"] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test filter after join
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|"pages1:{type:terms, field:v_t, domain:{"
operator|+
name|toChildren
operator|+
literal|", filter:'*:*'} }"
operator|+
literal|",pages2:{type:terms, field:v_t, domain:{"
operator|+
name|toChildren
operator|+
literal|", filter:'-id:3.1'} }"
operator|+
literal|",books:{type:terms, field:v_t, domain:{"
operator|+
name|toParents
operator|+
literal|", filter:'*:*'} }"
operator|+
literal|",books2:{type:terms, field:v_t, domain:{"
operator|+
name|toParents
operator|+
literal|", filter:'id:2'} }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", pages1:{ buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ] }"
operator|+
literal|", pages2:{ buckets:[ {val:y,count:4},{val:z,count:3},{val:x,count:2} ] }"
operator|+
literal|", books:{ buckets:[ {val:q,count:2},{val:w,count:2},{val:e,count:1} ] }"
operator|+
literal|", books2:{ buckets:[ {val:q,count:1}, {val:w,count:1} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test other various ways to get filters
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"f1"
argument_list|,
literal|"-id:3.1"
argument_list|,
literal|"f2"
argument_list|,
literal|"id:2"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{ "
operator|+
literal|"pages1:{type:terms, field:v_t, domain:{"
operator|+
name|toChildren
operator|+
literal|", filter:[]} }"
operator|+
literal|",pages2:{type:terms, field:v_t, domain:{"
operator|+
name|toChildren
operator|+
literal|", filter:{param:f1} } }"
operator|+
literal|",books:{type:terms, field:v_t, domain:{"
operator|+
name|toParents
operator|+
literal|", filter:[{param:q},{param:missing_param}]} }"
operator|+
literal|",books2:{type:terms, field:v_t, domain:{"
operator|+
name|toParents
operator|+
literal|", filter:[{param:f2}] } }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:10"
operator|+
literal|", pages1:{ buckets:[ {val:y,count:4},{val:x,count:3},{val:z,count:3} ] }"
operator|+
literal|", pages2:{ buckets:[ {val:y,count:4},{val:z,count:3},{val:x,count:2} ] }"
operator|+
literal|", books:{ buckets:[ {val:q,count:2},{val:w,count:2},{val:e,count:1} ] }"
operator|+
literal|", books2:{ buckets:[ {val:q,count:1}, {val:w,count:1} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrors
specifier|public
name|void
name|testErrors
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestErrors
argument_list|(
name|Client
operator|.
name|localClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestErrors
specifier|public
name|void
name|doTestErrors
parameter_list|(
name|Client
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|p
init|=
name|params
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"ignore_exception"
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{f:{type:ignore_exception_aaa, field:bbbbbb}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ignore_exception_aaa"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|XtestPercentiles
specifier|public
name|void
name|XtestPercentiles
parameter_list|()
block|{
name|AVLTreeDigest
name|catA
init|=
operator|new
name|AVLTreeDigest
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|catA
operator|.
name|add
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|catA
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|AVLTreeDigest
name|catB
init|=
operator|new
name|AVLTreeDigest
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|catB
operator|.
name|add
argument_list|(
operator|-
literal|9
argument_list|)
expr_stmt|;
name|catB
operator|.
name|add
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|catB
operator|.
name|add
argument_list|(
operator|-
literal|5
argument_list|)
expr_stmt|;
name|AVLTreeDigest
name|all
init|=
operator|new
name|AVLTreeDigest
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|all
operator|.
name|add
argument_list|(
name|catA
argument_list|)
expr_stmt|;
name|all
operator|.
name|add
argument_list|(
name|catB
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|str
argument_list|(
name|catA
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|str
argument_list|(
name|catB
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|str
argument_list|(
name|all
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2.0 2.2 3.0 3.8 4.0
comment|// -9.0 -8.2 -5.0 7.800000000000001 11.0
comment|// -9.0 -7.3999999999999995 2.0 8.200000000000001 11.0
block|}
DECL|method|str
specifier|private
specifier|static
name|String
name|str
parameter_list|(
name|AVLTreeDigest
name|digest
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|double
name|d
range|:
operator|new
name|double
index|[]
block|{
literal|0
block|,
literal|.1
block|,
literal|.5
block|,
literal|.9
block|,
literal|1
block|}
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|digest
operator|.
name|quantile
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/*** test code to ensure TDigest is working as we expect. */
DECL|method|XtestTDigest
specifier|public
name|void
name|XtestTDigest
parameter_list|()
throws|throws
name|Exception
block|{
name|AVLTreeDigest
name|t1
init|=
operator|new
name|AVLTreeDigest
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|t1
operator|.
name|add
argument_list|(
literal|10
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|t1
operator|.
name|add
argument_list|(
literal|90
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|t1
operator|.
name|add
argument_list|(
literal|50
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|t1
operator|.
name|quantile
argument_list|(
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|t1
operator|.
name|quantile
argument_list|(
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|t1
operator|.
name|quantile
argument_list|(
literal|0.9
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t1
operator|.
name|quantile
argument_list|(
literal|0.5
argument_list|)
argument_list|,
literal|50.0
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|AVLTreeDigest
name|t2
init|=
operator|new
name|AVLTreeDigest
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|t2
operator|.
name|add
argument_list|(
literal|130
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|t2
operator|.
name|add
argument_list|(
literal|170
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|t2
operator|.
name|add
argument_list|(
literal|90
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|t2
operator|.
name|quantile
argument_list|(
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|t2
operator|.
name|quantile
argument_list|(
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|t2
operator|.
name|quantile
argument_list|(
literal|0.9
argument_list|)
argument_list|)
expr_stmt|;
name|AVLTreeDigest
name|top
init|=
operator|new
name|AVLTreeDigest
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|t1
operator|.
name|compress
argument_list|()
expr_stmt|;
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|t1
operator|.
name|byteSize
argument_list|()
argument_list|)
decl_stmt|;
comment|// upper bound
name|t1
operator|.
name|asSmallBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|byte
index|[]
name|arr1
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|buf
operator|.
name|array
argument_list|()
argument_list|,
name|buf
operator|.
name|position
argument_list|()
argument_list|)
decl_stmt|;
name|ByteBuffer
name|rbuf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|arr1
argument_list|)
decl_stmt|;
name|top
operator|.
name|add
argument_list|(
name|AVLTreeDigest
operator|.
name|fromBytes
argument_list|(
name|rbuf
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|top
operator|.
name|quantile
argument_list|(
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|top
operator|.
name|quantile
argument_list|(
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|top
operator|.
name|quantile
argument_list|(
literal|0.9
argument_list|)
argument_list|)
expr_stmt|;
name|t2
operator|.
name|compress
argument_list|()
expr_stmt|;
name|ByteBuffer
name|buf2
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|t2
operator|.
name|byteSize
argument_list|()
argument_list|)
decl_stmt|;
comment|// upper bound
name|t2
operator|.
name|asSmallBytes
argument_list|(
name|buf2
argument_list|)
expr_stmt|;
name|byte
index|[]
name|arr2
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|buf2
operator|.
name|array
argument_list|()
argument_list|,
name|buf2
operator|.
name|position
argument_list|()
argument_list|)
decl_stmt|;
name|ByteBuffer
name|rbuf2
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|arr2
argument_list|)
decl_stmt|;
name|top
operator|.
name|add
argument_list|(
name|AVLTreeDigest
operator|.
name|fromBytes
argument_list|(
name|rbuf2
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|top
operator|.
name|quantile
argument_list|(
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|top
operator|.
name|quantile
argument_list|(
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|top
operator|.
name|quantile
argument_list|(
literal|0.9
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|XtestHLL
specifier|public
name|void
name|XtestHLL
parameter_list|()
block|{
name|HLLAgg
operator|.
name|HLLFactory
name|fac
init|=
operator|new
name|HLLAgg
operator|.
name|HLLFactory
argument_list|()
decl_stmt|;
name|HLL
name|hll
init|=
name|fac
operator|.
name|getHLL
argument_list|()
decl_stmt|;
name|hll
operator|.
name|addRaw
argument_list|(
literal|123456789
argument_list|)
expr_stmt|;
name|hll
operator|.
name|addRaw
argument_list|(
literal|987654321
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

