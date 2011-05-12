begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|analyzing
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
name|io
operator|.
name|StringReader
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
name|List
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
name|Analyzer
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|queryParser
operator|.
name|ParseException
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
name|search
operator|.
name|Query
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
comment|/**  * Overrides Lucene's default QueryParser so that Fuzzy-, Prefix-, Range-, and WildcardQuerys  * are also passed through the given analyzer, but wild card characters (like<code>*</code>)   * don't get removed from the search terms.  *   *<p><b>Warning:</b> This class should only be used with analyzers that do not use stopwords  * or that add tokens. Also, several stemming analyzers are inappropriate: for example, GermanAnalyzer   * will turn<code>H&auml;user</code> into<code>hau</code>, but<code>H?user</code> will   * become<code>h?user</code> when using this parser and thus no match would be found (i.e.  * using this parser will be no improvement over QueryParser in such cases).   *  * @version $Revision$, $Date$  */
end_comment

begin_class
DECL|class|AnalyzingQueryParser
specifier|public
class|class
name|AnalyzingQueryParser
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|QueryParser
block|{
comment|/**    * Constructs a query parser.    * @param field    the default field for query terms.    * @param analyzer used to find terms in the query text.    */
DECL|method|AnalyzingQueryParser
specifier|public
name|AnalyzingQueryParser
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called when parser    * parses an input term token that contains one or more wildcard    * characters (like<code>*</code>), but is not a prefix term token (one    * that has just a single * character at the end).    *<p>    * Example: will be called for<code>H?user</code> or for<code>H*user</code>     * but not for<code>*user</code>.    *<p>    * Depending on analyzer and settings, a wildcard term may (most probably will)    * be lower-cased automatically. It<b>will</b> go through the default Analyzer.    *<p>    * Overrides super class, by passing terms through analyzer.    *    * @param  field   Name of the field query will use.    * @param  termStr Term token that contains one or more wild card    *                 characters (? or *), but is not simple prefix term    *    * @return Resulting {@link Query} built for the term    * @throws ParseException    */
annotation|@
name|Override
DECL|method|getWildcardQuery
specifier|protected
name|Query
name|getWildcardQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tlist
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/* somewhat a hack: find/store wildcard chars      * in order to put them back after analyzing */
name|boolean
name|isWithinToken
init|=
operator|(
operator|!
name|termStr
operator|.
name|startsWith
argument_list|(
literal|"?"
argument_list|)
operator|&&
operator|!
name|termStr
operator|.
name|startsWith
argument_list|(
literal|"*"
argument_list|)
operator|)
decl_stmt|;
name|StringBuilder
name|tmpBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|chars
init|=
name|termStr
operator|.
name|toCharArray
argument_list|()
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
name|termStr
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|chars
index|[
name|i
index|]
operator|==
literal|'?'
operator|||
name|chars
index|[
name|i
index|]
operator|==
literal|'*'
condition|)
block|{
if|if
condition|(
name|isWithinToken
condition|)
block|{
name|tlist
operator|.
name|add
argument_list|(
name|tmpBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tmpBuffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|isWithinToken
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|isWithinToken
condition|)
block|{
name|wlist
operator|.
name|add
argument_list|(
name|tmpBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tmpBuffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|isWithinToken
operator|=
literal|true
expr_stmt|;
block|}
name|tmpBuffer
operator|.
name|append
argument_list|(
name|chars
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isWithinToken
condition|)
block|{
name|tlist
operator|.
name|add
argument_list|(
name|tmpBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|wlist
operator|.
name|add
argument_list|(
name|tmpBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// get Analyzer from superclass and tokenize the term
name|TokenStream
name|source
init|=
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|termStr
argument_list|)
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|source
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|countTokens
init|=
literal|0
decl_stmt|;
try|try
block|{
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
break|break;
block|}
name|String
name|term
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|term
argument_list|)
condition|)
block|{
try|try
block|{
name|tlist
operator|.
name|set
argument_list|(
name|countTokens
operator|++
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|ioobe
parameter_list|)
block|{
name|countTokens
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|source
operator|.
name|end
argument_list|()
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|countTokens
operator|!=
name|tlist
operator|.
name|size
argument_list|()
condition|)
block|{
comment|/* this means that the analyzer used either added or consumed         * (common for a stemmer) tokens, and we can't build a WildcardQuery */
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Cannot build WildcardQuery with analyzer "
operator|+
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
operator|+
literal|" - tokens added or lost"
argument_list|)
throw|;
block|}
if|if
condition|(
name|tlist
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|tlist
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|wlist
operator|!=
literal|null
operator|&&
name|wlist
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|/* if wlist contains one wildcard, it must be at the end, because:          * 1) wildcards are not allowed in 1st position of a term by QueryParser          * 2) if wildcard was *not* in end, there would be *two* or more tokens */
return|return
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|field
argument_list|,
name|tlist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
name|wlist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|/* we should never get here! if so, this method was called          * with a termStr containing no wildcard ... */
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"getWildcardQuery called without wildcard"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|/* the term was tokenized, let's rebuild to one token        * with wildcards put back in postion */
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|tlist
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|tlist
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|wlist
operator|!=
literal|null
operator|&&
name|wlist
operator|.
name|size
argument_list|()
operator|>
name|i
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|wlist
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|field
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Called when parser parses an input term    * token that uses prefix notation; that is, contains a single '*' wildcard    * character as its last character. Since this is a special case    * of generic wildcard term, and such a query can be optimized easily,    * this usually results in a different query object.    *<p>    * Depending on analyzer and settings, a prefix term may (most probably will)    * be lower-cased automatically. It<b>will</b> go through the default Analyzer.    *<p>    * Overrides super class, by passing terms through analyzer.    *    * @param  field   Name of the field query will use.    * @param  termStr Term token to use for building term for the query    *                 (<b>without</b> trailing '*' character!)    *    * @return Resulting {@link Query} built for the term    * @throws ParseException    */
annotation|@
name|Override
DECL|method|getPrefixQuery
specifier|protected
name|Query
name|getPrefixQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// get Analyzer from superclass and tokenize the term
name|TokenStream
name|source
init|=
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|termStr
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tlist
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|source
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
break|break;
block|}
name|tlist
operator|.
name|add
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|source
operator|.
name|end
argument_list|()
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|tlist
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|super
operator|.
name|getPrefixQuery
argument_list|(
name|field
argument_list|,
name|tlist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|/* this means that the analyzer used either added or consumed        * (common for a stemmer) tokens, and we can't build a PrefixQuery */
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Cannot build PrefixQuery with analyzer "
operator|+
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
operator|+
operator|(
name|tlist
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|?
literal|" - token(s) added"
else|:
literal|" - token consumed"
operator|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Called when parser parses an input term token that has the fuzzy suffix (~) appended.    *<p>    * Depending on analyzer and settings, a fuzzy term may (most probably will)    * be lower-cased automatically. It<b>will</b> go through the default Analyzer.    *<p>    * Overrides super class, by passing terms through analyzer.    *    * @param field Name of the field query will use.    * @param termStr Term token to use for building term for the query    *    * @return Resulting {@link Query} built for the term    * @exception ParseException    */
annotation|@
name|Override
DECL|method|getFuzzyQuery
specifier|protected
name|Query
name|getFuzzyQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|,
name|float
name|minSimilarity
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// get Analyzer from superclass and tokenize the term
name|TokenStream
name|source
init|=
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|termStr
argument_list|)
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|source
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|nextToken
init|=
literal|null
decl_stmt|;
name|boolean
name|multipleTokens
init|=
literal|false
decl_stmt|;
try|try
block|{
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|nextToken
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|multipleTokens
operator|=
name|source
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|nextToken
operator|=
literal|null
expr_stmt|;
block|}
try|try
block|{
name|source
operator|.
name|end
argument_list|()
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|multipleTokens
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Cannot build FuzzyQuery with analyzer "
operator|+
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
operator|+
literal|" - tokens were added"
argument_list|)
throw|;
block|}
return|return
operator|(
name|nextToken
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|super
operator|.
name|getFuzzyQuery
argument_list|(
name|field
argument_list|,
name|nextToken
argument_list|,
name|minSimilarity
argument_list|)
return|;
block|}
comment|/**    * Overrides super class, by passing terms through analyzer.    * @exception ParseException    */
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|protected
name|Query
name|getRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|startInclusive
parameter_list|,
name|boolean
name|endInclusive
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// get Analyzer from superclass and tokenize the terms
name|TokenStream
name|source
init|=
literal|null
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
literal|null
decl_stmt|;
name|boolean
name|multipleTokens
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|part1
operator|!=
literal|null
condition|)
block|{
comment|// part1
try|try
block|{
name|source
operator|=
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|part1
argument_list|)
argument_list|)
expr_stmt|;
name|termAtt
operator|=
name|source
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
name|multipleTokens
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|part1
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|multipleTokens
operator|=
name|source
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|source
operator|.
name|end
argument_list|()
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|multipleTokens
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Cannot build RangeQuery with analyzer "
operator|+
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
operator|+
literal|" - tokens were added to part1"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|part2
operator|!=
literal|null
condition|)
block|{
comment|// part2
name|source
operator|=
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|part2
argument_list|)
argument_list|)
expr_stmt|;
name|termAtt
operator|=
name|source
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|part2
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|multipleTokens
operator|=
name|source
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|source
operator|.
name|end
argument_list|()
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|multipleTokens
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Cannot build RangeQuery with analyzer "
operator|+
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
operator|+
literal|" - tokens were added to part2"
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|getRangeQuery
argument_list|(
name|field
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
argument_list|,
name|endInclusive
argument_list|)
return|;
block|}
block|}
end_class

end_unit

