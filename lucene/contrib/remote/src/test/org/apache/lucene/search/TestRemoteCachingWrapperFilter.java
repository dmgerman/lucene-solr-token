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
name|util
operator|.
name|Map
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
name|FilterManager
operator|.
name|FilterItem
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
name|Before
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests that the index is cached on the searcher side of things.  */
end_comment

begin_class
DECL|class|TestRemoteCachingWrapperFilter
specifier|public
class|class
name|TestRemoteCachingWrapperFilter
extends|extends
name|RemoteTestCase
block|{
DECL|field|indexStore
specifier|private
specifier|static
name|Directory
name|indexStore
decl_stmt|;
DECL|field|local
specifier|private
specifier|static
name|Searchable
name|local
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// construct an index
name|indexStore
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"test"
argument_list|,
literal|"test text"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"type"
argument_list|,
literal|"A"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"other"
argument_list|,
literal|"other test text"
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
name|ANALYZED
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
comment|//Need a second document to search for
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"test"
argument_list|,
literal|"test text"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"type"
argument_list|,
literal|"B"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"other"
argument_list|,
literal|"other test text"
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
name|ANALYZED
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
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|local
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|startServer
argument_list|(
name|local
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
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
comment|// to support test iteration> 1
name|Map
argument_list|<
name|Integer
argument_list|,
name|FilterItem
argument_list|>
name|cache
init|=
name|FilterManager
operator|.
name|getInstance
argument_list|()
operator|.
name|cache
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|local
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|search
specifier|private
specifier|static
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|hitNumber
parameter_list|,
name|String
name|typeValue
parameter_list|)
throws|throws
name|Exception
block|{
name|Searchable
index|[]
name|searchables
init|=
block|{
name|lookupRemote
argument_list|()
block|}
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
name|searchables
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|Document
name|document
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|result
index|[
name|hitNumber
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"document is null and it shouldn't be"
argument_list|,
name|document
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|typeValue
argument_list|,
name|document
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document.getFields() Size: "
operator|+
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|3
argument_list|,
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTermRemoteFilter
specifier|public
name|void
name|testTermRemoteFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|CachingWrapperFilterHelper
name|cwfh
init|=
operator|new
name|CachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// This is what we are fixing - if one uses a CachingWrapperFilter(Helper) it will never
comment|// cache the filter on the remote site
name|cwfh
operator|.
name|setShouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|cwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|cwfh
operator|.
name|setShouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|cwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// This is how we fix caching - we wrap a Filter in the RemoteCachingWrapperFilter(Handler - for testing)
comment|// to cache the Filter on the searcher (remote) side
name|RemoteCachingWrapperFilterHelper
name|rcwfh
init|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
name|cwfh
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// 2nd time we do the search, we should be using the cached Filter
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// assert that we get the same cached Filter, even if we create a new instance of RemoteCachingWrapperFilter(Helper)
comment|// this should pass because the Filter parameters are the same, and the cache uses Filter's hashCode() as cache keys,
comment|// and Filters' hashCode() builds on Filter parameters, not the Filter instance itself
name|rcwfh
operator|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|rcwfh
operator|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
comment|// assert that we get a non-cached version of the Filter because this is a new Query (type:b)
name|rcwfh
operator|=
operator|new
name|RemoteCachingWrapperFilterHelper
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rcwfh
operator|.
name|shouldHaveCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|,
name|rcwfh
argument_list|,
literal|0
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

