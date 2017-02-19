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
name|ArrayList
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
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Analyzer
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
operator|.
name|Store
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
name|IndexWriterConfig
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|search
operator|.
name|Scorer
operator|.
name|ChildScorer
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
name|search
operator|.
name|similarities
operator|.
name|ClassicSimilarity
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

begin_comment
comment|// TODO: refactor to a base class, that collects freqs from the scorer tree
end_comment

begin_comment
comment|// and test all queries with it
end_comment

begin_class
DECL|class|TestBooleanQueryVisitSubscorers
specifier|public
class|class
name|TestBooleanQueryVisitSubscorers
extends|extends
name|LuceneTestCase
block|{
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|scorerSearcher
name|IndexSearcher
name|scorerSearcher
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|F1
specifier|static
specifier|final
name|String
name|F1
init|=
literal|"title"
decl_stmt|;
DECL|field|F2
specifier|static
specifier|final
name|String
name|F2
init|=
literal|"body"
decl_stmt|;
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
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|config
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// we will use docids to validate
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
literal|"lucene"
argument_list|,
literal|"lucene is a very popular search engine library"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
literal|"solr"
argument_list|,
literal|"solr is a very popular search server and is using lucene"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|(
literal|"nutch"
argument_list|,
literal|"nutch is an internet search engine with web crawler and is using lucene and hadoop"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// we do not use newSearcher because the assertingXXX layers break
comment|// the toString representations we are relying on
comment|// TODO: clean that up
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|ClassicSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|scorerSearcher
operator|=
operator|new
name|ScorerIndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|scorerSearcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|ClassicSimilarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testDisjunctions
specifier|public
name|void
name|testDisjunctions
parameter_list|()
throws|throws
name|IOException
block|{
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
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F1
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"search"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|tfs
init|=
name|getDocCounts
argument_list|(
name|scorerSearcher
argument_list|,
name|bq
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tfs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3 documents
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f1:lucene + f2:lucene + f2:search
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f2:search + f2:lucene
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f2:search + f2:lucene
block|}
DECL|method|testNestedDisjunctions
specifier|public
name|void
name|testNestedDisjunctions
parameter_list|()
throws|throws
name|IOException
block|{
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
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F1
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"search"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|tfs
init|=
name|getDocCounts
argument_list|(
name|scorerSearcher
argument_list|,
name|bq
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tfs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3 documents
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f1:lucene + f2:lucene + f2:search
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f2:search + f2:lucene
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f2:search + f2:lucene
block|}
DECL|method|testConjunctions
specifier|public
name|void
name|testConjunctions
parameter_list|()
throws|throws
name|IOException
block|{
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
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"is"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|tfs
init|=
name|getDocCounts
argument_list|(
name|searcher
argument_list|,
name|bq
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tfs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3 documents
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f2:lucene + f2:is
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f2:is + f2:is + f2:lucene
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tfs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// f2:is + f2:is + f2:lucene
block|}
DECL|method|doc
specifier|static
name|Document
name|doc
parameter_list|(
name|String
name|v1
parameter_list|,
name|String
name|v2
parameter_list|)
block|{
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
operator|new
name|TextField
argument_list|(
name|F1
argument_list|,
name|v1
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|F2
argument_list|,
name|v2
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|getDocCounts
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|getDocCounts
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|MyCollector
name|collector
init|=
operator|new
name|MyCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|collector
operator|.
name|docCounts
return|;
block|}
DECL|class|MyCollector
specifier|static
class|class
name|MyCollector
extends|extends
name|FilterCollector
block|{
DECL|field|docCounts
specifier|public
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|docCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|tqsSet
specifier|private
specifier|final
name|Set
argument_list|<
name|Scorer
argument_list|>
name|tqsSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MyCollector
name|MyCollector
parameter_list|()
block|{
name|super
argument_list|(
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docBase
init|=
name|context
operator|.
name|docBase
decl_stmt|;
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|tqsSet
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fillLeaves
argument_list|(
name|scorer
argument_list|,
name|tqsSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|freq
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Scorer
name|scorer
range|:
name|tqsSet
control|)
block|{
if|if
condition|(
name|doc
operator|==
name|scorer
operator|.
name|docID
argument_list|()
condition|)
block|{
name|freq
operator|+=
name|scorer
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
block|}
name|docCounts
operator|.
name|put
argument_list|(
name|doc
operator|+
name|docBase
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|fillLeaves
specifier|private
name|void
name|fillLeaves
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|Set
argument_list|<
name|Scorer
argument_list|>
name|set
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|scorer
operator|.
name|getWeight
argument_list|()
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|TermQuery
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|ChildScorer
name|child
range|:
name|scorer
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|fillLeaves
argument_list|(
name|child
operator|.
name|child
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|()
block|{
return|return
operator|(
operator|(
name|TopDocsCollector
argument_list|<
name|?
argument_list|>
operator|)
name|in
operator|)
operator|.
name|topDocs
argument_list|()
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docCounts
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
DECL|method|testDisjunctionMatches
specifier|public
name|void
name|testDisjunctionMatches
parameter_list|()
throws|throws
name|IOException
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F1
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|PhraseQuery
argument_list|(
name|F2
argument_list|,
literal|"search"
argument_list|,
literal|"engine"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Weight
name|w1
init|=
name|scorerSearcher
operator|.
name|createNormalizedWeight
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|s1
init|=
name|w1
operator|.
name|scorer
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s1
operator|.
name|iterator
argument_list|()
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|s1
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F1
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|PhraseQuery
argument_list|(
name|F2
argument_list|,
literal|"search"
argument_list|,
literal|"library"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Weight
name|w2
init|=
name|scorerSearcher
operator|.
name|createNormalizedWeight
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|s2
init|=
name|w2
operator|.
name|scorer
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s2
operator|.
name|iterator
argument_list|()
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s2
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinShouldMatchMatches
specifier|public
name|void
name|testMinShouldMatchMatches
parameter_list|()
throws|throws
name|IOException
block|{
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
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F1
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|PhraseQuery
argument_list|(
name|F2
argument_list|,
literal|"search"
argument_list|,
literal|"library"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Weight
name|w
init|=
name|scorerSearcher
operator|.
name|createNormalizedWeight
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|s
init|=
name|w
operator|.
name|scorer
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|iterator
argument_list|()
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|s
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetChildrenMinShouldMatchSumScorer
specifier|public
name|void
name|testGetChildrenMinShouldMatchSumScorer
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"nutch"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"web"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"crawler"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|ScorerSummarizingCollector
name|collector
init|=
operator|new
name|ScorerSummarizingCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|collector
operator|.
name|getNumHits
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|collector
operator|.
name|getSummaries
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|summary
range|:
name|collector
operator|.
name|getSummaries
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"ConjunctionScorer\n"
operator|+
literal|"    MUST ConstantScoreScorer\n"
operator|+
literal|"    MUST MinShouldMatchSumScorer\n"
operator|+
literal|"            SHOULD TermScorer body:web\n"
operator|+
literal|"            SHOULD TermScorer body:crawler\n"
operator|+
literal|"            SHOULD TermScorer body:nutch"
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGetChildrenBoosterScorer
specifier|public
name|void
name|testGetChildrenBoosterScorer
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"nutch"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F2
argument_list|,
literal|"miss"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|ScorerSummarizingCollector
name|collector
init|=
operator|new
name|ScorerSummarizingCollector
argument_list|()
decl_stmt|;
name|scorerSearcher
operator|.
name|search
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|collector
operator|.
name|getNumHits
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|collector
operator|.
name|getSummaries
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|summary
range|:
name|collector
operator|.
name|getSummaries
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"TermScorer body:nutch"
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ScorerSummarizingCollector
specifier|private
specifier|static
class|class
name|ScorerSummarizingCollector
implements|implements
name|Collector
block|{
DECL|field|summaries
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|summaries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numHits
specifier|private
name|int
name|numHits
index|[]
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|method|getNumHits
specifier|public
name|int
name|getNumHits
parameter_list|()
block|{
return|return
name|numHits
index|[
literal|0
index|]
return|;
block|}
DECL|method|getSummaries
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSummaries
parameter_list|()
block|{
return|return
name|summaries
return|;
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
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LeafCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|summarizeScorer
argument_list|(
name|builder
argument_list|,
name|scorer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|summaries
operator|.
name|add
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|numHits
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|summarizeScorer
specifier|private
specifier|static
name|void
name|summarizeScorer
parameter_list|(
specifier|final
name|StringBuilder
name|builder
parameter_list|,
specifier|final
name|Scorer
name|scorer
parameter_list|,
specifier|final
name|int
name|indent
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|append
argument_list|(
name|scorer
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|scorer
operator|instanceof
name|TermScorer
condition|)
block|{
name|TermQuery
name|termQuery
init|=
operator|(
name|TermQuery
operator|)
name|scorer
operator|.
name|getWeight
argument_list|()
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|ChildScorer
name|childScorer
range|:
name|scorer
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|indent
argument_list|(
name|builder
argument_list|,
name|indent
operator|+
literal|1
argument_list|)
operator|.
name|append
argument_list|(
name|childScorer
operator|.
name|relationship
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|summarizeScorer
argument_list|(
name|builder
argument_list|,
name|childScorer
operator|.
name|child
argument_list|,
name|indent
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|indent
specifier|private
specifier|static
name|StringBuilder
name|indent
parameter_list|(
specifier|final
name|StringBuilder
name|builder
parameter_list|,
specifier|final
name|int
name|indent
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indent
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"    "
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
block|}
end_class

end_unit

