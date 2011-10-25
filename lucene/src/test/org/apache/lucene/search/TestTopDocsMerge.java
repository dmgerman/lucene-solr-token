begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|NumericField
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
name|document
operator|.
name|StringField
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
name|document
operator|.
name|TextField
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|RandomIndexWriter
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
name|index
operator|.
name|Term
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
name|store
operator|.
name|Directory
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
name|lucene
operator|.
name|util
operator|.
name|ReaderUtil
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestTopDocsMerge
specifier|public
class|class
name|TestTopDocsMerge
extends|extends
name|LuceneTestCase
block|{
DECL|class|ShardSearcher
specifier|private
specifier|static
class|class
name|ShardSearcher
extends|extends
name|IndexSearcher
block|{
DECL|field|ctx
specifier|private
specifier|final
name|IndexReader
operator|.
name|AtomicReaderContext
index|[]
name|ctx
decl_stmt|;
DECL|method|ShardSearcher
specifier|public
name|ShardSearcher
parameter_list|(
name|IndexReader
operator|.
name|AtomicReaderContext
name|ctx
parameter_list|,
name|IndexReader
operator|.
name|ReaderContext
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
operator|new
name|IndexReader
operator|.
name|AtomicReaderContext
index|[]
block|{
name|ctx
block|}
expr_stmt|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|search
argument_list|(
name|ctx
argument_list|,
name|weight
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|ctx
argument_list|,
name|weight
argument_list|,
literal|null
argument_list|,
name|topN
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ShardSearcher("
operator|+
name|ctx
index|[
literal|0
index|]
operator|+
literal|")"
return|;
block|}
block|}
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|//final int numDocs = atLeast(50);
specifier|final
name|String
index|[]
name|tokens
init|=
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|}
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: make index"
argument_list|)
expr_stmt|;
block|}
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// w.setDoRandomOptimize(false);
comment|// w.w.getConfig().setMaxBufferedDocs(atLeast(100));
specifier|final
name|String
index|[]
name|content
init|=
operator|new
name|String
index|[
name|atLeast
argument_list|(
literal|20
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|contentIDX
init|=
literal|0
init|;
name|contentIDX
operator|<
name|content
operator|.
name|length
condition|;
name|contentIDX
operator|++
control|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numTokens
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|tokenIDX
init|=
literal|0
init|;
name|tokenIDX
operator|<
name|numTokens
condition|;
name|tokenIDX
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|tokens
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|tokens
operator|.
name|length
argument_list|)
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|content
index|[
name|contentIDX
index|]
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|docIDX
init|=
literal|0
init|;
name|docIDX
operator|<
name|numDocs
condition|;
name|docIDX
operator|++
control|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"string"
argument_list|,
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"text"
argument_list|,
name|content
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|content
operator|.
name|length
argument_list|)
index|]
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"float"
argument_list|)
operator|.
name|setFloatValue
argument_list|(
name|random
operator|.
name|nextFloat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|intValue
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|17
condition|)
block|{
name|intValue
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|17
condition|)
block|{
name|intValue
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|intValue
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
literal|"int"
argument_list|)
operator|.
name|setIntValue
argument_list|(
name|intValue
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  doc="
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// NOTE: sometimes reader has just one segment, which is
comment|// important to test
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|IndexReader
index|[]
name|subReaders
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|subReaders
operator|==
literal|null
condition|)
block|{
name|subReaders
operator|=
operator|new
name|IndexReader
index|[]
block|{
name|searcher
operator|.
name|getIndexReader
argument_list|()
block|}
expr_stmt|;
block|}
specifier|final
name|ShardSearcher
index|[]
name|subSearchers
init|=
operator|new
name|ShardSearcher
index|[
name|subReaders
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|IndexReader
operator|.
name|ReaderContext
name|ctx
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|instanceof
name|IndexReader
operator|.
name|AtomicReaderContext
condition|)
block|{
assert|assert
name|subSearchers
operator|.
name|length
operator|==
literal|1
assert|;
name|subSearchers
index|[
literal|0
index|]
operator|=
operator|new
name|ShardSearcher
argument_list|(
operator|(
name|IndexReader
operator|.
name|AtomicReaderContext
operator|)
name|ctx
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|IndexReader
operator|.
name|CompositeReaderContext
name|compCTX
init|=
operator|(
name|IndexReader
operator|.
name|CompositeReaderContext
operator|)
name|ctx
decl_stmt|;
for|for
control|(
name|int
name|searcherIDX
init|=
literal|0
init|;
name|searcherIDX
operator|<
name|subSearchers
operator|.
name|length
condition|;
name|searcherIDX
operator|++
control|)
block|{
name|subSearchers
index|[
name|searcherIDX
index|]
operator|=
operator|new
name|ShardSearcher
argument_list|(
name|compCTX
operator|.
name|leaves
index|[
name|searcherIDX
index|]
argument_list|,
name|compCTX
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|SortField
argument_list|>
name|sortFields
init|=
operator|new
name|ArrayList
argument_list|<
name|SortField
argument_list|>
argument_list|()
decl_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"string"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"string"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"int"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"int"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"float"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"float"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOC
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOC
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|docStarts
init|=
operator|new
name|int
index|[
name|subSearchers
operator|.
name|length
index|]
decl_stmt|;
name|int
name|docBase
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|subIDX
init|=
literal|0
init|;
name|subIDX
operator|<
name|docStarts
operator|.
name|length
condition|;
name|subIDX
operator|++
control|)
block|{
name|docStarts
index|[
name|subIDX
index|]
operator|=
name|docBase
expr_stmt|;
name|docBase
operator|+=
name|subReaders
index|[
name|subIDX
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
comment|//System.out.println("docStarts[" + subIDX + "]=" + docStarts[subIDX]);
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|1000
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
comment|// TODO: custom FieldComp...
specifier|final
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
name|tokens
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|tokens
operator|.
name|length
argument_list|)
index|]
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Sort
name|sort
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|4
condition|)
block|{
comment|// Sort by score
name|sort
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|SortField
index|[]
name|randomSortFields
init|=
operator|new
name|SortField
index|[
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|sortIDX
init|=
literal|0
init|;
name|sortIDX
operator|<
name|randomSortFields
operator|.
name|length
condition|;
name|sortIDX
operator|++
control|)
block|{
name|randomSortFields
index|[
name|sortIDX
index|]
operator|=
name|sortFields
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|sortFields
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|randomSortFields
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numHits
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|numDocs
operator|+
literal|5
argument_list|)
decl_stmt|;
comment|//final int numHits = 5;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: search query="
operator|+
name|query
operator|+
literal|" sort="
operator|+
name|sort
operator|+
literal|" numHits="
operator|+
name|numHits
argument_list|)
expr_stmt|;
block|}
comment|// First search on whole index:
specifier|final
name|TopDocs
name|topHits
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|topHits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TopFieldCollector
name|c
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|topHits
operator|=
name|c
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  top search: "
operator|+
name|topHits
operator|.
name|totalHits
operator|+
literal|" totalHits; hits="
operator|+
operator|(
name|topHits
operator|.
name|scoreDocs
operator|==
literal|null
condition|?
literal|"null"
else|:
name|topHits
operator|.
name|scoreDocs
operator|.
name|length
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|topHits
operator|.
name|scoreDocs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|hitIDX
init|=
literal|0
init|;
name|hitIDX
operator|<
name|topHits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hitIDX
operator|++
control|)
block|{
specifier|final
name|ScoreDoc
name|sd
init|=
name|topHits
operator|.
name|scoreDocs
index|[
name|hitIDX
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    doc="
operator|+
name|sd
operator|.
name|doc
operator|+
literal|" score="
operator|+
name|sd
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// ... then all shards:
specifier|final
name|Weight
name|w
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|TopDocs
index|[]
name|shardHits
init|=
operator|new
name|TopDocs
index|[
name|subSearchers
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|shardIDX
init|=
literal|0
init|;
name|shardIDX
operator|<
name|subSearchers
operator|.
name|length
condition|;
name|shardIDX
operator|++
control|)
block|{
specifier|final
name|TopDocs
name|subHits
decl_stmt|;
specifier|final
name|ShardSearcher
name|subSearcher
init|=
name|subSearchers
index|[
name|shardIDX
index|]
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|subHits
operator|=
name|subSearcher
operator|.
name|search
argument_list|(
name|w
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TopFieldCollector
name|c
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|subSearcher
operator|.
name|search
argument_list|(
name|w
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|subHits
operator|=
name|c
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
name|shardHits
index|[
name|shardIDX
index|]
operator|=
name|subHits
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  shard="
operator|+
name|shardIDX
operator|+
literal|" "
operator|+
name|subHits
operator|.
name|totalHits
operator|+
literal|" totalHits hits="
operator|+
operator|(
name|subHits
operator|.
name|scoreDocs
operator|==
literal|null
condition|?
literal|"null"
else|:
name|subHits
operator|.
name|scoreDocs
operator|.
name|length
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|subHits
operator|.
name|scoreDocs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ScoreDoc
name|sd
range|:
name|subHits
operator|.
name|scoreDocs
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    doc="
operator|+
name|sd
operator|.
name|doc
operator|+
literal|" score="
operator|+
name|sd
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Merge:
specifier|final
name|TopDocs
name|mergedHits
init|=
name|TopDocs
operator|.
name|merge
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
name|shardHits
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergedHits
operator|.
name|scoreDocs
operator|!=
literal|null
condition|)
block|{
comment|// Make sure the returned shards are correct:
for|for
control|(
name|int
name|hitIDX
init|=
literal|0
init|;
name|hitIDX
operator|<
name|mergedHits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hitIDX
operator|++
control|)
block|{
specifier|final
name|ScoreDoc
name|sd
init|=
name|mergedHits
operator|.
name|scoreDocs
index|[
name|hitIDX
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"doc="
operator|+
name|sd
operator|.
name|doc
operator|+
literal|" wrong shard"
argument_list|,
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|sd
operator|.
name|doc
argument_list|,
name|docStarts
argument_list|)
argument_list|,
name|sd
operator|.
name|shardIndex
argument_list|)
expr_stmt|;
block|}
block|}
name|_TestUtil
operator|.
name|assertEquals
argument_list|(
name|topHits
argument_list|,
name|mergedHits
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

