begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|TestUtil
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
name|SolrServerException
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
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Tests that highlighting doesn't break on grouped documents  * with duplicate unique key fields stored on multiple shards.  */
end_comment

begin_class
DECL|class|TestHighlightDedupGrouping
specifier|public
class|class
name|TestHighlightDedupGrouping
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|id_s1
specifier|private
specifier|static
specifier|final
name|String
name|id_s1
init|=
literal|"id_s1"
decl_stmt|;
comment|// string copy of the id for highlighting
DECL|field|group_ti1
specifier|private
specifier|static
specifier|final
name|String
name|group_ti1
init|=
literal|"group_ti1"
decl_stmt|;
DECL|field|shard_i1
specifier|private
specifier|static
specifier|final
name|String
name|shard_i1
init|=
literal|"shard_i1"
decl_stmt|;
DECL|method|TestHighlightDedupGrouping
specifier|public
name|TestHighlightDedupGrouping
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|shardCount
operator|=
literal|2
expr_stmt|;
block|}
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
name|basicTest
argument_list|()
expr_stmt|;
name|randomizedTest
argument_list|()
expr_stmt|;
block|}
DECL|method|basicTest
specifier|private
name|void
name|basicTest
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
literal|"grouped"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
comment|// distrib grouping doesn't guarantee order of top level group commands
name|int
name|docid
init|=
literal|1
decl_stmt|;
name|int
name|group
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|shard
init|=
literal|0
init|;
name|shard
operator|<
name|shardCount
condition|;
operator|++
name|shard
control|)
block|{
name|addDoc
argument_list|(
name|docid
argument_list|,
name|group
argument_list|,
name|shard
argument_list|)
expr_stmt|;
comment|// add the same doc to both shards
name|clients
operator|.
name|get
argument_list|(
name|shard
argument_list|)
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|QueryResponse
name|rsp
init|=
name|queryServer
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
name|id_s1
operator|+
literal|":"
operator|+
name|docid
argument_list|,
literal|"shards"
argument_list|,
name|shards
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|id_s1
argument_list|,
literal|"group.limit"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|shardCount
argument_list|)
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
name|id_s1
argument_list|)
argument_list|)
decl_stmt|;
comment|// The number of highlit documents should be the same as the de-duplicated docs
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getHighlighting
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|randomizedTest
specifier|private
name|void
name|randomizedTest
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
literal|"grouped"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
comment|// distrib grouping doesn't guarantee order of top level group commands
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|int
name|numGroups
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
operator|/
literal|50
argument_list|)
decl_stmt|;
name|int
index|[]
name|docsInGroup
init|=
operator|new
name|int
index|[
name|numGroups
operator|+
literal|1
index|]
decl_stmt|;
name|int
name|percentDuplicates
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|25
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docid
init|=
literal|0
init|;
name|docid
operator|<
name|numDocs
condition|;
operator|++
name|docid
control|)
block|{
name|int
name|group
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numGroups
argument_list|)
decl_stmt|;
operator|++
name|docsInGroup
index|[
name|group
index|]
expr_stmt|;
name|boolean
name|makeDuplicate
init|=
literal|0
operator|==
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|numDocs
operator|/
name|percentDuplicates
argument_list|)
decl_stmt|;
if|if
condition|(
name|makeDuplicate
condition|)
block|{
for|for
control|(
name|int
name|shard
init|=
literal|0
init|;
name|shard
operator|<
name|shardCount
condition|;
operator|++
name|shard
control|)
block|{
name|addDoc
argument_list|(
name|docid
argument_list|,
name|group
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|shard
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|shardCount
operator|-
literal|1
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
name|docid
argument_list|,
name|group
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|shard
init|=
literal|0
init|;
name|shard
operator|<
name|shardCount
condition|;
operator|++
name|shard
control|)
block|{
name|clients
operator|.
name|get
argument_list|(
name|shard
argument_list|)
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|group
init|=
literal|1
init|;
name|group
operator|<=
name|numGroups
condition|;
operator|++
name|group
control|)
block|{
name|QueryResponse
name|rsp
init|=
name|queryServer
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
name|group_ti1
operator|+
literal|":"
operator|+
name|group
operator|+
literal|" AND "
operator|+
name|id_s1
operator|+
literal|":[* TO *]"
argument_list|,
literal|"start"
argument_list|,
literal|"0"
argument_list|,
literal|"rows"
argument_list|,
literal|""
operator|+
name|numDocs
argument_list|,
literal|"fl"
argument_list|,
name|id_s1
operator|+
literal|","
operator|+
name|shard_i1
argument_list|,
literal|"sort"
argument_list|,
name|id_s1
operator|+
literal|" asc"
argument_list|,
literal|"shards"
argument_list|,
name|shards
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|id_s1
argument_list|,
literal|"group.limit"
argument_list|,
literal|""
operator|+
name|numDocs
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
literal|"*"
argument_list|,
literal|"hl.requireFieldMatch"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
comment|// The number of highlit documents should be the same as the de-duplicated docs for this group
name|assertEquals
argument_list|(
name|docsInGroup
index|[
name|group
index|]
argument_list|,
name|rsp
operator|.
name|getHighlighting
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|int
name|docid
parameter_list|,
name|int
name|group
parameter_list|,
name|int
name|shard
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|id
argument_list|,
name|docid
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|id_s1
argument_list|,
name|docid
argument_list|)
expr_stmt|;
comment|// string copy of the id for highlighting
name|doc
operator|.
name|addField
argument_list|(
name|group_ti1
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|shard_i1
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|clients
operator|.
name|get
argument_list|(
name|shard
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

