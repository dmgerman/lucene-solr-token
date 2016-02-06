begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|LeafReader
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
name|LeafReaderContext
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
name|Terms
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
name|search
operator|.
name|suggest
operator|.
name|BitsProducer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionAnalyzer
operator|.
name|HOLE_CHARACTER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionAnalyzer
operator|.
name|SEP_LABEL
import|;
end_import

begin_comment
comment|/**  * Abstract {@link Query} that match documents containing terms with a specified prefix  * filtered by {@link BitsProducer}. This should be used to query against any {@link SuggestField}s  * or {@link ContextSuggestField}s of documents.  *<p>  * Use {@link SuggestIndexSearcher#suggest(CompletionQuery, int)} to execute any query  * that provides a concrete implementation of this query. Example below shows using this query  * to retrieve the top 5 documents.  *  *<pre class="prettyprint">  *  SuggestIndexSearcher searcher = new SuggestIndexSearcher(reader);  *  TopSuggestDocs suggestDocs = searcher.suggest(query, 5);  *</pre>  * This query rewrites to an appropriate {@link CompletionQuery} depending on the  * type ({@link SuggestField} or {@link ContextSuggestField}) of the field the query is run against.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompletionQuery
specifier|public
specifier|abstract
class|class
name|CompletionQuery
extends|extends
name|Query
block|{
comment|/**    * Term to query against    */
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
comment|/**    * {@link BitsProducer} which is used to filter the document scope.    */
DECL|field|filter
specifier|private
specifier|final
name|BitsProducer
name|filter
decl_stmt|;
comment|/**    * Creates a base Completion query against a<code>term</code>    * with a<code>filter</code> to scope the documents    */
DECL|method|CompletionQuery
specifier|protected
name|CompletionQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|BitsProducer
name|filter
parameter_list|)
block|{
name|validate
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**    * Returns a {@link BitsProducer}. Only suggestions matching the returned    * bits will be returned.    */
DECL|method|getFilter
specifier|public
name|BitsProducer
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
comment|/**    * Returns the field name this query should    * be run against    */
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
comment|/**    * Returns the term to be queried against    */
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
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|type
init|=
literal|0
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|Terms
name|terms
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|leafReader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|(
name|terms
operator|=
name|leafReader
operator|.
name|terms
argument_list|(
name|getField
argument_list|()
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
continue|continue;
block|}
if|if
condition|(
name|terms
operator|instanceof
name|CompletionTerms
condition|)
block|{
name|CompletionTerms
name|completionTerms
init|=
operator|(
name|CompletionTerms
operator|)
name|terms
decl_stmt|;
name|byte
name|t
init|=
name|completionTerms
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
condition|)
block|{
name|type
operator|=
name|t
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|!=
name|t
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|getField
argument_list|()
operator|+
literal|" has values of multiple types"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|first
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|this
operator|instanceof
name|ContextQuery
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|SuggestField
operator|.
name|TYPE
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" can not be executed against a non context-enabled SuggestField: "
operator|+
name|getField
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|type
operator|==
name|ContextSuggestField
operator|.
name|TYPE
condition|)
block|{
return|return
operator|new
name|ContextQuery
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
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
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
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
name|buffer
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"filter"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|filter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|validate
specifier|private
name|void
name|validate
parameter_list|(
name|String
name|termText
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termText
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|termText
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
name|HOLE_CHARACTER
case|:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Term text cannot contain HOLE character U+001E; this character is reserved"
argument_list|)
throw|;
case|case
name|SEP_LABEL
case|:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Term text cannot contain unit separator character U+001F; this character is reserved"
argument_list|)
throw|;
default|default:
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

