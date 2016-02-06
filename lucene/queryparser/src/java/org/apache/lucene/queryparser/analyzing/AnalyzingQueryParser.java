begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|analyzing
package|;
end_package

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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|queryparser
operator|.
name|classic
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

begin_comment
comment|/**  * Overrides Lucene's default QueryParser so that Fuzzy-, Prefix-, Range-, and WildcardQuerys  * are also passed through the given analyzer, but wildcard characters<code>*</code> and  *<code>?</code> don't get removed from the search terms.  *   *<p><b>Warning:</b> This class should only be used with analyzers that do not use stopwords  * or that add tokens. Also, several stemming analyzers are inappropriate: for example, GermanAnalyzer   * will turn<code>H&auml;user</code> into<code>hau</code>, but<code>H?user</code> will   * become<code>h?user</code> when using this parser and thus no match would be found (i.e.  * using this parser will be no improvement over QueryParser in such cases).   */
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
block|{
comment|// gobble escaped chars or find a wildcard character
DECL|field|wildcardPattern
specifier|private
specifier|final
name|Pattern
name|wildcardPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\.)|([?*]+)"
argument_list|)
decl_stmt|;
DECL|method|AnalyzingQueryParser
specifier|public
name|AnalyzingQueryParser
parameter_list|(
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|setAnalyzeRangeTerms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called when parser parses an input term that contains one or more wildcard    * characters (like<code>*</code>), but is not a prefix term (one that has    * just a single<code>*</code> character at the end).    *<p>    * Example: will be called for<code>H?user</code> or for<code>H*user</code>.    *<p>    * Depending on analyzer and settings, a wildcard term may (most probably will)    * be lower-cased automatically. It<b>will</b> go through the default Analyzer.    *<p>    * Overrides super class, by passing terms through analyzer.    *    * @param  field   Name of the field query will use.    * @param  termStr Term that contains one or more wildcard    *                 characters (? or *), but is not simple prefix term    *    * @return Resulting {@link Query} built for the term    */
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
if|if
condition|(
name|termStr
operator|==
literal|null
condition|)
block|{
comment|//can't imagine this would ever happen
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Passed null value as term to getWildcardQuery"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|getAllowLeadingWildcard
argument_list|()
operator|&&
operator|(
name|termStr
operator|.
name|startsWith
argument_list|(
literal|"*"
argument_list|)
operator|||
name|termStr
operator|.
name|startsWith
argument_list|(
literal|"?"
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"'*' or '?' not allowed as first character in WildcardQuery"
operator|+
literal|" unless getAllowLeadingWildcard() returns true"
argument_list|)
throw|;
block|}
name|Matcher
name|wildcardMatcher
init|=
name|wildcardPattern
operator|.
name|matcher
argument_list|(
name|termStr
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|last
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|wildcardMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
comment|// continue if escaped char
if|if
condition|(
name|wildcardMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|!=
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|wildcardMatcher
operator|.
name|start
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|chunk
init|=
name|termStr
operator|.
name|substring
argument_list|(
name|last
argument_list|,
name|wildcardMatcher
operator|.
name|start
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|analyzed
init|=
name|analyzeSingleChunk
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|chunk
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|analyzed
argument_list|)
expr_stmt|;
block|}
comment|//append the wildcard character
name|sb
operator|.
name|append
argument_list|(
name|wildcardMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|last
operator|=
name|wildcardMatcher
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|last
operator|<
name|termStr
operator|.
name|length
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|analyzeSingleChunk
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|termStr
operator|.
name|substring
argument_list|(
name|last
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
comment|/**    * Called when parser parses an input term    * that uses prefix notation; that is, contains a single '*' wildcard    * character as its last character. Since this is a special case    * of generic wildcard term, and such a query can be optimized easily,    * this usually results in a different query object.    *<p>    * Depending on analyzer and settings, a prefix term may (most probably will)    * be lower-cased automatically. It<b>will</b> go through the default Analyzer.    *<p>    * Overrides super class, by passing terms through analyzer.    *    * @param  field   Name of the field query will use.    * @param  termStr Term to use for building term for the query    *                 (<b>without</b> trailing '*' character!)    *    * @return Resulting {@link Query} built for the term    */
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
name|String
name|analyzed
init|=
name|analyzeSingleChunk
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|termStr
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|getPrefixQuery
argument_list|(
name|field
argument_list|,
name|analyzed
argument_list|)
return|;
block|}
comment|/**    * Called when parser parses an input term that has the fuzzy suffix (~) appended.    *<p>    * Depending on analyzer and settings, a fuzzy term may (most probably will)    * be lower-cased automatically. It<b>will</b> go through the default Analyzer.    *<p>    * Overrides super class, by passing terms through analyzer.    *    * @param field Name of the field query will use.    * @param termStr Term to use for building term for the query    *    * @return Resulting {@link Query} built for the term    */
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
name|String
name|analyzed
init|=
name|analyzeSingleChunk
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|termStr
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|getFuzzyQuery
argument_list|(
name|field
argument_list|,
name|analyzed
argument_list|,
name|minSimilarity
argument_list|)
return|;
block|}
comment|/**    * Returns the analyzed form for the given chunk    *     * If the analyzer produces more than one output token from the given chunk,    * a ParseException is thrown.    *    * @param field The target field    * @param termStr The full term from which the given chunk is excerpted    * @param chunk The portion of the given termStr to be analyzed    * @return The result of analyzing the given chunk    * @throws ParseException when analysis returns other than one output token    */
DECL|method|analyzeSingleChunk
specifier|protected
name|String
name|analyzeSingleChunk
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|,
name|String
name|chunk
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|analyzed
init|=
literal|null
decl_stmt|;
try|try
init|(
name|TokenStream
name|stream
init|=
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|chunk
argument_list|)
init|)
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// get first and hopefully only output token
if|if
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|analyzed
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// try to increment again, there should only be one output token
name|StringBuilder
name|multipleOutputs
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|multipleOutputs
condition|)
block|{
name|multipleOutputs
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|multipleOutputs
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|multipleOutputs
operator|.
name|append
argument_list|(
name|analyzed
argument_list|)
expr_stmt|;
name|multipleOutputs
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
name|multipleOutputs
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|multipleOutputs
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|multipleOutputs
operator|.
name|append
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|multipleOutputs
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|multipleOutputs
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|getLocale
argument_list|()
argument_list|,
literal|"Analyzer created multiple terms for \"%s\": %s"
argument_list|,
name|chunk
argument_list|,
name|multipleOutputs
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// nothing returned by analyzer.  Was it a stop word and the user accidentally
comment|// used an analyzer with stop words?
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|getLocale
argument_list|()
argument_list|,
literal|"Analyzer returned nothing for \"%s\""
argument_list|,
name|chunk
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|getLocale
argument_list|()
argument_list|,
literal|"IO error while trying to analyze single term: \"%s\""
argument_list|,
name|termStr
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|analyzed
return|;
block|}
block|}
end_class

end_unit

