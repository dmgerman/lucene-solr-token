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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|IndexReader
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

begin_comment
comment|/** Matches spans containing a term. */
end_comment

begin_class
DECL|class|SpanTermQuery
specifier|public
class|class
name|SpanTermQuery
extends|extends
name|SpanQuery
block|{
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
comment|/** Construct a SpanTermQuery matching the named term's spans. */
DECL|method|SpanTermQuery
specifier|public
name|SpanTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
comment|/** Return the term whose spans are matched. */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|term
operator|.
name|field
argument_list|()
return|;
block|}
comment|/** Returns a collection of all terms matched by this query.    * @deprecated use extractTerms instead    * @see #extractTerms(Set)    */
DECL|method|getTerms
specifier|public
name|Collection
name|getTerms
parameter_list|()
block|{
name|Collection
name|terms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|terms
return|;
block|}
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
name|terms
parameter_list|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|toString
argument_list|()
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
comment|/** Returns true iff<code>o</code> is equal to this. */
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
operator|!
operator|(
name|o
operator|instanceof
name|SpanTermQuery
operator|)
condition|)
return|return
literal|false
return|;
name|SpanTermQuery
name|other
init|=
operator|(
name|SpanTermQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|)
operator|&&
name|this
operator|.
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object.*/
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|term
operator|.
name|hashCode
argument_list|()
operator|^
literal|0xD23FE494
return|;
block|}
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Spans
argument_list|()
block|{
specifier|private
name|TermPositions
name|positions
init|=
name|reader
operator|.
name|termPositions
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|freq
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|private
name|int
name|position
decl_stmt|;
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
comment|// are we already at the correct position?
if|if
condition|(
name|doc
operator|>=
name|target
condition|)
block|{
return|return
literal|true
return|;
block|}
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
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|position
return|;
block|}
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
operator|+
name|SpanTermQuery
operator|.
name|this
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
block|}
return|;
block|}
block|}
end_class

end_unit

