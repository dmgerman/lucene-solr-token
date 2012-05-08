begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

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
name|Token
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
name|util
operator|.
name|CharArrayMap
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
name|util
operator|.
name|InitializationException
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
name|Version
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/** Mapping rules for use with {@link SlowSynonymFilter}  * @deprecated (3.4) use {@link SynonymFilterFactory} instead. only for precise index backwards compatibility. this factory will be removed in Lucene 5.0  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SlowSynonymMap
class|class
name|SlowSynonymMap
block|{
comment|/** @lucene.internal */
DECL|field|submap
specifier|public
name|CharArrayMap
argument_list|<
name|SlowSynonymMap
argument_list|>
name|submap
decl_stmt|;
comment|// recursive: Map<String, SynonymMap>
comment|/** @lucene.internal */
DECL|field|synonyms
specifier|public
name|Token
index|[]
name|synonyms
decl_stmt|;
DECL|field|flags
name|int
name|flags
decl_stmt|;
DECL|field|INCLUDE_ORIG
specifier|static
specifier|final
name|int
name|INCLUDE_ORIG
init|=
literal|0x01
decl_stmt|;
DECL|field|IGNORE_CASE
specifier|static
specifier|final
name|int
name|IGNORE_CASE
init|=
literal|0x02
decl_stmt|;
DECL|method|SlowSynonymMap
specifier|public
name|SlowSynonymMap
parameter_list|()
block|{}
DECL|method|SlowSynonymMap
specifier|public
name|SlowSynonymMap
parameter_list|(
name|boolean
name|ignoreCase
parameter_list|)
block|{
if|if
condition|(
name|ignoreCase
condition|)
name|flags
operator||=
name|IGNORE_CASE
expr_stmt|;
block|}
DECL|method|includeOrig
specifier|public
name|boolean
name|includeOrig
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
name|INCLUDE_ORIG
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|ignoreCase
specifier|public
name|boolean
name|ignoreCase
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
name|IGNORE_CASE
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**    * @param singleMatch  List<String>, the sequence of strings to match    * @param replacement  List<Token> the list of tokens to use on a match    * @param includeOrig  sets a flag on this mapping signaling the generation of matched tokens in addition to the replacement tokens    * @param mergeExisting merge the replacement tokens with any other mappings that exist    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|singleMatch
parameter_list|,
name|List
argument_list|<
name|Token
argument_list|>
name|replacement
parameter_list|,
name|boolean
name|includeOrig
parameter_list|,
name|boolean
name|mergeExisting
parameter_list|)
block|{
name|SlowSynonymMap
name|currMap
init|=
name|this
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|singleMatch
control|)
block|{
if|if
condition|(
name|currMap
operator|.
name|submap
operator|==
literal|null
condition|)
block|{
comment|// for now hardcode at 4.0, as its what the old code did.
comment|// would be nice to fix, but shouldn't store a version in each submap!!!
name|currMap
operator|.
name|submap
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|SlowSynonymMap
argument_list|>
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
literal|1
argument_list|,
name|ignoreCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SlowSynonymMap
name|map
init|=
name|currMap
operator|.
name|submap
operator|.
name|get
argument_list|(
name|str
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|SlowSynonymMap
argument_list|()
expr_stmt|;
name|map
operator|.
name|flags
operator||=
name|flags
operator|&
name|IGNORE_CASE
expr_stmt|;
name|currMap
operator|.
name|submap
operator|.
name|put
argument_list|(
name|str
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
name|currMap
operator|=
name|map
expr_stmt|;
block|}
if|if
condition|(
name|currMap
operator|.
name|synonyms
operator|!=
literal|null
operator|&&
operator|!
name|mergeExisting
condition|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"SynonymFilter: there is already a mapping for "
operator|+
name|singleMatch
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Token
argument_list|>
name|superset
init|=
name|currMap
operator|.
name|synonyms
operator|==
literal|null
condition|?
name|replacement
else|:
name|mergeTokens
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|currMap
operator|.
name|synonyms
argument_list|)
argument_list|,
name|replacement
argument_list|)
decl_stmt|;
name|currMap
operator|.
name|synonyms
operator|=
name|superset
operator|.
name|toArray
argument_list|(
operator|new
name|Token
index|[
name|superset
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeOrig
condition|)
name|currMap
operator|.
name|flags
operator||=
name|INCLUDE_ORIG
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<"
argument_list|)
decl_stmt|;
if|if
condition|(
name|synonyms
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|synonyms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|synonyms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|flags
operator|&
name|INCLUDE_ORIG
operator|)
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|",ORIG"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"],"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|submap
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Produces a List<Token> from a List<String> */
DECL|method|makeTokens
specifier|public
specifier|static
name|List
argument_list|<
name|Token
argument_list|>
name|makeTokens
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
name|List
argument_list|<
name|Token
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|(
name|strings
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|strings
control|)
block|{
comment|//Token newTok = new Token(str,0,0,"SYNONYM");
name|Token
name|newTok
init|=
operator|new
name|Token
argument_list|(
name|str
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"SYNONYM"
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|newTok
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Merge two lists of tokens, producing a single list with manipulated positionIncrements so that    * the tokens end up at the same position.    *    * Example:  [a b] merged with [c d] produces [a/b c/d]  ('/' denotes tokens in the same position)    * Example:  [a,5 b,2] merged with [c d,4 e,4] produces [c a,5/d b,2 e,2]  (a,n means a has posInc=n)    *    */
DECL|method|mergeTokens
specifier|public
specifier|static
name|List
argument_list|<
name|Token
argument_list|>
name|mergeTokens
parameter_list|(
name|List
argument_list|<
name|Token
argument_list|>
name|lst1
parameter_list|,
name|List
argument_list|<
name|Token
argument_list|>
name|lst2
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|lst1
operator|==
literal|null
operator|||
name|lst2
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|lst2
operator|!=
literal|null
condition|)
name|result
operator|.
name|addAll
argument_list|(
name|lst2
argument_list|)
expr_stmt|;
if|if
condition|(
name|lst1
operator|!=
literal|null
condition|)
name|result
operator|.
name|addAll
argument_list|(
name|lst1
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|Token
argument_list|>
name|iter1
init|=
name|lst1
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Token
argument_list|>
name|iter2
init|=
name|lst2
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Token
name|tok1
init|=
name|iter1
operator|.
name|hasNext
argument_list|()
condition|?
name|iter1
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|Token
name|tok2
init|=
name|iter2
operator|.
name|hasNext
argument_list|()
condition|?
name|iter2
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|int
name|pos1
init|=
name|tok1
operator|!=
literal|null
condition|?
name|tok1
operator|.
name|getPositionIncrement
argument_list|()
else|:
literal|0
decl_stmt|;
name|int
name|pos2
init|=
name|tok2
operator|!=
literal|null
condition|?
name|tok2
operator|.
name|getPositionIncrement
argument_list|()
else|:
literal|0
decl_stmt|;
while|while
condition|(
name|tok1
operator|!=
literal|null
operator|||
name|tok2
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|tok1
operator|!=
literal|null
operator|&&
operator|(
name|pos1
operator|<=
name|pos2
operator|||
name|tok2
operator|==
literal|null
operator|)
condition|)
block|{
name|Token
name|tok
init|=
operator|new
name|Token
argument_list|(
name|tok1
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tok1
operator|.
name|endOffset
argument_list|()
argument_list|,
name|tok1
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|tok
operator|.
name|copyBuffer
argument_list|(
name|tok1
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|tok1
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|tok
operator|.
name|setPositionIncrement
argument_list|(
name|pos1
operator|-
name|pos
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|tok
argument_list|)
expr_stmt|;
name|pos
operator|=
name|pos1
expr_stmt|;
name|tok1
operator|=
name|iter1
operator|.
name|hasNext
argument_list|()
condition|?
name|iter1
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
name|pos1
operator|+=
name|tok1
operator|!=
literal|null
condition|?
name|tok1
operator|.
name|getPositionIncrement
argument_list|()
else|:
literal|0
expr_stmt|;
block|}
while|while
condition|(
name|tok2
operator|!=
literal|null
operator|&&
operator|(
name|pos2
operator|<=
name|pos1
operator|||
name|tok1
operator|==
literal|null
operator|)
condition|)
block|{
name|Token
name|tok
init|=
operator|new
name|Token
argument_list|(
name|tok2
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tok2
operator|.
name|endOffset
argument_list|()
argument_list|,
name|tok2
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|tok
operator|.
name|copyBuffer
argument_list|(
name|tok2
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|tok2
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|tok
operator|.
name|setPositionIncrement
argument_list|(
name|pos2
operator|-
name|pos
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|tok
argument_list|)
expr_stmt|;
name|pos
operator|=
name|pos2
expr_stmt|;
name|tok2
operator|=
name|iter2
operator|.
name|hasNext
argument_list|()
condition|?
name|iter2
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
name|pos2
operator|+=
name|tok2
operator|!=
literal|null
condition|?
name|tok2
operator|.
name|getPositionIncrement
argument_list|()
else|:
literal|0
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

