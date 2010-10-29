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
name|util
operator|.
name|AttributeSource
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

begin_class
DECL|class|DocValues
specifier|public
specifier|abstract
class|class
name|DocValues
implements|implements
name|Closeable
block|{
DECL|field|lock
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|cachedReference
specifier|private
name|Source
name|cachedReference
decl_stmt|;
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|DocValues
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|DocValues
index|[
literal|0
index|]
decl_stmt|;
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getEnum
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|getEnum
specifier|public
specifier|abstract
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|load
specifier|public
specifier|abstract
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getCached
specifier|public
name|Source
name|getCached
parameter_list|(
name|boolean
name|load
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
comment|// TODO make sorted source cachable too
if|if
condition|(
name|load
operator|&&
name|cachedReference
operator|==
literal|null
condition|)
name|cachedReference
operator|=
name|load
argument_list|()
expr_stmt|;
return|return
name|cachedReference
return|;
block|}
block|}
DECL|method|releaseCached
specifier|public
name|Source
name|releaseCached
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
specifier|final
name|Source
name|retVal
init|=
name|cachedReference
decl_stmt|;
name|cachedReference
operator|=
literal|null
expr_stmt|;
return|return
name|retVal
return|;
block|}
block|}
DECL|method|loadSorted
specifier|public
name|SortedSource
name|loadSorted
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
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
DECL|method|type
specifier|public
specifier|abstract
name|Values
name|type
parameter_list|()
function_decl|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|releaseCached
argument_list|()
expr_stmt|;
block|}
comment|/**    * Source of integer (returned as java long), per document. The underlying    * implementation may use different numbers of bits per value; long is only    * used since it can handle all precisions.    */
DECL|class|Source
specifier|public
specifier|static
specifier|abstract
class|class
name|Source
block|{
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
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
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
comment|/**      * Returns number of unique values. Some impls may throw      * UnsupportedOperationException.      */
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getEnum
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|// nocommit - enable obtaining enum from source since this is already in
comment|// memory
DECL|method|getEnum
specifier|public
comment|/* abstract */
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
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
DECL|method|ramBytesUsed
specifier|public
specifier|abstract
name|long
name|ramBytesUsed
parameter_list|()
function_decl|;
block|}
DECL|class|SortedSource
specifier|public
specifier|static
specifier|abstract
class|class
name|SortedSource
extends|extends
name|Source
block|{
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|getByOrd
argument_list|(
name|ord
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns ord for specified docID. If this docID had not been added to the      * Writer, the ord is 0. Ord is dense, ie, starts at 0, then increments by 1      * for the next (as defined by {@link Comparator} value.      */
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
parameter_list|)
function_decl|;
DECL|class|LookupResult
specifier|public
specifier|static
class|class
name|LookupResult
block|{
DECL|field|found
specifier|public
name|boolean
name|found
decl_stmt|;
DECL|field|ord
specifier|public
name|int
name|ord
decl_stmt|;
block|}
comment|/**      * Finds the largest ord whose value is<= the requested value. If      * {@link LookupResult#found} is true, then ord is an exact match. The      * returned {@link LookupResult} may be reused across calls.      */
DECL|method|getByValue
specifier|public
specifier|abstract
name|LookupResult
name|getByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

