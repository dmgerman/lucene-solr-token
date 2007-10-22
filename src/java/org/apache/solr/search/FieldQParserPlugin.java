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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|TextField
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

begin_comment
comment|/**  * Create a field query from the input value, applying text analysis and constructing a phrase query if appropriate.  *<br>Other parameters:<code>f</code>, the field  *<br>Example:<code>&lt;!field f=myfield&gt;Foo Bar</code> creates a phrase query with "foo" followed by "bar"  * if the analyzer for myfield is a text field with an analyzer that splits on whitespace and lowercases terms.  * This is generally equivalent to the lucene query parser expression<code>myfield:"Foo Bar"</code>  */
end_comment

begin_class
DECL|class|FieldQParserPlugin
specifier|public
class|class
name|FieldQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
literal|"field"
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|QParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
block|{
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
name|field
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|F
argument_list|)
decl_stmt|;
name|String
name|queryText
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ft
operator|instanceof
name|TextField
operator|)
condition|)
block|{
name|String
name|internal
init|=
name|ft
operator|.
name|toInternal
argument_list|(
name|queryText
argument_list|)
decl_stmt|;
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|internal
argument_list|)
argument_list|)
return|;
block|}
name|int
name|phraseSlop
init|=
literal|0
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getQueryAnalyzer
argument_list|()
decl_stmt|;
comment|// most of the following code is taken from the Lucene QueryParser
comment|// Use the analyzer to get all the tokens, and then build a TermQuery,
comment|// PhraseQuery, or nothing based on the term count
name|TokenStream
name|source
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|queryText
argument_list|)
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
name|Token
name|t
decl_stmt|;
name|int
name|positionCount
init|=
literal|0
decl_stmt|;
name|boolean
name|severalTokensAtSamePosition
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|t
operator|=
name|source
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|t
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|==
literal|null
condition|)
break|break;
name|lst
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|getPositionIncrement
argument_list|()
operator|!=
literal|0
condition|)
name|positionCount
operator|+=
name|t
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
else|else
name|severalTokensAtSamePosition
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
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
name|lst
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
elseif|else
if|if
condition|(
name|lst
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|t
operator|=
name|lst
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|t
operator|.
name|termText
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|severalTokensAtSamePosition
condition|)
block|{
if|if
condition|(
name|positionCount
operator|==
literal|1
condition|)
block|{
comment|// no phrase query:
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
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
name|lst
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|t
operator|=
operator|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
operator|)
name|lst
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|TermQuery
name|currentQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|t
operator|.
name|termText
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|currentQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
else|else
block|{
comment|// phrase query:
name|MultiPhraseQuery
name|mpq
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|mpq
operator|.
name|setSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
name|ArrayList
name|multiTerms
init|=
operator|new
name|ArrayList
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
name|lst
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|t
operator|=
operator|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
operator|)
name|lst
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
operator|&&
name|multiTerms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
operator|(
name|Term
index|[]
operator|)
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|multiTerms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|multiTerms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|t
operator|.
name|termText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mpq
operator|.
name|add
argument_list|(
operator|(
name|Term
index|[]
operator|)
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|mpq
return|;
block|}
block|}
else|else
block|{
name|PhraseQuery
name|q
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|setSlop
argument_list|(
name|phraseSlop
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
name|lst
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|lst
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|termText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

