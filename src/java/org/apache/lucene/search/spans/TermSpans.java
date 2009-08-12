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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Term
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
name|TermPositions
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Expert:  * Public for extension only  */
end_comment

begin_class
DECL|class|TermSpans
specifier|public
class|class
name|TermSpans
extends|extends
name|Spans
block|{
DECL|field|positions
specifier|protected
name|TermPositions
name|positions
decl_stmt|;
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
DECL|field|doc
specifier|protected
name|int
name|doc
decl_stmt|;
DECL|field|freq
specifier|protected
name|int
name|freq
decl_stmt|;
DECL|field|count
specifier|protected
name|int
name|count
decl_stmt|;
DECL|field|position
specifier|protected
name|int
name|position
decl_stmt|;
DECL|method|TermSpans
specifier|public
name|TermSpans
parameter_list|(
name|TermPositions
name|positions
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|==
name|freq
condition|)
block|{
if|if
condition|(
operator|!
name|positions
operator|.
name|next
argument_list|()
condition|)
block|{
name|doc
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|positions
operator|.
name|doc
argument_list|()
expr_stmt|;
name|freq
operator|=
name|positions
operator|.
name|freq
argument_list|()
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
name|position
operator|=
name|positions
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|positions
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|doc
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|positions
operator|.
name|doc
argument_list|()
expr_stmt|;
name|freq
operator|=
name|positions
operator|.
name|freq
argument_list|()
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|position
operator|=
name|positions
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|position
return|;
block|}
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|position
operator|+
literal|1
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
DECL|method|getPayload
specifier|public
name|Collection
comment|/*<byte[]>*/
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|positions
operator|.
name|getPayloadLength
argument_list|()
index|]
decl_stmt|;
name|bytes
operator|=
name|positions
operator|.
name|getPayload
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
name|positions
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
operator|+
name|term
operator|.
name|toString
argument_list|()
operator|+
literal|")@"
operator|+
operator|(
name|doc
operator|==
operator|-
literal|1
condition|?
literal|"START"
else|:
operator|(
name|doc
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|)
condition|?
literal|"END"
else|:
name|doc
operator|+
literal|"-"
operator|+
name|position
operator|)
return|;
block|}
DECL|method|getPositions
specifier|public
name|TermPositions
name|getPositions
parameter_list|()
block|{
return|return
name|positions
return|;
block|}
block|}
end_class

end_unit

