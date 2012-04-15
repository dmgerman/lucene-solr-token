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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|*
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
name|*
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
name|*
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
name|*
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
name|tokenattributes
operator|.
name|*
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
name|codecs
operator|.
name|Codec
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
name|*
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|Collections
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
name|Random
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
comment|// NOTE: this test will fail w/ PreFlexRW codec!  (Because
end_comment

begin_comment
comment|// this test uses full binary term space, but PreFlex cannot
end_comment

begin_comment
comment|// handle this since it requires the terms are UTF8 bytes).
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Also, SimpleText codec will consume very large amounts of
end_comment

begin_comment
comment|// disk (but, should run successfully).  Best to run w/
end_comment

begin_comment
comment|// -Dtests.codec=Standard, and w/ plenty of RAM, eg:
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//   ant test -Dtest.slow=true -Dtests.heapsize=8g
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//   java -server -Xmx8g -d64 -cp .:lib/junit-4.10.jar:./build/classes/test:./build/classes/test-framework:./build/classes/java -Dlucene.version=4.0-dev -Dtests.directory=MMapDirectory -DtempDir=build -ea org.junit.runner.JUnitCore org.apache.lucene.index.Test2BTerms
end_comment

begin_comment
comment|//
end_comment

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|UseNoMemoryExpensiveCodec
DECL|class|Test2BTerms
specifier|public
class|class
name|Test2BTerms
extends|extends
name|LuceneTestCase
block|{
DECL|field|TOKEN_LEN
specifier|private
specifier|final
specifier|static
name|int
name|TOKEN_LEN
init|=
literal|10
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
specifier|static
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
name|TOKEN_LEN
argument_list|)
decl_stmt|;
DECL|class|MyTokenStream
specifier|private
specifier|final
specifier|static
class|class
name|MyTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|tokensPerDoc
specifier|private
specifier|final
name|int
name|tokensPerDoc
decl_stmt|;
DECL|field|tokenCount
specifier|private
name|int
name|tokenCount
decl_stmt|;
DECL|field|savedTerms
specifier|public
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|savedTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nextSave
specifier|private
name|int
name|nextSave
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|MyTokenStream
specifier|public
name|MyTokenStream
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|tokensPerDoc
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|MyAttributeFactory
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokensPerDoc
operator|=
name|tokensPerDoc
expr_stmt|;
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|TOKEN_LEN
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|nextSave
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|500000
argument_list|,
literal|1000000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|tokenCount
operator|>=
name|tokensPerDoc
condition|)
block|{
return|return
literal|false
return|;
block|}
name|random
operator|.
name|nextBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|tokenCount
operator|++
expr_stmt|;
if|if
condition|(
operator|--
name|nextSave
operator|==
literal|0
condition|)
block|{
name|savedTerms
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: save term="
operator|+
name|bytes
argument_list|)
expr_stmt|;
name|nextSave
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|500000
argument_list|,
literal|1000000
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|tokenCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|class|MyTermAttributeImpl
specifier|private
specifier|final
specifier|static
class|class
name|MyTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|TermToBytesRefAttribute
block|{
DECL|method|fillBytesRef
specifier|public
name|int
name|fillBytesRef
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{       }
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
name|other
operator|==
name|this
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
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{       }
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MyTermAttributeImpl
name|clone
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|class|MyAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|MyAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeFactory
name|delegate
decl_stmt|;
DECL|method|MyAttributeFactory
specifier|public
name|MyAttributeFactory
parameter_list|(
name|AttributeFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
if|if
condition|(
name|attClass
operator|==
name|TermToBytesRefAttribute
operator|.
name|class
condition|)
return|return
operator|new
name|MyTermAttributeImpl
argument_list|()
return|;
if|if
condition|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no"
argument_list|)
throw|;
return|return
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Slow
DECL|method|test2BTerms
specifier|public
name|void
name|test2BTerms
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|"Lucene3x"
operator|.
name|equals
argument_list|(
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this test cannot run with PreFlex codec"
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting Test2B"
argument_list|)
expr_stmt|;
specifier|final
name|long
name|TERM_COUNT
init|=
operator|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|+
literal|100000000
decl_stmt|;
specifier|final
name|int
name|TERMS_PER_DOC
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100000
argument_list|,
literal|1000000
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|savedTerms
init|=
literal|null
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"2BTerms"
argument_list|)
argument_list|)
decl_stmt|;
comment|//MockDirectoryWrapper dir = newFSDirectory(new File("/p/lucene/indices/2bindex"));
name|dir
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't double-checkindex
if|if
condition|(
literal|true
condition|)
block|{
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|256.0
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|10
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|MergePolicy
name|mp
init|=
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|mp
operator|instanceof
name|LogByteSizeMergePolicy
condition|)
block|{
comment|// 1 petabyte:
operator|(
operator|(
name|LogByteSizeMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setMaxMergeMB
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|MyTokenStream
name|ts
init|=
operator|new
name|MyTokenStream
argument_list|(
name|random
argument_list|()
argument_list|,
name|TERMS_PER_DOC
argument_list|)
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
name|ts
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|//w.setInfoStream(System.out);
specifier|final
name|int
name|numDocs
init|=
call|(
name|int
call|)
argument_list|(
name|TERM_COUNT
operator|/
name|TERMS_PER_DOC
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TERMS_PER_DOC="
operator|+
name|TERMS_PER_DOC
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" of "
operator|+
name|numDocs
operator|+
literal|" "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
name|savedTerms
operator|=
name|ts
operator|.
name|savedTerms
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: full merge"
argument_list|)
expr_stmt|;
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: close writer"
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: open reader"
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|savedTerms
operator|==
literal|null
condition|)
block|{
name|savedTerms
operator|=
name|findTerms
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numSavedTerms
init|=
name|savedTerms
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|bigOrdTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|savedTerms
operator|.
name|subList
argument_list|(
name|numSavedTerms
operator|-
literal|10
argument_list|,
name|numSavedTerms
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: test big ord terms..."
argument_list|)
expr_stmt|;
name|testSavedTerms
argument_list|(
name|r
argument_list|,
name|bigOrdTerms
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: test all saved terms..."
argument_list|)
expr_stmt|;
name|testSavedTerms
argument_list|(
name|r
argument_list|,
name|savedTerms
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now CheckIndex..."
argument_list|)
expr_stmt|;
name|CheckIndex
operator|.
name|Status
name|status
init|=
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|long
name|tc
init|=
name|status
operator|.
name|segmentInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|termIndexStatus
operator|.
name|termCount
decl_stmt|;
name|assertTrue
argument_list|(
literal|"count "
operator|+
name|tc
operator|+
literal|" is not> "
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|tc
operator|>
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: done!"
argument_list|)
expr_stmt|;
block|}
DECL|method|findTerms
specifier|private
name|List
argument_list|<
name|BytesRef
argument_list|>
name|findTerms
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: findTerms"
argument_list|)
expr_stmt|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|savedTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|nextSave
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|500000
argument_list|,
literal|1000000
argument_list|)
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|termsEnum
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
operator|--
name|nextSave
operator|==
literal|0
condition|)
block|{
name|savedTerms
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: add "
operator|+
name|term
argument_list|)
expr_stmt|;
name|nextSave
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|500000
argument_list|,
literal|1000000
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|savedTerms
return|;
block|}
DECL|method|testSavedTerms
specifier|private
name|void
name|testSavedTerms
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|List
argument_list|<
name|BytesRef
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: run "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" terms on reader="
operator|+
name|r
argument_list|)
expr_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
operator|*
name|terms
operator|.
name|size
argument_list|()
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|terms
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: search "
operator|+
name|term
argument_list|)
expr_stmt|;
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|term
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
decl_stmt|;
if|if
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  FAILED: count="
operator|+
name|count
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|long
name|t1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  took "
operator|+
operator|(
name|t1
operator|-
name|t0
operator|)
operator|+
literal|" millis"
argument_list|)
expr_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|result
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
if|if
condition|(
name|result
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  FAILED: got END"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  FAILED: wrong term: got "
operator|+
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|failed
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

