begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|regex
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|search
operator|.
name|MultiTermQuery
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
name|BooleanQuery
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
name|BooleanClause
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
name|TermQuery
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
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanTermQuery
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
name|spans
operator|.
name|Spans
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

begin_comment
comment|/**  * A SpanQuery version of {@link RegexQuery} allowing regular expression  * queries to be nested within other SpanQuery subclasses.  */
end_comment

begin_class
DECL|class|SpanRegexQuery
specifier|public
class|class
name|SpanRegexQuery
extends|extends
name|SpanQuery
implements|implements
name|RegexQueryCapable
block|{
DECL|field|regexImpl
specifier|private
name|RegexCapabilities
name|regexImpl
init|=
operator|new
name|JavaUtilRegexCapabilities
argument_list|()
decl_stmt|;
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
DECL|method|SpanRegexQuery
specifier|public
name|SpanRegexQuery
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
name|RegexQuery
name|orig
init|=
operator|new
name|RegexQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|orig
operator|.
name|setRegexImplementation
argument_list|(
name|regexImpl
argument_list|)
expr_stmt|;
name|orig
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|orig
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|BooleanClause
index|[]
name|clauses
init|=
name|bq
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|sqs
init|=
operator|new
name|SpanQuery
index|[
name|clauses
operator|.
name|length
index|]
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|clause
init|=
name|clauses
index|[
name|i
index|]
decl_stmt|;
comment|// Clauses from RegexQuery.rewrite are always TermQuery's
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|sqs
index|[
name|i
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|sqs
index|[
name|i
index|]
operator|.
name|setBoost
argument_list|(
name|tq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SpanOrQuery
name|query
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|sqs
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|orig
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Query should have been rewritten"
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
DECL|method|getTerms
specifier|public
name|Collection
argument_list|<
name|Term
argument_list|>
name|getTerms
parameter_list|()
block|{
name|Collection
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
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
comment|/* generated by IntelliJ IDEA */
annotation|@
name|Override
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|SpanRegexQuery
name|that
init|=
operator|(
name|SpanRegexQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|regexImpl
operator|.
name|equals
argument_list|(
name|that
operator|.
name|regexImpl
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|term
operator|.
name|equals
argument_list|(
name|that
operator|.
name|term
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/* generated by IntelliJ IDEA */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
decl_stmt|;
name|result
operator|=
name|regexImpl
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|29
operator|*
name|result
operator|+
name|term
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
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
name|buffer
operator|.
name|append
argument_list|(
literal|"spanRegexQuery("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
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
DECL|method|setRegexImplementation
specifier|public
name|void
name|setRegexImplementation
parameter_list|(
name|RegexCapabilities
name|impl
parameter_list|)
block|{
name|this
operator|.
name|regexImpl
operator|=
name|impl
expr_stmt|;
block|}
DECL|method|getRegexImplementation
specifier|public
name|RegexCapabilities
name|getRegexImplementation
parameter_list|()
block|{
return|return
name|regexImpl
return|;
block|}
block|}
end_class

end_unit

