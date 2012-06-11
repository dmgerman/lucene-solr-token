begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_comment
comment|/**  * A {@link BoundaryScanner} implementation that uses {@link BreakIterator} to find  * boundaries in the text.  * @see BreakIterator  */
end_comment

begin_class
DECL|class|BreakIteratorBoundaryScanner
specifier|public
class|class
name|BreakIteratorBoundaryScanner
implements|implements
name|BoundaryScanner
block|{
DECL|field|bi
specifier|final
name|BreakIterator
name|bi
decl_stmt|;
DECL|method|BreakIteratorBoundaryScanner
specifier|public
name|BreakIteratorBoundaryScanner
parameter_list|(
name|BreakIterator
name|bi
parameter_list|)
block|{
name|this
operator|.
name|bi
operator|=
name|bi
expr_stmt|;
block|}
DECL|method|findStartOffset
specifier|public
name|int
name|findStartOffset
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
name|start
parameter_list|)
block|{
comment|// avoid illegal start offset
if|if
condition|(
name|start
operator|>
name|buffer
operator|.
name|length
argument_list|()
operator|||
name|start
operator|<
literal|1
condition|)
return|return
name|start
return|;
name|bi
operator|.
name|setText
argument_list|(
name|buffer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|bi
operator|.
name|last
argument_list|()
expr_stmt|;
return|return
name|bi
operator|.
name|previous
argument_list|()
return|;
block|}
DECL|method|findEndOffset
specifier|public
name|int
name|findEndOffset
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
name|start
parameter_list|)
block|{
comment|// avoid illegal start offset
if|if
condition|(
name|start
operator|>
name|buffer
operator|.
name|length
argument_list|()
operator|||
name|start
operator|<
literal|0
condition|)
return|return
name|start
return|;
name|bi
operator|.
name|setText
argument_list|(
name|buffer
operator|.
name|substring
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bi
operator|.
name|next
argument_list|()
operator|+
name|start
return|;
block|}
block|}
end_class

end_unit

