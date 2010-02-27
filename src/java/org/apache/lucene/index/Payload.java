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
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|TokenStream
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
name|ArrayUtil
import|;
end_import

begin_comment
comment|/**  *  A Payload is metadata that can be stored together with each occurrence   *  of a term. This metadata is stored inline in the posting list of the  *  specific term.    *<p>  *  To store payloads in the index a {@link TokenStream} has to be used that  *  produces payload data.  *<p>  *  Use {@link TermPositions#getPayloadLength()} and {@link TermPositions#getPayload(byte[], int)}  *  to retrieve the payloads from the index.<br>  *  */
end_comment

begin_class
DECL|class|Payload
specifier|public
class|class
name|Payload
implements|implements
name|Serializable
implements|,
name|Cloneable
block|{
comment|/** the byte array containing the payload data */
DECL|field|data
specifier|protected
name|byte
index|[]
name|data
decl_stmt|;
comment|/** the offset within the byte array */
DECL|field|offset
specifier|protected
name|int
name|offset
decl_stmt|;
comment|/** the length of the payload data */
DECL|field|length
specifier|protected
name|int
name|length
decl_stmt|;
comment|/** Creates an empty payload and does not allocate a byte array. */
DECL|method|Payload
specifier|public
name|Payload
parameter_list|()
block|{
comment|// nothing to do
block|}
comment|/**    * Creates a new payload with the the given array as data.    * A reference to the passed-in array is held, i. e. no     * copy is made.    *     * @param data the data of this payload    */
DECL|method|Payload
specifier|public
name|Payload
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new payload with the the given array as data.     * A reference to the passed-in array is held, i. e. no     * copy is made.    *     * @param data the data of this payload    * @param offset the offset in the data byte array    * @param length the length of the data    */
DECL|method|Payload
specifier|public
name|Payload
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|offset
argument_list|<
literal|0
operator|||
name|offset
operator|+
name|length
argument_list|>
name|data
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * Sets this payloads data.     * A reference to the passed-in array is held, i. e. no     * copy is made.    */
DECL|method|setData
specifier|public
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|setData
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets this payloads data.     * A reference to the passed-in array is held, i. e. no     * copy is made.    */
DECL|method|setData
specifier|public
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * Returns a reference to the underlying byte array    * that holds this payloads data.    */
DECL|method|getData
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|this
operator|.
name|data
return|;
block|}
comment|/**    * Returns the offset in the underlying byte array     */
DECL|method|getOffset
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|this
operator|.
name|offset
return|;
block|}
comment|/**    * Returns the length of the payload data.     */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|this
operator|.
name|length
return|;
block|}
comment|/**    * Returns the byte at the given index.    */
DECL|method|byteAt
specifier|public
name|byte
name|byteAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|<=
name|index
operator|&&
name|index
operator|<
name|this
operator|.
name|length
condition|)
block|{
return|return
name|this
operator|.
name|data
index|[
name|this
operator|.
name|offset
operator|+
name|index
index|]
return|;
block|}
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
name|index
argument_list|)
throw|;
block|}
comment|/**    * Allocates a new byte array, copies the payload data into it and returns it.     */
DECL|method|toByteArray
specifier|public
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
name|byte
index|[]
name|retArray
init|=
operator|new
name|byte
index|[
name|this
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|data
argument_list|,
name|this
operator|.
name|offset
argument_list|,
name|retArray
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|retArray
return|;
block|}
comment|/**    * Copies the payload data to a byte array.    *     * @param target the target byte array    * @param targetOffset the offset in the target byte array    */
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|byte
index|[]
name|target
parameter_list|,
name|int
name|targetOffset
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|length
operator|>
name|target
operator|.
name|length
operator|+
name|targetOffset
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|()
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|data
argument_list|,
name|this
operator|.
name|offset
argument_list|,
name|target
argument_list|,
name|targetOffset
argument_list|,
name|this
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clones this payload by creating a copy of the underlying    * byte array.    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
comment|// Start with a shallow copy of data
name|Payload
name|clone
init|=
operator|(
name|Payload
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// Only copy the part of data that belongs to this Payload
if|if
condition|(
name|offset
operator|==
literal|0
operator|&&
name|length
operator|==
name|data
operator|.
name|length
condition|)
block|{
comment|// It is the whole thing, so just clone it.
name|clone
operator|.
name|data
operator|=
name|data
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Just get the part
name|clone
operator|.
name|data
operator|=
name|this
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|clone
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// shouldn't happen
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|instanceof
name|Payload
condition|)
block|{
name|Payload
name|other
init|=
operator|(
name|Payload
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|length
operator|==
name|other
operator|.
name|length
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|data
index|[
name|offset
operator|+
name|i
index|]
operator|!=
name|other
operator|.
name|data
index|[
name|other
operator|.
name|offset
operator|+
name|i
index|]
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
else|else
return|return
literal|false
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
name|ArrayUtil
operator|.
name|hashCode
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|)
return|;
block|}
block|}
end_class

end_unit

