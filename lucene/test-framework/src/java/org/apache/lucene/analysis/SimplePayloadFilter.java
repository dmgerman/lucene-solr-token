begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|CharTermAttribute
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
name|PayloadAttribute
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
comment|/** Simple payload filter that sets the payload as pos: XXXX */
end_comment

begin_class
DECL|class|SimplePayloadFilter
specifier|public
specifier|final
class|class
name|SimplePayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|payloadAttr
specifier|final
name|PayloadAttribute
name|payloadAttr
decl_stmt|;
DECL|field|termAttr
specifier|final
name|CharTermAttribute
name|termAttr
decl_stmt|;
DECL|method|SimplePayloadFilter
specifier|public
name|SimplePayloadFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|payloadAttr
operator|=
name|input
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|termAttr
operator|=
name|input
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
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
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|payloadAttr
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|(
literal|"pos: "
operator|+
name|pos
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|pos
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

