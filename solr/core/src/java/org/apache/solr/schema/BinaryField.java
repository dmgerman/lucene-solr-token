begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|nio
operator|.
name|ByteBuffer
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
name|index
operator|.
name|StorableField
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
name|SortField
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|TextResponseWriter
import|;
end_import

begin_class
DECL|class|BinaryField
specifier|public
class|class
name|BinaryField
extends|extends
name|FieldType
block|{
DECL|method|toBase64String
specifier|private
name|String
name|toBase64String
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
block|{
return|return
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|buf
operator|.
name|array
argument_list|()
argument_list|,
name|buf
operator|.
name|position
argument_list|()
argument_list|,
name|buf
operator|.
name|limit
argument_list|()
operator|-
name|buf
operator|.
name|position
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|StorableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|toBase64String
argument_list|(
name|toObject
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot sort on a Binary field"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|StorableField
name|f
parameter_list|)
block|{
return|return
name|toBase64String
argument_list|(
name|toObject
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|ByteBuffer
name|toObject
parameter_list|(
name|StorableField
name|f
parameter_list|)
block|{
name|BytesRef
name|bytes
init|=
name|f
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createField
specifier|public
name|StorableField
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
operator|!
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Ignoring unstored binary field: "
operator|+
name|field
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|buf
init|=
literal|null
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|,
name|len
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|buf
operator|=
operator|(
name|byte
index|[]
operator|)
name|val
expr_stmt|;
name|len
operator|=
name|buf
operator|.
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|ByteBuffer
operator|&&
operator|(
operator|(
name|ByteBuffer
operator|)
name|val
operator|)
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|ByteBuffer
name|byteBuf
init|=
operator|(
name|ByteBuffer
operator|)
name|val
decl_stmt|;
name|buf
operator|=
name|byteBuf
operator|.
name|array
argument_list|()
expr_stmt|;
name|offset
operator|=
name|byteBuf
operator|.
name|position
argument_list|()
expr_stmt|;
name|len
operator|=
name|byteBuf
operator|.
name|limit
argument_list|()
operator|-
name|byteBuf
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|strVal
init|=
name|val
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//the string has to be a base64 encoded string
name|buf
operator|=
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|strVal
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|len
operator|=
name|buf
operator|.
name|length
expr_stmt|;
block|}
name|Field
name|f
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StoredField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
block|}
end_class

end_unit

