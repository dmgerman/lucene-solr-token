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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|AttributeImpl
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
comment|/**  * a binary tokenstream that lets you index a BytesRef  */
end_comment

begin_class
DECL|class|BinaryTokenStream
specifier|public
specifier|final
class|class
name|BinaryTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|bytesAtt
specifier|private
specifier|final
name|ByteTermAttribute
name|bytesAtt
init|=
name|addAttribute
argument_list|(
name|ByteTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|available
specifier|private
name|boolean
name|available
init|=
literal|true
decl_stmt|;
DECL|method|BinaryTokenStream
specifier|public
name|BinaryTokenStream
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
name|bytesAtt
operator|.
name|setBytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|available
condition|)
block|{
name|available
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|available
operator|=
literal|true
expr_stmt|;
block|}
DECL|interface|ByteTermAttribute
specifier|public
interface|interface
name|ByteTermAttribute
extends|extends
name|TermToBytesRefAttribute
block|{
DECL|method|setBytesRef
specifier|public
name|void
name|setBytesRef
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
function_decl|;
block|}
DECL|class|ByteTermAttributeImpl
specifier|public
specifier|static
class|class
name|ByteTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|ByteTermAttribute
implements|,
name|TermToBytesRefAttribute
block|{
DECL|field|bytes
specifier|private
name|BytesRef
name|bytes
decl_stmt|;
DECL|method|fillBytesRef
specifier|public
name|int
name|fillBytesRef
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
DECL|method|setBytesRef
specifier|public
name|void
name|setBytesRef
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|ByteTermAttributeImpl
name|other
init|=
operator|(
name|ByteTermAttributeImpl
operator|)
name|target
decl_stmt|;
name|other
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

