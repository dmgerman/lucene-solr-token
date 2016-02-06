begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.fa
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fa
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
name|io
operator|.
name|Reader
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
name|CharFilter
import|;
end_import

begin_comment
comment|/**  * CharFilter that replaces instances of Zero-width non-joiner with an  * ordinary space.  */
end_comment

begin_class
DECL|class|PersianCharFilter
specifier|public
class|class
name|PersianCharFilter
extends|extends
name|CharFilter
block|{
DECL|method|PersianCharFilter
specifier|public
name|PersianCharFilter
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
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
name|charsRead
init|=
name|input
operator|.
name|read
argument_list|(
name|cbuf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|charsRead
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|end
init|=
name|off
operator|+
name|charsRead
decl_stmt|;
while|while
condition|(
name|off
operator|<
name|end
condition|)
block|{
if|if
condition|(
name|cbuf
index|[
name|off
index|]
operator|==
literal|'\u200C'
condition|)
name|cbuf
index|[
name|off
index|]
operator|=
literal|' '
expr_stmt|;
name|off
operator|++
expr_stmt|;
block|}
block|}
return|return
name|charsRead
return|;
block|}
comment|// optimized impl: some other charfilters consume with read()
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|ch
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\u200C'
condition|)
block|{
return|return
literal|' '
return|;
block|}
else|else
block|{
return|return
name|ch
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|correct
specifier|protected
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
name|currentOff
return|;
comment|// we don't change the length of the string
block|}
block|}
end_class

end_unit

