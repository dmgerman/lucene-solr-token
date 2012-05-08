begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|TokenStream
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
name|reverse
operator|.
name|ReverseStringFilter
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
name|TokenFilterFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link ReversedWildcardFilter}-s. When this factory is  * added to an analysis chain, it will be used both for filtering the  * tokens during indexing, and to determine the query processing of  * this field during search.  *<p>This class supports the following init arguments:  *<ul>  *<li><code>withOriginal</code> - if true, then produce both original and reversed tokens at  * the same positions. If false, then produce only reversed tokens.</li>  *<li><code>maxPosAsterisk</code> - maximum position (1-based) of the asterisk wildcard  * ('*') that triggers the reversal of query term. Asterisk that occurs at  * positions higher than this value will not cause the reversal of query term.  * Defaults to 2, meaning that asterisks on positions 1 and 2 will cause  * a reversal.</li>  *<li><code>maxPosQuestion</code> - maximum position (1-based) of the question  * mark wildcard ('?') that triggers the reversal of query term. Defaults to 1.  * Set this to 0, and<code>maxPosAsterisk</code> to 1 to reverse only  * pure suffix queries (i.e. ones with a single leading asterisk).</li>  *<li><code>maxFractionAsterisk</code> - additional parameter that  * triggers the reversal if asterisk ('*') position is less than this  * fraction of the query token length. Defaults to 0.0f (disabled).</li>  *<li><code>minTrailing</code> - minimum number of trailing characters in query  * token after the last wildcard character. For good performance this should be  * set to a value larger than 1. Defaults to 2.  *</ul>  * Note 1: This filter always reverses input tokens during indexing.  * Note 2: Query tokens without wildcard characters will never be reversed.  *<pre class="prettyprint">  *&lt;fieldType name="text_rvswc" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer type="index"&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.ReversedWildcardFilterFactory" withOriginal="true"  *             maxPosAsterisk="2" maxPosQuestion="1" minTrailing="2" maxFractionAsterisk="0"/&gt;  *&lt;/analyzer&gt;  *&lt;analyzer type="query"&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment

begin_class
DECL|class|ReversedWildcardFilterFactory
specifier|public
class|class
name|ReversedWildcardFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|markerChar
specifier|private
name|char
name|markerChar
init|=
name|ReverseStringFilter
operator|.
name|START_OF_HEADING_MARKER
decl_stmt|;
DECL|field|withOriginal
specifier|private
name|boolean
name|withOriginal
decl_stmt|;
DECL|field|maxPosAsterisk
specifier|private
name|int
name|maxPosAsterisk
decl_stmt|;
DECL|field|maxPosQuestion
specifier|private
name|int
name|maxPosQuestion
decl_stmt|;
DECL|field|minTrailing
specifier|private
name|int
name|minTrailing
decl_stmt|;
DECL|field|maxFractionAsterisk
specifier|private
name|float
name|maxFractionAsterisk
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|withOriginal
operator|=
name|getBoolean
argument_list|(
literal|"withOriginal"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|maxPosAsterisk
operator|=
name|getInt
argument_list|(
literal|"maxPosAsterisk"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|maxPosQuestion
operator|=
name|getInt
argument_list|(
literal|"maxPosQuestion"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|minTrailing
operator|=
name|getInt
argument_list|(
literal|"minTrailing"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|maxFractionAsterisk
operator|=
name|getFloat
argument_list|(
literal|"maxFractionAsterisk"
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|ReversedWildcardFilter
argument_list|(
name|input
argument_list|,
name|withOriginal
argument_list|,
name|markerChar
argument_list|)
return|;
block|}
comment|/**    * This method encapsulates the logic that determines whether    * a query token should be reversed in order to use the    * reversed terms in the index.    * @param token input token.    * @return true if input token should be reversed, false otherwise.    */
DECL|method|shouldReverse
specifier|public
name|boolean
name|shouldReverse
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|int
name|posQ
init|=
name|token
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
name|int
name|posA
init|=
name|token
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
decl_stmt|;
if|if
condition|(
name|posQ
operator|==
operator|-
literal|1
operator|&&
name|posA
operator|==
operator|-
literal|1
condition|)
block|{
comment|// not a wildcard query
return|return
literal|false
return|;
block|}
name|int
name|pos
decl_stmt|;
name|int
name|lastPos
decl_stmt|;
name|int
name|len
init|=
name|token
operator|.
name|length
argument_list|()
decl_stmt|;
name|lastPos
operator|=
name|token
operator|.
name|lastIndexOf
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
name|pos
operator|=
name|token
operator|.
name|lastIndexOf
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>
name|lastPos
condition|)
name|lastPos
operator|=
name|pos
expr_stmt|;
if|if
condition|(
name|posQ
operator|!=
operator|-
literal|1
condition|)
block|{
name|pos
operator|=
name|posQ
expr_stmt|;
if|if
condition|(
name|posA
operator|!=
operator|-
literal|1
condition|)
block|{
name|pos
operator|=
name|Math
operator|.
name|min
argument_list|(
name|posQ
argument_list|,
name|posA
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|pos
operator|=
name|posA
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|-
name|lastPos
operator|<
name|minTrailing
condition|)
block|{
comment|// too few trailing chars
return|return
literal|false
return|;
block|}
if|if
condition|(
name|posQ
operator|!=
operator|-
literal|1
operator|&&
name|posQ
operator|<
name|maxPosQuestion
condition|)
block|{
comment|// leading '?'
return|return
literal|true
return|;
block|}
if|if
condition|(
name|posA
operator|!=
operator|-
literal|1
operator|&&
name|posA
operator|<
name|maxPosAsterisk
condition|)
block|{
comment|// leading '*'
return|return
literal|true
return|;
block|}
comment|// '*' in the leading part
if|if
condition|(
name|maxFractionAsterisk
operator|>
literal|0.0f
operator|&&
name|pos
operator|<
operator|(
name|float
operator|)
name|token
operator|.
name|length
argument_list|()
operator|*
name|maxFractionAsterisk
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
DECL|method|getMarkerChar
specifier|public
name|char
name|getMarkerChar
parameter_list|()
block|{
return|return
name|markerChar
return|;
block|}
DECL|method|getFloat
specifier|protected
name|float
name|getFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|defValue
parameter_list|)
block|{
name|String
name|val
init|=
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
return|return
name|defValue
return|;
block|}
else|else
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

