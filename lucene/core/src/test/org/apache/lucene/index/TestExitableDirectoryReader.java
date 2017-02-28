begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|ExitableDirectoryReader
operator|.
name|ExitingReaderException
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
name|IndexSearcher
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
name|PrefixQuery
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
name|Query
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
name|BytesRef
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
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/**  * Test that uses a default/lucene Implementation of {@link QueryTimeout}  * to exit out long running queries that take too long to iterate over Terms.  */
end_comment

begin_class
DECL|class|TestExitableDirectoryReader
specifier|public
class|class
name|TestExitableDirectoryReader
extends|extends
name|LuceneTestCase
block|{
DECL|class|TestReader
specifier|private
specifier|static
class|class
name|TestReader
extends|extends
name|FilterLeafReader
block|{
DECL|class|TestFields
specifier|private
specifier|static
class|class
name|TestFields
extends|extends
name|FilterFields
block|{
DECL|method|TestFields
name|TestFields
parameter_list|(
name|Fields
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TestTerms
argument_list|(
name|super
operator|.
name|terms
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|TestTerms
specifier|private
specifier|static
class|class
name|TestTerms
extends|extends
name|FilterTerms
block|{
DECL|method|TestTerms
name|TestTerms
parameter_list|(
name|Terms
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TestTermsEnum
argument_list|(
name|super
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|TestTermsEnum
specifier|private
specifier|static
class|class
name|TestTermsEnum
extends|extends
name|FilterTermsEnum
block|{
DECL|method|TestTermsEnum
specifier|public
name|TestTermsEnum
parameter_list|(
name|TermsEnum
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**        * Sleep between iterations to timeout things.        */
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
comment|// Sleep for 100ms before each .next() call.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|method|TestReader
specifier|public
name|TestReader
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TestFields
argument_list|(
name|super
operator|.
name|fields
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheHelper
specifier|public
name|CacheHelper
name|getCoreCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheHelper
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getReaderCacheHelper
argument_list|()
return|;
block|}
block|}
comment|/**    * Tests timing out of TermsEnum iterations    * @throws Exception on error    */
annotation|@
name|Ignore
argument_list|(
literal|"this test relies on wall clock time and sometimes false fails"
argument_list|)
DECL|method|testExitableFilterIndexReader
specifier|public
name|void
name|testExitableFilterIndexReader
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
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
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"default"
argument_list|,
literal|"one two"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"default"
argument_list|,
literal|"one three"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"default"
argument_list|,
literal|"ones two four"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|directoryReader
decl_stmt|;
name|DirectoryReader
name|exitableDirectoryReader
decl_stmt|;
name|IndexReader
name|reader
decl_stmt|;
name|IndexSearcher
name|searcher
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"default"
argument_list|,
literal|"o"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Set a fairly high timeout value (1 second) and expect the query to complete in that time frame.
comment|// Not checking the validity of the result, all we are bothered about in this test is the timing out.
name|directoryReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|exitableDirectoryReader
operator|=
operator|new
name|ExitableDirectoryReader
argument_list|(
name|directoryReader
argument_list|,
operator|new
name|QueryTimeoutImpl
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|TestReader
argument_list|(
name|getOnlyLeafReader
argument_list|(
name|exitableDirectoryReader
argument_list|)
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
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Set a really low timeout value (1 millisecond) and expect an Exception
name|directoryReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|exitableDirectoryReader
operator|=
operator|new
name|ExitableDirectoryReader
argument_list|(
name|directoryReader
argument_list|,
operator|new
name|QueryTimeoutImpl
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|TestReader
argument_list|(
name|getOnlyLeafReader
argument_list|(
name|exitableDirectoryReader
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSearcher
name|slowSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|ExitingReaderException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|slowSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Set maximum time out and expect the query to complete.
comment|// Not checking the validity of the result, all we are bothered about in this test is the timing out.
name|directoryReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|exitableDirectoryReader
operator|=
operator|new
name|ExitableDirectoryReader
argument_list|(
name|directoryReader
argument_list|,
operator|new
name|QueryTimeoutImpl
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|TestReader
argument_list|(
name|getOnlyLeafReader
argument_list|(
name|exitableDirectoryReader
argument_list|)
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
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Set a negative time allowed and expect the query to complete (should disable timeouts)
comment|// Not checking the validity of the result, all we are bothered about in this test is the timing out.
name|directoryReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|exitableDirectoryReader
operator|=
operator|new
name|ExitableDirectoryReader
argument_list|(
name|directoryReader
argument_list|,
operator|new
name|QueryTimeoutImpl
argument_list|(
operator|-
literal|189034L
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|TestReader
argument_list|(
name|getOnlyLeafReader
argument_list|(
name|exitableDirectoryReader
argument_list|)
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
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

