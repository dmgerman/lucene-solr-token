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
comment|/**  * Wraps a {@link LegacyBinaryDocValues} into a {@link BinaryDocValues}.  *  * @deprecated Implement {@link BinaryDocValues} directly.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|LegacyBinaryDocValuesWrapper
specifier|public
specifier|final
class|class
name|LegacyBinaryDocValuesWrapper
extends|extends
name|BinaryDocValues
block|{
DECL|field|docsWithField
specifier|private
specifier|final
name|Bits
name|docsWithField
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|LegacyBinaryDocValues
name|values
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|docID
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|LegacyBinaryDocValuesWrapper
specifier|public
name|LegacyBinaryDocValuesWrapper
parameter_list|(
name|Bits
name|docsWithField
parameter_list|,
name|LegacyBinaryDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|docsWithField
operator|=
name|docsWithField
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|docsWithField
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|docID
operator|++
expr_stmt|;
while|while
condition|(
name|docID
operator|<
name|maxDoc
condition|)
block|{
if|if
condition|(
name|docsWithField
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
return|return
name|docID
return|;
block|}
name|docID
operator|++
expr_stmt|;
block|}
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|<
name|docID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot advance backwards: docID="
operator|+
name|docID
operator|+
literal|" target="
operator|+
name|target
argument_list|)
throw|;
block|}
if|if
condition|(
name|target
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|this
operator|.
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|docID
operator|=
name|target
operator|-
literal|1
expr_stmt|;
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|docID
operator|=
name|target
expr_stmt|;
return|return
name|docsWithField
operator|.
name|get
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

