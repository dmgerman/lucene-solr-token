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
name|analysis
operator|.
name|MockTokenizer
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
name|document
operator|.
name|NumericDocValuesField
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
name|search
operator|.
name|CollectionStatistics
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
name|TermStatistics
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
name|Similarity
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
name|TestUtil
import|;
end_import

begin_comment
comment|/**  * Abstract class to do basic tests for a norms format.  * NOTE: This test focuses on the norms impl, nothing else.  * The [stretch] goal is for this test to be  * so thorough in testing a new NormsFormat that if this  * test passes, then all Lucene/Solr tests should also pass.  Ie,  * if there is some bug in a given NormsFormat that this  * test fails to catch then this test needs to be improved! */
end_comment

begin_class
DECL|class|BaseNormsFormatTestCase
specifier|public
specifier|abstract
class|class
name|BaseNormsFormatTestCase
extends|extends
name|BaseIndexFileFormatTestCase
block|{
DECL|method|testByteRange
specifier|public
name|void
name|testByteRange
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testShortRange
specifier|public
name|void
name|testShortRange
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Short
operator|.
name|MIN_VALUE
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLongRange
specifier|public
name|void
name|testLongRange
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFullLongRange
specifier|public
name|void
name|testFullLongRange
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
name|int
name|thingToDo
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|thingToDo
condition|)
block|{
case|case
literal|0
case|:
return|return
name|Long
operator|.
name|MIN_VALUE
return|;
case|case
literal|1
case|:
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
default|default:
return|return
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFewValues
specifier|public
name|void
name|testFewValues
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|20
else|:
literal|3
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFewLargeValues
specifier|public
name|void
name|testFewLargeValues
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|1000000L
else|:
operator|-
literal|5000
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAllZeros
specifier|public
name|void
name|testAllZeros
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSparse
specifier|public
name|void
name|testSparse
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|?
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
else|:
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOutliers
specifier|public
name|void
name|testOutliers
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|commonValue
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|?
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
else|:
name|commonValue
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOutliers2
specifier|public
name|void
name|testOutliers2
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|commonValue
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|long
name|uncommonValue
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|?
name|uncommonValue
else|:
name|commonValue
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNCommon
specifier|public
name|void
name|testNCommon
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|int
name|N
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|15
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|commonValues
init|=
operator|new
name|long
index|[
name|N
index|]
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
name|N
condition|;
operator|++
name|j
control|)
block|{
name|commonValues
index|[
name|j
index|]
operator|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numOtherValues
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|256
operator|-
name|N
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|otherValues
init|=
operator|new
name|long
index|[
name|numOtherValues
index|]
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
name|numOtherValues
condition|;
operator|++
name|j
control|)
block|{
name|otherValues
index|[
name|j
index|]
operator|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|?
name|otherValues
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|numOtherValues
operator|-
literal|1
argument_list|)
index|]
else|:
name|commonValues
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|N
operator|-
literal|1
argument_list|)
index|]
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * a more thorough n-common that tests all low bpv    */
annotation|@
name|Nightly
DECL|method|testNCommonBig
specifier|public
name|void
name|testNCommonBig
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
comment|// 16 is 4 bpv, the max before we jump to 8bpv
for|for
control|(
name|int
name|n
init|=
literal|2
init|;
name|n
operator|<
literal|16
condition|;
operator|++
name|n
control|)
block|{
specifier|final
name|int
name|N
init|=
name|n
decl_stmt|;
specifier|final
name|long
index|[]
name|commonValues
init|=
operator|new
name|long
index|[
name|N
index|]
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
name|N
condition|;
operator|++
name|j
control|)
block|{
name|commonValues
index|[
name|j
index|]
operator|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numOtherValues
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|256
operator|-
name|N
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|otherValues
init|=
operator|new
name|long
index|[
name|numOtherValues
index|]
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
name|numOtherValues
condition|;
operator|++
name|j
control|)
block|{
name|otherValues
index|[
name|j
index|]
operator|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|r
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
name|doTestNormsVersusDocValues
argument_list|(
operator|new
name|LongProducer
argument_list|()
block|{
annotation|@
name|Override
name|long
name|next
parameter_list|()
block|{
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|?
name|otherValues
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|numOtherValues
operator|-
literal|1
argument_list|)
index|]
else|:
name|commonValues
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|N
operator|-
literal|1
argument_list|)
index|]
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doTestNormsVersusDocValues
specifier|private
name|void
name|doTestNormsVersusDocValues
parameter_list|(
name|LongProducer
name|longs
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|long
name|norms
index|[]
init|=
operator|new
name|long
index|[
name|numDocs
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|norms
index|[
name|i
index|]
operator|=
name|longs
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setSimilarity
argument_list|(
operator|new
name|CannedNormSimilarity
argument_list|(
name|norms
argument_list|)
argument_list|)
expr_stmt|;
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
name|conf
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|idField
init|=
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|Field
name|indexedField
init|=
operator|new
name|TextField
argument_list|(
literal|"indexed"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|Field
name|dvField
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|indexedField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvField
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
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|value
init|=
name|norms
index|[
name|i
index|]
decl_stmt|;
name|dvField
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|indexedField
operator|.
name|setStringValue
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|value
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
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|31
argument_list|)
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|// delete some docs
name|int
name|numDeletions
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
operator|/
literal|20
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
name|numDeletions
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// compare
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|ir
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|NumericDocValues
name|expected
init|=
name|r
operator|.
name|getNumericDocValues
argument_list|(
literal|"dv"
argument_list|)
decl_stmt|;
name|NumericDocValues
name|actual
init|=
name|r
operator|.
name|getNormValues
argument_list|(
literal|"indexed"
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
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|actual
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// compare again
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|ir
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|NumericDocValues
name|expected
init|=
name|r
operator|.
name|getNumericDocValues
argument_list|(
literal|"dv"
argument_list|)
decl_stmt|;
name|NumericDocValues
name|actual
init|=
name|r
operator|.
name|getNormValues
argument_list|(
literal|"indexed"
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
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|actual
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
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
DECL|class|LongProducer
specifier|static
specifier|abstract
class|class
name|LongProducer
block|{
DECL|method|next
specifier|abstract
name|long
name|next
parameter_list|()
function_decl|;
block|}
DECL|class|CannedNormSimilarity
specifier|static
class|class
name|CannedNormSimilarity
extends|extends
name|Similarity
block|{
DECL|field|norms
specifier|final
name|long
name|norms
index|[]
decl_stmt|;
DECL|field|index
name|int
name|index
init|=
literal|0
decl_stmt|;
DECL|method|CannedNormSimilarity
name|CannedNormSimilarity
parameter_list|(
name|long
name|norms
index|[]
parameter_list|)
block|{
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|long
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|norms
index|[
name|index
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|computeWeight
specifier|public
name|SimWeight
name|computeWeight
parameter_list|(
name|float
name|boost
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|simScorer
specifier|public
name|SimScorer
name|simScorer
parameter_list|(
name|SimWeight
name|weight
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|addRandomFields
specifier|protected
name|void
name|addRandomFields
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
comment|// TODO: improve
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"foobar"
argument_list|,
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testMergeStability
specifier|public
name|void
name|testMergeStability
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: can we improve this base test to just have subclasses declare the extensions to check,
comment|// rather than a blacklist to exclude? we need to index stuff to get norms, but we dont care about testing
comment|// the PFs actually doing that...
name|assumeTrue
argument_list|(
literal|"The MockRandom PF randomizes content on the fly, so we can't check it"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// TODO: test thread safety (e.g. across different fields) explicitly here
comment|/*    * LUCENE-6006: Tests undead norms.    *                                 .....                *                             C C  /                *                            /<   /                 *             ___ __________/_#__=o                 *            /(- /(\_\________   \                  *            \ ) \ )_      \o     \                 *            /|\ /|\       |'     |                 *                          |     _|                 *                          /o   __\                 *                         / '     |                 *                        / /      |                 *                       /_/\______|                 *                      (   _(<                  *                       \    \    \                 *                        \    \    |                *                         \____\____\               *                         ____\_\__\_\              *                       /`   /`     o\              *                       |___ |_______|    *    */
DECL|method|testUndeadNorms
specifier|public
name|void
name|testUndeadNorms
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
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numDocs
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
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|1
condition|)
block|{
name|toDelete
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"some content"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
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
for|for
control|(
name|Integer
name|id
range|:
name|toDelete
control|)
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasDeletions
argument_list|()
argument_list|)
expr_stmt|;
comment|// Confusingly, norms should exist, and should all be 0, even though we deleted all docs that had the field "content".  They should not
comment|// be undead:
name|NumericDocValues
name|norms
init|=
name|MultiDocValues
operator|.
name|getNormValues
argument_list|(
name|r
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|norms
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
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|norms
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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

