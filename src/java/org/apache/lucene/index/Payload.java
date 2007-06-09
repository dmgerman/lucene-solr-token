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
name|Token
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

begin_comment
comment|/**   *  A Payload is metadata that can be stored together with each occurrence    *  of a term. This metadata is stored inline in the posting list of the   *  specific term.     *<p>   *  To store payloads in the index a {@link TokenStream} has to be used that   *  produces {@link Token}s containing payload data.   *<p>   *  Use {@link TermPositions#getPayloadLength()} and {@link TermPositions#getPayload(byte[], int)}   *  to retrieve the payloads from the index.<br>   *<br>   *     *<p><font color="#FF0000">   * WARNING: The status of the<b>Payloads</b> feature is experimental.    * The APIs introduced here might change in the future and will not be    * supported anymore in such a case.</font>   */
end_comment

begin_comment
comment|// TODO: Remove warning after API has been finalized
end_comment

begin_class
DECL|class|Payload
specifier|public
class|class
name|Payload
implements|implements
name|Serializable
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
specifier|protected
name|Payload
parameter_list|()
block|{
comment|// no-arg constructor since this class implements Serializable
block|}
comment|/**      * Creates a new payload with the the given array as data.      *       * @param data the data of this payload      */
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
comment|/**      * Creates a new payload with the the given array as data.       *       * @param data the data of this payload      * @param offset the offset in the data byte array      * @param length the length of the data      */
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
comment|/**      * Returns the length of the payload data.       */
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
comment|/**      * Returns the byte at the given index.      */
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
comment|/**      * Allocates a new byte array, copies the payload data into it and returns it.       */
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
comment|/**      * Copies the payload data to a byte array.      *       * @param target the target byte array      * @param targetOffset the offset in the target byte array      */
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
block|}
end_class

end_unit

