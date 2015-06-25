begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|search
operator|.
name|DocIdSetIterator
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
name|BaseDirectoryWrapper
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

begin_class
DECL|class|TestFilterLeafReader
specifier|public
class|class
name|TestFilterLeafReader
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
comment|/** Filter that only permits terms containing 'e'.*/
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
comment|/** Scan for terms containing the letter 'e'.*/
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
name|BytesRef
name|text
decl_stmt|;
while|while
condition|(
operator|(
name|text
operator|=
name|in
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|text
operator|.
name|utf8ToString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'e'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
return|return
name|text
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|postings
specifier|public
name|PostingsEnum
name|postings
parameter_list|(
name|PostingsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TestPositions
argument_list|(
name|super
operator|.
name|postings
argument_list|(
name|reuse
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
operator|(
name|FilterPostingsEnum
operator|)
name|reuse
operator|)
operator|.
name|in
argument_list|,
name|flags
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Filter that only returns odd numbered documents. */
DECL|class|TestPositions
specifier|private
specifier|static
class|class
name|TestPositions
extends|extends
name|FilterPostingsEnum
block|{
DECL|method|TestPositions
specifier|public
name|TestPositions
parameter_list|(
name|PostingsEnum
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/** Scan for odd numbered documents. */
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|in
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
operator|(
name|doc
operator|%
literal|2
operator|)
operator|==
literal|1
condition|)
return|return
name|doc
return|;
block|}
return|return
name|NO_MORE_DOCS
return|;
block|}
block|}
DECL|method|TestReader
specifier|public
name|TestReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
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
block|}
comment|/**    * Tests the IndexReader.getFieldNames implementation    * @throws Exception on error    */
DECL|method|testFilterIndexReader
specifier|public
name|void
name|testFilterIndexReader
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
literal|"two four"
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
name|close
argument_list|()
expr_stmt|;
name|Directory
name|target
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// We mess with the postings so this can fail:
operator|(
operator|(
name|BaseDirectoryWrapper
operator|)
name|target
operator|)
operator|.
name|setCrossCheckTermVectorsOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|target
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
expr_stmt|;
try|try
init|(
name|LeafReader
name|reader
init|=
operator|new
name|TestReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
argument_list|)
init|)
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|TermsEnum
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"default"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|terms
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'e'
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|terms
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"one"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|PostingsEnum
name|positions
init|=
name|terms
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|ALL
argument_list|)
decl_stmt|;
while|while
condition|(
name|positions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|assertTrue
argument_list|(
operator|(
name|positions
operator|.
name|docID
argument_list|()
operator|%
literal|2
operator|)
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|target
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkOverrideMethods
specifier|private
specifier|static
name|void
name|checkOverrideMethods
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|NoSuchMethodException
throws|,
name|SecurityException
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|superClazz
init|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
decl_stmt|;
for|for
control|(
name|Method
name|m
range|:
name|superClazz
operator|.
name|getMethods
argument_list|()
control|)
block|{
specifier|final
name|int
name|mods
init|=
name|m
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|mods
argument_list|)
operator|||
name|Modifier
operator|.
name|isAbstract
argument_list|(
name|mods
argument_list|)
operator|||
name|Modifier
operator|.
name|isFinal
argument_list|(
name|mods
argument_list|)
operator|||
name|m
operator|.
name|isSynthetic
argument_list|()
operator|||
name|m
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"attributes"
argument_list|)
operator|||
name|m
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"getStats"
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// The point of these checks is to ensure that methods that have a default
comment|// impl through other methods are not overridden. This makes the number of
comment|// methods to override to have a working impl minimal and prevents from some
comment|// traps: for example, think about having getCoreCacheKey delegate to the
comment|// filtered impl by default
specifier|final
name|Method
name|subM
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|m
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|subM
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|clazz
operator|&&
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|Object
operator|.
name|class
operator|&&
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|subM
operator|.
name|getDeclaringClass
argument_list|()
condition|)
block|{
name|fail
argument_list|(
name|clazz
operator|+
literal|" overrides "
operator|+
name|m
operator|+
literal|" although it has a default impl"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testOverrideMethods
specifier|public
name|void
name|testOverrideMethods
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOverrideMethods
argument_list|(
name|FilterLeafReader
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkOverrideMethods
argument_list|(
name|FilterLeafReader
operator|.
name|FilterFields
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkOverrideMethods
argument_list|(
name|FilterLeafReader
operator|.
name|FilterTerms
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkOverrideMethods
argument_list|(
name|FilterLeafReader
operator|.
name|FilterTermsEnum
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkOverrideMethods
argument_list|(
name|FilterLeafReader
operator|.
name|FilterPostingsEnum
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnwrap
specifier|public
name|void
name|testUnwrap
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|dr
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|LeafReader
name|r
init|=
name|dr
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|FilterLeafReader
name|r2
init|=
operator|new
name|FilterLeafReader
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|r
argument_list|,
name|r2
operator|.
name|getDelegate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
argument_list|,
name|FilterLeafReader
operator|.
name|unwrap
argument_list|(
name|r2
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dr
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

