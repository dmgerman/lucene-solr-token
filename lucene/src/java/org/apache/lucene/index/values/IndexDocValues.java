begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|Comparator
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
name|IndexDocValuesField
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
name|Fields
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
name|FieldsEnum
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
comment|/**  * {@link IndexDocValues} provides a dense per-document typed storage for fast  * value access based on the lucene internal document id. {@link IndexDocValues}  * exposes two distinct APIs:  *<ul>  *<li>via {@link #getSource()} providing RAM resident random access</li>  *<li>via {@link #getDirectSource()} providing on disk random access</li>  *</ul> {@link IndexDocValues} are exposed via  * {@link IndexReader#perDocValues()} on a per-segment basis. For best  * performance {@link IndexDocValues} should be consumed per-segment just like  * IndexReader.  *<p>  * {@link IndexDocValues} are fully integrated into the {@link Codec} API.  * Custom implementations can be exposed on a per field basis via  * {@link CodecProvider}.  *   * @see ValueType for limitations and default implementation documentation  * @see IndexDocValuesField for adding values to the index  * @see Codec#docsConsumer(org.apache.lucene.index.PerDocWriteState) for  *      customization  * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndexDocValues
specifier|public
specifier|abstract
class|class
name|IndexDocValues
implements|implements
name|Closeable
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|IndexDocValues
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|IndexDocValues
index|[
literal|0
index|]
decl_stmt|;
DECL|field|cache
specifier|private
specifier|volatile
name|SourceCache
name|cache
init|=
operator|new
name|SourceCache
operator|.
name|DirectSourceCache
argument_list|()
decl_stmt|;
DECL|field|cacheLock
specifier|private
specifier|final
name|Object
name|cacheLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/**    * Loads a new {@link Source} instance for this {@link IndexDocValues} field    * instance. Source instances returned from this method are not cached. It is    * the callers responsibility to maintain the instance and release its    * resources once the source is not needed anymore.    *<p>    * For managed {@link Source} instances see {@link #getSource()}.    *     * @see #getSource()    * @see #setCache(SourceCache)    */
DECL|method|load
specifier|public
specifier|abstract
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a {@link Source} instance through the current {@link SourceCache}.    * Iff no {@link Source} has been loaded into the cache so far the source will    * be loaded through {@link #load()} and passed to the {@link SourceCache}.    * The caller of this method should not close the obtained {@link Source}    * instance unless it is not needed for the rest of its life time.    *<p>    * {@link Source} instances obtained from this method are closed / released    * from the cache once this {@link IndexDocValues} instance is closed by the    * {@link IndexReader}, {@link Fields} or {@link FieldsEnum} the    * {@link IndexDocValues} was created from.    */
DECL|method|getSource
specifier|public
name|Source
name|getSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns a disk resident {@link Source} instance. Direct Sources are not    * cached in the {@link SourceCache} and should not be shared between threads.    */
DECL|method|getDirectSource
specifier|public
specifier|abstract
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the {@link ValueType} of this {@link IndexDocValues} instance    */
DECL|method|type
specifier|public
specifier|abstract
name|ValueType
name|type
parameter_list|()
function_decl|;
comment|/**    * Closes this {@link IndexDocValues} instance. This method should only be called    * by the creator of this {@link IndexDocValues} instance. API users should not    * close {@link IndexDocValues} instances.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|cache
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the size per value in bytes or<code>-1</code> iff size per value    * is variable.    *     * @return the size per value in bytes or<code>-1</code> iff size per value    * is variable.    */
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Sets the {@link SourceCache} used by this {@link IndexDocValues} instance. This    * method should be called before {@link #load()} is called. All {@link Source} instances in the currently used cache will be closed    * before the new cache is installed.    *<p>    * Note: All instances previously obtained from {@link #load()} will be lost.    *     * @throws IllegalArgumentException    *           if the given cache is<code>null</code>    *     */
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|SourceCache
name|cache
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cache must not be null"
argument_list|)
throw|;
synchronized|synchronized
init|(
name|cacheLock
init|)
block|{
name|SourceCache
name|toClose
init|=
name|this
operator|.
name|cache
decl_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|toClose
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Source of per document values like long, double or {@link BytesRef}    * depending on the {@link IndexDocValues} fields {@link ValueType}. Source    * implementations provide random access semantics similar to array lookups    *<p>    * @see IndexDocValues#getSource()    * @see IndexDocValues#getDirectSource()    */
DECL|class|Source
specifier|public
specifier|static
specifier|abstract
class|class
name|Source
block|{
DECL|field|type
specifier|protected
specifier|final
name|ValueType
name|type
decl_stmt|;
DECL|method|Source
specifier|protected
name|Source
parameter_list|(
name|ValueType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Returns a<tt>long</tt> for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>long</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>long</tt> values.      */
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ints are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns a<tt>double</tt> for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>double</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>double</tt> values.      */
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"floats are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns a {@link BytesRef} for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>byte[]</tt> values.      * @throws IOException       *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>byte[]</tt> values.      */
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"bytes are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns the {@link ValueType} of this source.      *       * @return the {@link ValueType} of this source.      */
DECL|method|type
specifier|public
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Returns<code>true</code> iff this {@link Source} exposes an array via      * {@link #getArray()} otherwise<code>false</code>.      *       * @return<code>true</code> iff this {@link Source} exposes an array via      *         {@link #getArray()} otherwise<code>false</code>.      */
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns the internal array representation iff this {@link Source} uses an      * array as its inner representation, otherwise<code>null</code>.      */
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * If this {@link Source} is sorted this method will return an instance of      * {@link SortedSource} otherwise<code>null</code>      */
DECL|method|asSortedSource
specifier|public
name|SortedSource
name|asSortedSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * A sorted variant of {@link Source} for<tt>byte[]</tt> values per document.    *<p>    */
DECL|class|SortedSource
specifier|public
specifier|static
specifier|abstract
class|class
name|SortedSource
extends|extends
name|Source
block|{
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
DECL|method|SortedSource
specifier|protected
name|SortedSource
parameter_list|(
name|ValueType
name|type
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|ord
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|bytesRef
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|getByOrd
argument_list|(
name|ord
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
return|return
name|bytesRef
return|;
block|}
comment|/**      * Returns ord for specified docID. Ord is dense, ie, starts at 0, then increments by 1      * for the next (as defined by {@link Comparator} value.      */
DECL|method|ord
specifier|public
specifier|abstract
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Returns value for specified ord. */
DECL|method|getByOrd
specifier|public
specifier|abstract
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
function_decl|;
comment|/**      * Performs a lookup by value.      *       * @param value      *          the value to look up      * @param spare      *          a spare {@link BytesRef} instance used to compare internal      *          values to the given value. Must not be<code>null</code>      * @return the given values ordinal if found or otherwise      *<code>(-(ord)-1)</code>, defined as the ordinal of the first      *         element that is greater than the given value. This guarantees      *         that the return value will always be&gt;= 0 if the given value      *         is found.      */
DECL|method|getByValue
specifier|public
name|int
name|getByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|BytesRef
name|spare
parameter_list|)
block|{
return|return
name|binarySearch
argument_list|(
name|value
argument_list|,
name|spare
argument_list|,
literal|0
argument_list|,
name|getValueCount
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|binarySearch
specifier|protected
name|int
name|binarySearch
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|,
name|int
name|low
parameter_list|,
name|int
name|high
parameter_list|)
block|{
name|int
name|mid
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
expr_stmt|;
name|getByOrd
argument_list|(
name|mid
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
specifier|final
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|mid
return|;
block|}
block|}
assert|assert
name|comparator
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
operator|!=
literal|0
assert|;
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|asSortedSource
specifier|public
name|SortedSource
name|asSortedSource
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/**      * Returns the number of unique values in this sorted source      */
DECL|method|getValueCount
specifier|public
specifier|abstract
name|int
name|getValueCount
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

