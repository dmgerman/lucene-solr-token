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
name|Closeable
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
name|Iterator
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
name|ByteDocValuesField
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
name|DerefBytesDocValuesField
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
name|DoubleDocValuesField
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
name|FloatDocValuesField
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
name|IntDocValuesField
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
name|LongDocValuesField
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
name|PackedLongDocValuesField
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
name|ShortDocValuesField
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
name|SortedBytesDocValuesField
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
name|StraightBytesDocValuesField
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

begin_comment
comment|// javadoc
end_comment

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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
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

begin_comment
comment|/** Silly class that randomizes the indexing experience.  EG  *  it may swap in a different merge policy/scheduler; may  *  commit periodically; may or may not forceMerge in the end,  *  may flush by doc count instead of RAM, etc.   */
end_comment

begin_class
DECL|class|RandomIndexWriter
specifier|public
class|class
name|RandomIndexWriter
implements|implements
name|Closeable
block|{
DECL|field|w
specifier|public
name|IndexWriter
name|w
decl_stmt|;
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
DECL|field|flushAt
name|int
name|flushAt
decl_stmt|;
DECL|field|flushAtFactor
specifier|private
name|double
name|flushAtFactor
init|=
literal|1.0
decl_stmt|;
DECL|field|getReaderCalled
specifier|private
name|boolean
name|getReaderCalled
decl_stmt|;
DECL|field|fixedBytesLength
specifier|private
specifier|final
name|int
name|fixedBytesLength
decl_stmt|;
DECL|field|docValuesFieldPrefix
specifier|private
specifier|final
name|long
name|docValuesFieldPrefix
decl_stmt|;
DECL|field|doDocValues
specifier|private
specifier|volatile
name|boolean
name|doDocValues
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
decl_stmt|;
comment|// sugar
comment|// Randomly calls Thread.yield so we mixup thread scheduling
DECL|class|MockIndexWriter
specifier|private
specifier|static
specifier|final
class|class
name|MockIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
DECL|method|MockIndexWriter
specifier|public
name|MockIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// TODO: this should be solved in a different way; Random should not be shared (!).
name|this
operator|.
name|r
operator|=
operator|new
name|Random
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testPoint
name|boolean
name|testPoint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|2
condition|)
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/** create a RandomIndexWriter with a random config: Uses TEST_VERSION_CURRENT and MockAnalyzer */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with a random config: Uses TEST_VERSION_CURRENT */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with a random config */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Version
name|v
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|v
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with the provided config */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|c
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: this should be solved in a different way; Random should not be shared (!).
name|this
operator|.
name|r
operator|=
operator|new
name|Random
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|MockIndexWriter
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|flushAt
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|codec
operator|=
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getCodec
argument_list|()
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW dir="
operator|+
name|dir
operator|+
literal|" config="
operator|+
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"codec default="
operator|+
name|codec
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* TODO: find some way to make this random...      * This must be fixed across all fixed bytes       * fields in one index. so if you open another writer      * this might change if I use r.nextInt(x)      * maybe we can peek at the existing files here?       */
name|fixedBytesLength
operator|=
literal|17
expr_stmt|;
comment|// NOTE: this means up to 13 * 5 unique fields (we have
comment|// 13 different DV types):
name|docValuesFieldPrefix
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|switchDoDocValues
argument_list|()
expr_stmt|;
comment|// Make sure we sometimes test indices that don't get
comment|// any forced merges:
name|doRandomForceMerge
operator|=
name|r
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
block|}
DECL|field|addDocValuesFields
specifier|private
name|boolean
name|addDocValuesFields
init|=
literal|true
decl_stmt|;
comment|/**    * set to false if you don't want RandomIndexWriter    * adding docvalues fields.    */
DECL|method|setAddDocValuesFields
specifier|public
name|void
name|setAddDocValuesFields
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|addDocValuesFields
operator|=
name|v
expr_stmt|;
name|switchDoDocValues
argument_list|()
expr_stmt|;
block|}
DECL|method|switchDoDocValues
specifier|private
name|void
name|switchDoDocValues
parameter_list|()
block|{
if|if
condition|(
name|addDocValuesFields
operator|==
literal|false
condition|)
block|{
name|doDocValues
operator|=
literal|false
expr_stmt|;
return|return;
block|}
comment|// randomly enable / disable docValues
name|doDocValues
operator|=
name|LuceneTestCase
operator|.
name|rarely
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
if|if
condition|(
name|doDocValues
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: RIW: turning on random DocValues fields"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Adds a Document.    * @see IndexWriter#addDocument(org.apache.lucene.index.IndexDocument)    */
DECL|method|addDocument
specifier|public
parameter_list|<
name|T
extends|extends
name|IndexableField
parameter_list|>
name|void
name|addDocument
parameter_list|(
specifier|final
name|IndexDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|addDocument
argument_list|(
name|doc
argument_list|,
name|w
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addDocument
specifier|public
parameter_list|<
name|T
extends|extends
name|IndexableField
parameter_list|>
name|void
name|addDocument
parameter_list|(
specifier|final
name|IndexDocument
name|doc
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doDocValues
operator|&&
name|doc
operator|instanceof
name|Document
condition|)
block|{
name|randomPerDocFieldValues
argument_list|(
operator|(
name|Document
operator|)
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
comment|// TODO: maybe, we should simply buffer up added docs
comment|// (but we need to clone them), and only when
comment|// getReader, commit, etc. are called, we do an
comment|// addDocuments?  Would be better testing.
name|w
operator|.
name|addDocuments
argument_list|(
operator|new
name|Iterable
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
name|boolean
name|done
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|done
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|IndexDocument
name|next
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
name|done
operator|=
literal|true
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
DECL|method|getFixedRandomBytes
specifier|private
name|BytesRef
name|getFixedRandomBytes
parameter_list|()
block|{
specifier|final
name|String
name|randomUnicodeString
init|=
name|_TestUtil
operator|.
name|randomFixedByteLengthUnicodeString
argument_list|(
name|r
argument_list|,
name|fixedBytesLength
argument_list|)
decl_stmt|;
name|BytesRef
name|fixedRef
init|=
operator|new
name|BytesRef
argument_list|(
name|randomUnicodeString
argument_list|)
decl_stmt|;
if|if
condition|(
name|fixedRef
operator|.
name|length
operator|>
name|fixedBytesLength
condition|)
block|{
name|fixedRef
operator|=
operator|new
name|BytesRef
argument_list|(
name|fixedRef
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|fixedBytesLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fixedRef
operator|.
name|grow
argument_list|(
name|fixedBytesLength
argument_list|)
expr_stmt|;
name|fixedRef
operator|.
name|length
operator|=
name|fixedBytesLength
expr_stmt|;
block|}
return|return
name|fixedRef
return|;
block|}
DECL|method|randomPerDocFieldValues
specifier|private
name|void
name|randomPerDocFieldValues
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|DocValues
operator|.
name|Type
index|[]
name|values
init|=
name|DocValues
operator|.
name|Type
operator|.
name|values
argument_list|()
decl_stmt|;
name|DocValues
operator|.
name|Type
name|type
init|=
name|values
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|values
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|String
name|name
init|=
literal|"random_"
operator|+
name|type
operator|.
name|name
argument_list|()
operator|+
literal|""
operator|+
name|docValuesFieldPrefix
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getField
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|Field
name|f
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
name|f
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
name|name
argument_list|,
name|getFixedRandomBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
name|f
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|r
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|f
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
name|name
argument_list|,
name|getFixedRandomBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|f
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|r
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_SORTED
case|:
name|f
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
name|name
argument_list|,
name|getFixedRandomBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
name|f
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|r
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|f
operator|=
operator|new
name|FloatDocValuesField
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|f
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|nextDouble
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|VAR_INTS
case|:
name|f
operator|=
operator|new
name|PackedLongDocValuesField
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
comment|// TODO: we should test negatives too?
name|f
operator|=
operator|new
name|ShortDocValuesField
argument_list|(
name|name
argument_list|,
operator|(
name|short
operator|)
name|r
operator|.
name|nextInt
argument_list|(
name|Short
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|f
operator|=
operator|new
name|IntDocValuesField
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|f
operator|=
operator|new
name|LongDocValuesField
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
comment|// TODO: we should test negatives too?
name|f
operator|=
operator|new
name|ByteDocValuesField
argument_list|(
name|name
argument_list|,
operator|(
name|byte
operator|)
name|r
operator|.
name|nextInt
argument_list|(
literal|128
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no such type: "
operator|+
name|type
argument_list|)
throw|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
DECL|method|maybeCommit
specifier|private
name|void
name|maybeCommit
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|docCount
operator|++
operator|==
name|flushAt
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.add/updateDocument: now doing a commit at docCount="
operator|+
name|docCount
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|flushAt
operator|+=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
call|(
name|int
call|)
argument_list|(
name|flushAtFactor
operator|*
literal|10
argument_list|)
argument_list|,
call|(
name|int
call|)
argument_list|(
name|flushAtFactor
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|flushAtFactor
operator|<
literal|2e6
condition|)
block|{
comment|// gradually but exponentially increase time b/w flushes
name|flushAtFactor
operator|*=
literal|1.05
expr_stmt|;
block|}
name|switchDoDocValues
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addDocuments
specifier|public
name|void
name|addDocuments
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexDocument
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
DECL|method|updateDocuments
specifier|public
name|void
name|updateDocuments
parameter_list|(
name|Term
name|delTerm
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexDocument
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|updateDocuments
argument_list|(
name|delTerm
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
comment|/**    * Updates a document.    * @see IndexWriter#updateDocument(Term, org.apache.lucene.index.IndexDocument)    */
DECL|method|updateDocument
specifier|public
parameter_list|<
name|T
extends|extends
name|IndexableField
parameter_list|>
name|void
name|updateDocument
parameter_list|(
name|Term
name|t
parameter_list|,
specifier|final
name|IndexDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doDocValues
condition|)
block|{
name|randomPerDocFieldValues
argument_list|(
operator|(
name|Document
operator|)
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
name|w
operator|.
name|updateDocuments
argument_list|(
name|t
argument_list|,
operator|new
name|Iterable
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
name|boolean
name|done
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|done
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|IndexDocument
name|next
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
name|done
operator|=
literal|true
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|w
operator|.
name|updateDocument
argument_list|(
name|t
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
DECL|method|addIndexes
specifier|public
name|void
name|addIndexes
parameter_list|(
name|Directory
modifier|...
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
block|}
DECL|method|addIndexes
specifier|public
name|void
name|addIndexes
parameter_list|(
name|IndexReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|addIndexes
argument_list|(
name|readers
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDocuments
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDocuments
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|switchDoDocValues
argument_list|()
expr_stmt|;
block|}
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|w
operator|.
name|numDocs
argument_list|()
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|w
operator|.
name|maxDoc
argument_list|()
return|;
block|}
DECL|method|deleteAll
specifier|public
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|w
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|getReader
specifier|public
name|DirectoryReader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getReader
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|field|doRandomForceMerge
specifier|private
name|boolean
name|doRandomForceMerge
init|=
literal|true
decl_stmt|;
DECL|field|doRandomForceMergeAssert
specifier|private
name|boolean
name|doRandomForceMergeAssert
init|=
literal|true
decl_stmt|;
DECL|method|forceMergeDeletes
specifier|public
name|void
name|forceMergeDeletes
parameter_list|(
name|boolean
name|doWait
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|forceMergeDeletes
argument_list|(
name|doWait
argument_list|)
expr_stmt|;
block|}
DECL|method|forceMergeDeletes
specifier|public
name|void
name|forceMergeDeletes
parameter_list|()
throws|throws
name|IOException
block|{
name|w
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
block|}
DECL|method|setDoRandomForceMerge
specifier|public
name|void
name|setDoRandomForceMerge
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|doRandomForceMerge
operator|=
name|v
expr_stmt|;
block|}
DECL|method|setDoRandomForceMergeAssert
specifier|public
name|void
name|setDoRandomForceMergeAssert
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|doRandomForceMergeAssert
operator|=
name|v
expr_stmt|;
block|}
DECL|method|doRandomForceMerge
specifier|private
name|void
name|doRandomForceMerge
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doRandomForceMerge
condition|)
block|{
specifier|final
name|int
name|segCount
init|=
name|w
operator|.
name|getSegmentCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
operator|||
name|segCount
operator|==
literal|0
condition|)
block|{
comment|// full forceMerge
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW: doRandomForceMerge(1)"
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
block|}
else|else
block|{
comment|// partial forceMerge
specifier|final
name|int
name|limit
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
name|segCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW: doRandomForceMerge("
operator|+
name|limit
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
name|limit
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|doRandomForceMergeAssert
operator|||
name|w
operator|.
name|getSegmentCount
argument_list|()
operator|<=
name|limit
operator|:
literal|"limit="
operator|+
name|limit
operator|+
literal|" actual="
operator|+
name|w
operator|.
name|getSegmentCount
argument_list|()
assert|;
block|}
block|}
name|switchDoDocValues
argument_list|()
expr_stmt|;
block|}
DECL|method|getReader
specifier|public
name|DirectoryReader
name|getReader
parameter_list|(
name|boolean
name|applyDeletions
parameter_list|)
throws|throws
name|IOException
block|{
name|getReaderCalled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|2
condition|)
block|{
name|doRandomForceMerge
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|applyDeletions
operator|||
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.getReader: use NRT reader"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|1
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
return|return
name|w
operator|.
name|getReader
argument_list|(
name|applyDeletions
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.getReader: open new reader"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|switchDoDocValues
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|w
operator|.
name|getReader
argument_list|(
name|applyDeletions
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Close this writer.    * @see IndexWriter#close()    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if someone isn't using getReader() API, we want to be sure to
comment|// forceMerge since presumably they might open a reader on the dir.
if|if
condition|(
name|getReaderCalled
operator|==
literal|false
operator|&&
name|r
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
operator|==
literal|2
condition|)
block|{
name|doRandomForceMerge
argument_list|()
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Forces a forceMerge.    *<p>    * NOTE: this should be avoided in tests unless absolutely necessary,    * as it will result in less test coverage.    * @see IndexWriter#forceMerge(int)    */
DECL|method|forceMerge
specifier|public
name|void
name|forceMerge
parameter_list|(
name|int
name|maxSegmentCount
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|forceMerge
argument_list|(
name|maxSegmentCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

