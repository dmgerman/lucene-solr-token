begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Copyright 2009 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FieldCache
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
name|MultiReader
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
name|store
operator|.
name|RAMDirectory
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
name|FieldCacheSanityChecker
operator|.
name|Insanity
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
name|FieldCacheSanityChecker
operator|.
name|InsanityType
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

begin_class
DECL|class|TestFieldCacheSanityChecker
specifier|public
class|class
name|TestFieldCacheSanityChecker
extends|extends
name|LuceneTestCase
block|{
DECL|field|readerA
specifier|protected
name|IndexReader
name|readerA
decl_stmt|;
DECL|field|readerB
specifier|protected
name|IndexReader
name|readerB
decl_stmt|;
DECL|field|readerX
specifier|protected
name|IndexReader
name|readerX
decl_stmt|;
DECL|field|NUM_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS
init|=
literal|1000
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|RAMDirectory
name|dirA
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|RAMDirectory
name|dirB
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|wA
init|=
operator|new
name|IndexWriter
argument_list|(
name|dirA
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|wB
init|=
operator|new
name|IndexWriter
argument_list|(
name|dirB
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|theLong
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|double
name|theDouble
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
name|byte
name|theByte
init|=
name|Byte
operator|.
name|MAX_VALUE
decl_stmt|;
name|short
name|theShort
init|=
name|Short
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|theInt
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|float
name|theFloat
init|=
name|Float
operator|.
name|MAX_VALUE
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
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
name|Field
argument_list|(
literal|"theLong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theLong
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"theDouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theDouble
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"theByte"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theByte
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"theShort"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theShort
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"theInt"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theInt
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"theFloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|theFloat
operator|--
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|i
operator|%
literal|3
condition|)
block|{
name|wA
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|wB
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|wA
operator|.
name|close
argument_list|()
expr_stmt|;
name|wB
operator|.
name|close
argument_list|()
expr_stmt|;
name|readerA
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|readerB
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirB
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|readerX
operator|=
operator|new
name|MultiReader
argument_list|(
operator|new
name|IndexReader
index|[]
block|{
name|readerA
block|,
name|readerB
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|readerA
operator|.
name|close
argument_list|()
expr_stmt|;
name|readerB
operator|.
name|close
argument_list|()
expr_stmt|;
name|readerX
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
DECL|method|testSanity
specifier|public
name|void
name|testSanity
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getDoubles
argument_list|(
name|readerA
argument_list|,
literal|"theDouble"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getDoubles
argument_list|(
name|readerA
argument_list|,
literal|"theDouble"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_DOUBLE_PARSER
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getDoubles
argument_list|(
name|readerB
argument_list|,
literal|"theDouble"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_DOUBLE_PARSER
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getInts
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getInts
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_INT_PARSER
argument_list|)
expr_stmt|;
comment|// // //
name|Insanity
index|[]
name|insanity
init|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|cache
operator|.
name|getCacheEntries
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|insanity
operator|.
name|length
condition|)
name|dumpArray
argument_list|(
name|getTestLabel
argument_list|()
operator|+
literal|" INSANITY"
argument_list|,
name|insanity
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shouldn't be any cache insanity"
argument_list|,
literal|0
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
DECL|method|testInsanity1
specifier|public
name|void
name|testInsanity1
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getInts
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_INT_PARSER
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getBytes
argument_list|(
name|readerX
argument_list|,
literal|"theByte"
argument_list|)
expr_stmt|;
comment|// // //
name|Insanity
index|[]
name|insanity
init|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|cache
operator|.
name|getCacheEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cache errors"
argument_list|,
literal|1
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong type of cache error"
argument_list|,
name|InsanityType
operator|.
name|VALUEMISMATCH
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of entries in cache error"
argument_list|,
literal|2
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getCacheEntries
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// we expect bad things, don't let tearDown complain about them
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
DECL|method|testInsanity2
specifier|public
name|void
name|testInsanity2
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerA
argument_list|,
literal|"theString"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerB
argument_list|,
literal|"theString"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerX
argument_list|,
literal|"theString"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getBytes
argument_list|(
name|readerX
argument_list|,
literal|"theByte"
argument_list|)
expr_stmt|;
comment|// // //
name|Insanity
index|[]
name|insanity
init|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|cache
operator|.
name|getCacheEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cache errors"
argument_list|,
literal|1
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong type of cache error"
argument_list|,
name|InsanityType
operator|.
name|SUBREADER
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of entries in cache error"
argument_list|,
literal|3
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getCacheEntries
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// we expect bad things, don't let tearDown complain about them
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
DECL|method|testInsanity3
specifier|public
name|void
name|testInsanity3
parameter_list|()
throws|throws
name|IOException
block|{
comment|// :TODO: subreader tree walking is really hairy ... add more crazy tests.
block|}
block|}
end_class

end_unit

