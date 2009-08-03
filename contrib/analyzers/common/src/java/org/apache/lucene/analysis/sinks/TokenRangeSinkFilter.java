begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
package|;
end_package

begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
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
name|AttributeSource
import|;
end_import

begin_comment
comment|/**  * Counts the tokens as they go by and saves to the internal list those between the range of lower and upper, exclusive of upper  *  **/
end_comment

begin_class
DECL|class|TokenRangeSinkFilter
specifier|public
class|class
name|TokenRangeSinkFilter
extends|extends
name|SinkFilter
block|{
DECL|field|lower
specifier|private
name|int
name|lower
decl_stmt|;
DECL|field|upper
specifier|private
name|int
name|upper
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|method|TokenRangeSinkFilter
specifier|public
name|TokenRangeSinkFilter
parameter_list|(
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|)
block|{
name|this
operator|.
name|lower
operator|=
name|lower
expr_stmt|;
name|this
operator|.
name|upper
operator|=
name|upper
expr_stmt|;
block|}
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|count
operator|>=
name|lower
operator|&&
name|count
operator|<
name|upper
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

