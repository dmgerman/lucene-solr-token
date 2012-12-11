begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|postingshighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Constructs a formatted passage.  *<p>  * The default implementation marks the query terms as bold, and places  * ellipses between unconnected passages.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PassageFormatter
specifier|public
class|class
name|PassageFormatter
block|{
comment|/**    * @return formatted highlight    */
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|Passage
name|passages
index|[]
parameter_list|,
name|String
name|content
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Passage
name|passage
range|:
name|passages
control|)
block|{
comment|// don't add ellipsis if its the first one, or if its connected.
if|if
condition|(
name|passage
operator|.
name|startOffset
operator|>
name|pos
operator|&&
name|pos
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"... "
argument_list|)
expr_stmt|;
block|}
name|pos
operator|=
name|passage
operator|.
name|startOffset
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
name|passage
operator|.
name|numMatches
condition|;
name|i
operator|++
control|)
block|{
name|int
name|start
init|=
name|passage
operator|.
name|matchStarts
index|[
name|i
index|]
decl_stmt|;
name|int
name|end
init|=
name|passage
operator|.
name|matchEnds
index|[
name|i
index|]
decl_stmt|;
comment|// its possible to have overlapping terms
if|if
condition|(
name|start
operator|>
name|pos
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|content
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|end
operator|>
name|pos
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"<b>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|content
operator|.
name|substring
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|pos
argument_list|,
name|start
argument_list|)
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</b>"
argument_list|)
expr_stmt|;
name|pos
operator|=
name|end
expr_stmt|;
block|}
block|}
comment|// its possible a "term" from the analyzer could span a sentence boundary.
name|sb
operator|.
name|append
argument_list|(
name|content
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|pos
argument_list|,
name|passage
operator|.
name|endOffset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|pos
operator|=
name|passage
operator|.
name|endOffset
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

