begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|ByteArrayOutputStream
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
operator|.
name|IntEncoder
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Accumulates category IDs for a single document, for writing in byte array  * form, for example, to a Lucene Payload.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryListPayloadStream
specifier|public
class|class
name|CategoryListPayloadStream
block|{
DECL|field|baos
specifier|private
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|50
argument_list|)
decl_stmt|;
DECL|field|encoder
specifier|private
name|IntEncoder
name|encoder
decl_stmt|;
comment|/** Creates a Payload stream using the specified encoder. */
DECL|method|CategoryListPayloadStream
specifier|public
name|CategoryListPayloadStream
parameter_list|(
name|IntEncoder
name|encoder
parameter_list|)
block|{
name|this
operator|.
name|encoder
operator|=
name|encoder
expr_stmt|;
name|this
operator|.
name|encoder
operator|.
name|reInit
argument_list|(
name|baos
argument_list|)
expr_stmt|;
block|}
comment|/** Appends an integer to the stream. */
DECL|method|appendIntToStream
specifier|public
name|void
name|appendIntToStream
parameter_list|(
name|int
name|intValue
parameter_list|)
throws|throws
name|IOException
block|{
name|encoder
operator|.
name|encode
argument_list|(
name|intValue
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the streamed bytes so far accumulated, as an array of bytes. */
DECL|method|convertStreamToByteArray
specifier|public
name|byte
index|[]
name|convertStreamToByteArray
parameter_list|()
block|{
try|try
block|{
name|encoder
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// This cannot happen, because of BAOS (no I/O).
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
block|}
comment|/** Resets this stream to begin building a new payload. */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|encoder
operator|.
name|close
argument_list|()
expr_stmt|;
name|baos
operator|.
name|reset
argument_list|()
expr_stmt|;
name|encoder
operator|.
name|reInit
argument_list|(
name|baos
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

