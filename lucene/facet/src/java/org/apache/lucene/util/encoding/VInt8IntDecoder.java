begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An {@link IntDecoder} which can decode values encoded by  * {@link VInt8IntEncoder}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|VInt8IntDecoder
specifier|public
class|class
name|VInt8IntDecoder
extends|extends
name|IntDecoder
block|{
DECL|field|legalEOS
specifier|private
name|boolean
name|legalEOS
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|decode
specifier|public
name|long
name|decode
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|value
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|first
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
operator|<
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|legalEOS
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected End-Of-Stream"
argument_list|)
throw|;
block|}
return|return
name|EOS
return|;
block|}
name|value
operator||=
name|first
operator|&
literal|0x7F
expr_stmt|;
if|if
condition|(
operator|(
name|first
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
name|legalEOS
operator|=
literal|true
expr_stmt|;
return|return
name|value
return|;
block|}
name|legalEOS
operator|=
literal|false
expr_stmt|;
name|value
operator|<<=
literal|7
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"VInt8"
return|;
block|}
block|}
end_class

end_unit

