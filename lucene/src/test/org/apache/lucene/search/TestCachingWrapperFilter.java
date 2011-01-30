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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|SerialMergeScheduler
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
name|SlowMultiReaderWrapper
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
name|OpenBitSet
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
name|OpenBitSetDISI
import|;
end_import

begin_class
DECL|class|TestCachingWrapperFilter
specifier|public
class|class
name|TestCachingWrapperFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCachingWorks
specifier|public
name|void
name|testCachingWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AtomicReaderContext
name|context
init|=
operator|(
name|AtomicReaderContext
operator|)
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|MockFilter
name|filter
init|=
operator|new
name|MockFilter
argument_list|()
decl_stmt|;
name|CachingWrapperFilter
name|cacher
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
comment|// first time, nested filter is called
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"first time"
argument_list|,
name|filter
operator|.
name|wasCalled
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure no exception if cache is holding the wrong docIdSet
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// second time, nested filter should not be called
name|filter
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"second time"
argument_list|,
name|filter
operator|.
name|wasCalled
argument_list|()
argument_list|)
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
DECL|method|testNullDocIdSet
specifier|public
name|void
name|testNullDocIdSet
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AtomicReaderContext
name|context
init|=
operator|(
name|AtomicReaderContext
operator|)
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
specifier|final
name|Filter
name|filter
init|=
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|CachingWrapperFilter
name|cacher
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
comment|// the caching filter should return the empty set constant
name|assertSame
argument_list|(
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
argument_list|,
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
argument_list|)
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
DECL|method|testNullDocIdSetIterator
specifier|public
name|void
name|testNullDocIdSetIterator
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AtomicReaderContext
name|context
init|=
operator|(
name|AtomicReaderContext
operator|)
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
specifier|final
name|Filter
name|filter
init|=
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|CachingWrapperFilter
name|cacher
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
comment|// the caching filter should return the empty set constant
name|assertSame
argument_list|(
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
argument_list|,
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
argument_list|)
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
DECL|method|assertDocIdSetCacheable
specifier|private
specifier|static
name|void
name|assertDocIdSetCacheable
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|boolean
name|shouldCacheable
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|reader
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|isAtomic
argument_list|)
expr_stmt|;
name|AtomicReaderContext
name|context
init|=
operator|(
name|AtomicReaderContext
operator|)
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
specifier|final
name|CachingWrapperFilter
name|cacher
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSet
name|originalSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSet
name|cachedSet
init|=
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cachedSet
operator|.
name|isCacheable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shouldCacheable
argument_list|,
name|originalSet
operator|.
name|isCacheable
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("Original: "+originalSet.getClass().getName()+" -- cached: "+cachedSet.getClass().getName());
if|if
condition|(
name|originalSet
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Cached DocIdSet must be of same class like uncached, if cacheable"
argument_list|,
name|originalSet
operator|.
name|getClass
argument_list|()
argument_list|,
name|cachedSet
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"Cached DocIdSet must be an OpenBitSet if the original one was not cacheable"
argument_list|,
name|cachedSet
operator|instanceof
name|OpenBitSetDISI
operator|||
name|cachedSet
operator|==
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIsCacheAble
specifier|public
name|void
name|testIsCacheAble
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
comment|// not cacheable:
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// returns default empty docidset, always cacheable:
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
name|NumericRangeFilter
operator|.
name|newIntRange
argument_list|(
literal|"test"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|10000
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
operator|-
literal|10000
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// is cacheable:
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
name|FieldCacheRangeFilter
operator|.
name|newIntRange
argument_list|(
literal|"test"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|20
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// a openbitset filter is always cacheable
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|OpenBitSet
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|true
argument_list|)
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
DECL|method|testEnforceDeletions
specifier|public
name|void
name|testEnforceDeletions
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
operator|.
comment|// asserts below requires no unexpected merges:
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// NOTE: cannot use writer.getReader because RIW (on
comment|// flipping a coin) may give us a newly opened reader,
comment|// but we use .reopen on this reader below and expect to
comment|// (must) get an NRT reader:
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
operator|.
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// add a doc, refresh the reader, and check that its there
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
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Filter
name|startFilter
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// ignore deletions
name|CachingWrapperFilter
name|filter
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|startFilter
argument_list|,
name|CachingWrapperFilter
operator|.
name|DeletesMode
operator|.
name|IGNORE
argument_list|)
decl_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ConstantScoreQuery
name|constantScore
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// now delete the doc, refresh the reader, and see that it's not there
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// force cache to regenerate:
name|filter
operator|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|startFilter
argument_list|,
name|CachingWrapperFilter
operator|.
name|DeletesMode
operator|.
name|RECACHE
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|constantScore
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// NOTE: important to hold ref here so GC doesn't clear
comment|// the cache entry!  Else the assert below may sometimes
comment|// fail:
name|IndexReader
name|oldReader
init|=
name|reader
decl_stmt|;
comment|// make sure we get a cache hit when we reopen reader
comment|// that had no change to deletions
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
name|oldReader
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|int
name|missCount
init|=
name|filter
operator|.
name|missCount
decl_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missCount
argument_list|,
name|filter
operator|.
name|missCount
argument_list|)
expr_stmt|;
comment|// now delete the doc, refresh the reader, and see that it's not there
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|missCount
operator|=
name|filter
operator|.
name|missCount
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missCount
operator|+
literal|1
argument_list|,
name|filter
operator|.
name|missCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// apply deletions dynamically
name|filter
operator|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|startFilter
argument_list|,
name|CachingWrapperFilter
operator|.
name|DeletesMode
operator|.
name|DYNAMIC
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|constantScore
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// now delete the doc, refresh the reader, and see that it's not there
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|missCount
operator|=
name|filter
operator|.
name|missCount
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// doesn't count as a miss
name|assertEquals
argument_list|(
name|missCount
argument_list|,
name|filter
operator|.
name|missCount
argument_list|)
expr_stmt|;
comment|// NOTE: silliness to make sure JRE does not optimize
comment|// away our holding onto oldReader to prevent
comment|// CachingWrapperFilter's WeakHashMap from dropping the
comment|// entry:
name|assertTrue
argument_list|(
name|oldReader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
DECL|method|refreshReader
specifier|private
specifier|static
name|IndexReader
name|refreshReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|oldReader
init|=
name|reader
decl_stmt|;
name|reader
operator|=
name|reader
operator|.
name|reopen
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
name|oldReader
condition|)
block|{
name|oldReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
block|}
end_class

end_unit

