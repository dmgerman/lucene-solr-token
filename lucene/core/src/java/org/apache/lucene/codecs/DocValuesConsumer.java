begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|DocValues
operator|.
name|Source
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
name|DocValues
operator|.
name|Type
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
name|DocValues
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
name|IndexableField
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
name|MergeState
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
name|Bits
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

begin_comment
comment|/**  * Abstract API that consumes {@link IndexableField}s.  * {@link DocValuesConsumer} are always associated with a specific field and  * segments. Concrete implementations of this API write the given  * {@link IndexableField} into a implementation specific format depending on  * the fields meta-data.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocValuesConsumer
specifier|public
specifier|abstract
class|class
name|DocValuesConsumer
block|{
DECL|field|spare
specifier|protected
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|getType
specifier|protected
specifier|abstract
name|Type
name|getType
parameter_list|()
function_decl|;
comment|/**    * Adds the given {@link IndexableField} instance to this    * {@link DocValuesConsumer}    *     * @param docID    *          the document ID to add the value for. The docID must always    *          increase or be<tt>0</tt> if it is the first call to this method.    * @param value    *          the value to add    * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|IndexableField
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called when the consumer of this API is done adding values.    *     * @param docCount    *          the total number of documents in this {@link DocValuesConsumer}.    *          Must be greater than or equal the last given docID to    *          {@link #add(int, IndexableField)}.    * @throws IOException    */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Merges the given {@link org.apache.lucene.index.MergeState} into    * this {@link DocValuesConsumer}.    *     * @param mergeState    *          the state to merge    * @param docValues docValues array containing one instance per reader (    *          {@link org.apache.lucene.index.MergeState#readers}) or<code>null</code> if the reader has    *          no {@link DocValues} instance.    * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|DocValues
index|[]
name|docValues
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|mergeState
operator|!=
literal|null
assert|;
name|boolean
name|hasMerged
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|readerIDX
init|=
literal|0
init|;
name|readerIDX
operator|<
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIDX
operator|++
control|)
block|{
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerIDX
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValues
index|[
name|readerIDX
index|]
operator|!=
literal|null
condition|)
block|{
name|hasMerged
operator|=
literal|true
expr_stmt|;
name|merge
argument_list|(
name|docValues
index|[
name|readerIDX
index|]
argument_list|,
name|mergeState
operator|.
name|docBase
index|[
name|readerIDX
index|]
argument_list|,
name|reader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|reader
operator|.
name|liveDocs
argument_list|)
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
name|reader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// only finish if no exception is thrown!
if|if
condition|(
name|hasMerged
condition|)
block|{
name|finish
argument_list|(
name|mergeState
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Merges the given {@link DocValues} into this {@link DocValuesConsumer}.    *     * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|DocValues
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Bits
name|liveDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This enables bulk copies in subclasses per MergeState, subclasses can
comment|// simply override this and decide if they want to merge
comment|// segments using this generic implementation or if a bulk merge is possible
comment|// / feasible.
specifier|final
name|Source
name|source
init|=
name|reader
operator|.
name|getDirectSource
argument_list|()
decl_stmt|;
assert|assert
name|source
operator|!=
literal|null
assert|;
name|int
name|docID
init|=
name|docBase
decl_stmt|;
specifier|final
name|Type
name|type
init|=
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|Field
name|scratchField
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|VAR_INTS
case|:
name|scratchField
operator|=
operator|new
name|PackedLongDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|scratchField
operator|=
operator|new
name|ByteDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|scratchField
operator|=
operator|new
name|ShortDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|scratchField
operator|=
operator|new
name|IntDocValuesField
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|scratchField
operator|=
operator|new
name|LongDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|scratchField
operator|=
operator|new
name|FloatDocValuesField
argument_list|(
literal|""
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|scratchField
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
literal|""
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|scratchField
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|scratchField
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_DEREF
case|:
name|scratchField
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
name|scratchField
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_SORTED
case|:
name|scratchField
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
name|scratchField
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unknown Type: "
operator|+
name|type
argument_list|)
throw|;
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
name|docCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|mergeDoc
argument_list|(
name|scratchField
argument_list|,
name|source
argument_list|,
name|docID
operator|++
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Merges a document with the given<code>docID</code>. The methods    * implementation obtains the value for the<i>sourceDoc</i> id from the    * current {@link Source}.    *<p>    * This method is used during merging to provide implementation agnostic    * default merge implementation.    *</p>    *<p>    * All documents IDs between the given ID and the previously given ID or    *<tt>0</tt> if the method is call the first time are filled with default    * values depending on the implementation. The given document    * ID must always be greater than the previous ID or<tt>0</tt> if called the    * first time.    */
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|Field
name|scratchField
parameter_list|,
name|Source
name|source
parameter_list|,
name|int
name|docID
parameter_list|,
name|int
name|sourceDoc
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|scratchField
operator|.
name|setBytesValue
argument_list|(
name|source
operator|.
name|getBytes
argument_list|(
name|sourceDoc
argument_list|,
name|spare
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|scratchField
operator|.
name|setByteValue
argument_list|(
operator|(
name|byte
operator|)
name|source
operator|.
name|getInt
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|scratchField
operator|.
name|setShortValue
argument_list|(
operator|(
name|short
operator|)
name|source
operator|.
name|getInt
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|scratchField
operator|.
name|setIntValue
argument_list|(
operator|(
name|int
operator|)
name|source
operator|.
name|getInt
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|scratchField
operator|.
name|setLongValue
argument_list|(
name|source
operator|.
name|getInt
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|VAR_INTS
case|:
name|scratchField
operator|.
name|setLongValue
argument_list|(
name|source
operator|.
name|getInt
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|scratchField
operator|.
name|setFloatValue
argument_list|(
operator|(
name|float
operator|)
name|source
operator|.
name|getFloat
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|scratchField
operator|.
name|setDoubleValue
argument_list|(
name|source
operator|.
name|getFloat
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|add
argument_list|(
name|docID
argument_list|,
name|scratchField
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

