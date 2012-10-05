begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|store
operator|.
name|DataOutput
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
comment|/**  * A {@link DataOutput} that can be used to build a byte[].  */
end_comment

begin_class
DECL|class|GrowableByteArrayDataOutput
specifier|final
class|class
name|GrowableByteArrayDataOutput
extends|extends
name|DataOutput
block|{
DECL|field|bytes
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|length
name|int
name|length
decl_stmt|;
DECL|method|GrowableByteArrayDataOutput
name|GrowableByteArrayDataOutput
parameter_list|(
name|int
name|cp
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|cp
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
name|this
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|length
operator|>=
name|bytes
operator|.
name|length
condition|)
block|{
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
name|bytes
index|[
name|length
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|newLength
init|=
name|length
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|newLength
operator|>
name|bytes
operator|.
name|length
condition|)
block|{
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|bytes
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|bytes
argument_list|,
name|length
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|length
operator|=
name|newLength
expr_stmt|;
block|}
block|}
end_class

end_unit

