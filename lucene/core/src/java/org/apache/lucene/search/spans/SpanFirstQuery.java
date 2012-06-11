begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ToStringUtils
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

begin_comment
comment|/** Matches spans near the beginning of a field.  *<p/>   * This class is a simple extension of {@link SpanPositionRangeQuery} in that it assumes the  * start to be zero and only checks the end boundary.  *  *  *  */
end_comment

begin_class
DECL|class|SpanFirstQuery
specifier|public
class|class
name|SpanFirstQuery
extends|extends
name|SpanPositionRangeQuery
block|{
comment|/** Construct a SpanFirstQuery matching spans in<code>match</code> whose end    * position is less than or equal to<code>end</code>. */
DECL|method|SpanFirstQuery
specifier|public
name|SpanFirstQuery
parameter_list|(
name|SpanQuery
name|match
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|super
argument_list|(
name|match
argument_list|,
literal|0
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptPosition
specifier|protected
name|AcceptStatus
name|acceptPosition
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|spans
operator|.
name|start
argument_list|()
operator|!=
name|spans
operator|.
name|end
argument_list|()
operator|:
literal|"start equals end: "
operator|+
name|spans
operator|.
name|start
argument_list|()
assert|;
if|if
condition|(
name|spans
operator|.
name|start
argument_list|()
operator|>=
name|end
condition|)
return|return
name|AcceptStatus
operator|.
name|NO_AND_ADVANCE
return|;
elseif|else
if|if
condition|(
name|spans
operator|.
name|end
argument_list|()
operator|<=
name|end
condition|)
return|return
name|AcceptStatus
operator|.
name|YES
return|;
else|else
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanFirst("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|match
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|end
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SpanFirstQuery
name|clone
parameter_list|()
block|{
name|SpanFirstQuery
name|spanFirstQuery
init|=
operator|new
name|SpanFirstQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|match
operator|.
name|clone
argument_list|()
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|spanFirstQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spanFirstQuery
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SpanFirstQuery
operator|)
condition|)
return|return
literal|false
return|;
name|SpanFirstQuery
name|other
init|=
operator|(
name|SpanFirstQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|end
operator|==
name|other
operator|.
name|end
operator|&&
name|this
operator|.
name|match
operator|.
name|equals
argument_list|(
name|other
operator|.
name|match
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|match
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|8
operator|)
operator||
operator|(
name|h
operator|>>>
literal|25
operator|)
expr_stmt|;
comment|// reversible
name|h
operator|^=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|end
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

