begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  *<code>DocSlice</code> implements DocList as an array of docids and optional scores.  *  * @author yonik  * @version $Id$  * @since solr 0.9  */
end_comment

begin_class
DECL|class|DocSlice
specifier|public
class|class
name|DocSlice
extends|extends
name|DocSetBase
implements|implements
name|DocList
block|{
DECL|field|offset
specifier|final
name|int
name|offset
decl_stmt|;
comment|// starting position of the docs (zero based)
DECL|field|len
specifier|final
name|int
name|len
decl_stmt|;
comment|// number of positions used in arrays
DECL|field|docs
specifier|final
name|int
index|[]
name|docs
decl_stmt|;
comment|// a slice of documents (docs 0-100 of the query)
DECL|field|scores
specifier|final
name|float
index|[]
name|scores
decl_stmt|;
comment|// optional score list
DECL|field|matches
specifier|final
name|int
name|matches
decl_stmt|;
DECL|field|maxScore
specifier|final
name|float
name|maxScore
decl_stmt|;
comment|/**    * Primary constructor for a DocSlice instance.    *    * @param offset  starting offset for this range of docs    * @param len     length of results    * @param docs    array of docids starting at position 0    * @param scores  array of scores that corresponds to docs, may be null    * @param matches total number of matches for the query    */
DECL|method|DocSlice
specifier|public
name|DocSlice
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
index|[]
name|docs
parameter_list|,
name|float
index|[]
name|scores
parameter_list|,
name|int
name|matches
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|scores
operator|=
name|scores
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|matches
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
DECL|method|subset
specifier|public
name|DocList
name|subset
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|offset
operator|==
name|offset
operator|&&
name|this
operator|.
name|len
operator|==
name|len
condition|)
return|return
name|this
return|;
comment|// if we didn't store enough (and there was more to store)
comment|// then we can't take a subset.
name|int
name|requestedEnd
init|=
name|offset
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|requestedEnd
operator|>
name|docs
operator|.
name|length
operator|&&
name|this
operator|.
name|matches
operator|>
name|docs
operator|.
name|length
condition|)
return|return
literal|null
return|;
name|int
name|realEndDoc
init|=
name|Math
operator|.
name|min
argument_list|(
name|requestedEnd
argument_list|,
name|docs
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|realLen
init|=
name|Math
operator|.
name|max
argument_list|(
name|realEndDoc
operator|-
name|offset
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|offset
operator|==
name|offset
operator|&&
name|this
operator|.
name|len
operator|==
name|realLen
condition|)
return|return
name|this
return|;
return|return
operator|new
name|DocSlice
argument_list|(
name|offset
argument_list|,
name|realLen
argument_list|,
name|docs
argument_list|,
name|scores
argument_list|,
name|matches
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
DECL|method|hasScores
specifier|public
name|boolean
name|hasScores
parameter_list|()
block|{
return|return
name|scores
operator|!=
literal|null
return|;
block|}
DECL|method|maxScore
specifier|public
name|float
name|maxScore
parameter_list|()
block|{
return|return
name|maxScore
return|;
block|}
DECL|method|offset
specifier|public
name|int
name|offset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|len
return|;
block|}
DECL|method|matches
specifier|public
name|int
name|matches
parameter_list|()
block|{
return|return
name|matches
return|;
block|}
DECL|method|memSize
specifier|public
name|long
name|memSize
parameter_list|()
block|{
return|return
operator|(
name|docs
operator|.
name|length
operator|<<
literal|2
operator|)
operator|+
operator|(
name|scores
operator|==
literal|null
condition|?
literal|0
else|:
operator|(
name|scores
operator|.
name|length
operator|<<
literal|2
operator|)
operator|)
operator|+
literal|24
return|;
block|}
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
range|:
name|docs
control|)
block|{
if|if
condition|(
name|i
operator|==
name|doc
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|// Hmmm, maybe I could have reused the scorer interface here...
comment|// except that it carries Similarity baggage...
DECL|method|iterator
specifier|public
name|DocIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIterator
argument_list|()
block|{
name|int
name|pos
init|=
name|offset
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|len
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|end
return|;
block|}
specifier|public
name|Integer
name|next
parameter_list|()
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{       }
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|docs
index|[
name|pos
operator|++
index|]
return|;
block|}
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
name|scores
index|[
name|pos
operator|-
literal|1
index|]
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

