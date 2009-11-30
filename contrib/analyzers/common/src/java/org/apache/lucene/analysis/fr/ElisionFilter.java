begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|standard
operator|.
name|StandardTokenizer
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|CharArraySet
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
name|TokenFilter
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
name|tokenattributes
operator|.
name|TermAttribute
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

begin_comment
comment|/**  * Removes elisions from a {@link TokenStream}. For example, "l'avion" (the plane) will be  * tokenized as "avion" (plane).  *<p>  * Note that {@link StandardTokenizer} sees " ' " as a space, and cuts it out.  *   * @see<a href="http://fr.wikipedia.org/wiki/%C3%89lision">Elision in Wikipedia</a>  */
end_comment

begin_class
DECL|class|ElisionFilter
specifier|public
specifier|final
class|class
name|ElisionFilter
extends|extends
name|TokenFilter
block|{
DECL|field|articles
specifier|private
name|CharArraySet
name|articles
init|=
name|CharArraySet
operator|.
name|EMPTY_SET
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|DEFAULT_ARTICLES
specifier|private
specifier|static
specifier|final
name|CharArraySet
name|DEFAULT_ARTICLES
init|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"l"
argument_list|,
literal|"m"
argument_list|,
literal|"t"
argument_list|,
literal|"qu"
argument_list|,
literal|"n"
argument_list|,
literal|"s"
argument_list|,
literal|"j"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|apostrophes
specifier|private
specifier|static
name|char
index|[]
name|apostrophes
init|=
block|{
literal|'\''
block|,
literal|'\u2019'
block|}
decl_stmt|;
comment|/**    * Set the stopword articles    * @param matchVersion the lucene backwards compatibility version    * @param articles a set of articles    * @deprecated use {@link #ElisionFilter(Version, TokenStream, Set)} instead    */
DECL|method|setArticles
specifier|public
name|void
name|setArticles
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|articles
parameter_list|)
block|{
name|this
operator|.
name|articles
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|matchVersion
argument_list|,
name|articles
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the stopword articles    * @param articles a set of articles    * @deprecated use {@link #setArticles(Version, Set)} instead    */
DECL|method|setArticles
specifier|public
name|void
name|setArticles
parameter_list|(
name|Set
argument_list|<
name|?
argument_list|>
name|articles
parameter_list|)
block|{
name|setArticles
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|articles
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an elision filter with standard stop words    */
DECL|method|ElisionFilter
specifier|protected
name|ElisionFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|DEFAULT_ARTICLES
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an elision filter with standard stop words    * @deprecated use {@link #ElisionFilter(Version, TokenStream)} instead    */
DECL|method|ElisionFilter
specifier|protected
name|ElisionFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an elision filter with a Set of stop words    * @deprecated use {@link #ElisionFilter(Version, TokenStream, Set)} instead    */
DECL|method|ElisionFilter
specifier|public
name|ElisionFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|articles
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|,
name|articles
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an elision filter with a Set of stop words    * @param matchVersion the lucene backwards compatibility version    * @param input the source {@link TokenStream}    * @param articles a set of stopword articles    */
DECL|method|ElisionFilter
specifier|public
name|ElisionFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|articles
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|articles
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|articles
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an elision filter with an array of stop words    * @deprecated use {@link #ElisionFilter(Version, TokenStream, Set)} instead    */
DECL|method|ElisionFilter
specifier|public
name|ElisionFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
index|[]
name|articles
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|input
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|articles
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increments the {@link TokenStream} with a {@link TermAttribute} without elisioned start    */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|char
index|[]
name|termBuffer
init|=
name|termAtt
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
name|int
name|termLength
init|=
name|termAtt
operator|.
name|termLength
argument_list|()
decl_stmt|;
name|int
name|minPoz
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|apostrophes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|apos
init|=
name|apostrophes
index|[
name|i
index|]
decl_stmt|;
comment|// The equivalent of String.indexOf(ch)
for|for
control|(
name|int
name|poz
init|=
literal|0
init|;
name|poz
operator|<
name|termLength
condition|;
name|poz
operator|++
control|)
block|{
if|if
condition|(
name|termBuffer
index|[
name|poz
index|]
operator|==
name|apos
condition|)
block|{
name|minPoz
operator|=
name|Math
operator|.
name|min
argument_list|(
name|poz
argument_list|,
name|minPoz
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// An apostrophe has been found. If the prefix is an article strip it off.
if|if
condition|(
name|minPoz
operator|!=
name|Integer
operator|.
name|MAX_VALUE
operator|&&
name|articles
operator|.
name|contains
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|minPoz
argument_list|)
condition|)
block|{
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
name|minPoz
operator|+
literal|1
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
operator|-
operator|(
name|minPoz
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

