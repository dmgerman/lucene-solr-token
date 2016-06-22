begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|BitSet
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
name|analysis
operator|.
name|MockAnalyzer
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
name|Field
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|LeafReaderContext
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
name|BitSetIterator
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
name|FixedBitSet
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

begin_class
DECL|class|TestScorerPerf
specifier|public
class|class
name|TestScorerPerf
extends|extends
name|LuceneTestCase
block|{
DECL|field|validate
name|boolean
name|validate
init|=
literal|true
decl_stmt|;
comment|// set to false when doing performance testing
DECL|field|sets
name|FixedBitSet
index|[]
name|sets
decl_stmt|;
DECL|field|terms
name|Term
index|[]
name|terms
decl_stmt|;
DECL|field|s
name|IndexSearcher
name|s
decl_stmt|;
DECL|field|r
name|IndexReader
name|r
decl_stmt|;
DECL|field|d
name|Directory
name|d
decl_stmt|;
comment|// TODO: this should be setUp()....
DECL|method|createDummySearcher
specifier|public
name|void
name|createDummySearcher
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a dummy index with nothing in it.
comment|// This could possibly fail if Lucene starts checking for docid ranges...
name|d
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|s
operator|=
name|newSearcher
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|s
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|createRandomTerms
specifier|public
name|void
name|createRandomTerms
parameter_list|(
name|int
name|nDocs
parameter_list|,
name|int
name|nTerms
parameter_list|,
name|double
name|power
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
name|int
index|[]
name|freq
init|=
operator|new
name|int
index|[
name|nTerms
index|]
decl_stmt|;
name|terms
operator|=
operator|new
name|Term
index|[
name|nTerms
index|]
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
name|nTerms
condition|;
name|i
operator|++
control|)
block|{
name|int
name|f
init|=
operator|(
name|nTerms
operator|+
literal|1
operator|)
operator|-
name|i
decl_stmt|;
comment|// make first terms less frequent
name|freq
index|[
name|i
index|]
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
name|f
argument_list|,
name|power
argument_list|)
argument_list|)
expr_stmt|;
name|terms
index|[
name|i
index|]
operator|=
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|'A'
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nTerms
condition|;
name|j
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
name|freq
index|[
name|j
index|]
argument_list|)
operator|==
literal|0
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"f"
argument_list|,
name|terms
index|[
name|j
index|]
operator|.
name|text
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println(d);
block|}
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|randBitSet
specifier|public
name|FixedBitSet
name|randBitSet
parameter_list|(
name|int
name|sz
parameter_list|,
name|int
name|numBitsToSet
parameter_list|)
block|{
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|sz
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
name|numBitsToSet
condition|;
name|i
operator|++
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
DECL|method|randBitSets
specifier|public
name|FixedBitSet
index|[]
name|randBitSets
parameter_list|(
name|int
name|numSets
parameter_list|,
name|int
name|setSize
parameter_list|)
block|{
name|FixedBitSet
index|[]
name|sets
init|=
operator|new
name|FixedBitSet
index|[
name|numSets
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
name|sets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sets
index|[
name|i
index|]
operator|=
name|randBitSet
argument_list|(
name|setSize
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|setSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sets
return|;
block|}
DECL|class|CountingHitCollector
specifier|public
specifier|static
class|class
name|CountingHitCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|sum
name|int
name|sum
init|=
literal|0
decl_stmt|;
DECL|field|docBase
specifier|protected
name|int
name|docBase
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|sum
operator|+=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// use it to avoid any possibility of being eliminated by hotspot
block|}
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getSum
specifier|public
name|int
name|getSum
parameter_list|()
block|{
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|MatchingHitCollector
specifier|public
specifier|static
class|class
name|MatchingHitCollector
extends|extends
name|CountingHitCollector
block|{
DECL|field|answer
name|FixedBitSet
name|answer
decl_stmt|;
DECL|field|pos
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|MatchingHitCollector
specifier|public
name|MatchingHitCollector
parameter_list|(
name|FixedBitSet
name|answer
parameter_list|)
block|{
name|this
operator|.
name|answer
operator|=
name|answer
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|pos
operator|=
name|answer
operator|.
name|nextSetBit
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|!=
name|doc
operator|+
name|docBase
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Expected doc "
operator|+
name|pos
operator|+
literal|" but got "
operator|+
name|doc
operator|+
name|docBase
argument_list|)
throw|;
block|}
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|BitSetQuery
specifier|private
specifier|static
class|class
name|BitSetQuery
extends|extends
name|Query
block|{
DECL|field|docs
specifier|private
specifier|final
name|FixedBitSet
name|docs
decl_stmt|;
DECL|method|BitSetQuery
name|BitSetQuery
parameter_list|(
name|FixedBitSet
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
operator|new
name|BitSetIterator
argument_list|(
name|docs
argument_list|,
name|docs
operator|.
name|approximateCardinality
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"randomBitSetFilter"
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|docs
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|BitSetQuery
operator|)
name|other
operator|)
operator|.
name|docs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|classHash
argument_list|()
operator|+
name|docs
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|method|addClause
name|FixedBitSet
name|addClause
parameter_list|(
name|BooleanQuery
operator|.
name|Builder
name|bq
parameter_list|,
name|FixedBitSet
name|result
parameter_list|)
block|{
specifier|final
name|FixedBitSet
name|rnd
init|=
name|sets
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sets
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|BitSetQuery
argument_list|(
name|rnd
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
if|if
condition|(
name|validate
condition|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
name|result
operator|=
name|rnd
operator|.
name|clone
argument_list|()
expr_stmt|;
else|else
name|result
operator|.
name|and
argument_list|(
name|rnd
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|doConjunctions
specifier|public
name|int
name|doConjunctions
parameter_list|(
name|int
name|iter
parameter_list|,
name|int
name|maxClauses
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
literal|0
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|FixedBitSet
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|result
operator|=
name|addClause
argument_list|(
name|bq
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
name|CountingHitCollector
name|hc
init|=
name|validate
condition|?
operator|new
name|MatchingHitCollector
argument_list|(
name|result
argument_list|)
else|:
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
if|if
condition|(
name|validate
condition|)
name|assertEquals
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|hc
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// System.out.println(hc.getCount());
block|}
return|return
name|ret
return|;
block|}
DECL|method|doNestedConjunctions
specifier|public
name|int
name|doNestedConjunctions
parameter_list|(
name|int
name|iter
parameter_list|,
name|int
name|maxOuterClauses
parameter_list|,
name|int
name|maxClauses
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|long
name|nMatches
init|=
literal|0
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|oClauses
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxOuterClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|oq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|FixedBitSet
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|o
init|=
literal|0
init|;
name|o
operator|<
name|oClauses
condition|;
name|o
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|result
operator|=
name|addClause
argument_list|(
name|bq
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
name|oq
operator|.
name|add
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|// outer
name|CountingHitCollector
name|hc
init|=
name|validate
condition|?
operator|new
name|MatchingHitCollector
argument_list|(
name|result
argument_list|)
else|:
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|oq
operator|.
name|build
argument_list|()
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|nMatches
operator|+=
name|hc
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
if|if
condition|(
name|validate
condition|)
name|assertEquals
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|hc
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// System.out.println(hc.getCount());
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Average number of matches="
operator|+
operator|(
name|nMatches
operator|/
name|iter
operator|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|doTermConjunctions
specifier|public
name|int
name|doTermConjunctions
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|int
name|termsInIndex
parameter_list|,
name|int
name|maxClauses
parameter_list|,
name|int
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|long
name|nMatches
init|=
literal|0
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BitSet
name|termflag
init|=
operator|new
name|BitSet
argument_list|(
name|termsInIndex
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tnum
decl_stmt|;
comment|// don't pick same clause twice
name|tnum
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|termsInIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|termflag
operator|.
name|get
argument_list|(
name|tnum
argument_list|)
condition|)
name|tnum
operator|=
name|termflag
operator|.
name|nextClearBit
argument_list|(
name|tnum
argument_list|)
expr_stmt|;
if|if
condition|(
name|tnum
operator|<
literal|0
operator|||
name|tnum
operator|>=
name|termsInIndex
condition|)
name|tnum
operator|=
name|termflag
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|termflag
operator|.
name|set
argument_list|(
name|tnum
argument_list|)
expr_stmt|;
name|Query
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
name|tnum
index|]
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|CountingHitCollector
name|hc
init|=
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|nMatches
operator|+=
name|hc
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Average number of matches="
operator|+
operator|(
name|nMatches
operator|/
name|iter
operator|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|doNestedTermConjunctions
specifier|public
name|int
name|doNestedTermConjunctions
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|int
name|termsInIndex
parameter_list|,
name|int
name|maxOuterClauses
parameter_list|,
name|int
name|maxClauses
parameter_list|,
name|int
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|long
name|nMatches
init|=
literal|0
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|oClauses
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxOuterClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|oq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|o
init|=
literal|0
init|;
name|o
operator|<
name|oClauses
condition|;
name|o
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BitSet
name|termflag
init|=
operator|new
name|BitSet
argument_list|(
name|termsInIndex
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tnum
decl_stmt|;
comment|// don't pick same clause twice
name|tnum
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|termsInIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|termflag
operator|.
name|get
argument_list|(
name|tnum
argument_list|)
condition|)
name|tnum
operator|=
name|termflag
operator|.
name|nextClearBit
argument_list|(
name|tnum
argument_list|)
expr_stmt|;
if|if
condition|(
name|tnum
operator|<
literal|0
operator|||
name|tnum
operator|>=
literal|25
condition|)
name|tnum
operator|=
name|termflag
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|termflag
operator|.
name|set
argument_list|(
name|tnum
argument_list|)
expr_stmt|;
name|Query
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
name|tnum
index|]
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|// inner
name|oq
operator|.
name|add
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|// outer
name|CountingHitCollector
name|hc
init|=
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|oq
operator|.
name|build
argument_list|()
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|nMatches
operator|+=
name|hc
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Average number of matches="
operator|+
operator|(
name|nMatches
operator|/
name|iter
operator|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|doSloppyPhrase
specifier|public
name|int
name|doSloppyPhrase
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|int
name|termsInIndex
parameter_list|,
name|int
name|maxClauses
parameter_list|,
name|int
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
literal|0
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tnum
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|termsInIndex
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
call|(
name|char
call|)
argument_list|(
name|tnum
operator|+
literal|'A'
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// slop could be random too
name|builder
operator|.
name|setSlop
argument_list|(
name|termsInIndex
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|CountingHitCollector
name|hc
init|=
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|testConjunctions
specifier|public
name|void
name|testConjunctions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test many small sets... the bugs will be found on boundary conditions
name|createDummySearcher
argument_list|()
expr_stmt|;
name|validate
operator|=
literal|true
expr_stmt|;
name|sets
operator|=
name|randBitSets
argument_list|(
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|doConjunctions
argument_list|(
name|atLeast
argument_list|(
literal|10000
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|doNestedConjunctions
argument_list|(
name|atLeast
argument_list|(
literal|10000
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|3
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/***   int bigIter=10;    public void testConjunctionPerf() throws Exception {     r = newRandom();     createDummySearcher();     validate=false;     sets=randBitSets(32,1000000);     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doConjunctions(500,6);       long end = System.currentTimeMillis();       if (VERBOSE) System.out.println("milliseconds="+(end-start));     }     s.close();   }    public void testNestedConjunctionPerf() throws Exception {     r = newRandom();     createDummySearcher();     validate=false;     sets=randBitSets(32,1000000);     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doNestedConjunctions(500,3,3);       long end = System.currentTimeMillis();       if (VERBOSE) System.out.println("milliseconds="+(end-start));     }     s.close();   }     public void testConjunctionTerms() throws Exception {     r = newRandom();     validate=false;     RAMDirectory dir = new RAMDirectory();     if (VERBOSE) System.out.println("Creating index");     createRandomTerms(100000,25,.5, dir);     s = newSearcher(dir, true);     if (VERBOSE) System.out.println("Starting performance test");     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doTermConjunctions(s,25,5,1000);       long end = System.currentTimeMillis();       if (VERBOSE) System.out.println("milliseconds="+(end-start));     }     s.close();   }    public void testNestedConjunctionTerms() throws Exception {     r = newRandom();     validate=false;         RAMDirectory dir = new RAMDirectory();     if (VERBOSE) System.out.println("Creating index");     createRandomTerms(100000,25,.2, dir);     s = newSearcher(dir, true);     if (VERBOSE) System.out.println("Starting performance test");     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doNestedTermConjunctions(s,25,3,3,200);       long end = System.currentTimeMillis();       if (VERBOSE) System.out.println("milliseconds="+(end-start));     }     s.close();   }     public void testSloppyPhrasePerf() throws Exception {     r = newRandom();     validate=false;         RAMDirectory dir = new RAMDirectory();     if (VERBOSE) System.out.println("Creating index");     createRandomTerms(100000,25,2,dir);     s = newSearcher(dir, true);     if (VERBOSE) System.out.println("Starting performance test");     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doSloppyPhrase(s,25,2,1000);       long end = System.currentTimeMillis();       if (VERBOSE) System.out.println("milliseconds="+(end-start));     }     s.close();   }    ***/
block|}
end_class

end_unit

